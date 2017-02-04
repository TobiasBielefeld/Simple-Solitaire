/*
 * Copyright (C) 2016  Tobias Bielefeld
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you want to contact me, send me an e-mail at tobias.bielefeld@gmail.com
 */

package de.tobiasbielefeld.solitaire.games;

import android.widget.RelativeLayout;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.Stack;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Freecell Solitaire Game! 8 tableau, 4 free and 4 foundation stacks
 */

public class Freecell extends Game {

    public Freecell() {
        setNumberOfDecks(1);
        setNumberOfStacks(16);
        setDealFromID(0);
        setLastTableauID(7);
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape) {
        //initialize the dimensions
        setUpCardWidth(layoutGame,isLandscape,9,10);

        //order the stacks on the screen
        int spacing = setUpSpacing(layoutGame,8,9);
        int startPos = layoutGame.getWidth() / 2 - 4 * Card.width - 3 * spacing - spacing/2;
        //free cells and foundation stacks
        for (int i = 0; i < 8; i++) {
            stacks[8 + i].view.setX(startPos + spacing * i + Card.width * i);
            stacks[8 + i].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1 );
        }
        //tableau stacks
        for (int i = 0; i < 8; i++) {
            stacks[i].view.setX(startPos + spacing * i + Card.width * i);
            stacks[i].view.setY(stacks[8].view.getY() + Card.height +
                    (isLandscape ? Card.width / 4 : Card.width / 2));
        }
        //nice background for foundation stacks
        for (int i=12;i<16;i++) {
            stacks[i].view.setBackgroundResource(R.drawable.background_stack_ace);
        }
    }

    public boolean winTest(){
        //won if the foundation stacks are full
        for (int i = 12; i <= 15; i++)
            if (stacks[i].getSize() != 13)
                return false;

        return true;
    }

    public void dealCards(){
        //flip every card up the move them to the tableau
        for (Card card : cards)
            card.flipUp();

        for (int i = 1; i < 8; i++) {
            for (int j = 0; j < 7; j++) {
                if (!(i>=4 && j==6))
                    moveToStack(dealFromStack().getTopCard(), stacks[i], OPTION_NO_RECORD);
            }
        }
    }

    public void onMainStackTouch(){
        //no main stack, so empty
    }

    public boolean cardTest(Stack stack, Card card) {
        if (stack.getID() < 8) {
            //if there are as many cards moving as free stacks, and one of the free stacks was choosen, dont move
            int numberOfFreeCells = 0;
            int movingCards = card.getStack().getSize() - card.getIndexOnStack();

            for (int i=0;i<12;i++){
                if (stacks[i].isEmpty())
                    numberOfFreeCells++;
            }


            if (movingCards > numberOfFreeCells && stack.isEmpty())
                return false;


            return stack.isEmpty() || (stack.getTopCard().getColor() % 2 != card.getColor() % 2)
                    && (stack.getTopCard().getValue() == card.getValue() + 1);
        } else if (stack.getID() < 12) {
            return movingCards.hasSingleCard() && stack.isEmpty();
        }
        else if (movingCards.hasSingleCard()) {
            if (stack.isEmpty())
                return card.getValue() == 1;
            else
                return (stack.getTopCard().getColor() == card.getColor())
                        && (stack.getTopCard().getValue() == card.getValue() - 1);
        }
        else {
            return false;
        }
    }

    public boolean addCardToMovementTest(Card card){
        /*
         *  add cards to movement depending on settings:
         *  normally the player can only move one card at once, but he can also put cards to free
         *  cells and replace them on a new stack. To make this easier, the player can move more
         *  cards at once, if they are in the right order and if there are enough free cells
         *  Use the testCardsUpToTop() method for that test
         */
        int numberOfFreeCells = 0;
        int startPos;

        Stack sourceStack = card.getStack();

        for (int i=0;i<12;i++){
            if (stacks[i].isEmpty())
                numberOfFreeCells++;
        }

        startPos = max(sourceStack.getSize() - numberOfFreeCells-1, card.getStack().getIndexOfCard(card));

        return card.getStack().getIndexOfCard(card) >= startPos && testCardsUpToTop(sourceStack, startPos,ALTERNATING_COLOR);
    }

    public CardAndStack hintTest(){
        /*
         * showing hints also depends on the settings. It can also show multiple cards at once
         */
        for (int i=0;i<12;i++){

            Stack sourceStack = stacks[i];

            if (sourceStack.isEmpty())
                continue;

            int startPos;

            int numberOfFreeCells = 0;

            for (int j=0;j<12;j++){
                if (stacks[j].isEmpty())
                    numberOfFreeCells++;
            }

            startPos = max(sourceStack.getSize() - numberOfFreeCells - 1, 0);

            for (int j=startPos;j<sourceStack.getSize();j++){
                Card cardToMove = sourceStack.getCard(j);

                if (hint.hasVisited(cardToMove)|| !testCardsUpToTop(sourceStack,j,ALTERNATING_COLOR))
                    continue;

                if (cardToMove.getValue()==1 && cardToMove.isTopCard()) {
                    for (int k=12;k<16;k++){
                        if (cardToMove.test(stacks[k]))
                            return new CardAndStack(cardToMove,stacks[k]);
                    }
                }

                if (cardToMove.getValue()==13 && cardToMove.isFirstCard())
                    continue;

                for (int k=0;k<8;k++){
                    Stack destStack = stacks[k];
                    if (i==k || destStack.isEmpty())
                        continue;

                    if (cardToMove.test(destStack)) {

                        if (sameCardOnOtherStack(cardToMove,destStack,SAME_VALUE_AND_COLOR))
                            continue;

                        return new CardAndStack(cardToMove,destStack);
                    }

                }
            }
        }

        return null;
    }

    @Override
    public Stack doubleTapTest(Card card) {
        //first foundation
        if (card.isTopCard()) {
            for (int k = 12; k < 16; k++) {
                if (card.test(stacks[k]))
                    return stacks[k];
            }
        }

        //then non empty tableau fields
        for (int k=0;k<8;k++){
            if (card.test(stacks[k])  && !stacks[k].isEmpty() && !sameCardOnOtherStack(card, stacks[k], SAME_VALUE_AND_COLOR)) {
                return stacks[k];
            }
        }

        //then all empty tableau fields
        for (int k = 0; k < 8; k++) {
            if (card.test(stacks[k]) && stacks[k].isEmpty() && !sameCardOnOtherStack(card, stacks[k], SAME_VALUE_AND_COLOR)) {
                return stacks[k];
            }
        }

        //and empty cells
        if (card.isTopCard()) {
            for (int k = 8; k < 12; k++) {
                if (card.test(stacks[k])  && stacks[k].isEmpty() && !sameCardOnOtherStack(card, stacks[k], SAME_VALUE_AND_COLOR)) {
                    return stacks[k];
                }
            }
        }

        return null;
    }

    public boolean autoCompleteStartTest() {
        //autocomplete can start if stack has cards in the right order
        for (int i=0;i<8;i++){
            if (stacks[i].isEmpty())
                continue;

            if (!testCardsUpToTop(stacks[i],0,ALTERNATING_COLOR))
                return false;
        }

        return true;
    }

    public CardAndStack autoCompletePhaseTwo() {
        for (int i=0; i<12; i++) {
            Stack origin = stacks[i];

            if (origin.isEmpty())
                continue;

            for (int j=12;j<16;j++){
                Stack destination = stacks[j];

                if (origin.getTopCard().test(destination))
                    return new CardAndStack(origin.getTopCard(),destination);
            }
        }

        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs){
        if ((originIDs[0] < 12 && destinationIDs[0] >=12))                                          //to foundations
            return 60;
        if ((destinationIDs[0] < 12 && originIDs[0] >=12))                                          //from foundations
            return -75;
        if (cards.get(0).getValue()==13 && destinationIDs[0] < 12 && stacks[originIDs[0]].getSize()!=1)//king to a empty field
            return 20;
        else
            return 0;
    }
}

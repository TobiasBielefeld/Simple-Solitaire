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

/*
 * Gypsy Solitaire! (Maybe needs another name)
 */

public class Gypsy extends Game {

    public  Gypsy() {
        setNumberOfDecks(2);
        setNumberOfStacks(17);
        setFirstMainStackID(16);
        setLastTableauID(7);
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape) {

        setUpCardWidth(layoutGame,isLandscape,9+1,9+3);

        int spacing = setUpSpacing(layoutGame,9,10);
        int verticalSpacing = (isLandscape ? Card.width / 4 : Card.width / 2) +1;
        int startPos = (int)(layoutGame.getWidth()/2 - 4.5*Card.width - 4*spacing);


        for (int i = 0; i < 8; i++) {
            stacks[8+i].view.setX(startPos + i*(spacing+Card.width) );
            stacks[8+i].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) +1);
            stacks[8+i].view.setImageBitmap(Stack.background1);
        }

        for (int i = 0; i < 8; i++) {
            stacks[i].view.setX(startPos + i*(spacing+Card.width) );
            stacks[i].view.setY(stacks[8].view.getY() + Card.height + verticalSpacing);
        }

        stacks[16].view.setX(stacks[15].view.getX() + spacing + Card.width);
        stacks[16].view.setY(stacks[15].view.getY());
    }


    public boolean winTest(){
        for (int i=0;i<8;i++){
            if (stacks[8+i].getSize()!=13)
                return false;
        }

        return true;
    }

    public void dealCards(){

        for (int i=0;i<8;i++) {
            for (int j=0;j<3;j++) {
                moveToStack(getMainStack().getTopCard(), stacks[i], OPTION_NO_RECORD);
                if (j>0)
                    stacks[i].getTopCard().flipUp();
            }
        }
    }

    public void onMainStackTouch(){

        if (!getMainStack().isEmpty()) {
            ArrayList<Card> cards = new ArrayList<>();
            ArrayList<Stack> destinations = new ArrayList<>();

            for (int i = 0; i < 8; i++) {
                    cards.add(getMainStack().getCardFromTop(i));
                    getMainStack().getCardFromTop(i).flipUp();
                    destinations.add(stacks[i]);
            }

            moveToStack(cards,destinations,OPTION_REVERSED_RECORD);
        }
    }

    public boolean cardTest(Stack stack, Card card){

        if (stack.getID() < 8) {
            return stack.isEmpty() || (stack.getTopCard().getColor() % 2 != card.getColor() % 2) && (stack.getTopCard().getValue() == card.getValue() + 1);
        } else if (stack.getID() < 16 && movingCards.hasSingleCard()) {
            if (stack.isEmpty())
                return card.getValue() == 1;
            else
                return (stack.getTopCard().getColor() == card.getColor())
                        && (stack.getTopCard().getValue() == card.getValue() - 1);
        } else
            return false;
    }

    public boolean addCardToMovementTest(Card card){
        return testCardsUpToTop(card.getStack(),card.getIndexOnStack(),ALTERNATING_COLOR);
    }

    public CardAndStack hintTest(){

        for (int i=0;i<8;i++){
            Stack sourceStack = stacks[i];

            if (sourceStack.isEmpty())
                continue;

            for (int j=sourceStack.getFirstUpCardPos();j<sourceStack.getSize();j++){
                Card cardToMove = sourceStack.getCard(j);

                if (hint.hasVisited(cardToMove) || !testCardsUpToTop(sourceStack,j,ALTERNATING_COLOR))
                    continue;

                if (cardToMove.getValue()!=1) {
                    for (int k = 0; k < 8; k++) {
                        Stack destStack = stacks[k];
                        if (i == k || destStack.isEmpty())
                            continue;

                        if (cardToMove.test(destStack)) {

                            //if the card is already on the same card as on the other stack, don't return it
                            if (sameCardOnOtherStack(cardToMove,destStack,SAME_VALUE_AND_COLOR))
                                continue;

                            return new CardAndStack(cardToMove, destStack);
                        }
                    }
                }

                if (cardToMove.isTopCard()) {
                    for (int k = 0; k < 8; k++) {
                        Stack destStack = stacks[8 + k];

                        if (cardToMove.test(destStack)) {
                            return new CardAndStack(cardToMove, destStack);
                        }
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Stack doubleTapTest(Card card) {
        //tableau without the same card
        for (int k = 0; k < 8; k++) {
            if (card.test(stacks[k]) && !sameCardOnOtherStack(card,stacks[k],SAME_VALUE_AND_COLOR)) {
                return stacks[k];
            }
        }

        //foundation
        if (card.isTopCard()) {
            for (int k = 0; k < 8; k++) {
                if (card.test(stacks[8 + k])) {
                    return stacks[8 + k];
                }
            }
        }

        //then empty tableau fields
        for (int k = 0; k < 8; k++) {
            if (stacks[k].isEmpty() && card.test(stacks[k])) {
                return stacks[k];
            }
        }

        return null;
    }

    public boolean autoCompleteStartTest() {

        if (!getMainStack().isEmpty())
            return false;

        for (int i=0;i<8;i++){
            if (stacks[i].isEmpty() || !stacks[i].getCard(0).isUp() || !testCardsUpToTop(stacks[i],0,ALTERNATING_COLOR))
                return false;
        }

        return true;
    }

    public CardAndStack autoCompletePhaseTwo() {

        for (int i=0;i<8;i++){

            if (stacks[i].isEmpty())
                continue;

            Card cardToTest = stacks[i].getTopCard();

            for (int j=0;j<8;j++){


                if (cardTest(stacks[8+j],cardToTest))
                    return new CardAndStack(cardToTest,stacks[8+j]);
            }
        }

        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs){
        if (originIDs[0]==destinationIDs[0])
            return 50;

        if (originIDs[0]<8 && destinationIDs[0]>=8)
            return 75;

        if (originIDs[0]>=8 && originIDs[0]<getMainStack().getID() && destinationIDs[0]<8)
            return -100;

        return 0;
    }
}

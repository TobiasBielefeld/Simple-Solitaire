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

import android.content.Context;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.Stack;

import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.games.Game.testMode.*;
import static de.tobiasbielefeld.solitaire.games.Game.testMode2.*;
import static de.tobiasbielefeld.solitaire.games.Game.testMode3.*;

/**
 * Freecell Solitaire Game! 8 tableau, 4 free and 4 foundation stacks
 */

public class Freecell extends Game {

    public Freecell() {
        setNumberOfDecks(1);
        setNumberOfStacks(16);  //one extra stack only for dealing cards

        setTableauStackIDs(0,1,2,3,4,5,6,7,8,9,10,11);
        setFoundationStackIDs(12,13,14,15);
        setDealFromID(0);

        setMixingCardsTestMode(testMode.ALTERNATING_COLOR);
        setDirections(1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0);
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape, Context context) {
        //initialize the dimensions
        setUpCardWidth(layoutGame, isLandscape, 9, 10);

        //order the stacks on the screen
        int spacing = setUpHorizontalSpacing(layoutGame, 8, 9);
        int startPos = layoutGame.getWidth() / 2 - 4 * Card.width - 3 * spacing - spacing / 2;
        //free cells and foundation stacks
        for (int i = 0; i < 8; i++) {
            stacks[8 + i].setX(startPos + spacing * i + Card.width * i);
            stacks[8 + i].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1);
        }
        //tableau stacks
        for (int i = 0; i < 8; i++) {
            stacks[i].setX(startPos + spacing * i + Card.width * i);
            stacks[i].setY(stacks[8].getY() + Card.height +
                    (isLandscape ? Card.width / 4 : Card.width / 2));
        }
        //nice background for foundation stacks
        for (int i = 12; i < 16; i++) {
            stacks[i].setImageBitmap(Stack.background1);
        }
    }

    public boolean winTest() {
        //won if the foundation stacks are full
        for (int i = 12; i <= 15; i++) {
            if (stacks[i].getSize() != 13) {
                return false;
            }
        }

        return true;
    }

    public void dealCards() {
        //flip every card up then move them to the tableau
        flipAllCardsUp();

        //the deal stack is stack 0, so don't need to cover that stack in  the loop
        for (int i = 1; i < 8; i++) {
            for (int j = 0; j < 7; j++) {
                if (!(i >= 4 && j == 6)) {
                    moveToStack(getDealStack().getTopCard(), stacks[i], OPTION_NO_RECORD);
                }
            }
        }

        //in case there are already kings at the first position of a stack, grant the bonus for
        //moving kings to empty places. Otherwise max score wouldn't be possible
        for (int i = 0; i < 8; i++) {
            if (stacks[i].getCard(0).getValue() == 13)
                scores.update(20);
        }
    }

    public int onMainStackTouch() {
        //no main stack, so empty
        return 0;
    }

    public boolean cardTest(Stack stack, Card card) {
        if (stack.getId() < 8) {
            //if there are as many cards moving as free stacks, and one of the free stacks was choosen, dont move
            int movingCards = card.getStack().getSize() - card.getIndexOnStack();

            return movingCards <= getPowerMoveCount(stack.isEmpty()) && canCardBePlaced(stack, card, ALTERNATING_COLOR, DESCENDING);

        } else if (stack.getId() < 12) {
            return movingCards.hasSingleCard() && stack.isEmpty();
        } else if (movingCards.hasSingleCard() && stack.getId() < 16) {
            if (stack.isEmpty()) {
                return card.getValue() == 1;
            } else {
                return canCardBePlaced(stack, card, SAME_FAMILY, ASCENDING);
            }
        }

        return false;
    }

    public boolean addCardToMovementGameTest(Card card) {
        /*
         *  normally the player can only move one card at once, but he can also put cards to free
         *  cells and replace them on a new stack. To make this easier, the player can move more
         *  cards at once, if they are in the right order and if there are enough free cells
         */
        Stack sourceStack = card.getStack();

        int startPos = max(sourceStack.getSize() - getPowerMoveCount(false), card.getStack().getIndexOfCard(card));

        return card.getStack().getIndexOfCard(card) >= startPos && testCardsUpToTop(sourceStack, startPos, ALTERNATING_COLOR);
    }

    public CardAndStack hintTest(ArrayList<Card> visited) {
        for (int i = 0; i < 12; i++) {

            Stack sourceStack = stacks[i];

            if (sourceStack.isEmpty()) {
                continue;
            }

            int startPos;

            startPos = max(sourceStack.getSize() - getPowerMoveCount(false), 0);

            for (int j = startPos; j < sourceStack.getSize(); j++) {
                Card cardToMove = sourceStack.getCard(j);

                if (visited.contains(cardToMove) || !testCardsUpToTop(sourceStack, j, ALTERNATING_COLOR)) {
                    continue;
                }

                if (cardToMove.getValue() == 1 && cardToMove.isTopCard()) {
                    for (int k = 12; k < 16; k++) {
                        if (cardToMove.test(stacks[k])) {
                            return new CardAndStack(cardToMove, stacks[k]);
                        }
                    }
                }

                if (cardToMove.getValue() == 13 && cardToMove.isFirstCard()) {
                    continue;
                }

                for (int k = 0; k < 8; k++) {
                    Stack destStack = stacks[k];

                    if (i == k || destStack.isEmpty()) {
                        continue;
                    }

                    if (cardToMove.test(destStack)) {
                        if (sameCardOnOtherStack(cardToMove, destStack, SAME_VALUE_AND_COLOR)) {
                            continue;
                        }

                        return new CardAndStack(cardToMove, destStack);
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
                if (card.test(stacks[k])) {
                    return stacks[k];
                }
            }
        }

        //then non empty tableau fields
        for (int k = 0; k < 8; k++) {
            if (card.test(stacks[k]) && !stacks[k].isEmpty() && !sameCardOnOtherStack(card, stacks[k], SAME_VALUE_AND_COLOR)) {
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
                if (card.test(stacks[k]) && stacks[k].isEmpty() && !sameCardOnOtherStack(card, stacks[k], SAME_VALUE_AND_COLOR)) {
                    return stacks[k];
                }
            }
        }

        return null;
    }

    public boolean autoCompleteStartTest() {
        //autocomplete can start if stack has cards in the right order
        for (int i = 0; i < 8; i++) {
            if (!testCardsUpToTop(stacks[i], 0, DOESNT_MATTER)) {
                return false;
            }
        }

        return true;
    }

    public CardAndStack autoCompletePhaseTwo() {
        for (int i = 0; i < 12; i++) {
            Stack origin = stacks[i];

            if (origin.isEmpty()) {
                continue;
            }

            for (int j = 12; j < 16; j++) {
                Stack destination = stacks[j];

                if (origin.getTopCard().test(destination)) {
                    return new CardAndStack(origin.getTopCard(), destination);
                }
            }
        }

        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs, boolean isUndoMovement) {
        //to foundations
        if ((originIDs[0] < 12 && destinationIDs[0] >= 12)) {
            return 60;
        }
        //from foundations
        if ((destinationIDs[0] < 12 && originIDs[0] >= 12)) {
            return -75;
        }
        //king to a empty field
        if (cards.get(0).getValue() == 13 && destinationIDs[0] < 12 && cards.get(0).getIndexOnStack()!=0) {
            return 20;
        }

        return 0;
    }

    private int getPowerMoveCount(boolean movingToEmptyStack){
        return getPowerMoveCount(new int[]{8,9,10,11}, new int[]{0,1,2,3,4,5,6,7}, movingToEmptyStack);
    }
}

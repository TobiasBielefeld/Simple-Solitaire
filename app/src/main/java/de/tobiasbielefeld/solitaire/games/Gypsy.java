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
 * Gypsy Solitaire! (Maybe needs another name)
 */

public class Gypsy extends Game {

    public Gypsy() {
        setNumberOfDecks(2);
        setNumberOfStacks(17);

        setTableauStackIDs(0,1,2,3,4,5,6,7);
        setFoundationStackIDs(8,9,10,11,12,13,14,15);
        setMainStackIDs(16);

        setMixingCardsTestMode(testMode.ALTERNATING_COLOR);
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape, Context context) {

        setUpCardWidth(layoutGame, isLandscape, 9 + 1, 9 + 3);
        int spacing = setUpHorizontalSpacing(layoutGame, 9, 10);
        int verticalSpacing = (isLandscape ? Card.width / 4 : Card.width / 2) + 1;
        int startPos = (int) (layoutGame.getWidth() / 2 - 4.5 * Card.width - 4 * spacing);


        for (int i = 0; i < 8; i++) {
            stacks[8 + i].setX(startPos + i * (spacing + Card.width));
            stacks[8 + i].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1);
            stacks[8 + i].setImageBitmap(Stack.background1);
        }

        for (int i = 0; i < 8; i++) {
            stacks[i].setX(startPos + i * (spacing + Card.width));
            stacks[i].setY(stacks[8].getY() + Card.height + verticalSpacing);
        }

        stacks[16].setX(stacks[15].getX() + spacing + Card.width);
        stacks[16].setY(stacks[15].getY());
    }


    public boolean winTest() {
        for (int i = 0; i < 8; i++) {
            if (stacks[8 + i].getSize() != 13)
                return false;
        }

        return true;
    }

    public void dealCards() {

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 3; j++) {
                moveToStack(getMainStack().getTopCard(), stacks[i], OPTION_NO_RECORD);

                if (j>0){
                    stacks[i].getCard(j).flipUp();
                }
            }
        }
    }

    public int onMainStackTouch() {

        if (!getMainStack().isEmpty()) {
            ArrayList<Card> cards = new ArrayList<>();
            ArrayList<Stack> destinations = new ArrayList<>();

            for (int i = 0; i < 8; i++) {
                cards.add(getMainStack().getCardFromTop(i));
                getMainStack().getCardFromTop(i).flipUp();
                destinations.add(stacks[i]);
            }

            moveToStack(cards, destinations, OPTION_REVERSED_RECORD);
            return 1;
        }

        return 0;
    }

    public boolean cardTest(Stack stack, Card card) {
        if (stack.getId() < 8) {
            return canCardBePlaced(stack, card, ALTERNATING_COLOR, DESCENDING);
        } else if (stack.getId() < 16 && movingCards.hasSingleCard()) {
            if (stack.isEmpty()) {
                return card.getValue() == 1;
            } else {
                return canCardBePlaced(stack, card, SAME_FAMILY, ASCENDING);
            }
        } else {
            return false;
        }
    }

    public boolean addCardToMovementGameTest(Card card) {
        return testCardsUpToTop(card.getStack(), card.getIndexOnStack(), ALTERNATING_COLOR);
    }

    public CardAndStack hintTest(ArrayList<Card> visited) {

        for (int i = 0; i < 8; i++) {
            Stack sourceStack = stacks[i];

            if (sourceStack.isEmpty())
                continue;

            for (int j = sourceStack.getFirstUpCardPos(); j < sourceStack.getSize(); j++) {
                Card cardToMove = sourceStack.getCard(j);

                if (visited.contains(cardToMove) || !testCardsUpToTop(sourceStack, j, ALTERNATING_COLOR))
                    continue;

                if (cardToMove.getValue() != 1) {
                    for (int k = 0; k < 8; k++) {
                        Stack destStack = stacks[k];
                        if (i == k || destStack.isEmpty())
                            continue;

                        if (cardToMove.test(destStack)) {

                            //if the card is already on the same card as on the other stack, don't return it
                            if (sameCardOnOtherStack(cardToMove, destStack, SAME_VALUE_AND_COLOR))
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

        return findBestSequenceToMoveToEmptyStack(ALTERNATING_COLOR);
    }

    @Override
    public Stack doubleTapTest(Card card) {

        //foundation
        if (card.isTopCard()) {
            for (int k = 0; k < 8; k++) {
                if (card.test(stacks[8 + k])) {
                    return stacks[8 + k];
                }
            }
        }

        //non empty tableau without the same card
        for (int k = 0; k < 8; k++) {
            if (card.test(stacks[k]) && !sameCardOnOtherStack(card, stacks[k], SAME_VALUE_AND_COLOR) && !stacks[k].isEmpty()) {
                return stacks[k];
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

        for (int i = 0; i < 8; i++) {
            if (!testCardsUpToTop(stacks[i], 0, DOESNT_MATTER)) {
                return false;
            }
        }

        return true;
    }

    public CardAndStack autoCompletePhaseTwo() {

        for (int i = 0; i < 8; i++) {

            if (stacks[i].isEmpty())
                continue;

            Card cardToTest = stacks[i].getTopCard();

            for (int j = 0; j < 8; j++) {


                if (cardTest(stacks[8 + j], cardToTest))
                    return new CardAndStack(cardToTest, stacks[8 + j]);
            }
        }

        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs, boolean isUndoMovement) {
        if (originIDs[0] == destinationIDs[0])
            return 50;

        if (originIDs[0] < 8 && destinationIDs[0] >= 8)
            return 75;

        if (originIDs[0] >= 8 && originIDs[0] < getMainStack().getId() && destinationIDs[0] < 8)
            return -100;

        return 0;
    }
}

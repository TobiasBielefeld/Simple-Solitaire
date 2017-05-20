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

import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.Stack;

import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.games.Game.testMode.*;
import static de.tobiasbielefeld.solitaire.games.Game.testMode2.*;

/**
 * Simple Simon Game! It's nearly like Spider, but with less cards and all cards are
 * already faced up at start
 */

public class SimpleSimon extends Game {

    public SimpleSimon() {
        setNumberOfDecks(1);
        setNumberOfStacks(14);
        setDealFromID(0);
        setLastTableauID(9);
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape) {
        //initialize the dimensions
        setUpCardWidth(layoutGame, isLandscape, 11, 12);
        int spacing = setUpHorizontalSpacing(layoutGame, 10, 11);
        int startPos = layoutGame.getWidth() / 2 - 2 * Card.width - (int) (1.5 * spacing);
        //foundation stacks
        for (int i = 0; i < 4; i++) {
            stacks[10 + i].setX(startPos + spacing * i + Card.width * i);
            stacks[10 + i].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1);
        }
        //tableau stacks
        startPos = layoutGame.getWidth() / 2 - 5 * Card.width - 4 * spacing - spacing / 2;
        for (int i = 0; i < 10; i++) {
            stacks[i].setX(startPos + spacing * i + Card.width * i);
            stacks[i].setY(stacks[10].getY() + Card.height + (isLandscape ? Card.width / 4 : Card.width / 2) + 1);
        }
    }

    public boolean winTest() {
        return (stacks[10].getSize() == 13 && stacks[11].getSize() == 13 && stacks[12].getSize() == 13 && stacks[13].getSize() == 13);
    }

    public void dealCards() {

        flipAllCardsUp();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                moveToStack(getDealStack().getTopCard(), stacks[7 + i], OPTION_NO_RECORD);
            }
        }

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 1 + i; j++) {
                moveToStack(getDealStack().getTopCard(), stacks[i], OPTION_NO_RECORD);
            }
        }

        for (Card card : cards)
            card.flipUp();
    }

    public boolean cardTest(Stack stack, Card card) {
        if (stack.getId() < 10) {
            if (stack.isEmpty() || stack.getTopCard().getValue() == card.getValue() + 1)
                return true;
        }

        return false;
    }

    public boolean addCardToMovementTest(Card card) {
        return card.getStackId() < 10 && testCardsUpToTop(card.getStack(), card.getIndexOnStack(), SAME_COLOR);
    }

    public CardAndStack hintTest() {
        for (int i = 0; i < 10; i++) {
            Stack sourceStack = stacks[i];

            if (sourceStack.isEmpty())
                continue;

            for (int j = sourceStack.getFirstUpCardPos(); j < sourceStack.getSize(); j++) {
                Card cardToMove = sourceStack.getCard(j);

                if (hint.hasVisited(cardToMove) || !testCardsUpToTop(sourceStack, j, SAME_COLOR))
                    continue;

                for (int k = 0; k < 10; k++) {
                    Stack destStack = stacks[k];
                    if (i == k || destStack.isEmpty())
                        continue;

                    if (cardToMove.test(destStack)) {
                        //if the card above has the corret value, and the card on destination is not the same family as the cardToMove, don't move it
                        if (j > 0 && sourceStack.getCard(j - 1).isUp() && sourceStack.getCard(j - 1).getValue() == cardToMove.getValue() + 1
                                && destStack.getTopCard().getColor() != cardToMove.getColor())
                            continue;
                        //if the card is already on the same card as on the other stack, don't return it
                        if (sameCardOnOtherStack(cardToMove, destStack, SAME_VALUE_AND_FAMILY))
                            continue;

                        return new CardAndStack(cardToMove, destStack);
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Stack doubleTapTest(Card card) {
        Card cardBelow = null;

        if (card.getIndexOnStack() > 0)
            cardBelow = card.getStack().getCard(card.getIndexOnStack() - 1);

        //tableau stacks
        for (int k = 0; k < 10; k++) {
            Stack destStack = stacks[k];
            if (card.getStackId() == k || destStack.isEmpty())
                continue;

            if (cardBelow != null && cardBelow.isUp() && cardBelow.getValue() == card.getValue() + 1 && destStack.getTopCard().getColor() != card.getColor())
                continue;

            if (card.test(destStack) && !sameCardOnOtherStack(card, destStack, SAME_VALUE_AND_FAMILY)) {
                return destStack;
            }
        }

        //empty stacks
        for (int k = 0; k < 10; k++) {
            if (stacks[k].isEmpty() && card.test(stacks[k])) {
                return stacks[k];
            }
        }

        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs, boolean isUndoMovement) {
        if (destinationIDs[0] >= 10 && destinationIDs[0] < 14)
            return 200;
        else
            return 0;
    }

    public void onMainStackTouch() {
        //no main stack so empty
    }

    @Override
    public void testAfterMove() {
        /*
         * after a move, test if somewhere is a complete card family, if so, move it to foundations
         */

        for (int i = 0; i < 10; i++) {
            Stack currentStack = stacks[i];

            if (currentStack.isEmpty() || currentStack.getTopCard().getValue() != 1)
                continue;

            for (int j = currentStack.getFirstUpCardPos(); j < currentStack.getSize(); j++) {
                if (j == -1)
                    break;

                Card cardToTest = currentStack.getCard(j);

                if (cardToTest.getValue() == 13 && testCardsUpToTop(currentStack, j, SAME_COLOR)) {
                    Stack foundationStack = stacks[10];

                    while (!foundationStack.isEmpty())
                        foundationStack = stacks[foundationStack.getId() + 1];

                    ArrayList<Card> cards = new ArrayList<>();
                    ArrayList<Stack> origins = new ArrayList<>();

                    for (int k = j; k < currentStack.getSize(); k++) {
                        cards.add(currentStack.getCard(k));
                        origins.add(currentStack);
                    }

                    recordList.addAtEndOfLastEntry(cards, origins);
                    moveToStack(cards, foundationStack, OPTION_NO_RECORD);
                    scores.update(200);

                    testIfWonHandler.sendEmptyMessageDelayed(0, 200);
                    break;
                }
            }
        }
    }

    public boolean autoCompleteStartTest() {
        for (int i = 0; i < 10; i++)
            if (stacks[i].getSize() > 0 && (stacks[i].getFirstUpCardPos() != 0 || !testCardsUpToTop(stacks[i], 0, SAME_COLOR)))
                return false;

        return true;
    }

    public CardAndStack autoCompletePhaseOne() {

        for (int i = 0; i < 10; i++) {
            Stack sourceStack = stacks[i];

            if (sourceStack.isEmpty())
                continue;

            Card cardToMove = sourceStack.getCard(0);

            for (int k = 0; k < 10; k++) {
                Stack destStack = stacks[k];
                if (i == k || destStack.isEmpty() || destStack.getTopCard().getColor() != cardToMove.getColor())
                    continue;

                if (cardToMove.test(destStack)) {
                    return new CardAndStack(cardToMove, destStack);
                }
            }
        }

        return null;
    }
}

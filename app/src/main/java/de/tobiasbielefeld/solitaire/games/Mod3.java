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

/**
 * Mod3! Only for this game, i had to implement the ability to change the max height/width of a stack
 */

public class Mod3 extends Game {

    public Mod3() {
        setNumberOfDecks(2);
        setNumberOfStacks(34);
        setFirstMainStackID(33);
        setLastTableauID(31);
        setFirstDiscardStackID(32);
        setDirections(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0);
        setDirectionBorders(8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, -1, -1, -1, -1, -1, -1, -1, -1, 33, -1);
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape) {

        setUpCardDimensions(layoutGame, 10, 7);

        int spacing = setUpSpacing(layoutGame, 9, 10);
        int spacingVertical = min(Card.width, (layoutGame.getHeight() - 4 * Card.height) / (4 + 1));

        int startPos = (int) (layoutGame.getWidth() / 2 - 4.5 * Card.width - 4 * spacing);
        int startPosVertical = (isLandscape ? Card.width / 4 : Card.width / 2) + 1;

        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 8; i++) {
                stacks[(j * 8) + i].view.setX(startPos + i * (spacing + Card.width));
                stacks[(j * 8) + i].view.setY(startPosVertical + j * (spacingVertical + Card.height));
            }
        }

        stacks[32].view.setX(stacks[15].view.getX() + Card.width + spacing);
        stacks[32].view.setY(stacks[15].view.getY() - Card.height / 2);

        stacks[33].view.setX(stacks[23].view.getX() + Card.width + spacing);
        stacks[33].view.setY(stacks[23].view.getY() + Card.height / 2);
    }

    public boolean winTest() {
        for (int i = 0; i < 8; i++) {
            if (!stacks[24 + i].isEmpty())
                return false;
        }

        return getMainStack().isEmpty();
    }

    public void dealCards() {

        for (int i = 0; i < 32; i++) {
            moveToStack(getDealStack().getTopCard(), stacks[i], OPTION_NO_RECORD);
            stacks[i].getTopCard().flipUp();
        }
    }

    public void onMainStackTouch() {

        if (!getMainStack().isEmpty()) {
            ArrayList<Card> cards = new ArrayList<>();
            ArrayList<Stack> destinations = new ArrayList<>();

            for (int i = 0; i < 8; i++) {
                cards.add(getMainStack().getCardFromTop(i));
                getMainStack().getCardFromTop(i).flipUp();
                destinations.add(stacks[24 + i]);
            }

            moveToStack(cards, destinations, OPTION_REVERSED_RECORD);
        }
    }

    public boolean cardTest(Stack stack, Card card) {
        if (card.getValue() == 1 && stack == getDiscardStack())
            return true;

        if (stack.isEmpty()) {
            if (stack.getID() < 8)
                return card.getValue() == 2;
            else if (stack.getID() < 16)
                return card.getValue() == 3;
            else if (stack.getID() < 24)
                return card.getValue() == 4;
            else return stack.getID() < 32;
        } else {
            return stack.getID() < 24 && validOrder(stack) && card.getValue() == stack.getTopCard().getValue() + 3 && card.getColor() == stack.getTopCard().getColor();
        }
    }

    private boolean validOrder(Stack stack) {
        if (stack.getID() < 8)
            return stack.getCard(0).getValue() == 2;
        else if (stack.getID() < 16)
            return stack.getCard(0).getValue() == 3;
        else
            return stack.getCard(0).getValue() == 4;
    }

    public boolean addCardToMovementTest(Card card) {
        return card.isTopCard() && card.getStack() != getDiscardStack();
    }

    public CardAndStack hintTest() {
        for (int i = 0; i <= getLastTableauID(); i++) {
            if (stacks[i].isEmpty() || (i < 24 && stacks[i].getSize() > 1) || hint.hasVisited(stacks[i].getTopCard()))
                continue;

            Card cardToTest = stacks[i].getTopCard();

            if (cardToTest.getValue() == 1)
                return new CardAndStack(cardToTest, getDiscardStack());

            for (int j = 0; j <= getLastTableauID(); j++) {
                if (i == j)
                    continue;

                if (cardToTest.test(stacks[j])) {
                    if (i >= 24 && j >= 24 && i < 32 && j < 32)
                        continue;

                    if (stacks[j].getSize() == 0 && stacks[i].getSize() == 1 && ((j >= 24) || (i < 8 && j < 8) || (i >= 8 && j >= 8 && i < 16 && j < 16) || (i >= 16 && j >= 16 && i < 24 && j < 24)))
                        continue;

                    return new CardAndStack(cardToTest, stacks[j]);
                }
            }
        }

        return null;
    }

    @Override
    public Stack doubleTapTest(Card card) {

        int stackID = card.getStack().getID();

        if (card.getValue() == 1)
            return getDiscardStack();

        for (int j = 0; j <= getLastTableauID(); j++) {

            if (card.test(stacks[j])) {
                if (stackID >= 24 && j >= 24 && stackID < 32 && j < 32)
                    continue;

                if (stacks[j].getSize() == 0 && stacks[stackID].getSize() == 1 && ((j >= 24) || (stackID < 8 && j < 8) || (stackID >= 8 && j >= 8 && stackID < 16 && j < 16) || (stackID >= 16 && j >= 16 && stackID < 24 && j < 24)))
                    continue;

                return stacks[j];
            }
        }

        for (int j = 0; j <= getLastTableauID(); j++) {
            if (card.test(stacks[j])) {
                return stacks[j];
            }
        }

        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs) {
        int i = originIDs[0];
        int j = destinationIDs[0];

        if ((i < 8 && j < 8) || (i >= 8 && j >= 8 && i < 16 && j < 16) || (i >= 16 && j >= 16 && i < 24 && j < 24))
            return 0;

        if (destinationIDs[0] < 24)
            return 50;

        if (originIDs[0] < 24 && destinationIDs[0] >= 24 && destinationIDs[0] < 32)
            return -75;

        return 0;
    }
}

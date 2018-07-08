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

/**
 * Aces Up! Just 6 stacks and pretty easy rules
 */

public class AcesUp extends Game {

    public AcesUp() {
        setNumberOfDecks(1);
        setNumberOfStacks(6);

        setTableauStackIDs(0,1,2,3);
        setFoundationStackIDs(4);
        setMainStackIDs(5);

        setMixingCardsTestMode(null);
        setDirections(1, 1, 1, 1, 0, 0);
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape, Context context) {

        setUpCardWidth(layoutGame, isLandscape, 7 + 1, 7 + 2);

        int spacing = setUpHorizontalSpacing(layoutGame, 7, 8);

        int startPos = (int) (layoutGame.getWidth() / 2 - 3.5 * Card.width - 2.5 * spacing);

        stacks[4].setX(startPos);
        stacks[4].view.setY((isLandscape ? Card.height / 4 : Card.height / 2) + 1);

        for (int i = 0; i < 4; i++) {
            stacks[i].setX(stacks[4].getX() + spacing + Card.width * 3 / 2 + i * (spacing + Card.width));
            stacks[i].setY(stacks[4].getY());
        }

        stacks[5].setX(stacks[3].getX() + Card.width + Card.width / 2 + spacing);
        stacks[5].setY(stacks[4].getY());
    }


    public boolean winTest() {
        if (!getMainStack().isEmpty()) {
            return false;
        }

        for (int i = 0; i < 4; i++) {
            if (stacks[i].getSize() != 1 || stacks[i].getTopCard().getValue() != 1) {
                return false;
            }
        }

        return true;
    }

    public void dealCards() {

        for (int i = 0; i < 4; i++) {
                moveToStack(getMainStack().getTopCard(), stacks[i], OPTION_NO_RECORD);
                stacks[i].getCard(0).flipUp();
        }
    }

    public int onMainStackTouch() {
        if (getMainStack().isEmpty()) {
            return 0;
        }

        ArrayList<Card> cards = new ArrayList<>();
        ArrayList<Stack> destinations = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            getMainStack().getCardFromTop(i).flipUp();
            cards.add(getMainStack().getCardFromTop(i));
            destinations.add(stacks[i]);
        }

        moveToStack(cards, destinations, OPTION_REVERSED_RECORD);

        return 1;
    }

    public boolean cardTest(Stack stack, Card card) {
        if (stack.getId() < 4 && stack.isEmpty()) {
            return true;
        } else if (stack.getId() == getMainStack().getId() || card.getValue() == 1) {
            return false;
        } else if (stack.getId() == 4) {
            for (int i = 0; i < 4; i++) {
                if (stacks[i].isEmpty() || i == card.getStack().getId()) {
                    continue;
                }

                Card cardOnStack = stacks[i].getTopCard();

                if (cardOnStack.getColor() == card.getColor() && (cardOnStack.getValue() > card.getValue() || cardOnStack.getValue() == 1)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean addCardToMovementGameTest(Card card) {
        return card.isTopCard() && card.getStack() != stacks[4];
    }

    public CardAndStack hintTest(ArrayList<Card> visited) {

        for (int j = 0; j < 4; j++) {
            if (stacks[j].isEmpty() || visited.contains(stacks[j].getTopCard())
                    || (stacks[j].getSize() == 1 && stacks[j].getTopCard().getValue() == 1)) {
                continue;
            }

            Card cardToTest = stacks[j].getTopCard();
            boolean success = false;

            if (cardToTest.getValue() == 1) {
                for (int i = 0; i < 4; i++) {
                    if (i == j || !stacks[i].isEmpty()) {
                        continue;
                    }

                    return new CardAndStack(cardToTest, stacks[i]);
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    if (stacks[i].isEmpty() || i == j) {
                        continue;
                    }

                    Card cardOnStack = stacks[i].getTopCard();

                    if (cardOnStack.getColor() == cardToTest.getColor()
                            && (cardOnStack.getValue() > cardToTest.getValue() || cardOnStack.getValue() == 1)) {
                        success = true;
                    }
                }

                if (success) {
                    return new CardAndStack(cardToTest, stacks[4]);
                }
            }
        }


        return null;
    }

    public Stack doubleTapTest(Card card) {

        boolean success = false;

        if (card.getValue() != 1) {                                                                   //do not move aces to discard stack
            for (int i = 0; i < 4; i++) {
                if (stacks[i].isEmpty() || i == card.getStack().getId()) {
                    continue;
                }

                Card cardOnStack = stacks[i].getTopCard();

                if (cardOnStack.getColor() == card.getColor()
                        && (cardOnStack.getValue() > card.getValue() || cardOnStack.getValue() == 1)) {
                    success = true;
                }
            }

            if (success) {
                return stacks[4];
            }
        }

        if (!card.isFirstCard()) {
            for (int i = 0; i < 4; i++) {
                if (i == card.getStackId() || !stacks[i].isEmpty()) {
                    continue;
                }

                return stacks[i];
            }
        }

        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs, boolean isUndoMovement) {
        if (destinationIDs[0] == 4) {
            return 50;
        }

        return 0;
    }
}

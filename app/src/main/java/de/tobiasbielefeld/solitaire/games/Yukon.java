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
 * Yukon Game! 7 tableau stacks, 4 foundation stacks and no main stack
 */

public class Yukon extends Game {

    public Yukon() {
        setNumberOfDecks(1);
        setNumberOfStacks(11);
        setDealFromID(0);
        setLastTableauID(6);

    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape) {
        //initialize the dimensions
        setUpCardDimensions(layoutGame, 9, 5);

        //order the stacks on the screen
        int spacingHorizontal = setUpSpacing(layoutGame, 8, 9);
        int spacingVertical = min((layoutGame.getHeight() - 4 * Card.height) / 5, Card.width / 4);
        int startPos = (int) (layoutGame.getWidth() / 2 - 4 * Card.width - 3.5 * spacingHorizontal);
        //tableau stacks
        for (int i = 0; i <= 7; i++) {
            stacks[i].view.setX(startPos + spacingHorizontal * i + Card.width * i);
            stacks[i].view.setY(spacingVertical);
        }
        //foundation stacks
        for (int i = 8; i <= 10; i++) {
            stacks[i].view.setX(stacks[7].view.getX());
            stacks[i].view.setY(stacks[i - 1].view.getY() + Card.height + spacingVertical);
        }
        //nice background for foundation stacks
        for (int i = 7; i <= 10; i++) {
            stacks[i].view.setImageBitmap(Stack.background1);
        }
    }

    public boolean winTest() {
        //won if foundation stacks are full
        for (int i = 7; i < 11; i++)
            if (stacks[i].getSize() != 13)
                return false;

        return true;
    }

    public void dealCards() {
        /*
         * because there is no main stack, use the stack from getDealStack()
         */

        putSharedString(PREF_KEY_YUKON_RULES_OLD, getSharedString(PREF_KEY_YUKON_RULES, DEFAULT_YUKON_RULES));

        for (int i = 1; i <= 6; i++) {
            for (int j = 0; j < 5 + i; j++) {
                moveToStack(getDealStack().getTopCard(), stacks[i], OPTION_NO_RECORD);

                if (j >= i)
                    stacks[i].getTopCard().flipUp();
            }
        }

        getDealStack().getTopCard().flipUp();
    }

    public void onMainStackTouch() {
        //no main stack, so empty
    }

    public boolean cardTest(Stack stack, Card card) {

        if (stack.getID() < 7) {                                                                    //tableau
            if (stack.isEmpty())
                return card.getValue() == 13;
            else
                return checkRules(stack, card) && (stack.getTopCard().getValue() == card.getValue() + 1);
        } else if (movingCards.hasSingleCard()) {                                                     //foundation
            if (stack.isEmpty())
                return card.getValue() == 1;
            else
                return (stack.getTopCard().getColor() == card.getColor())
                        && (stack.getTopCard().getValue() == card.getValue() - 1);
        } else {
            return false;
        }
    }

    boolean checkRules(Stack stack, Card card) {
        boolean defaultRules = sharedStringEquals(PREF_KEY_YUKON_RULES_OLD, DEFAULT_YUKON_RULES);

        return (defaultRules && (stack.getTopCard().getColor() % 2 != card.getColor() % 2)) ||
                (!defaultRules && (stack.getTopCard().getColor() == card.getColor()));
    }

    public boolean addCardToMovementTest(Card card) {
        //yukon is simple in this way: you can move every card
        return true;
    }

    public CardAndStack hintTest() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 11; j++) {
                if (stacks[i].isEmpty() || i == j)
                    continue;

                for (int k = 0; k < stacks[i].getSize(); k++) {

                    Card cardToMove = stacks[i].getCard(k);

                    if (j >= 7 && !cardToMove.isTopCard())
                        continue;

                    if (cardToMove.isUp() && !hint.hasVisited(cardToMove) && cardToMove.test(stacks[j])) {
                        //don't move if it's an ace and not a top card and also not if the stack id is below 7
                        //so only move single aces to the foundation stacks
                        if (cardToMove.getValue() == 1 && j < 7)
                            continue;
                        //move kings not when they are the first card on a stack
                        //so they won't be moved around on empty fields
                        if (cardToMove.getValue() == 13 && cardToMove.isFirstCard())
                            continue;
                        //example: i don't want to move a hearts 5 to a clubs 6 if the hearts card is already lying on a (faced up) spades 6.
                        if (sameCardOnOtherStack(cardToMove, stacks[j], SAME_VALUE_AND_COLOR))
                            continue;

                        return new CardAndStack(cardToMove, stacks[j]);
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Stack doubleTapTest(Card card) {
        //tableau fields first
        for (int j = 0; j < 7; j++) {

            if (!stacks[j].isEmpty() && card.getStack().getID() != j && card.test(stacks[j]) && !sameCardOnOtherStack(card, stacks[j], SAME_VALUE_AND_COLOR)) {
                return stacks[j];
            }
        }

        //then foundation stacks
        if (card.isTopCard()) {
            for (int j = 7; j <= 10; j++) {
                if (card.getStack().getID() != j && card.test(stacks[j])) {
                    return stacks[j];
                }
            }
        }

        //and empty stacks
        for (int k = 0; k < 7; k++) {
            if (card.getValue() == 13 && card.isFirstCard() && stacks[k].isEmpty())
                continue;

            if (stacks[k].isEmpty() && card.test(stacks[k])) {
                return stacks[k];
            }
        }

        return null;
    }

    public boolean autoCompleteStartTest() {
        /*
         * start auto complete if every card is in the right order
         */
        for (int i = 0; i < 7; i++) {
            if (stacks[i].isEmpty())
                continue;

            for (int j = 0; j < stacks[i].getSize() - 1; j++) {
                Card cardBottom = stacks[i].getCard(j);
                Card cardTop = stacks[i].getCard(j + 1);

                if (!cardBottom.isUp() || !cardTop.isUp())
                    return false;

                if ((cardBottom.getColor() % 2 == cardTop.getColor() % 2) || (cardBottom.getValue() != cardTop.getValue() + 1))
                    return false;
            }
        }

        return true;
    }

    public CardAndStack autoCompletePhaseTwo() {
        for (int i = 0; i < 7; i++) {
            Stack origin = stacks[i];

            if (origin.isEmpty())
                continue;

            for (int j = 7; j < 11; j++) {
                Stack destination = stacks[j];

                if (origin.getTopCard().test(destination))
                    return new CardAndStack(origin.getTopCard(), destination);
            }
        }

        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs) {
        if (originIDs[0] < 7 && destinationIDs[0] >= 7)                                         //from tableau to foundations
            return 60;
        if (destinationIDs[0] < 7 && originIDs[0] >= 7)                                        //foundations to tableau
            return -75;
        if (originIDs[0] == destinationIDs[0])                                                  //card flip up
            return 25;
        if (!cards.get(0).isFirstCard() && cards.get(0).getValue() == 13 && destinationIDs[0] < 7 && stacks[originIDs[0]].getSize() != 1)//king to an empty filed
            return 20;
        else
            return 0;
    }
}

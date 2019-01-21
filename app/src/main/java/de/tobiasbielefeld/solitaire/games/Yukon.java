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
 * Yukon Game! 7 tableau stacks, 4 foundation stacks and no main stack
 */

public class Yukon extends Game {

    public Yukon() {
        setNumberOfDecks(1);
        setNumberOfStacks(11);

        setTableauStackIDs(0,1,2,3,4,5,6);
        setFoundationStackIDs(7,8,9,10);
        setDealFromID(0);
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape, Context context) {
        //initialize the dimensions
        setUpCardDimensions(layoutGame, 9, 5);

        //order the stacks on the screen
        int spacingHorizontal = setUpHorizontalSpacing(layoutGame, 8, 9);
        int spacingVertical = min((layoutGame.getHeight() - 4 * Card.height) / 5, Card.width / 4);
        int startPos = (int) (layoutGame.getWidth() / 2 - 4 * Card.width - 3.5 * spacingHorizontal);
        //tableau stacks
        for (int i = 0; i <= 7; i++) {
            stacks[i].setX(startPos + spacingHorizontal * i + Card.width * i);
            stacks[i].setY(spacingVertical);
        }
        //foundation stacks
        for (int i = 8; i <= 10; i++) {
            stacks[i].setX(stacks[7].getX());
            stacks[i].setY(stacks[i - 1].getY() + Card.height + spacingVertical);
        }
        //nice background for foundation stacks
        for (int i = 7; i <= 10; i++) {
            stacks[i].setImageBitmap(Stack.background1);
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

        prefs.saveYukonRulesOld();

        for (int i = 1; i <= 6; i++) {
            for (int j = 0; j < 5 + i; j++) {
                moveToStack(getDealStack().getTopCard(), stacks[i], OPTION_NO_RECORD);

                if (j >= i) {
                    stacks[i].getTopCard().flipUp();
                }
            }
        }

        getDealStack().flipTopCardUp();
    }

    public int onMainStackTouch() {
        //no main stack, so empty
        return 0;
    }

    public boolean cardTest(Stack stack, Card card) {

        if (stack.getId() < 7) {                                                                    //tableau
            if (stack.isEmpty()) {
                return card.getValue() == 13;
            } else {
                return checkRules(stack, card) && (stack.getTopCard().getValue() == card.getValue() + 1);
            }
        } else if (movingCards.hasSingleCard()) {                                                     //foundation
            if (stack.isEmpty()) {
                return card.getValue() == 1;
            } else {
                return canCardBePlaced(stack, card, SAME_FAMILY, ASCENDING);
            }
        } else {
            return false;
        }
    }

    boolean checkRules(Stack stack, Card card) {
        boolean defaultRules = prefs.getSavedYukonRulesOld().equals("default");

        return canCardBePlaced(stack, card, defaultRules ? ALTERNATING_COLOR : SAME_FAMILY, DESCENDING);

    }

    public boolean addCardToMovementGameTest(Card card) {
        //yukon is simple in this way: you can move every card
        return true;
    }

    public CardAndStack hintTest(ArrayList<Card> visited) {
        for (int i = 0; i < 7; i++) {
            Stack sourceStack = stacks[i];

            for (int k = 0; k < sourceStack.getSize(); k++) {
                Card cardToMove = sourceStack.getCard(k);

                for (int j = 0; j < 11; j++) {
                    Stack otherStack = stacks[j];

                    if (sourceStack.isEmpty() || i == j)
                        continue;

                    if (j >= 7 && !cardToMove.isTopCard())
                        continue;

                    if (cardToMove.isUp() && !visited.contains(cardToMove) && cardToMove.test(otherStack)) {
                        //don't move if it's an ace and not a top card and also not if the stack id is below 7
                        //so only move single aces to the foundation stacks
                        if (cardToMove.getValue() == 1 && j < 7)
                            continue;
                        //move kings not when they are the first card on a stack
                        //so they won't be moved around on empty fields
                        if (cardToMove.getValue() == 13 && cardToMove.isFirstCard() && tableauStacksContain(j))
                            continue;
                        //example: i don't want to move a hearts 5 to a clubs 6 if the hearts card is already lying on a (faced up) spades 6.
                        if (sameCardOnOtherStack(cardToMove, otherStack, SAME_VALUE_AND_COLOR))
                            continue;

                        return new CardAndStack(cardToMove, otherStack);
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Stack doubleTapTest(Card card) {
        //then foundation stacks
        if (card.isTopCard()) {
            for (int j = 7; j <= 10; j++) {
                if (card.getStackId() != j && card.test(stacks[j])) {
                    return stacks[j];
                }
            }
        }

        //tableau fields first
        for (int j = 0; j < 7; j++) {

            if (!stacks[j].isEmpty() && card.getStackId() != j && card.test(stacks[j]) && !sameCardOnOtherStack(card, stacks[j], SAME_VALUE_AND_COLOR)) {
                return stacks[j];
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
            if (!testCardsUpToTop(stacks[i], 0, DOESNT_MATTER)) {
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

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs, boolean isUndoMovement) {
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

    @Override
    protected boolean excludeCardFromMixing(Card card) {
        boolean defaultRules = prefs.getSavedYukonRulesOld().equals("default");
        setMixingCardsTestMode(defaultRules ? ALTERNATING_COLOR : SAME_FAMILY);

        return super.excludeCardFromMixing(card);
    }
}

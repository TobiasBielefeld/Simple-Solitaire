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
import static de.tobiasbielefeld.solitaire.games.Game.testMode3.*;

/**
 * Klondike game! This game has 7 tableau stacks, 4 foundation fields,
 * 1 main stack and 3 discard stacks. The 3 discard stacks are for the "deal3" option. if it's
 * set to "deal1", the last discard stack will be used
 */

public class Klondike extends Game {

    public Klondike() {
        setNumberOfDecks(1);
        setNumberOfStacks(15);
        setFirstMainStackID(14);
        setFirstDiscardStackID(11);
        setLastTableauID(6);
        setHasFoundationStacks(true);
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape) {

        // initialize the dimensions
        setUpCardWidth(layoutGame, isLandscape, 8, 10);

        //calculate spacing and startposition of cards
        int spacing = setUpHorizontalSpacing(layoutGame, 7, 8);
        int startPos = layoutGame.getWidth() / 2 - Card.width / 2 - 3 * Card.width - 3 * spacing;

        //first order the foundation stacks
        for (int i = 0; i < 4; i++) {
            stacks[7 + i].setX(startPos + spacing * i + Card.width * i);
            stacks[7 + i].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1);
        }

        //then the trash and main stacks
        startPos = layoutGame.getWidth() - 2 * spacing - 3 * Card.width;
        for (int i = 0; i < 3; i++) {
            stacks[11 + i].setX(startPos + Card.width / 2 * i);
            stacks[11 + i].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1);
        }
        stacks[14].setX(stacks[13].getX() + Card.width + spacing);
        stacks[14].setY(stacks[13].getY());

        //now the tabluea stacks
        startPos = layoutGame.getWidth() / 2 - Card.width / 2 - 3 * Card.width - 3 * spacing;
        for (int i = 0; i < 7; i++) {
            stacks[i].setX(startPos + spacing * i + Card.width * i);
            stacks[i].setY(stacks[7].getY() + Card.height +
                    (isLandscape ? Card.width / 4 : Card.width / 2));
        }

        //also set backgrounds of the stacks
        for (Stack stack : stacks) {
            if (stack.getId() > 6 && stack.getId() <= 10)
                stack.view.setImageBitmap(Stack.background1);
            else if (stack.getId() > 10 && stack.getId() <= 13)
                stack.view.setImageBitmap(Stack.backgroundTransparent);
            else if (stack.getId() == 14)
                stack.view.setImageBitmap(Stack.backgroundTalon);
        }
    }

    public boolean winTest() {
        //if the foundation stacks aren't full, not won. Else won
        for (int i = 7; i <= 10; i++) {
            if (stacks[i].getSize() != 13) {
                return false;
            }
        }

        return true;
    }

    public void dealCards() {
        //save the new settings, so it only takes effect on new deals
        putSharedString(PREF_KEY_KLONDIKE_DRAW_OLD, getSharedString(PREF_KEY_KLONDIKE_DRAW, DEFAULT_KLONDIKE_DRAW));

        //and move cards to the tableau
        for (int i = 0; i <= 6; i++) {
            for (int j = 0; j < i + 1; j++) {
                moveToStack(getMainStack().getTopCard(), stacks[i], OPTION_NO_RECORD);
                if (j == i)
                    stacks[i].getTopCard().flipUp();
            }
        }

        //deal cards to trash according to the draw option
        if (sharedStringEquals(PREF_KEY_KLONDIKE_DRAW_OLD, DEFAULT_KLONDIKE_DRAW)) {
            moveToStack(getMainStack().getTopCard(), stacks[13], OPTION_NO_RECORD);
            stacks[13].getTopCard().flipUp();
        } else {
            for (int i = 0; i < 3; i++) {
                moveToStack(getMainStack().getTopCard(), stacks[11 + i], OPTION_NO_RECORD);
                stacks[11 + i].getTopCard().flipUp();
            }
        }
    }

    public int onMainStackTouch() {

        boolean deal3 = !sharedStringEquals(PREF_KEY_KLONDIKE_DRAW_OLD, DEFAULT_KLONDIKE_DRAW);

        //if there are cards on the main stack
        if (getMainStack().getSize() > 0) {
            if (deal3) {
                int size = min(3, getMainStack().getSize());
                ArrayList<Card> cardsReversed = new ArrayList<>();
                ArrayList<Stack> originReversed = new ArrayList<>();
                ArrayList<Card> cards = new ArrayList<>();
                ArrayList<Stack> origin = new ArrayList<>();

                //add cards from 2. and 3. discard stack to the first one
                while (!stacks[12].isEmpty()) {
                    cards.add(stacks[12].getTopCard());
                    origin.add(stacks[12]);
                    moveToStack(stacks[12].getTopCard(), stacks[11], OPTION_NO_RECORD);
                }
                while (!stacks[13].isEmpty()) {
                    cards.add(stacks[13].getTopCard());
                    origin.add(stacks[13]);
                    moveToStack(stacks[13].getTopCard(), stacks[11], OPTION_NO_RECORD);
                }

                //reverse the array orders, soon they will be reversed again so they are in the right
                // order again at this part, because now there are only the cards from the discard
                // stacks on. So i don't need to save how many cards are actually moved
                // (for example, when the third stack is empty
                for (int i = 0; i < cards.size(); i++) {
                    cardsReversed.add(cards.get(cards.size() - 1 - i));
                    originReversed.add(origin.get(cards.size() - 1 - i));
                }
                for (int i = 0; i < cards.size(); i++) {
                    cards.set(i, cardsReversed.get(i));
                    origin.set(i, originReversed.get(i));
                }

                //add up to 3 cards from main to the first discard stack
                for (int i = 0; i < size; i++) {
                    cards.add(getMainStack().getTopCard());
                    origin.add(getMainStack());
                    moveToStack(getMainStack().getTopCard(), stacks[11], OPTION_NO_RECORD);
                    stacks[11].getTopCard().flipUp();
                }

                //then move up to 2 cards to the 2. and 3. discard stack
                size = min(3, stacks[11].getSize());
                if (size > 1) {
                    moveToStack(stacks[11].getCardFromTop(1), stacks[12], OPTION_NO_RECORD);
                    if (!cards.contains(stacks[12].getTopCard())) {
                        cards.add(stacks[12].getTopCard());
                        origin.add(stacks[11]);
                    }
                }
                if (size > 0) {
                    moveToStack(stacks[11].getTopCard(), stacks[13], OPTION_NO_RECORD);
                    if (!cards.contains(stacks[13].getTopCard())) {
                        cards.add(stacks[13].getTopCard());
                        origin.add(stacks[11]);
                    }
                }

                //now bring the cards to front
                if (!stacks[12].isEmpty())
                    stacks[12].getTopCard().view.bringToFront();

                if (!stacks[13].isEmpty())
                    stacks[13].getTopCard().view.bringToFront();

                //reverse everything so the cards on the stack will be in the right order when using an undo
                //the cards from 2. and 3 trash stack are in the right order again
                cardsReversed.clear();
                originReversed.clear();
                for (int i = 0; i < cards.size(); i++) {
                    cardsReversed.add(cards.get(cards.size() - 1 - i));
                    originReversed.add(origin.get(cards.size() - 1 - i));
                }

                //finally add the record
                recordList.add(cardsReversed, originReversed);
            } else {
                //no deal3 option, just deal one card without that fucking huge amount of calculation for the recordLit
                moveToStack(getMainStack().getTopCard(), stacks[13]);
                stacks[13].getTopCard().flipUp();
            }

            return 1;
        }
        //if there are NO cards on the main stack, but cards on the discard stacks, move them all to main
        else if (stacks[11].getSize() != 0 || stacks[12].getSize() != 0 || stacks[13].getSize() != 0) {
            ArrayList<Card> cards = new ArrayList<>();

            for (int i = 0; i < stacks[11].getSize(); i++)
                cards.add(stacks[11].getCard(i));

            for (int i = 0; i < stacks[12].getSize(); i++)
                cards.add(stacks[12].getCard(i));

            for (int i = 0; i < stacks[13].getSize(); i++)
                cards.add(stacks[13].getCard(i));

            ArrayList<Card> cardsReversed = new ArrayList<>();
            for (int i = 0; i < cards.size(); i++)
                cardsReversed.add(cards.get(cards.size() - 1 - i));

            moveToStack(cardsReversed, getMainStack(), OPTION_REVERSED_RECORD);
            return 2;
        }

        return 0;
    }

    public boolean autoCompleteStartTest() {

        //if every card is faced up, show the auto complete button
        for (int i = 0; i < 7; i++)
            if (stacks[i].getSize() > 0 && !stacks[i].getCard(0).isUp())
                return false;

        //for deal3 mode, discard and main stack have to be empty too
        if (!sharedStringEquals(PREF_KEY_KLONDIKE_DRAW_OLD, DEFAULT_KLONDIKE_DRAW)) {
            if (getMainStack().getSize()>0 || getDiscardStack().getSize()>0){
                return false;
            }
        }

        return true;
    }

    public boolean cardTest(Stack stack, Card card) {
        //move cards according to the klondike rules
        if (stack.getId() < 7) {
            if (stack.isEmpty()) {
                return card.getValue() == 13;
            } else {
                return canCardBePlaced(stack, card, ALTERNATING_COLOR, DESCENDING);
            }
        } else if (stack.getId() < 11 && movingCards.hasSingleCard()) {
            if (stack.isEmpty()) {
                return card.getValue() == 1;
            } else {
                return canCardBePlaced(stack, card, SAME_FAMILY, ASCENDING);
            }
        } else
            return false;
    }

    public boolean addCardToMovementTest(Card card) {
        //don't move cards from the discard stacks if there is a card on top of them
        //for example: if touched a card on stack 11 (first discard stack) but there is a card
        //on stack 12 (second discard stack) don't move if.
        return !(((card.getStackId() == 11 || card.getStackId() == 12) && !stacks[13].isEmpty())
                || (card.getStackId() == 11 && !stacks[12].isEmpty()));
    }

    public CardAndStack hintTest() {
        Card card;

        for (int i = 0; i <= 6; i++) {

            Stack origin = stacks[i];

            if (origin.isEmpty())
                continue;

            /* complete visible part of a stack to move on the tableau */
            card = origin.getFirstUpCard();

            if (!hint.hasVisited(card) && !(card.isFirstCard() && card.getValue() == 13)
                    && card.getValue() != 1) {
                for (int j = 0; j <= 6; j++) {
                    if (j == i)
                        continue;

                    if (card.test(stacks[j])) {
                        return new CardAndStack(card, stacks[j]);
                    }
                }
            }

            /* last card of a stack to move to the foundation */
            card = origin.getTopCard();

            if (!hint.hasVisited(card)) {
                for (int j = 7; j <= 10; j++) {
                    if (card.test(stacks[j]))
                        return new CardAndStack(card, stacks[j]);
                }
            }

        }

        /* card from trash of stock to every other stack*/
        for (int i = 0; i < 3; i++) {

            if ((i < 2 && !stacks[13].isEmpty()) || (i == 0 && !stacks[12].isEmpty()))
                continue;

            if (stacks[11 + i].getSize() > 0 && !hint.hasVisited(stacks[11 + i].getTopCard())) {
                for (int j = 10; j >= 0; j--) {
                    if (stacks[11 + i].getTopCard().test(stacks[j])) {
                        return new CardAndStack(stacks[11 + i].getTopCard(), stacks[j]);
                    }
                }
            }
        }

        return null;
    }

    public Stack doubleTapTest(Card card) {

        //foundation stacks
        if (card.isTopCard()) {
            for (int j = 7; j < 11; j++) {
                if (card.test(stacks[j]))
                    return stacks[j];
            }
        }

        //tableau stacks
        for (int j = 0; j < 7; j++) {

            if (card.getStackId() < 7 && sameCardOnOtherStack(card, stacks[j], SAME_VALUE_AND_COLOR))
                continue;

            if (card.getValue() == 13 && card.isFirstCard() && card.getStackId() <= 6)
                continue;

            if (card.test(stacks[j])) {
                return stacks[j];
            }
        }

        //empty tableau stacks
        for (int j = 0; j < 7; j++) {
            if (stacks[j].isEmpty() && card.test(stacks[j]))
                return stacks[j];
        }

        return null;
    }

    public CardAndStack autoCompletePhaseOne() {
        return null;
    }

    public CardAndStack autoCompletePhaseTwo() {
        //just go through every stack
        for (int i = 7; i <= 10; i++) {
            Stack destination = stacks[i];

            for (int j = 0; j <= 6; j++) {
                Stack origin = stacks[j];

                if (origin.getSize() > 0 && origin.getTopCard().test(destination)) {
                    return new CardAndStack(origin.getTopCard(), destination);
                }
            }

            for (int j = 11; j < 15; j++) {
                Stack origin = stacks[j];

                for (int k = 0; k < origin.getSize(); k++) {
                    if (origin.getCard(k).test(destination)) {
                        origin.getCard(k).flipUp();
                        return new CardAndStack(origin.getCard(k), destination);
                    }
                }
            }
        }

        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs, boolean isUndoMovement) {
        int originID = originIDs[0];
        int destinationID = destinationIDs[0];

        if (originID >= 11 && originID <= 13 && destinationID >= 11 && destinationID <= 13)            //used for from stock to tabaleau/f
            return 45;
        if ((originID < 7 || originID == 14) && destinationID >= 7 && destinationID <= 10)          //transfer from tableau to foundations
            return 60;
        if ((originID == 11 || originID == 12 || originID == 13) && destinationID < 10)              //stock to tableau
            return 45;
        if (destinationID < 7 && originID >= 7 && originID <= 10)                                   //foundation to tableau
            return -75;
        if (originID == destinationID)                                                              //turn a card over
            return 25;
        if (originID >= 11 && originID < 14 && destinationID == 14)                                 //returning cards to stock
            return -200;

        return 0;
    }

    public void testAfterMove() {
        /*
         *  after a card is moved from the discard stacks, it needs to update the order of the cards
         *  on the discard stacks. (But only in deal3 mode).
         *  This movement will be added to the last record list entry, so it will be also undone if
         *  the card will be moved back to the discard stacks
         */
        if (sharedStringEquals(PREF_KEY_KLONDIKE_DRAW_OLD, DEFAULT_KLONDIKE_DRAW) || gameLogic.hasWon())
            return;

        if (stacks[12].getSize() == 0 || stacks[13].getSize() == 0) {
            ArrayList<Card> cards = new ArrayList<>();
            ArrayList<Stack> origin = new ArrayList<>();

            //add the cards to the first discard pile
            while (!stacks[12].isEmpty()) {
                cards.add(stacks[12].getTopCard());
                origin.add(stacks[12]);
                moveToStack(stacks[12].getTopCard(), stacks[11], OPTION_NO_RECORD);
            }

            //and then move cards from there to fill the discard stacks
            if (stacks[11].getSize() > 1) {
                moveToStack(stacks[11].getCardFromTop(1), stacks[12], OPTION_NO_RECORD);
                if (!cards.contains(stacks[12].getTopCard())) {
                    cards.add(stacks[12].getTopCard());
                    origin.add(stacks[11]);
                }
            }
            if (!stacks[11].isEmpty()) {
                moveToStack(stacks[11].getTopCard(), stacks[13], OPTION_NO_RECORD);
                if (!cards.contains(stacks[13].getTopCard())) {
                    cards.add(stacks[13].getTopCard());
                    origin.add(stacks[11]);
                }
            }

            //reverse order for the record
            ArrayList<Card> cardsReversed = new ArrayList<>();
            ArrayList<Stack> originReversed = new ArrayList<>();
            for (int i = 0; i < cards.size(); i++) {
                cardsReversed.add(cards.get(cards.size() - 1 - i));
                originReversed.add(origin.get(cards.size() - 1 - i));
            }

            if (!stacks[12].isEmpty())
                stacks[12].getTopCard().view.bringToFront();

            if (!stacks[13].isEmpty())
                stacks[13].getTopCard().view.bringToFront();

            //and add it IN FRONT of the last entry
            recordList.addInFrontOfLastEntry(cardsReversed, originReversed);
        }
    }
}

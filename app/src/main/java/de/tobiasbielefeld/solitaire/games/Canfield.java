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

import android.graphics.Bitmap;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.Stack;

import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.games.Game.testMode2.*;

/**
 * Klondike game! This game has 7 tableau stacks, 4 foundation fields,
 * 1 main stack and 3 discard stacks. The 3 discard stacks are for the "deal3" option. if it's
 * set to "deal1", the last discard stack will be used
 */

public class Canfield extends Game {

    private int startCardValue;

    public Canfield() {
        setNumberOfDecks(1);
        setNumberOfStacks(13);
        setFirstMainStackID(12);
        setFirstDiscardStackID(9);
        setLastTableauID(4);
        setHasFoundationStacks(true);
    }

    @Override
    public void save() {
        putInt(CANFIELD_START_CARD_VALUE, startCardValue);
    }

    @Override
    public void load() {
        startCardValue = getInt(CANFIELD_START_CARD_VALUE, 0);
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape) {

        // initialize the dimensions
        setUpCardWidth(layoutGame, isLandscape, 8, 10);

        //calculate spacing and startposition of cards
        int spacing = setUpHorizontalSpacing(layoutGame, 7, 8);
        int startPos = layoutGame.getWidth() / 2 - Card.width / 2 - 3 * Card.width - 3 * spacing;

        //first order the foundation stacks
        for (int i = 0; i < 4; i++) {
            stacks[5 + i].setX(startPos + spacing * i + Card.width * i);
            stacks[5 + i].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1);
        }

        //then the trash and main stacks
        startPos = layoutGame.getWidth() - 2 * spacing - 3 * Card.width;
        for (int i = 0; i < 3; i++) {
            stacks[9 + i].setX(startPos + Card.width / 2 * i);
            stacks[9 + i].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1);
        }
        stacks[12].setX(stacks[11].getX() + Card.width + spacing);
        stacks[12].setY(stacks[11].getY());

        stacks[4].setX(stacks[12].getX());
        stacks[4].setY(stacks[12].getY() + Card.height +
                (isLandscape ? Card.width / 4 : Card.width / 2));

        //now the tableau stacks
        startPos = layoutGame.getWidth() / 2 - Card.width / 2 - 3 * Card.width - 3 * spacing;
        for (int i = 0; i < 4; i++) {
            stacks[i].setX(startPos + spacing * i + Card.width * i);
            stacks[i].setY(stacks[7].getY() + Card.height +
                    (isLandscape ? Card.width / 4 : Card.width / 2));
        }

        //also set backgrounds of the stacks
        for (int i = 9; i < 12; i++)
            stacks[i].view.setImageBitmap(Stack.backgroundTransparent);

        stacks[12].view.setImageBitmap(Stack.backgroundTalon);
        load();
        setFoundationBackgrounds();
    }

    public boolean winTest() {
        //if the foundation stacks aren't full, not won. Else won
        for (int i = 5; i <= 8; i++)
            if (stacks[i].getSize() != 13)
                return false;

        return true;
    }

    private void setFoundationBackgrounds() {
        Bitmap bitmap;

        switch (startCardValue) {
            case 1:
            default:
                bitmap = Stack.background1;
                break;
            case 2:
                bitmap = Stack.background2;
                break;
            case 3:
                bitmap = Stack.background3;
                break;
            case 4:
                bitmap = Stack.background4;
                break;
            case 5:
                bitmap = Stack.background5;
                break;
            case 6:
                bitmap = Stack.background6;
                break;
            case 7:
                bitmap = Stack.background7;
                break;
            case 8:
                bitmap = Stack.background8;
                break;
            case 9:
                bitmap = Stack.background9;
                break;
            case 10:
                bitmap = Stack.background10;
                break;
            case 11:
                bitmap = Stack.background11;
                break;
            case 12:
                bitmap = Stack.background12;
                break;
            case 13:
                bitmap = Stack.background13;
                break;
        }

        for (int i = 5; i < 9; i++) {
            stacks[i].view.setImageBitmap(bitmap);
        }
    }

    public void dealCards() {

        //save the new settings, so it only takes effect on new deals
        putSharedString(PREF_KEY_CANFIELD_DRAW_OLD, getSharedString(PREF_KEY_CANFIELD_DRAW, DEFAULT_CANFIELD_DRAW));

        //and move cards to the tableau
        for (int i = 0; i < 4; i++) {
            moveToStack(getMainStack().getTopCard(), stacks[i], OPTION_NO_RECORD);
            stacks[i].getTopCard().flipUp();
        }

        //one card to reserve
        for (int i = 0; i < 13; i++) {
            moveToStack(getMainStack().getTopCard(), stacks[4], OPTION_NO_RECORD);
        }

        stacks[4].getTopCard().flipUp();

        //one card to foundation, and save its value
        moveToStack(getMainStack().getTopCard(), stacks[5], OPTION_NO_RECORD);
        stacks[5].getTopCard().flipUp();
        startCardValue = stacks[5].getTopCard().getValue();
        setFoundationBackgrounds();

        //deal cards to trash according to the draw option
        if (sharedStringEquals(PREF_KEY_CANFIELD_DRAW_OLD, DEFAULT_CANFIELD_DRAW)) {
            for (int i = 0; i < 3; i++) {
                moveToStack(getMainStack().getTopCard(), stacks[9 + i], OPTION_NO_RECORD);
                stacks[9 + i].getTopCard().flipUp();
            }
        } else {
            moveToStack(getMainStack().getTopCard(), stacks[11], OPTION_NO_RECORD);
            stacks[11].getTopCard().flipUp();
        }
    }

    public void onMainStackTouch() {

        boolean deal3 = sharedStringEquals(PREF_KEY_CANFIELD_DRAW_OLD, DEFAULT_CANFIELD_DRAW);

        //if there are cards on the main stack
        if (getMainStack().getSize() > 0) {
            if (deal3) {
                int size = min(3, getMainStack().getSize());
                ArrayList<Card> cardsReversed = new ArrayList<>();
                ArrayList<Stack> originReversed = new ArrayList<>();
                ArrayList<Card> cards = new ArrayList<>();
                ArrayList<Stack> origin = new ArrayList<>();

                //add cards from 2. and 3. discard stack to the first one
                while (!stacks[10].isEmpty()) {
                    cards.add(stacks[10].getTopCard());
                    origin.add(stacks[10]);
                    moveToStack(stacks[10].getTopCard(), stacks[9], OPTION_NO_RECORD);
                }
                while (!stacks[11].isEmpty()) {
                    cards.add(stacks[11].getTopCard());
                    origin.add(stacks[11]);
                    moveToStack(stacks[11].getTopCard(), stacks[9], OPTION_NO_RECORD);
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
                    moveToStack(getMainStack().getTopCard(), stacks[9], OPTION_NO_RECORD);
                    stacks[9].getTopCard().flipUp();
                }

                //then move up to 2 cards to the 2. and 3. discard stack
                size = stacks[9].getSize();
                if (size > 1) {
                    moveToStack(stacks[9].getCardFromTop(1), stacks[10], OPTION_NO_RECORD);
                    if (!cards.contains(stacks[10].getTopCard())) {
                        cards.add(stacks[10].getTopCard());
                        origin.add(stacks[9]);
                    }
                }
                if (size > 0) {
                    moveToStack(stacks[9].getTopCard(), stacks[11], OPTION_NO_RECORD);
                    if (!cards.contains(stacks[11].getTopCard())) {
                        cards.add(stacks[11].getTopCard());
                        origin.add(stacks[9]);
                    }
                }

                //now bring the cards to front
                if (!stacks[10].isEmpty())
                    stacks[10].getTopCard().view.bringToFront();

                if (!stacks[11].isEmpty())
                    stacks[11].getTopCard().view.bringToFront();

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
                moveToStack(getMainStack().getTopCard(), stacks[11]);
                stacks[11].getTopCard().flipUp();
            }
        }
        //of there are NO cards on the main stack, but cards on the discard stacks, move them all to main
        else if (stacks[9].getSize() != 0 || stacks[10].getSize() != 0 || stacks[11].getSize() != 0) {
            ArrayList<Card> cards = new ArrayList<>();

            for (int i = 0; i < stacks[9].getSize(); i++)
                cards.add(stacks[9].getCard(i));

            for (int i = 0; i < stacks[10].getSize(); i++)
                cards.add(stacks[10].getCard(i));

            for (int i = 0; i < stacks[11].getSize(); i++)
                cards.add(stacks[11].getCard(i));

            ArrayList<Card> cardsReversed = new ArrayList<>();
            for (int i = 0; i < cards.size(); i++)
                cardsReversed.add(cards.get(cards.size() - 1 - i));

            moveToStack(cardsReversed, getMainStack(), OPTION_REVERSED_RECORD);
        }
    }

    public boolean autoCompleteStartTest() {
        //if every card is faced up, show the auto complete button
        /*for (int i = 0; i < 7; i++)
            if (stacks[i].getSize() > 0 && !stacks[i].getCard(0).isUp())
                return false;*/

        return false;
    }

    public boolean cardTest(Stack stack, Card card) {
        if (stack.getId() == 4)
            return false;

        if (stack.getId() < 4) {
            return stack.isEmpty() || (stack.getTopCard().getColor() % 2 != card.getColor() % 2)
                    && ((stack.getTopCard().getValue() == card.getValue() + 1) || (stack.getTopCard().getValue() == 1 && card.getValue() == 13));

        } else if (stack.getId() < 9 && movingCards.hasSingleCard()) {
            if (stack.isEmpty())
                return card.getValue() == startCardValue;
            else
                return (stack.getTopCard().getColor() == card.getColor() && ((stack.getTopCard().getValue() == 13 && card.getValue() == 1) || (stack.getTopCard().getValue() == card.getValue() - 1)));
        } else
            return false;
    }

    public boolean addCardToMovementTest(Card card) {
        //don't move cards from the discard stacks if there is a card on top of them
        //for example: if touched a card on stack 11 (first discard stack) but there is a card
        //on stack 12 (second discard stack) don't move if.
        return !(((card.getStackId() == 9 || card.getStackId() == 10) && !stacks[11].isEmpty())
                || (card.getStackId() == 9 && !stacks[10].isEmpty()));
    }

    public CardAndStack hintTest() {
        Card card;

        for (int i = 0; i <= 4; i++) {

            Stack origin = stacks[i];

            if (origin.isEmpty())
                continue;

            /* complete visible part of a stack to move on the tableau */
            card = origin.getCard(0);

            if (!hint.hasVisited(card) && card.getValue() != startCardValue) {
                for (int j = 0; j <= 3; j++) {
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
                for (int j = 5; j <= 8; j++) {
                    if (card.test(stacks[j]))
                        return new CardAndStack(card, stacks[j]);
                }
            }

        }

        for (int i = 5; i <= 8; i++) {

            Stack origin = stacks[i];

            if (origin.isEmpty())
                continue;

            /* last card of a stack to move to the foundation */
            card = origin.getTopCard();

            if (!hint.hasVisited(card)) {
                for (int j = 0; j <= 3; j++) {
                    if (card.test(stacks[j]))
                        return new CardAndStack(card, stacks[j]);
                }
            }

        }

        /* card from trash of stock to every other stack*/
        for (int i = 0; i < 3; i++) {
            if ((i < 2 && !stacks[11].isEmpty()) || (i == 0 && !stacks[10].isEmpty()))
                continue;

            if (stacks[9 + i].getSize() > 0 && !hint.hasVisited(stacks[9 + i].getTopCard())) {
                for (int j = 0; j <= 3; j++) {
                    if (stacks[9 + i].getTopCard().test(stacks[j])) {
                        return new CardAndStack(stacks[9 + i].getTopCard(), stacks[j]);
                    }
                }

                for (int j = 5; j <= 8; j++) {
                    if (stacks[9 + i].getTopCard().test(stacks[j])) {
                        return new CardAndStack(stacks[9 + i].getTopCard(), stacks[j]);
                    }
                }
            }
        }

        return null;
    }

    public Stack doubleTapTest(Card card) {

        //foundation stacks

        if (card.isTopCard() && !(card.getStackId() >= 5 && card.getStackId() <= 8)) {
            for (int j = 5; j < 9; j++) {
                if (card.test(stacks[j]))
                    return stacks[j];
            }
        }

        //tableau stacks
        for (int j = 0; j < 5; j++) {

            if (card.getStackId() < 4 && sameCardOnOtherStack(card, stacks[j], SAME_VALUE_AND_COLOR))
                continue;

            if (card.getValue() == 13 && card.isFirstCard() && card.getStackId() <= 3)
                continue;

            if (card.test(stacks[j])) {
                return stacks[j];
            }
        }

        for (int j = 0; j < 4; j++) {
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
        for (int i = 5; i <= 8; i++) {
            Stack destination = stacks[i];

            for (int j = 0; j <= 4; j++) {
                Stack origin = stacks[j];

                if (origin.getSize() > 0 && origin.getTopCard().test(destination)) {
                    return new CardAndStack(origin.getTopCard(), destination);
                }
            }

            for (int j = 9; j <= 12; j++) {
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

        if (originID >= 9 && originID <= 11 && destinationID >= 9 && destinationID <= 11)            //used for from stock to tabaleau/foundation
            return 45;
        if ((originID < 5 || originID == 12) && destinationID >= 5 && destinationID <= 8)          //transfer from tableau to foundations
            return 60;
        if ((originID == 9 || originID == 10 || originID == 11) && destinationID < 9)              //stock to tableau
            return 45;
        if (destinationID < 5 && originID >= 5 && originID <= 8)                                   //foundation to tableau
            return -75;
        if (originID == destinationID)                                                              //turn a card over
            return 25;
        if (originID >= 9 && originID < 12 && destinationID == 12)                                 //returning cards to stock
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
        if (gameLogic.hasWon())
            return;

        for (int i = 0; i < 4; i++) {
            if (stacks[i].isEmpty()) {

                if (!stacks[4].isEmpty()) {
                    moveToStack(stacks[4].getTopCard(), stacks[i], OPTION_NO_RECORD);
                    recordList.addAtEndOfLastEntry(stacks[i].getTopCard(), stacks[4]);

                    if (!stacks[4].isEmpty()) {
                        stacks[4].getTopCard().flipWithAnim();
                    }

                } else if (!stacks[11].isEmpty()) {
                    moveToStack(stacks[11].getTopCard(), stacks[i], OPTION_NO_RECORD);
                    recordList.addAtEndOfLastEntry(stacks[i].getTopCard(), stacks[11]);
                } else if (!stacks[10].isEmpty()) {
                    moveToStack(stacks[10].getTopCard(), stacks[i], OPTION_NO_RECORD);
                    recordList.addAtEndOfLastEntry(stacks[i].getTopCard(), stacks[10]);
                } else if (!stacks[9].isEmpty()) {
                    moveToStack(stacks[9].getTopCard(), stacks[i], OPTION_NO_RECORD);
                    recordList.addAtEndOfLastEntry(stacks[i].getTopCard(), stacks[9]);
                } else if (!getMainStack().isEmpty()) {
                    getMainStack().getTopCard().flipUp();
                    moveToStack(getMainStack().getTopCard(), stacks[i], OPTION_NO_RECORD);
                    recordList.addAtEndOfLastEntry(stacks[i].getTopCard(), getMainStack());
                }
            }
        }

        if (!sharedStringEquals(PREF_KEY_CANFIELD_DRAW_OLD, DEFAULT_CANFIELD_DRAW))
            return;

        if (stacks[10].getSize() == 0 || stacks[11].getSize() == 0) {
            ArrayList<Card> cards = new ArrayList<>();
            ArrayList<Stack> origin = new ArrayList<>();

            //add the cards to the first discard pile
            while (!stacks[10].isEmpty()) {
                cards.add(stacks[10].getTopCard());
                origin.add(stacks[10]);
                moveToStack(stacks[10].getTopCard(), stacks[9], OPTION_NO_RECORD);
            }

            //and then move cards from there to fill the discard stacks
            if (stacks[9].getSize() > 1) {
                moveToStack(stacks[9].getCardFromTop(1), stacks[10], OPTION_NO_RECORD);
                if (!cards.contains(stacks[10].getTopCard())) {
                    cards.add(stacks[10].getTopCard());
                    origin.add(stacks[9]);
                }
            }
            if (!stacks[9].isEmpty()) {
                moveToStack(stacks[9].getTopCard(), stacks[11], OPTION_NO_RECORD);
                if (!cards.contains(stacks[11].getTopCard())) {
                    cards.add(stacks[11].getTopCard());
                    origin.add(stacks[9]);
                }
            }

            //reverse order for the record
            ArrayList<Card> cardsReversed = new ArrayList<>();
            ArrayList<Stack> originReversed = new ArrayList<>();
            for (int i = 0; i < cards.size(); i++) {
                cardsReversed.add(cards.get(cards.size() - 1 - i));
                originReversed.add(origin.get(cards.size() - 1 - i));
            }

            if (!stacks[10].isEmpty())
                stacks[10].getTopCard().view.bringToFront();

            if (!stacks[11].isEmpty())
                stacks[11].getTopCard().view.bringToFront();

            //and add it IN FRONT of the last entry
            recordList.addInFrontOfLastEntry(cardsReversed, originReversed);
        }
    }
}

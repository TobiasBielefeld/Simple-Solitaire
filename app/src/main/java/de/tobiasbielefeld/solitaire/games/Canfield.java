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
    }

    @Override
    public void save() {
        putInt("canfield_startCardValue",startCardValue);
    }

    @Override
    public void load() {
        startCardValue = getInt("canfield_startCardValue",0);
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape) {

        // initialize the dimensions
        setUpCardWidth(layoutGame,isLandscape,8,10);

        //calculate spacing and startposition of cards
        int spacing = setUpSpacing(layoutGame,7,8);
        int startPos = layoutGame.getWidth() / 2 - Card.width / 2 - 3 * Card.width - 3 * spacing;

        //first order the foundation stacks
        for (int i = 0; i < 4; i++) {
            stacks[5 + i].view.setX(startPos + spacing * i + Card.width * i);
            stacks[5 + i].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1);
        }

        //then the trash and main stacks
        startPos = layoutGame.getWidth() - 2 * spacing - 3 * Card.width;
        for (int i = 0; i < 3; i++) {
            stacks[9 + i].view.setX(startPos + Card.width / 2 * i);
            stacks[9 + i].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1);
        }
        stacks[12].view.setX(stacks[11].view.getX() + Card.width + spacing);
        stacks[12].view.setY(stacks[11].view.getY());

        stacks[4].view.setX(stacks[12].view.getX());
        stacks[4].view.setY(stacks[12].view.getY() + Card.height +
                (isLandscape ? Card.width / 4 : Card.width / 2));

        //now the tableau stacks
        startPos = layoutGame.getWidth() / 2 - Card.width / 2 - 3 * Card.width - 3 * spacing;
        for (int i = 0; i < 4; i++) {
            stacks[i].view.setX(startPos + spacing * i + Card.width * i);
            stacks[i].view.setY(stacks[7].view.getY() + Card.height +
                    (isLandscape ? Card.width / 4 : Card.width / 2));
        }

        //also set backgrounds of the stacks
        for (int i=9;i<12;i++)
            stacks[i].view.setBackgroundResource(R.drawable.background_stack_transparent);

        stacks[12].view.setBackgroundResource(R.drawable.background_stack_stock);
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

    private void setFoundationBackgrounds(){
        int resID;

        switch (startCardValue){
            case 1:default:
                resID = R.drawable.background_stack_1;
                break;
            case 2:
                resID = R.drawable.background_stack_2;
                break;
            case 3:
                resID = R.drawable.background_stack_3;
                break;
            case 4:
                resID = R.drawable.background_stack_4;
                break;
            case 5:
                resID = R.drawable.background_stack_5;
                break;
            case 6:
                resID = R.drawable.background_stack_6;
                break;
            case 7:
                resID = R.drawable.background_stack_7;
                break;
            case 8:
                resID = R.drawable.background_stack_8;
                break;
            case 9:
                resID = R.drawable.background_stack_9;
                break;
            case 10:
                resID = R.drawable.background_stack_10;
                break;
            case 11:
                resID = R.drawable.background_stack_11;
                break;
            case 12:
                resID = R.drawable.background_stack_12;
                break;
            case 13:
                resID = R.drawable.background_stack_13;
                break;
        }

        for (int i=5;i<9;i++) {
            stacks[i].view.setBackgroundResource(resID);
        }
    }

    public void dealCards() {

        //save the new settings, so it only takes effect on new deals
        putSharedString("pref_key_canfield_draw_old", getSharedString("pref_key_canfield_draw", "3"));

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
        if (getSharedString("pref_key_canfield_draw_old", "1").equals("3")) {
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

        boolean deal3 = getSharedString("pref_key_canfield_draw_old", "1").equals("3");

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

            for (int i=0;i<stacks[9].getSize();i++)
                cards.add(stacks[9].getCard(i));

            for (int i=0;i<stacks[10].getSize();i++)
                cards.add(stacks[10].getCard(i));

            for (int i=0;i<stacks[11].getSize();i++)
                cards.add(stacks[11].getCard(i));

            ArrayList<Card> cardsReversed = new ArrayList<>();
            for (int i = 0; i < cards.size(); i++)
                cardsReversed.add(cards.get(cards.size() - 1 - i));

            moveToStack(cardsReversed,getMainStack(),OPTION_REVERSED_RECORD);
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
        if (stack.getID()==4)
            return false;

        if (stack.getID() < 4) {
            return stack.isEmpty() || (stack.getTopCard().getColor() % 2 != card.getColor() % 2)
                    && ( (stack.getTopCard().getValue() == card.getValue() + 1) || (stack.getTopCard().getValue()==1 && card.getValue()==13) );

        } else if (stack.getID() < 9 && movingCards.hasSingleCard()) {
            if (stack.isEmpty())
                return card.getValue() == startCardValue;
            else
                return (stack.getTopCard().getColor()==card.getColor() && ((stack.getTopCard().getValue()==13 && card.getValue()==1) || (stack.getTopCard().getValue() == card.getValue() - 1)));
        } else
            return false;
    }

    public boolean addCardToMovementTest(Card card) {
        //don't move cards from the discard stacks if there is a card on top of them
        //for example: if touched a card on stack 11 (first discard stack) but there is a card
        //on stack 12 (second discard stack) don't move if.
        return !(((card.getStack().getID() == 9 || card.getStack().getID() == 10) && !stacks[11].isEmpty())
                || (card.getStack().getID() == 9 && !stacks[10].isEmpty()));
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
                        return new CardAndStack(card,stacks[j]);
                    }
                }
            }

            /* last card of a stack to move to the foundation */
            card = origin.getTopCard();

            if (!hint.hasVisited(card)) {
                for (int j = 5; j <= 8; j++) {
                    if (card.test(stacks[j]))
                        return new CardAndStack(card,stacks[j]);
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
                        return new CardAndStack(card,stacks[j]);
                }
            }

        }

        /* card from trash of stock to every other stack*/
        for (int i = 0; i < 3; i++) {
            if ((i < 2 && !stacks[11].isEmpty()) || (i == 0 && !stacks[10].isEmpty()))
                continue;

            if (stacks[9 + i].getSize() > 0 && !hint.hasVisited(stacks[9 + i].getTopCard())) {
                for (int j = 0;j<=3;j++) {
                    if (stacks[9 + i].getTopCard().test(stacks[j])) {
                        return new CardAndStack(stacks[9 + i].getTopCard(),stacks[j]);
                    }
                }

                for (int j = 5;j<=8;j++) {
                    if (stacks[9 + i].getTopCard().test(stacks[j])) {
                        return new CardAndStack(stacks[9 + i].getTopCard(),stacks[j]);
                    }
                }
            }
        }

        return null;
    }

    public Stack doubleTapTest(Card card) {

        //foundation stacks

        if (card.isTopCard() && !(card.getStack().getID()>=5 && card.getStack().getID()<=8)) {
            for (int j = 5; j <9; j++) {
                if (card.test(stacks[j]))
                    return stacks[j];
            }
        }

        //tableau stacks
        for (int j = 0; j < 5; j++) {

            if (card.getStack().getID() < 4 && sameCardOnOtherStack(card,stacks[j],SAME_VALUE_AND_COLOR))
                continue;

            if (card.getValue()==13 && card.isFirstCard() && card.getStack().getID()<=3)
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

    public CardAndStack autoCompletePhaseOne(){
        return null;
    }

    public CardAndStack autoCompletePhaseTwo() {
        //just go through every stack
        for (int i = 5; i <= 8; i++) {
            Stack destination = stacks[i];

            for (int j = 0; j <= 4; j++) {
                Stack origin = stacks[j];

                if (origin.getSize() > 0 && origin.getTopCard().test(destination)) {
                    return new CardAndStack(origin.getTopCard(),destination);
                }
            }

            for (int j = 9; j <= 12; j++) {
                Stack origin = stacks[j];

                for (int k = 0; k < origin.getSize(); k++) {
                    if (origin.getCard(k).test(destination)) {
                        origin.getCard(k).flipUp();
                        return new CardAndStack(origin.getCard(k),destination);
                    }
                }
            }
        }

        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs) {
        int originID = originIDs[0];
        int destinationID = destinationIDs[0];

        if (originID >=9 && originID <= 11 && destinationID >=9 && destinationID <=11)            //used for from stock to tabaleau/foundation
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

        for (int i=0;i<4;i++){
            if (stacks[i].isEmpty()){

                if (!stacks[4].isEmpty()) {
                    moveToStack(stacks[4].getTopCard(),stacks[i],OPTION_NO_RECORD);
                    recordList.addAtEndOfLastEntry(stacks[i].getTopCard(), stacks[4]);

                    if (!stacks[4].isEmpty()) {
                        stacks[4].getTopCard().flipWithAnim();
                    }

                } else if (!stacks[11].isEmpty()){
                    moveToStack(stacks[11].getTopCard(), stacks[i], OPTION_NO_RECORD);
                    recordList.addAtEndOfLastEntry(stacks[i].getTopCard(), stacks[11]);
                } else if (!stacks[10].isEmpty()){
                    moveToStack(stacks[10].getTopCard(), stacks[i], OPTION_NO_RECORD);
                    recordList.addAtEndOfLastEntry(stacks[i].getTopCard(), stacks[10]);
                } else if (!stacks[9].isEmpty()){
                    moveToStack(stacks[9].getTopCard(), stacks[i], OPTION_NO_RECORD);
                    recordList.addAtEndOfLastEntry(stacks[i].getTopCard(), stacks[9]);
                } else if (!getMainStack().isEmpty()){
                    getMainStack().getTopCard().flipUp();
                    moveToStack(getMainStack().getTopCard(), stacks[i], OPTION_NO_RECORD);
                    recordList.addAtEndOfLastEntry(stacks[i].getTopCard(), getMainStack());
                }
            }
        }

        if (!getSharedString("pref_key_canfield_draw_old", "1").equals("3"))
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

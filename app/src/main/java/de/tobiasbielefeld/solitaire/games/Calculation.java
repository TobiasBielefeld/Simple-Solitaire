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
 * Calculation Solitaire, first game which uses custom text views in the layout
 */

public class Calculation extends Game {

    public Calculation() {
        setNumberOfDecks(1);
        setNumberOfStacks(10);

        setTableauStackIDs(0,1,2,3);
        setFoundationStackIDs(4,5,6,7);
        setDiscardStackIDs(8);
        setMainStackIDs(9);

        setMixingCardsTestMode(null);
        prefs.saveCalculationAlternativeModeOld();
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape, Context context) {

        setUpCardWidth(layoutGame, isLandscape, 7 + 1, 7 + 2);

        int spacing = setUpHorizontalSpacing(layoutGame, 7, 8);

        int startPosX = (int) (layoutGame.getWidth()  - 6.5 * Card.width - 4 * spacing)/2;
        int startPosY = isLandscape ? Card.height / 4 : Card.height / 2;

        //foundation stacks
        for (int i = 0; i < 4; i++) {
            stacks[4+i].setX(startPosX  + i * (spacing + Card.width));
            stacks[4+i].setY(startPosY);
        }

        //tableau stacks
        for (int i = 0; i < 4; i++) {
            stacks[i].setX(startPosX  + i * (spacing + Card.width));
            stacks[i].setY(startPosY + Card.height + (isLandscape ? Card.height / 8 : Card.height / 4) + 1);
        }

        //trash
        stacks[8].setX(stacks[3].getX() + Card.width + Card.width / 2);
        stacks[8].setY(stacks[4].getY());

        //stock
        stacks[9].setX(stacks[8].getX() + Card.width + spacing);
        stacks[9].setY(stacks[4].getY());

        stacks[4].setImageBitmap(Stack.background1);
        stacks[5].setImageBitmap(Stack.background2);
        stacks[6].setImageBitmap(Stack.background3);
        stacks[7].setImageBitmap(Stack.background4);

        //generate the textViews over the foundation stacks

        addTextViews(4, Card.width, layoutGame, context);

        for (int i=0;i<4;i++){
            textViewPutAboveStack(i, stacks[4+i]);
        }
    }

    public boolean winTest() {
        for (int i = 0; i < 4; i++) {
            if (stacks[4+i].currentCards.size()!=13){
                return false;
            }
        }

        return true;
    }

    public void dealCards() {
        prefs.saveCalculationAlternativeModeOld();
        gameLogic.showOrHideRecycles();

        //deal cards to foundation: search an ace for the first stack, a two for the second and so on
        for (int i = 0; i < 4; i++) {
            for (Card card : getMainStack().currentCards){
                if (card.getValue()==(i+1)){
                    moveToStack(card, stacks[4+i], OPTION_NO_RECORD);
                    stacks[4+i].getCard(0).flipUp();
                    break;
                }
            }
        }

        //card to trash
        moveToStack(getMainStack().getTopCard(), stacks[8], OPTION_NO_RECORD);
        stacks[8].getCard(0).flipUp();
    }

    @Override
    public void load() {
        //just use this method to set the texts, because it gets called after a game was loaded
        setTexts();
    }

    public int onMainStackTouch() {
        //if alternative mode is true, use the main stack like in klondike
        if (prefs.getSavedCalculationAlternativeModeOld()) {
            if (!getMainStack().isEmpty()) {
                moveToStack(getMainStack().getTopCard(), getDiscardStack());
                getDiscardStack().getTopCard().flipUp();
                return 1;
            } else {
                return 0;                                                                           //no moving cards back to main stack
                /*ArrayList<Card> cardsReversed = new ArrayList<>();
                for (int i = 0; i < getDiscardStack().currentCards.size(); i++) {
                    cardsReversed.add(getDiscardStack().currentCards.getHighScore(getDiscardStack().currentCards.size() - 1 - i));
                }

                moveToStack(cardsReversed, getMainStack(), OPTION_REVERSED_RECORD);
                return 2;*/
            }
        }

        if (getMainStack().getSize()==0 || getDiscardStack().getSize()==0){
            return 0;
        }

        //first move card from discard stack to tableau
        int stackID = 0;
        for (int i = 1; i < 4; i++) {
            if (stacks[i].getSize() < stacks[stackID].getSize()) {
                stackID = i;
            }
        }

        //then a new card to discard stack
        moveToStack(getDiscardStack().getTopCard(),stacks[stackID]);
        recordList.addToLastEntry(getMainStack().getTopCard(), getMainStack());
        moveToStack(getMainStack().getTopCard(),stacks[8], OPTION_NO_RECORD);

        return 1;
    }

    public boolean cardTest(Stack stack, Card card) {
        if (stack.getId() < 4) {
            return card.getStack() == getDiscardStack();
        } else if (stack.getId() <8 && stack.getTopCard().getValue()!=13) {
            int requestedDistance = stack.getId()-3;
            int stackCardValue = stack.getTopCard().getValue();
            int cardToMoveValue = card.getValue() < stackCardValue ? 13 + card.getValue() : card.getValue();

            if (cardToMoveValue - stackCardValue == requestedDistance){
                return true;
            }
        }

        return false;
    }

    public boolean addCardToMovementGameTest(Card card) {
        return card.getStackId()<4 && card.isTopCard() || card.getStack()==getDiscardStack();
    }

    @Override
    public void testAfterMove() {
        if (getDiscardStack().isEmpty() && !getMainStack().isEmpty()){
            recordList.addToLastEntry(getMainStack().getTopCard(), getMainStack());
            moveToStack(getMainStack().getTopCard(),stacks[8], OPTION_NO_RECORD);
        }

        setTexts();
    }

    @Override
    public void afterUndo() {
        setTexts();
    }

    public CardAndStack hintTest(ArrayList<Card> visited) {

        for (int j = 0; j < 4; j++) {
            if (stacks[j].isEmpty() || visited.contains(stacks[j].getTopCard())) {
                continue;
            }

            Card cardToTest = stacks[j].getTopCard();

            for (int i = 0; i < 4; i++) {
                if (cardTest(stacks[4+i],cardToTest)) {
                    return new CardAndStack(cardToTest, stacks[4+i]);
                }
            }
        }

        if (!getDiscardStack().isEmpty() && !visited.contains(getDiscardStack().getTopCard())){
            Card cardToTest = getDiscardStack().getTopCard();

            for (int i = 0; i < 4; i++) {
                if (cardTest(stacks[4+i],cardToTest)) {
                    return new CardAndStack(cardToTest, stacks[4+i]);
                }
            }
        }

        return null;
    }

    public Stack doubleTapTest(Card card) {

        for (int j = 0; j < 4; j++) {

            for (int i = 0; i < 4; i++) {
                if (cardTest(stacks[4+i],card)) {
                    return stacks[4+i];
                }
            }
        }

        if (card.getStack()==getDiscardStack()){
            //tableau stack with the fewest cards
            int stackID = 0;
            for (int i = 1; i < 4; i++) {
                if (stacks[i].getSize() < stacks[stackID].getSize()) {
                    stackID = i;
                }
            }

            return stacks[stackID];
        }

        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs, boolean isUndoMovement) {
        int destinationID = destinationIDs[0];

        //cards can't be moved away from the foundation, so don't need to check originID
        if (destinationID >=4 && destinationID <8) {                                                //anywhere to foundation
            return 50;
        }

        return 0;
    }

    private void setTexts(){

        for (int i = 0;i <4; i++) {
            if (stacks[4 + i].isEmpty()){
                continue;
            }

            int topCardValue = stacks[4 + i].getTopCard().getValue();
            int value;
            String text;

            if (topCardValue == 13) {
                value = -1;                                                                         //signalise that the stack is full
            } else {
                value = (topCardValue + i+1) % 13;                                                  //getHighScore the value of the next playable card
            }

            switch (value) {
                case 1:
                    text = "A";
                    break;
                case 11:
                    text = "J";
                    break;
                case 12:
                    text = "Q";
                    break;
                case 0:                                                                             //because it is mod 13
                    text = "K";
                    break;
                case -1:
                    text = " ";
                    break;
                default:
                    text = Integer.toString(value);
                    break;
            }

            textViewSetText(i, text);
        }
    }
}

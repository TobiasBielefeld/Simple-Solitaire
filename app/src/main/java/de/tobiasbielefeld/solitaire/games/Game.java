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

import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;


import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Abstract class for all the games. See the DUMMY GAME for detailed explanation of everything!
 */

public abstract class Game {

    final static protected boolean SAME_COLOR = false;
    final static protected boolean ALTERNATING_COLOR = true;

    final static protected int SAME_VALUE_AND_COLOR = 0;
    final static protected int SAME_VALUE_AND_FAMILY = 1;

    final static protected int LEFT = 1;
    final static protected int RIGHT = 2;

    public int[] cardDrawablesOrder = new int[]{1,2,3,4};
    public int[] directions;
    public int[] directionBorders;
    private boolean hasMainStack = false;
    private int dealFromID = -1;
    private int mainStackID = -1;
    private boolean hasDiscardStack = false;
    private boolean hasLimitedRedeals = false;
    private int discardStackID = -1;
    private int lastTableauID = -1;
    private int redealCounter=0;
    private int totalRedeals=0;
    private boolean hasArrow=false;

    /*
     *  stuff that a game MUST implement
     */

    abstract public void setStacks(RelativeLayout layoutGame, boolean isLandscape);

    abstract public boolean winTest();

    abstract public void dealCards();

    abstract public boolean cardTest(Stack stack, Card card);

    abstract public boolean addCardToMovementTest(Card card);

    abstract public CardAndStack hintTest();

    abstract public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs);

    abstract public void onMainStackTouch();

    protected boolean sameCardOnOtherStack(Card card, Stack otherStack, int mode){
        Stack origin = card.getStack();

        if (card.getIndexOnStack()>0 && origin.getCard(card.getIndexOnStack()-1).isUp() && otherStack.getSize()>0){
            Card cardBelow = origin.getCard(card.getIndexOnStack()-1);

            if (mode == SAME_VALUE_AND_COLOR) {
                if (cardBelow.getValue()==otherStack.getTopCard().getValue() && cardBelow.getColor()%2 ==otherStack.getTopCard().getColor()%2)
                    return true;
            } else if (mode == SAME_VALUE_AND_FAMILY){
                if (cardBelow.getValue()==otherStack.getTopCard().getValue() && cardBelow.getColor() ==otherStack.getTopCard().getColor())
                    return true;
            }
        }

        return false;
    }

    public CardAndStack doubleTap(Card card){
        CardAndStack cardAndStack = null;
        Stack destination = null;

        if (getSharedBoolean("pref_key_double_tap_all_cards",true) && card.getStack().getID()<=getLastTableauID() ){
            Stack stack = card.getStack();



            for (int i = stack.getFirstUpCardPos(); i < stack.getSize(); i++) {
                if (addCardToMovementTest(stack.getCard(i))) {
                    destination = doubleTapTest(stack.getCard(i));
                }

                if (destination!=null) {
                    cardAndStack = new CardAndStack(stack.getCard(i),destination);
                    break;
                }
            }
        } else {
            destination = doubleTapTest(card);

            if (destination!=null){
                cardAndStack = new CardAndStack(card,destination);
            }
        }

        return cardAndStack;
    }

    public Stack doubleTapTest(Card card){return null;}



    /*
     * auto complete stuff, can be used or not
     */

    public boolean autoCompleteStartTest(){
        return false;
    }

    public CardAndStack autoCompletePhaseOne(){
        return null;
    }

    public CardAndStack autoCompletePhaseTwo(){
        return null;
    }

    /*
     *  stuff that the games can override if necessary
     */

    public void reset(GameManager gm){
        if (hasLimitedRedeals) {
            redealCounter = 0;

            gm.updateNumberOfRedeals();
        }
    }

    public boolean testIfMainStackTouched(float X, float Y){
        return getMainStack().isOnLocation(X,Y);
    }

    protected void setCardDrawables(int p1, int p2, int p3, int p4){
        cardDrawablesOrder = new int[]{p1,p2,p3,p4};
    }

    public void testAfterMove(){}

    public void addOnTouchListener(View.OnTouchListener listener){
        if (hasMainStack()) {
            getMainStack().view.setOnTouchListener(listener);
        }
    }

    /*
     *  stuff that the games should use to set up other stuff
     */

    protected boolean testCardsUpToTop(Stack stack, int startPos, boolean mode) {
        /*
         * tests card from startPos to stack top if the cards are in the right order, returns true if so
         * set mode to true if the card color has to alternate, false otherwise
         */

        for (int i=startPos;i<stack.getSize()-1;i++){
            Card bottomCard = stack.getCard(i);
            Card upperCard = stack.getCard(i+1);

            if (mode == ALTERNATING_COLOR){  //alternating color
                if ((bottomCard.getColor()%2 == upperCard.getColor()%2) || (bottomCard.getValue() != upperCard.getValue()+1))
                    return false;
            } else {    //same color
                if ((bottomCard.getColor() != upperCard.getColor()) || (bottomCard.getValue() != upperCard.getValue()+1))
                    return false;
            }

        }

        return true;
    }

    protected void setLimitedRedeals(int number){
        if (number>=0) {
            hasLimitedRedeals = true;
            totalRedeals = number;
        } else {
            hasLimitedRedeals = false;
        }
    }

    protected void setUpCardWidth(RelativeLayout layoutGame, boolean isLandscape, int portraitValue, int landscapeValue){
        //use this to set the cards with according to last two values.
        //second last is for portrait mode, last one for landscape.
        //the game width will be divided by these values according to orientation to use as card widths.
        //Card height is 1.5*widht and the dimensions are applied to every card and stack

        Card.width = isLandscape ? layoutGame.getWidth() / (landscapeValue) : layoutGame.getWidth() / (portraitValue);
        Card.height = (int) (Card.width * 1.5);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Card.width, Card.height);
        for (Card card : cards) card.view.setLayoutParams(params);
        for (Stack stack : stacks) stack.view.setLayoutParams(params);
    }

    protected int setUpSpacing(RelativeLayout layoutGame, int cardWidth, int divider){
        return min(Card.width / 2,(layoutGame.getWidth() - cardWidth * Card.width) / (divider));
    }

    protected void setUpCardDimensions(RelativeLayout layoutGame, int portraitValue, int landscapeValue){

        int testWidth1, testHeight1, testWidth2, testHeight2;

        testWidth1 = layoutGame.getWidth() / portraitValue;
        testHeight1 = (int) (testWidth1 * 1.5);

        testHeight2 = layoutGame.getHeight() / landscapeValue;
        testWidth2 = (int) (testHeight2 / 1.5);

        if (testHeight1  < testHeight2) {
            Card.width = testWidth1;
            Card.height = testHeight1;
        } else {
            Card.width = testWidth2;
            Card.height = testHeight2;
        }


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Card.width, Card.height);
        for (Card card : cards) card.view.setLayoutParams(params);
        for (Stack stack : stacks) stack.view.setLayoutParams(params);
    }

    protected void setNumberOfDecks(int number){
        cards = new Card[52*number];
        gameLogic.randomCards = new Card[cards.length];
    }

    protected void setNumberOfStacks(int number){
        stacks = new Stack[number];
    }

    protected void setFirstMainStackID(int ID){
        hasMainStack=true;
        mainStackID = ID;
        dealFromID = ID;
    }

    protected void setFirstDiscardStackID(int ID){
        hasDiscardStack = true;
        discardStackID = ID;
    }

    protected void setDealFromID(int ID) {
        dealFromID = ID;
    }

    protected void setDirections(int[] directions){
        this.directions = directions;
    }

    protected void setDirectionBorders(int[] stackIDs) {
        directionBorders = stackIDs;
    }

    protected void setArrow(Stack stack, int direction){
        hasArrow=true;

        stack.setArrow(direction);

        if (direction == LEFT) {
            stack.view.setBackgroundResource(R.drawable.arrow_left);
        } else if (direction == RIGHT){
            stack.view.setBackgroundResource(R.drawable.arrow_right);
        }
    }

    /*
     * some getters, games should'nt override these
     */

    public Stack getMainStack(){
        if (mainStackID==-1)
            Log.e("Game.getMainStack()","No main stack specified");

        return stacks[mainStackID];
    }

    public int getLastTableauID(){
        if (lastTableauID==-1)
            Log.e("Game.getLastTableauID()","No last tableau stack specified");

        return lastTableauID;
    }

    protected void setLastTableauID(int ID){
        lastTableauID = ID;
    }

    public boolean hasMainStack(){
        return hasMainStack;
    }

    public Stack dealFromStack(){
        return stacks[dealFromID];
    }

    public boolean hasDiscardStack() {
        return hasDiscardStack;
    }

    public Stack getDiscardStack(){
        if (discardStackID==-1)
            Log.e("Game.getDiscardStack()","No discard stack specified");

        return stacks[discardStackID];
    }

    public boolean hasLimitedRedeals(){
        return hasLimitedRedeals;
    }

    public int getRemainingNumberOfRedeals(){
        return totalRedeals - redealCounter;
    }

    public void incrementRedealCounter(GameManager gm){
        redealCounter++;
        gm.updateNumberOfRedeals();
    }

    public void decrementRedealCounter(GameManager gm){
        redealCounter--;
        gm.updateNumberOfRedeals();
    }

    public void saveRedealCount(){
        putInt(GAME_REDEAL_COUNT,redealCounter);
    }

    public void loadRedealCount(GameManager gm){
        redealCounter = getInt(GAME_REDEAL_COUNT,totalRedeals);
        gm.updateNumberOfRedeals();
    }

    public boolean hasArrow(){
        return hasArrow;
    }

    public void save(){}

    public void load(){}

    public void toggleRedeals(){
        hasLimitedRedeals = !hasLimitedRedeals;
    }








}

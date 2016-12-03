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
 * Abstract class for all the games. See the DUMMY GAME for detailed explanation of everything!
 */

public abstract class Game {

    public int[] cardDrawablesOrder = new int[]{1,2,3,4};
    private boolean hasMainStack = false;
    private int dealFromID;
    private int mainStackID;
    private boolean hasDiscardStack = false;
    private int discardStackID;
    private int lastTableauID;
    public int[] directions;

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

    public boolean testIfMainStackTouched(float X, float Y){
        return currentGame.getMainStack().isOnLocation(X,Y);
    }

    protected void setCardDrawables(int p1, int p2, int p3, int p4){
        cardDrawablesOrder = new int[]{p1,p2,p3,p4};
    }

    public void testAfterMove(){}

    /*
     *  stuff that the games should use to set up other stuff
     */

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

    /*
     * some getters, games should'nt override these
     */

    public Stack getMainStack(){
        return stacks[mainStackID];
    }

    public int getLastTableauID(){
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
        return stacks[discardStackID];
    }
}

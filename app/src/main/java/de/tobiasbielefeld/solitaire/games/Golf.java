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
 * Golf Game! Very easy but a lot of dealt games can't be won.
 * It has 7 tableau stacks, one discard and one main stack
 * The discard stack is special: It's stapling direction is to the left and not to the bottom like
 * the tableau stacks
 */

public class Golf extends Game {

    public Golf(){
        setNumberOfDecks(1);
        setNumberOfStacks(9);
        setFirstMainStackID(8);
        setFirstDiscardStackID(7);
        setLastTableauID(6);
        setDirections(new int[]{1,1,1,1,1,1,1,3});
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape) {
        //initialize the dimensions
        setUpCardWidth(layoutGame,isLandscape,8,9);

        //order stacks on the screen
        int spacing = setUpSpacing(layoutGame,7,8);
        int startPos = layoutGame.getWidth()/2 - 3*spacing - (int)(3.5*Card.width);
        //main stack
        stacks[8].view.setX(layoutGame.getWidth() - startPos - Card.width);
        stacks[8].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1 );
        //discard stack
        stacks[7].view.setX(layoutGame.getWidth() - 2*startPos - 2*Card.width);
        stacks[7].view.setY(stacks[8].view.getY());
        //tableau stacks
        for (int i = 0; i < 7; i++) {
            stacks[i].view.setX(startPos + spacing * i + Card.width * i);
            stacks[i].view.setY(stacks[8].view.getY() + Card.height +  (isLandscape ? Card.width / 4 : Card.width / 2) + 1 );
        }
    }

    public boolean winTest(){
        //game is won if tableau is empty
        for (int i=0;i<=getLastTableauID();i++)
            if (!stacks[i].isEmpty())
                return false;

        return true;
    }

    public void dealCards(){

        for (int i=0;i<7;i++){
            for (int j=0;j<5;j++) {
                moveToStack(getMainStack().getTopCard(),stacks[i],OPTION_NO_RECORD);
                stacks[i].getTopCard().flipUp();
            }
        }

        moveToStack(getMainStack().getTopCard(),getDiscardStack(),OPTION_NO_RECORD);
    }

    public boolean cardTest(Stack stack, Card card) {
        /*
         * only allowed stack is the discard stack.
         * then check the settings: if cyclic moves are set to true, check if the cards are an ace and a king, if so return true
         * or the cards values difference is 1 or -1
         */
        return stack == getDiscardStack()
                && ((getSharedBoolean("pref_key_golf_cyclic", true)
                    && (card.getValue() == 13 && stack.getTopCard().getValue() == 1 || card.getValue() == 1 && stack.getTopCard().getValue() == 13))
                || (card.getValue() == stack.getTopCard().getValue() + 1 || card.getValue() == stack.getTopCard().getValue() - 1));
    }

    public boolean addCardToMovementTest(Card card){
        return card.getStack().getID()<7 && card.isTopCard();
    }

    public CardAndStack hintTest(){
        for (int i=0;i<7;i++){
            if (stacks[i].isEmpty())
                continue;

            if (!hint.hasVisited(stacks[i].getTopCard()) && stacks[i].getTopCard().test(getDiscardStack()))
                return new CardAndStack(stacks[i].getTopCard(),getDiscardStack());
        }

        return null;
    }

    @Override
    public Stack doubleTapTest(Card card) {
        return card.test(getDiscardStack()) ? getDiscardStack() : null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs){
        if (destinationIDs[0]==getDiscardStack().getID() && originIDs[0]<7)
            return 50;
        else
            return 0;
    }

    public void onMainStackTouch() {
        if (getMainStack().getSize()>0)
        moveToStack(getMainStack().getTopCard(),getDiscardStack());
    }
}

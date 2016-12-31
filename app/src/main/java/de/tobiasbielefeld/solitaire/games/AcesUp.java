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

public class AcesUp extends Game {

    public  AcesUp() {
        setNumberOfDecks(1);
        setNumberOfStacks(6);
        setFirstMainStackID(5);
        setFirstDiscardStackID(4);
        setLastTableauID(3);
        setDirections(new int[]{1,1,1,1,0,0});
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape) {

        setUpCardWidth(layoutGame,isLandscape,7+1,7+2);

        int spacing = setUpSpacing(layoutGame,7,8);

        int startPos = (int)(layoutGame.getWidth()/2 - 3.5*Card.width - 2.5*spacing);

        stacks[4].view.setX(startPos);
        stacks[4].view.setY((isLandscape ? Card.height / 4 : Card.height / 2) + 1 );

        for (int i = 0; i < 4; i++) {
            stacks[i].view.setX(stacks[4].view.getX() + spacing + Card.width*3/2 + i* (spacing+ Card.width) );
            stacks[i].view.setY(stacks[4].view.getY());
        }

        stacks[5].view.setX(stacks[3].view.getX() + Card.width +  Card.width/2+ spacing);
        stacks[5].view.setY(stacks[4].view.getY());
    }


    public boolean winTest(){
        if (!getMainStack().isEmpty())
            return false;

        for (int i=0;i<4;i++){
            if (stacks[i].getSize()!=1 || stacks[i].getTopCard().getValue()!=1)
                return false;
        }

        return true;
    }

    public void dealCards(){

        for (int i=0;i<4;i++) {
            if (!getMainStack().isEmpty()){
                moveToStack(getMainStack().getTopCard(),stacks[i],OPTION_NO_RECORD);
                stacks[i].getTopCard().flipUp();
            }
        }
    }

    public void onMainStackTouch(){

        if (!getMainStack().isEmpty()){
            ArrayList<Card> cards = new ArrayList<>();
            ArrayList<Stack> destinations = new ArrayList<>();

            for (int i=0;i<4;i++) {
                getMainStack().getCardFromTop(i).flipUp();
                cards.add(getMainStack().getCardFromTop(i));
                destinations.add(stacks[i]);
            }

            moveToStack(cards,destinations,OPTION_REVERSED_RECORD);
        }
    }

    public boolean cardTest(Stack stack, Card card){
        if (stack.getID()<4 && stack.isEmpty())
            return true;
        else if (stack.getID()==getMainStack().getID() || card.getValue()==1)
            return false;
        else if (stack.getID()==getDiscardStack().getID()){
            for (int i=0;i<4;i++){
                if (stacks[i].isEmpty() || i==card.getStack().getID())
                    continue;

                Card cardOnStack = stacks[i].getTopCard();

                if (cardOnStack.getColor()==card.getColor() && (cardOnStack.getValue()>card.getValue() || cardOnStack.getValue()==1))
                    return true;
            }
        }

        return false;
    }

    public boolean addCardToMovementTest(Card card){
        return card.isTopCard();
    }

    public CardAndStack hintTest(){

        for (int j=0;j<4;j++) {
            if (stacks[j].isEmpty() || hint.hasVisited(stacks[j].getTopCard()) || (stacks[j].getSize()==1 && stacks[j].getTopCard().getValue()==1) )
                continue;

            Card cardToTest = stacks[j].getTopCard();
            boolean success = false;

            if (cardToTest.getValue()==1){
                for (int i = 0; i < 4; i++) {
                    if (i==j || !stacks[i].isEmpty())
                        continue;

                    return new CardAndStack(cardToTest,stacks[i]);
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    if (stacks[i].isEmpty() || i == j)
                        continue;

                    Card cardOnStack = stacks[i].getTopCard();

                    if (cardOnStack.getColor() == cardToTest.getColor() && (cardOnStack.getValue() > cardToTest.getValue() || cardOnStack.getValue() == 1))
                        success = true;
                }

                if (success)
                    return new CardAndStack(cardToTest, getDiscardStack());
            }
        }


        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs){
        if (destinationIDs[0]== getDiscardStack().getID())
            return 50;

        return 0;
    }
}

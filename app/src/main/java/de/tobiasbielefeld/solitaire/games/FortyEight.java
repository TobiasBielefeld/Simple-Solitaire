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

public class FortyEight extends Game {

    public  FortyEight() {

        setNumberOfDecks(2);
        setNumberOfStacks(18);
        setFirstMainStackID(17);
        setFirstDiscardStackID(16);
        setLastTableauID(7);

        setLimitedRedeals(1);

        setDirections(new int[]{1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,3,0});
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape) {

        setUpCardWidth(layoutGame,isLandscape,8+1,8+4);

        int spacing = setUpSpacing(layoutGame,8,9);
        int startPos = (int) (layoutGame.getWidth()/2 - 4*Card.width - 3.5*spacing);

        stacks[17].view.setX((int)(layoutGame.getWidth()/2 + 3*Card.width + 3.5*spacing));
        stacks[17].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1 );
        stacks[17].view.setBackgroundResource(R.drawable.background_stack_stock);

        stacks[16].view.setX(stacks[17].view.getX() - spacing - Card.width);
        stacks[16].view.setY(stacks[17].view.getY());

        for (int i = 0; i < 8; i++) {
            stacks[8+i].view.setX(startPos + i* (spacing + Card.width) );
            stacks[8+i].view.setY(stacks[17].view.getY() + Card.height + (isLandscape ? Card.width / 4 : Card.width / 2) );
            stacks[8+i].view.setBackgroundResource(R.drawable.background_stack_ace);
        }

        for (int i = 0; i < 8; i++) {
            stacks[i].view.setX(startPos + i* (spacing + Card.width) );
            stacks[i].view.setY(stacks[8].view.getY() + Card.height + (isLandscape ? Card.width / 4 : Card.width / 2) );
        }

    }

    public boolean winTest(){
        for (int i=0;i<8;i++)
            if (stacks[8+i].getSize()!=13)
                return false;

        return true;
    }

    public void dealCards(){
        //putSharedString("pref_key_fortyeight_redeals_old",getSharedString("pref_key_fortyeight_redeals","5"));

        for (int i=0;i<8;i++){
            for (int j=0;j<4;j++){
                moveToStack(dealFromStack().getTopCard(), stacks[i], OPTION_NO_RECORD);
                stacks[i].getTopCard().flipUp();
            }
        }

        moveToStack(dealFromStack().getTopCard(), getDiscardStack(), OPTION_NO_RECORD);
        getDiscardStack().getTopCard().flipUp();
    }


    public void onMainStackTouch(){

        if (!getMainStack().isEmpty()) {
            moveToStack(getMainStack().getTopCard(), getDiscardStack());

        }
        else if (getDiscardStack().getSize() != 0){
            recordList.add(getDiscardStack().currentCards);

            while (getDiscardStack().getSize() > 0)
                moveToStack(getDiscardStack().getTopCard(), getMainStack(), OPTION_NO_RECORD);

            scores.update(-200);    //because of no record, it isnt updated automatically
        }
    }


    public boolean cardTest(Stack stack, Card card){
         if (stack.getID() < 8) {
            return stack.isEmpty() || (stack.getTopCard().getColor() == card.getColor())
                    && (stack.getTopCard().getValue() == card.getValue() + 1);

        }
        else if (stack.getID() < 16 && movingCards.hasSingleCard()) {
            if (stack.isEmpty())
                return card.getValue() == 1;
            else
                return (stack.getTopCard().getColor() == card.getColor())
                        && (stack.getTopCard().getValue() == card.getValue() - 1);
        } else
            return false;
    }


    public boolean addCardToMovementTest(Card card){
        return card.isTopCard();
    }

    public CardAndStack hintTest(){
        for (int i=0;i<8;i++){
            if (stacks[i].isEmpty() || hint.hasVisited(stacks[i].getTopCard()))
                continue;

            Card cardToTest = stacks[i].getTopCard();

            for (int j=0;j<8;j++){
                if (i==j)
                    continue;

                if (stacks[j].isEmpty() &&  cardToTest.getValue()==13)
                    return new CardAndStack(cardToTest,stacks[j]);
                else if (cardTest(stacks[j],cardToTest) && cardToTest.getValue()!=1)
                    return new CardAndStack(cardToTest,stacks[j]);
            }

            for (int j=0;j<8;j++){
                if (cardTest(stacks[8+j],cardToTest))
                    return new CardAndStack(cardToTest,stacks[8+j]);
            }
        }

        if (!getDiscardStack().isEmpty() && !hint.hasVisited(getDiscardStack().getTopCard())){
            Card cardToTest = getDiscardStack().getTopCard();

            for (int j=0;j<8;j++){
                if (stacks[j].isEmpty() &&  cardToTest.getValue()==13)
                    return new CardAndStack(cardToTest,stacks[j]);
                else if (cardTest(stacks[j],cardToTest) && cardToTest.getValue()!=1)
                    return new CardAndStack(cardToTest,stacks[j]);
            }

            for (int j=0;j<8;j++){
                if (cardTest(stacks[8+j],cardToTest))
                    return new CardAndStack(cardToTest,stacks[8+j]);
            }
        }


        return null;
    }

    public boolean autoCompleteStartTest() {
        for (int i=0;i<8;i++){
            Stack stack = stacks[i];

            if (stack.isEmpty() || !stack.getCard(0).isUp() || !testCardsUpToTop(stack,0,SAME_COLOR))
                return false;
        }

       return getMainStack().isEmpty() && getDiscardStack().isEmpty();
    }

    public CardAndStack autoCompletePhaseTwo() {
        for (int i=0;i<8;i++){
            if (stacks[i].isEmpty())
                continue;

            Card cardToTest = stacks[i].getTopCard();

            for (int j=0;j<8;j++){
                if (cardTest(stacks[8+j],cardToTest))
                    return new CardAndStack(cardToTest,stacks[8+j]);
            }
        }

        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs){

        //anywhere to foundation
        if (destinationIDs[0]>=8 && destinationIDs[0]<16)
            return 45;
        //foundation to tableau
        if (originIDs[0]>=8 && originIDs[0]<16 && destinationIDs[0]<8)
            return -60;
        //discard to tableau
        if (originIDs[0]==getDiscardStack().getID() && destinationIDs[0]<8)
            return 60;
        //redeal cards from discard to main stack
        if (originIDs[0]==getDiscardStack().getID() && destinationIDs[0]==getMainStack().getID() && originIDs.length>0)
            return -200;

        return 0;
    }
}

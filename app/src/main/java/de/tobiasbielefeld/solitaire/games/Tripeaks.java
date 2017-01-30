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

/*
 *  Tripeaks is nearly the same as Golf, but with a different field layout
 */

public class Tripeaks extends Game {

    //contains which stack is above another stack. So stackAboveID[0]=3 means, that above stack
    //with index 0 are the stacks with index 3 and 3+1
    int[] stackAboveID = new int[]{3,5,7,9,10,12,13,15,16,18,19,20,21,22,23,24,25,26};//28

    public Tripeaks() {

        setNumberOfDecks(1);
        setNumberOfStacks(30);

        setLastTableauID(27);
        setFirstDiscardStackID(28);
        setFirstMainStackID(29);
        setDirections(new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape) {

        setUpCardDimensions(layoutGame,11,6);

        int spacing = setUpSpacing(layoutGame,10,11);

        int startPosX = (int) (layoutGame.getWidth()/2 - 3.5*Card.width - 3*spacing);
        int startPosY = (int) ((layoutGame.getHeight()- Card.height *4.25  - (isLandscape ? Card.height / 4 : Card.height / 2))/2 );

        for (int i=0;i<28;i++) {

            if (i==3){
                startPosX = (int) (layoutGame.getWidth()/2 - 4*Card.width - 3.5*spacing);
                startPosY = (int) ((layoutGame.getHeight()- Card.height *4.25  - (isLandscape ? Card.height / 4 : Card.height / 2))/2 + 0.75* Card.height);
            } else if (i==9){
                startPosX = (int) (layoutGame.getWidth()/2 - 4.5*Card.width - 4*spacing);
                startPosY = (int) ((layoutGame.getHeight()- Card.height *4.25  - (isLandscape ? Card.height / 4 : Card.height / 2))/2 + 1.5* Card.height);
            } else if (i==18){
                startPosX = (int) (layoutGame.getWidth()/2 - 5*Card.width - 4.5*spacing);
                startPosY = (int) ((layoutGame.getHeight()- Card.height *4.25  - (isLandscape ? Card.height / 4 : Card.height / 2))/2 + 2.25* Card.height);
            }

            if (i>3 && i< 9 && (i-1)%2==0)
                startPosX += Card.width + spacing;

            stacks[i].view.setX(startPosX);
            stacks[i].view.setY(startPosY);
            stacks[i].view.setBackgroundResource(R.drawable.background_stack_transparent);


            if (i<3)
                startPosX += 3*Card.width + 3*spacing;
            else
                startPosX+= Card.width + spacing;
        }

        stacks[28].view.setX(layoutGame.getWidth() / 2 -Card.width - spacing);
        stacks[28].view.setY(stacks[18].view.getY() + Card.height + (isLandscape ? Card.height / 4 : Card.height / 2));

        stacks[29].view.setX(stacks[28].view.getX() + 2*spacing + Card.width);
        stacks[29].view.setY(stacks[28].view.getY());

    }

    public boolean winTest(){
        for (int i=0;i<=getLastTableauID();i++){
            if (!stacks[i].isEmpty())
                return false;
        }

        return true;
    }

    public void dealCards(){
        for (int i = 0; i<28 ; i++) {
            moveToStack(dealFromStack().getTopCard(), stacks[i], OPTION_NO_RECORD);

            if (i>17)
                stacks[i].getTopCard().flipUp();
        }

        moveToStack(dealFromStack().getTopCard(),getDiscardStack(),OPTION_NO_RECORD);
    }


    public void onMainStackTouch(){

        if (getMainStack().getSize() > 0) {
            moveToStack(getMainStack().getTopCard(), getDiscardStack());
        } /*else if (!getDiscardStack().isEmpty()){
            recordList.add(getDiscardStack().currentCards);

            while (getDiscardStack().getSize() > 0)
                moveToStack(getDiscardStack().getTopCard(), getMainStack(), OPTION_NO_RECORD);

            scores.update(-200);    //because of no record, it isnt updated automatically
        }//*/
    }


    public boolean cardTest(Stack stack, Card card){
        return stack == getDiscardStack() &&
                (card.getValue() == 13 && stack.getTopCard().getValue() == 1
                        || card.getValue() == 1 && stack.getTopCard().getValue() == 13
                        || (card.getValue() == stack.getTopCard().getValue() + 1
                        || card.getValue() == stack.getTopCard().getValue() - 1));
    }


    public boolean addCardToMovementTest(Card card){

        return card.getStack().getID() != getDiscardStack().getID();
    }

    public CardAndStack hintTest(){
        for (int i=0;i<28;i++){
            if (stacks[i].isEmpty() || !stacks[i].getTopCard().isUp())
                continue;

            if (!hint.hasVisited(stacks[i].getTopCard()) && stacks[i].getTopCard().test(getDiscardStack()))
                return new CardAndStack(stacks[i].getTopCard(),getDiscardStack());
        }

        return null;
    }

    @Override
    public Stack doubleTapTest(Card card) {

        if (card.test(getDiscardStack()))
                return getDiscardStack();

        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs){
        int points = 0;

        for (int i=0;i<originIDs.length;i++)
            if (originIDs[i] == destinationIDs[i])
                points+=25;

        if (originIDs[0]<28 && destinationIDs[0]==28)
            points+=50;
        //else if (originIDs[0]==getDiscardStack().getID() && destinationIDs[0]==getMainStack().getID())
        //    points-=200;

        return points;
    }

    public void testAfterMove(){
        for (int i=0;i<18;i++){
            if (!stacks[i].isEmpty() && !stacks[i].getTopCard().isUp() && stackIsFree(stacks[i])){
                stacks[i].getTopCard().flipWithAnim();
            }
        }
    }

    private boolean stackIsFree(Stack stack){
        if (stack.getID()>17)
            return true;

        Stack stackAbove1 = stacks[stackAboveID[stack.getID()]];
        Stack stackAbove2 = stacks[stackAboveID[stack.getID()]+1];

        return stackAbove1.isEmpty() && stackAbove2.isEmpty();
    }

}

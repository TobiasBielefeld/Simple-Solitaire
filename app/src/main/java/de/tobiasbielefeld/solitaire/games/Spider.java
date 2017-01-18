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
 * Spider Solitaire! A bit special game, because it has 2 card decks, the card families depend
 * on the chosen difficulty. The game has 10 tableau stacks, 8 foundation stacks and 4 main stacks
 */

public class Spider extends Game {

    public Spider() {
        setNumberOfDecks(2);
        setNumberOfStacks(23);
        setFirstMainStackID(18);
        setLastTableauID(9);
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape) {
        //initialize the dimensions
        setUpCardWidth(layoutGame,isLandscape,11,12);
        int spacing = setUpSpacing(layoutGame,10,11);
        int startPos = layoutGame.getWidth()-Card.width - 5*Card.width/2;
        //main stacks
        for (int i=0;i<5;i++) {
            stacks[18+i].view.setX(startPos + i*Card.width/2);
            stacks[18+i].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1 );
            stacks[18+i].view.setBackgroundResource(R.drawable.background_stack_transparent);
        }
        //foundation stacks
        for (int i=0;i<8;i++) {
            stacks[10+i].view.setX(Card.width/2+i*Card.width/2);
            stacks[10+i].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1 );
            stacks[10+i].view.setBackgroundResource(R.drawable.background_stack_transparent);
        }
        //tableau stacks
        startPos = layoutGame.getWidth() / 2 - 5 * Card.width - 4 * spacing - spacing/2;
        for (int i = 0; i < 10; i++) {
            stacks[i].view.setX(startPos + spacing * i + Card.width * i);
            stacks[i].view.setY(stacks[18].view.getY() + Card.height +  (isLandscape ? Card.width / 4 : Card.width / 2) + 1 );
        }
        //set card families depending on settings
        loadCards();
    }

    public boolean winTest(){
        //if every foundation stacks is full, game is won
        for (int i=0;i<8;i++)
            if (stacks[10+i].getSize()!=13)
                return false;

        return true;
    }

    public void dealCards(){
        //when starting a new game, load the difficulty preference in the "old" preference
        putSharedString("pref_key_spider_difficulty_old",getSharedString("pref_key_spider_difficulty","1"));
        loadCards();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 5; j++) {
                moveToStack(getMainStack().getTopCard(), stacks[i], OPTION_NO_RECORD);
            }

            if (i<4) {
                moveToStack(getMainStack().getTopCard(), stacks[i], OPTION_NO_RECORD);
            }

            stacks[i].getTopCard().flipUp();
        }

        for (int i=0;i<5;i++) {
            for (int j = 0; j < 10; j++) {
                moveToStack(getMainStack().getTopCard(), stacks[18+i], OPTION_NO_RECORD);
            }
        }

        for (int i=0;i<5;i++) {
            for (int j = 0; j < 10; j++) {
                stacks[18+i].getCard(i).view.bringToFront();
            }
        }
    }

    public void onMainStackTouch(){
        /*
         * first get the current main stack, then deal the cards from it to the tableau.
         * with the reversed record option
         */
        int currentMainStackID=22;

        while(stacks[currentMainStackID].isEmpty())
            currentMainStackID--;

        //ID below 18 means all main stacks are empty
        if (stacks[currentMainStackID].getID()>=18) {

            ArrayList<Card> cards = new ArrayList<>();
            ArrayList<Stack> destinations = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                cards.add(stacks[currentMainStackID].getCardFromTop(i));
                stacks[currentMainStackID].getCardFromTop(i).flipUp();
                destinations.add(stacks[i]);
            }

            moveToStack(cards,destinations,OPTION_REVERSED_RECORD);
            //test if a card family is now full
            testAfterMoveHandler.sendEmptyMessageDelayed(0, 100);
        }
    }

    public boolean cardTest(Stack stack, Card card) {
        //can always place a card on an empty field, or the value of the card on the other stack is +1
        if (stack.getID()<10) {
            if (stack.isEmpty() || (stack.getSize()>0 && stack.getTopCard().getValue() == card.getValue()+1))
                return true;
        }

        return false;
    }

    public boolean addCardToMovementTest(Card card) {
        //do not accept cards from foundation and test if the cards are in the right order.
        return card.getStack().getID() < 10 && testCardsUpToTop(card.getStack(), card.getIndexOnStack(),SAME_COLOR);
    }

    public CardAndStack hintTest(){
        for (int i=0;i<10;i++){
            Stack sourceStack = stacks[i];

            if (sourceStack.isEmpty())
                continue;

            for (int j=sourceStack.getFirstUpCardPos();j<sourceStack.getSize();j++){
                Card cardToMove = sourceStack.getCard(j);

                if (hint.hasVisited(cardToMove) || !testCardsUpToTop(sourceStack,j,SAME_COLOR))
                    continue;

                for (int k=0;k<10;k++){
                    Stack destStack = stacks[k];
                    if (i==k || destStack.isEmpty())
                        continue;

                    if (cardToMove.test(destStack)) {
                        //if the card above has the corret value, and the card on destination is not the same family as the cardToMove, don't move it
                        if (j>0 && sourceStack.getCard(j-1).isUp()  && sourceStack.getCard(j-1).getValue()==cardToMove.getValue()+1
                                && destStack.getTopCard().getColor()!=cardToMove.getColor())
                            continue;
                        //if the card is already on the same card as on the other stack, don't return it
                        if (destStack.getSize()>0 && j>0 && sourceStack.getCard(j-1).isUp()
                                && sourceStack.getCard(j-1).getValue()==destStack.getTopCard().getValue()
                                && sourceStack.getCard(j-1).getColor()==destStack.getTopCard().getColor())
                            continue;

                        return new CardAndStack(cardToMove, destStack);
                    }
                }
            }
        }

        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs){
        int points = 0;
        boolean foundation = false;

        for (int i=0;i<originIDs.length;i++){
            if (originIDs[i] == destinationIDs[i])
                points+=25;

            if (!foundation && destinationIDs[i] >= 10 && destinationIDs[i]<18) {
                points += 200;
                foundation = true;
            }
        }

        return points;
    }

    @Override
    public boolean testIfMainStackTouched(float X, float Y){
        return (stacks[18].isOnLocation(X,Y) ||
                stacks[19].isOnLocation(X,Y) ||
                stacks[20].isOnLocation(X,Y) ||
                stacks[21].isOnLocation(X,Y) ||
                stacks[22].isOnLocation(X,Y));
    }

    @Override
    public void testAfterMove(){
        /*
         * after a move, test if somewhere is a complete card family, if so, move it to foundations
         */
        for (int i=0;i<10;i++){
            Stack currentStack = stacks[i];

            if (currentStack.isEmpty() || currentStack.getTopCard().getValue()!=1)
                continue;

            for (int j=currentStack.getFirstUpCardPos();j<currentStack.getSize();j++){
                if (j==-1)
                    break;

                Card cardToTest = currentStack.getCard(j);

                if (cardToTest.getValue()==13 && testCardsUpToTop(currentStack,j,SAME_COLOR)){
                    Stack foundationStack = stacks[10];

                    while (!foundationStack.isEmpty())
                        foundationStack = stacks[foundationStack.getID()+1];

                    ArrayList<Card> cards = new ArrayList<>();
                    ArrayList<Stack> origins = new ArrayList<>();

                    for (int k=j;k<currentStack.getSize();k++){
                        cards.add(currentStack.getCard(k));
                        origins.add(currentStack);
                    }

                    recordList.addAtEndOfLastEntry(cards,origins);
                    moveToStack(cards,foundationStack,OPTION_NO_RECORD);
                    scores.update(200);

                    //turn the card below up, if there is one
                    if (!currentStack.isEmpty() && !currentStack.getTopCard().isUp()){
                        currentStack.getTopCard().flipWithAnim();
                    }

                    testIfWonHandler.sendEmptyMessageDelayed(0,200);
                    break;
                }
            }
        }
    }

    private void loadCards(){
        /*
         * load the card families depending on the preference
         */
        switch(getSharedString("pref_key_spider_difficulty_old","1")){
            case "1":
                setCardDrawables(3,3,3,3);
                break;
            case "2":
                setCardDrawables(2,3,2,3);
                break;
            case "4":
                setCardDrawables(1,2,3,4);
                break;
        }

        //and update the cards!
        for (Card card : cards) {
            card.setColor();
        }

        Card.updateCardDrawableChoice();
    }

    public boolean autoCompleteStartTest() {
        for (int i=0;i<4;i++)
            if (!stacks[18+i].isEmpty())
               return false;

        for (int i = 0; i < 10; i++)
            if (stacks[i].getSize()>0 && (stacks[i].getFirstUpCardPos()!=0 || !testCardsUpToTop(stacks[i],0,SAME_COLOR)))
                return false;

        return true;
    }

    public CardAndStack autoCompletePhaseOne() {

        for (int i = 0; i < 10; i++) {
            Stack sourceStack = stacks[i];

            if (sourceStack.isEmpty())
                continue;

            Card cardToMove = sourceStack.getCard(0);

            for (int k = 0; k < 10; k++) {
                Stack destStack = stacks[k];
                if (i == k || destStack.isEmpty() || destStack.getTopCard().getColor()!=cardToMove.getColor())
                    continue;

                if (cardToMove.test(destStack)) {
                    return new CardAndStack(cardToMove, destStack);
                }
            }
        }

        return null;
    }
}

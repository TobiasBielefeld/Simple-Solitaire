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
 * Simple Simon Game! It's nearly like Spider, but with less cards and all cards are
 * already faced up at start
 */

public class SimpleSimon extends Game {

    public SimpleSimon(){
        setNumberOfDecks(1);
        setNumberOfStacks(14);
        setDealFromID(0);
        setLastTableauID(9);
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape) {
        //initialize the dimensions
        Card.width = isLandscape? layoutGame.getWidth() / 12 : layoutGame.getWidth() / 11;
        Card.height = (int) (Card.width * 1.5);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Card.width, Card.height);

        //apply dimensions to cards and stacks
        for (Card card : cards) card.view.setLayoutParams(params);
        for (Stack stack : stacks) stack.view.setLayoutParams(params);
        //order stacks on the screen
        int spacing = min((layoutGame.getWidth() - 10*Card.width)/11, Card.width/2);
        int startPos = layoutGame.getWidth()/2 - 2*Card.width - (int)(1.5*spacing);
        //foundation stacks
        for (int i=0;i<4;i++) {
            stacks[10+i].view.setX(startPos + spacing * i + Card.width * i);
            stacks[10+i].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1 );
        }
        //tableau stacks
        startPos = layoutGame.getWidth() / 2 - 5 * Card.width - 4 * spacing - spacing/2;
        for (int i = 0; i < 10; i++) {
            stacks[i].view.setX(startPos + spacing * i + Card.width * i);
            stacks[i].view.setY(stacks[10].view.getY() + Card.height +  (isLandscape ? Card.width / 4 : Card.width / 2) + 1 );
        }
    }

    public boolean winTest(){
        return (stacks[10].getSize()==13 && stacks[11].getSize()==13 && stacks[12].getSize()==13 && stacks[13].getSize()==13);
    }

    public void dealCards(){

        for (Card card : cards)
            card.flipUp();

        for (int i=0;i<3;i++){
            for (int j=0;j<8;j++){
                moveToStack(dealFromStack().getTopCard(),stacks[7+i],OPTION_NO_RECORD);
            }
        }

        for (int i=0;i<7;i++){
            for (int j=0;j<1+i;j++){
                moveToStack(dealFromStack().getTopCard(),stacks[i],OPTION_NO_RECORD);
            }
        }

        for (Card card : cards)
            card.flipUp();
    }

    public boolean cardTest(Stack stack, Card card){
        if (stack.getID()<10) {
            if (stack.isEmpty() || stack.getTopCard().getValue() == card.getValue()+1)
                return true;
        }

        return false;
    }

    public boolean addCardToMovementTest(Card card){
        return card.getStack().getID() < 10 && testCardsUpToTop(card.getStack(), card.getIndexOnStack());
    }

    public CardAndStack hintTest(){
        for (int i=0;i<10;i++){
            Stack sourceStack = stacks[i];

            if (sourceStack.isEmpty())
                continue;

            for (int j=sourceStack.getFirstUpCardPos();j<sourceStack.getSize();j++){
                Card cardToMove = sourceStack.getCard(j);

                if (hint.hasVisited(cardToMove) || !testCardsUpToTop(sourceStack,j))
                    continue;

                //if (cardToMove.getValue()==13 && cardToMove.isFirstCard())
                //    continue;

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

                        return new CardAndStack(cardToMove,destStack);
                    }
                }
            }
        }

        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs){
        if (destinationIDs[0] >= 10 && destinationIDs[0]<14)
            return 200;
        else
            return 0;
    }

    public void onMainStackTouch() {
        //no main stack so empty
    }

    private boolean testCardsUpToTop(Stack stack, int startPos) {
        /*
         * tests card from startPos to stack top if the cards are in the right order
         */
        for (int i=startPos;i<stack.getSize()-1;i++){
            Card bottomCard = stack.getCard(i);
            Card upperCard = stack.getCard(i+1);

            if ((bottomCard.getColor() != upperCard.getColor()) || (bottomCard.getValue() != upperCard.getValue()+1))
                return false;
        }

        return true;
    }

    @Override
    public void testAfterMove(){
        /*
         * after a move, test if somewhere is a complete card family, if so, move it to foundations
         */
        for (int i=0;i<10;i++){
            Stack currentStack = stacks[i];

            if (currentStack.isEmpty())
                continue;

            for (int j=currentStack.getFirstUpCardPos();j<currentStack.getSize();j++){
                Card cardToTest = currentStack.getCard(j);

                if (cardToTest.getValue()==13 && currentStack.getTopCard().getValue()==1 && testCardsUpToTop(currentStack,j)){
                    Stack foundationStack = stacks[10];

                    while (!foundationStack.isEmpty())
                        foundationStack = stacks[foundationStack.getID()+1];

                    ArrayList<Card> cards = new ArrayList<>();

                    for (int k=j;k<currentStack.getSize();k++){
                        cards.add(currentStack.getCard(k));
                    }

                    moveToStack(cards,stacks[foundationStack.getID()]);
                    break;
                }
            }
        }
    }

}

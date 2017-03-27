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

import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.Stack;

import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.classes.Stack.ArrowDirection.LEFT;

/**
 * Pyramid Solitaire! It has a lot of stacks.
 */

public class Pyramid extends Game {

    int[] stackAboveID = new int[28];

    ArrayList<Card> cardsToMove = new ArrayList<>();
    ArrayList<Stack> origins = new ArrayList<>();

    public Pyramid() {
        setNumberOfDecks(1);
        setNumberOfStacks(32);
        setFirstMainStackID(31);
        setFirstDiscardStackID(29);
        setLastTableauID(27);
        setDealFromID(30);
        setDirections();                                                                              //empty so all stacks have no spacing direction
        ignoreEmptyTableauStacks();

        setLimitedRedeals(2);

        if (!getSharedBoolean(PREF_KEY_PYRAMID_LIMITED_REDEALS, DEFAULT_PYRAMID_LIMITED_REDEALS))
            toggleRedeals();
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape) {

        setUpCardDimensions(layoutGame, 7 + 1, 5 + 1);

        int spacing = setUpSpacing(layoutGame, 7, 8);

        int index = 0;
        for (int i = 0; i < 7; i++) {

            int startPosX = layoutGame.getWidth() / 2 - (i + 1) * Card.width / 2 - i * spacing / 2;
            int startPosY = (isLandscape ? Card.width / 4 : Card.width / 2) + i * Card.height / 2;

            for (int j = 0; j < i + 1; j++) {

                stackAboveID[index] = ((i + 1) * (i + 2)) / 2 + j;

                stacks[index].view.setX(startPosX + j * (spacing + Card.width));
                stacks[index].view.setY(startPosY);
                stacks[index].view.setImageBitmap(Stack.backgroundTransparent);

                index++;
            }
        }

        stacks[28].view.setX(stacks[21].view.getX() + Card.width / 2 + spacing / 2);
        stacks[28].view.setY(stacks[21].view.getY() + Card.height + (isLandscape ? Card.width / 4 : Card.width / 2));

        stacks[29].view.setX(stacks[24].view.getX() + Card.width / 2);
        stacks[29].view.setY(stacks[28].view.getY());

        stacks[31].view.setX(stacks[29].view.getX() + Card.width + spacing);
        stacks[31].view.setY(stacks[28].view.getY());
        setArrow(stacks[31], LEFT);

        stacks[30].view.setX(stacks[31].view.getX() + Card.width + spacing);
        stacks[30].view.setY(stacks[28].view.getY());
    }

    public boolean winTest() {
        for (int i = 0; i <= getLastTableauID(); i++)
            if (!stacks[i].isEmpty())
                return false;

        return sharedStringEquals(PREF_KEY_PYRAMID_DIFFICULTY, DEFAULT_PYRAMID_DIFFICULTY) || getDiscardStack().isEmpty() && stacks[30].isEmpty();
    }

    public void dealCards() {
        for (Card card : cards) {
            card.flipUp();
        }

        for (int i = 0; i < 28; i++) {
            moveToStack(getDealStack().getTopCard(), stacks[i], OPTION_NO_RECORD);
        }

        moveToStack(getDealStack().getTopCard(), getDiscardStack(), OPTION_NO_RECORD);
    }

    public boolean testIfMainStackTouched(float X, float Y) {
        return (getDealStack().isEmpty() && getDealStack().isOnLocation(X, Y)) || getMainStack().isOnLocation(X, Y);
    }

    public void onMainStackTouch() {

        if (!getDealStack().isEmpty()) {
            moveToStack(getDealStack().getTopCard(), getDiscardStack());
        } else if (!getDiscardStack().isEmpty()) {
            recordList.add(getDiscardStack().currentCards);

            while (getDiscardStack().getSize() > 0)
                moveToStack(getDiscardStack().getTopCard(), getDealStack(), OPTION_NO_RECORD);

            scores.update(-200);    //because of no record, it isn't updated automatically
        }
    }


    public boolean cardTest(Stack stack, Card card) {
        if (stack.getID() == 31)
            return false;

        if (stack.getID() == 28 && card.getValue() == 13)
            return true;

        if (!stack.isEmpty() && stackIsFree(stack) && card.getValue() + stack.getTopCard().getValue() == 13) {

            cardsToMove.add(stack.getTopCard());
            cardsToMove.add(card);

            origins.add(stack);
            origins.add(card.getStack());

            return true;
        }

        return card.getStack() == getDealStack() && stack == getDiscardStack();
    }


    public boolean addCardToMovementTest(Card card) {

        if (card.getStack().getID() == 28)
            return false;

        if (card.getStack().getID() == getDiscardStack().getID())
            return true;

        Stack currentStack = card.getStack();

        return currentStack.getID() > 20 || stackIsFree(currentStack);

    }

    public CardAndStack hintTest() {

        ArrayList<Stack> freeStacks = new ArrayList<>();

        for (int i = 0; i <= getLastTableauID(); i++) {
            if (stackIsFree(stacks[i]) && !stacks[i].isEmpty() && !hint.hasVisited(stacks[i].getTopCard()))
                freeStacks.add(stacks[i]);
        }

        //first discard stack
        if (!getDiscardStack().isEmpty() && !hint.hasVisited(getDiscardStack().getTopCard()))
            freeStacks.add(getDiscardStack());
        //second discard stack
        if (!stacks[30].isEmpty() && !hint.hasVisited(stacks[30].getTopCard()))
            freeStacks.add(stacks[30]);

        for (Stack stack : freeStacks) {

            if (stack.getTopCard().getValue() == 13) {
                return new CardAndStack(stack.getTopCard(), stacks[28]);
            }

            for (Stack otherStack : freeStacks) {
                if (stack.getID() == otherStack.getID())
                    continue;

                if (stackIsFree(stack) && stack.getTopCard().getValue() + otherStack.getTopCard().getValue() == 13)
                    return new CardAndStack(stack.getTopCard(), otherStack);
            }
        }

        return null;
    }

    @Override
    public Stack doubleTapTest(Card card) {

        Stack returnStack = null;

        if (card.getValue() == 13) {
            return stacks[28];
        }

        for (int i = getLastTableauID(); i >= 0; i--) {

            if (stacks[i].isEmpty())
                continue;

            if (card.getStack().getID() != i && stackIsFree(stacks[i]) && card.getValue() + stacks[i].getTopCard().getValue() == 13) {
                returnStack = stacks[i];
                break;
            }
        }

        if (returnStack == null && !getDiscardStack().isEmpty() && card.getStack() != getDiscardStack() && card.getValue() + getDiscardStack().getTopCard().getValue() == 13)
            returnStack = getDiscardStack();

        if (returnStack == null && !getDealStack().isEmpty() && card.getStack() != getDealStack() && card.getValue() + getDealStack().getTopCard().getValue() == 13)
            returnStack = getDealStack();

        if (returnStack != null) {
            cardsToMove.add(returnStack.getTopCard());
            cardsToMove.add(card);

            origins.add(returnStack);
            origins.add(card.getStack());
            return returnStack;
        }

        if (card.getStack() == getDealStack())
            return getDiscardStack();

        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs) {
        if (destinationIDs[0] == 28)
            return 50;
        else if (cards.size() > 1 && originIDs[0] == getDiscardStack().getID() && destinationIDs[0] == getDealStack().getID())
            return -200;
        else
            return 0;
    }

    public void testAfterMove() {
        if (cardsToMove.size() > 0) {
            recordList.deleteLast();
            moveToStack(cardsToMove, stacks[28], OPTION_NO_RECORD);
            recordList.add(cardsToMove, origins);
            scores.update(50);

            cardsToMove.clear();
            origins.clear();

            testIfWonHandler.sendEmptyMessageDelayed(0, 200);
        }
    }

    public void addOnTouchListener(View.OnTouchListener listener) {
        super.addOnTouchListener(listener);
        getDealStack().view.setOnTouchListener(listener);
    }

    private boolean stackIsFree(Stack stack) {
        if (stack.getID() > 20)
            return true;

        Stack stackAbove1 = stacks[stackAboveID[stack.getID()]];
        Stack stackAbove2 = stacks[stackAboveID[stack.getID()] + 1];

        return stackAbove1.isEmpty() && stackAbove2.isEmpty();
    }
}

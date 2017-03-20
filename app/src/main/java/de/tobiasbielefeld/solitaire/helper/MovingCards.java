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

package de.tobiasbielefeld.solitaire.helper;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.Stack;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/*
 *  Handles the input of cards to move around. When a card was touched, it adds all cards
 *  up to the stack top card. It moves the cards and also returns the cards to their old location
 *  if they can't be placed on another stack
 */

public class MovingCards {

    private ArrayList<Card> currentCards = new ArrayList<>();                                       //array list containing the current cards to move
    private float offsetX, offsetY;

    public void reset() {
        currentCards.clear();
    }

    public void add(Card card, float offsetX, float offsetY) {
        /*
         *  add the card and every card above it, also set up the little offset from the
         *  touch position to the card coordinates, for smother movements
         */

        this.offsetX = offsetX;
        this.offsetY = offsetY;
        Stack stack = card.getStack();

        for (int i = stack.getIndexOfCard(card); i < stack.getSize(); i++) {
            stack.getCard(i).saveOldLocation();
            currentCards.add(stack.getCard(i));
        }
    }

    public void move(float X, float Y) {
        for (Card card : currentCards)
            card.setLocationWithoutMovement(X - offsetX, (Y - offsetY)
                    + currentCards.indexOf(card) * Stack.defaultSpacing / 2);
    }

    public void moveToDestination(Stack destination) {
        gameLogic.checkFirstMovement();

        Stack origin = currentCards.get(0).getStack();

        moveToStack(currentCards, destination);

        if (origin.getSize() > 0 && origin.getID() <= currentGame.getLastTableauID() && !origin.getTopCard().isUp())
            origin.getTopCard().flipWithAnim();

        currentCards.clear();

        if (!autoComplete.buttonIsShown() && currentGame.autoCompleteStartTest()) {
            autoComplete.showButton();
        }
    }

    public void returnToPos() {
        for (Card card : currentCards)
            card.returnToOldLocation();

        currentCards.clear();
    }

    public Card first() {
        return currentCards.get(0);
    }

    public int getSize() {
        return currentCards.size();
    }

    public boolean hasCards() {
        return !currentCards.isEmpty();
    }

    public boolean hasSingleCard() {
        //size can be 1 or zero, because it should return true when using hint tests.
        // In that case  the size is zero
        return movingCards.getSize() < 2;
    }
}

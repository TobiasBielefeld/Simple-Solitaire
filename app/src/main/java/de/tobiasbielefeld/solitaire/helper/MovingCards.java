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
import static java.lang.Math.abs;

/**
 * Handles the input of cards to move around. When a card was touched, it adds all cards
 * up to the stack top card. It moves the cards and also returns the cards to their old location
 * if they can't be placed on another stack
 */

public class MovingCards {

    private ArrayList<Card> currentCards = new ArrayList<>();                                       //array list containing the current cards to move
    private float offsetX, offsetY;
    private boolean moveStarted;

    public void reset() {
        currentCards.clear();
    }

    /**
     * Adds a card and every card above it to the movement.  Also sets up the little offset from the
     * touch position to the card coordinates, for smother movements
     *
     * @param card    The card to add.
     * @param offsetX X-coordinate of the offset from card coordinates and touch coordinates
     * @param offsetY Y-coordinate of the offset from card coordinates and touch coordinates
     */
    public void add(Card card, float offsetX, float offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        Stack stack = card.getStack();
        moveStarted = false;

        for (int i = stack.getIndexOfCard(card); i < stack.getSize(); i++) {
            stack.getCard(i).saveOldLocation();
            currentCards.add(stack.getCard(i));
        }
    }

    /**
     * Moves the cards to the new location
     *
     * @param X X-coordinate of the destination
     * @param Y Y-coordinate of the destination
     */
    public void move(float X, float Y) {
        for (Card card : currentCards) {
            card.setLocationWithoutMovement(X - offsetX, (Y - offsetY)
                    + currentCards.indexOf(card) * Stack.defaultSpacing / 2);
        }
    }

    public boolean moveStarted(float X, float Y) {
        return moveStarted || didMoveStart(X, Y);
    }

    /**
     * checks if the current touch point of the movement left the little area around the starting point.
     *
     * @param X X-coordinate of the point
     * @param Y Y-coordinate of the point
     * @return True if the area was left, false otherwise
     */
    private boolean didMoveStart(float X, float Y) {
        if (abs(currentCards.get(0).getX() + offsetX - X) > Card.width / 4 || abs(currentCards.get(0).getY() + offsetY - Y) > Card.height / 4) {
            moveStarted = true;
            return true;
        }

        return false;
    }

    /**
     * Moves the current cards to the new destination.
     *
     * @param destination The destination stack
     */
    public void moveToDestination(Stack destination) {
        gameLogic.checkFirstMovement();
        sounds.playSound(Sounds.names.CARD_SET);

        Stack origin = currentCards.get(0).getStack();

        moveToStack(currentCards, destination);

        if (origin.getSize() > 0 && origin.getId() <= currentGame.getLastTableauId() && !origin.getTopCard().isUp()) {
            origin.getTopCard().flipWithAnim();
        }

        currentCards.clear();
        gameLogic.checkForAutoCompleteButton(false);
    }

    /**
     * return the cards to the old location, in case the movement to the new stack isn't working
     */
    public void returnToPos() {
        for (Card card : currentCards)
            card.returnToOldLocation();

        currentCards.clear();
    }

    /**
     * Checks if only one card is moving. But the size is eg in hints still zero
     * (because hints call the cardTest() method in Game, which in turn calls hasSingleCard(). But
     * in hints, the user doesn't move by himself, so return true if < 2 cards are moving)
     *
     * @return True if a single card is moving, False otherwise
     */
    public boolean hasSingleCard() {
        return getSize() < 2;
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
}

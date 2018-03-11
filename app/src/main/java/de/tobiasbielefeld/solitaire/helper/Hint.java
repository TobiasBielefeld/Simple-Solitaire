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
import de.tobiasbielefeld.solitaire.handler.HandlerHint;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Shows hints. It has a handler which shows up to MAX_NUMBER_OF_HINTS hints.
 * The hint function tests the tableau and stock if a card can be moved. If so,
 * the hint animation will be started and the card will be marked, so it won't be shown again
 * as a hint
 */

public class Hint {

    public static final int MAX_NUMBER_OF_HINTS = 3;                                                //max number of hints which are shown when pressing the button
    public HandlerHint handlerHint;                                             //handler to show the hinzd

    private int counter = 0;                                                                        //counter to know how many hints were shown
    private Card[] visited = new Card[MAX_NUMBER_OF_HINTS];                                         //array for already shown cards in hint

    public Hint(GameManager gm){
        handlerHint = new HandlerHint(gm);
    }

    public void showHint() {
        handlerHint.sendEmptyMessage(0);
    }

    /**
     * moves a card with the hint animation. It will also be marked as visited, so the card
     * won't be used in the next step. It gets one card and the stack destination, but it
     * also adds all cards above.
     */
    public void move(Card card, Stack destination) {
        Stack origin = card.getStack();
        int index = origin.getIndexOfCard(card);
        ArrayList<Card> currentCards = new ArrayList<>();

        if (counter == 0 && !prefs.getDisableHintCosts()) {
            scores.update(-currentGame.getHintCosts());
        }

        visited[counter] = card;

        for (int i = index; i < origin.getSize(); i++) {
            currentCards.add(origin.getCard(i));
        }

        for (int i = 0; i < currentCards.size(); i++) {
            animate.cardHint(currentCards.get(i), i, destination);
        }
    }

    /**
     * tests a card if it has been visited in the current hint
     *
     * @param test_card The card to test
     * @return True if the card has been visited, false otherwise
     */
    public boolean hasVisited(Card test_card) {
        for (int i = 0; i < counter; i++) {
            if (test_card == visited[i]) {
                return true;
            }
        }

        return false;
    }

    public boolean isWorking() {
        return counter != 0;
    }

    public void stop() {
        counter = MAX_NUMBER_OF_HINTS;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int value) {
        counter = value;
    }
}
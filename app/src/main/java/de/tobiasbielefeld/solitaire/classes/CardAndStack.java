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

package de.tobiasbielefeld.solitaire.classes;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Just a little class to return a stack and a card at once from a method
 */

public class CardAndStack {
    private int cardID;
    private int stackID;

    public CardAndStack(Card card, Stack stack) {
        cardID = card.getId();
        stackID = stack.getId();
    }

    public CardAndStack(int cardID, int stackID) {
        this.cardID = cardID;
        this.stackID = stackID;
    }

    public int getCardId() {
        return cardID;
    }

    public int getStackId() {
        return stackID;
    }

    public Card getCard() {
        return cards[cardID];
    }

    public Stack getStack() {
        return stacks[stackID];
    }
}

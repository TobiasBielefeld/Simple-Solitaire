package de.tobiasbielefeld.solitaire.classes;

import static de.tobiasbielefeld.solitaire.SharedData.cards;
import static de.tobiasbielefeld.solitaire.SharedData.stacks;

/**
 * Just a little class to return a stack and a card at once from a method
 */

public class CardAndStack {
    private int cardID;
    private int stackID;

    public CardAndStack(Card card, Stack stack){
        cardID = card.getID();
        stackID = stack.getID();
    }

    public CardAndStack(int cardID, int stackID){
        this.cardID = cardID;
        this.stackID = stackID;
    }

    public int getCardID() {
        return cardID;
    }

    public int getStackID() {
        return stackID;
    }

    public Card getCard() {
        return cards[cardID];
    }

    public Stack getStack() {
        return stacks[stackID];
    }
}

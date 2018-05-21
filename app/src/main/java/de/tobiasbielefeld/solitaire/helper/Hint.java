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

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.CustomHandler;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Shows hints. It has a handler which shows up to MAX_NUMBER_OF_HINTS hints.
 * The hint function tests the tableau and stock if a card can be moved. If so,
 * the hint animation will be started and the card will be marked, so it won't be shown again
 * as a hint
 */

public class Hint {

    private static final int MAX_NUMBER_OF_HINTS = 3;                                                //max number of hints which are shown when pressing the button

    private int counter = 0;                                                                        //counter to know how many hints were shown
    private boolean showedFirstHint = false;
    private boolean working = false;
    private GameManager gm;
    private ArrayList<Card> visited;                                                                //array for already shown cards in hint
    private CustomHandler handler;                                                                  //handler to show the hints

    public Hint(GameManager gm){
        handler = new CustomHandler(new CustomHandler.MessageCallBack() {
            @Override
            public void sendMessage() {
                handleMessage();
            }
        });

        visited = new ArrayList<>(MAX_NUMBER_OF_HINTS);
        this.gm = gm;
    }

    public void start() {
        showedFirstHint = false;
        working = true;
        visited.clear();
        counter = 0;

        handler.sendDelayed();
    }

    public void stop() {
        working = false;
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

        addToVisited(card);


        for (int i = index; i < origin.getSize(); i++) {
            currentCards.add(origin.getCard(i));
        }

        for (int i = 0; i < currentCards.size(); i++) {
            animate.cardHint(currentCards.get(i), i, destination);
        }
    }

    public void handleMessage(){
        if (working && counter < Hint.MAX_NUMBER_OF_HINTS) {
            CardAndStack cardAndStack;

            if (!animate.cardIsAnimating()) {
                cardAndStack = currentGame.hintTest();

                if (cardAndStack == null) {
                    if (!showedFirstHint){
                        showToast(gm.getString(R.string.dialog_no_hint_available),gm);
                    }

                    hint.stop();

                } else {
                    if (!showedFirstHint) {
                        sounds.playSound(Sounds.names.HINT);
                        showedFirstHint = true;

                        int amount = prefs.getSavedTotalHintsShown() + 1;
                        prefs.saveTotalHintsShown(amount);
                    }

                    hint.move(cardAndStack.getCard(), cardAndStack.getStack());
                }

                counter++;
            }

            handler.sendDelayed();
        } else {
            stop();
        }
    }

    public void addToVisited(Card card){
        visited.add(card);
    }

    public boolean hasVisited(Card testCard) {
        return visited.contains(testCard);
    }

    public boolean isWorking() {
        return working;
    }
}
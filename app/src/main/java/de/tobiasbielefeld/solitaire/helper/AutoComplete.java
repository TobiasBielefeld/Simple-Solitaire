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

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.HelperCardMovement;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * if the last card on the tableau is flipped up, the auto complete can be run. it simply test
 * every card from the tableau and the stock if they can be placed on the foundation.
 * it continues until the last card was moved to the foundation. after that,
 * the win animation will be started
 */

public class AutoComplete extends HelperCardMovement {

    private final static int START_TIME = 300;                                                  //start velocity of the handler callings
    private final static int DELTA_TIME = 5;                                                    //will be decreased on every call by this number
    private final static int MIN_TIME = 50;                                                     //minimum to avoid errors
    private int currentTime = START_TIME;                                                                    //current velocity of the handler calling
    private boolean isFinished = false;                                                                 //needed to know when to call the win animation
    private int phase = 1;

    private boolean buttonShown = false;

    public AutoComplete(GameManager gm) {
        super(gm, "AUTO_COMPLETE");
    }

    public void start() {
        hideButton();
        phase = 1;
        isFinished = false;
        currentTime = START_TIME;

        super.start();
    }

    @Override
    protected void saveState(Bundle bundle) {
        bundle.putInt("AUTOCOMPLETE_CURRENT_TIME",currentTime);
        bundle.putInt("AUTOCOMPLETE_PHASE",phase);
    }

    @Override
    protected void loadState(Bundle bundle) {
        currentTime = bundle.getInt("AUTOCOMPLETE_CURRENT_TIME");
        phase = bundle.getInt("AUTOCOMPLETE_PHASE");

        hideButton();
    }

    public boolean buttonIsShown() {
        return buttonShown;
    }

    public void showButton(boolean withoutMovement) {
        buttonShown = true;

        if (!withoutMovement) {
            sounds.playSound(Sounds.names.SHOW_AUTOCOMPLETE);
            animate.showAutoCompleteButton();
        }
        else {
            gm.buttonAutoComplete.setVisibility(View.VISIBLE);
        }
    }

    public void hideButton() {
        buttonShown = false;

        if (gm.buttonAutoComplete.getVisibility() == View.VISIBLE) {
            gm.buttonAutoComplete.setVisibility(View.GONE);
        }
    }

    @Override
    protected void moveCard() {
        switch (phase){
            case 1:
                phase1();
                break;
            case 2:
                phase2();
                break;
            case 3: default:
                phase3();
                break;
        }
    }

    public void phase1() {
        CardAndStack cardAndStack = currentGame.autoCompletePhaseOne();

        if (cardAndStack == null) {
            phase = 2;
            nextIteration(0);
        }
        else {
            ArrayList<Card> cards = new ArrayList<>();
            Stack origin = cardAndStack.getCard().getStack();

            for (int i = origin.getIndexOfCard(cardAndStack.getCard()); i < origin.getSize(); i++) {
                cards.add(cardAndStack.getCard().getStack().getCard(i));
            }

            sounds.playSound(Sounds.names.CARD_SET);
            moveToStack(cards, cardAndStack.getStack());


            //start the next handler in some milliseconds
            currentTime = max(currentTime - DELTA_TIME, MIN_TIME);
            nextIteration(currentTime);
        }
    }

    public void phase2() {
        CardAndStack cardAndStack = currentGame.autoCompletePhaseTwo();

        if (cardAndStack == null) {
            phase = 3;
            nextIteration(START_TIME);
        }
        else {
            Card card = cardAndStack.getCard();
            Stack destination = cardAndStack.getStack();

            scores.move(card, destination);
            card.removeFromCurrentStack();
            destination.addCard(card);
            card.bringToFront();
            sounds.playSound(Sounds.names.CARD_SET);
            card.setLocation(destination.getX(),destination.getY());

            //start the next handler in some milliseconds
            currentTime = max(currentTime - DELTA_TIME, MIN_TIME);
            nextIteration(currentTime);
        }
    }

    public void phase3(){
        stop();
        gameLogic.testIfWon();
    }


    @Override
    protected boolean haltCondition() {
        return animate.cardIsAnimating() && (phase == 1 || isFinished);
    }

}

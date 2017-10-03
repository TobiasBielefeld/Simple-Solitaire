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

import android.content.res.Resources;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.classes.Card;

import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_VEGAS_BET_AMOUNT;
import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_VEGAS_DRAW;
import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_VEGAS_MONEY;
import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_VEGAS_MONEY_ENABLED;
import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_VEGAS_NUMBER_OF_RECYCLES;
import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_VEGAS_RESET_MONEY;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_VEGAS_BET_AMOUNT;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_VEGAS_BET_AMOUNT_OLD;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_VEGAS_DRAW;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_VEGAS_DRAW_OLD;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_VEGAS_MONEY;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_VEGAS_MONEY_ENABLED;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_VEGAS_NUMBER_OF_RECYCLES;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_VEGAS_RESET_MONEY;
import static de.tobiasbielefeld.solitaire.SharedData.gameLogic;
import static de.tobiasbielefeld.solitaire.SharedData.getSharedBoolean;
import static de.tobiasbielefeld.solitaire.SharedData.getSharedInt;
import static de.tobiasbielefeld.solitaire.SharedData.getSharedLong;
import static de.tobiasbielefeld.solitaire.SharedData.putSharedBoolean;
import static de.tobiasbielefeld.solitaire.SharedData.putSharedInt;
import static de.tobiasbielefeld.solitaire.SharedData.putSharedLong;
import static de.tobiasbielefeld.solitaire.SharedData.scores;

/**
 * Vegas game! It's like Klondike, but with some changes and different scoring.
 */

public class Vegas extends Klondike {

    private int betAmount=50;

    public Vegas(){
        disableBonus();
        setPointsInDollar();
        loadData();

        PREF_KEY_DRAW_OLD = PREF_KEY_VEGAS_DRAW_OLD;
        PREF_KEY_DRAW = PREF_KEY_VEGAS_DRAW;
        DEFAULT_DRAW = DEFAULT_VEGAS_DRAW;

        setNumberOfRecycles(PREF_KEY_VEGAS_NUMBER_OF_RECYCLES,DEFAULT_VEGAS_NUMBER_OF_RECYCLES);
    }

    @Override
    public void dealCards() {
        super.dealCards();

        loadData();

        putSharedInt(PREF_KEY_VEGAS_BET_AMOUNT_OLD, getSharedInt(PREF_KEY_VEGAS_BET_AMOUNT, DEFAULT_VEGAS_BET_AMOUNT));

        boolean moneyEnabled = getSharedBoolean(PREF_KEY_VEGAS_MONEY_ENABLED,DEFAULT_VEGAS_MONEY_ENABLED);
        long money = moneyEnabled ? getSharedLong(PREF_KEY_VEGAS_MONEY,DEFAULT_VEGAS_MONEY) : 0;
        scores.update(money-betAmount);
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs, boolean isUndoMovement) {
        int originID = originIDs[0];
        int destinationID = destinationIDs[0];

        //relevant for deal3 options, because cards on the waste move first and checking only
        //the first id wouldn't be enough
        for (int i=0;i<originIDs.length;i++){
            if (originIDs[i] >=11 && originIDs[i]<=13 && destinationIDs[i] >=7 && destinationIDs[i] <=10){//stock to foundation
                return betAmount/10;
            }
        }

        if (originID < 7 && destinationID >= 7 && destinationID <= 10){                             //from tableau to foundation
            return betAmount/10;
        }

        if (originID >= 7 && originID <= 10 && destinationID < 7){                                  //from foundation to tableau
            return -2*betAmount/10;
        }

        return 0;
    }

    @Override
    public boolean processScore(long currentScore) {
        boolean moneyEnabled = getSharedBoolean(PREF_KEY_VEGAS_MONEY_ENABLED,DEFAULT_VEGAS_MONEY_ENABLED);
        boolean resetMoney = getSharedBoolean(PREF_KEY_VEGAS_RESET_MONEY, DEFAULT_VEGAS_RESET_MONEY);

        if (resetMoney) {
            putSharedLong(PREF_KEY_VEGAS_MONEY, DEFAULT_VEGAS_MONEY);
            putSharedBoolean(PREF_KEY_VEGAS_RESET_MONEY,false);
        } else if (moneyEnabled) {
            putSharedLong(PREF_KEY_VEGAS_MONEY, scores.getScore());
        }

        if (!gameLogic.hasWon() && currentScore > 0){
            gameLogic.incrementNumberWonGames();
        }

        //return false, to let the  addNewHighScore() save a possible score.
        return !moneyEnabled || resetMoney;
    }

    private void loadData(){
        betAmount = getSharedInt(PREF_KEY_VEGAS_BET_AMOUNT_OLD, DEFAULT_VEGAS_BET_AMOUNT)*10;

        setHintCosts(betAmount/10);
        setUndoCosts(betAmount/10);
    }
}

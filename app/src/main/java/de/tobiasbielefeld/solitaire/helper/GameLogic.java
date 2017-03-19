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

import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_FIRST_RUN;
import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_MOVED_FIRST_CARD;
import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_WON;
import static de.tobiasbielefeld.solitaire.SharedData.GAME_FIRST_RUN;
import static de.tobiasbielefeld.solitaire.SharedData.GAME_MOVED_FIRST_CARD;
import static de.tobiasbielefeld.solitaire.SharedData.GAME_NUMBER_OF_PLAYED_GAMES;
import static de.tobiasbielefeld.solitaire.SharedData.GAME_NUMBER_OF_WON_GAMES;
import static de.tobiasbielefeld.solitaire.SharedData.GAME_RANDOM_CARDS;
import static de.tobiasbielefeld.solitaire.SharedData.GAME_WON;
import static de.tobiasbielefeld.solitaire.SharedData.TIMER_CURRENT_TIME;
import static de.tobiasbielefeld.solitaire.SharedData.animate;
import static de.tobiasbielefeld.solitaire.SharedData.autoComplete;
import static de.tobiasbielefeld.solitaire.SharedData.cards;
import static de.tobiasbielefeld.solitaire.SharedData.currentGame;
import static de.tobiasbielefeld.solitaire.SharedData.getBoolean;
import static de.tobiasbielefeld.solitaire.SharedData.getInt;
import static de.tobiasbielefeld.solitaire.SharedData.getIntList;
import static de.tobiasbielefeld.solitaire.SharedData.getLong;
import static de.tobiasbielefeld.solitaire.SharedData.getSharedBoolean;
import static de.tobiasbielefeld.solitaire.SharedData.movingCards;
import static de.tobiasbielefeld.solitaire.SharedData.putBoolean;
import static de.tobiasbielefeld.solitaire.SharedData.putInt;
import static de.tobiasbielefeld.solitaire.SharedData.putIntList;
import static de.tobiasbielefeld.solitaire.SharedData.recordList;
import static de.tobiasbielefeld.solitaire.SharedData.scores;
import static de.tobiasbielefeld.solitaire.SharedData.stacks;
import static de.tobiasbielefeld.solitaire.SharedData.timer;

/**
 * Contains stuff for the game which i didn't know where i should put it.
 */

public class GameLogic {

    public Card[] randomCards;                                                                      //array to shuffle the cards
    private int numberWonGames;                                                                     //number of won games. It's shown in the high score activity
    private boolean won;                                                                            //shows if the player has won, needed to know if the timer can stop, or to deal new cards on game start
    private GameManager gm;
    private boolean movedFirstCard = false;

    public GameLogic(GameManager gm) {
        this.gm = gm;
    }

    public void checkFirstMovement() {
        if (!movedFirstCard) {
            incrementPlayedGames();
            movedFirstCard = true;
        }
    }

    public void save() {
        /*
         * save the current game (called in onPause, because onDestroy is not always called
         * when the app closes for some reason
         */
        scores.save();
        recordList.save();
        putBoolean(GAME_WON, won);
        putBoolean(GAME_MOVED_FIRST_CARD, movedFirstCard);
        putInt(GAME_NUMBER_OF_WON_GAMES, numberWonGames);
        /* Timer will be saved in onPause() */
        for (Stack stack : stacks)
            stack.save();

        Card.save();
        saveRandomCards();
        currentGame.save();
        currentGame.saveRedealCount();
    }

    public void load() {
        /*
         * load everything saved on start of a game. If the last game has been won put every card
         * outside the screen.
         * The main loading part is put in a try catch block, so when there goes something wrong
         * on saving/loading, it won't crash the game. (in that case, it loads a new game
         */
        boolean first_run = getBoolean(GAME_FIRST_RUN, DEFAULT_FIRST_RUN);
        numberWonGames = getInt(GAME_NUMBER_OF_WON_GAMES, 0);
        won = getBoolean(GAME_WON, DEFAULT_WON);
        movedFirstCard = getBoolean(GAME_MOVED_FIRST_CARD, DEFAULT_MOVED_FIRST_CARD);
        //update and reset
        Card.updateCardDrawableChoice();
        Card.updateCardBackgroundChoice();
        animate.reset();
        autoComplete.reset();
        currentGame.load();
        currentGame.loadRedealCount(gm);

        if (first_run) {
            newGame();
            putBoolean(GAME_FIRST_RUN, false);
        } else if (won) {
            loadRandomCards();

            for (Card card : cards)
                card.setLocationWithoutMovement(gm.layoutGame.getWidth(), 0);
        } else {
            try {
                for (Card card : cards) {
                    card.setLocationWithoutMovement(currentGame.dealFromStack().view.getX(), currentGame.dealFromStack().view.getY());
                    card.flipDown();
                }

                scores.load();
                recordList.load();
                timer.setCurrentTime(getLong(TIMER_CURRENT_TIME, 0));
                //timer will be loaded in onResume()
                for (Stack stack : stacks)
                    stack.load();

                Card.load();
                loadRandomCards();

                if (!autoComplete.buttonIsShown() && currentGame.autoCompleteStartTest()) {
                    autoComplete.showButton();
                }
            } catch (Exception e) {
                Log.e(gm.getString(R.string.loading_data_failed), e.toString());
                gm.showToast(gm.getString(R.string.game_load_error));
                newGame();
            }
        }
    }

    public void newGame() {
        //new game is like a re-deal, but with randomized cards
        randomCards = cards.clone();
        randomize(randomCards);

        redeal();
    }

    public void redeal() {
        //reset EVERYTHING
        if (!won) {                                                                                 //if the game has been won, the score was already saved
            scores.addNewHighScore();
        }

        movedFirstCard = false;
        won = false;
        currentGame.reset(gm);

        animate.reset();
        scores.reset();
        movingCards.reset();
        recordList.reset();
        timer.reset();
        autoComplete.hideButton();

        for (Stack stack : stacks)
            stack.reset();

        //Put cards to the specified "deal from" stack. (=main stack if the game has one, else specify it in the game
        for (Card card : randomCards) {
            card.setLocationWithoutMovement(currentGame.dealFromStack().view.getX(), currentGame.dealFromStack().view.getY());
            currentGame.dealFromStack().addCard(card);
            card.flipDown();
        }

        //and finally deal the cards from the game!
        currentGame.dealCards();
    }

    public void testIfWon() {
        if (!won && !autoComplete.isRunning() && currentGame.winTest()) {
            won = true;
            numberWonGames++;

            scores.updateBonus();
            scores.addNewHighScore();
            recordList.reset();
            autoComplete.hideButton();
            animate.wonAnimation();
        }
    }

    private void randomize(Card[] Array) {
        /*
         * Fisher–Yates shuffle
         */
        int index;
        Card dummy;
        Random rand = new Random();

        for (int i = Array.length - 1; i > 0; i--) {
            if ((index = rand.nextInt(i + 1)) != i) {
                dummy = Array[i];
                Array[i] = Array[index];
                Array[index] = dummy;
            }
        }
    }

    public void mirrorStacks() {
        /*
         * for left handed mode: mirrors the stacks to the other side and then updates the card
         * positions.
         */

        if (stacks != null) {
            for (Stack stack : stacks) {
                stack.mirrorStack(gm.layoutGame);
            }
        }

        //move the re-deal counter too
        if (currentGame.hasLimitedRedeals()) {
            gm.mainTextViewRedeals.setX(currentGame.getMainStack().view.getX());
            gm.mainTextViewRedeals.setY(currentGame.getMainStack().view.getY());
        }

        //change the arrow direction
        if (currentGame.hasArrow()) {
            for (Stack stack : stacks) {
                if (stack.hasArrow() > 0) {
                    if (stack.hasArrow() == 1) {
                        if (getSharedBoolean(gm.getString(R.string.pref_key_left_handed_mode), false))
                            stack.view.setImageBitmap(Stack.arrowRight);
                        else
                            stack.view.setImageBitmap(Stack.arrowLeft);
                    } else {
                        if (getSharedBoolean(gm.getString(R.string.pref_key_left_handed_mode), false))
                            stack.view.setImageBitmap(Stack.arrowLeft);
                        else
                            stack.view.setImageBitmap(Stack.arrowRight);
                    }
                }
            }
        }
    }

    public boolean hasWon() {
        return won;
    }

    public int getNumberWonGames() {
        return numberWonGames;
    }

    public void deleteStatistics() {
        numberWonGames = 0;
        putInt(GAME_NUMBER_OF_PLAYED_GAMES, 0);
    }

    private void saveRandomCards() {
        ArrayList<Integer> list = new ArrayList<>();

        for (Card card : randomCards)
            list.add(card.getID());

        putIntList(GAME_RANDOM_CARDS, list);
    }

    private void loadRandomCards() {
        ArrayList<Integer> list = getIntList(GAME_RANDOM_CARDS);

        for (int i = 0; i < randomCards.length; i++)
            randomCards[i] = cards[list.get(i)];
    }

    private void incrementPlayedGames() {
        int playedGames = getInt(GAME_NUMBER_OF_PLAYED_GAMES, numberWonGames);
        putInt(GAME_NUMBER_OF_PLAYED_GAMES, ++playedGames);
    }

    public int getNumberOfPlayedGames() {
        return getInt(GAME_NUMBER_OF_PLAYED_GAMES, numberWonGames);
    }

    public void toggleNumberOfRedeals() {

        if (currentGame == null)
            return;

        currentGame.toggleRedeals();

        if (currentGame.hasLimitedRedeals()) {

            gm.mainTextViewRedeals.setVisibility(View.VISIBLE);
            gm.mainTextViewRedeals.setX(currentGame.getMainStack().view.getX());
            gm.mainTextViewRedeals.setY(currentGame.getMainStack().view.getY());

        } else {
            gm.mainTextViewRedeals.setVisibility(View.GONE);
        }
    }

    public void updateIcons() {
        gm.updateIcons();
    }

    public void updateMenuBar() {
        gm.updateMenuBar();
    }
}
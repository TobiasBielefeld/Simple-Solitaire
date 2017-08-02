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

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Contains stuff for the game which i didn't know where i should put it.
 */

public class GameLogic {

    public Card[] randomCards;                                                                      //array to shuffle the cards
    private int numberWonGames;                                                                     //number of won games. It's shown in the high score activity
    private boolean won, wonAndReloaded;                                                                            //shows if the player has won, needed to know if the timer can stop, or to deal new cards on game start
    private GameManager gm;
    private boolean movedFirstCard = false;

    public GameLogic(GameManager gm) {
        this.gm = gm;
    }

    /**
     * checks if the first card of a game has been moved, if so, increment the number of played games
     */
    public void checkFirstMovement() {
        if (!movedFirstCard) {
            incrementPlayedGames();
            movedFirstCard = true;
        }
    }

    /**
     * saves all relevant data of the current game in shared preferences, so it can be loaded
     * when resuming the game, called in onPause() of the GameManager
     */
    public void save() {
        scores.save();
        recordList.save();
        putBoolean(GAME_WON, won);
        putBoolean(GAME_WON_AND_RELOADED, wonAndReloaded);
        putBoolean(GAME_MOVED_FIRST_CARD, movedFirstCard);
        putInt(GAME_NUMBER_OF_WON_GAMES, numberWonGames);
        // Timer will be saved in onPause()
        for (Stack stack : stacks)
            stack.save();

        Card.save();
        saveRandomCards();
        currentGame.save();
        currentGame.saveRedealCount();
    }

    public void setWonAndReloaded(){
        if (won){
            wonAndReloaded = true;
        }
    }

    /**
     * load everything saved on start of a game. If the last game has been won put every card
     * outside the screen.
     * The main loading part is put in a try catch block, so when there goes something wrong
     * on saving/loading, it won't crash the game. (in that case, it loads a new game)
     */
    public void load() {

        boolean firstRun = getBoolean(GAME_FIRST_RUN, DEFAULT_FIRST_RUN);
        numberWonGames = getInt(GAME_NUMBER_OF_WON_GAMES, 0);
        won = getBoolean(GAME_WON, DEFAULT_WON);
        wonAndReloaded = getBoolean(GAME_WON_AND_RELOADED, DEFAULT_WON_AND_RELOADED);
        movedFirstCard = getBoolean(GAME_MOVED_FIRST_CARD, DEFAULT_MOVED_FIRST_CARD);
        //update and reset
        Card.updateCardDrawableChoice();
        Card.updateCardBackgroundChoice();
        animate.reset();
        autoComplete.reset();
        currentGame.load();
        currentGame.loadRedealCount(gm);
        sounds.playSound(Sounds.names.DEAL_CARDS);

        try {
            if (firstRun) {
                newGame();
                putBoolean(GAME_FIRST_RUN, false);
            }  else if (wonAndReloaded){        //in case the game was selected from the main menu and it was already won, start a new game
                newGame();
            } else if (won) {                   //in case the screen orientation changes, do not immediately start a new game
                loadRandomCards();

                for (Card card : cards) {
                    card.setLocationWithoutMovement(gm.layoutGame.getWidth(), 0);
                }
            } else {
                for (Card card : cards) {
                    card.setLocationWithoutMovement(currentGame.getDealStack().getX(), currentGame.getDealStack().getY());
                    card.flipDown();
                }

                scores.load();
                recordList.load();
                timer.setCurrentTime(getLong(TIMER_END_TIME, 0));

                //timer will be loaded in onResume() of the game manager

                Card.load();

                for (Stack stack : stacks) {
                    stack.load();
                }

                loadRandomCards();

                if (!autoComplete.buttonIsShown() && currentGame.autoCompleteStartTest()) {
                    autoComplete.showButtonWithoutSound();
                }
            }
        } catch (Exception e) {
            Log.e(gm.getString(R.string.loading_data_failed), e.toString());
            gm.showToast(gm.getString(R.string.game_load_error));
            newGame();
        }
    }

    /**
     * starts a new game. The only difference to a re-deal is the shuffling of the cards
     */
    public void newGame() {
        System.arraycopy(cards, 0, randomCards, 0, cards.length);
        randomize(randomCards);

        redeal();
    }

    /**
     * starts a new game, but with the same deal.
     */
    public void redeal() {
        //reset EVERYTHING
        if (!won) {                                                                                 //if the game has been won, the score was already saved
            scores.addNewHighScore();
        }

        movedFirstCard = false;
        won = false;
        wonAndReloaded = false;
        currentGame.reset(gm);
        sounds.playSound(Sounds.names.DEAL_CARDS);

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
            card.setLocationWithoutMovement(currentGame.getDealStack().getX(), currentGame.getDealStack().getY());          ///TODO there is an error somewhere here "null pointer exception"
            currentGame.getDealStack().addCard(card);
            card.flipDown();
        }

        //and finally deal the cards from the game!
        currentGame.dealCards();
    }

    /**
     * in case the current game is won: save the score and start the win animation. The record list
     * is reseted, so the player can't revert card movements after the animation
     */
    public void testIfWon() {
        if (!won && !autoComplete.isRunning() && currentGame.winTest()) {

            new Thread(new Runnable() {
                public void run() {
                    numberWonGames++;
                    scores.updateBonus();
                    scores.addNewHighScore();
                    recordList.reset();
                    timer.setWinningTime();
                }
            }).start();

            autoComplete.hideButton();
            animate.winAnimation();
            won = true;
        }
    }

    /**
     * Randomizes a given card array using the Fisher–Yates shuffle
     *
     * @param Array The array to randomize
     */
    private void randomize(Card[] Array) {
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

    /**
     * for left handed mode: mirrors the stacks to the other side and then updates the card
     * positions.
     */
    public void mirrorStacks() {


        if (stacks != null) {
            for (Stack stack : stacks) {
                stack.mirrorStack(gm.layoutGame);
            }
        }

        //move the re-deal counter too
        if (currentGame.hasLimitedRedeals()) {
            gm.mainTextViewRedeals.setX(currentGame.getMainStack().getX());
            gm.mainTextViewRedeals.setY(currentGame.getMainStack().getY());
        }

        //change the arrow direction
        if (currentGame.hasArrow()) {
            for (Stack stack : stacks) {
                stack.applyArrow();
            }
        }
    }

    /**
     * toggle the redeal counter: From enabled to disabled and vice versa. When enabled, the location
     * is also updated.
     */
    public void toggleNumberOfRedeals() {

        if (currentGame == null)
            return;

        currentGame.toggleRedeals();

        if (currentGame.hasLimitedRedeals()) {
            gm.mainTextViewRedeals.setVisibility(View.VISIBLE);
            gm.mainTextViewRedeals.setX(currentGame.getMainStack().getX());
            gm.mainTextViewRedeals.setY(currentGame.getMainStack().getY());
        } else {
            gm.mainTextViewRedeals.setVisibility(View.GONE);
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
            list.add(card.getId());

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

    public void updateMenuBar() {
        gm.updateMenuBar();
    }
}
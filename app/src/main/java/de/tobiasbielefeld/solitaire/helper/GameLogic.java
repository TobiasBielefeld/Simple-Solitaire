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

import java.util.ArrayList;
import java.util.Random;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Contains stuff for the game which I didn't know where I should put it.
 */

public class GameLogic {

    public Card[] randomCards;                                                                      //array to shuffle the cards
    private boolean won, wonAndReloaded;                                                            //shows if the player has won, needed to know if the timer can stop, or to deal new cards on game start
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
            movedFirstCard = true;
        }
    }

    /**
     * saves all relevant data of the current game in shared preferences, so it can be loaded
     * when resuming the game, called in onPause() of the GameManager
     */
    public void save() {
        if (!prefs.isDeveloperOptionSavingDisabled() && !stopUiUpdates) {
            scores.save();
            recordList.save();
            prefs.saveWon(won);
            prefs.saveWonAndReloaded(wonAndReloaded);
            prefs.saveMovedFirstCard(movedFirstCard);
            // Timer will be saved in onPause()

            for (Stack stack : stacks) {
                stack.save();
            }

            Card.save();
            saveRandomCards();
            currentGame.save();
            currentGame.saveRecycleCount();
        }
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
    public void load(boolean withoutMovement) {
        boolean firstRun = prefs.isFirstRun();
        won = prefs.isWon();
        wonAndReloaded = prefs.isWonAndReloaded();
        movedFirstCard = prefs.hasMovedFirstCard();
        //update and reset
        Card.updateCardDrawableChoice();
        Card.updateCardBackgroundChoice();
        animate.reset();
        autoComplete.hideButton();


        if (!withoutMovement) {
            sounds.playSound(Sounds.names.DEAL_CARDS);

            for (Card card : cards) {
                card.setLocationWithoutMovement(currentGame.getDealStack().getX(), currentGame.getDealStack().getY());
                card.flipDown();
            }
        }

        try {
            if (firstRun) {
                newGame();
                prefs.saveFirstRun(false);
            }  else if (wonAndReloaded && prefs.getSavedAutoStartNewGame()){
                //in case the game was selected from the main menu and it was already won, start a new game
                newGame();
            } else {
                scores.load();
                recordList.load();
                timer.setCurrentTime(prefs.getSavedEndTime());

                //timer will be loaded in onResume() of the game manager

                Card.load();

                for (Stack stack : stacks) {
                    stack.load(withoutMovement);
                }

                loadRandomCards();

                checkForAutoCompleteButton(withoutMovement);

                //load game dependent data
                currentGame.load();
                currentGame.loadRecycleCount();
            }
        } catch (Exception e) {
            Log.e(gm.getString(R.string.loading_data_failed), e.toString());
            showToast(gm.getString(R.string.game_load_error),gm);
            newGame();
        }

        gm.hasLoaded = true;
    }

    public void checkForAutoCompleteButton(boolean withoutMovement){
        if (!prefs.getHideAutoCompleteButton() && !autoComplete.buttonIsShown() && currentGame.autoCompleteStartTest() && !hasWon()) {
            autoComplete.showButton(withoutMovement);
        }
    }

    public void newGameForEnsureMovability(){
        System.arraycopy(cards, 0, randomCards, 0, cards.length);
        randomize(randomCards);
        redealForEnsureMovability();
    }

    /**
     * starts a new game. The only difference to a re-deal is the shuffling of the cards
     */
    public void newGame() {
        System.arraycopy(cards, 0, randomCards, 0, cards.length);
        randomize(randomCards);

        if (prefs.getSavedEnsureMovability()) {
            gameLogic.save();
            stopUiUpdates = true;
            redealForEnsureMovability();

            ensureMovability.start();
        } else {
            redeal();
        }
    }

    public void setWon(boolean value){
        won = value;
    }

    /**
     * starts a new game, but with the same deal.
     */
    public void redealForEnsureMovability() {

        for (Stack stack : stacks) {
            stack.reset();
        }

        for (Card card : randomCards) {
            currentGame.getDealStack().addCard(card);
            card.flipDown();
        }

        //to reset the recycle counter
        currentGame.reset();
        currentGame.dealNewGame();
    }

    /**
     * starts a new game, but with the same deal.
     */
    public void redeal() {
        //reset EVERYTHING
        if (!won) {                                                                                 //if the game has been won, the score was already saved
            incrementPlayedGames();
            scores.addNewScore(movedFirstCard || currentGame.saveRecentScore());
            currentGame.onGameEnd();
        }

        currentGame.reset();
        animate.reset();
        scores.reset();
        movingCards.reset();
        recordList.reset();
        timer.reset();
        autoComplete.hideButton();

        for (Stack stack : stacks) {
            stack.reset();
        }

        //Put cards to the specified "deal from" stack. (=main stack if the game has one, else specify it in the game)
        for (Card card : randomCards) {
            if (won) {
                card.setLocationWithoutMovement(currentGame.getDealStack().getX(), currentGame.getDealStack().getY());
            } else {
                card.setLocation(currentGame.getDealStack().getX(), currentGame.getDealStack().getY());
            }

            currentGame.getDealStack().addCard(card);
            card.flipDown();
        }

        movedFirstCard = false;
        won = false;
        wonAndReloaded = false;

        if (stopUiUpdates) {
            //no need to wait in the handler when stopUiUpdates is true
            currentGame.dealNewGame();
        } else {
            //deal the cards from the game!
            dealCards.start();
        }
    }

    /**
     * in case the current game is won: save the score and start the win animation. The record list
     * is reseted, so the player can't revert card movements after the animation
     */
    public void testIfWon() {
        if (!won && !autoComplete.isRunning() && ((prefs.isDeveloperOptionInstantWinEnabled() && movedFirstCard) || currentGame.winTest())) {
            incrementPlayedGames();
            incrementNumberWonGames();
            scores.updateBonus();
            scores.addNewScore(movedFirstCard);
            recordList.reset();
            timer.setWinningTime();
            autoComplete.hideButton();
            animate.winAnimation();
            won = true;
            currentGame.onGameEnd();
        }
    }

    /**
     * Randomizes a given card array using the Fisher–Yates shuffle
     *
     * @param array The array to randomize
     */
    public void randomize(Card[] array) {
        int index;
        Card dummy;
        Random random = getPrng();

        int counter;

        //swap first card outside the loop
        index = random.nextInt(array.length);
        dummy = array[array.length-1];
        array[array.length-1] = array[index];
        array[index] = dummy;

        for (int i = array.length - 2; i > 0; i--) {
            if (prefs.getSavedUseTrueRandomisation()){
                index = random.nextInt(i+1);
            } else {
                //choose a new card as long the chosen card is too similar to the previous card in the array
                //(same value or color) also limit the loop to max 10 iterations to avoid infinite loops
                counter = 0;

                do {
                    index = random.nextInt(i + 1);
                    counter++;
                }
                while ((array[index].getValue() == array[i + 1].getValue() || array[index].getColor() == array[i + 1].getColor()) && counter < 10);
            }

            dummy = array[i];
            array[i] = array[index];
            array[index] = dummy;
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

        gm.updateLimitedRecyclesCounter();
        currentGame.mirrorTextViews(gm.layoutGame);

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
    public void toggleRecycles(boolean value) {
        currentGame.toggleRecycles(value);
        showOrHideRecycles();
    }

    /**
     * updates the recycle counter in the ui
     */
    public void showOrHideRecycles() {
        gm.updateLimitedRecyclesCounter();
    }

    public void setNumberOfRecycles(String key, String defaultValue){
        if (currentGame.hasLimitedRecycles()) {
            currentGame.setNumberOfRecycles(key, defaultValue);


            //gm.updateNumberOfRecycles();
            //gm.updateLimitedRecyclesCounter();
        }
    }

    public boolean hasWon() {
        return won;
    }

    public void deleteStatistics() {
        prefs.saveNumberOfWonGames(0);
        prefs.saveNumberOfPlayedGames(0);
    }

    private void saveRandomCards() {
        ArrayList<Integer> list = new ArrayList<>();

        for (Card card : randomCards)
            list.add(card.getId());

        prefs.saveRandomCards(list);
    }

    private void loadRandomCards() {
        ArrayList<Integer> list = prefs.getSavedRandomCards();

        for (int i = 0; i < randomCards.length; i++)
            randomCards[i] = cards[list.get(i)];
    }

    private void incrementPlayedGames() {
        if (movedFirstCard) {
            prefs.saveNumberOfPlayedGames(prefs.getSavedNumberOfPlayedGames() + 1);
        }
    }

    public void incrementNumberWonGames(){
        prefs.saveNumberOfWonGames(prefs.getSavedNumberOfWonGames()+1);
    }

    /**
     * Tests if movements shouldn't be allowed. For example. If a hint is currently shown, don't
     * accept input, or otherwise something will go wrong
     *
     * @return True if no movement is allowed, false otherwise
     */
    public boolean stopConditions() {
        return (autoComplete.isRunning() || animate.cardIsAnimating() || hint.isRunning()
                || recordList.isWorking() || autoMove.isRunning() || isDialogVisible);
    }
}
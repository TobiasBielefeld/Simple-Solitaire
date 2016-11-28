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
 * Contains stuff for the game which i didn't know where i should put it.
 */

public class GameLogic{

    public Card[] randomCards;                                                                      //array to shuffle the cards
    private int numberWonGames;                                                                     //number of won games. It's shown in the high score activity
    private boolean won;                                                                            //shows if the player has won, needed to know if the timer can stop, or to deal new cards on game start
    private GameManager gm;

    public GameLogic(GameManager gm){
        this.gm = gm;
    }

    public void save() {
        /*
         * save the current game (called in onPause, because onDestroy is not always called
         * when the app closes for some reason
         */
        scores.save();
        recordList.save();
        putBoolean(GAME_WON, won);
        putInt(GAME_NUMBER_OF_WON_GAMES, numberWonGames);
        /* Timer will be saved in onPause() */
        for (Stack stack : stacks)
            stack.save();

        Card.save();
        saveRandomCards();
    }

    public void load() {
        /*
         * load everything saved on start of a game. If the last game has been won put every card
         * outside the screen.
         * The main loading part is put in a try catch block, so when there goes something wrong
         * on saving/loading, it won't crash the game. (in that case, it loads a new game
         */
        boolean first_run = getBoolean(GAME_FIRST_RUN, true);
        numberWonGames = getInt(GAME_NUMBER_OF_WON_GAMES, 0);
        won = getBoolean(GAME_WON, false);
        //update and reset
        Card.updateCardDrawableChoice();
        Card.updateCardBackgroundChoice();
        animate.reset();
        autoComplete.reset();

        if (first_run) {
            newGame();
            putBoolean(GAME_FIRST_RUN, false);
        }
        else if (won) {
            scores.load();

            loadRandomCards();

            for (Card card : cards)
                card.setLocationWithoutMovement(gm.layoutGame.getWidth(), 0);
        }
        else {
            try {
                for (Card card : cards) {
                    card.setLocationWithoutMovement(currentGame.dealFromStack().view.getX(), currentGame.dealFromStack().view.getY());
                    card.flipDown();
                }

                scores.load();
                recordList.load();
                timer.setCurrentTime(getLong(TIMER_CURRENT_TIME,0));
                //timer will be loaded in onResume()
                for (Stack stack : stacks)
                    stack.load();

                Card.load();
                loadRandomCards();

                if (! autoComplete.buttonIsShown() && currentGame.autoCompleteStartTest()) {
                    autoComplete.showButton();
                }
            } catch (Exception e) {
                Log.e("Loading data failed", e.toString());
                gm.showToast(gm.getString(R.string.game_load_error));
                newGame();
            }
        }
    }

    public void newGame() {
        //new game is like a redeal, but with new randomized cards
        randomCards = cards.clone();
        randomize(randomCards);

        redeal();
    }

    public void redeal(){
        //reset EVERYTHING
        won = false;
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
        if (!won && ! autoComplete.isRunning() && currentGame.winTest()) {
            scores.updateBonus();
            won = true;
            numberWonGames++;

            scores.addNewHighScore();
            recordList.reset();
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

    public void mirrorStacks(){
        /*
         * for left handed mode: mirrors the stacks to the other side and then updates the card
         * positions.
         */
        if (stacks!=null) {
            for (Stack stack : stacks) {
                stack.view.setX(gm.layoutGame.getWidth() - stack.view.getX() - Card.width);

                for (int j = 0; j < stack.getSize(); j++) {
                    Card card = stack.getCard(j);
                    card.setLocationWithoutMovement(gm.layoutGame.getWidth() -
                            card.view.getX() - Card.width, card.view.getY());
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

    public void deleteNumberWonGames() {
        numberWonGames = 0;
    }

    private void saveRandomCards(){
        ArrayList<Integer> list = new ArrayList<>();

        for (Card card: randomCards)
            list.add(card.getID());

        putIntList(GAME_RANDOM_CARDS,list);
    }

    private void loadRandomCards(){
        ArrayList<Integer> list = getIntList(GAME_RANDOM_CARDS);

        for (int i=0;i<randomCards.length;i++)
            randomCards[i] = cards[list.get(i)];
    }



}

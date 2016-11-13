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

package de.tobiasbielefeld.solitaire;

import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.ui.Main;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.helper.Animate;
import de.tobiasbielefeld.solitaire.helper.AutoComplete;
import de.tobiasbielefeld.solitaire.helper.Game;
import de.tobiasbielefeld.solitaire.helper.Hint;
import de.tobiasbielefeld.solitaire.helper.MovingCards;
import de.tobiasbielefeld.solitaire.helper.RecordList;
import de.tobiasbielefeld.solitaire.helper.Scores;
import de.tobiasbielefeld.solitaire.helper.Timer;

/*
 * static data which is shared across the whole project, i like this more than passing stuff around
 */

public class SharedData {

    public final static int OPTION_UNDO = 1, OPTION_NO_RECORD = 2;

    public static Card[] cards = new Card[52];                                                      //array for all cards
    public static Stack[] stacks = new Stack[13];                                                   //array for the stacks, where the cards are placed
    public static MovingCards movingCards = new MovingCards();                                      //maintains the cards which the player moves
    public static RecordList recordList = new RecordList();                                         //maintains the records which the player can undo
    public static Scores scores = new Scores();                                                     //maintains all the stuff around scoring
    public static Game game = new Game();                                                           //maintains game loading, saving and other stuff
    public static Animate animate = new Animate();                                                  //all animations are found here
    public static Hint hint = new Hint();                                                           //shows movable cards as a hint
    public static AutoComplete autoComplete = new AutoComplete();                                   //automatically completes the game
    public static Timer timer = new Timer();                                                        //maintains the current time of a game

    public static SharedPreferences savedData;                                                      //sharedPref to load and save SharedData
    public static SharedPreferences.Editor editor;                                                  //editor to get rid off some faulty behavior
    private static Toast toast;
    public static Main mainActivity;                                                                //used to call getString or something like that from the helper classes

    public static void moveToStack(Card card, Stack destination) {                                  //single card movement without option
        moveToStack(card, destination, 0);                                                          //call moveToStack with option 0
    }

    public static void moveToStack(Card card, Stack destination, int option) {                      //single card movement with option
        ArrayList<Card> cards = new ArrayList<>();                                                  //creates a new array
        cards.add(card);                                                                            //fill it with one card
        moveToStack(cards, destination, option);                                                    //and call the Main move function
    }

    public static void moveToStack(ArrayList<Card> cards, Stack destination) {                      //multiple card movement without option
        moveToStack(cards, destination, 0);                                                         //call moveToStack with option 0
    }

    public static void moveToStack(ArrayList<Card> cards, Stack destination, int option) {          //multiple card movement
        if (option == OPTION_UNDO)                                                                  //if the movement is a undo, use scores.undo() and no record
            scores.undo(cards, destination);
        else if (option != OPTION_NO_RECORD) {                                                      //if a record is wanted
            scores.move(cards, destination);                                                        //update the scores
            recordList.add(cards);                                                                  //and set a record
        }

        for (Card card : cards) {                                                                   //loop through every card
            if (card.getStack() == destination)                                                     //if its already on the destination stack...
                card.flip();                                                                        //...just flip it
            else {                                                                                  //else move it
                card.getStack().removeCard(card);                                                   //remove from old stack
                destination.addCard(card);                                                          //and add to destination
            }
        }

        game.testIfWon();                                                                           //also test if the player has won
    }

    public static void showToast(String text) {                                                     //simple function to show a new toast text
        if (toast == null)
            toast = Toast.makeText(mainActivity, text, Toast.LENGTH_SHORT);                         //initialize toast
        else
            toast.setText(text);

        toast.show();
    }
}
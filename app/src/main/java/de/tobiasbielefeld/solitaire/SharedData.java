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
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import de.tobiasbielefeld.solitaire.games.Game;
import de.tobiasbielefeld.solitaire.handler.TestAfterMoveHandler;
import de.tobiasbielefeld.solitaire.handler.TestIfWonHandler;
import de.tobiasbielefeld.solitaire.helper.GameLogic;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.helper.Animate;
import de.tobiasbielefeld.solitaire.helper.AutoComplete;
import de.tobiasbielefeld.solitaire.helper.Hint;
import de.tobiasbielefeld.solitaire.helper.MovingCards;
import de.tobiasbielefeld.solitaire.helper.RecordList;
import de.tobiasbielefeld.solitaire.helper.Scores;
import de.tobiasbielefeld.solitaire.helper.Timer;

/*
 * static data which is shared across the whole project, i like this more than passing stuff around
 */

public class SharedData {

    //Strings
    public final static String SCORE = "Score";
    public final static String SAVED_SCORES = "SavedScores";

    public final static String GAME_REDEAL_COUNT = "GameRedealCount";
    public final static String GAME_WON = "GameWon";
    public final static String GAME_NUMBER_OF_WON_GAMES = "GameNumberOfWonGames";
    public final static String GAME_NUMBER_OF_PLAYED_GAMES = "GameNumberOfPlayedGames";


    public final static String GAME_RANDOM_CARDS = "GameRandomCards";
    public final static String GAME_FIRST_RUN = "GameFirstRun";

    public final static String RECORD_LIST_ENTRY = "RecordListEntry";
    public final static String RECORD_LIST_ENTRIES_SIZE = "RecordListEntriesSize";
    public final static String FLIP_CARD = "FlipCard";
    public final static String ORIGIN = "Origin";
    public final static String CARD = "Card";
    public final static String CARDS = "Cards";
    public final static String STACK = "Stack";

    public final static String TIMER_CURRENT_TIME = "SavedCurrentTime";
    public final static String TIMER_START_TIME = "SavedStartTime";
    public final static String TIMER_SHOWN_TIME = "SavedShownTime";

    final public static String CARD_DRAWABLES = "CardDrawables";
    final public static String CARD_BACKGROUND = "CardBackground";
    final public static String MENU_COLUMNS_PORTRAIT = "MenuColumnsPortrait";
    final public static String MENU_COLUMNS_LANDSCAPE = "MenuColumnsLandscape";

    public final static int OPTION_UNDO = 1, OPTION_NO_RECORD = 2, OPTION_REVERSED_RECORD = 3;

    public static Card[] cards;
    public static Stack[] stacks;
    public static RecordList recordList;
    public static Scores scores;
    public static MovingCards movingCards;
    public static GameLogic gameLogic;
    public static Animate animate;
    public static Hint hint;
    public static AutoComplete autoComplete;
    public static Timer timer;
    public static LoadGame lg = new LoadGame();

    public static SharedPreferences savedSharedData;
    public static SharedPreferences savedGameData;
    public static Game currentGame;

    public static TestAfterMoveHandler testAfterMoveHandler = new TestAfterMoveHandler();
    public static TestIfWonHandler testIfWonHandler = new TestIfWonHandler();

    /*
     * a lot of overloaded versions for moveToStack(), because it can be called with one card, card
     * array, destination stack, destination stack array, option, no option and a combination
     * of everything
     *
     * But everyone uses the "main" version of moveToStack(), the last method
     */

    public static void moveToStack(Card card, Stack destination) {
        moveToStack(card, destination, 0);
    }

    public static void moveToStack(Card card, Stack destination, int option) {
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(card);

        ArrayList<Stack> destinations = new ArrayList<>();
        destinations.add(destination);

        moveToStack(cards, destinations, option);
    }

    public static void moveToStack(ArrayList<Card> cards, Stack destination) {
        moveToStack(cards, destination, 0);
    }

    public static void moveToStack(ArrayList<Card> cards, Stack destination, int option) {
        ArrayList<Stack> destinations = new ArrayList<>();

        for (int i = 0; i < cards.size(); i++)
            destinations.add(destination);

        moveToStack(cards, destinations, option);
    }

    public static void moveToStack(ArrayList<Card> cards, ArrayList<Stack> destinations) {
        moveToStack(cards, destinations, 0);
    }

    public static void moveToStack(ArrayList<Card> cards, ArrayList<Stack> destinations, int option) {
        /*
         * moves a card to a stack by doing this:
         * - change the score according to the cards
         * - add the cards to the record list
         * - move every card one by one
         * - bring the moving cards to front
         * - and start handlers to call some methods
         */

        if (option == OPTION_UNDO)
            scores.undo(cards, destinations);
        else if (option == 0) {
            scores.move(cards, destinations);
            recordList.add(cards);
        } else if (option == OPTION_REVERSED_RECORD) {
            //reverse the cards and add the reversed list to the record
            ArrayList<Card> cardsReversed = new ArrayList<>();

            for (int i = 0; i < cards.size(); i++)
                cardsReversed.add(cards.get(cards.size() - 1 - i));

            recordList.add(cardsReversed);
            scores.move(cards, destinations);
        }

        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getStack() == destinations.get(i))                                     //this means to flip a card
                cards.get(i).flip();
            else {
                cards.get(i).getStack().removeCard(cards.get(i));
                destinations.get(i).addCard(cards.get(i));
            }
        }

        for (Card card : cards) {
            card.view.bringToFront();
        }

        /*
         * following stuff in handlers, because they should wait until possible card
         * movements are over.
         */
        if (option == 0) {
            testAfterMoveHandler.sendEmptyMessageDelayed(0, 100);
            testIfWonHandler.sendEmptyMessageDelayed(0, 200);
        }
    }

    /*
     * some getters and setters for saving and loading stuff in the games.
     * "savedGameData" is a different sharedPreference for every game
     */

    public static void putIntList(String name, List<Integer> list) {
        //thanks to this answer for this idea http://stackoverflow.com/a/11201225/7016229
        String s = "";
        for (int i : list) {
            s += i + ",";
        }
        savedGameData.edit().putString(name, s).apply();
    }

    public static ArrayList<Integer> getIntList(String name) {
        //thanks to this answer for this idea http://stackoverflow.com/a/11201225/7016229
        String s = savedGameData.getString(name, "");
        StringTokenizer st = new StringTokenizer(s, ",");
        ArrayList<Integer> result = new ArrayList<>();

        while (st.hasMoreTokens()) {
            result.add(Integer.parseInt(st.nextToken()));
        }

        return result;
    }

    public static void putLongList(String name, List<Long> list) {
        //thanks to this answer for this idea http://stackoverflow.com/a/11201225/7016229
        String s = "";
        for (long i : list) {
            s += i + ",";
        }
        savedGameData.edit().putString(name, s).apply();
    }

    public static ArrayList<Long> getLongList(String name) {
        //thanks to this answer for this idea http://stackoverflow.com/a/11201225/7016229
        String s = savedGameData.getString(name, "");
        StringTokenizer st = new StringTokenizer(s, ",");
        ArrayList<Long> result = new ArrayList<>();

        while (st.hasMoreTokens()) {
            result.add(Long.parseLong(st.nextToken()));
        }

        return result;
    }

    public static Long getLong(String name, long defaultValue) {
        return savedGameData.getLong(name, defaultValue);
    }

    public static int getInt(String name, int defaultValue) {
        return savedGameData.getInt(name, defaultValue);
    }

    public static boolean getBoolean(String name, boolean defaultValue) {
        return savedGameData.getBoolean(name, defaultValue);
    }

    public static String getString(String name, String defaultValue) {
        return savedGameData.getString(name, defaultValue);
    }

    public static void putLong(String name, long value) {
        savedGameData.edit().putLong(name, value).apply();
    }

    public static void putInt(String name, int value) {
        savedGameData.edit().putInt(name, value).apply();
    }

    public static void putBoolean(String name, boolean value) {
        savedGameData.edit().putBoolean(name, value).apply();
    }

    public static void putString(String name, String value) {
        savedGameData.edit().putString(name, value).apply();
    }

    /*
     * getters and setters for shared settings, which are used in every game
     * like the orientation setting
     */

    public static int getSharedInt(String name, int defaultValue) {
        return savedSharedData.getInt(name, defaultValue);
    }

    public static String getSharedString(String name, String defaultValue) {
        return savedSharedData.getString(name, defaultValue);
    }

    public static boolean getSharedBoolean(String name, boolean defaultValue) {
        return savedSharedData.getBoolean(name, defaultValue);
    }

    public static void putSharedInt(String name, int value) {
        savedSharedData.edit().putInt(name, value).apply();
    }

    public static void putSharedString(String name, String value) {
        savedSharedData.edit().putString(name, value).apply();
    }


    public static void putSharedIntList(String name, List<Integer> list) {
        //thanks to this answer for this idea http://stackoverflow.com/a/11201225/7016229
        String s = "";
        for (int i : list) {
            s += i + ",";
        }
        savedSharedData.edit().putString(name, s).apply();
    }

    public static ArrayList<Integer> getSharedIntList(String name) {
        //thanks to this answer for this idea http://stackoverflow.com/a/11201225/7016229
        String s = savedSharedData.getString(name, "");
        StringTokenizer st = new StringTokenizer(s, ",");
        ArrayList<Integer> result = new ArrayList<>();

        while (st.hasMoreTokens()) {
            result.add(Integer.parseInt(st.nextToken()));
        }

        return result;
    }

    /*
     *  Some methods I use in a lot of different files
     */

    public static void logText(String text) {
        Log.e("hey", text);
    }

    public static int min(int value1, int value2) {
        return value1 < value2 ? value1 : value2;
    }

    public static float min(float value1, float value2) {
        return value1 < value2 ? value1 : value2;
    }

    public static int max(int value1, int value2) {
        return value1 > value2 ? value1 : value2;
    }

    public static float max(float value1, float value2) {
        return value1 > value2 ? value1 : value2;
    }


}
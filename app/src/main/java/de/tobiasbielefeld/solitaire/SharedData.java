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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import de.tobiasbielefeld.solitaire.games.Game;
import de.tobiasbielefeld.solitaire.handler.TestAfterMoveHandler;
import de.tobiasbielefeld.solitaire.handler.TestIfWonHandler;
import de.tobiasbielefeld.solitaire.helper.Bitmaps;
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
    public static String SCORE;
    public static String SAVED_SCORES;
    public static String OLD;

    public static String GAME;
    public static String GAME_REDEAL_COUNT;
    public static String GAME_WON;
    public static String GAME_NUMBER_OF_WON_GAMES;
    public static String GAME_NUMBER_OF_PLAYED_GAMES;
    public static String GAME_RANDOM_CARDS;
    public static String GAME_FIRST_RUN;
    public static String GAME_MOVED_FIRST_CARD;

    public static String RECORD_LIST_ENTRY;
    public static String RECORD_LIST_ENTRIES_SIZE;
    public static String FLIP_CARD;
    public static String ORIGIN;
    public static String CARD;
    public static String CARDS;
    public static String STACK;

    public static String TIMER_CURRENT_TIME;
    public static String TIMER_START_TIME;
    public static String TIMER_SHOWN_TIME;

    public static String CARD_DRAWABLES;
    public static String CARD_BACKGROUND;
    public static String MENU_COLUMNS_PORTRAIT;
    public static String MENU_COLUMNS_LANDSCAPE;

    public static String CANFIELD_START_CARD_VALUE;
    public static String BACKGROUND_COLOR_DEFAULT;
    public static String RESTART_DIALOG;

    public static String PREF_KEY_YUKON_RULES;
    public static String PREF_KEY_YUKON_RULES_OLD;
    public static String PREF_KEY_FORTY_EIGHT_LIMITED_REDEALS;
    public static String PREF_KEY_KLONDIKE_DRAW;
    public static String PREF_KEY_KLONDIKE_DRAW_OLD;
    public static String PREF_KEY_GOLF_CYCLIC;
    public static String PREF_KEY_CANFIELD_DRAW;
    public static String PREF_KEY_CANFIELD_DRAW_OLD;
    public static String PREF_KEY_PYRAMID_LIMITED_REDEALS;
    public static String PREF_KEY_PYRAMID_DIFFICULTY;
    public static String PREF_KEY_SPIDER_DIFFICULTY;
    public static String PREF_KEY_SPIDER_DIFFICULTY_OLD;
    public static String PREF_KEY_LANGUAGE;
    public static String PREF_KEY_CURRENT_GAME;
    public static String PREF_KEY_ORIENTATION;
    public static String PREF_KEY_MENU_GAMES;
    public static String PREF_KEY_4_COLOR_MODE;

    public static String DEFAULT_CANFIELD_DRAW;
    public static String DEFAULT_KLONDIKE_DRAW;
    public static String DEFAULT_YUKON_RULES;
    public static String DEFAULT_MENU_BAR_POSITION_LANDSCAPE;
    public static String DEFAULT_MENU_BAR_POSITION_PORTRAIT;
    public static String DEFAULT_ICON_THEME;
    public static String DEFAULT_PYRAMID_DIFFICULTY;
    public static String DEFAULT_SPIDER_DIFFICULTY;
    public static String DEFAULT_LANGUAGE;
    public static String DEFAULT_MENU_COLUMNS_LANDSCAPE;
    public static String DEFAULT_MENU_COLUMNS_PORTRAIT;
    public static String DEFAULT_ORIENTATION;

    public static int DEFAULT_CURRENT_GAME;

    public static boolean DEFAULT_PYRAMID_LIMITED_REDEALS;
    public static boolean DEFAULT_GOLF_CYCLIC;
    public static boolean DEFAULT_FORTY_EIGHT_LIMITED_REDEALS;
    public static boolean DEFAULT_LEFT_HANDED_MODE;
    public static boolean DEFAULT_DOUBLE_TAP_ENABLE;
    public static boolean DEFAULT_DOUBLE_TAP_ALL_CARDS;
    public static boolean DEFAULT_WON;
    public static boolean DEFAULT_FIRST_RUN;
    public static boolean DEFAULT_MOVED_FIRST_CARD;
    public static boolean DEFAULT_4_COLOR_MODE;

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
    public static Bitmaps bitmaps = new Bitmaps();

    public static SharedPreferences savedSharedData;
    public static SharedPreferences savedGameData;
    public static Game currentGame;

    public static TestAfterMoveHandler testAfterMoveHandler = new TestAfterMoveHandler();
    public static TestIfWonHandler testIfWonHandler = new TestIfWonHandler();

    public static int NUMBER_OF_CARD_BACKGROUNDS;
    public static int NUMBER_OF_CARD_THEMES;

    //load the values from the xml files
    public static void loadStrings(Context context){
        Resources res = context.getResources();

        GAME = context.getString(R.string.game);

        PREF_KEY_YUKON_RULES = res.getString(R.string.pref_key_yukon_rules);
        PREF_KEY_KLONDIKE_DRAW = res.getString(R.string.pref_key_klondike_draw);
        PREF_KEY_CANFIELD_DRAW = res.getString(R.string.pref_key_canfield_draw);
        PREF_KEY_YUKON_RULES_OLD = PREF_KEY_YUKON_RULES + OLD;
        PREF_KEY_KLONDIKE_DRAW_OLD = PREF_KEY_KLONDIKE_DRAW + OLD;
        PREF_KEY_CANFIELD_DRAW_OLD = PREF_KEY_CANFIELD_DRAW + OLD;
        PREF_KEY_FORTY_EIGHT_LIMITED_REDEALS = res.getString(R.string.pref_key_forty_eight_limited_redeals);
        PREF_KEY_GOLF_CYCLIC = res.getString(R.string.pref_key_golf_cyclic);
        PREF_KEY_PYRAMID_LIMITED_REDEALS = res.getString(R.string.pref_key_pyramid_limited_redeals);
        PREF_KEY_PYRAMID_DIFFICULTY = res.getString(R.string.pref_key_pyramid_difficulty);
        PREF_KEY_SPIDER_DIFFICULTY = res.getString(R.string.pref_key_language);
        PREF_KEY_SPIDER_DIFFICULTY_OLD = PREF_KEY_SPIDER_DIFFICULTY + OLD;
        PREF_KEY_LANGUAGE = res.getString(R.string.pref_key_language);
        PREF_KEY_CURRENT_GAME = res.getString(R.string.pref_key_current_game);
        PREF_KEY_MENU_GAMES = res.getString(R.string.pref_key_menu_games);
        PREF_KEY_ORIENTATION = res.getString(R.string.pref_key_orientation);
        PREF_KEY_4_COLOR_MODE = res.getString(R.string.pref_key_4_color_mode);

        DEFAULT_PYRAMID_DIFFICULTY = res.getStringArray(R.array.pref_pyramid_difficulty_values)[0];
        DEFAULT_LANGUAGE = res.getStringArray(R.array.pref_language_values)[0];
        DEFAULT_SPIDER_DIFFICULTY = res.getStringArray(R.array.pref_spider_difficulty_values)[0];
        DEFAULT_ORIENTATION = res.getStringArray(R.array.pref_orientation_values)[0];
        DEFAULT_DOUBLE_TAP_ALL_CARDS = res.getBoolean(R.bool.default_double_tap_all_cards);
        DEFAULT_DOUBLE_TAP_ENABLE = res.getBoolean(R.bool.default_double_tap_enable);
        DEFAULT_LEFT_HANDED_MODE = res.getBoolean(R.bool.default_left_handed_mode);
        DEFAULT_PYRAMID_LIMITED_REDEALS = res.getBoolean(R.bool.default_pyramid_limited_redeals);
        DEFAULT_GOLF_CYCLIC = res.getBoolean(R.bool.default_golf_cyclic);
        DEFAULT_FORTY_EIGHT_LIMITED_REDEALS = res.getBoolean(R.bool.default_forty_eight_limited_redeals);
        DEFAULT_CURRENT_GAME = res.getInteger(R.integer.default_current_game);
        DEFAULT_MENU_COLUMNS_LANDSCAPE = res.getString(R.string.default_menu_columns_landscape);
        DEFAULT_MENU_COLUMNS_PORTRAIT = res.getString(R.string.default_menu_columns_portrait);
        DEFAULT_MENU_BAR_POSITION_LANDSCAPE = res.getString(R.string.default_menu_bar_position_landscape);
        DEFAULT_MENU_BAR_POSITION_PORTRAIT = res.getString(R.string.default_menu_bar_position_portrait);
        DEFAULT_FIRST_RUN = res.getBoolean(R.bool.default_first_run);
        DEFAULT_WON = res.getBoolean(R.bool.default_won);
        DEFAULT_MOVED_FIRST_CARD = res.getBoolean(R.bool.default_moved_first_card);
        DEFAULT_4_COLOR_MODE = res.getBoolean(R.bool.default_4_color_mode);

        DEFAULT_YUKON_RULES = res.getStringArray(R.array.pref_yukon_rules_values)[0];
        DEFAULT_KLONDIKE_DRAW = res.getStringArray(R.array.pref_klondike_draw_values)[0];
        DEFAULT_CANFIELD_DRAW = res.getStringArray(R.array.pref_canfield_draw_values)[1];
        DEFAULT_ICON_THEME = res.getStringArray(R.array.pref_icon_theme_values)[0];

        GAME_REDEAL_COUNT = res.getString(R.string.game_redeal_count);
        GAME_WON = res.getString(R.string.game_won);
        GAME_NUMBER_OF_WON_GAMES = res.getString(R.string.game_number_of_won_games);
        GAME_NUMBER_OF_PLAYED_GAMES = res.getString(R.string.game_number_of_played_games);
        GAME_RANDOM_CARDS = res.getString(R.string.game_random_cards);
        GAME_FIRST_RUN = res.getString(R.string.game_first_run);
        GAME_MOVED_FIRST_CARD = res.getString(R.string.game_moved_first_card);

        BACKGROUND_COLOR_DEFAULT = res.getStringArray(R.array.pref_background_colors_values)[1];
        RESTART_DIALOG = res.getString(R.string.restart_dialog);
        CANFIELD_START_CARD_VALUE = res.getString(R.string.canfield_start_value);
        SCORE = res.getString(R.string.score);
        SAVED_SCORES = res.getString(R.string.saved_scores);

        RECORD_LIST_ENTRY = res.getString(R.string.record_list_entry);
        RECORD_LIST_ENTRIES_SIZE = res.getString(R.string.record_list_entries_size);
        FLIP_CARD = res.getString(R.string.flip_card);
        ORIGIN = res.getString(R.string.origin);
        CARD = res.getString(R.string.card);
        CARDS = res.getString(R.string.cards);
        STACK = res.getString(R.string.stack);

        TIMER_CURRENT_TIME = res.getString(R.string.saved_current_time);
        TIMER_START_TIME = res.getString(R.string.saved_start_time);
        TIMER_SHOWN_TIME = res.getString(R.string.saved_shown_time);

        CARD_DRAWABLES = res.getString(R.string.pref_key_card_drawables);
        CARD_BACKGROUND = res.getString(R.string.pref_key_card_background);
        MENU_COLUMNS_PORTRAIT = res.getString(R.string.pref_key_menu_columns_portrait);
        MENU_COLUMNS_LANDSCAPE = res.getString(R.string.pref_key_menu_columns_landscape);

        NUMBER_OF_CARD_BACKGROUNDS = res.getInteger(R.integer.number_of_card_backgrounds);
        NUMBER_OF_CARD_THEMES = res.getInteger(R.integer.number_of_card_themes);
    }

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

    //test if saved value equals the default value
    public static boolean sharedStringEquals(String name, String defaultValue) {
        return savedSharedData.getString(name, defaultValue).equals(defaultValue);
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
package de.tobiasbielefeld.solitaire.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.Card;

import static android.content.Context.MODE_PRIVATE;
import static de.tobiasbielefeld.solitaire.SharedData.lg;
import static de.tobiasbielefeld.solitaire.SharedData.prefs;
import static de.tobiasbielefeld.solitaire.helper.Scores.MAX_SAVED_SCORES;

/**
 * Handles all the preference stuff
 */

public class Preferences {

    private SharedPreferences savedSharedData;
    private SharedPreferences savedGameData;

    //Strings
    public static String PREF_KEY_NEXT_CARD_VALUES;
    public static String PREF_KEY_HIDE_STATUS_BAR;
    public static String PREF_KEY_LONGEST_RUN;
    public static String PREF_KEY_RUN_COUNTER;
    public static String PREF_KEY_ORDER;
    public static String PREF_KEY_SCORE;
    public static String PREF_KEY_SAVED_SCORES;

    public static String PREF_KEY_SAVED_RECENT_SCORES;
    public static String PREF_KEY_TOTAL_NUMBER_UNDOS;
    public static String PREF_KEY_TOTAL_HINTS_SHOWN;
    public static String PREF_KEY_TOTAL_POINTS_EARNED;
    public static String PREF_KEY_TOTAL_TIME_PLAYED;
    public static String PREF_KEY_DEALING_CARDS;
    public static String PREF_KEY_HIDE_MENU_BUTTON;
    public static String OLD;

    public static String PREF_KEY_GAME_LAYOUT_MARGINS_PORTRAIT;
    public static String PREF_KEY_ENSURE_MOVABILITY;
    public static String PREF_KEY_ENSURE_MOVABILITY_MIN_MOVES;
    public static String PREF_KEY_SETTINGS_ONLY_FOR_THIS_GAME;
    public static String PREF_KEY_GAME_LAYOUT_MARGINS_LANDSCAPE;
    public static String PREF_KEY_DISABLE_UNDO_COSTS;
    public static String PREF_KEY_DISABLE_HINT_COSTS;
    public static String PREF_KEY_VEGAS_OLD_SCORE;
    public static String PREF_KEY_VEGAS_TIME;
    public static String PREF_KEY_GAME_REDEAL_COUNT;
    public static String PREF_KEY_GAME_WON;
    public static String PREF_KEY_GAME_WON_AND_RELOADED;
    public static String PREF_KEY_GAME_NUMBER_OF_WON_GAMES;
    public static String PREF_KEY_GAME_NUMBER_OF_PLAYED_GAMES;
    public static String PREF_KEY_GAME_RANDOM_CARDS;
    public static String PREF_KEY_GAME_FIRST_RUN;
    public static String PREF_KEY_GAME_MOVED_FIRST_CARD;
    public static String PREF_KEY_RECORD_LIST_ENTRY;
    public static String PREF_KEY_RECORD_LIST_ENTRIES_SIZE;
    public static String PREF_KEY_FLIP_CARD;
    public static String PREF_KEY_ORIGIN;
    public static String PREF_KEY_IMMERSIVE_MODE;
    public static String PREF_KEY_CARD;
    public static String PREF_KEY_CARDS;
    public static String PREF_KEY_STACK;
    public static String PREF_KEY_TIMER_END_TIME;
    public static String PREF_KEY_TIMER_START_TIME;
    public static String PREF_KEY_TIMER_WINNING_TIME;
    public static String PREF_KEY_CARD_DRAWABLES;
    public static String PREF_KEY_CARD_BACKGROUND;
    public static String PREF_KEY_CARD_BACKGROUND_COLOR;
    public static String PREF_KEY_MENU_COLUMNS_PORTRAIT;
    public static String PREF_KEY_MENU_COLUMNS_LANDSCAPE;
    public static String PREF_KEY_CANFIELD_START_CARD_VALUE;
    public static String PREF_KEY_START_WITH_MENU;
    public static String PREF_KEY_YUKON_RULES;
    public static String PREF_KEY_YUKON_RULES_OLD;
    public static String PREF_KEY_KLONDIKE_DRAW;
    public static String PREF_KEY_KLONDIKE_DRAW_OLD;
    public static String PREF_KEY_VEGAS_DRAW;
    public static String PREF_KEY_VEGAS_DRAW_OLD;
    public static String PREF_KEY_GOLF_CYCLIC;
    public static String PREF_KEY_CANFIELD_DRAW;
    public static String PREF_KEY_CANFIELD_DRAW_OLD;
    public static String PREF_KEY_PYRAMID_DIFFICULTY;
    public static String PREF_KEY_SPIDER_DIFFICULTY;
    public static String PREF_KEY_SPIDER_DIFFICULTY_OLD;
    public static String PREF_KEY_LANGUAGE;
    public static String PREF_KEY_CURRENT_GAME;
    public static String PREF_KEY_ORIENTATION;
    public static String PREF_KEY_MENU_GAMES;
    public static String PREF_KEY_4_COLOR_MODE;
    public static String PREF_KEY_LEFT_HANDED_MODE;
    public static String PREF_KEY_MENU_BAR_POS_PORTRAIT;
    public static String PREF_KEY_MENU_BAR_POS_LANDSCAPE;
    public static String PREF_KEY_DOUBLE_TAP_ENABLED;
    public static String PREF_KEY_DOUBLE_TAP_ALL_CARDS;
    public static String PREF_KEY_DOUBLE_TAP_FOUNDATION_FIRST;
    public static String PREF_KEY_TAP_TO_SELECT_ENABLED;
    public static String PREF_KEY_SINGLE_TAP_SPECIAL_GAMES;
    public static String PREF_KEY_BACKGROUND_COLOR_TYPE;
    public static String PREF_KEY_BACKGROUND_COLOR;
    public static String PREF_KEY_BACKGROUND_COLOR_CUSTOM;
    public static String PREF_KEY_MOVEMENT_SPEED;
    public static String PREF_KEY_TEXT_COLOR;
    public static String PREF_KEY_SOUND_ENABLED;
    public static String PREF_KEY_WIN_SOUND;
    public static String PREF_KEY_BACKGROUND_MUSIC;
    public static String PREF_KEY_BACKGROUND_VOLUME;
    public static String PREF_KEY_PYRAMID_LIMITED_RECYCLES;
    public static String PREF_KEY_FORTYEIGHT_LIMITED_RECYCLES;
    public static String PREF_KEY_PYRAMID_NUMBER_OF_RECYCLES;
    public static String PREF_KEY_NAPOLEONSTOMB_NUMBER_OF_RECYCLES;
    public static String PREF_KEY_FORTYEIGHT_NUMBER_OF_RECYCLES;
    public static String PREF_KEY_KLONDIKE_LIMITED_RECYCLES;
    public static String PREF_KEY_KLONDIKE_NUMBER_OF_RECYCLES;
    public static String PREF_KEY_VEGAS_NUMBER_OF_RECYCLES;
    public static String PREF_KEY_VEGAS_BET_AMOUNT;
    public static String PREF_KEY_VEGAS_BET_AMOUNT_OLD;
    public static String PREF_KEY_VEGAS_WIN_AMOUNT;
    public static String PREF_KEY_VEGAS_WIN_AMOUNT_OLD;
    public static String PREF_KEY_MENU_ORDER;
    public static String PREF_KEY_AUTO_START_NEW_GAME;
    public static String PREF_KEY_FORCE_TABLET_LAYOUT;
    public static String PREF_KEY_CALCULATION_ALTERNATIVE;
    public static String PREF_KEY_CALCULATION_ALTERNATIVE_OLD;
    public static String PREF_KEY_SHOW_ADVANCED_SETTINGS;
    public static String PREF_KEY_HIDE_TIME;
    public static String PREF_KEY_HIDE_SCORE;
    public static String PREF_KEY_HIDE_AUTOCOMPLETE_BUTTON;
    public static String PREF_KEY_VEGAS_MONEY;
    public static String PREF_KEY_VEGAS_MONEY_ENABLED;
    public static String PREF_KEY_VEGAS_RESET_MONEY;
    public static String PREF_KEY_MOD3_AUTO_MOVE;
    public static String PREF_KEY_PYRAMID_AUTO_MOVE;
    public static String PREF_KEY_SINGLE_TAP_ALL_GAMES;
    public static String PREF_KEY_CANFIELD_SIZE_OF_RESERVE;
    public static String PREF_KEY_DEVELOPER_OPTION_MOVE_CARDS_EVERYWHERE;
    public static String PREF_KEY_DEVELOPER_OPTION_PLAY_EVERY_CARD;
    public static String PREF_KEY_DEVELOPER_OPTION_INSTANT_WIN;
    public static String PREF_KEY_USE_TRUE_RANDOMISATION;
    public static String PREF_KEY_DEVELOPER_OPTION_NO_SAVING;
    public static String PREF_KEY_DEVELOPER_OPTION_DEAL_CORRECT_SEQUENCES;
    public static String PREF_KEY_MAX_NUMBER_UNDOS;
    public static String PREF_KEY_SHOW_DIALOG_NEW_GAME;
    public static String PREF_KEY_SHOW_DIALOG_REDEAL;
    public static String PREF_KEY_SHOW_DIALOG_MIX_CARDS;
    public static String PREF_KEY_HIDE_MENU_BAR;
    public static String PREF_KEY_IMRPOVE_AUTO_MOVE;
    public static String DEFAULT_CANFIELD_DRAW;
    public static String DEFAULT_KLONDIKE_DRAW;
    public static String DEFAULT_VEGAS_DRAW;
    public static String DEFAULT_YUKON_RULES;
    public static String DEFAULT_MENU_BAR_POSITION_LANDSCAPE;
    public static String DEFAULT_MENU_BAR_POSITION_PORTRAIT;
    public static String DEFAULT_PYRAMID_DIFFICULTY;
    public static String DEFAULT_SPIDER_DIFFICULTY;
    public static String DEFAULT_LANGUAGE;
    public static String DEFAULT_MENU_COLUMNS_LANDSCAPE;
    public static String DEFAULT_MENU_COLUMNS_PORTRAIT;
    public static String DEFAULT_ORIENTATION;
    public static String DEFAULT_BACKGROUND_COLOR;
    public static String DEFAULT_BACKGROUND_MUSIC;
    public static String DEFAULT_PYRAMID_NUMBER_OF_RECYCLES;
    public static String DEFAULT_FORTYEIGHT_NUMBER_OF_RECYCLES;
    public static String DEFAULT_VEGAS_NUMBER_OF_RECYCLES;
    public static String DEFAULT_KLONDIKE_NUMBER_OF_RECYCLES;
    public static String DEFAULT_WIN_SOUND;
    public static String DEFAULT_MOVEMENT_SPEED;
    public static String DEFAULT_CANFIELD_SIZE_OF_RESERVE;
    public static String DEFAULT_DEVELOPER_OPTION_DEAL_CORRECT_SEQUENCES;
    public static String DEFAULT_NAPOLEONSTOMB_NUMBER_OF_RECYCLES;
    public static int DEFAULT_CURRENT_GAME;
    public static int DEFAULT_GAME_LAYOUT_MARGINS_PORTRAIT;
    public static int DEFAULT_GAME_LAYOUT_MARGINS_LANDSCAPE;
    public static int DEFAULT_CARD_BACKGROUND;
    public static int DEFAULT_CARD_BACKGROUND_COLOR;
    public static int DEFAULT_WINNING_TIME;
    public static int DEFAULT_BACKGROUND_COLOR_TYPE;
    public static int DEFAULT_BACKGROUND_VOLUME;
    public static int DEFAULT_BACKGROUND_COLOR_CUSTOM;
    public static int DEFAULT_VEGAS_BET_AMOUNT;
    public static int DEFAULT_VEGAS_WIN_AMOUNT;
    public static int DEFAULT_VEGAS_MONEY;
    public static int DEFAULT_MAX_NUMBER_UNDOS;
    public static int DEFAULT_ENSURE_MOVABILITY_MIN_MOVES;
    public static int DEFAULT_TEXT_COLOR;
    public static boolean DEFAULT_ENSURE_MOVABILITY;
    public static boolean DEFAULT_HIDE_AUTOCOMPLETE_BUTTON;
    public static boolean DEFAULT_SETTINGS_ONLY_FOR_THIS_GAME;
    public static boolean DEFAULT_HIDE_MENU_BUTTON;
    public static boolean DEFAULT_IMMERSIVE_MODE;
    public static boolean DEFAULT_DISABLE_UNDO_COSTS;
    public static boolean DEFAULT_DISABLE_HINT_COSTS;
    public static boolean DEFAULT_SHOW_DIALOG_NEW_GAME;
    public static boolean DEFAULT_SHOW_DIALOG_REDEAL;
    public static boolean DEFAULT_SHOW_DIALOG_MIX_CARDS;
    public static boolean DEFAULT_SHOW_ADVANCED_SETTINGS;
    public static boolean DEFAULT_GOLF_CYCLIC;
    public static boolean DEFAULT_LEFT_HANDED_MODE;
    public static boolean DEFAULT_DOUBLE_TAP_ENABLE;
    public static boolean DEFAULT_DOUBLE_TAP_ALL_CARDS;
    public static boolean DEFAULT_DOUBLE_TAP_FOUNDATION_FIRST;
    public static boolean DEFAULT_WON;
    public static boolean DEFAULT_HIDE_MENU_BAR;
    public static boolean DEFAULT_IMPROVE_AUTO_MOVE;
    public static boolean DEFAULT_WON_AND_RELOADED;
    public static boolean DEFAULT_FIRST_RUN;
    public static boolean DEFAULT_MOVED_FIRST_CARD;
    public static boolean DEFAULT_4_COLOR_MODE;
    public static boolean DEFAULT_TAP_TO_SELECT_ENABLED;
    public static boolean DEFAULT_SINGLE_TAP_SPECIAL_GAMES_ENABLED;
    public static boolean DEFAULT_SOUND_ENABLED;
    public static boolean DEFAULT_AUTO_START_NEW_GAME;
    public static boolean DEFAULT_FORCE_TABLET_LAYOUT;
    public static boolean DEFAULT_HIDE_TIME;
    public static boolean DEFAULT_HIDE_SCORE;
    public static boolean DEFAULT_VEGAS_MONEY_ENABLED;
    public static boolean DEFAULT_VEGAS_RESET_MONEY;
    public static boolean DEFAULT_SINGLE_TAP_ALL_GAMES;
    public static boolean DEFAULT_PYRAMID_LIMITED_RECYCLES;
    public static boolean DEFAULT_FORTYEIGHT_LIMITED_RECYCLES;
    public static boolean DEFAULT_KLONDIKE_LIMITED_RECYCLES;
    public static boolean DEFAULT_CALCULATION_ALTERNATIVE;
    public static boolean DEFAULT_MOD3_AUTO_MOVE;
    public static boolean DEFAULT_PYRAMID_AUTO_MOVE;
    public static boolean DEFAULT_DEVELOPER_OPTION_MOVE_CARDS_EVERYWHERE;
    public static boolean DEFAULT_DEVELOPER_OPTION_PLAY_EVERY_CARD;
    public static boolean DEFAULT_DEVELOPER_OPTION_INSTANT_WIN;
    public static boolean DEFAULT_DEVELOPER_OPTION_NO_SAVING;
    public static boolean DEFAULT_USE_TRUE_RANDOMISATION;

    public Preferences(Context context){
        loadStrings(context.getResources());

        savedSharedData = PreferenceManager.getDefaultSharedPreferences(context);
        setGamePreferences(context);
    }

    public void setGamePreferences(Context context){
        savedGameData = context.getSharedPreferences(lg.getSharedPrefName(), MODE_PRIVATE);

    }

    public void registerListener(SharedPreferences.OnSharedPreferenceChangeListener listener){
        savedSharedData.registerOnSharedPreferenceChangeListener(listener);

        if (savedGameData !=null){
            savedGameData.registerOnSharedPreferenceChangeListener(listener);
        }
    }

    public void unregisterListener(SharedPreferences.OnSharedPreferenceChangeListener listener){
        savedSharedData.unregisterOnSharedPreferenceChangeListener(listener);

        if (savedGameData !=null){
            savedGameData.unregisterOnSharedPreferenceChangeListener(listener);
        }
    }

    /**
     * Load the static strings, so i can use them in every file instead of writing the string itself,
     * which would be susceptible for errors. TODO manage this in a better way.
     *
     * @param res Used to load the strings
     */
    private void loadStrings(Resources res) {

        OLD = "_old";

        PREF_KEY_ENSURE_MOVABILITY = res.getString(R.string.pref_key_ensure_movability);
        PREF_KEY_ENSURE_MOVABILITY_MIN_MOVES = res.getString(R.string.pref_key_ensure_movability_min_moves);
        PREF_KEY_SETTINGS_ONLY_FOR_THIS_GAME = res.getString(R.string.pref_key_settings_only_for_this_game);
        PREF_KEY_DEALING_CARDS = "pref_key_dealing_cards";
        PREF_KEY_VEGAS_TIME = "pref_key_vegas_time";
        PREF_KEY_VEGAS_OLD_SCORE = "pref_key_vegas_old_score";
        PREF_KEY_ORDER = "order";
        PREF_KEY_LONGEST_RUN = "longest_run";
        PREF_KEY_RUN_COUNTER = "run_counter";
        PREF_KEY_NEXT_CARD_VALUES = "pref_key_next_card_values";
        PREF_KEY_START_WITH_MENU = res.getString(R.string.pref_key_start_menu);
        PREF_KEY_HIDE_STATUS_BAR = res.getString(R.string.pref_key_hide_status_bar);
        PREF_KEY_YUKON_RULES = res.getString(R.string.pref_key_yukon_rules);
        PREF_KEY_KLONDIKE_DRAW = res.getString(R.string.pref_key_klondike_draw);
        PREF_KEY_VEGAS_DRAW = res.getString(R.string.pref_key_vegas_draw);
        PREF_KEY_CANFIELD_DRAW = res.getString(R.string.pref_key_canfield_draw);
        PREF_KEY_YUKON_RULES_OLD = PREF_KEY_YUKON_RULES + OLD;
        PREF_KEY_KLONDIKE_DRAW_OLD = PREF_KEY_KLONDIKE_DRAW + OLD;
        PREF_KEY_VEGAS_DRAW_OLD = PREF_KEY_VEGAS_DRAW + OLD;
        PREF_KEY_CANFIELD_DRAW_OLD = PREF_KEY_CANFIELD_DRAW + OLD;
        PREF_KEY_GOLF_CYCLIC = res.getString(R.string.pref_key_golf_cyclic);
        PREF_KEY_PYRAMID_DIFFICULTY = res.getString(R.string.pref_key_pyramid_difficulty);
        PREF_KEY_SPIDER_DIFFICULTY = res.getString(R.string.pref_key_spider_difficulty);
        PREF_KEY_SPIDER_DIFFICULTY_OLD = PREF_KEY_SPIDER_DIFFICULTY + OLD;
        PREF_KEY_SHOW_ADVANCED_SETTINGS = res.getString(R.string.pref_key_show_advanced_settings);
        PREF_KEY_LANGUAGE = res.getString(R.string.pref_key_language);
        PREF_KEY_CURRENT_GAME = res.getString(R.string.pref_key_current_game);
        PREF_KEY_MENU_GAMES = res.getString(R.string.pref_key_menu_games);
        PREF_KEY_HIDE_MENU_BUTTON = res.getString(R.string.pref_key_hide_menu_button);
        PREF_KEY_ORIENTATION = res.getString(R.string.pref_key_orientation);
        PREF_KEY_IMRPOVE_AUTO_MOVE = res.getString(R.string.pref_key_improve_auto_move);
        PREF_KEY_4_COLOR_MODE = res.getString(R.string.pref_key_4_color_mode);
        PREF_KEY_LEFT_HANDED_MODE = res.getString(R.string.pref_key_left_handed_mode);
        PREF_KEY_MENU_BAR_POS_PORTRAIT = res.getString(R.string.pref_key_menu_bar_position_portrait);
        PREF_KEY_MENU_BAR_POS_LANDSCAPE = res.getString(R.string.pref_key_menu_bar_position_landscape);
        PREF_KEY_DOUBLE_TAP_ENABLED = res.getString(R.string.pref_key_double_tap_enable);
        PREF_KEY_DOUBLE_TAP_ALL_CARDS = res.getString(R.string.pref_key_double_tap_all_cards);
        PREF_KEY_DOUBLE_TAP_FOUNDATION_FIRST = res.getString(R.string.pref_key_double_tap_foundation_first);
        PREF_KEY_TAP_TO_SELECT_ENABLED = res.getString(R.string.pref_key_tap_to_select_enable);
        PREF_KEY_SINGLE_TAP_SPECIAL_GAMES = res.getString(R.string.pref_key_single_tap_special_games);
        PREF_KEY_BACKGROUND_COLOR_TYPE = res.getString(R.string.pref_key_background_color_type);
        PREF_KEY_BACKGROUND_COLOR = res.getString(R.string.pref_key_background_color);
        PREF_KEY_BACKGROUND_COLOR_CUSTOM = res.getString(R.string.pref_key_background_color_custom);
        PREF_KEY_MOVEMENT_SPEED = res.getString(R.string.pref_key_movement_speed);
        PREF_KEY_SOUND_ENABLED = res.getString(R.string.pref_key_sound_enabled);
        PREF_KEY_WIN_SOUND = res.getString(R.string.pref_key_win_sound);
        PREF_KEY_BACKGROUND_MUSIC = res.getString(R.string.pref_key_background_music);
        PREF_KEY_BACKGROUND_VOLUME = res.getString(R.string.pref_key_background_volume);
        PREF_KEY_PYRAMID_LIMITED_RECYCLES = res.getString(R.string.pref_key_pyramid_limit_recycles);
        PREF_KEY_FORTYEIGHT_LIMITED_RECYCLES = res.getString(R.string.pref_key_fortyeight_limit_recycles);
        PREF_KEY_PYRAMID_NUMBER_OF_RECYCLES = res.getString(R.string.pref_key_pyramid_number_of_recycles);
        PREF_KEY_FORTYEIGHT_NUMBER_OF_RECYCLES = res.getString(R.string.pref_key_fortyeight_number_of_recycles);
        PREF_KEY_VEGAS_NUMBER_OF_RECYCLES = res.getString(R.string.pref_key_vegas_number_of_recycles);
        PREF_KEY_VEGAS_BET_AMOUNT = res.getString(R.string.pref_key_vegas_bet_amount);
        PREF_KEY_VEGAS_WIN_AMOUNT = res.getString(R.string.pref_key_vegas_win_amount);
        PREF_KEY_MENU_ORDER = res.getString(R.string.pref_key_menu_order);
        PREF_KEY_VEGAS_BET_AMOUNT_OLD = PREF_KEY_VEGAS_BET_AMOUNT + OLD;
        PREF_KEY_VEGAS_WIN_AMOUNT_OLD = PREF_KEY_VEGAS_WIN_AMOUNT + OLD;
        PREF_KEY_AUTO_START_NEW_GAME = res.getString(R.string.pref_key_auto_start_new_game);
        PREF_KEY_FORCE_TABLET_LAYOUT = res.getString(R.string.pref_key_force_tablet_layout);
        PREF_KEY_KLONDIKE_LIMITED_RECYCLES = res.getString(R.string.pref_key_klondike_limit_recycles);
        PREF_KEY_KLONDIKE_NUMBER_OF_RECYCLES = res.getString(R.string.pref_key_klondike_number_of_recycles);
        PREF_KEY_NAPOLEONSTOMB_NUMBER_OF_RECYCLES = res.getString(R.string.pref_key_napoleons_tomb_number_of_recycles);
        PREF_KEY_CALCULATION_ALTERNATIVE = res.getString(R.string.pref_key_calculation_alternative);
        PREF_KEY_HIDE_MENU_BAR = res.getString(R.string.pref_key_hide_menu_bar);
        PREF_KEY_IMMERSIVE_MODE = res.getString(R.string.pref_key_immersive_mode);
        PREF_KEY_CALCULATION_ALTERNATIVE_OLD = PREF_KEY_CALCULATION_ALTERNATIVE + OLD;
        PREF_KEY_HIDE_TIME = res.getString(R.string.pref_key_hide_time);
        PREF_KEY_HIDE_SCORE = res.getString(R.string.pref_key_hide_score);
        PREF_KEY_VEGAS_MONEY = res.getString(R.string.pref_key_vegas_money);
        PREF_KEY_VEGAS_MONEY_ENABLED = res.getString(R.string.pref_key_vegas_money_enabled);
        PREF_KEY_VEGAS_RESET_MONEY = res.getString(R.string.pref_key_vegas_reset_money);
        PREF_KEY_MOD3_AUTO_MOVE = res.getString(R.string.pref_key_mod3_auto_move);
        PREF_KEY_PYRAMID_AUTO_MOVE = res.getString(R.string.pref_key_pyramid_auto_move);
        PREF_KEY_SINGLE_TAP_ALL_GAMES = res.getString(R.string.pref_key_single_tap_all_games);
        PREF_KEY_CANFIELD_SIZE_OF_RESERVE = res.getString(R.string.pref_key_canfield_size_of_reserve);
        PREF_KEY_USE_TRUE_RANDOMISATION = res.getString(R.string.pref_key_use_true_randomisation);
        PREF_KEY_MAX_NUMBER_UNDOS = res.getString(R.string.pref_key_max_number_undos);
        PREF_KEY_TOTAL_TIME_PLAYED = res.getString(R.string.pref_key_total_time_played);
        PREF_KEY_TOTAL_NUMBER_UNDOS = res.getString(R.string.pref_key_total_number_undos);
        PREF_KEY_TOTAL_HINTS_SHOWN = res.getString(R.string.pref_key_total_hints_shown);
        PREF_KEY_TOTAL_POINTS_EARNED = res.getString(R.string.pref_key_total_points_earned);
        PREF_KEY_SHOW_DIALOG_NEW_GAME = res.getString(R.string.pref_key_show_dialog_new_game);
        PREF_KEY_SHOW_DIALOG_REDEAL = res.getString(R.string.pref_key_show_dialog_redeal);
        PREF_KEY_SHOW_DIALOG_MIX_CARDS = res.getString(R.string.pref_key_show_dialog_mix_cards);
        PREF_KEY_DISABLE_UNDO_COSTS = res.getString(R.string.pref_key_disable_undo_costs);
        PREF_KEY_DISABLE_HINT_COSTS = res.getString(R.string.pref_key_disable_hint_costs);
        PREF_KEY_HIDE_AUTOCOMPLETE_BUTTON = res.getString(R.string.pref_key_hide_auto_complete_button);
        PREF_KEY_GAME_REDEAL_COUNT = res.getString(R.string.game_recycle_count);
        PREF_KEY_GAME_WON = res.getString(R.string.game_won);
        PREF_KEY_GAME_WON_AND_RELOADED = res.getString(R.string.game_won_and_reloaded);
        PREF_KEY_GAME_NUMBER_OF_WON_GAMES = res.getString(R.string.game_number_of_won_games);
        PREF_KEY_GAME_NUMBER_OF_PLAYED_GAMES = res.getString(R.string.game_number_of_played_games);
        PREF_KEY_GAME_RANDOM_CARDS = res.getString(R.string.game_random_cards);
        PREF_KEY_GAME_FIRST_RUN = res.getString(R.string.game_first_run);
        PREF_KEY_GAME_MOVED_FIRST_CARD = res.getString(R.string.game_moved_first_card);
        PREF_KEY_GAME_LAYOUT_MARGINS_PORTRAIT = res.getString(R.string.pref_key_game_layout_margins_portrait);
        PREF_KEY_GAME_LAYOUT_MARGINS_LANDSCAPE = res.getString(R.string.pref_key_game_layout_margins_landscape);
        PREF_KEY_TEXT_COLOR = res.getString(R.string.pref_key_text_color);

        PREF_KEY_CANFIELD_START_CARD_VALUE = res.getString(R.string.canfield_start_value);
        PREF_KEY_SCORE = res.getString(R.string.score);
        PREF_KEY_SAVED_SCORES = res.getString(R.string.saved_scores);
        PREF_KEY_SAVED_RECENT_SCORES = res.getString(R.string.saved_recent_scores);

        PREF_KEY_RECORD_LIST_ENTRY = res.getString(R.string.record_list_entry);
        PREF_KEY_RECORD_LIST_ENTRIES_SIZE = res.getString(R.string.record_list_entries_size);
        PREF_KEY_FLIP_CARD = res.getString(R.string.flip_card);
        PREF_KEY_ORIGIN = res.getString(R.string.origin);
        PREF_KEY_CARD = res.getString(R.string.card);
        PREF_KEY_CARDS = res.getString(R.string.cards);
        PREF_KEY_STACK = res.getString(R.string.stack);

        PREF_KEY_TIMER_END_TIME = res.getString(R.string.saved_current_time);
        PREF_KEY_TIMER_START_TIME = res.getString(R.string.saved_start_time);
        PREF_KEY_TIMER_WINNING_TIME = res.getString(R.string.saved_shown_time);

        PREF_KEY_CARD_DRAWABLES = res.getString(R.string.pref_key_card_drawables);
        PREF_KEY_CARD_BACKGROUND = res.getString(R.string.pref_key_cards_background);
        PREF_KEY_CARD_BACKGROUND_COLOR = res.getString(R.string.pref_key_cards_background_color);
        PREF_KEY_MENU_COLUMNS_PORTRAIT = res.getString(R.string.pref_key_menu_columns_portrait);
        PREF_KEY_MENU_COLUMNS_LANDSCAPE = res.getString(R.string.pref_key_menu_columns_landscape);

        PREF_KEY_DEVELOPER_OPTION_MOVE_CARDS_EVERYWHERE = res.getString(R.string.pref_key_developer_option_move_cards_everywhere);
        PREF_KEY_DEVELOPER_OPTION_PLAY_EVERY_CARD = res.getString(R.string.pref_key_developer_option_play_every_card);
        PREF_KEY_DEVELOPER_OPTION_INSTANT_WIN = res.getString(R.string.pref_key_developer_option_instant_win);
        PREF_KEY_DEVELOPER_OPTION_NO_SAVING = res.getString(R.string.pref_key_developer_option_no_saving);
        PREF_KEY_DEVELOPER_OPTION_DEAL_CORRECT_SEQUENCES = res.getString(R.string.pref_key_developer_option_deal_correct_sequences);

        DEFAULT_PYRAMID_DIFFICULTY = res.getStringArray(R.array.pref_pyramid_difficulty_values)[0];
        DEFAULT_LANGUAGE = res.getStringArray(R.array.pref_language_values)[0];
        DEFAULT_SPIDER_DIFFICULTY = res.getStringArray(R.array.pref_spider_difficulty_values)[0];
        DEFAULT_ORIENTATION = res.getStringArray(R.array.pref_orientation_values)[0];
        DEFAULT_DOUBLE_TAP_ALL_CARDS = res.getBoolean(R.bool.default_double_tap_all_cards);
        DEFAULT_DOUBLE_TAP_ENABLE = res.getBoolean(R.bool.default_double_tap_enable);
        DEFAULT_DOUBLE_TAP_FOUNDATION_FIRST = res.getBoolean(R.bool.default_double_tap_foundation_first);
        DEFAULT_LEFT_HANDED_MODE = res.getBoolean(R.bool.default_left_handed_mode);
        DEFAULT_GOLF_CYCLIC = res.getBoolean(R.bool.default_golf_cyclic);
        DEFAULT_TAP_TO_SELECT_ENABLED = res.getBoolean(R.bool.default_tap_to_select_enable);
        DEFAULT_SINGLE_TAP_SPECIAL_GAMES_ENABLED = res.getBoolean(R.bool.default_single_tap_enable);
        DEFAULT_AUTO_START_NEW_GAME = res.getBoolean(R.bool.default_auto_start_new_game);
        DEFAULT_KLONDIKE_LIMITED_RECYCLES = res.getBoolean(R.bool.default_klondike_limited_recycles);
        DEFAULT_CALCULATION_ALTERNATIVE = res.getBoolean(R.bool.default_calculation_alternative);
        DEFAULT_HIDE_TIME = res.getBoolean(R.bool.default_hide_time);
        DEFAULT_HIDE_SCORE = res.getBoolean(R.bool.default_hide_score);
        DEFAULT_VEGAS_MONEY_ENABLED = res.getBoolean(R.bool.default_vegas_money_enabled);
        DEFAULT_VEGAS_RESET_MONEY = res.getBoolean(R.bool.default_vegas_reset_money);
        DEFAULT_MOD3_AUTO_MOVE = res.getBoolean(R.bool.default_mod3_auto_move);
        DEFAULT_PYRAMID_AUTO_MOVE = res.getBoolean(R.bool.default_pyramid_auto_move);
        DEFAULT_SINGLE_TAP_ALL_GAMES = res.getBoolean(R.bool.default_single_tap_all_games);
        DEFAULT_DEVELOPER_OPTION_NO_SAVING = res.getBoolean(R.bool.default_developer_option_no_saving);
        DEFAULT_SHOW_ADVANCED_SETTINGS = res.getBoolean(R.bool.default_show_advaced_settings);
        DEFAULT_SHOW_DIALOG_NEW_GAME = res.getBoolean(R.bool.default_show_dialog_new_game);
        DEFAULT_SHOW_DIALOG_REDEAL = res.getBoolean(R.bool.default_show_dialog_redeal);
        DEFAULT_SHOW_DIALOG_MIX_CARDS = res.getBoolean(R.bool.default_show_dialog_mix_cards);
        DEFAULT_HIDE_MENU_BAR = res.getBoolean(R.bool.default_hide_menu_bar);
        DEFAULT_IMMERSIVE_MODE = res.getBoolean(R.bool.default_immersive_mode);
        DEFAULT_HIDE_MENU_BUTTON = res.getBoolean(R.bool.default_hide_menu_button);
        DEFAULT_ENSURE_MOVABILITY = res.getBoolean(R.bool.default_ensure_movability);
        DEFAULT_IMPROVE_AUTO_MOVE = res.getBoolean(R.bool.default_improve_auto_move);
        DEFAULT_SETTINGS_ONLY_FOR_THIS_GAME = false;
        DEFAULT_CURRENT_GAME = res.getInteger(R.integer.default_current_game);
        DEFAULT_TEXT_COLOR = res.getInteger(R.integer.default_text_color);
        DEFAULT_MENU_COLUMNS_LANDSCAPE = res.getString(R.string.default_menu_columns_landscape);
        DEFAULT_MENU_COLUMNS_PORTRAIT = res.getString(R.string.default_menu_columns_portrait);
        DEFAULT_MENU_BAR_POSITION_LANDSCAPE = res.getString(R.string.default_menu_bar_position_landscape);
        DEFAULT_MENU_BAR_POSITION_PORTRAIT = res.getString(R.string.default_menu_bar_position_portrait);
        DEFAULT_FIRST_RUN = res.getBoolean(R.bool.default_first_run);
        DEFAULT_WON = res.getBoolean(R.bool.default_won);
        DEFAULT_HIDE_AUTOCOMPLETE_BUTTON = res.getBoolean(R.bool.default_hide_auto_complete_button);
        DEFAULT_WON_AND_RELOADED = res.getBoolean(R.bool.default_won_and_reloaded);
        DEFAULT_MOVED_FIRST_CARD = res.getBoolean(R.bool.default_moved_first_card);
        DEFAULT_4_COLOR_MODE = res.getBoolean(R.bool.default_4_color_mode);
        DEFAULT_DEVELOPER_OPTION_MOVE_CARDS_EVERYWHERE = res.getBoolean(R.bool.default_developer_option_move_cards_everywhere);
        DEFAULT_DEVELOPER_OPTION_PLAY_EVERY_CARD = res.getBoolean(R.bool.default_developer_option_play_every_card);
        DEFAULT_DEVELOPER_OPTION_INSTANT_WIN = res.getBoolean(R.bool.default_developer_option_instant_win);
        DEFAULT_DEVELOPER_OPTION_DEAL_CORRECT_SEQUENCES = res.getString(R.string.default_developer_option_deal_correct_sequences);
        DEFAULT_USE_TRUE_RANDOMISATION = res.getBoolean(R.bool.default_use_true_randomisation);
        DEFAULT_CARD_BACKGROUND = res.getInteger(R.integer.default_card_background);
        DEFAULT_GAME_LAYOUT_MARGINS_PORTRAIT = res.getInteger(R.integer.default_game_layout_margins_portrait);
        DEFAULT_GAME_LAYOUT_MARGINS_LANDSCAPE = res.getInteger(R.integer.default_game_layout_margins_landscape);
        DEFAULT_CARD_BACKGROUND_COLOR = res.getInteger(R.integer.default_card_background_color);
        DEFAULT_WINNING_TIME = res.getInteger(R.integer.default_winning_time);
        DEFAULT_BACKGROUND_COLOR_TYPE = res.getInteger(R.integer.default_background_color_type);
        DEFAULT_CANFIELD_SIZE_OF_RESERVE = res.getString(R.string.default_canfield_size_of_reserve);
        DEFAULT_BACKGROUND_COLOR = res.getString(R.string.default_background_color);
        DEFAULT_BACKGROUND_COLOR_CUSTOM = res.getInteger(R.integer.default_background_color_custom);
        DEFAULT_MOVEMENT_SPEED = res.getString(R.string.default_movement_speed);
        DEFAULT_SOUND_ENABLED = res.getBoolean(R.bool.default_sound_enabled);
        DEFAULT_FORCE_TABLET_LAYOUT = res.getBoolean(R.bool.default_force_tablet_layout);
        DEFAULT_WIN_SOUND = res.getString(R.string.default_win_sound);
        DEFAULT_BACKGROUND_MUSIC = res.getString(R.string.default_background_music);
        DEFAULT_BACKGROUND_VOLUME = res.getInteger(R.integer.default_background_volume);
        DEFAULT_VEGAS_BET_AMOUNT = res.getInteger(R.integer.default_vegas_bet_amount);
        DEFAULT_VEGAS_WIN_AMOUNT = res.getInteger(R.integer.default_vegas_win_amount);
        DEFAULT_VEGAS_MONEY = res.getInteger(R.integer.default_vegas_money);
        DEFAULT_ENSURE_MOVABILITY_MIN_MOVES = res.getInteger(R.integer.default_ensure_movability_min_moves);
        DEFAULT_MAX_NUMBER_UNDOS = res.getInteger(R.integer.default_max_number_undos);
        DEFAULT_PYRAMID_NUMBER_OF_RECYCLES = res.getString(R.string.default_pyramid_number_of_recycles);
        DEFAULT_FORTYEIGHT_NUMBER_OF_RECYCLES = res.getString(R.string.default_fortyeight_number_of_recycles);
        DEFAULT_VEGAS_NUMBER_OF_RECYCLES = res.getString(R.string.default_vegas_number_of_recycles);
        DEFAULT_KLONDIKE_NUMBER_OF_RECYCLES = res.getString(R.string.default_klondike_number_of_recycles);
        DEFAULT_PYRAMID_LIMITED_RECYCLES = res.getBoolean(R.bool.default_pyramid_limited_recycles);
        DEFAULT_FORTYEIGHT_LIMITED_RECYCLES = res.getBoolean(R.bool.default_fortyeight_limited_recycles);
        DEFAULT_DISABLE_UNDO_COSTS = res.getBoolean(R.bool.default_disable_undo_costs);
        DEFAULT_DISABLE_HINT_COSTS = res.getBoolean(R.bool.default_disable_hint_costs);
        DEFAULT_YUKON_RULES = res.getStringArray(R.array.pref_yukon_rules_values)[0];
        DEFAULT_KLONDIKE_DRAW = res.getStringArray(R.array.pref_draw_values)[0];
        DEFAULT_VEGAS_DRAW = res.getStringArray(R.array.pref_draw_values)[1];
        DEFAULT_CANFIELD_DRAW = res.getStringArray(R.array.pref_draw_values)[1];
        DEFAULT_NAPOLEONSTOMB_NUMBER_OF_RECYCLES = res.getString(R.string.default_napoleons_tomb_number_of_recycles);
    }

    private void putIntList(String name, List<Integer> list) {
        //Thanks to this answer for this idea http://stackoverflow.com/a/11201225/7016229

        StringBuilder s = new StringBuilder();
        for (int i : list) {
            s.append(i).append(",");
        }

        savedGameData.edit().putString(name, s.toString()).apply();
    }

    private void putLongList(String name, List<Long> list) {
        //Thanks to this answer for this idea http://stackoverflow.com/a/11201225/7016229

        StringBuilder s = new StringBuilder();
        for (long i : list) {
            s.append(i).append(",");
        }
        savedGameData.edit().putString(name, s.toString()).apply();
    }

    private void putSharedIntList(String name, List<Integer> list) {
        //
        StringBuilder s = new StringBuilder();
        for (int i : list) {
            s.append(i).append(",");
        }
        savedSharedData.edit().putString(name, s.toString()).apply();
    }

    private void putSharedStringList(String name, List<String> list) {
        //
        StringBuilder s = new StringBuilder();
        for (String i : list) {
            s.append(i).append(",");
        }
        savedSharedData.edit().putString(name, s.toString()).apply();
    }

    private ArrayList<Integer> getIntList(String name) {
        //Thanks to this answer for this idea http://stackoverflow.com/a/11201225/7016229

        String s = savedGameData.getString(name, "");
        StringTokenizer st = new StringTokenizer(s, ",");
        ArrayList<Integer> result = new ArrayList<>();

        while (st.hasMoreTokens()) {
            result.add(Integer.parseInt(st.nextToken()));
        }

        return result;
    }

    private ArrayList<Long> getLongList(String name) {
        //Thanks to this answer for this idea http://stackoverflow.com/a/11201225/7016229

        String s = savedGameData.getString(name, "");
        StringTokenizer st = new StringTokenizer(s, ",");
        ArrayList<Long> result = new ArrayList<>();

        while (st.hasMoreTokens()) {
            result.add(Long.parseLong(st.nextToken()));
        }

        return result;
    }

    private ArrayList<String> getStringList(String name) {
        String s = savedGameData.getString(name, "");
        StringTokenizer st = new StringTokenizer(s, ",");
        ArrayList<String> result = new ArrayList<>();

        while (st.hasMoreTokens()) {
            result.add(st.nextToken());
        }

        return result;
    }

    private ArrayList<Integer> getSharedIntList(String name) {
        String s = savedSharedData.getString(name, "");
        StringTokenizer st = new StringTokenizer(s, ",");
        ArrayList<Integer> result = new ArrayList<>();

        while (st.hasMoreTokens()) {
            result.add(Integer.parseInt(st.nextToken()));
        }

        return result;
    }

    private ArrayList<String> getSharedStringList(String name) {
        String s = savedSharedData.getString(name, "");
        StringTokenizer st = new StringTokenizer(s, ",");
        ArrayList<String> result = new ArrayList<>();

        while (st.hasMoreTokens()) {
            result.add(st.nextToken());
        }

        return result;
    }

    /**
     * need to ensure these settings already exist in the shared pref, or otherwise they getHighScore created
     * by the settings headers and the settings activity would do stuff, because it thinks the user changed
     * the values
     */
    public void setCriticalSettings(){
        saveLocale(getSavedLocale());
        saveForcedTabletLayout(getSavedForcedTabletLayout());
        saveShowExpertSettings(getShowAdvancedSettings());
        saveSingleTapAllGames(getSingleTapAllGames());
        saveTapToSelectEnabled(getSavedTapToSelectEnabled());
        saveLeftHandedMode(getSavedLeftHandedMode());
    }

    /**
     * see description of setCriticalSettings(). Without setting these before loading the settings-activity,
     * the activity would show toasts to start a new game. (Because the preferences getHighScore created and trigger
     * the toast notification)
     */
    public void setCriticalGameSettings(){
        saveCanfieldDrawMode(getSavedCanfieldDrawMode());
        saveKlondikeDrawMode(getSavedKlondikeDrawMode());
        saveVegasDrawMode(getSavedVegasDrawMode());
        saveSpiderDifficulty(getSavedSpiderDifficulty());
        saveYukonRules(getSavedYukonRules());
    }

    /* getters for individual game data */

    public long getSavedTotalTimePlayed(){
        return savedGameData.getLong(PREF_KEY_TOTAL_TIME_PLAYED,0);
    }

    public long getSavedTotalPointsEarned(){
        return savedGameData.getLong(PREF_KEY_TOTAL_POINTS_EARNED,0);
    }

    public long getSavedEndTime(){
        return savedGameData.getLong(PREF_KEY_TIMER_END_TIME,System.currentTimeMillis());
    }

    public long getSavedScore(){
        return savedGameData.getLong(PREF_KEY_SCORE,0);
    }

    public long getSavedStartTime(){
        return savedGameData.getLong(PREF_KEY_TIMER_START_TIME, System.currentTimeMillis());
    }

    public long getSavedWinningTime(){
        return savedGameData.getLong(PREF_KEY_TIMER_WINNING_TIME,DEFAULT_WINNING_TIME);
    }

    public long getSavedVegasMoney(){
        return savedGameData.getLong(PREF_KEY_VEGAS_MONEY,DEFAULT_VEGAS_MONEY);
    }

    public long getSavedVegasOldScore(){
        return savedGameData.getLong(PREF_KEY_VEGAS_OLD_SCORE,0);
    }

    public long getSavedVegasTime(){
        return savedGameData.getLong(PREF_KEY_VEGAS_TIME,0);
    }

    public long[][] getSavedHighScores(){
        long savedScores[][] = new long[MAX_SAVED_SCORES][3];

        ArrayList<Long> listScores = getLongList(PREF_KEY_SAVED_SCORES + 0);
        ArrayList<Long> listTimes = getLongList(PREF_KEY_SAVED_SCORES + 1);
        ArrayList<Long> listDates = getLongList(PREF_KEY_SAVED_SCORES + 2);

        //for compatibility for older app versions, check the size of the saved data
        for (int i = 0; i < MAX_SAVED_SCORES; i++) {
            savedScores[i][0] = listScores.size() > i ? listScores.get(i) : 0;
            savedScores[i][1] = listTimes.size() > i ? listTimes.get(i) : 0;
            savedScores[i][2] = listDates.size() > i ? listDates.get(i) : 0;
        }

        return savedScores;
    }

    public long[][] getSavedRecentScores(){
        long savedScores[][] = new long[MAX_SAVED_SCORES][3];

        ArrayList<Long> listScores = getLongList(PREF_KEY_SAVED_RECENT_SCORES + 0);
        ArrayList<Long> listTimes = getLongList(PREF_KEY_SAVED_RECENT_SCORES + 1);
        ArrayList<Long> listDates = getLongList(PREF_KEY_SAVED_RECENT_SCORES + 2);

        //for compatibility for older app versions, check the size of the saved data
        for (int i = 0; i < MAX_SAVED_SCORES; i++) {
            savedScores[i][0] = listScores.size() > i ? listScores.get(i) : 0;
            savedScores[i][1] = listTimes.size() > i ? listTimes.get(i) : 0;
            savedScores[i][2] = listDates.size() > i ? listDates.get(i) : 0;
        }

        return savedScores;
    }

    public int getSavedTotalNumberUndos(){
        return savedGameData.getInt(PREF_KEY_TOTAL_NUMBER_UNDOS,0);
    }

    public int getSavedTotalHintsShown(){
        return savedGameData.getInt(PREF_KEY_TOTAL_HINTS_SHOWN,0);
    }

    public int getSavedRecycleCounter(int total){
        return savedGameData.getInt(PREF_KEY_GAME_REDEAL_COUNT,total);
    }

    public int getSavedLongestRun(){
        return savedGameData.getInt(PREF_KEY_LONGEST_RUN,0);
    }

    public int getSavedRunCounter(){
        return savedGameData.getInt(PREF_KEY_RUN_COUNTER,0);
    }

    public int getSavedNumberOfPlayedGames(){
        return savedGameData.getInt(PREF_KEY_GAME_NUMBER_OF_PLAYED_GAMES,getSavedNumberOfWonGames());
    }

    public int getSavedNumberOfWonGames(){
        return savedGameData.getInt(PREF_KEY_GAME_NUMBER_OF_WON_GAMES,0);
    }

    public int getSavedEnsureMovabilityMinMoves(){
        return savedGameData.getInt(PREF_KEY_ENSURE_MOVABILITY_MIN_MOVES,DEFAULT_ENSURE_MOVABILITY_MIN_MOVES);
    }

    public int getSavedRecordListEntriesSize(){
        return savedGameData.getInt(PREF_KEY_RECORD_LIST_ENTRIES_SIZE, -1);
    }

    public int getSavedFlipCardId(String pos){
        return savedGameData.getInt(PREF_KEY_RECORD_LIST_ENTRY + pos + PREF_KEY_FLIP_CARD, -1);
    }

    public boolean isFirstRun(){
        return savedGameData.getBoolean(PREF_KEY_GAME_FIRST_RUN,DEFAULT_FIRST_RUN);
    }

    public boolean hasSettingsOnlyForThisGame(){
        return (prefs.getSavedCurrentGame() != DEFAULT_CURRENT_GAME)
                && savedGameData.getBoolean(PREF_KEY_SETTINGS_ONLY_FOR_THIS_GAME,DEFAULT_SETTINGS_ONLY_FOR_THIS_GAME);
    }

    public boolean isDealingCards(){
        return savedGameData.getBoolean(PREF_KEY_DEALING_CARDS,false);
    }

    public boolean isWon(){
        return savedGameData.getBoolean(PREF_KEY_GAME_WON,DEFAULT_WON);
    }

    public boolean isWonAndReloaded(){
        return savedGameData.getBoolean(PREF_KEY_GAME_WON_AND_RELOADED,DEFAULT_WON_AND_RELOADED);
    }

    public boolean hasMovedFirstCard(){
        return savedGameData.getBoolean(PREF_KEY_GAME_MOVED_FIRST_CARD, DEFAULT_MOVED_FIRST_CARD);
    }

    public boolean isDeveloperOptionMoveCardsEverywhereEnabled(){
        return savedSharedData.getBoolean(PREF_KEY_DEVELOPER_OPTION_MOVE_CARDS_EVERYWHERE, DEFAULT_DEVELOPER_OPTION_MOVE_CARDS_EVERYWHERE);
    }

    public boolean isDeveloperOptionPlayEveryCardEnabled(){
        return savedSharedData.getBoolean(PREF_KEY_DEVELOPER_OPTION_PLAY_EVERY_CARD, DEFAULT_DEVELOPER_OPTION_PLAY_EVERY_CARD);
    }

    public boolean isDeveloperOptionInstantWinEnabled(){
        return savedSharedData.getBoolean(PREF_KEY_DEVELOPER_OPTION_INSTANT_WIN, DEFAULT_DEVELOPER_OPTION_INSTANT_WIN);
    }

    public boolean isDeveloperOptionSavingDisabled(){
        return savedSharedData.getBoolean(PREF_KEY_DEVELOPER_OPTION_NO_SAVING, DEFAULT_DEVELOPER_OPTION_NO_SAVING);
    }

    public int getDeveloperOptionDealCorrectSequences(){
        String value = savedSharedData.getString(PREF_KEY_DEVELOPER_OPTION_DEAL_CORRECT_SEQUENCES, DEFAULT_DEVELOPER_OPTION_DEAL_CORRECT_SEQUENCES);
        return Integer.parseInt(value);
    }

    public ArrayList<Integer> getSavedCards(){
        return getIntList(PREF_KEY_CARDS);
    }

    public ArrayList<Integer> getSavedStacks(int id){
        return getIntList(PREF_KEY_STACK + id);
    }

    public ArrayList<Integer> getSavedRandomCards(){
        return getIntList(PREF_KEY_GAME_RANDOM_CARDS);
    }

    public ArrayList<Integer> getSavedRecordListCards(String pos){
        return getIntList(PREF_KEY_RECORD_LIST_ENTRY + pos + PREF_KEY_CARD);
    }

    public ArrayList<Integer> getSavedRecordListOrigins(String pos){
        return getIntList(PREF_KEY_RECORD_LIST_ENTRY + pos + PREF_KEY_ORIGIN);
    }

    public ArrayList<Integer> getSavedRecordListOrders(String pos){
        return getIntList(PREF_KEY_RECORD_LIST_ENTRY + pos + PREF_KEY_ORDER);
    }

    public ArrayList<Integer> getSavedRecordListFlipCards(String pos){
        return getIntList(PREF_KEY_RECORD_LIST_ENTRY + pos + PREF_KEY_FLIP_CARD);
    }

    /* setters for individual game data */

    public void saveTotalPointsEarned(long value){
        savedGameData.edit().putLong(PREF_KEY_TOTAL_POINTS_EARNED,value).apply();
    }

    public void saveTotalTimePlayed(long value){
        savedGameData.edit().putLong(PREF_KEY_TOTAL_TIME_PLAYED,value).apply();
    }

    public void saveScore(long value){
        savedGameData.edit().putLong(PREF_KEY_SCORE,value).apply();
    }

    public void saveStartTime(long value){
        savedGameData.edit().putLong(PREF_KEY_TIMER_START_TIME,value).apply();
    }

    public void saveEndTime(long value){
        savedGameData.edit().putLong(PREF_KEY_TIMER_END_TIME,value).apply();
    }

    public void saveWinningTime(long value){
        savedGameData.edit().putLong(PREF_KEY_TIMER_WINNING_TIME,value).apply();
    }

    public void saveVegasMoney(long value){
        savedGameData.edit().putLong(PREF_KEY_VEGAS_MONEY,value).apply();
    }

    public void saveVegasOldScore(long value){
        savedGameData.edit().putLong(PREF_KEY_VEGAS_OLD_SCORE,value).apply();
    }

    public void saveVegasTime(long value){
        savedGameData.edit().putLong(PREF_KEY_VEGAS_TIME,value).apply();
    }

    public void saveHighScores(long savedScores[][]){
        ArrayList<Long> listScores = new ArrayList<>();
        ArrayList<Long> listTimes = new ArrayList<>();
        ArrayList<Long> listDates = new ArrayList<>();

        for (int i = 0; i < MAX_SAVED_SCORES; i++) {
            listScores.add(savedScores[i][0]);
            listTimes.add(savedScores[i][1]);
            listDates.add(savedScores[i][2]);
        }

        putLongList(PREF_KEY_SAVED_SCORES + 0, listScores);
        putLongList(PREF_KEY_SAVED_SCORES + 1, listTimes);
        putLongList(PREF_KEY_SAVED_SCORES + 2, listDates);
    }

    public void saveRecentScores(long savedScores[][]){
        ArrayList<Long> listScores = new ArrayList<>();
        ArrayList<Long> listTimes = new ArrayList<>();
        ArrayList<Long> listDates = new ArrayList<>();

        for (int i = 0; i < MAX_SAVED_SCORES; i++) {
            listScores.add(savedScores[i][0]);
            listTimes.add(savedScores[i][1]);
            listDates.add(savedScores[i][2]);
        }

        putLongList(PREF_KEY_SAVED_RECENT_SCORES + 0, listScores);
        putLongList(PREF_KEY_SAVED_RECENT_SCORES + 1, listTimes);
        putLongList(PREF_KEY_SAVED_RECENT_SCORES + 2, listDates);
    }

    public void saveTotalNumberUndos(int value){
        savedGameData.edit().putInt(PREF_KEY_TOTAL_NUMBER_UNDOS,value).apply();
    }

    public void saveTotalHintsShown(int value){
        savedGameData.edit().putInt(PREF_KEY_TOTAL_HINTS_SHOWN,value).apply();
    }

    public void saveRedealCount(int value){
        savedGameData.edit().putInt(PREF_KEY_GAME_REDEAL_COUNT,value).apply();
    }

    public void saveEnsureMovabilityMinMoves(int value){
        savedGameData.edit().putInt(PREF_KEY_ENSURE_MOVABILITY_MIN_MOVES, value).apply();
    }

    public void saveLongestRun(int value){
        savedGameData.edit().putInt(PREF_KEY_LONGEST_RUN,value).apply();
    }

    public void saveRunCounter(int value){
        savedGameData.edit().putInt(PREF_KEY_RUN_COUNTER,value).apply();
    }

    public void saveNumberOfWonGames(int value){
        savedGameData.edit().putInt(PREF_KEY_GAME_NUMBER_OF_WON_GAMES,value).apply();
    }

    public void saveNumberOfPlayedGames(int value){
        savedGameData.edit().putInt(PREF_KEY_GAME_NUMBER_OF_PLAYED_GAMES,value).apply();
    }

    public void saveRecordListEntriesSize(int value){
        savedGameData.edit().putInt(PREF_KEY_RECORD_LIST_ENTRIES_SIZE,value).apply();
    }

    public void setSettingsOnlyForThisGame(boolean value){
        savedGameData.edit().putBoolean(PREF_KEY_SETTINGS_ONLY_FOR_THIS_GAME,value).apply();
    }

    public void saveFirstRun(boolean value){
        savedGameData.edit().putBoolean(PREF_KEY_GAME_FIRST_RUN,value).apply();
    }

    public void setDealingCards(boolean value){
        savedGameData.edit().putBoolean(PREF_KEY_DEALING_CARDS,value).apply();
    }

    public void saveWon(boolean value){
        savedGameData.edit().putBoolean(PREF_KEY_GAME_WON,value).apply();
    }

    public void saveWonAndReloaded(boolean value){
        savedGameData.edit().putBoolean(PREF_KEY_GAME_WON_AND_RELOADED,value).apply();
    }

    public void saveMovedFirstCard(boolean value){
        savedGameData.edit().putBoolean(PREF_KEY_GAME_MOVED_FIRST_CARD,value).apply();
    }

    public void saveCards(List<Integer> list){
        putIntList(PREF_KEY_CARDS,list);
    }

    public void saveStacks(ArrayList<Integer> list, int id){
        putIntList(PREF_KEY_STACK + id,list);
    }

    public void saveRandomCards(ArrayList<Integer> list){
        putIntList(PREF_KEY_GAME_RANDOM_CARDS,list);
    }

    public void saveRecordListCards(ArrayList<Integer> list, String pos){
        putIntList(PREF_KEY_RECORD_LIST_ENTRY + pos + PREF_KEY_CARD,list);
    }

    public void saveRecordListOrigins(ArrayList<Integer> list, String pos){
        putIntList(PREF_KEY_RECORD_LIST_ENTRY + pos + PREF_KEY_ORIGIN,list);
    }

    public void saveRecordListOrders(ArrayList<Integer> list, String pos){
        putIntList(PREF_KEY_RECORD_LIST_ENTRY + pos + PREF_KEY_ORDER,list);
    }

    public void saveRecordListFlipCards(ArrayList<Integer> list, String pos){
        putIntList(PREF_KEY_RECORD_LIST_ENTRY + pos + PREF_KEY_FLIP_CARD,list);
    }

    /* getters for shared data */

    public int getSavedGameLayoutMarginsPortrait(){
        if (hasSettingsOnlyForThisGame()){
            return savedGameData.getInt(PREF_KEY_GAME_LAYOUT_MARGINS_PORTRAIT,DEFAULT_GAME_LAYOUT_MARGINS_PORTRAIT);
        } else {
            return savedSharedData.getInt(PREF_KEY_GAME_LAYOUT_MARGINS_PORTRAIT,DEFAULT_GAME_LAYOUT_MARGINS_PORTRAIT);
        }
    }

    public int getSavedGameLayoutMarginsLandscape(){
        if (hasSettingsOnlyForThisGame()){
            return savedGameData.getInt(PREF_KEY_GAME_LAYOUT_MARGINS_LANDSCAPE,DEFAULT_GAME_LAYOUT_MARGINS_LANDSCAPE);
        } else {
            return savedSharedData.getInt(PREF_KEY_GAME_LAYOUT_MARGINS_LANDSCAPE,DEFAULT_GAME_LAYOUT_MARGINS_LANDSCAPE);
        }
    }

    public int getSavedCardBackground(){
        if (hasSettingsOnlyForThisGame()){
            return savedGameData.getInt(PREF_KEY_CARD_BACKGROUND,DEFAULT_CARD_BACKGROUND);
        } else {
            return savedSharedData.getInt(PREF_KEY_CARD_BACKGROUND,DEFAULT_CARD_BACKGROUND);
        }
    }

    public int getSavedCardBackgroundColor(){
        if (hasSettingsOnlyForThisGame()){
            return savedGameData.getInt(PREF_KEY_CARD_BACKGROUND_COLOR,DEFAULT_CARD_BACKGROUND_COLOR);
        } else {
            return savedSharedData.getInt(PREF_KEY_CARD_BACKGROUND_COLOR,DEFAULT_CARD_BACKGROUND_COLOR);
        }
    }

    public int getSavedBackgroundColorType(){
        if (hasSettingsOnlyForThisGame()){
            return savedGameData.getInt(PREF_KEY_BACKGROUND_COLOR_TYPE,DEFAULT_BACKGROUND_COLOR_TYPE);
        } else {
            return savedSharedData.getInt(PREF_KEY_BACKGROUND_COLOR_TYPE,DEFAULT_BACKGROUND_COLOR_TYPE);
        }
    }

    public int getSavedBackgroundCustomColor(){
        if (hasSettingsOnlyForThisGame()){
            return savedGameData.getInt(PREF_KEY_BACKGROUND_COLOR_CUSTOM,DEFAULT_BACKGROUND_COLOR_CUSTOM);
        } else {
            return savedSharedData.getInt(PREF_KEY_BACKGROUND_COLOR_CUSTOM,DEFAULT_BACKGROUND_COLOR_CUSTOM);
        }
    }

    public int getSavedCardTheme(){
        if (hasSettingsOnlyForThisGame()){
            return savedGameData.getInt(PREF_KEY_CARD_DRAWABLES,1);
        } else {
            return savedSharedData.getInt(PREF_KEY_CARD_DRAWABLES,1);
        }
    }

    public int getSavedBackgroundVolume(){
        return savedSharedData.getInt(PREF_KEY_BACKGROUND_VOLUME,DEFAULT_BACKGROUND_VOLUME);
    }

    public int getSavedVegasBetAmount(){
        return savedSharedData.getInt(PREF_KEY_VEGAS_BET_AMOUNT,DEFAULT_VEGAS_BET_AMOUNT);
    }

    public int getSavedVegasWinAmount(){
        return savedSharedData.getInt(PREF_KEY_VEGAS_WIN_AMOUNT,DEFAULT_VEGAS_WIN_AMOUNT);
    }

    public int getSavedVegasBetAmountOld(){
        return savedSharedData.getInt(PREF_KEY_VEGAS_BET_AMOUNT_OLD,DEFAULT_VEGAS_BET_AMOUNT);
    }

    public int getSavedVegasWinAmountOld(){
        return savedSharedData.getInt(PREF_KEY_VEGAS_WIN_AMOUNT_OLD,DEFAULT_VEGAS_WIN_AMOUNT);
    }

    public int getSavedCurrentGame(){
        return savedSharedData.getInt(PREF_KEY_CURRENT_GAME,DEFAULT_CURRENT_GAME);
    }

    public int getSavedOrientation(){
        return Integer.parseInt(savedSharedData.getString(PREF_KEY_ORIENTATION,DEFAULT_ORIENTATION));
    }

    public int getSavedBackgroundColor(){
        if (hasSettingsOnlyForThisGame()){
            return Integer.parseInt(savedGameData.getString(PREF_KEY_BACKGROUND_COLOR,DEFAULT_BACKGROUND_COLOR));
        } else {
            return Integer.parseInt(savedSharedData.getString(PREF_KEY_BACKGROUND_COLOR,DEFAULT_BACKGROUND_COLOR));
        }
    }

    public int getSavedTextColor(){
        if (hasSettingsOnlyForThisGame()){
            return savedGameData.getInt(PREF_KEY_TEXT_COLOR,DEFAULT_TEXT_COLOR);
        } else {
            return savedSharedData.getInt(PREF_KEY_TEXT_COLOR,DEFAULT_TEXT_COLOR);
        }
    }

    public int getSavedMenuColumnsPortrait(){
        return Integer.parseInt(savedSharedData.getString(PREF_KEY_MENU_COLUMNS_PORTRAIT,DEFAULT_MENU_COLUMNS_PORTRAIT));
    }

    public int getSavedMenuColumnsLandscape(){
        return Integer.parseInt(savedSharedData.getString(PREF_KEY_MENU_COLUMNS_LANDSCAPE,DEFAULT_MENU_COLUMNS_LANDSCAPE));
    }

    public int getSavedNumberOfRecycles(String Key, String defaulValue){
        return Integer.parseInt(savedSharedData.getString(Key,defaulValue));
    }

    public int getSavedCanfieldSizeOfReserve(){
        return Integer.parseInt(savedSharedData.getString(PREF_KEY_CANFIELD_SIZE_OF_RESERVE, DEFAULT_CANFIELD_SIZE_OF_RESERVE));
    }

    public float getSavedMovementSpeed(){
        return Float.parseFloat(savedSharedData.getString(PREF_KEY_MOVEMENT_SPEED,DEFAULT_MOVEMENT_SPEED));
    }

    public int getSavedMaxNumberUndos(){
        return savedSharedData.getInt(PREF_KEY_MAX_NUMBER_UNDOS,DEFAULT_MAX_NUMBER_UNDOS);
    }

    public String getSavedBackgroundMusic(){
        return savedSharedData.getString(PREF_KEY_BACKGROUND_MUSIC,DEFAULT_BACKGROUND_MUSIC);
    }

    public String getSavedLocale(){
        return savedSharedData.getString(PREF_KEY_LANGUAGE,DEFAULT_LANGUAGE);
    }

    public String getSavedCanfieldDrawMode(){
        return savedSharedData.getString(PREF_KEY_CANFIELD_DRAW,DEFAULT_CANFIELD_DRAW);
    }

    public String getSavedCanfieldDrawModeOld(){
        return savedSharedData.getString(PREF_KEY_CANFIELD_DRAW_OLD,DEFAULT_CANFIELD_DRAW);
    }

    public String getSavedKlondikeDrawMode(){
        return savedSharedData.getString(PREF_KEY_KLONDIKE_DRAW,DEFAULT_KLONDIKE_DRAW);
    }

    public String getSavedVegasDrawMode(){
        return savedSharedData.getString(PREF_KEY_VEGAS_DRAW,DEFAULT_VEGAS_DRAW);
    }

    public String getSavedKlondikeVegasDrawModeOld(int which){
        if (which==1) {
            return savedSharedData.getString(PREF_KEY_KLONDIKE_DRAW_OLD, DEFAULT_KLONDIKE_DRAW);
        } else {
            return savedSharedData.getString(PREF_KEY_VEGAS_DRAW_OLD, DEFAULT_VEGAS_DRAW);
        }
    }

    public String getSavedSpiderDifficulty(){
        return savedSharedData.getString(PREF_KEY_SPIDER_DIFFICULTY,DEFAULT_SPIDER_DIFFICULTY);
    }

    public String getSavedSpiderDifficultyOld(){
        return savedSharedData.getString(PREF_KEY_SPIDER_DIFFICULTY_OLD,DEFAULT_SPIDER_DIFFICULTY);
    }

    public String getSavedYukonRules(){
        return savedSharedData.getString(PREF_KEY_YUKON_RULES,DEFAULT_YUKON_RULES);
    }

    public String getSavedYukonRulesOld(){
        return savedSharedData.getString(PREF_KEY_YUKON_RULES_OLD,DEFAULT_YUKON_RULES);
    }

    public String getSavedMenuBarPosPortrait(){
        if (hasSettingsOnlyForThisGame()){
            return savedGameData.getString(PREF_KEY_MENU_BAR_POS_PORTRAIT,DEFAULT_MENU_BAR_POSITION_PORTRAIT);
        } else {
            return savedSharedData.getString(PREF_KEY_MENU_BAR_POS_PORTRAIT,DEFAULT_MENU_BAR_POSITION_PORTRAIT);
        }
    }

    public String getSavedMenuBarPosLandscape(){
        if (hasSettingsOnlyForThisGame()){
            return savedGameData.getString(PREF_KEY_MENU_BAR_POS_LANDSCAPE,DEFAULT_MENU_BAR_POSITION_LANDSCAPE);
        } else {
            return savedSharedData.getString(PREF_KEY_MENU_BAR_POS_LANDSCAPE,DEFAULT_MENU_BAR_POSITION_LANDSCAPE);
        }

    }

    public String getSavedPyramidDifficulty(){
        return savedSharedData.getString(PREF_KEY_PYRAMID_DIFFICULTY,DEFAULT_PYRAMID_DIFFICULTY);
    }

    public String getSavedWinSound(){
        return savedSharedData.getString(PREF_KEY_WIN_SOUND, DEFAULT_WIN_SOUND);
    }

    public boolean getSavedForcedTabletLayout(){
        return savedSharedData.getBoolean(PREF_KEY_FORCE_TABLET_LAYOUT,DEFAULT_FORCE_TABLET_LAYOUT);
    }

    public boolean getSavedLeftHandedMode(){
        return savedSharedData.getBoolean(PREF_KEY_LEFT_HANDED_MODE,DEFAULT_LEFT_HANDED_MODE);
    }

    public boolean getSavedFourColorMode(){
        if (hasSettingsOnlyForThisGame()){
            return savedGameData.getBoolean(PREF_KEY_4_COLOR_MODE,DEFAULT_4_COLOR_MODE);
        } else {
            return savedSharedData.getBoolean(PREF_KEY_4_COLOR_MODE,DEFAULT_4_COLOR_MODE);
        }
    }

    public boolean getSavedHideStatusBar(){
        return savedSharedData.getBoolean(PREF_KEY_HIDE_STATUS_BAR,false);
    }

    public boolean getHideMenuButton(){
        if (hasSettingsOnlyForThisGame()){
            return savedGameData.getBoolean(PREF_KEY_HIDE_MENU_BUTTON,DEFAULT_HIDE_MENU_BUTTON);
        } else {
            return savedSharedData.getBoolean(PREF_KEY_HIDE_MENU_BUTTON,DEFAULT_HIDE_MENU_BUTTON);
        }
    }

    public boolean getHideAutoCompleteButton(){
        if (hasSettingsOnlyForThisGame()){
            return savedGameData.getBoolean(PREF_KEY_HIDE_AUTOCOMPLETE_BUTTON,DEFAULT_HIDE_AUTOCOMPLETE_BUTTON);
        } else {
            return savedSharedData.getBoolean(PREF_KEY_HIDE_AUTOCOMPLETE_BUTTON,DEFAULT_HIDE_AUTOCOMPLETE_BUTTON);
        }
    }

    public boolean getSavedCalculationAlternativeMode(){
        return savedSharedData.getBoolean(PREF_KEY_CALCULATION_ALTERNATIVE,DEFAULT_CALCULATION_ALTERNATIVE);
    }

    public boolean getSavedCalculationAlternativeModeOld(){
        return savedSharedData.getBoolean(PREF_KEY_CALCULATION_ALTERNATIVE_OLD,DEFAULT_CALCULATION_ALTERNATIVE);
    }

    public boolean getSavedFortyEightLimitedRecycles(){
        return savedSharedData.getBoolean(PREF_KEY_FORTYEIGHT_LIMITED_RECYCLES, DEFAULT_FORTYEIGHT_LIMITED_RECYCLES);
    }

    public boolean getSavedGoldCyclic(){
        return savedSharedData.getBoolean(PREF_KEY_GOLF_CYCLIC,DEFAULT_GOLF_CYCLIC);
    }

    public boolean getSavedImmersiveMode(){
        return savedSharedData.getBoolean(PREF_KEY_IMMERSIVE_MODE,DEFAULT_IMMERSIVE_MODE);
    }

    public boolean getSavedKlondikeLimitedRecycles(){
        return savedSharedData.getBoolean(PREF_KEY_KLONDIKE_LIMITED_RECYCLES, DEFAULT_KLONDIKE_LIMITED_RECYCLES);
    }

    public boolean getSavedMod3AutoMove(){
        return savedSharedData.getBoolean(PREF_KEY_MOD3_AUTO_MOVE,DEFAULT_MOD3_AUTO_MOVE);
    }

    public boolean getSavedPyramidLimitedRecycles(){
        return savedSharedData.getBoolean(PREF_KEY_PYRAMID_LIMITED_RECYCLES, DEFAULT_PYRAMID_LIMITED_RECYCLES);
    }

    public boolean getSavedPyramidAutoMove(){
        return savedSharedData.getBoolean(PREF_KEY_PYRAMID_AUTO_MOVE,DEFAULT_PYRAMID_AUTO_MOVE);
    }

    public boolean getSavedVegasSaveMoneyEnabled(){
        return savedSharedData.getBoolean(PREF_KEY_VEGAS_MONEY_ENABLED,DEFAULT_VEGAS_MONEY_ENABLED);
    }

    public boolean getSavedVegasResetMoney(){
        return savedSharedData.getBoolean(PREF_KEY_VEGAS_RESET_MONEY,DEFAULT_VEGAS_RESET_MONEY);
    }

    public boolean getSavedHideTime(){
        if (hasSettingsOnlyForThisGame()){
            return savedGameData.getBoolean(PREF_KEY_HIDE_TIME,DEFAULT_HIDE_TIME);
        } else {
            return savedSharedData.getBoolean(PREF_KEY_HIDE_TIME,DEFAULT_HIDE_TIME);
        }
    }

    public boolean getSavedHideScore(){
        if (hasSettingsOnlyForThisGame()){
            return savedGameData.getBoolean(PREF_KEY_HIDE_SCORE,DEFAULT_HIDE_SCORE);
        } else {
            return savedSharedData.getBoolean(PREF_KEY_HIDE_SCORE,DEFAULT_HIDE_SCORE);
        }
    }

    public boolean getSavedAutoStartNewGame(){
        return savedSharedData.getBoolean(PREF_KEY_AUTO_START_NEW_GAME,DEFAULT_AUTO_START_NEW_GAME);
    }

    public boolean getSavedTapToSelectEnabled(){
        return savedSharedData.getBoolean(PREF_KEY_TAP_TO_SELECT_ENABLED,DEFAULT_TAP_TO_SELECT_ENABLED);
    }

    public boolean getSavedDoubleTapEnabled(){
        return savedSharedData.getBoolean(PREF_KEY_DOUBLE_TAP_ENABLED,DEFAULT_DOUBLE_TAP_ENABLE);
    }

    public boolean getSavedDoubleTapAllCards(){
        return savedSharedData.getBoolean(PREF_KEY_DOUBLE_TAP_ALL_CARDS,DEFAULT_DOUBLE_TAP_ALL_CARDS);
    }

    public boolean getShowAdvancedSettings(){
        return savedSharedData.getBoolean(PREF_KEY_SHOW_ADVANCED_SETTINGS,DEFAULT_SHOW_ADVANCED_SETTINGS);
    }

    public boolean getSavedDoubleTapFoundationFirst(){
        return savedSharedData.getBoolean(PREF_KEY_DOUBLE_TAP_FOUNDATION_FIRST,DEFAULT_DOUBLE_TAP_FOUNDATION_FIRST);
    }

    public boolean getSavedEnsureMovability(){
        return savedSharedData.getBoolean(PREF_KEY_ENSURE_MOVABILITY,DEFAULT_ENSURE_MOVABILITY);
    }

    public boolean getSavedSingleTapSpecialGames(){
        return savedSharedData.getBoolean(PREF_KEY_SINGLE_TAP_SPECIAL_GAMES, DEFAULT_SINGLE_TAP_SPECIAL_GAMES_ENABLED);
    }

    public boolean getSavedStartWithMenu(){
        return savedSharedData.getBoolean(PREF_KEY_START_WITH_MENU,false);
    }

    public boolean getSavedSoundEnabled(){
        return savedSharedData.getBoolean(PREF_KEY_SOUND_ENABLED, DEFAULT_SOUND_ENABLED);
    }

    public boolean getSingleTapAllGames(){
        return savedSharedData.getBoolean(PREF_KEY_SINGLE_TAP_ALL_GAMES, DEFAULT_SINGLE_TAP_ALL_GAMES);
    }

    public boolean getSavedUseTrueRandomisation(){
        return savedSharedData.getBoolean(PREF_KEY_USE_TRUE_RANDOMISATION, DEFAULT_USE_TRUE_RANDOMISATION);
    }

    public boolean getShowDialogNewGame(){
        return savedSharedData.getBoolean(PREF_KEY_SHOW_DIALOG_NEW_GAME, DEFAULT_SHOW_DIALOG_NEW_GAME);
    }

    public boolean getShowDialogRedeal(){
        return savedSharedData.getBoolean(PREF_KEY_SHOW_DIALOG_REDEAL, DEFAULT_SHOW_DIALOG_REDEAL);
    }

    public boolean getShowDialogMixCards(){
        return savedSharedData.getBoolean(PREF_KEY_SHOW_DIALOG_MIX_CARDS, DEFAULT_SHOW_DIALOG_MIX_CARDS);
    }

    public boolean getDisableUndoCosts(){
        return savedSharedData.getBoolean(PREF_KEY_DISABLE_UNDO_COSTS, DEFAULT_DISABLE_UNDO_COSTS);
    }

    public boolean getDisableHintCosts(){
        return savedSharedData.getBoolean(PREF_KEY_DISABLE_HINT_COSTS, DEFAULT_DISABLE_HINT_COSTS);
    }

    public boolean getHideMenuBar(){
        return savedSharedData.getBoolean(PREF_KEY_HIDE_MENU_BAR, DEFAULT_HIDE_MENU_BAR);
    }

    public boolean getImproveAutoMove(){
        return savedSharedData.getBoolean(PREF_KEY_IMRPOVE_AUTO_MOVE, DEFAULT_IMPROVE_AUTO_MOVE);
    }

    public ArrayList<Integer> getSavedMenuGamesList(){
        return getSharedIntList(PREF_KEY_MENU_GAMES);
    }

    public ArrayList<Integer> getSavedMenuOrderList(){
        return getSharedIntList(PREF_KEY_MENU_ORDER);
    }

    /* setters for shared data */

    public void saveYukonRulesOld(){
        savedSharedData.edit().putString(PREF_KEY_YUKON_RULES_OLD, getSavedYukonRules()).apply();
    }

    public void saveBackgroundColorType(int value){
        if (hasSettingsOnlyForThisGame()){
            savedGameData.edit().putInt(PREF_KEY_BACKGROUND_COLOR_TYPE,value).apply();
        } else {
            savedSharedData.edit().putInt(PREF_KEY_BACKGROUND_COLOR_TYPE,value).apply();
        }
    }

    public void saveBackgroundCustomColor(int value){
        if (hasSettingsOnlyForThisGame()){
            savedGameData.edit().putInt(PREF_KEY_BACKGROUND_COLOR_CUSTOM,value).apply();
        } else {
            savedSharedData.edit().putInt(PREF_KEY_BACKGROUND_COLOR_CUSTOM,value).apply();
        }
    }

    public void saveCardBackground(int value){
        if (hasSettingsOnlyForThisGame()){
            savedGameData.edit().putInt(PREF_KEY_CARD_BACKGROUND,value).apply();
        } else {
            savedSharedData.edit().putInt(PREF_KEY_CARD_BACKGROUND,value).apply();
        }
    }

    public void saveCardBackgroundColor(int value){
        if (hasSettingsOnlyForThisGame()){
            savedGameData.edit().putInt(PREF_KEY_CARD_BACKGROUND_COLOR,value).apply();
        } else {
            savedSharedData.edit().putInt(PREF_KEY_CARD_BACKGROUND_COLOR,value).apply();
        }
    }

    public void saveCardTheme(int value){
        if (hasSettingsOnlyForThisGame()){
            savedGameData.edit().putInt(PREF_KEY_CARD_DRAWABLES,value).apply();
        } else {
            savedSharedData.edit().putInt(PREF_KEY_CARD_DRAWABLES, value).apply();
        }
    }

    public void saveBackgroundVolume(int value){
        savedSharedData.edit().putInt(PREF_KEY_BACKGROUND_VOLUME,value).apply();
    }

    public void saveVegasBetAmount(int value){
        savedSharedData.edit().putInt(PREF_KEY_VEGAS_BET_AMOUNT,value).apply();
    }

    public void saveVegasWinAmount(int value){
        savedSharedData.edit().putInt(PREF_KEY_VEGAS_WIN_AMOUNT,value).apply();
    }

    public void saveGameLayoutMarginsPortrait(int value){
        if (hasSettingsOnlyForThisGame()){
            savedGameData.edit().putInt(PREF_KEY_GAME_LAYOUT_MARGINS_PORTRAIT, value).apply();
        } else {
            savedSharedData.edit().putInt(PREF_KEY_GAME_LAYOUT_MARGINS_PORTRAIT, value).apply();
        }
    }

    public void saveGameLayoutMarginsLandscape(int value){
        if (hasSettingsOnlyForThisGame()){
            savedGameData.edit().putInt(PREF_KEY_GAME_LAYOUT_MARGINS_LANDSCAPE, value).apply();
        } else {
            savedSharedData.edit().putInt(PREF_KEY_GAME_LAYOUT_MARGINS_LANDSCAPE, value).apply();
        }
    }

    public void saveVegasBetAmountOld(){
        savedSharedData.edit().putInt(PREF_KEY_VEGAS_BET_AMOUNT_OLD,getSavedVegasBetAmount()).apply();
    }

    public void saveVegasWinAmountOld(){
        savedSharedData.edit().putInt(PREF_KEY_VEGAS_WIN_AMOUNT_OLD,getSavedVegasWinAmount()).apply();
    }

    public void saveCurrentGame(int value){
        savedSharedData.edit().putInt(PREF_KEY_CURRENT_GAME,value).apply();
    }

    public void saveLocale(String locale){
        savedSharedData.edit().putString(PREF_KEY_LANGUAGE,locale).apply();
    }

    public void saveCanfieldDrawMode(String value){
        savedSharedData.edit().putString(PREF_KEY_CANFIELD_DRAW,value).apply();
    }

    public void saveCanfieldDrawModeOld(){
        savedSharedData.edit().putString(PREF_KEY_CANFIELD_DRAW_OLD,getSavedCanfieldDrawMode()).apply();
    }

    public void saveKlondikeDrawMode(String value){
        savedSharedData.edit().putString(PREF_KEY_KLONDIKE_DRAW,value).apply();
    }

    public void saveKlondikeVegasDrawModeOld(int which){
        if (which==1) {
            savedSharedData.edit().putString(PREF_KEY_KLONDIKE_DRAW_OLD, getSavedKlondikeDrawMode()).apply();
        } else{
            savedSharedData.edit().putString(PREF_KEY_VEGAS_DRAW_OLD,getSavedVegasDrawMode()).apply();
        }
    }

    public void saveVegasDrawMode(String value){
        savedSharedData.edit().putString(PREF_KEY_VEGAS_DRAW,value).apply();
    }

    public void saveSpiderDifficulty(String value){
        savedSharedData.edit().putString(PREF_KEY_SPIDER_DIFFICULTY,value).apply();
    }

    public void saveSpiderDifficultyOld(){
        savedSharedData.edit().putString(PREF_KEY_SPIDER_DIFFICULTY_OLD,getSavedSpiderDifficulty()).apply();
    }

    public void saveYukonRules(String value){
        savedSharedData.edit().putString(PREF_KEY_YUKON_RULES,value).apply();
    }

    public void saveCalculationAlternativeModeOld(){
        savedSharedData.edit().putBoolean(PREF_KEY_CALCULATION_ALTERNATIVE_OLD,getSavedCalculationAlternativeMode()).apply();
    }

    public void saveForcedTabletLayout(boolean value){
        savedSharedData.edit().putBoolean(PREF_KEY_FORCE_TABLET_LAYOUT,value).apply();
    }

    public void saveShowExpertSettings(boolean value){
        savedSharedData.edit().putBoolean(PREF_KEY_SHOW_ADVANCED_SETTINGS,value).apply();
    }

    public void saveHideMenuBar(boolean value){
        savedSharedData.edit().putBoolean(PREF_KEY_HIDE_MENU_BAR,value).apply();
    }

    public void saveBackgroundColor(int value){
        if (hasSettingsOnlyForThisGame()){
            savedGameData.edit().putString(PREF_KEY_BACKGROUND_COLOR,Integer.toString(value)).apply();
        } else {
            savedSharedData.edit().putString(PREF_KEY_BACKGROUND_COLOR,Integer.toString(value)).apply();
        }
    }

    public void saveMaxNumberUndos(int value){
        savedSharedData.edit().putInt(PREF_KEY_MAX_NUMBER_UNDOS,value).apply();
    }

    public void saveTextColor(int value){
        if (hasSettingsOnlyForThisGame()) {
            savedGameData.edit().putInt(PREF_KEY_TEXT_COLOR, value).apply();
        } else {
            savedSharedData.edit().putInt(PREF_KEY_TEXT_COLOR, value).apply();
        }
    }

    public void saveMenuBarPosPortrait(String value){
        if (hasSettingsOnlyForThisGame()){
            savedGameData.edit().putString(PREF_KEY_MENU_BAR_POS_PORTRAIT,value).apply();
        } else {
            savedSharedData.edit().putString(PREF_KEY_MENU_BAR_POS_PORTRAIT,value).apply();
        }
    }

    public void saveMenuBarPosLandscape(String value){
        if (hasSettingsOnlyForThisGame()){
            savedGameData.edit().putString(PREF_KEY_MENU_BAR_POS_LANDSCAPE,value).apply();
        } else {
            savedSharedData.edit().putString(PREF_KEY_MENU_BAR_POS_LANDSCAPE,value).apply();
        }
    }

    public void saveMenuColumnsPortrait(String value){
        savedSharedData.edit().putString(PREF_KEY_MENU_COLUMNS_PORTRAIT,value).apply();
    }

    public void saveMenuColumnsLandscape(String value){
        savedSharedData.edit().putString(PREF_KEY_MENU_COLUMNS_LANDSCAPE,value).apply();
    }

    public void saveVegasResetMoney(boolean value){
        savedSharedData.edit().putBoolean(PREF_KEY_VEGAS_RESET_MONEY,value).apply();
    }

    public void saveSingleTapAllGames(boolean value){
        savedSharedData.edit().putBoolean(PREF_KEY_SINGLE_TAP_ALL_GAMES,value).apply();
    }

    public void saveTapToSelectEnabled(boolean value){
        savedSharedData.edit().putBoolean(PREF_KEY_TAP_TO_SELECT_ENABLED,value).apply();
    }

    public void saveLeftHandedMode(boolean value){
        savedSharedData.edit().putBoolean(PREF_KEY_LEFT_HANDED_MODE,value).apply();
    }

    public void putShowDialogNewGame(boolean value){
        savedSharedData.edit().putBoolean(PREF_KEY_SHOW_DIALOG_NEW_GAME,value).apply();
    }

    public void putShowDialogRedeal(boolean value){
        savedSharedData.edit().putBoolean(PREF_KEY_SHOW_DIALOG_REDEAL,value).apply();
    }

    public void putShowDialogMixCards(boolean value){
        savedSharedData.edit().putBoolean(PREF_KEY_SHOW_DIALOG_MIX_CARDS,value).apply();
    }

    public void putFourColorMode(boolean value){
        if (hasSettingsOnlyForThisGame()){
            savedGameData.edit().putBoolean(PREF_KEY_4_COLOR_MODE,value).apply();
        } else {
            savedSharedData.edit().putBoolean(PREF_KEY_4_COLOR_MODE,value).apply();
        }
    }

    public void putHideTime(boolean value){
        if (hasSettingsOnlyForThisGame()){
            savedGameData.edit().putBoolean(PREF_KEY_HIDE_TIME,value).apply();
        } else {
            savedSharedData.edit().putBoolean(PREF_KEY_HIDE_TIME,value).apply();
        }
    }

    public void putHideScore(boolean value){
        if (hasSettingsOnlyForThisGame()){
            savedGameData.edit().putBoolean(PREF_KEY_HIDE_SCORE,value).apply();
        } else {
            savedSharedData.edit().putBoolean(PREF_KEY_HIDE_SCORE,value).apply();
        }
    }

    public void putHideMenuButton(boolean value){
        if (hasSettingsOnlyForThisGame()){
            savedGameData.edit().putBoolean(PREF_KEY_HIDE_MENU_BUTTON,value).apply();
        } else {
            savedSharedData.edit().putBoolean(PREF_KEY_HIDE_MENU_BUTTON,value).apply();
        }
    }

    public void putHideAutoCompleteButton(boolean value){
        if (hasSettingsOnlyForThisGame()){
            savedGameData.edit().putBoolean(PREF_KEY_HIDE_AUTOCOMPLETE_BUTTON,value).apply();
        } else {
            savedSharedData.edit().putBoolean(PREF_KEY_HIDE_AUTOCOMPLETE_BUTTON,value).apply();
        }
    }

    public void saveMenuGamesList(ArrayList<Integer> list){
        putSharedIntList(PREF_KEY_MENU_GAMES,list);
    }

    public void saveMenuOrderList(ArrayList<Integer> list){
        putSharedIntList(PREF_KEY_MENU_ORDER,list);
    }

    public void copyToGameIndividualSettings(){
        savedGameData.edit().putBoolean(PREF_KEY_HIDE_MENU_BUTTON,getHideMenuButton()).apply();
        savedGameData.edit().putBoolean(PREF_KEY_HIDE_TIME,getSavedHideTime()).apply();
        savedGameData.edit().putBoolean(PREF_KEY_HIDE_SCORE,getSavedHideScore()).apply();
        savedGameData.edit().putBoolean(PREF_KEY_HIDE_AUTOCOMPLETE_BUTTON,getHideAutoCompleteButton()).apply();
        savedGameData.edit().putBoolean(PREF_KEY_4_COLOR_MODE,getSavedFourColorMode()).apply();

        savedGameData.edit().putString(PREF_KEY_MENU_BAR_POS_PORTRAIT,getSavedMenuBarPosPortrait()).apply();
        savedGameData.edit().putString(PREF_KEY_MENU_BAR_POS_LANDSCAPE,getSavedMenuBarPosLandscape()).apply();

        savedGameData.edit().putInt(PREF_KEY_GAME_LAYOUT_MARGINS_PORTRAIT,getSavedGameLayoutMarginsPortrait()).apply();
        savedGameData.edit().putInt(PREF_KEY_TEXT_COLOR,getSavedTextColor()).apply();
        savedGameData.edit().putInt(PREF_KEY_GAME_LAYOUT_MARGINS_LANDSCAPE,getSavedGameLayoutMarginsLandscape()).apply();
        savedGameData.edit().putString(PREF_KEY_BACKGROUND_COLOR,Integer.toString(getSavedBackgroundColor())).apply();
        savedGameData.edit().putInt(PREF_KEY_BACKGROUND_COLOR_TYPE,getSavedBackgroundColorType()).apply();
        savedGameData.edit().putInt(PREF_KEY_BACKGROUND_COLOR_CUSTOM,getSavedBackgroundCustomColor()).apply();
        savedGameData.edit().putInt(PREF_KEY_CARD_DRAWABLES,getSavedCardTheme()).apply();
        savedGameData.edit().putInt(PREF_KEY_CARD_BACKGROUND,getSavedCardBackground()).apply();
        savedGameData.edit().putInt(PREF_KEY_CARD_BACKGROUND_COLOR,getSavedCardBackgroundColor()).apply();
    }
}

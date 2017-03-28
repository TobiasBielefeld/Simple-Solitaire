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

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.games.AcesUp;
import de.tobiasbielefeld.solitaire.games.Canfield;
import de.tobiasbielefeld.solitaire.games.FortyEight;
import de.tobiasbielefeld.solitaire.games.Freecell;
import de.tobiasbielefeld.solitaire.games.Game;
import de.tobiasbielefeld.solitaire.games.Golf;
import de.tobiasbielefeld.solitaire.games.Gypsy;
import de.tobiasbielefeld.solitaire.games.Klondike;
import de.tobiasbielefeld.solitaire.games.Mod3;
import de.tobiasbielefeld.solitaire.games.Pyramid;
import de.tobiasbielefeld.solitaire.games.SimpleSimon;
import de.tobiasbielefeld.solitaire.games.Spider;
import de.tobiasbielefeld.solitaire.games.Tripeaks;
import de.tobiasbielefeld.solitaire.games.Yukon;

/**
 * Everything about loading a game should be here. If you want to add a game, just expand the switch
 * statements with a new case. But think of the order! Every game is alphabetically ordered.
 * The order is important for the methods which returns ArrayLists.
 */

public class LoadGame {

    private String gameName;
    private String sharedPrefName;

    /**
     * load the game class and set the shown name and the name used for the sharedPref of the
     * current game. These names can be the same, but i used different ones and i don't want
     * to loose the saved data, if I changed that.
     *
     * @param activity The activity to get the strings from the xml file
     * @param buttonID The pressed button
     */

    public Game loadClass(Activity activity, int buttonID) {

        Game game;

        switch (buttonID) {
            default:
                Log.e("LoadGame.loadClass()", "Your games seems not to be added here?");
            case R.id.buttonStartAcesUp:
                sharedPrefName = "AcesUp";
                gameName = activity.getString(R.string.games_acesup);
                game = new AcesUp();
                break;
                //fall through
            case R.id.buttonStartCanfield:
                sharedPrefName = "Canfield";
                gameName = activity.getString(R.string.games_canfield);
                game = new Canfield();
                break;
            case R.id.buttonStartFortyEight:
                sharedPrefName = "FortyEight";
                gameName = activity.getString(R.string.games_fortyeight);
                game = new FortyEight();
                break;
            case R.id.buttonStartFreecell:
                sharedPrefName = "Freecell";
                gameName = activity.getString(R.string.games_freecell);
                game = new Freecell();
                break;
            case R.id.buttonStartGolf:
                sharedPrefName = "Golf";
                gameName = activity.getString(R.string.games_golf);
                game = new Golf();
                break;
            case R.id.buttonStartGypsy:
                sharedPrefName = "Gypsy";
                gameName = activity.getString(R.string.games_gypsy);
                game = new Gypsy();
                break;
            case R.id.buttonStartKlondike:
                sharedPrefName = "Klondike";
                gameName = activity.getString(R.string.games_klondike);
                game = new Klondike();
                break;
            case R.id.buttonStartMod3:
                sharedPrefName = "mod3";
                gameName = activity.getString(R.string.games_mod3);
                game = new Mod3();
                break;
            case R.id.buttonStartPyramid:
                sharedPrefName = "Pyramid";
                gameName = activity.getString(R.string.games_pyramid);
                game = new Pyramid();
                break;
            case R.id.buttonStartSimpleSimon:
                sharedPrefName = "SimpleSimon";
                gameName = activity.getString(R.string.games_simplesimon);
                game = new SimpleSimon();
                break;
            case R.id.buttonStartSpider:
                sharedPrefName = "Spider";
                gameName = activity.getString(R.string.games_spider);
                game = new Spider();
                break;
            case R.id.buttonStartTriPeaks:
                sharedPrefName = "TriPeaks";
                gameName = activity.getString(R.string.games_tripeaks);
                game = new Tripeaks();
                break;
            case R.id.buttonStartYukon:
                sharedPrefName = "Yukon";
                gameName = activity.getString(R.string.games_yukon);
                game = new Yukon();
                break;
        }

        return game;
    }

    /**
     * Loads the game list for the game selector.
     *
     * @param activity Activity to get the id's
     * @return An array list with each layout
     */
    public ArrayList<LinearLayout> loadLayouts(Activity activity) {
        ArrayList<LinearLayout> layouts = new ArrayList<>();

        //This is the exact same order like the games are shown in the main menu!
        layouts.add((LinearLayout) activity.findViewById(R.id.layout_acesup));
        layouts.add((LinearLayout) activity.findViewById(R.id.layout_canfield));
        layouts.add((LinearLayout) activity.findViewById(R.id.layout_fortyeight));
        layouts.add((LinearLayout) activity.findViewById(R.id.layout_freecell));
        layouts.add((LinearLayout) activity.findViewById(R.id.layout_golf));
        layouts.add((LinearLayout) activity.findViewById(R.id.layout_gypsy));
        layouts.add((LinearLayout) activity.findViewById(R.id.layout_klondike));
        layouts.add((LinearLayout) activity.findViewById(R.id.layout_mod3));
        layouts.add((LinearLayout) activity.findViewById(R.id.layout_pyramid));
        layouts.add((LinearLayout) activity.findViewById(R.id.layout_simplesimon));
        layouts.add((LinearLayout) activity.findViewById(R.id.layout_spider));
        layouts.add((LinearLayout) activity.findViewById(R.id.layout_tripeaks));
        layouts.add((LinearLayout) activity.findViewById(R.id.layout_yukon));

        return layouts;
    }

    /**
     * Loads the buttons for the manual and applies the onClickListener.
     *
     * @param view The view, where to search the buttons
     * @param listener The listener to apply
     */
    public void loadManualButtons(View view, View.OnClickListener listener) {
        ArrayList<Button> gameButtons = new ArrayList<>();

        gameButtons.add((Button) view.findViewById(R.id.manual_games_button_acesup));
        gameButtons.add((Button) view.findViewById(R.id.manual_games_button_canfield));
        gameButtons.add((Button) view.findViewById(R.id.manual_games_button_fortyeight));
        gameButtons.add((Button) view.findViewById(R.id.manual_games_button_freecell));
        gameButtons.add((Button) view.findViewById(R.id.manual_games_button_golf));
        gameButtons.add((Button) view.findViewById(R.id.manual_games_button_gypsy));
        gameButtons.add((Button) view.findViewById(R.id.manual_games_button_klondike));
        gameButtons.add((Button) view.findViewById(R.id.manual_games_button_mod3));
        gameButtons.add((Button) view.findViewById(R.id.manual_games_button_pyramid));
        gameButtons.add((Button) view.findViewById(R.id.manual_games_button_simplesimon));
        gameButtons.add((Button) view.findViewById(R.id.manual_games_button_spider));
        gameButtons.add((Button) view.findViewById(R.id.manual_games_button_tripeaks));
        gameButtons.add((Button) view.findViewById(R.id.manual_games_button_yukon));

        for (Button button : gameButtons) {
            button.setOnClickListener(listener);
        }
    }

    /**
     * Loads the game list for the Show/Hide games preference.
     *
     * @param view The view, where to search the layouts
     * @return The array list of the entries
     */
    public ArrayList<LinearLayout> loadMenuPreferenceViews(View view) {
        ArrayList<LinearLayout> linearLayouts = new ArrayList<>();

        linearLayouts.add((LinearLayout) view.findViewById(R.id.menu_linearLayout_1));
        linearLayouts.add((LinearLayout) view.findViewById(R.id.menu_linearLayout_2));
        linearLayouts.add((LinearLayout) view.findViewById(R.id.menu_linearLayout_3));
        linearLayouts.add((LinearLayout) view.findViewById(R.id.menu_linearLayout_4));
        linearLayouts.add((LinearLayout) view.findViewById(R.id.menu_linearLayout_5));
        linearLayouts.add((LinearLayout) view.findViewById(R.id.menu_linearLayout_6));
        linearLayouts.add((LinearLayout) view.findViewById(R.id.menu_linearLayout_7));
        linearLayouts.add((LinearLayout) view.findViewById(R.id.menu_linearLayout_8));
        linearLayouts.add((LinearLayout) view.findViewById(R.id.menu_linearLayout_9));
        linearLayouts.add((LinearLayout) view.findViewById(R.id.menu_linearLayout_10));
        linearLayouts.add((LinearLayout) view.findViewById(R.id.menu_linearLayout_11));
        linearLayouts.add((LinearLayout) view.findViewById(R.id.menu_linearLayout_12));
        linearLayouts.add((LinearLayout) view.findViewById(R.id.menu_linearLayout_13));

        return linearLayouts;
    }

    /**
     * Loads the game list for the Show/Hide games preference, but only the checkboxes.
     *
     * @param view The view, where to search the layouts
     * @return The array list of the checkboxes
     */
    public ArrayList<CheckBox> loadMenuPreferenceCheckBoxes(View view) {
        ArrayList<CheckBox> checkBoxes = new ArrayList<>();

        checkBoxes.add((CheckBox) view.findViewById(R.id.menu_checkBox_1));
        checkBoxes.add((CheckBox) view.findViewById(R.id.menu_checkBox_2));
        checkBoxes.add((CheckBox) view.findViewById(R.id.menu_checkBox_3));
        checkBoxes.add((CheckBox) view.findViewById(R.id.menu_checkBox_4));
        checkBoxes.add((CheckBox) view.findViewById(R.id.menu_checkBox_5));
        checkBoxes.add((CheckBox) view.findViewById(R.id.menu_checkBox_6));
        checkBoxes.add((CheckBox) view.findViewById(R.id.menu_checkBox_7));
        checkBoxes.add((CheckBox) view.findViewById(R.id.menu_checkBox_8));
        checkBoxes.add((CheckBox) view.findViewById(R.id.menu_checkBox_9));
        checkBoxes.add((CheckBox) view.findViewById(R.id.menu_checkBox_10));
        checkBoxes.add((CheckBox) view.findViewById(R.id.menu_checkBox_11));
        checkBoxes.add((CheckBox) view.findViewById(R.id.menu_checkBox_12));
        checkBoxes.add((CheckBox) view.findViewById(R.id.menu_checkBox_13));

        return checkBoxes;
    }

    /**
     * returns the prefix of the manual entries for the games. The strings have the following structure:
     * manual_<game name>_rules , manual_<game name>_points and so on.
     *
     * @param id The clicked manual entry
     * @return The prefix for the strings
     */
    public String manualClick(int id) {
        switch (id) {
            default:
                Log.e("LoadGame.manualClick()", "Your games seems not to be added here?");
            case R.id.manual_games_button_acesup:
                return "acesup";
            case R.id.manual_games_button_canfield:
                return "canfield";
            case R.id.manual_games_button_fortyeight:
                return "fortyeight";
            case R.id.manual_games_button_freecell:
                return "freecell";
            case R.id.manual_games_button_golf:
                return "golf";
            case R.id.manual_games_button_gypsy:
                return "gypsy";
            case R.id.manual_games_button_klondike:
                return "klondike";
            case R.id.manual_games_button_mod3:
                return "mod3";
            case R.id.manual_games_button_pyramid:
                return "pyramid";
            case R.id.manual_games_button_simplesimon:
                return "simplesimon";
            case R.id.manual_games_button_spider:
                return "spider";
            case R.id.manual_games_button_tripeaks:
                return "tripeaks";
            case R.id.manual_games_button_yukon:
                return "yukon";

        }
    }

    public String getGameName() {
        return gameName;
    }

    public String getSharedPrefName() {
        return sharedPrefName;
    }
}

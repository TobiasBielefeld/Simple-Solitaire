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
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.StringTokenizer;

import de.tobiasbielefeld.solitaire.games.AcesUp;
import de.tobiasbielefeld.solitaire.games.Canfield;
import de.tobiasbielefeld.solitaire.games.FortyEight;
import de.tobiasbielefeld.solitaire.games.Freecell;
import de.tobiasbielefeld.solitaire.games.Game;
import de.tobiasbielefeld.solitaire.games.Golf;
import de.tobiasbielefeld.solitaire.games.GrandfathersClock;
import de.tobiasbielefeld.solitaire.games.Gypsy;
import de.tobiasbielefeld.solitaire.games.Klondike;
import de.tobiasbielefeld.solitaire.games.Mod3;
import de.tobiasbielefeld.solitaire.games.Pyramid;
import de.tobiasbielefeld.solitaire.games.SimpleSimon;
import de.tobiasbielefeld.solitaire.games.Spider;
import de.tobiasbielefeld.solitaire.games.TriPeaks;
import de.tobiasbielefeld.solitaire.games.Vegas;
import de.tobiasbielefeld.solitaire.games.Yukon;

import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_MENU_ORDER;
import static de.tobiasbielefeld.solitaire.SharedData.savedSharedData;

/**
 * Everything about loading a game should be here. If you want to add a game, just expand the switch
 * statements with a new case. But think of the order! Every game is alphabetically ordered.
 * The order is important for the methods which returns ArrayLists.
 */

public class LoadGame {

    private String gameName;
    private String sharedPrefName;
    private ArrayList<AllGameInfos> allGameInfos = new ArrayList<>();
    private int GAME_COUNT;

    public ArrayList<Integer> getOrderedGameList(){
        String s = savedSharedData.getString(PREF_KEY_MENU_ORDER, "");
        StringTokenizer st = new StringTokenizer(s, ",");
        ArrayList<Integer> result = new ArrayList<>();

        while (st.hasMoreTokens()) {
            result.add(Integer.parseInt(st.nextToken()));
        }

        if (result.isEmpty()){
            for (int i=0;i<getGameCount();i++){
                result.add(i);
            }
        }

        return result;
    }

    public int getGameCount(){
        return GAME_COUNT;
    }

    public String[] getDefaultGameList(Resources res){
        String[] list = new String[allGameInfos.size()];

        for (int i=0;i<allGameInfos.size();i++){
            list[i] = allGameInfos.get(i).getName(res);
        }

        return list;
    }

    public ArrayList<String> getAllGameNames(Resources res){

        ArrayList<Integer> savedList = getOrderedGameList();
        ArrayList<String> returnList = new ArrayList<>();
        String[] defaultList = getDefaultGameList(res);

        for (int i=0;i<defaultList.length;i++){
            returnList.add(defaultList[savedList.indexOf(i)]);
        }

        return returnList;
    }

    /**
     * Loads the game list for the game selector.
     *
     * @param activity Activity to get the id's
     * @return An array list with each layout
     */
    public ArrayList<ImageView> loadImageViews(Activity activity) {
        ArrayList<ImageView> imageViews = new ArrayList<>();

        for (AllGameInfos gameInfo : allGameInfos){
            imageViews.add(gameInfo.getGameSelectorButton(activity));
        }

        return imageViews;
    }

    /**
     * Loads the buttons for the manual and applies the onClickListener.
     *
     * @param view     The view, where to search the buttons
     * @param listener The listener to apply
     */
    public void loadManualButtons(View view, View.OnClickListener listener) {
        for (AllGameInfos gameInfo : allGameInfos){
            gameInfo.setManualButtonListener(view,listener);
        }
    }

    /**
     * returns the prefix of the manual entries for the games. The strings have the following structure:
     * manual_<game name>_rules , manual_<game name>_points and so on.
     * <p>
     * The game name needs to have the same structure! games_<game name>
     *
     * @param id The clicked manual entry
     * @return The prefix for the strings
     */
    public String manualClick(int id) {

        for (AllGameInfos gameInfos : allGameInfos){
            if (gameInfos.getManualButtonResID() == id){
                return gameInfos.getSharedPrefName();
            }
        }

        return "";
    }

    /**
     * Returns the string prefix of the manual entries of a given game.
     * Used for the direct link to the manual from the in game menu.
     * (It only has the shown game name, which is different to the manaul prefix name)
     *
     * @return              the string for the manual entries
     */
    public String manualName() {
       return sharedPrefName;
    }

    public String getGameName() {
        return gameName;
    }

    public String getSharedPrefName() {
        return sharedPrefName;
    }

    private class AllGameInfos{

        private int shownNameResID;
        private String sharedPrefName;
        private int gameSelectorButtonResID;
        private int manualButtonResID;

        AllGameInfos(int shownNameResID, String sharedPrefName, int gameSelectorButtonResID, int manualButtonResID){
            this.shownNameResID = shownNameResID;
            this.sharedPrefName = sharedPrefName;
            this.gameSelectorButtonResID = gameSelectorButtonResID;
            this.manualButtonResID = manualButtonResID;
        }

        public String getName(Resources res){
            return res.getString(shownNameResID);
        }

        public String getSharedPrefName(){
            return sharedPrefName;
        }

        public ImageView getGameSelectorButton(Activity activity){
            return (ImageView) activity.findViewById(gameSelectorButtonResID);
        }

        public void setManualButtonListener(View view, View.OnClickListener listener){
            (view.findViewById(manualButtonResID)).setOnClickListener(listener);
        }

        public int getManualButtonResID(){
            return manualButtonResID;
        }

        public int getGameSelectorButtonResID(){
            return gameSelectorButtonResID;
        }
    }

    /**
     * load the game class and set the shown name and the name used for the sharedPref of the
     * current game. These names can be the same, but i used different ones and i don't want
     * to loose the saved data, if I changed that.
     *
     * @param activity The activity to get the strings from the xml file
     * @param buttonID The pressed button
     */
    public Game loadClass(Activity activity, int buttonID) {

        for (AllGameInfos gameInfos : allGameInfos){
            if (gameInfos.getGameSelectorButtonResID() == buttonID){
                sharedPrefName = gameInfos.getSharedPrefName();
                gameName = gameInfos.getName(activity.getResources());
            }
        }

        switch (buttonID) {
            default:
                Log.e("LoadGame.loadClass()", "Your games seems not to be added here?");
                //fallthrough
            case R.id.buttonStartAcesUp:
                return new AcesUp();
            case R.id.buttonStartCanfield:
                return new Canfield();
            case R.id.buttonStartFortyEight:
                return new FortyEight();
            case R.id.buttonStartFreecell:
                return new Freecell();
            case R.id.buttonStartGolf:
                return new Golf();
            case R.id.buttonStartGrandfathersClock:
                return new GrandfathersClock();
            case R.id.buttonStartGypsy:
                return new Gypsy();
            case R.id.buttonStartKlondike:
                return new Klondike();
            case R.id.buttonStartMod3:
                return new Mod3();
            case R.id.buttonStartPyramid:
                return new Pyramid();
            case R.id.buttonStartSimpleSimon:
                return new SimpleSimon();
            case R.id.buttonStartSpider:
                return new Spider();
            case R.id.buttonStartTriPeaks:
                return new TriPeaks();
            case R.id.buttonStartYukon:
                return new Yukon();
            case R.id.buttonStartVegas:
                return new Vegas();
        }
    }

    public void loadAllGames(){
        allGameInfos.add(new AllGameInfos(R.string.games_AcesUp,"AcesUp",R.id.buttonStartAcesUp,R.id.manual_games_button_acesup));
        allGameInfos.add(new AllGameInfos(R.string.games_Canfield,"Canfield",R.id.buttonStartCanfield,R.id.manual_games_button_canfield));
        allGameInfos.add(new AllGameInfos(R.string.games_FortyEight,"FortyEight",R.id.buttonStartFortyEight,R.id.manual_games_button_fortyeight));
        allGameInfos.add(new AllGameInfos(R.string.games_Freecell,"Freecell",R.id.buttonStartFreecell,R.id.manual_games_button_freecell));
        allGameInfos.add(new AllGameInfos(R.string.games_Golf,"Golf",R.id.buttonStartGolf,R.id.manual_games_button_golf));
        allGameInfos.add(new AllGameInfos(R.string.games_GrandfathersClock,"GrandfathersClock",R.id.buttonStartGrandfathersClock,R.id.manual_games_button_grandfathers_clock));
        allGameInfos.add(new AllGameInfos(R.string.games_Gypsy,"Gypsy",R.id.buttonStartGypsy,R.id.manual_games_button_gypsy));
        allGameInfos.add(new AllGameInfos(R.string.games_Klondike,"Klondike",R.id.buttonStartKlondike,R.id.manual_games_button_klondike));
        allGameInfos.add(new AllGameInfos(R.string.games_mod3,"mod3",R.id.buttonStartMod3,R.id.manual_games_button_mod3));
        allGameInfos.add(new AllGameInfos(R.string.games_Pyramid,"Pyramid",R.id.buttonStartPyramid,R.id.manual_games_button_pyramid));
        allGameInfos.add(new AllGameInfos(R.string.games_SimpleSimon,"SimpleSimon",R.id.buttonStartSimpleSimon,R.id.manual_games_button_simplesimon));
        allGameInfos.add(new AllGameInfos(R.string.games_Spider,"Spider",R.id.buttonStartSpider,R.id.manual_games_button_spider));
        allGameInfos.add(new AllGameInfos(R.string.games_TriPeaks,"TriPeaks",R.id.buttonStartTriPeaks,R.id.manual_games_button_tripeaks));
        allGameInfos.add(new AllGameInfos(R.string.games_Vegas,"Vegas",R.id.buttonStartVegas,R.id.manual_games_button_vegas));
        allGameInfos.add(new AllGameInfos(R.string.games_Yukon,"Yukon",R.id.buttonStartYukon,R.id.manual_games_button_yukon));

        GAME_COUNT = allGameInfos.size();
    }
}

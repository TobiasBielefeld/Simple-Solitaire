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

package de.tobiasbielefeld.solitaire.ui.settings;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomPreferenceFragment;
import de.tobiasbielefeld.solitaire.games.Calculation;
import de.tobiasbielefeld.solitaire.games.FortyEight;
import de.tobiasbielefeld.solitaire.games.Klondike;
import de.tobiasbielefeld.solitaire.games.Pyramid;
import de.tobiasbielefeld.solitaire.games.Vegas;
import de.tobiasbielefeld.solitaire.handler.HandlerStopBackgroundMusic;

import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_CALCULATION_ALTERNATIVE;
import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_FORTYEIGHT_NUMBER_OF_RECYCLES;
import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_KLONDIKE_NUMBER_OF_RECYCLES;
import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_ORIENTATION;
import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_PYRAMID_NUMBER_OF_RECYCLES;
import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_VEGAS_BET_AMOUNT;
import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_VEGAS_NUMBER_OF_RECYCLES;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_CALCULATION_ALTERNATIVE;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_CANFIELD_DRAW;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_FORTYEIGHT_LIMITED_RECYCLES;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_FORTYEIGHT_NUMBER_OF_RECYCLES;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_KLONDIKE_DRAW;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_KLONDIKE_LIMITED_RECYCLES;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_KLONDIKE_NUMBER_OF_RECYCLES;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_ORIENTATION;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_PYRAMID_LIMITED_RECYCLES;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_PYRAMID_NUMBER_OF_RECYCLES;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_SPIDER_DIFFICULTY;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_VEGAS_BET_AMOUNT;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_VEGAS_DRAW;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_VEGAS_NUMBER_OF_RECYCLES;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_YUKON_RULES;
import static de.tobiasbielefeld.solitaire.SharedData.activityCounter;
import static de.tobiasbielefeld.solitaire.SharedData.backgroundSound;
import static de.tobiasbielefeld.solitaire.SharedData.currentGame;
import static de.tobiasbielefeld.solitaire.SharedData.gameLogic;
import static de.tobiasbielefeld.solitaire.SharedData.getSharedBoolean;
import static de.tobiasbielefeld.solitaire.SharedData.getSharedInt;
import static de.tobiasbielefeld.solitaire.SharedData.getSharedString;
import static de.tobiasbielefeld.solitaire.SharedData.isXLargeTablet;
import static de.tobiasbielefeld.solitaire.SharedData.reinitializeData;
import static de.tobiasbielefeld.solitaire.SharedData.savedSharedData;

/**
 * Settings activity created with the "Create settings activity" tool from Android Studio.
 */

public class SettingsGames extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Toast toast;
    private Preference preferenceVegasBetAmount;

    HandlerStopBackgroundMusic handlerStopBackgroundMusic = new HandlerStopBackgroundMusic();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ViewGroup) getListView().getParent()).setPadding(0, 0, 0, 0);                             //remove huge padding in landscape

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        reinitializeData(getApplicationContext());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //only item is the back arrow
        finish();
        return true;
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers_games, target);
    }

    @Override
    public void onResume() {
        super.onResume();

        savedSharedData.registerOnSharedPreferenceChangeListener(this);
        showOrHideStatusBar();
        setOrientation();

        activityCounter++;
        backgroundSound.doInBackground(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        savedSharedData.unregisterOnSharedPreferenceChangeListener(this);

        activityCounter--;
        handlerStopBackgroundMusic.sendEmptyMessageDelayed(0, 100);
    }

    /*
     * Update settings when the shared preferences get new values. It uses a lot of if/else instead
     * of switch/case because only this way i can use getString() to get the xml values, otherwise
     * I would need to write the strings manually in the cases.
     */
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PREF_KEY_KLONDIKE_DRAW)) {
            showToast(getString(R.string.settings_restart_klondike));

        } else if (key.equals(PREF_KEY_VEGAS_DRAW)) {
            showToast(getString(R.string.settings_restart_vegas));

        } else if (key.equals(PREF_KEY_CANFIELD_DRAW)) {
            showToast(getString(R.string.settings_restart_canfield));

        } else if (key.equals(PREF_KEY_SPIDER_DIFFICULTY)) {
            showToast(getString(R.string.settings_restart_spider));

        } else if (key.equals(PREF_KEY_YUKON_RULES)) {
            showToast(getString(R.string.settings_restart_yukon));

        } else if (key.equals(PREF_KEY_FORTYEIGHT_LIMITED_RECYCLES)) {
            if (currentGame instanceof FortyEight) {
                gameLogic.toggleRecycles();
            }

        } else if (key.equals(PREF_KEY_PYRAMID_LIMITED_RECYCLES)) {
            if (currentGame instanceof Pyramid) {
                gameLogic.toggleRecycles();
            }

        } else if (key.equals(PREF_KEY_PYRAMID_NUMBER_OF_RECYCLES)){
            if (currentGame instanceof Pyramid) {
                gameLogic.setNumberOfRecycles(key,DEFAULT_PYRAMID_NUMBER_OF_RECYCLES);
            }

        } else if (key.equals(PREF_KEY_FORTYEIGHT_NUMBER_OF_RECYCLES)){
            if (currentGame instanceof FortyEight) {
                gameLogic.setNumberOfRecycles(key,DEFAULT_FORTYEIGHT_NUMBER_OF_RECYCLES);
            }

        } else if (key.equals(PREF_KEY_VEGAS_NUMBER_OF_RECYCLES)) {
            if (currentGame instanceof Vegas) {
                gameLogic.setNumberOfRecycles(key, DEFAULT_VEGAS_NUMBER_OF_RECYCLES);
            }

        } else if (key.equals(PREF_KEY_VEGAS_BET_AMOUNT)){
            updatePreferenceVegasBetAmountSummary();
            showToast(getString(R.string.settings_restart_vegas));

        } else if (key.equals(PREF_KEY_KLONDIKE_LIMITED_RECYCLES)) {
            if (currentGame instanceof Klondike) {
                gameLogic.toggleRecycles();
            }

        } else if (key.equals(PREF_KEY_KLONDIKE_NUMBER_OF_RECYCLES)){
            if (currentGame instanceof Klondike) {
                gameLogic.setNumberOfRecycles(key,DEFAULT_KLONDIKE_NUMBER_OF_RECYCLES);
            }

        } else if (key.equals(PREF_KEY_CALCULATION_ALTERNATIVE)){
            showToast(getString(R.string.settings_restart_calculation));

        }
    }

    /**
     * Tests if a loaded fragment is valid
     *
     * @param fragmentName The name of the fragment to test
     * @return True if it's valid, false otherwise
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || CalculationPreferenceFragment.class.getName().equals(fragmentName)
                || CanfieldPreferenceFragment.class.getName().equals(fragmentName)
                || FortyEightPreferenceFragment.class.getName().equals(fragmentName)
                || GolfPreferenceFragment.class.getName().equals(fragmentName)
                || KlondikePreferenceFragment.class.getName().equals(fragmentName)
                || PyramidPreferenceFragment.class.getName().equals(fragmentName)
                || VegasPreferenceFragment.class.getName().equals(fragmentName)
                || YukonPreferenceFragment.class.getName().equals(fragmentName)
                || SpiderPreferenceFragment.class.getName().equals(fragmentName)
                || Mod3PreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * Applies the user setting of the screen orientation.
     */
    private void setOrientation() {
        switch (getSharedString(PREF_KEY_ORIENTATION, DEFAULT_ORIENTATION)) {
            case "1": //follow system settings
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                break;
            case "2": //portrait
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case "3": //landscape
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case "4": //landscape upside down
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
        }
    }

    /**
     * Applies the user setting of the status bar.
     */
    private void showOrHideStatusBar() {
        if (getSharedBoolean(getString(R.string.pref_key_hide_status_bar), false))
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Shows the given text as a toast. New texts override the old one.
     *
     * @param text The text to show
     */
    private void showToast(String text) {
        if (toast == null) {
            toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        } else
            toast.setText(text);

        toast.show();
    }

    private void updatePreferenceVegasBetAmountSummary(){
        int amount = getSharedInt(PREF_KEY_VEGAS_BET_AMOUNT,DEFAULT_VEGAS_BET_AMOUNT);

        preferenceVegasBetAmount.setSummary(String.format(Locale.getDefault(),getString(R.string.settings_vegas_bet_amount_summary),amount*10,amount));
    }

    public static class CalculationPreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_games_calculation);
            setHasOptionsMenu(true);
        }
    }

    public static class CanfieldPreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_games_canfield);
            setHasOptionsMenu(true);
        }
    }

    public static class FortyEightPreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_games_forty_eight);
            setHasOptionsMenu(true);
        }
    }

    public static class GolfPreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_games_golf);
            setHasOptionsMenu(true);
        }
    }

    public static class KlondikePreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_games_klondike);
            setHasOptionsMenu(true);
        }
    }

    public static class PyramidPreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_games_pyramid);
            setHasOptionsMenu(true);
        }
    }

    public static class SpiderPreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_games_spider);
            setHasOptionsMenu(true);
        }
    }

    public static class VegasPreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_games_vegas);
            setHasOptionsMenu(true);

            SettingsGames settings = (SettingsGames) getActivity();

            settings.preferenceVegasBetAmount = findPreference(getString(R.string.pref_key_vegas_bet_amount));
            settings.updatePreferenceVegasBetAmountSummary();
        }
    }

    public static class YukonPreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_games_yukon);
            setHasOptionsMenu(true);
        }
    }

    public static class Mod3PreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_games_mod3);
            setHasOptionsMenu(true);
        }
    }
}

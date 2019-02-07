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
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.view.ViewGroup;

import java.util.List;
import java.util.Locale;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomPreferenceFragment;
import de.tobiasbielefeld.solitaire.games.FortyEight;
import de.tobiasbielefeld.solitaire.games.Klondike;
import de.tobiasbielefeld.solitaire.games.NapoleonsTomb;
import de.tobiasbielefeld.solitaire.games.Pyramid;
import de.tobiasbielefeld.solitaire.games.Vegas;

import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.helper.Preferences.*;

/**
 * Settings activity created with the "Create settings activity" tool from Android Studio.
 */

public class SettingsGames extends AppCompatPreferenceActivity {

    private Preference preferenceVegasBetAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        reinitializeData(getApplicationContext());
        super.onCreate(savedInstanceState);

        ((ViewGroup) getListView().getParent()).setPadding(0, 0, 0, 0);                             //remove huge padding in landscape

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        prefs.setCriticalGameSettings();
    }

    @Override
    public boolean onIsMultiPane() {
        return isLargeTablet(this);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers_games, target);
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.unregisterListener(this);

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
            showToast(String.format(getString(R.string.settings_restart_game), getString(R.string.games_Klondike)), this);

        } else if (key.equals(PREF_KEY_VEGAS_DRAW)) {
            showToast(String.format(getString(R.string.settings_restart_game), getString(R.string.games_Vegas)), this);

        } else if (key.equals(PREF_KEY_CANFIELD_DRAW)) {
            showToast(String.format(getString(R.string.settings_restart_game), getString(R.string.games_Canfield)), this);

        } else if (key.equals(PREF_KEY_SPIDER_DIFFICULTY)) {
            showToast(String.format(getString(R.string.settings_restart_game), getString(R.string.games_Spider)), this);

        } else if (key.equals(PREF_KEY_SPIDERETTE_DIFFICULTY)) {
            showToast(String.format(getString(R.string.settings_restart_game), getString(R.string.games_Spiderette)), this);

        } else if (key.equals(PREF_KEY_YUKON_RULES)) {
            showToast(String.format(getString(R.string.settings_restart_game), getString(R.string.games_Yukon)), this);

        } else if (key.equals(PREF_KEY_FORTYEIGHT_LIMITED_RECYCLES)) {
            if (currentGame instanceof FortyEight) {
                gameLogic.toggleRecycles(prefs.getSavedFortyEightLimitedRecycles());
            }

        } else if (key.equals(PREF_KEY_PYRAMID_LIMITED_RECYCLES)) {
            if (currentGame instanceof Pyramid) {
                gameLogic.toggleRecycles(prefs.getSavedPyramidLimitedRecycles());
            }

        } else if (key.equals(PREF_KEY_PYRAMID_NUMBER_OF_RECYCLES)) {
            if (currentGame instanceof Pyramid) {
                gameLogic.setNumberOfRecycles(key, DEFAULT_PYRAMID_NUMBER_OF_RECYCLES);
            }

        } else if (key.equals(PREF_KEY_NAPOLEONSTOMB_NUMBER_OF_RECYCLES)) {
            if (currentGame instanceof NapoleonsTomb) {
                gameLogic.setNumberOfRecycles(key, DEFAULT_NAPOLEONSTOMB_NUMBER_OF_RECYCLES);
            }

        } else if (key.equals(PREF_KEY_FORTYEIGHT_NUMBER_OF_RECYCLES)) {
            if (currentGame instanceof FortyEight) {
                gameLogic.setNumberOfRecycles(key, DEFAULT_FORTYEIGHT_NUMBER_OF_RECYCLES);
            }

        } else if (key.equals(PREF_KEY_VEGAS_NUMBER_OF_RECYCLES)) {
            if (currentGame instanceof Vegas) {
                gameLogic.setNumberOfRecycles(key, DEFAULT_VEGAS_NUMBER_OF_RECYCLES);
            }

        } else if (key.equals(PREF_KEY_VEGAS_BET_AMOUNT) || key.equals(PREF_KEY_VEGAS_WIN_AMOUNT)) {
            updatePreferenceVegasBetAmountSummary();
            showToast(String.format(getString(R.string.settings_restart_game), getString(R.string.games_Vegas)), this);

        } else if (key.equals(PREF_KEY_VEGAS_MONEY_ENABLED)) {
            if (!prefs.getSavedVegasSaveMoneyEnabled()) {
                prefs.saveVegasResetMoney(true);
            }

        } else if (key.equals(PREF_KEY_KLONDIKE_LIMITED_RECYCLES)) {
            if (currentGame instanceof Klondike) {
                gameLogic.toggleRecycles(prefs.getSavedKlondikeLimitedRecycles());
            }

        } else if (key.equals(PREF_KEY_KLONDIKE_NUMBER_OF_RECYCLES)) {
            if (currentGame instanceof Klondike) {
                gameLogic.setNumberOfRecycles(key, DEFAULT_KLONDIKE_NUMBER_OF_RECYCLES);
            }

        } else if (key.equals(PREF_KEY_CALCULATION_ALTERNATIVE)) {
            //showToast(String.format(getString(R.string.settings_restart_game), getString(R.string.games_Calculation)), this);
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
                || SpiderettePreferenceFragment.class.getName().equals(fragmentName)
                || Mod3PreferenceFragment.class.getName().equals(fragmentName)
                || NapoleonsTombPreferenceFragment.class.getName().equals(fragmentName);
    }

    private void updatePreferenceVegasBetAmountSummary() {
        int betAmount = prefs.getSavedVegasBetAmount();
        int winAmount = prefs.getSavedVegasWinAmount();

        preferenceVegasBetAmount.setSummary(String.format(Locale.getDefault(), getString(R.string.settings_vegas_bet_amount_summary), betAmount, winAmount));
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

    public static class SpiderettePreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_games_spiderette);
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

    public static class NapoleonsTombPreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_games_napoleons_tomb);
            setHasOptionsMenu(true);
        }
    }
}

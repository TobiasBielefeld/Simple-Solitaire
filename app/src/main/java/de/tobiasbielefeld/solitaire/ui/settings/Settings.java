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

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.security.SecureRandom;
import java.util.List;
import java.util.Locale;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CustomPreferenceFragment;
import de.tobiasbielefeld.solitaire.dialogs.DialogPreferenceCardDialog;
import de.tobiasbielefeld.solitaire.handler.HandlerStopBackgroundMusic;
import de.tobiasbielefeld.solitaire.helper.Sounds;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.helper.Preferences.*;

/**
 * Settings activity created with the "Create settings activity" tool from Android Studio.
 */

public class Settings extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Preference preferenceMenuBarPosition;
    private Preference preferenceMenuColumns;
    private Preference preferenceBackgroundVolume;
    private Preference preferenceMaxNumberUndos;
    private CheckBoxPreference preferenceSingleTapAllGames;
    private CheckBoxPreference preferenceTapToSelect;
    private DialogPreferenceCardDialog preferenceCards;
    private Sounds settingsSounds;

    HandlerStopBackgroundMusic handlerStopBackgroundMusic = new HandlerStopBackgroundMusic();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        reinitializeData(getApplicationContext());
        super.onCreate(savedInstanceState);

        ((ViewGroup) getListView().getParent()).setPadding(0, 0, 0, 0);                             //remove huge padding in landscape

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        prefs.setCriticalSettings();

        settingsSounds = new Sounds(this);
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        if (prefs.getShowAdvancedSettings()) {
            loadHeadersFromResource(R.xml.pref_headers_with_advanced_settings, target);
        } else {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        prefs.registerListener(this);
        showOrHideStatusBar();
        setOrientation();

        activityCounter++;
        backgroundSound.doInBackground(this);
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
        if (key.equals(PREF_KEY_CARD_DRAWABLES)) {
            Card.updateCardDrawableChoice();

        } else if (key.equals(PREF_KEY_CARD_BACKGROUND) || key.equals(PREF_KEY_CARD_BACKGROUND_COLOR)) {
            Card.updateCardBackgroundChoice();

        } else if (key.equals(PREF_KEY_HIDE_STATUS_BAR)) {
            showOrHideStatusBar();

        } else if (key.equals(PREF_KEY_ORIENTATION)) {
            setOrientation();

        } else if (key.equals(PREF_KEY_LEFT_HANDED_MODE)) {
            if (gameLogic != null) {
                gameLogic.mirrorStacks();
            }

        } else if (key.equals(PREF_KEY_MENU_COLUMNS_PORTRAIT) || key.equals(PREF_KEY_MENU_COLUMNS_LANDSCAPE)) {
            updatePreferenceMenuColumnsSummary();

        } else if (key.equals(PREF_KEY_LANGUAGE)) {
            bitmaps.resetMenuPreviews();
            restartApplication();

        } else if (key.equals(PREF_KEY_MENU_BAR_POS_LANDSCAPE) || key.equals(PREF_KEY_MENU_BAR_POS_PORTRAIT)) {
            updatePreferenceMenuBarPositionSummary();
            if (gameLogic != null) {
                gameLogic.updateMenuBar();
            }

        } else if (key.equals(PREF_KEY_4_COLOR_MODE)) {
            Card.updateCardDrawableChoice();

            if (preferenceCards!=null) {
                preferenceCards.updateSummary();
            }

        } else if (key.equals(PREF_KEY_MOVEMENT_SPEED)) {
            if (animate != null) {
                animate.updateMovementSpeed();
            }

        } else if (key.equals(PREF_KEY_WIN_SOUND)) {
            settingsSounds.playWinSound();

        } else if (key.equals(PREF_KEY_BACKGROUND_MUSIC) || key.equals(PREF_KEY_SOUND_ENABLED)) {
            backgroundSound.doInBackground(this);

        } else if (key.equals(PREF_KEY_BACKGROUND_VOLUME)){
            updatePreferenceBackgroundVolumeSummary();
            backgroundSound.doInBackground(this);

        } else if (key.equals(PREF_KEY_FORCE_TABLET_LAYOUT)){
            restartApplication();

        } else if (key.equals(PREF_KEY_HIDE_SCORE)) {
            if (scores!=null) {
                scores.output();
            }
        } else if (key.equals(PREF_KEY_SINGLE_TAP_ALL_GAMES)){
            if (sharedPreferences.getBoolean(key,false) && preferenceTapToSelect!=null) {
                preferenceTapToSelect.setChecked(false);
            }

        } else if (key.equals(PREF_KEY_TAP_TO_SELECT_ENABLED)){
            if (sharedPreferences.getBoolean(key,false) && preferenceSingleTapAllGames!=null) {
                preferenceSingleTapAllGames.setChecked(false);
            }

        } else if (key.equals(PREF_KEY_MAX_NUMBER_UNDOS)) {
            if (recordList !=null){
                recordList.setMaxRecords();
            }

            updatePreferenceMaxNumberUndos();
        } else if (key.equals(PREF_KEY_SHOW_ADVANCED_SETTINGS)) {
            final Intent intent = new Intent(getApplicationContext(), Settings.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            startActivity(intent);
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
                || CustomizationPreferenceFragment.class.getName().equals(fragmentName)
                || OtherPreferenceFragment.class.getName().equals(fragmentName)
                || MenuPreferenceFragment.class.getName().equals(fragmentName)
                || AdditionalMovementsPreferenceFragment.class.getName().equals(fragmentName)
                || SoundPreferenceFragment.class.getName().equals(fragmentName)
                || DeveloperOptionsPreferenceFragment.class.getName().equals(fragmentName)
                || ExpertSettingsPreferenceFragment.class.getName().equals(fragmentName);

    }

    /**
     * Applies the user setting of the screen orientation.
     */
    private void setOrientation() {
        switch (prefs.getSavedOrientation()) {
            case 1: //follow system settings
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                break;
            case 2: //portrait
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case 3: //landscape
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case 4: //landscape upside down
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
        }
    }

    /**
     * Applies the user setting of the status bar.
     */
    private void showOrHideStatusBar() {
        if (prefs.getSavedHideStatusBar()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * Restarts the app to apply the new locale settings
     */
    private void restartApplication() {
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());

        if (i!=null) {
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            startActivity(i);
        }
    }

    private void updatePreferenceMenuColumnsSummary() {
        int portraitValue = prefs.getSavedMenuColumnsPortrait();
        int landscapeValue = prefs.getSavedMenuColumnsLandscape();

        String text = String.format(Locale.getDefault(), "%s: %d\n%s: %d",
                getString(R.string.portrait), portraitValue, getString(R.string.landscape), landscapeValue);

        preferenceMenuColumns.setSummary(text);
    }

    private void updatePreferenceMaxNumberUndos() {
        int amount = prefs.getSavedMaxNumberUndos();

        preferenceMaxNumberUndos.setSummary(Integer.toString(amount));
    }

    private void updatePreferenceMenuBarPositionSummary() {
        String portrait, landscape;
        if (prefs.getSavedMenuBarPosPortrait().equals(DEFAULT_MENU_BAR_POSITION_PORTRAIT)) {
            portrait = getString(R.string.settings_menu_bar_position_bottom);
        } else {
            portrait = getString(R.string.settings_menu_bar_position_top);
        }

        if (prefs.getSavedMenuBarPosLandscape().equals(DEFAULT_MENU_BAR_POSITION_LANDSCAPE)) {
            landscape = getString(R.string.settings_menu_bar_position_right);
        } else {
            landscape = getString(R.string.settings_menu_bar_position_left);
        }

        String text = String.format(Locale.getDefault(), "%s: %s\n%s: %s",
                getString(R.string.portrait), portrait, getString(R.string.landscape), landscape);

        preferenceMenuBarPosition.setSummary(text);
    }

    private void updatePreferenceBackgroundVolumeSummary(){
        int volume = prefs.getSavedBackgroundVolume();

        preferenceBackgroundVolume.setSummary(String.format(Locale.getDefault(),"%s %%",volume));
    }

    public static class CustomizationPreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_customize);
            setHasOptionsMenu(true);

            Settings settings = (Settings) getActivity();

            settings.preferenceMenuBarPosition = findPreference(getString(R.string.pref_key_menu_bar_position));
            settings.preferenceCards = (DialogPreferenceCardDialog) findPreference(getString(R.string.pref_key_cards));

            settings.updatePreferenceMenuBarPositionSummary();
        }
    }

    public static class OtherPreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_other);
            setHasOptionsMenu(true);
        }
    }

    public static class SoundPreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_sounds);
            setHasOptionsMenu(true);


            Settings settings = (Settings) getActivity();

            settings.preferenceBackgroundVolume = findPreference(getString(R.string.pref_key_background_volume));

            settings.updatePreferenceBackgroundVolumeSummary();
        }
    }



    public static class MenuPreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_menu);
            setHasOptionsMenu(true);

            Settings settings = (Settings) getActivity();

            settings.preferenceMenuColumns = findPreference(getString(R.string.pref_key_menu_columns));
            settings.updatePreferenceMenuColumnsSummary();
        }
    }

    public static class AdditionalMovementsPreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_movement_methods);
            setHasOptionsMenu(true);

            Settings settings = (Settings) getActivity();

            settings.preferenceSingleTapAllGames = (CheckBoxPreference) findPreference(getString(R.string.pref_key_single_tap_all_games));
            settings.preferenceTapToSelect = (CheckBoxPreference) findPreference(getString(R.string.pref_key_tap_to_select_enable));
        }
    }

    public static class DeveloperOptionsPreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_developer_options);
            setHasOptionsMenu(true);
        }
    }

    public static class ExpertSettingsPreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_expert_settings);
            setHasOptionsMenu(true);

            Settings settings = (Settings) getActivity();

            settings.preferenceMaxNumberUndos = findPreference(getString(R.string.pref_key_max_number_undos));
            settings.updatePreferenceMaxNumberUndos();
        }
    }
}

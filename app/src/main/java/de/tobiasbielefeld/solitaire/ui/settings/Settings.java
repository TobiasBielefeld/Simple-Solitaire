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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.tobiasbielefeld.solitaire.LoadGame;
import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.checkboxpreferences.CheckBoxPreferenceFourColorMode;
import de.tobiasbielefeld.solitaire.checkboxpreferences.CheckBoxPreferenceHideAutoCompleteButton;
import de.tobiasbielefeld.solitaire.checkboxpreferences.CheckBoxPreferenceHideMenuButton;
import de.tobiasbielefeld.solitaire.checkboxpreferences.CheckBoxPreferenceHideScore;
import de.tobiasbielefeld.solitaire.checkboxpreferences.CheckBoxPreferenceHideTime;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CustomPreferenceFragment;
import de.tobiasbielefeld.solitaire.dialogs.DialogPreferenceBackgroundColor;
import de.tobiasbielefeld.solitaire.dialogs.DialogPreferenceCardBackground;
import de.tobiasbielefeld.solitaire.dialogs.DialogPreferenceCards;
import de.tobiasbielefeld.solitaire.dialogs.DialogPreferenceOnlyForThisGame;
import de.tobiasbielefeld.solitaire.dialogs.DialogPreferenceTextColor;
import de.tobiasbielefeld.solitaire.helper.Sounds;

import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.helper.Preferences.*;

/**
 * Settings activity created with the "Create settings activity" tool from Android Studio.
 */

public class Settings extends AppCompatPreferenceActivity {

    private Preference preferenceMenuBarPosition;
    private Preference preferenceMenuColumns;
    private Preference preferenceBackgroundVolume;
    private Preference preferenceMaxNumberUndos;
    private Preference preferenceGameLayoutMargins;

    private CheckBoxPreference preferenceSingleTapAllGames;
    private CheckBoxPreference preferenceTapToSelect;
    private CheckBoxPreference preferenceImmersiveMode;

    private DialogPreferenceCards preferenceCards;
    private DialogPreferenceCardBackground preferenceCardBackground;
    private DialogPreferenceBackgroundColor preferenceBackgroundColor;
    private DialogPreferenceTextColor preferenceTextColor;
    private DialogPreferenceOnlyForThisGame dialogPreferenceOnlyForThisGame;

    private CheckBoxPreferenceFourColorMode preferenceFourColorMode;
    private CheckBoxPreferenceHideAutoCompleteButton preferenceHideAutoCompleteButton;
    private CheckBoxPreferenceHideMenuButton preferenceHideMenuButton;
    private CheckBoxPreferenceHideScore preferenceHideScore;
    private CheckBoxPreferenceHideTime preferenceHideTime;

    private PreferenceCategory categoryOnlyForThisGame;

    CustomizationPreferenceFragment customizationPreferenceFragment;

    private Sounds settingsSounds;

    //make this static so the preference fragments use the same intent
    //don't forget: Android 8 doesn't call onCreate for the fragments, so there only one intent is
    //created. Android 7 calls onCreate for each fragment and would create new intents
    static Intent returnIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        reinitializeData(getApplicationContext());
        super.onCreate(savedInstanceState);

        ((ViewGroup) getListView().getParent()).setPadding(0, 0, 0, 0);     //remove huge padding in landscape

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        prefs.setCriticalSettings();

        settingsSounds = new Sounds(this);

        if (returnIntent == null) {
            returnIntent = new Intent();
        }
    }

    @Override
    public boolean onIsMultiPane() {
        return isLargeTablet(this);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        if (prefs.getShowAdvancedSettings()) {
            loadHeadersFromResource(R.xml.pref_headers_with_advanced_settings, target);
        } else {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }

    }

    /*
     * Update settings when the shared preferences get new values. It uses a lot of if/else instead
     * of switch/case because only this way i can use getString() to get the xml values, otherwise
     * I would need to write the strings manually in the cases.
     */
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PREF_KEY_SETTINGS_ONLY_FOR_THIS_GAME)) {

            if (preferenceFourColorMode != null) {
                preferenceFourColorMode.update();
            }

            if (preferenceHideAutoCompleteButton != null) {
                preferenceHideAutoCompleteButton.update();
            }

            if (preferenceHideMenuButton != null) {
                preferenceHideMenuButton.update();
            }

            if (preferenceHideScore != null) {
                preferenceHideScore.update();
            }

            if (preferenceHideTime != null) {
                preferenceHideTime.update();
            }

            if (preferenceCards != null) {
                preferenceCards.updateSummary();
            }

            if (preferenceCardBackground != null) {
                preferenceCardBackground.updateSummary();
            }

            if (preferenceBackgroundColor != null) {
                preferenceBackgroundColor.updateSummary();
            }

            if (preferenceTextColor != null) {
                preferenceTextColor.updateSummary();
            }

            Card.updateCardDrawableChoice();
            Card.updateCardBackgroundChoice();

            updatePreferenceGameLayoutMarginsSummary();
            updatePreferenceMenuBarPositionSummary();

            returnIntent.putExtra(getString(R.string.intent_update_game_layout), true);
            returnIntent.putExtra(getString(R.string.intent_update_menu_bar), true);
            returnIntent.putExtra(getString(R.string.intent_background_color), true);
            returnIntent.putExtra(getString(R.string.intent_text_color), true);
            returnIntent.putExtra(getString(R.string.intent_update_score_visibility), true);
            returnIntent.putExtra(getString(R.string.intent_update_time_visibility), true);
        }
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
            returnIntent.putExtra(getString(R.string.intent_update_menu_bar), true);
        } else if (key.equals(PREF_KEY_4_COLOR_MODE)) {
            Card.updateCardDrawableChoice();

            if (preferenceCards != null) {
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
        } else if (key.equals(PREF_KEY_BACKGROUND_VOLUME)) {
            updatePreferenceBackgroundVolumeSummary();
            backgroundSound.doInBackground(this);
        } else if (key.equals(PREF_KEY_FORCE_TABLET_LAYOUT)) {
            restartApplication();
        } else if (key.equals(PREF_KEY_SINGLE_TAP_ALL_GAMES)) {
            if (sharedPreferences.getBoolean(key, false) && preferenceTapToSelect != null) {
                preferenceTapToSelect.setChecked(false);
            }
        } else if (key.equals(PREF_KEY_TAP_TO_SELECT_ENABLED)) {
            if (sharedPreferences.getBoolean(key, false) && preferenceSingleTapAllGames != null) {
                preferenceSingleTapAllGames.setChecked(false);
            }
        } else if (key.equals(PREF_KEY_MAX_NUMBER_UNDOS)) {
            if (recordList != null) {
                recordList.setMaxRecords();
            }

            updatePreferenceMaxNumberUndos();
        } else if (key.equals(PREF_KEY_SHOW_ADVANCED_SETTINGS)) {
            final Intent intent = new Intent(getApplicationContext(), Settings.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            startActivity(intent);
        } else if (key.equals(PREF_KEY_GAME_LAYOUT_MARGINS_PORTRAIT) || key.equals(PREF_KEY_GAME_LAYOUT_MARGINS_LANDSCAPE)) {
            updatePreferenceGameLayoutMarginsSummary();
            returnIntent.putExtra(getString(R.string.intent_update_game_layout), true);
        } else if (key.equals(PREF_KEY_HIDE_MENU_BUTTON)) {
            returnIntent.putExtra(getString(R.string.intent_update_menu_bar), true);
        } else if (key.equals(PREF_KEY_IMMERSIVE_MODE)) {
            returnIntent.putExtra(getString(R.string.intent_update_game_layout), true);
        } else if (key.equals(PREF_KEY_BACKGROUND_COLOR) || key.equals(PREF_KEY_BACKGROUND_COLOR_CUSTOM) || key.equals(PREF_KEY_BACKGROUND_COLOR_TYPE)) {
            returnIntent.putExtra(getString(R.string.intent_background_color), true);
        } else if (key.equals(PREF_KEY_TEXT_COLOR)) {
            returnIntent.putExtra(getString(R.string.intent_text_color), true);
        } else if (key.equals(PREF_KEY_HIDE_SCORE)) {
            returnIntent.putExtra(getString(R.string.intent_update_score_visibility), true);
        } else if (key.equals(PREF_KEY_HIDE_TIME)) {
            returnIntent.putExtra(getString(R.string.intent_update_time_visibility), true);
        } else if (key.equals(PREF_KEY_ENSURE_MOVABILITY)) {
            ArrayList<LoadGame.AllGameInformation> gameInfoList = lg.getOrderedGameInfoList();

            for (int i = 0; i < lg.getGameCount(); i++) {
                SharedPreferences sharedPref = getSharedPreferences(gameInfoList.get(i).getSharedPrefName(), MODE_PRIVATE);
                sharedPref.edit().putInt(PREF_KEY_ENSURE_MOVABILITY_MIN_MOVES, sharedPref.getInt(PREF_KEY_ENSURE_MOVABILITY_MIN_MOVES, gameInfoList.get(i).getEnsureMovabilityMoves())).apply();
            }
        }
    }

    @Override
    public void finish() {
        setResult(Activity.RESULT_OK, returnIntent);
        super.finish();
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

    private void updatePreferenceMenuColumnsSummary() {
        int portraitValue = prefs.getSavedMenuColumnsPortrait();
        int landscapeValue = prefs.getSavedMenuColumnsLandscape();

        String text = String.format(Locale.getDefault(), "%s: %d\n%s: %d",
                getString(R.string.settings_portrait), portraitValue, getString(R.string.settings_landscape), landscapeValue);

        preferenceMenuColumns.setSummary(text);
    }

    private void updatePreferenceGameLayoutMarginsSummary() {
        String textPortrait = "", textLandscape = "";

        switch (prefs.getSavedGameLayoutMarginsPortrait()) {
            case 0:
                textPortrait = getString(R.string.settings_game_layout_margins_none);
                break;
            case 1:
                textPortrait = getString(R.string.settings_game_layout_margins_small);
                break;
            case 2:
                textPortrait = getString(R.string.settings_game_layout_margins_medium);
                break;
            case 3:
                textPortrait = getString(R.string.settings_game_layout_margins_large);
                break;
        }

        switch (prefs.getSavedGameLayoutMarginsLandscape()) {
            case 0:
                textLandscape = getString(R.string.settings_game_layout_margins_none);
                break;
            case 1:
                textLandscape = getString(R.string.settings_game_layout_margins_small);
                break;
            case 2:
                textLandscape = getString(R.string.settings_game_layout_margins_medium);
                break;
            case 3:
                textLandscape = getString(R.string.settings_game_layout_margins_large);
                break;
        }

        String text = String.format(Locale.getDefault(), "%s: %s\n%s: %s",
                getString(R.string.settings_portrait), textPortrait, getString(R.string.settings_landscape), textLandscape);

        preferenceGameLayoutMargins.setSummary(text);
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
                getString(R.string.settings_portrait), portrait, getString(R.string.settings_landscape), landscape);

        preferenceMenuBarPosition.setSummary(text);
    }

    private void updatePreferenceBackgroundVolumeSummary() {
        int volume = prefs.getSavedBackgroundVolume();

        preferenceBackgroundVolume.setSummary(String.format(Locale.getDefault(), "%s %%", volume));
    }

    public static class CustomizationPreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_customize);
            setHasOptionsMenu(true);

            Settings settings = (Settings) getActivity();

            settings.customizationPreferenceFragment = this;

            settings.preferenceMenuBarPosition = findPreference(getString(R.string.pref_key_menu_bar_position));
            settings.preferenceCards = (DialogPreferenceCards) findPreference(getString(R.string.pref_key_cards));
            settings.preferenceGameLayoutMargins = findPreference(getString(R.string.pref_key_game_layout_margins));
            settings.preferenceCardBackground = (DialogPreferenceCardBackground) findPreference(getString(R.string.pref_key_cards_background));
            settings.preferenceBackgroundColor = (DialogPreferenceBackgroundColor) findPreference(getString(R.string.pref_key_background_color));
            settings.preferenceTextColor = (DialogPreferenceTextColor) findPreference(getString(R.string.pref_key_text_color));

            settings.preferenceFourColorMode = (CheckBoxPreferenceFourColorMode) findPreference(getString(R.string.dummy_pref_key_4_color_mode));
            settings.preferenceHideAutoCompleteButton = (CheckBoxPreferenceHideAutoCompleteButton) findPreference(getString(R.string.dummy_pref_key_hide_auto_complete_button));
            settings.preferenceHideMenuButton = (CheckBoxPreferenceHideMenuButton) findPreference(getString(R.string.dummy_pref_key_hide_menu_button));
            settings.preferenceHideScore = (CheckBoxPreferenceHideScore) findPreference(getString(R.string.dummy_pref_key_hide_score));
            settings.preferenceHideTime = (CheckBoxPreferenceHideTime) findPreference(getString(R.string.dummy_pref_key_hide_time));
            settings.dialogPreferenceOnlyForThisGame = (DialogPreferenceOnlyForThisGame) findPreference(getString(R.string.pref_key_settings_only_for_this_game));

            //the preferenceCategory for the dialogPreferenceOnlyForThisGame is only used to make
            //the widget update on Android 8+ devices (otherwise it wouldn't due to a bug)
            //So remove the title with an empty layout of the category to make it (nearly) disappear
            settings.categoryOnlyForThisGame = (PreferenceCategory) findPreference(getString(R.string.pref_cat_key_only_for_this_game));
            settings.categoryOnlyForThisGame.setLayoutResource(R.layout.empty);

            settings.preferenceFourColorMode.update();
            settings.preferenceHideAutoCompleteButton.update();
            settings.preferenceHideMenuButton.update();
            settings.preferenceHideScore.update();
            settings.preferenceHideTime.update();

            settings.updatePreferenceGameLayoutMarginsSummary();
            settings.updatePreferenceMenuBarPositionSummary();
            settings.hidePreferenceOnlyForThisGame();
        }
    }

    public void hidePreferenceOnlyForThisGame() {
        if (dialogPreferenceOnlyForThisGame.canBeHidden()) {
            customizationPreferenceFragment.getPreferenceScreen().removePreference(categoryOnlyForThisGame);
        }
    }

    public static class OtherPreferenceFragment extends CustomPreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_other);
            setHasOptionsMenu(true);

            Settings settings = (Settings) getActivity();

            settings.preferenceImmersiveMode = (CheckBoxPreference) findPreference(getString(R.string.pref_key_immersive_mode));

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                settings.preferenceImmersiveMode.setEnabled(false);
            }
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

/* Copyright (C) 2018  Tobias Bielefeld
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

package de.tobiasbielefeld.solitaire.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;


import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomDialogPreference;
import de.tobiasbielefeld.solitaire.ui.settings.Settings;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static de.tobiasbielefeld.solitaire.SharedData.createBulletParagraph;
import static de.tobiasbielefeld.solitaire.SharedData.lg;
import static de.tobiasbielefeld.solitaire.SharedData.prefs;
import static de.tobiasbielefeld.solitaire.SharedData.showToast;
import static de.tobiasbielefeld.solitaire.helper.Preferences.DEFAULT_CURRENT_GAME;
import static de.tobiasbielefeld.solitaire.helper.Preferences.DEFAULT_SETTINGS_ONLY_FOR_THIS_GAME;
import static de.tobiasbielefeld.solitaire.helper.Preferences.PREF_KEY_SETTINGS_ONLY_FOR_THIS_GAME;

/**
 * Dialog to enable game individual settings.
 * The dialog has 4 "states":
 * - If the settings are opened from within a game, which doesn't have this enabled yet, the dialog
 *   shows information about what this does and how to change settings for the other games
 * - Same situation, but the game HAS this enabled, the information will be, that the dialog will
 *   restore the previous settings
 * - Settings are opened from the main menu: No individual settings possible, so the preference will
 *   tell if some games have individual settings enabled, because they won't be affected by changes
 * - Settings are opened from the main menu, but NO game has individual settings enabled. So just
 *   hide the preference somehow
 *
 * The preference is placed in an own PreferenceCategory, otherwise the widget wouldn't update on
 * Android 8+ devices (bug?)
 */

public class DialogPreferenceOnlyForThisGame extends CustomDialogPreference {

    private Context context;
    private CheckBox widget;


    public DialogPreferenceOnlyForThisGame(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_settings_only_for_this_game);
        setDialogIcon(null);
        setDialogTitle(null);
        this.context = context;
    }

    @Override
    protected void onBindDialogView(View view) {
        TextView textView1 = (TextView) view.findViewById(R.id.textViewDialogOnlyForThisGame1);
        TextView textView2 = (TextView) view.findViewById(R.id.textViewDialogOnlyForThisGame2);
        TextView textView3 = (TextView) view.findViewById(R.id.textViewDialogOnlyForThisGame3);

        //settings were opened from the main menu
        if (isNotInGame()) {
            String sharedPrefNames[] = lg.getSharedPrefNameList();
            String gameNames[] = lg.getDefaultGameNameList(context.getResources());

            ArrayList<String> gamesWithIndividualSettings = new ArrayList<>(sharedPrefNames.length);

            for (int i=0; i<sharedPrefNames.length; i++) {
                SharedPreferences savedGameData = context.getSharedPreferences(sharedPrefNames[i], MODE_PRIVATE);

                if (savedGameData.getBoolean(PREF_KEY_SETTINGS_ONLY_FOR_THIS_GAME, DEFAULT_SETTINGS_ONLY_FOR_THIS_GAME)) {
                    gamesWithIndividualSettings.add(gameNames[i]);
                }
            }

            textView1.setText(R.string.settings_dialog_only_for_this_game_information_2);
            textView2.setText(createBulletParagraph(gamesWithIndividualSettings.toArray(new CharSequence[gamesWithIndividualSettings.size()])));
            textView3.setText(R.string.settings_dialog_only_for_this_game_information_3);
        //settings are switching to individual settings
        } else if (!prefs.hasSettingsOnlyForThisGame()) {

            //build the list with bullet characters
            CharSequence strings[] = new CharSequence[]{
                    context.getString(R.string.settings_dialog_only_for_this_game_enable_2),
                    context.getString(R.string.settings_dialog_only_for_this_game_enable_3),
                    context.getString(R.string.settings_dialog_only_for_this_game_enable_4)
            };

            //set up the textView
            textView1.setText(R.string.settings_dialog_only_for_this_game_enable_1);
            textView2.setText(createBulletParagraph(strings));
            textView3.setText(R.string.settings_dialog_only_for_this_game_enable_5);
        //settings are switching back to normal settings
        } else {
            textView1.setText(R.string.settings_dialog_only_for_this_game_disable);
            textView2.setVisibility(GONE);
            textView3.setVisibility(GONE);
        }

        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult){
            if (!isNotInGame()){
                if (!prefs.hasSettingsOnlyForThisGame()) {
                    //copy all relevant settings before switching to game-individual settings
                    prefs.copyToGameIndividualSettings();

                    prefs.setSettingsOnlyForThisGame(true);

                } else {
                    prefs.setSettingsOnlyForThisGame(false);
                }

                if (widget != null) {
                    widget.setChecked(!widget.isChecked());
                }
            } else {
                //reset the setting for individual game settings for all games
                for (String name : lg.getSharedPrefNameList()) {
                    SharedPreferences savedGameData = context.getSharedPreferences(name, MODE_PRIVATE);

                    if (savedGameData.getBoolean(PREF_KEY_SETTINGS_ONLY_FOR_THIS_GAME,DEFAULT_SETTINGS_ONLY_FOR_THIS_GAME)){
                        savedGameData.edit().putBoolean(PREF_KEY_SETTINGS_ONLY_FOR_THIS_GAME,false).apply();
                    }
                }

                ((Settings) getContext()).hidePreferenceOnlyForThisGame();
                showToast(context.getString(R.string.settings_dialog_only_for_this_game_removed_all), context);
            }
        }


        super.onDialogClosed(positiveResult);
    }

    /*
     * Get the layout from the preference, so I can get the imageView from the widgetLayout
     */
    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);
        view.setBackgroundResource(R.color.colorDrawerSelected);

        //get rid of the stupid single line restriction for the title
        TextView textView = (TextView) view.findViewById(android.R.id.title);
        if (textView != null) {
            textView.setSingleLine(false);
        }

        widget = (CheckBox) view.findViewById(R.id.preference_only_for_this_game_switch);

        if (isNotInGame()) {
            if (widget != null) {
                widget.setVisibility(GONE);
            }

            if (getNumberOfGamesWithIndividualSettings() > 0){
                setTitle(context.getString(R.string.settings_dialog_only_for_this_game_information_1));
            }

        } else {
            setTitle(String.format(context.getString(R.string.settings_apply_only_for_this_game),lg.getGameName()));

            if (widget != null) {
                widget.setChecked(prefs.hasSettingsOnlyForThisGame());
            }
        }

        return view;
    }

    private int getNumberOfGamesWithIndividualSettings(){
        int numberOfGamesWithIndividualSettings = 0;

        for (String name : lg.getSharedPrefNameList()) {
            SharedPreferences savedGameData = context.getSharedPreferences(name, MODE_PRIVATE);

            if (savedGameData.getBoolean(PREF_KEY_SETTINGS_ONLY_FOR_THIS_GAME,DEFAULT_SETTINGS_ONLY_FOR_THIS_GAME)){
                numberOfGamesWithIndividualSettings ++;
            }
        }

        return numberOfGamesWithIndividualSettings;
    }

    private boolean isNotInGame(){
        return prefs.getSavedCurrentGame() == DEFAULT_CURRENT_GAME;
    }

    public boolean canBeHidden(){
        return isNotInGame() && getNumberOfGamesWithIndividualSettings() == 0;
    }
}

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

package de.tobiasbielefeld.solitaire.dialogs;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomDialogPreference;

import static de.tobiasbielefeld.solitaire.SharedData.prefs;

/**
 * dialog for changing the rows shown in the menu. It uses different values for portrait and landscape
 */

public class DialogPreferenceGameLayoutMargins extends CustomDialogPreference {

    RadioButton[] portrait = new RadioButton[4];
    RadioButton[] landscape = new RadioButton[4];



    public DialogPreferenceGameLayoutMargins(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_settings_game_layout_margins);
        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {

        portrait[0] = (RadioButton) view.findViewById(R.id.dialog_button_portrait_none);
        portrait[1] = (RadioButton) view.findViewById(R.id.dialog_button_portrait_small);
        portrait[2] = (RadioButton) view.findViewById(R.id.dialog_button_portrait_medium);
        portrait[3] = (RadioButton) view.findViewById(R.id.dialog_button_portrait_large);

        landscape[0] = (RadioButton) view.findViewById(R.id.dialog_button_landscape_none);
        landscape[1] = (RadioButton) view.findViewById(R.id.dialog_button_landscape_small);
        landscape[2] = (RadioButton) view.findViewById(R.id.dialog_button_landscape_medium);
        landscape[3] = (RadioButton) view.findViewById(R.id.dialog_button_landscape_large);

        portrait[prefs.getSavedGameLayoutMarginsPortrait()].setChecked(true);
        landscape[prefs.getSavedGameLayoutMarginsLandscape()].setChecked(true);

        super.onBindDialogView(view);
    }


    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            int portraitValue = 0, landscapeValue = 0;

            for (int i=0;i<4;i++){
                if (portrait[i].isChecked()) {
                    portraitValue = i;
                }

                if (landscape[i].isChecked()){
                    landscapeValue = i;
                }
            }

            prefs.saveGameLayoutMarginsPortrait(portraitValue);
            prefs.saveGameLayoutMarginsLandscape(landscapeValue);
        }
    }
}

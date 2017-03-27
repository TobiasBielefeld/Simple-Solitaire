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

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * dialog for changing the rows shown in the menu. It uses different values for portrait and landscape
 */

public class MenuBarPositionDialogPreference extends DialogPreference {

    RadioButton top, bottom, left, right;

    public MenuBarPositionDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_settings_menu_bar_position);
        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        top = (RadioButton) view.findViewById(R.id.dialog_button_portrait_top);
        bottom = (RadioButton) view.findViewById(R.id.dialog_button_portrait_bottom);
        left = (RadioButton) view.findViewById(R.id.dialog_button_landscape_left);
        right = (RadioButton) view.findViewById(R.id.dialog_button_landscape_right);


        //minus 1 because the values are 1 to 10, indexes are from 0 to 9
        if (sharedStringEquals(PREF_KEY_MENU_BAR_POS_PORTRAIT,DEFAULT_MENU_BAR_POSITION_PORTRAIT)){
            bottom.setChecked(true);
        } else {
            top.setChecked(true);
        }

        if (sharedStringEquals(PREF_KEY_MENU_BAR_POS_LANDSCAPE,DEFAULT_MENU_BAR_POSITION_LANDSCAPE)) {
            right.setChecked(true);
        } else {
            left.setChecked(true);
        }

        super.onBindDialogView(view);
    }


    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            putSharedString(PREF_KEY_MENU_BAR_POS_PORTRAIT, bottom.isChecked() ? "bottom" : "top");
            putSharedString(PREF_KEY_MENU_BAR_POS_LANDSCAPE, right.isChecked() ? "right" : "left");
        }
    }
}

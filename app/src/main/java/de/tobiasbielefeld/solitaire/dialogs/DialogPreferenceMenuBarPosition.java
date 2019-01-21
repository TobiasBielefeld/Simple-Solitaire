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

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * dialog for changing the rows shown in the menu. It uses different values for portrait and landscape
 */

public class DialogPreferenceMenuBarPosition extends CustomDialogPreference {

    RadioButton top, bottom, left, right;

    private static String BOTTOM = "bottom";
    private static String TOP = "top";
    private static String LEFT = "left";
    private static String RIGHT = "right";

    public DialogPreferenceMenuBarPosition(Context context, AttributeSet attrs) {
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

        if (prefs.getSavedMenuBarPosPortrait().equals(BOTTOM)) {
            bottom.setChecked(true);
        } else {
            top.setChecked(true);
        }

        if (prefs.getSavedMenuBarPosLandscape().equals(RIGHT)) {
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
            prefs.saveMenuBarPosPortrait(bottom.isChecked() ? BOTTOM : TOP);
            prefs.saveMenuBarPosLandscape(right.isChecked() ? RIGHT : LEFT);
        }
    }
}

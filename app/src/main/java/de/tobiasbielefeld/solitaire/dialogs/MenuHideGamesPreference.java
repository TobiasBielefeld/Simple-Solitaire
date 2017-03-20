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
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/*
 *  Dialog for hiding games in the main menu.
 *  It is NOT a multiSelection list, because it was buggy on tested Android 6 phones. So I
 *  just use a linearLayout with a button and a textView for each game
 */

public class MenuHideGamesPreference extends DialogPreference implements View.OnClickListener {

    ArrayList<LinearLayout> linearLayouts;
    ArrayList<CheckBox> checkBoxes;

    public MenuHideGamesPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_menu_show_games);
        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        linearLayouts = lg.loadMenuPreferenceViews(view);
        checkBoxes = lg.loadMenuPreferenceCheckBoxes(view);

        for (LinearLayout linearLayout : linearLayouts) {
            linearLayout.setOnClickListener(this);
        }

        ArrayList<Integer> result = getSharedIntList(PREF_KEY_MENU_GAMES);

        for (int i = 0; i < checkBoxes.size(); i++) {
            if (result.size() - 1 < i) {
                checkBoxes.get(i).setChecked(true);
            } else {
                checkBoxes.get(i).setChecked(result.get(i) == 1);
            }
        }

        super.onBindDialogView(view);
    }

    public void onClick(View v) {
        int index = linearLayouts.indexOf(v);
        boolean checked = checkBoxes.get(index).isChecked();
        checkBoxes.get(index).setChecked(!checked);

    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            ArrayList<Integer> list = new ArrayList<>();

            for (CheckBox checkBox : checkBoxes) {
                list.add(checkBox.isChecked() ? 1 : 0);
            }

            putSharedIntList("pref_key_menu_games", list);
        }
    }
}

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
import android.content.res.Resources;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_MENU_GAMES;
import static de.tobiasbielefeld.solitaire.SharedData.getSharedIntList;
import static de.tobiasbielefeld.solitaire.SharedData.lg;
import static de.tobiasbielefeld.solitaire.SharedData.putSharedIntList;

/**
 *  Dialog for hiding games in the main menu.
 *  It is NOT a multiSelection list, because it was buggy on tested Android 6 phones. So I
 *  just use a linearLayout with a button and a textView for each game
 */

public class BackgroundColorPreference extends DialogPreference implements View.OnClickListener {

    ArrayList<RadioButton> radioButtons;
    Button buttonCustom;
    Context context;
    Resources res;

    public BackgroundColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_background_color);
        setDialogIcon(null);
        this.context = context;
        res = context.getResources();
    }

    @Override
    protected void onBindDialogView(View view) {
        radioButtons = new ArrayList<>();

        radioButtons.add((RadioButton) view.findViewById(R.id.dialogBackgroundColorButton1));
        radioButtons.add((RadioButton) view.findViewById(R.id.dialogBackgroundColorButton2));
        radioButtons.add((RadioButton) view.findViewById(R.id.dialogBackgroundColorButton3));
        radioButtons.add((RadioButton) view.findViewById(R.id.dialogBackgroundColorButton4));
        radioButtons.add((RadioButton) view.findViewById(R.id.dialogBackgroundColorButton5));
        radioButtons.add((RadioButton) view.findViewById(R.id.dialogBackgroundColorButton6));

        buttonCustom = (Button) view.findViewById(R.id.dialogBackgroundColorButton7);
        buttonCustom.setOnClickListener(this);

        for (int i=0; i< radioButtons.size();i++){
            radioButtons.get(i).setText(res.getStringArray(R.array.pref_background_colors_titles)[i]);
        }

        /*linearLayouts = lg.loadMenuPreferenceViews(view);
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
        }*/

        super.onBindDialogView(view);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public void onClick(View view) {
        if (view.getId() == R.id.dialogBackgroundColorButton7){
            ColorPickerDialog colorPickerDialog= ColorPickerDialog.createColorPickerDialog(context);
            colorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
                @Override
                public void onColorPicked(int color, String hexVal) {
                    //Your code here
                }
            });
            colorPickerDialog.show();
        }

    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
           /* ArrayList<Integer> list = new ArrayList<>();

            for (CheckBox checkBox : checkBoxes) {
                list.add(checkBox.isChecked() ? 1 : 0);
            }

            putSharedIntList(PREF_KEY_MENU_GAMES, list);*/
        }
    }
}

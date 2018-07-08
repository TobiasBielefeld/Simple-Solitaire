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
import android.graphics.Typeface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomDialogPreference;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Dialog for hiding games in the main menu.
 * It is NOT a multiSelection list, because it was buggy on tested Android 6 phones. So I
 * just use a linearLayout with a button and a textView for each game
 */

public class DialogPreferenceMenuHideGames extends CustomDialogPreference implements View.OnClickListener {

    private ArrayList<LinearLayout> linearLayouts = new ArrayList<>();
    private ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    private ArrayList<Integer> gameOrder;

    public DialogPreferenceMenuHideGames(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_menu_show_games);
        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        LinearLayout container = (LinearLayout) view.findViewById(R.id.layoutContainer);

        linearLayouts.clear();
        checkBoxes.clear();

        ArrayList<Integer> results = lg.getMenuShownList();
        gameOrder = lg.getOrderedGameList();

        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
        int padding = (int) (getContext().getResources().getDimension(R.dimen.dialog_menu_layout_padding));
        int margin = (int) (getContext().getResources().getDimension(R.dimen.dialog_menu_button_margin));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(margin, 0, margin, 0);

        ArrayList<String> sortedGameList = lg.getOrderedGameNameList(getContext().getResources());

        for (int i=0;i<lg.getGameCount();i++){
            LinearLayout entry = new LinearLayout(getContext());
            entry.setBackgroundResource(typedValue.resourceId);
            entry.setPadding(padding,padding,padding,padding);
            entry.setOnClickListener(this);

            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setLayoutParams(layoutParams);
            int index = gameOrder.indexOf(i);
            checkBox.setChecked(results.get(index) == 1);

            TextView textView = new TextView(getContext());
            textView.setTypeface(null, Typeface.BOLD);
            textView.setText(sortedGameList.get(i));

            entry.addView(checkBox);
            entry.addView(textView);

            checkBoxes.add(checkBox);
            linearLayouts.add(entry);

            container.addView(entry);
        }


        super.onBindDialogView(view);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public void onClick(View view) {
        int index = linearLayouts.indexOf(view);
        boolean checked = checkBoxes.get(index).isChecked();
        checkBoxes.get(index).setChecked(!checked);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            ArrayList<Integer> list = new ArrayList<>();

            for (int i=0;i<lg.getGameCount();i++){
                int index = gameOrder.get(i);
                list.add(checkBoxes.get(index).isChecked() ? 1 : 0);
            }

            prefs.saveMenuGamesList(list);
        }
    }
}

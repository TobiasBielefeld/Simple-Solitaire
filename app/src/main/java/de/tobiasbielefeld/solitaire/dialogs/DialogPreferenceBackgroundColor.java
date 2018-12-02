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
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomDialogPreference;
import yuku.ambilwarna.AmbilWarnaDialog;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Dialog for changing the background color. It uses a custom layout, so I can dynamically update
 * the widget icon of the preference. The user can choose between 6 pre defined colors or set a custom
 * color. The custom color chooser uses this library: https://github.com/yukuku/ambilwarna
 * <p>
 * To distinguish between the pre defined and custom colors, I use another entry in the sharedPref.
 * I also planned to add a "Add background from gallery" option, but it would require the
 * permission to the external storage, and i wanted my app to use no permissions.
 */

public class DialogPreferenceBackgroundColor extends CustomDialogPreference implements View.OnClickListener {

    int backgroundType;
    int backgroundValue;
    int savedCustomColor;
    private ArrayList<LinearLayout> linearLayouts;
    private Context context;
    private ImageView image;

    public DialogPreferenceBackgroundColor(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_background_color);
        setDialogIcon(null);
        this.context = context;
    }

    @Override
    protected void onBindDialogView(View view) {

        backgroundType = prefs.getSavedBackgroundColorType();
        backgroundValue = prefs.getSavedBackgroundColor();
        savedCustomColor = prefs.getSavedBackgroundCustomColor();

        linearLayouts = new ArrayList<>();
        linearLayouts.add(view.findViewById(R.id.dialogBackgroundColorBlue));
        linearLayouts.add(view.findViewById(R.id.dialogBackgroundColorGreen));
        linearLayouts.add(view.findViewById(R.id.dialogBackgroundColorRed));
        linearLayouts.add(view.findViewById(R.id.dialogBackgroundColorYellow));
        linearLayouts.add(view.findViewById(R.id.dialogBackgroundColorOrange));
        linearLayouts.add(view.findViewById(R.id.dialogBackgroundColorPurple));

        for (LinearLayout linearLayout : linearLayouts) {
            linearLayout.setOnClickListener(this);
        }

        super.onBindDialogView(view);
    }


    @SuppressWarnings("SuspiciousMethodCalls")
    public void onClick(View view) {
        if (view == ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE)) {
            AmbilWarnaDialog dialog = new AmbilWarnaDialog(context, savedCustomColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    backgroundType = 2;
                    backgroundValue = savedCustomColor = color;

                    prefs.saveBackgroundColorType(backgroundType);
                    prefs.saveBackgroundCustomColor(backgroundValue);
                    updateSummary();
                    getDialog().dismiss();
                }

                @Override
                public void onCancel(AmbilWarnaDialog dialog) {
                    // cancel was selected by the user
                }
            });
            dialog.show();
        } else if (view == ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE)) {
            getDialog().dismiss();
        } else {
            backgroundValue = linearLayouts.indexOf(view) + 1;
            backgroundType = 1;

            prefs.saveBackgroundColorType(backgroundType);
            prefs.saveBackgroundColor(backgroundValue);

            updateSummary();
            getDialog().dismiss();
        }
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(this);
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);
    }

    /*
     * Get the layout from the preference, so I can get the imageView from the widgetLayout
     */
    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);

        image = view.findViewById(R.id.widget_layout_color_imageView);
        updateSummary();

        return view;
    }

    /**
     * Gets the saved data and updates the summary according to it
     */
    public void updateSummary() {

        if (prefs.getSavedBackgroundColorType() == 1) {
            int drawableID;
            int stringID;
            switch (prefs.getSavedBackgroundColor()) {
                case 1:
                default:
                    stringID = R.string.blue;
                    drawableID = R.drawable.background_color_blue;
                    break;
                case 2:
                    stringID = R.string.green;
                    drawableID = R.drawable.background_color_green;
                    break;
                case 3:
                    stringID = R.string.red;
                    drawableID = R.drawable.background_color_red;
                    break;
                case 4:
                    stringID = R.string.yellow;
                    drawableID = R.drawable.background_color_yellow;
                    break;
                case 5:
                    stringID = R.string.orange;
                    drawableID = R.drawable.background_color_orange;
                    break;
                case 6:
                    stringID = R.string.purple;
                    drawableID = R.drawable.background_color_purple;
                    break;
            }

            if (image != null) {
                image.setImageResource(drawableID);
            }

            setSummary(context.getString(stringID));
        } else {
            int customColor = prefs.getSavedBackgroundCustomColor();

            //this forces redrawing of the color preview
            setSummary("");

            //show as hex string, but without the opacity part at the beginning
            setSummary(String.format("#%06X", (0xFFFFFF & customColor)));

            if (image != null) {
                image.setImageResource(0);
                image.setBackgroundColor(customColor);
            }
        }
    }
}

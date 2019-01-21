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
import android.graphics.Bitmap;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Locale;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomDialogPreference;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * dialog for picking the card background drawable. It uses a custom layout, so I can dynamically update
 * the widget icon of the preference.
 */

public class DialogPreferenceCardBackground extends CustomDialogPreference implements View.OnClickListener {

    private static int NUMBER_OF_CARD_BACKGROUNDS = 10;

    private LinearLayout[] linearLayoutsBackgrounds = new LinearLayout[NUMBER_OF_CARD_BACKGROUNDS];
    private ImageView[] imageViews = new ImageView[NUMBER_OF_CARD_BACKGROUNDS];

    private LinearLayout[] linearLayoutsColors = new LinearLayout[4];
    private Context context;
    private ImageView image;
    private TypedValue typedValue = new TypedValue();

    private int selectedBackground;
    private int selectedBackgroundColor;


    public DialogPreferenceCardBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_settings_cards_background);
        setDialogIcon(null);
        setDialogTitle("");

        this.context = context;

        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
    }

    @Override
    protected void onBindDialogView(View view) {

        linearLayoutsBackgrounds[0] = (LinearLayout) view.findViewById(R.id.settingsCardBackground0);
        linearLayoutsBackgrounds[1] = (LinearLayout) view.findViewById(R.id.settingsCardBackground1);
        linearLayoutsBackgrounds[2] = (LinearLayout) view.findViewById(R.id.settingsCardBackground2);
        linearLayoutsBackgrounds[3] = (LinearLayout) view.findViewById(R.id.settingsCardBackground3);
        linearLayoutsBackgrounds[4] = (LinearLayout) view.findViewById(R.id.settingsCardBackground4);
        linearLayoutsBackgrounds[5] = (LinearLayout) view.findViewById(R.id.settingsCardBackground5);
        linearLayoutsBackgrounds[6] = (LinearLayout) view.findViewById(R.id.settingsCardBackground6);
        linearLayoutsBackgrounds[7] = (LinearLayout) view.findViewById(R.id.settingsCardBackground7);
        linearLayoutsBackgrounds[8] = (LinearLayout) view.findViewById(R.id.settingsCardBackground8);
        linearLayoutsBackgrounds[9] = (LinearLayout) view.findViewById(R.id.settingsCardBackground9);

        linearLayoutsColors[0] = (LinearLayout) view.findViewById(R.id.dialogBackgroundsCardsBlue);
        linearLayoutsColors[1] = (LinearLayout) view.findViewById(R.id.dialogBackgroundsCardsRed);
        linearLayoutsColors[2] = (LinearLayout) view.findViewById(R.id.dialogBackgroundsCardsGreen);
        linearLayoutsColors[3] = (LinearLayout) view.findViewById(R.id.dialogBackgroundsCardsYellow);

        for (int i = 0; i < NUMBER_OF_CARD_BACKGROUNDS; i++) {
            linearLayoutsBackgrounds[i].setOnClickListener(this);
            imageViews[i] = (ImageView) linearLayoutsBackgrounds[i].getChildAt(0);
        }

        for (int i = 0; i < 4; i++) {
            linearLayoutsColors[i].setOnClickListener(this);
        }

        selectedBackground = prefs.getSavedCardBackground();
        selectedBackgroundColor = prefs.getSavedCardBackgroundColor();
        updateDialog();

        super.onBindDialogView(view);
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.settingsCardBackground0:
                selectedBackground = 0;
                break;
            case R.id.settingsCardBackground1:
                selectedBackground = 1;
                break;
            case R.id.settingsCardBackground2:
                selectedBackground = 2;
                break;
            case R.id.settingsCardBackground3:
                selectedBackground = 3;
                break;
            case R.id.settingsCardBackground4:
                selectedBackground = 4;
                break;
            case R.id.settingsCardBackground5:
                selectedBackground = 5;
                break;
            case R.id.settingsCardBackground6:
                selectedBackground = 6;
                break;
            case R.id.settingsCardBackground7:
                selectedBackground = 7;
                break;
            case R.id.settingsCardBackground8:
                selectedBackground = 8;
                break;
            case R.id.settingsCardBackground9:
                selectedBackground = 9;
                break;
            case R.id.dialogBackgroundsCardsBlue:
                selectedBackgroundColor = 0;
                break;
            case R.id.dialogBackgroundsCardsRed:
                selectedBackgroundColor = 1;
                break;
            case R.id.dialogBackgroundsCardsGreen:
                selectedBackgroundColor = 2;
                break;
            case R.id.dialogBackgroundsCardsYellow:
                selectedBackgroundColor = 3;
                break;
        }

        updateDialog();
    }

    /*
     * Get the layout from the preference, so I can get the imageView from the widgetLayout
     */
    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);

        image = (ImageView) view.findViewById(R.id.preference_cards_background_imageView);
        updateSummary();

        return view;
    }

    /**
     * Update the "selection shadow" and the pictures of the dialog
     */
    private void updateDialog() {

        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        //params.
        for (int i = 0; i < NUMBER_OF_CARD_BACKGROUNDS; i++) {
            linearLayoutsBackgrounds[i].setBackgroundResource(i == selectedBackground ? R.drawable.settings_highlight : typedValue.resourceId);
        }

        for (int i = 0; i < NUMBER_OF_CARD_BACKGROUNDS; i++) {
            imageViews[i].setImageBitmap(bitmaps.getCardBack(i, selectedBackgroundColor));
        }

        for (int i = 0; i < 4; i++) {
            linearLayoutsColors[i].setBackgroundResource(i == selectedBackgroundColor ? R.drawable.settings_highlight : typedValue.resourceId);
        }
    }

    /**
     * save the selected background and update the summary
     */
    private void save() {
        prefs.saveCardBackground(selectedBackground);
        prefs.saveCardBackgroundColor(selectedBackgroundColor);

        updateSummary();
    }

    /**
     * Gets the bitmap for the card background preference icon and also set its summary
     */
    public void updateSummary() {
        Bitmap cardBack;

        int selectedBackground = prefs.getSavedCardBackground();
        int selectedBackgroundColor = prefs.getSavedCardBackgroundColor();

        if (image!=null) {
            cardBack = bitmaps.getCardBack(selectedBackground, selectedBackgroundColor);
            image.setImageBitmap(cardBack);
        }

        setSummary(String.format(Locale.getDefault(), "%s %s",
                context.getString(R.string.settings_background), selectedBackground + 1));
    }

    /*
     * only save when result is positive
     */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            save();
        }
    }
}

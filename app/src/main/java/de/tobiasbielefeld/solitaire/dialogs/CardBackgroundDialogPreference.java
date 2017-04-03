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
import android.graphics.drawable.BitmapDrawable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Locale;

import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * dialog for picking the card background drawable. It uses a custom layout, so I can dynamically update
 * the widget icon of the preference.
 */

public class CardBackgroundDialogPreference extends DialogPreference implements View.OnClickListener {

    private LinearLayout[] linearLayouts = new LinearLayout[NUMBER_OF_CARD_BACKGROUNDS];
    private Context context;
    private ImageView image;

    public CardBackgroundDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_settings_cards_background);
        setDialogIcon(null);
        this.context = context;
    }

    @Override
    protected void onBindDialogView(View view) {

        linearLayouts[0] = (LinearLayout) view.findViewById(R.id.settingsCardBackground1);
        linearLayouts[1] = (LinearLayout) view.findViewById(R.id.settingsCardBackground2);
        linearLayouts[2] = (LinearLayout) view.findViewById(R.id.settingsCardBackground3);
        linearLayouts[3] = (LinearLayout) view.findViewById(R.id.settingsCardBackground4);
        linearLayouts[4] = (LinearLayout) view.findViewById(R.id.settingsCardBackground5);
        linearLayouts[5] = (LinearLayout) view.findViewById(R.id.settingsCardBackground6);
        linearLayouts[6] = (LinearLayout) view.findViewById(R.id.settingsCardBackground7);
        linearLayouts[7] = (LinearLayout) view.findViewById(R.id.settingsCardBackground8);
        linearLayouts[8] = (LinearLayout) view.findViewById(R.id.settingsCardBackground9);
        linearLayouts[9] = (LinearLayout) view.findViewById(R.id.settingsCardBackground10);
        linearLayouts[10] = (LinearLayout) view.findViewById(R.id.settingsCardBackground11);
        linearLayouts[11] = (LinearLayout) view.findViewById(R.id.settingsCardBackground12);
        linearLayouts[12] = (LinearLayout) view.findViewById(R.id.settingsCardBackground13);
        linearLayouts[13] = (LinearLayout) view.findViewById(R.id.settingsCardBackground14);
        linearLayouts[14] = (LinearLayout) view.findViewById(R.id.settingsCardBackground15);
        linearLayouts[15] = (LinearLayout) view.findViewById(R.id.settingsCardBackground16);
        linearLayouts[16] = (LinearLayout) view.findViewById(R.id.settingsCardBackground17);
        linearLayouts[17] = (LinearLayout) view.findViewById(R.id.settingsCardBackground18);

        for (int i = 0; i < NUMBER_OF_CARD_BACKGROUNDS; i++) {
            linearLayouts[i].setOnClickListener(this);
            ImageView imageView = (ImageView) linearLayouts[i].getChildAt(0);
            imageView.setImageBitmap(bitmaps.getCardBack(i % 8, i / 8));
        }

        super.onBindDialogView(view);
    }

    public void onClick(View v) {
        int choice;

        switch (v.getId()) {
            case R.id.settingsCardBackground1:
            default:
                choice = 1;
                break;
            case R.id.settingsCardBackground2:
                choice = 2;
                break;
            case R.id.settingsCardBackground3:
                choice = 3;
                break;
            case R.id.settingsCardBackground4:
                choice = 4;
                break;
            case R.id.settingsCardBackground5:
                choice = 5;
                break;
            case R.id.settingsCardBackground6:
                choice = 6;
                break;
            case R.id.settingsCardBackground7:
                choice = 7;
                break;
            case R.id.settingsCardBackground8:
                choice = 8;
                break;
            case R.id.settingsCardBackground9:
                choice = 9;
                break;
            case R.id.settingsCardBackground10:
                choice = 10;
                break;
            case R.id.settingsCardBackground11:
                choice = 11;
                break;
            case R.id.settingsCardBackground12:
                choice = 12;
                break;
            case R.id.settingsCardBackground13:
                choice = 13;
                break;
            case R.id.settingsCardBackground14:
                choice = 14;
                break;
            case R.id.settingsCardBackground15:
                choice = 15;
                break;
            case R.id.settingsCardBackground16:
                choice = 16;
                break;
            case R.id.settingsCardBackground17:
                choice = 17;
                break;
            case R.id.settingsCardBackground18:
                choice = 18;
                break;
        }

        putSharedInt(CARD_BACKGROUND, choice);
        updateSummary();
        getDialog().dismiss();
    }

    /*
     * Applies a custom layout, so I can get the widget image from it, to update the background choice
     */
    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.preference_cards_background, parent, false);
        image = (ImageView) view.findViewById(R.id.preference_cards_background_imageView);
        updateSummary();

        return view;
    }

    /**
     * Gets the bitmap for the card background preference icon and also set its summary
     */
    private void updateSummary() {
        Bitmap cardBack;

        int selectedBackground = getSharedInt(CARD_BACKGROUND, DEFAULT_CARD_BACKGROUND);

        cardBack = bitmaps.getCardBack((selectedBackground-1)%8,(selectedBackground-1)/8);

        image.setImageBitmap(cardBack);
        setSummary(String.format(Locale.getDefault(), "%s %s",
                context.getString(R.string.settings_background), selectedBackground));
    }


}

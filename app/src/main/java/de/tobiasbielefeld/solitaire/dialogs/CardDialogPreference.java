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
import android.widget.ImageView;
import android.widget.LinearLayout;

import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.CARD_DRAWABLES;
import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_4_COLOR_MODE;
import static de.tobiasbielefeld.solitaire.SharedData.NUMBER_OF_CARD_THEMES;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_4_COLOR_MODE;
import static de.tobiasbielefeld.solitaire.SharedData.bitmaps;
import static de.tobiasbielefeld.solitaire.SharedData.getSharedBoolean;
import static de.tobiasbielefeld.solitaire.SharedData.putSharedInt;

/*
 * dialog for picking the card front drawable. Clicks on it are handled here and the
 * sharedPrefChanged listener in Settings will update the cards.
 */

public class CardDialogPreference extends DialogPreference implements View.OnClickListener {

    private LinearLayout[] linearLayouts = new LinearLayout[NUMBER_OF_CARD_THEMES];

    public CardDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_settings_cards);
        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        int row = getSharedBoolean(PREF_KEY_4_COLOR_MODE, DEFAULT_4_COLOR_MODE) ? 1 : 0;

        linearLayouts[0] = (LinearLayout) view.findViewById(R.id.settingsLinearLayoutCardsBasic);
        linearLayouts[1] = (LinearLayout) view.findViewById(R.id.settingsLinearLayoutCardsClassic);
        linearLayouts[2] = (LinearLayout) view.findViewById(R.id.settingsLinearLayoutCardsAbstract);
        linearLayouts[3] = (LinearLayout) view.findViewById(R.id.settingsLinearLayoutCardsSimple);
        linearLayouts[4] = (LinearLayout) view.findViewById(R.id.settingsLinearLayoutCardsModern);
        linearLayouts[5] = (LinearLayout) view.findViewById(R.id.settingsLinearLayoutCardsOxygenDark);
        linearLayouts[6] = (LinearLayout) view.findViewById(R.id.settingsLinearLayoutCardsOxygenLight);
        linearLayouts[7] = (LinearLayout) view.findViewById(R.id.settingsLinearLayoutCardsPoker);

        for (int i = 0; i < NUMBER_OF_CARD_THEMES; i++) {
            linearLayouts[i].setOnClickListener(this);
            ImageView imageView = (ImageView) linearLayouts[i].getChildAt(0);
            imageView.setImageBitmap(bitmaps.getCardPreview(i, row));
        }

        super.onBindDialogView(view);
    }

    public void onClick(View v) {
        int choice = 1;

        switch (v.getId()) {
            case R.id.settingsLinearLayoutCardsBasic:
                choice = 1;
                break;
            case R.id.settingsLinearLayoutCardsClassic:
                choice = 2;
                break;
            case R.id.settingsLinearLayoutCardsAbstract:
                choice = 3;
                break;
            case R.id.settingsLinearLayoutCardsSimple:
                choice = 4;
                break;
            case R.id.settingsLinearLayoutCardsModern:
                choice = 5;
                break;
            case R.id.settingsLinearLayoutCardsOxygenDark:
                choice = 6;
                break;
            case R.id.settingsLinearLayoutCardsOxygenLight:
                choice = 7;
                break;
            case R.id.settingsLinearLayoutCardsPoker:
                choice = 8;
                break;

        }

        putSharedInt(CARD_DRAWABLES, choice);
        getDialog().dismiss();
    }
}

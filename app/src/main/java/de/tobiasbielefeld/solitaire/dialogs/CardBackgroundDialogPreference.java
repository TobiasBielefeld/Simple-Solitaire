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

import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/*
 * dialog for picking the card background drawable. Clicks on it are handled here and the
 * sharedPrefChanged listener in Settings will update the cards.
 */

public class CardBackgroundDialogPreference extends DialogPreference implements View.OnClickListener{

    public CardBackgroundDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_settings_cards_background);
        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        view.findViewById(R.id.settingsCardBackground1).setOnClickListener(this);
        view.findViewById(R.id.settingsCardBackground2).setOnClickListener(this);
        view.findViewById(R.id.settingsCardBackground3).setOnClickListener(this);
        view.findViewById(R.id.settingsCardBackground4).setOnClickListener(this);
        view.findViewById(R.id.settingsCardBackground5).setOnClickListener(this);
        view.findViewById(R.id.settingsCardBackground6).setOnClickListener(this);
        view.findViewById(R.id.settingsCardBackground7).setOnClickListener(this);
        view.findViewById(R.id.settingsCardBackground8).setOnClickListener(this);
        view.findViewById(R.id.settingsCardBackground9).setOnClickListener(this);
        view.findViewById(R.id.settingsCardBackground10).setOnClickListener(this);
        view.findViewById(R.id.settingsCardBackground11).setOnClickListener(this);
        view.findViewById(R.id.settingsCardBackground12).setOnClickListener(this);

        super.onBindDialogView(view);
    }

    public void onClick(View v) {
        int choice;

        switch (v.getId()) {
            case R.id.settingsCardBackground1:default:
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
        }

        putSharedInt(CARD_BACKGROUND, choice);
        getDialog().dismiss();
    }
}

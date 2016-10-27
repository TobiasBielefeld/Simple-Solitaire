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
import de.tobiasbielefeld.solitaire.classes.Card;

import static de.tobiasbielefeld.solitaire.SharedData.editor;

/*
 * dialog for picking the card front drawable. Clicks on it are handled here and the
 * sharedPrefChanged listener in Settings will update the cards.
 */

public class CardPicker extends DialogPreference implements View.OnClickListener{

    public CardPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_settings_cards);
        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        view.findViewById(R.id.settingsLinearLayoutCardsClassic).setOnClickListener(this);
        view.findViewById(R.id.settingsLinearLayoutCardsAbstract).setOnClickListener(this);
        view.findViewById(R.id.settingsLinearLayoutCardsSimple).setOnClickListener(this);
        view.findViewById(R.id.settingsLinearLayoutCardsModern).setOnClickListener(this);
        view.findViewById(R.id.settingsLinearLayoutCardsDark).setOnClickListener(this);

        super.onBindDialogView(view);
    }

    public void onClick(View v) {
        int choice = 1;

        switch (v.getId()) {
            case R.id.settingsLinearLayoutCardsClassic:
                choice = 1;
                break;
            case R.id.settingsLinearLayoutCardsAbstract:
                choice = 2;
                break;
            case R.id.settingsLinearLayoutCardsSimple:
                choice = 3;
                break;
            case R.id.settingsLinearLayoutCardsModern:
                choice = 4;
                break;
            case R.id.settingsLinearLayoutCardsDark:
                choice = 5;
                break;
        }

        editor.putInt(Card.CARD_DRAWABLES, choice).apply();                                         //save it
        getDialog().dismiss();
    }
}

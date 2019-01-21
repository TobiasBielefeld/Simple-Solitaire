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
import android.widget.EditText;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomDialogPreference;

import static de.tobiasbielefeld.solitaire.SharedData.prefs;
import static de.tobiasbielefeld.solitaire.SharedData.showToast;
import static de.tobiasbielefeld.solitaire.SharedData.stringFormat;

/*
 * custom dialog to set the maximum amount of undos
 */

public class DialogPreferenceMaxNumberUndos extends CustomDialogPreference {

    private EditText input;

    public DialogPreferenceMaxNumberUndos(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_max_number_undos);
        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        input = (EditText) view.findViewById(R.id.settings_max_number_undos_input);

        input.setText(stringFormat(Integer.toString(prefs.getSavedMaxNumberUndos())));


        super.onBindDialogView(view);
    }



    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // When the user selects "OK", persist the new value
        if (positiveResult) {
            try {
                //Saving zero would cause force closes, so just catch it here
                if (Integer.parseInt(input.getText().toString()) < 1) {
                    showToast(getContext().getString(R.string.settings_number_input_error),getContext());
                    return;
                }

                prefs.saveMaxNumberUndos(Integer.parseInt(input.getText().toString()));
            } catch (Exception e){
                showToast(getContext().getString(R.string.settings_number_input_error),getContext());
            }
        }
    }
}

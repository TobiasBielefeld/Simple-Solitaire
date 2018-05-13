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
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.R;

import static android.content.Context.MODE_PRIVATE;
import static de.tobiasbielefeld.solitaire.SharedData.lg;
import static de.tobiasbielefeld.solitaire.SharedData.logText;
import static de.tobiasbielefeld.solitaire.SharedData.showToast;
import static de.tobiasbielefeld.solitaire.helper.Preferences.DEFAULT_ENSURE_MOVABILITY_MIN_MOVES;
import static de.tobiasbielefeld.solitaire.helper.Preferences.DEFAULT_SETTINGS_ONLY_FOR_THIS_GAME;
import static de.tobiasbielefeld.solitaire.helper.Preferences.PREF_KEY_ENSURE_MOVABILITY_MIN_MOVES;
import static de.tobiasbielefeld.solitaire.helper.Preferences.PREF_KEY_SETTINGS_ONLY_FOR_THIS_GAME;

/*
 * custom dialog to set the minimum moves for ensuring movability. It can be adjusted per game
 */

public class DialogPreferenceEnsureMovabilityMinMoves extends DialogPreference{

    ArrayList<EditText> inputs;
    ArrayList<String> sortedSharedPrefList;

    public DialogPreferenceEnsureMovabilityMinMoves(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_ensure_movability_min_moves);
        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        LinearLayout container = (LinearLayout) view.findViewById(R.id.settings_ensure_movability_container);

        inputs = new ArrayList<>(lg.getGameCount());
        sortedSharedPrefList = lg.getOrderedSharedPrefNameList();
        ArrayList<String> sortedGameList = lg.getOrderedGameNameList(getContext().getResources());

        for (int i=0;i<lg.getGameCount();i++){
            LinearLayout entry = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.dialog_ensure_movability_min_moves_entry, null);

            ((TextView) entry.getChildAt(0)).setText(sortedGameList.get(i));
            EditText newInput = (EditText) entry.getChildAt(1);

            inputs.add(newInput);
            SharedPreferences savedGameData = getContext().getSharedPreferences(sortedSharedPrefList.get(i), MODE_PRIVATE);
            newInput.setText(String.valueOf(savedGameData.getInt(PREF_KEY_ENSURE_MOVABILITY_MIN_MOVES,DEFAULT_ENSURE_MOVABILITY_MIN_MOVES)));

            container.addView(entry);
        }

        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // When the user selects "OK", persist the new value
        if (positiveResult) {
            try {
                int[] numbers = new int[lg.getGameCount()];

                for (int i=0;i<inputs.size();i++){
                    numbers[i] = Integer.parseInt(inputs.get(i).getText().toString());

                    if (numbers[i] < 0) {
                        showToast(getContext().getString(R.string.settings_number_input_error),getContext());
                        return;
                    }
                }

                for (int i=0;i<lg.getGameCount();i++){
                    SharedPreferences savedGameData = getContext().getSharedPreferences(sortedSharedPrefList.get(i), MODE_PRIVATE);
                    savedGameData.edit().putInt(PREF_KEY_ENSURE_MOVABILITY_MIN_MOVES,numbers[i]).apply();
                }
            } catch (Exception e){
                showToast(getContext().getString(R.string.settings_number_input_error),getContext());
            }
        }
    }
}

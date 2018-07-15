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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.LoadGame;
import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomDialogPreference;

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

public class DialogPreferenceEnsureMovabilityMinMoves extends CustomDialogPreference implements View.OnClickListener{

    private Button makeGamesWinnableButton;
    private String winnableText;
    private ArrayList<EditText> inputs;
    private int gameCount;

    ArrayList<LoadGame.AllGameInformation> gameInfoList;
    ArrayList<SharedPreferences> sharedPrefList = new ArrayList<>();

    public DialogPreferenceEnsureMovabilityMinMoves(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_ensure_movability_min_moves);
        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        view.findViewById(R.id.settings_ensure_movability_make_games_winnable).setOnClickListener(this);
        view.findViewById(R.id.settings_ensure_movability_reset).setOnClickListener(this);

        LinearLayout container = (LinearLayout) view.findViewById(R.id.settings_ensure_movability_container);
        winnableText = getContext().getString(R.string.settings_ensure_movability_winnable);

        gameCount = lg.getGameCount();
        inputs = new ArrayList<>(gameCount);
        gameInfoList = lg.getOrderedGameInfoList();

        for (int i=0;i<gameCount;i++){
            sharedPrefList.add(getContext().getSharedPreferences(gameInfoList.get(i).getSharedPrefName(), MODE_PRIVATE));
        }

        for (int i=0;i<gameCount;i++){
            LinearLayout entry = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.dialog_ensure_movability_min_moves_entry, null);

            ((TextView) entry.getChildAt(0)).setText(gameInfoList.get(i).getName(getContext().getResources()));
            final EditText newInput = (EditText) entry.getChildAt(1);
            inputs.add(newInput);

            newInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus && newInput.getText().toString().equals(winnableText)){
                            newInput.setText("500");

                    }

                    if (!hasFocus && newInput.getText().toString().equals("500")){
                        newInput.setText(winnableText);
                    }
                }
            });

            int value = sharedPrefList.get(i).getInt(PREF_KEY_ENSURE_MOVABILITY_MIN_MOVES, gameInfoList.get(i).getEnsureMovabilityMoves());
            newInput.setText(value == 500 ? winnableText : String.valueOf(value));

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
                    String text = inputs.get(i).getText().toString();

                    if (text.equals(getContext().getString(R.string.settings_ensure_movability_winnable))){
                        numbers[i] = 500;
                    } else {
                        numbers[i] = Integer.parseInt(text);
                    }

                    if (numbers[i] < 0) {
                        showToast(getContext().getString(R.string.settings_number_input_error),getContext());
                        return;
                    }
                }

                for (int i=0;i<gameCount;i++){
                    sharedPrefList.get(i).edit().putInt(PREF_KEY_ENSURE_MOVABILITY_MIN_MOVES,numbers[i]).apply();
                }
            } catch (Exception e){
                showToast(getContext().getString(R.string.settings_number_input_error),getContext());
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.settings_ensure_movability_make_games_winnable:
                for (int i=0;i<gameCount;i++){

                    if (gameInfoList.get(i).canStartWinnableGame()) {
                        inputs.get(i).setText(winnableText);
                    }
                }
                break;
            case R.id.settings_ensure_movability_reset:
                for (int i=0;i<gameCount;i++){
                    inputs.get(i).setText(String.valueOf(gameInfoList.get(i).getEnsureMovabilityMoves()));
                }
                break;
        }

    }
}

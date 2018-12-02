/* Copyright (C) 2016  Tobias Bielefeld
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

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomDialogFragment;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * dialog which is shown after winning a game. It shows options to start a new game, or to return
 * to the main menu. It also shows the current score.
 */

public class DialogWon extends CustomDialogFragment {

    private static String KEY_SCORE = "PREF_KEY_SCORE";
    private static String KEY_BONUS = "BONUS";
    private static String KEY_TOTAL = "TOTAL";

    private long score, bonus, total;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedState) {
        final GameManager gameManager = (GameManager) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_won, null);

        builder.setCustomTitle(view)
                .setItems(R.array.won_menu, (dialog, which) -> {
                    // "which" argument contains index of selected item
                    switch (which) {
                        case 0:
                            gameLogic.newGame();
                            break;
                        case 1:
                            gameLogic.redeal();
                            break;
                        case 2:
                            if (gameManager.hasLoaded) {
                                timer.save();
                                gameLogic.setWonAndReloaded();
                                gameLogic.save();
                            }

                            gameManager.finish();
                            break;
                    }
                })
                .setNegativeButton(R.string.game_cancel, (dialog, id) -> {
                    //just cancel
                });

        LinearLayout layoutScores = view.findViewById(R.id.dialog_won_layout_scores);

        //only show the calculation of the score if bonus is enabled
        if (currentGame.isBonusEnabled()) {
            layoutScores.setVisibility(View.VISIBLE);
            TextView text1 = view.findViewById(R.id.dialog_won_text1);
            TextView text2 = view.findViewById(R.id.dialog_won_text2);
            TextView text3 = view.findViewById(R.id.dialog_won_text3);

            score = (savedState != null && savedState.containsKey(KEY_SCORE))
                    ? savedState.getLong(KEY_SCORE)
                    : scores.getPreBonus();
            bonus = (savedState != null && savedState.containsKey(KEY_BONUS))
                    ? savedState.getLong(KEY_BONUS)
                    : scores.getBonus();
            total = (savedState != null && savedState.containsKey(KEY_TOTAL))
                    ? savedState.getLong(KEY_TOTAL)
                    : scores.getScore();

            text1.setText(String.format(Locale.getDefault(), getContext()
                    .getString(R.string.dialog_win_score), score));
            text2.setText(String.format(Locale.getDefault(), getContext()
                    .getString(R.string.dialog_win_bonus), bonus));
            text3.setText(String.format(Locale.getDefault(), getContext()
                    .getString(R.string.dialog_win_total), total));
        } else {
            layoutScores.setVisibility(View.GONE);
        }

        return applyFlags(builder.create());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_SCORE, score);
        outState.putLong(KEY_BONUS, bonus);
        outState.putLong(KEY_TOTAL, total);
    }
}
/* Copyright (C) 2016  Tobias Bielefeld
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

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.ui.statistics.StatisticsActivity;

/**
 * Dialog for deleting all high scores
 */

public class DialogHighScoreDelete extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.statistics_button_delete_text)
                .setPositiveButton(R.string.game_confirm, (dialog, id) -> {
                    StatisticsActivity statistics = (StatisticsActivity) getActivity();
                    statistics.deleteHighScores();
                })
                .setNegativeButton(R.string.game_cancel, (dialog, id) -> {
                    // User cancelled the dialog
                });

        return builder.create();
    }
}
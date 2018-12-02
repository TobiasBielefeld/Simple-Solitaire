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
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomDialogFragment;
import de.tobiasbielefeld.solitaire.ui.GameManager;
import de.tobiasbielefeld.solitaire.ui.manual.Manual;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * dialog to handle new games or returning to main menu( in that case, cancel the current activity)
 */

public class DialogInGameHelpMenu extends CustomDialogFragment {

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final GameManager gameManager = (GameManager) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.settings_support)
                .setItems(R.array.help_menu, (dialog, which) -> {
                    // "which" argument contains index of selected item
                    switch (which) {
                        case 0:
                            if (!gameLogic.hasWon()) {
                                hint.start();
                            }
                            break;
                        case 1:
                            if (!gameLogic.hasWon()) {
                                autoMove.start();
                            }
                            break;
                        case 2:
                            if (!gameLogic.hasWon()) {
                                if (currentGame.hintTest() == null) {
                                    if (prefs.getShowDialogMixCards()) {
                                        prefs.putShowDialogMixCards(false);
                                        DialogMixCards dialogMixCards = new DialogMixCards();
                                        dialogMixCards.show(getFragmentManager(), "MIX_DIALOG");
                                    } else {
                                        currentGame.mixCards();
                                    }
                                } else {
                                    showToast(getString(R.string.dialog_mix_cards_not_available), getActivity());
                                }
                            }
                            break;
                        case 3:
                            Intent intent = new Intent(gameManager, Manual.class);
                            intent.putExtra(GAME, lg.getSharedPrefName());
                            startActivity(intent);
                            break;
                    }
                })
                .setNegativeButton(R.string.game_cancel, (dialog, id) -> {
                    //just cancel
                });

        return applyFlags(builder.create());
    }
}
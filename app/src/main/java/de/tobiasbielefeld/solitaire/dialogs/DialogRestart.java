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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.IntentCompat;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.ui.GameManager;
import de.tobiasbielefeld.solitaire.ui.manual.Manual;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * dialog to handle new games or returning to main menu( in that case, cancel the current activity)
 */

public class DialogRestart extends DialogFragment {

    Activity activity;
    GameManager gameManager;

    public DialogRestart(){
        super();
    }

    public void setArguments(GameManager gameManager){
        activity = gameManager;
        this.gameManager = gameManager;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(lg.getGameName())
                .setItems(R.array.restart_menu, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // "which" argument contains index of selected item
                        switch (which) {
                            case 0:
                                gameLogic.newGame();
                                break;
                            case 1:
                                gameLogic.redeal();
                                break;
                            case 2:
                                Intent intent = new Intent(activity, Manual.class);
                                intent.putExtra(GAME,lg.getGameName());
                                startActivity(intent);
                                break;
                            case 3:
                                if (gameManager.hasLoaded) {
                                    timer.save();
                                    gameLogic.setWonAndReloaded();
                                    gameLogic.save();
                                }

                                putSharedInt(PREF_KEY_CURRENT_GAME, DEFAULT_CURRENT_GAME);          //otherwise the menu would load the current game again, because last played game will start
                                activity.finish();
                                break;
                        }
                    }
                })
                .setNegativeButton(R.string.game_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //just cancel
                    }
                });

        return builder.create();
    }
}
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.SharedData;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CustomDialogFragment;
import de.tobiasbielefeld.solitaire.classes.HelperCardMovement;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.helper.EnsureMovability;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.animate;
import static de.tobiasbielefeld.solitaire.SharedData.cards;
import static de.tobiasbielefeld.solitaire.SharedData.ensureMovability;
import static de.tobiasbielefeld.solitaire.SharedData.gameLogic;
import static de.tobiasbielefeld.solitaire.SharedData.logText;
import static de.tobiasbielefeld.solitaire.SharedData.stacks;

/**
 * Dialog to show while the EnsureMovability asyncTask is running. It shows a spinning wheel
 * and also has a cancel button.
 */

public class DialogEnsureMovability extends CustomDialogFragment implements View.OnClickListener{

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_ensure_movability, null);

        Button cancelButton = (Button) view.findViewById(R.id.dialog_ensure_movability_cancel);
        cancelButton.setOnClickListener(this);

        builder.setView(view);
        return applyFlags(builder.create());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setCancelable(false);
    }

    @Override
    public void onClick(View view) {
        ensureMovability.stop();
    }

}

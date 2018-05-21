package de.tobiasbielefeld.solitaire.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.ExecutionException;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomDialogFragment;
import de.tobiasbielefeld.solitaire.handler.HandlerLoadGame;
import de.tobiasbielefeld.solitaire.helper.EnsureMovability;
import de.tobiasbielefeld.solitaire.ui.GameManager;
import de.tobiasbielefeld.solitaire.ui.statistics.StatisticsActivity;

import static de.tobiasbielefeld.solitaire.SharedData.cards;
import static de.tobiasbielefeld.solitaire.SharedData.currentGame;
import static de.tobiasbielefeld.solitaire.SharedData.gameLogic;
import static de.tobiasbielefeld.solitaire.SharedData.stacks;
import static de.tobiasbielefeld.solitaire.SharedData.stopMovements;
import static de.tobiasbielefeld.solitaire.classes.Card.movements.NONE;

/**
 * Dialog to show while the EnsureMovability asyncTask is running. It shows a spinning wheel
 * and also has a cancel button.
 */

public class DialogEnsureMovability extends CustomDialogFragment implements View.OnClickListener{

    EnsureMovability ensureMovabilty ;

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

    public void startTest(){
        ensureMovabilty = new EnsureMovability();
        ensureMovabilty.execute(this);
    }

    @Override
    public void onClick(View view) {
        stop();
    }

    public void stop(){
        this.dismiss();

        ensureMovabilty.cancel(true);
        stopMovements = false;
        gameLogic.redeal();
    }
}

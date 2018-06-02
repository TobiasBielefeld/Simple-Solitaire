package de.tobiasbielefeld.solitaire.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
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
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.helper.EnsureMovability;

import static de.tobiasbielefeld.solitaire.SharedData.cards;
import static de.tobiasbielefeld.solitaire.SharedData.gameLogic;
import static de.tobiasbielefeld.solitaire.SharedData.logText;
import static de.tobiasbielefeld.solitaire.SharedData.stacks;

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
        dismiss();
        ensureMovabilty.cancel(true);
    }

    public void interrupt(){
        dismiss();
        ensureMovabilty.interrupt();
    }

    public boolean isRunning(){
        return SharedData.stopUiUpdates;
    }
}

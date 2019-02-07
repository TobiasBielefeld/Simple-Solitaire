package de.tobiasbielefeld.solitaire.classes;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;

import de.tobiasbielefeld.solitaire.SharedData;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Little custom dialog fragment for the in game dialogs. I added a fullscreen mode, but the dialogs
 * would destroy it when displaying so they have to apply some flags to keep the fullscreen mode.
 */

public class CustomDialogFragment extends DialogFragment {

    private static CustomDialogFragment shownDialog;

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        shownDialog = this;
        SharedData.isDialogVisible = true;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (shownDialog == this) {
            SharedData.isDialogVisible = false;
        }
    }

    protected AlertDialog applyFlags(AlertDialog dialog) {
        if (prefs.getSavedImmersiveMode()) {
            Window window = dialog.getWindow();

            if (window != null) {
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }

            if (dialog.getListView() != null) {
                dialog.getListView().setScrollbarFadingEnabled(false);
            }
        }

        return dialog;
    }
}

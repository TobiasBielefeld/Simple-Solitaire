package de.tobiasbielefeld.solitaire.classes;

import android.app.AlertDialog;
import android.support.v4.app.DialogFragment;
import android.view.Window;
import android.view.WindowManager;

import static de.tobiasbielefeld.solitaire.SharedData.prefs;

/**
 * Little custom dialog fragment for the in game dialogs. I added a fullscreen mode, but the dialogs
 * would destroy it when displaying so they have to apply some flags to keep the fullscreen mode.
 */

public class CustomDialogFragment extends DialogFragment {

    /*@Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);

        if (prefs.getSavedImmersiveMode()) {

            Window window = getDialog().getWindow();

            if (window != null) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }
        }
    }*/

    protected AlertDialog applyFlags(AlertDialog dialog){
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

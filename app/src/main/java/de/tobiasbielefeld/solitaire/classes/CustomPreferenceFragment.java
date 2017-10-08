package de.tobiasbielefeld.solitaire.classes;

import android.content.Context;
import android.preference.PreferenceFragment;

import static de.tobiasbielefeld.solitaire.SharedData.reinitializeData;

/**
 * Custom PreferenceFragment, to override onAttach. If the app got killed within a
 * PreferenceFragment and restarted, the data has to be reinitialized
 */

public class CustomPreferenceFragment extends PreferenceFragment {

    @Override
    public void onAttach(Context context) {
        reinitializeData(context);
        super.onAttach(context);
    }
}

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

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomDialogPreference;

import static de.tobiasbielefeld.solitaire.SharedData.*;


/*
 * custom dialog to set the background music volume. it can be set from 0 (off) to 100%.
 */

public class DialogPreferenceMusicVolume extends CustomDialogPreference implements SeekBar.OnSeekBarChangeListener{

    private SeekBar mSeekBar;
    private TextView mTextView;

    public DialogPreferenceMusicVolume(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_background_volume);
        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        mTextView = (TextView) view.findViewById(R.id.textView);
        mSeekBar = (SeekBar) view.findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(this);

        int volume = prefs.getSavedBackgroundVolume();
        mSeekBar.setProgress(volume);
        setProgressText(volume);

        super.onBindDialogView(view);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        setProgressText(i);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // When the user selects "OK", persist the new value
        if (positiveResult) {
            prefs.saveBackgroundVolume(mSeekBar.getProgress());
        }
    }

    private void setProgressText(int value){
        mTextView.setText(String.format(Locale.getDefault(),"%s %%",value));
    }
}

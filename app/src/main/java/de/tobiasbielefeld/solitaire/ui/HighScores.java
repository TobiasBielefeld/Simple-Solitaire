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


package de.tobiasbielefeld.solitaire.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.helper.Scores;

import static de.tobiasbielefeld.solitaire.SharedData.game;
import static de.tobiasbielefeld.solitaire.SharedData.savedData;
import static de.tobiasbielefeld.solitaire.SharedData.scores;
import static de.tobiasbielefeld.solitaire.SharedData.showToast;

/**
 * this activity shows the high scores. The entries are generated in onCreate and shown
 * in a vertical list. There is also a button to delete all entries with a dialog
 */

public class HighScores extends AppCompatActivity {

    private TextView text1;
    private LinearLayout layoutScores;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                                                         //initialize stuff
        setContentView(R.layout.activity_high_scores);

        ActionBar actionBar = getSupportActionBar();
        layoutScores = (LinearLayout) findViewById(R.id.highScoresLinearLayout1);                   //load the layouts and textView
        text1 = (TextView) findViewById(R.id.highScoresTextViewGamesWon);

        if (actionBar != null)                                                                      //set a nice back arrow in the actionBar
            actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedData.getBoolean(getString(R.string.pref_key_hide_status_bar), false))              //if fullscreen was saved, set it
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setOrientation();                                                                           //orientation according to preference

        text1.setText(String.format(Locale.getDefault(), "%s: %s", getString(                       //show the number of won games
                R.string.high_scores_games_won), game.getNumberWonGames()));

        for (int i = 0; i < Scores.MAX_SAVED_SCORES; i++) {                                         //for each entry in highScores, add a new view with it
            if (scores.get(i, 0) == 0)                                                              //if the score is zero, don't show it
                continue;

            final LinearLayout linearLayout2 = new LinearLayout(this);                              //new layout for the entry

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(                       //create new layout params
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);//
            params.setMargins(0, 0, 0, 10);                                                         //add some padding
            linearLayout2.setLayoutParams(params);                                                  //and apply them
            linearLayout2.setGravity(Gravity.CENTER);                                               //set gravity
            linearLayout2.setOrientation(LinearLayout.HORIZONTAL);                                  //also set orientation

            TextView textView = new TextView(this);                                                 //new textView for the score of the entry
            textView.setText(String.format(Locale.getDefault(),
                    "%s. %s %s ", i + 1, getString(         //add the score
                    R.string.scores_score), scores.get(i, 0)));
            textView.setTextSize(20);                                                               //and set text size

            TextView textView2 = new TextView(this);                                                //new textView for the time of the entry
            textView2.setText(String.format(Locale.getDefault(), "%s %02d:%02d:%02d",               //add it to the view
                    getString(R.string.scores_time),
                    scores.get(i, 1) / 3600,
                    (scores.get(i, 1) % 3600) / 60,
                    (scores.get(i, 1) % 60)));
            textView2.setTextSize(20);                                                              //set size

            linearLayout2.addView(textView);                                                        //now add both textViews to the entry layout
            linearLayout2.addView(textView2);
            layoutScores.addView(linearLayout2);                                                    //and finally add the new entry layout to the scores layout
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {                                           //only menu item is the back button in the action bar
        finish();                                                                                   //so finish this activity
        return true;
    }

    public void onClick(View view) {                                                                //handles clicks for deleting the scores
        DialogFragment newFragment = new myDialog();
        newFragment.show(getSupportFragmentManager(), "high_score_delete");
    }

    public static class myDialog extends DialogFragment {                                           //dialog for deleting the entries
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {



            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.highScoresButtonDelete_text)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            HighScores highScores = (HighScores) getActivity();
                            highScores.deleteHighScores();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

            return builder.create();
        }
    }

    private void deleteHighScores() {
        scores.delete_high_scores();                                            //so delete the high scores
        game.deleteNumberWonGames();                                            //also delete the number of won games
        text1.setText(String.format(Locale.getDefault(),
                "%s: %s", getString(   //refresh the textView
                        R.string.high_scores_games_won), game.getNumberWonGames()));
        layoutScores.setVisibility(View.GONE);                                  //hide the scores, because they were deleted
        showToast(getString(R.string.highScoresButtonDeleted_all_entries));
    }


    private void setOrientation() {
        switch (savedData.getString("pref_key_orientation","1")){
            case "1": //follow system settings
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                break;
            case "2": //portrait
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case "3": //landscape
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case "4": //landscape upside down
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
        }
    }
}

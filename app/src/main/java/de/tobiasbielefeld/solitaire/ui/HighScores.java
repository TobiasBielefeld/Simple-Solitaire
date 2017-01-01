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

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.dialogs.HighScoreDeleteDialog;
import de.tobiasbielefeld.solitaire.helper.Scores;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * this activity shows the high scores. The entries are generated in onCreate and shown
 * in a vertical list. There is also a button to delete all entries with a dialog
 */

public class HighScores extends AppCompatActivity {

    private TextView text1;
    private LinearLayout layoutScores;
    private Toast toast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                                                         //initialize stuff
        setContentView(R.layout.activity_high_scores);

        if (savedSharedData==null) {
            savedSharedData = PreferenceManager.getDefaultSharedPreferences(this);
        }

        ActionBar actionBar = getSupportActionBar();
        layoutScores = (LinearLayout) findViewById(R.id.highScoresLinearLayout1);                   //load the layouts and textView
        text1 = (TextView) findViewById(R.id.highScoresTextViewGamesWon);

        if (actionBar != null)                                                                      //set a nice back arrow in the actionBar
            actionBar.setDisplayHomeAsUpEnabled(true);

        showOrHideStatusBar(this);
        setOrientation(this);                                                                       //orientation according to preference

        text1.setText(String.format(Locale.getDefault(), "%s: %s", getString(                       //show the number of won games
                R.string.statistics_games_won), gameLogic.getNumberWonGames()));

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
                    R.string.game_score), scores.get(i, 0)));
            textView.setTextSize(20);                                                               //and set text size

            TextView textView2 = new TextView(this);                                                //new textView for the time of the entry
            textView2.setText(String.format(Locale.getDefault(), "%s %02d:%02d:%02d",               //add it to the view
                    getString(R.string.game_time),
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
    public boolean onOptionsItemSelected(MenuItem item) {
        //only menu item is the back button in the action bar, so just finish
        finish();
        return true;
    }

    public void onClick(View view) {
        DialogFragment deleteDialog = new HighScoreDeleteDialog();
        deleteDialog.show(getSupportFragmentManager(), "high_score_delete");
    }

    public void deleteHighScores() {
        scores.deleteHighScores();
        gameLogic.deleteNumberWonGames();
        text1.setText(String.format(Locale.getDefault(),
                "%s: %s", getString(   //refresh the textView
                        R.string.statistics_games_won), gameLogic.getNumberWonGames()));
        layoutScores.setVisibility(View.GONE);
        showToast(getString(R.string.statistics_button_deleted_all_entries));
    }

    private void showToast(String text) {
        if (toast == null)
            toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        else
            toast.setText(text);

        toast.show();
    }
}

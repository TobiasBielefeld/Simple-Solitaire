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
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomAppCompatActivity;
import de.tobiasbielefeld.solitaire.dialogs.HighScoreDeleteDialog;
import de.tobiasbielefeld.solitaire.helper.Scores;

import static android.view.View.GONE;
import static de.tobiasbielefeld.solitaire.SharedData.currentGame;
import static de.tobiasbielefeld.solitaire.SharedData.gameLogic;
import static de.tobiasbielefeld.solitaire.SharedData.scores;

public class Statistics extends CustomAppCompatActivity {

    private TableLayout tableLayout;
    private TextView textWonGames, textWinPercentage, textAdditonalStatistics;
    private Toast toast;


    /**
     * Loads the high score list
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_statistics);

        int padding = (int) getResources().getDimension(R.dimen.statistics_table_padding);
        int textSize = getResources().getInteger(R.integer.statistics_text_size);
        boolean addedEntries = false;
        TableRow row;

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tableLayout = (TableLayout) findViewById(R.id.statisticsTableHighScores);
        textWonGames = (TextView) findViewById(R.id.statisticsTextViewGamesWon);
        textWinPercentage = (TextView) findViewById(R.id.statisticsTextViewWinPercentage);
        textAdditonalStatistics = (TextView) findViewById(R.id.statisticsAdditionalText);

        //if the app got killed while the statistics are open and then the user restarts the app,
        //my helper classes aren't initialized so they can't be used. In this case, simply
        //close the statistics
        try {
            loadData();
        } catch (NullPointerException e){
            finish();
            return;
        }

        for (int i = 0; i < Scores.MAX_SAVED_SCORES; i++) {                                         //for each entry in highScores, add a new view with it
            if (scores.get(i, 0) == 0)                                                              //if the score is zero, don't show it
                continue;

            if (!addedEntries)
                addedEntries = true;

            row = new TableRow(this);

            TextView textView1 = new TextView(this);
            TextView textView2 = new TextView(this);
            TextView textView3 = new TextView(this);
            TextView textView4 = new TextView(this);

            textView1.setText(String.format(Locale.getDefault(),
                    "%s", scores.get(i, 0)));
            textView2.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d",               //add it to the view
                    scores.get(i, 1) / 3600,
                    (scores.get(i, 1) % 3600) / 60,
                    (scores.get(i, 1) % 60)));

            textView3.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(scores.get(i, 2)));
            //textView4.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(scores.get(i,2)));
            textView4.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(scores.get(i, 2)));

            textView1.setPadding(padding, 0, padding, 0);
            textView2.setPadding(padding, 0, padding, 0);
            textView3.setPadding(padding, 0, padding, 0);
            textView4.setPadding(padding, 0, padding, 0);

            textView1.setTextSize(textSize);
            textView2.setTextSize(textSize);
            textView3.setTextSize(textSize);
            textView4.setTextSize(textSize);

            textView1.setGravity(Gravity.CENTER);
            textView2.setGravity(Gravity.CENTER);
            textView3.setGravity(Gravity.CENTER);
            textView4.setGravity(Gravity.CENTER);

            row.addView(textView1);
            row.addView(textView2);
            row.addView(textView3);
            row.addView(textView4);
            row.setGravity(Gravity.CENTER);
            tableLayout.addView(row);
        }
    }

    /**
     * loads the other shown data
     */
    private void loadData() {
        int wonGames = gameLogic.getNumberWonGames();
        int totalGames = gameLogic.getNumberOfPlayedGames();

        textWonGames.setText(String.format(Locale.getDefault(), getString(R.string.statistics_text_won_games), wonGames, totalGames));
        textWinPercentage.setText(String.format(Locale.getDefault(), getString(R.string.statistics_win_percentage), totalGames > 0 ? ((float) wonGames * 100 / totalGames) : 0.0));

        String additionalText = currentGame.getAdditionalStatisticsData(getResources());

        if (additionalText!=null){
            textAdditonalStatistics.setText(additionalText);
            textAdditonalStatistics.setVisibility(View.VISIBLE);
        }
    }

    /**
     * deletes the data, reloads it and hides the high score list (easier way to "delete" it)
     */
    public void deleteHighScores() {
        scores.deleteHighScores();
        gameLogic.deleteStatistics();
        currentGame.deleteAdditionalStatisticsData();
        loadData();
        tableLayout.setVisibility(GONE);
        showToast(getString(R.string.statistics_button_deleted_all_entries));
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_statistics, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_delete:
                DialogFragment deleteDialog = new HighScoreDeleteDialog();
                deleteDialog.show(getSupportFragmentManager(), "high_score_delete");
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }

    private void showToast(String text) {
        if (toast == null)
            toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        else
            toast.setText(text);

        toast.show();
    }
}

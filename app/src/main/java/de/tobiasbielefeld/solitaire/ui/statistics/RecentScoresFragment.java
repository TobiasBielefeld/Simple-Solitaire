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

package de.tobiasbielefeld.solitaire.ui.statistics;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.helper.Scores;

import static de.tobiasbielefeld.solitaire.SharedData.currentGame;
import static de.tobiasbielefeld.solitaire.SharedData.scores;

/**
 * Shows the recent scores of the current game
 */

public class RecentScoresFragment extends Fragment{

    private String dollar;

    /**
     * Loads the high score list
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics_tab3, container, false);

        int padding = (int) getResources().getDimension(R.dimen.statistics_table_padding);
        int textSize = getResources().getInteger(R.integer.statistics_text_size);
        TableRow row;

        TableLayout tableLayout = (TableLayout) view.findViewById(R.id.statisticsTableHighScores);
        TextView textNoEntries = (TextView) view.findViewById(R.id.statisticsTextNoEntries);

        //if the app got killed while the statistics are open and then the user restarts the app,
        //my helper classes aren't initialized so they can't be used. In this case, simply
        //close the statistics
        try {
            loadData();
        } catch (NullPointerException e) {
            getActivity().finish();
            return view;
        }

        if (scores.getRecentScore(0, 2) != 0) {
            textNoEntries.setVisibility(View.GONE);
        }

        for (int i = 0; i < Scores.MAX_SAVED_SCORES; i++) {                                         //for each entry in highScores, add a new view with it
            if (scores.getRecentScore(i, 2) == 0) {                                                         //if the score is zero, don't show it
                continue;
            }

            row = new TableRow(getContext());

            TextView textView1 = new TextView(getContext());
            TextView textView2 = new TextView(getContext());
            TextView textView3 = new TextView(getContext());
            TextView textView4 = new TextView(getContext());

            textView1.setText(String.format(Locale.getDefault(),
                    "%s %s", scores.getRecentScore(i, 0), dollar));
            textView2.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d",               //add it to the view
                    scores.getRecentScore(i, 1) / 3600,
                    (scores.getRecentScore(i, 1) % 3600) / 60,
                    (scores.getRecentScore(i, 1) % 60)));

            textView3.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(scores.getRecentScore(i, 2)));
            textView4.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(scores.getRecentScore(i, 2)));

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

        return view;
    }

    /**
     * loads the other shown data
     */
    private void loadData() {
        dollar = currentGame.isPointsInDollar() ? "$" : "";
    }
}
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
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Locale;

import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Shows the high scores of the current game
 */

public class StatisticsFragment extends Fragment {

    private TextView textWonGames, textWinPercentage, textAdditionalStatisticsTitle,
            textAdditionalStatisticsValue, textTotalTimePlayed, textTotalPointsEarned,
            textTotalHintsShown, textTotalNumberUndos;

    private CardView winPercentageCardView;

    private TableRow tableRowAdditionalText;


    /**
     * Loads the high score list
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics_tab1, container, false);

        winPercentageCardView = view.findViewById(R.id.statisticsCardViewWinPercentage);

        textWonGames = view.findViewById(R.id.statisticsTextViewGamesWon);
        textWinPercentage = view.findViewById(R.id.statisticsTextViewWinPercentage);
        textAdditionalStatisticsTitle = view.findViewById(R.id.statisticsAdditionalText);
        textAdditionalStatisticsValue = view.findViewById(R.id.statisticsAdditionalTextValue);
        textTotalTimePlayed = view.findViewById(R.id.statisticsTotalTimePlayed);
        textTotalPointsEarned = view.findViewById(R.id.statisticsTotalPointsEarned);
        textTotalHintsShown = view.findViewById(R.id.statisticsTotalHintsShown);
        textTotalNumberUndos = view.findViewById(R.id.statisticsTotalUndoMovements);
        tableRowAdditionalText = view.findViewById(R.id.statisticsAdditionalRow);

        //if the app got killed while the statistics are open and then the user restarts the app,
        //my helper classes aren't initialized so they can't be used. In this case, simply
        //close the statistics
        try {
            loadData();
        } catch (NullPointerException e) {
            getActivity().finish();
            return view;
        }

        ((StatisticsActivity) getActivity()).setCallback(this::updateWinPercentageView);

        winPercentageCardView.setVisibility(prefs.getSavedStatisticsHideWinPercentage()
                ? View.GONE : View.VISIBLE);

        return view;
    }

    /**
     * loads the other shown data
     */
    private void loadData() {
        int wonGames = prefs.getSavedNumberOfWonGames();
        int totalGames = prefs.getSavedNumberOfPlayedGames();
        int totalHintsShown = prefs.getSavedTotalHintsShown();
        int totalNumberUndos = prefs.getSavedTotalNumberUndos();

        long totalTime = prefs.getSavedTotalTimePlayed();
        long totalPoints = prefs.getSavedTotalPointsEarned();

        textWonGames.setText(String.format(Locale.getDefault(),
                getString(R.string.statistics_text_won_games), wonGames, totalGames));
        textWinPercentage.setText(String.format(Locale.getDefault(),
                getString(R.string.statistics_win_percentage),
                totalGames > 0 ? ((float) wonGames * 100 / totalGames) : 0.0));
        textTotalTimePlayed.setText(String.format(Locale.getDefault(),
                "%02d:%02d:%02d", totalTime / 3600, (totalTime % 3600) / 60, totalTime % 60));
        textTotalHintsShown.setText(String.format(Locale.getDefault(), "%d", totalHintsShown));
        textTotalNumberUndos.setText(String.format(Locale.getDefault(), "%d", totalNumberUndos));
        textTotalPointsEarned.setText(String.format(Locale.getDefault(), currentGame.isPointsInDollar() ? "%d $" : "%d", totalPoints));

        boolean added = currentGame.setAdditionalStatisticsData(
                getResources(), textAdditionalStatisticsTitle, textAdditionalStatisticsValue);

        if (added) {
            tableRowAdditionalText.setVisibility(View.VISIBLE);
        }
    }

    private void updateWinPercentageView(boolean hide) {
        if (winPercentageCardView != null) {
            winPercentageCardView.setVisibility(hide ? View.GONE : View.VISIBLE);
        }
    }
}
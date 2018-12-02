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

package de.tobiasbielefeld.solitaire.ui.manual;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Games Page contains a button for each game. If one button is pressed, the view with the buttons
 * will be hidden and a scroll view with the manual entry will be loaded. A button click gets a prefix
 * for the string resources
 */

public class ManualGames extends Fragment implements View.OnClickListener {

    private static int COLUMNS = 2;

    private GamePageShown mCallback;

    private ScrollView layout1, scrollView;
    private TextView textName, textStructure, textObjective, textRules, textScoring, textBonus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manual_games, container, false);

        mCallback.setGamePageShown(false);

        layout1 = view.findViewById(R.id.manual_games_layout_selection);
        scrollView = view.findViewById(R.id.manual_games_scrollView);
        textName = view.findViewById(R.id.manual_games_name);
        textStructure = view.findViewById(R.id.manual_games_structure);
        textObjective = view.findViewById(R.id.manual_games_objective);
        textRules = view.findViewById(R.id.manual_games_rules);
        textScoring = view.findViewById(R.id.manual_games_scoring);
        textBonus = view.findViewById(R.id.manual_games_bonus);

        layout1.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        //if the manual is called from the in game menu, show the corresponding game rule page
        if (getArguments() != null && getArguments().containsKey(GAME)) {
            loadGameText(getArguments().getString(GAME));
        }

        //load the table
        String[] gameList = lg.getDefaultGameNameList(getResources());
        TableRow row = new TableRow(getContext());
        TableLayout tableLayout = view.findViewById(R.id.manual_games_container);
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);

        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
        params.weight = 1;

        //add each button
        for (int i = 0; i < lg.getGameCount(); i++) {
            Button entry = new Button(getContext());

            if (i % COLUMNS == 0) {
                row = new TableRow(getContext());
                tableLayout.addView(row);
            }

            entry.setBackgroundResource(typedValue.resourceId);
            entry.setEllipsize(TextUtils.TruncateAt.END);
            entry.setMaxLines(1);
            entry.setLayoutParams(params);
            entry.setText(gameList[i]);
            entry.setOnClickListener(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                entry.setAllCaps(false);
            }

            row.addView(entry);
        }

        //add some dummies to the last row, if necessary
        while (row.getChildCount() < COLUMNS) {
            FrameLayout dummy = new FrameLayout(getContext());
            dummy.setLayoutParams(params);
            row.addView(dummy);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (GamePageShown) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement TextClicked");
        }
    }

    @Override
    public void onClick(View v) {
        //get index of the button as seen from the container
        TableRow row = (TableRow) v.getParent();
        TableLayout table = (TableLayout) row.getParent();
        int index = table.indexOfChild(row) * COLUMNS + row.indexOfChild(v);

        loadGameText(index);
    }

    private void loadGameText(int index) {
        String gameName = lg.getSharedPrefNameOfGame(index);   //get prefix
        loadGameText(gameName);
    }

    private void loadGameText(String gameName) {
        try {
            //load everything
            textName.setText((getString(getResources().getIdentifier("games_" + gameName, "string", getActivity().getPackageName()))));
            textStructure.setText(getString(getResources().getIdentifier("manual_" + gameName + "_structure", "string", getActivity().getPackageName())));
            textObjective.setText(getString(getResources().getIdentifier("manual_" + gameName + "_objective", "string", getActivity().getPackageName())));
            textRules.setText(getString(getResources().getIdentifier("manual_" + gameName + "_rules", "string", getActivity().getPackageName())));
            textScoring.setText(getString(getResources().getIdentifier("manual_" + gameName + "_scoring", "string", getActivity().getPackageName())));
            textBonus.setVisibility(gameName.equals("Vegas") ? View.GONE : View.VISIBLE);           //TODO: Manage this in a better way

            //when the back button is pressed, it should return to the main page from the games, not to the start page.
            //this way is easier than implementing an interface to control what happens in onBackPressed()
            ((Manual) getActivity()).setGamePageShown(true);

            layout1.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            //no page available
            Log.e("Manual page not found", gameName + ": " + e.toString());
            showToast(getString(R.string.page_load_error), getContext());
        }
    }

    public interface GamePageShown {
        void setGamePageShown(boolean value);
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }
}

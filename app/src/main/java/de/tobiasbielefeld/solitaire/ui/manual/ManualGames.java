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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Games Page contains a button for each game. If one button is pressed, the view with the buttons
 * will be hidden and a scroll view with the manual entry will be loaded. A button click gets a prefix
 * for the string resources
 */

public class ManualGames extends Fragment implements View.OnClickListener {


    ScrollView layout1, scrollView;
    int currentGameButtonID;
    TextView textName, textStructure, textObjective, textRules, textScoring;
    private Toast toast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manual_games, container, false);

        lg.loadManualButtons(view, this);
        ((Manual) getActivity()).setGamePageShown(false);

        layout1 = (ScrollView) view.findViewById(R.id.manual_games_layout_selection);
        scrollView = (ScrollView) view.findViewById(R.id.manual_games_scrollView);
        textName = (TextView) view.findViewById(R.id.manual_games_name);
        textStructure = (TextView) view.findViewById(R.id.manual_games_structure);
        textObjective = (TextView) view.findViewById(R.id.manual_games_objective);
        textRules = (TextView) view.findViewById(R.id.manual_games_rules);
        textScoring = (TextView) view.findViewById(R.id.manual_games_scoring);

        layout1.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        //if the manual is called from the in game menu, show the corresponding game rule page
        if (getArguments()!=null && getArguments().containsKey(GAME)){
            String gameName = lg.manualName(getActivity(),getArguments().getString(GAME));
            loadGameText(gameName);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        loadGameText(v.getId());
    }

    private void loadGameText(int id) {
        currentGameButtonID = id;
        String gameName = lg.manualClick(id);   //get prefix
        loadGameText(gameName);
    }

    private void loadGameText(String gameName) {
        try {
            //load everything
            textName.setText((getString(getResources().getIdentifier("games_" + gameName + "", "string", getActivity().getPackageName()))));
            textStructure.setText(getString(getResources().getIdentifier("manual_" + gameName + "_structure", "string", getActivity().getPackageName())));
            textObjective.setText(getString(getResources().getIdentifier("manual_" + gameName + "_objective", "string", getActivity().getPackageName())));
            textRules.setText(getString(getResources().getIdentifier("manual_" + gameName + "_rules", "string", getActivity().getPackageName())));
            textScoring.setText(getString(getResources().getIdentifier("manual_" + gameName + "_scoring", "string", getActivity().getPackageName())));

            //when the back button is pressed, it should return to the main page from the games, not to the start page.
            //this way is easier than implementing an interface to control what happens in onBackPressed()
            layout1.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            ((Manual) getActivity()).setGamePageShown(true);

        } catch (Exception e) {
            //no page available
            Log.e("Manual page not found", e.toString());
            Log.e("hey",gameName);
            showToast(getString(R.string.page_load_error));
        }


    }

    public void showToast(final String text) {

        Activity activity = getActivity();

        if (toast == null)
            toast = Toast.makeText(activity, text, Toast.LENGTH_SHORT);
        else
            toast.setText(text);

        toast.show();
    }
}

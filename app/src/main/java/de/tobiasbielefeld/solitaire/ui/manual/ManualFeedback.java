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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.tobiasbielefeld.solitaire.R;

/**
 * Feedback contains just text and two buttons
 */

public class ManualFeedback extends Fragment implements View.OnClickListener {

    Button buttonGoogle, buttonGitHub;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_manual_feedback, container, false);

        buttonGoogle = view.findViewById(R.id.manual_feedback_button_google_play);
        buttonGitHub = view.findViewById(R.id.manual_feedback_button_github);

        buttonGoogle.setOnClickListener(this);
        buttonGitHub.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.manual_feedback_button_google_play:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_market_URL))));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_web_URL))));
                }
                break;
            case R.id.manual_feedback_button_github:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_issues_URL))));
                break;
        }
    }
}

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

package de.tobiasbielefeld.solitaire.ui.about;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import de.tobiasbielefeld.solitaire.BuildConfig;
import de.tobiasbielefeld.solitaire.R;

/**
 * Shows some infos about my app
 */

public class InformationFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_about_tab1, container, false);

        TextView textViewBuildDate = (TextView) view.findViewById(R.id.aboutTextViewBuild);         //build date
        TextView textViewAppVersion = (TextView) view.findViewById(R.id.aboutTextViewVersion);      //app version
        TextView textViewCC0License = (TextView) view.findViewById(R.id.aboutTextViewLicense);      //cc0 license from the pictures
        TextView textViewGitHubLink = (TextView) view.findViewById(R.id.aboutTextViewGitHubLink);   //link for the gitHub repo

        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");                           //generate a new date for build date
        String strDt = simpleDate.format(BuildConfig.TIMESTAMP);                                    //get the build date as a string


        //update the textViews
        textViewAppVersion.setText(String.format(Locale.getDefault(), "%s: %s", getString(R.string.app_version), BuildConfig.VERSION_NAME));
        textViewBuildDate.setText(String.format(Locale.getDefault(), "%s: %s", getString(R.string.about_build_date), strDt));
        textViewCC0License.setMovementMethod(LinkMovementMethod.getInstance());
        textViewGitHubLink.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }

    @Override
    public void onClick(View v) {
        //nothing
    }
}
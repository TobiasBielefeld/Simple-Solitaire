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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;

import de.tobiasbielefeld.solitaire.BuildConfig;
import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.stringFormat;

/**
 * Shows some info about my app
 */

public class InformationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_tab1, container, false);

        TableLayout table_further_contributors = view.findViewById(R.id.about_table_further_contributors);
        TableLayout table_translators = view.findViewById(R.id.about_table_translators);

        TextView textViewBuildDate = view.findViewById(R.id.aboutTextViewBuild);       //build date
        TextView textViewAppVersion = view.findViewById(R.id.aboutTextViewVersion);    //app version
        TextView textViewGitHubLink = view.findViewById(R.id.aboutTextViewGitHubLink); //link for the gitHub repo
        TextView textViewLicenseLink = view.findViewById(R.id.aboutTextViewLicenseLink);

        String buildDate = DateFormat.getDateInstance().format(BuildConfig.TIMESTAMP); //get the build date in locale time format

        //update the textViews
        textViewAppVersion.setText(stringFormat(BuildConfig.VERSION_NAME));
        textViewBuildDate.setText(stringFormat(buildDate));

        //enable the hyperlink clicks
        TextView[] textViews = new TextView[]{textViewGitHubLink, textViewLicenseLink};

        for (TextView textView : textViews) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        //enable hyperlinks in "Translations"
        for (int i = 0; i < table_translators.getChildCount(); i++) {
            TableRow row = (TableRow) table_translators.getChildAt(i);

            //first entry is language title, no need for hyperlinking that
            for (int j = 1; j < row.getChildCount(); j++) {
                TextView text = (TextView) row.getChildAt(j);
                text.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }

        //enable hyperlinks in "Further contributors"
        for (int i = 0; i < table_further_contributors.getChildCount(); i++) {
            TableRow row = (TableRow) table_further_contributors.getChildAt(i);

            for (int j = 0; j < row.getChildCount(); j++) {
                TextView text = (TextView) row.getChildAt(j);
                text.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }

        return view;
    }
}
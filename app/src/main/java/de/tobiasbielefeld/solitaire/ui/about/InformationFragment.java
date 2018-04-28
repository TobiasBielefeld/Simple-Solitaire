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

import java.text.DateFormat;

import de.tobiasbielefeld.solitaire.BuildConfig;
import de.tobiasbielefeld.solitaire.R;
import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Shows some info about my app
 */

public class InformationFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_tab1, container, false);

        TextView textViewBuildDate = (TextView) view.findViewById(R.id.aboutTextViewBuild);                     //build date
        TextView textViewAppVersion = (TextView) view.findViewById(R.id.aboutTextViewVersion);                  //app version
        TextView textViewGitHubLink = (TextView) view.findViewById(R.id.aboutTextViewGitHubLink);               //link for the gitHub repo
        TextView textViewLicenseLink = (TextView) view.findViewById(R.id.aboutTextViewLicenseLink);

        TextView textJapaneseContributors = (TextView) view.findViewById(R.id.about_japanese_contributors);
        TextView textEsperantoContributors = (TextView) view.findViewById(R.id.about_esperanto_contributors);
        TextView textPolishContributors = (TextView) view.findViewById(R.id.about_polish_contributors);
        TextView textFrenchContributors = (TextView) view.findViewById(R.id.about_french_contributors);
        TextView textFinnishContributors = (TextView) view.findViewById(R.id.about_finnish_contributors);
        TextView textTurkishContributors = (TextView) view.findViewById(R.id.about_turkish_contributors);
        TextView textSpanishArgentinaContributors = (TextView) view.findViewById(R.id.about_spanish_argentina_contributers);

        TextView textFurtherContributors1 = (TextView) view.findViewById(R.id.about_further_contributors_1);
        TextView textFurtherContributors2 = (TextView) view.findViewById(R.id.about_further_contributors_2);
        TextView textFurtherContributors3 = (TextView) view.findViewById(R.id.about_further_contributors_3);

        String buildDate = DateFormat.getDateInstance().format(BuildConfig.TIMESTAMP);                          //get the build date in locale time format

        //update the textViews
        textViewAppVersion.setText(stringFormat(BuildConfig.VERSION_NAME));
        textViewBuildDate.setText(stringFormat(buildDate));

        //enable the hyperlink clicks
        TextView[] textViews = new TextView[]{textViewGitHubLink,textViewLicenseLink,textJapaneseContributors,textEsperantoContributors,
                textPolishContributors,textFinnishContributors,textTurkishContributors,textFrenchContributors,textFurtherContributors1,
                textFurtherContributors2, textFurtherContributors3, textSpanishArgentinaContributors};

        for (TextView textView : textViews){
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }



        return view;
    }
}
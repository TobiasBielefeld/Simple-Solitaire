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
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Locale;
import java.util.regex.Pattern;

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

        TextView textJapaneseContributers = (TextView) view.findViewById(R.id.about_japanese_contributers);
        TextView textEsperantoContributers = (TextView) view.findViewById(R.id.about_esperanto_contributers);
        TextView textPolishContributers = (TextView) view.findViewById(R.id.about_polish_contributers);
        TextView textFrenchContributers = (TextView) view.findViewById(R.id.about_french_contributers);
        TextView textFinnishContributers = (TextView) view.findViewById(R.id.about_finnish_contributers);
        TextView textTurkishContributers = (TextView) view.findViewById(R.id.about_turkish_contributers);

        TextView textFurtherContributers1 = (TextView) view.findViewById(R.id.about_further_contributers_1);
        TextView textFurtherContributers2 = (TextView) view.findViewById(R.id.about_further_contributers_2);

        String buildDate = DateFormat.getDateInstance().format(BuildConfig.TIMESTAMP);                          //get the build date in locale time format

        //update the textViews
        textViewAppVersion.setText(stringFormat(BuildConfig.VERSION_NAME));
        textViewBuildDate.setText(stringFormat(buildDate));

        //enable the hyperlink clicks
        TextView[] textViews = new TextView[]{textViewGitHubLink,textViewLicenseLink,textJapaneseContributers,textEsperantoContributers,
                textPolishContributers,textFinnishContributers,textTurkishContributers,textFrenchContributers,textFurtherContributers1,
                textFurtherContributers2};

        for (TextView textView : textViews){
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }



        return view;
    }
}
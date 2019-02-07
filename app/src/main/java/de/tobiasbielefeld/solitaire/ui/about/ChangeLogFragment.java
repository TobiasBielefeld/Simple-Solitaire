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
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.createBulletParagraph;

/**
 * Shows the changelog, each version has an own string in strings-changelog.xml. This fragment
 * uses the version name to generate each entry
 */

public class ChangeLogFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_tab3, container, false);

        LinearLayout layoutContainer = view.findViewById(R.id.changelog_container);

        String[] titles = new String[]{"3.13", "3.12.1", "3.12", "3.11.3", "3.11.2", "3.11.1", "3.11", "3.10.2", "3.10.1", "3.10", "3.9.2", "3.9.1", "3.9",
                "3.8.6", "3.8.5", "3.8.4", "3.8.3", "3.8.2", "3.8.1", "3.8", "3.7.2", "3.7.1", "3.7",
                "3.6.2", "3.6.1", "3.6", "3.5", "3.4", "3.3.5", "3.3.4", "3.3.3", "3.3.2", "3.3.1",
                "3.3", "3.2", "3.1.5", "3.1.4", "3.1.3", "3.1.2", "3.1.1", "3.1", "3.0.1", "3.0",
                "2.0.2", "2.0.1", "2.0", "1.4", "1.3", "1.2", "1.1", "1.0"};

        for (int i = 0; i < titles.length; i++) {
            CardView card = (CardView) LayoutInflater.from(getContext()).inflate(R.layout.changelog_card_view, null);
            TextView title = card.findViewById(R.id.changelog_card_view_title);
            TextView description = card.findViewById(R.id.changelog_card_view_text);

            title.setText(titles[i]);
            description.setText(createText(titles.length - i));

            layoutContainer.addView(card);
        }

        return view;
    }


    private CharSequence createText(int pos) {

        int MAX_LINES_PER_VERSION = 10;
        List<CharSequence> stringList = new ArrayList<>(MAX_LINES_PER_VERSION);

        //load the lines from the changelog separately
        for (int i = 1; i <= MAX_LINES_PER_VERSION; i++) {

            int ID = getResources().getIdentifier(
                    "changelog_" + Integer.toString(pos) + "_" + Integer.toString(i),
                    "string", getActivity().getPackageName());

            if (ID != 0) {
                stringList.add(getString(ID));
            } else {
                break;
            }
        }

        //convert to array
        CharSequence[] strings = new CharSequence[stringList.size()];

        for (int i = 0; i < strings.length; i++) {
            strings[i] = stringList.get(i);
        }

        return TextUtils.concat(createBulletParagraph(stringList.toArray(new CharSequence[0])));
    }
}
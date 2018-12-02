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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Just show a textView for the menu page.
 */

public class ManualStatistics extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manual_statistics, container, false);

        TextView textView = view.findViewById(R.id.manual_statistics_list);

        //get the strings for the enumerated text part (with bullet characters)
        CharSequence strings[] = new CharSequence[]{
                getText(R.string.manual_statistics_part_2), getText(R.string.manual_statistics_part_3),
                getText(R.string.manual_statistics_part_4), getText(R.string.manual_statistics_part_5),
                getText(R.string.manual_statistics_part_6)
        };

        //set up the textView
        textView.setText(createBulletParagraph(strings));

        return view;
    }
}

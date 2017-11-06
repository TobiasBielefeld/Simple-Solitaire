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

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.tobiasbielefeld.solitaire.R;

/**
 * Shows the changelog, each version has an own string in strings-changelog.xml. This fragment
 * uses the version name to generate each entry
 */

public class ChangeLogFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_tab3, container, false);

        LinearLayout layoutContainer = (LinearLayout) view.findViewById(R.id.changelog_container);

        String[] titles = new String[]{"3.9.1", "3.9","3.8.6", "3.8.5", "3.8.4", "3.8.3", "3.8.2", "3.8.1",
                "3.8", "3.7.2", "3.7.1", "3.7", "3.6.2", "3.6.1", "3.6", "3.5", "3.4", "3.3.5",
                "3.3.4", "3.3.3", "3.3.2", "3.3.1", "3.3", "3.2", "3.1.5", "3.1.4", "3.1.3", "3.1.2",
                "3.1.1", "3.1", "3.0.1", "3.0", "2.0.2", "2.0.1", "2.0", "1.4", "1.3", "1.2", "1.1",
                "1.0"};

        for (int i=0;i<titles.length;i++) {
            CardView card = createCard();
            layoutContainer.addView(card);

            LinearLayout linearLayout = (LinearLayout) card.getChildAt(0);

            linearLayout.addView(createTitle(titles[i]));
            linearLayout.addView(createSeparator());
            linearLayout.addView(createText(titles.length -i));
        }

        return view;
    }

    private View createSeparator(){
        View view = new View(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        view.setLayoutParams(params);
        view.setBackgroundColor(getResources().getColor(R.color.colorDrawerSelected));

        return view;
    }

    private TextView createText(int pos){
        TextView view = new TextView(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int padding = (int) (getResources().getDimension(R.dimen.changelog_text_padding));
        view.setPadding(padding,padding,padding,padding);
        view.setLayoutParams(params);
        view.setText(getString(getResources().getIdentifier("changelog_" + Integer.toString(pos), "string", getActivity().getPackageName())));

        return view;
    }

    private TextView createTitle(String text){
        TextView view = new TextView(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int padding = (int) (getResources().getDimension(R.dimen.changelog_title_padding));
        view.setPadding(padding,padding,padding,padding);
        view.setLayoutParams(params);
        view.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
        view.setTypeface(view.getTypeface(), Typeface.BOLD);
        view.setText("Ver. " + text);

        return view;
    }

    private CardView createCard(){
        CardView card = new CardView(getContext());

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        float radius = getResources().getDimension(R.dimen.changelog_card_corner_radius);
        card.setLayoutParams(params);
        card.setCardBackgroundColor(getResources().getColor(R.color.white));
        card.setRadius(radius);
        card.setUseCompatPadding(true);

        LinearLayout layout = new LinearLayout(getContext());
        ViewGroup.LayoutParams params2 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params2);

        int padding = (int) (getResources().getDimension(R.dimen.changelog_linear_layout_Padding));
        layout.setPadding(padding,padding,padding,padding);
        layout.setOrientation(LinearLayout.VERTICAL);

        card.addView(layout);

        return card;
    }
}
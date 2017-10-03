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

import de.tobiasbielefeld.solitaire.R;

/**
 * Shows the GPL License, which is simply loaded from a webView. The About activity disables recreation
 * after orientation change, so don't need to handle that.
 */

public class LicenseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_tab2, container, false);


        TextView textMaterialIconsLicense = (TextView) view.findViewById(R.id.about_license_material_icons);
        TextView textCardThemesLicense = (TextView) view.findViewById(R.id.about_license_cards_theme);
        TextView textCardThemesLicenseUsage = (TextView) view.findViewById(R.id.about_license_card_themes_usage);
        TextView textPokerLicense = (TextView) view.findViewById(R.id.about_license_poker_theme);
        TextView textPokerLicenseUsage = (TextView) view.findViewById(R.id.about_license_poker_theme_usage);
        TextView textCustomColorPickerLicense = (TextView) view.findViewById(R.id.about_license_custom_color_picker);
        TextView textSoundsLicense = (TextView) view.findViewById(R.id.about_license_sounds);
        TextView textSoundsLicenseUsage = (TextView) view.findViewById(R.id.about_license_sounds_usage);
        TextView textSlidingTabsLicense = (TextView) view.findViewById(R.id.about_license_sliding_tabs);

        TextView[] textViews = new TextView[]{textMaterialIconsLicense, textCardThemesLicense, textCardThemesLicenseUsage,
                textPokerLicense,textPokerLicenseUsage,textCustomColorPickerLicense,textSoundsLicense,textSoundsLicenseUsage,
                textSlidingTabsLicense};

        for (TextView textView : textViews){
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }



        return view;
    }
}
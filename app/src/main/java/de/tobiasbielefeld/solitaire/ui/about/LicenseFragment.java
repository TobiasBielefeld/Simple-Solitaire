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
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.tobiasbielefeld.solitaire.R;


/**
 * Shows the licenses of all third party components
 */

public class LicenseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_tab2, container, false);

        TextView textMaterialIconsLicense = view.findViewById(R.id.about_license_material_icons);
        TextView textMaterialIconsLicense2 = view.findViewById(R.id.about_license_material_icons_2);
        TextView textMaterialIconsUsage2 = view.findViewById(R.id.about_license_material_icons_2_usage);
        TextView textCardThemesLicense = view.findViewById(R.id.about_license_cards_theme);
        TextView textCardThemesLicenseUsage = view.findViewById(R.id.about_license_card_themes_usage);
        TextView textPokerLicense = view.findViewById(R.id.about_license_poker_theme);
        TextView textPokerLicenseUsage = view.findViewById(R.id.about_license_poker_theme_usage);
        TextView textParisLicenseUsage = view.findViewById(R.id.about_license_paris_theme_usage);
        TextView textCustomColorPickerLicense = view.findViewById(R.id.about_license_custom_color_picker);
        TextView textSoundsLicense = view.findViewById(R.id.about_license_sounds);
        TextView textSoundsLicenseUsage = view.findViewById(R.id.about_license_sounds_usage);
        TextView textSlidingTabsLicense = view.findViewById(R.id.about_license_sliding_tabs);
        TextView textAndroidSupportLicense = view.findViewById(R.id.about_license_android_support_libraries);

        TextView[] textViews = new TextView[]{textMaterialIconsLicense, textMaterialIconsLicense2, textMaterialIconsUsage2,
                textCardThemesLicense, textCardThemesLicenseUsage, textPokerLicense, textPokerLicenseUsage,
                textCustomColorPickerLicense, textSoundsLicense, textSoundsLicenseUsage,
                textSlidingTabsLicense, textAndroidSupportLicense, textParisLicenseUsage};

        //explicitly set the strings here, otherwise the links in them wouldn't show properly
        textCardThemesLicenseUsage.setText(Html.fromHtml(getString(R.string.about_card_themes_usage)));
        textPokerLicenseUsage.setText(Html.fromHtml(getString(R.string.about_poker_themes_usage)));
        textParisLicenseUsage.setText(Html.fromHtml(getString(R.string.about_paris_themes_usage)));
        textSoundsLicenseUsage.setText(Html.fromHtml(getString(R.string.about_sounds_usage)));
        textMaterialIconsUsage2.setText(Html.fromHtml(getString(R.string.about_material_icons_2_usage)));

        for (TextView textView : textViews) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        return view;
    }
}
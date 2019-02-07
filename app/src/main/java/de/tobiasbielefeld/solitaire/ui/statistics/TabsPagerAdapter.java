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

package de.tobiasbielefeld.solitaire.ui.statistics;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.tobiasbielefeld.solitaire.R;

/**
 * Adapter for the tabs
 */

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES;

    TabsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        TITLES = new String[]{context.getString(R.string.settings_other),
                context.getString(R.string.statistics_high_scores), context.getString(R.string.statistics_recent_scores)};
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new StatisticsFragment();
            case 1:
                return new HighScoresFragment();
            case 2:
                return new RecentScoresFragment();
        }

        return null;
    }

}
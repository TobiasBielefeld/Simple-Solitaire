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

package de.tobiasbielefeld.solitaire.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.Locale;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.Card;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/*
 * Settings activity created from "New Settings Activity" Tool from Android Studio.
 * But i removed the multi pane fragment stuff because i had problems with it and i think it's
 * not necessary for my 6 settings. So i add the preferences in onCreate and use a
 * onSharedPreferenceChanged Listener for updating the game.
 *
 * I use 2 custom dialogs for the card drawables and background drawables, therefore they have
 * custom preference dialog classes with onClicks and the Summary is updated here
 */

@SuppressWarnings("deprecation")
public class Settings extends AppCompatPreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener{

    private Preference preferenceCards, preferenceCardsBackground;                                  //my custom preferences
    private Preference preferenceAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ViewGroup) getListView().getParent()).setPadding(0, 0, 0, 0);                             //remove huge padding in landscape
        addPreferencesFromResource(R.xml.pref_settings);
        showOrHideStatusBar();

         /* set a nice back arrow in the actionBar */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)                                                                      //set a nice back arrow in the actionBar
            actionBar.setDisplayHomeAsUpEnabled(true);

        /* initialize the custom preferences */
        preferenceCards = findPreference(getString(R.string.pref_key_cards));
        preferenceCardsBackground = findPreference(getString(R.string.pref_key_cards_background));
        preferenceAbout = findPreference(getString(R.string.pref_key_about));
        preferenceAbout.setOnPreferenceClickListener(this);

        /* set default values for summary of custom preferences*/
        setPreferenceCardsSummary();
        setPreferenceCardsBackgroundSummary();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {                                           //only menu item is the back button in the action bar
        finish();                                                                                   //so finish this activity
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);// Set up a listener whenever a key changes
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);//unregister the listener
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "pref_key_background_color":
                getIntent().putExtra(getString(R.string.pref_key_background_color), 1);
                break;
            case Card.CARD_DRAWABLES:
                Card.updateCardDrawableChoice();
                setPreferenceCardsSummary();
                break;
            case Card.CARD_BACKGROUND:
                Card.updateCardBackgroundChoice();
                setPreferenceCardsBackgroundSummary();
                break;
            case "pref_key_hide_status_bar":
                showOrHideStatusBar();
                getIntent().putExtra(getString(R.string.pref_key_hide_status_bar), 1);
                break;
            case "pref_key_left_handed_mode":
                for (int i = 0; i <= 12; i++) {                                                     //update the card and stack positions
                    stacks[i].mView.setX(main_activity.layoutGame.getWidth() - stacks[i].mView.getX() - Card.sWidth);

                    for (int j = 0; j < stacks[i].getSize(); j++) {
                        Card card = stacks[i].getCard(j);
                        card.setLocationWithoutMovement(main_activity.layoutGame.getWidth() -       //new position is layout width - current position - card width.
                                card.mView.getX() - Card.sWidth, card.mView.getY());
                    }
                }
                break;
        }

        setResult(RESULT_OK, getIntent());
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        startActivity(new Intent(getApplicationContext(), About.class));
        return false;
    }

    private void setPreferenceCardsBackgroundSummary() {
        preferenceCardsBackground.setSummary(String.format(Locale.getDefault(), "%s %s",
                getString(R.string.settings_background), savedData.getInt(Card.CARD_BACKGROUND, 1)));
    }

    private void setPreferenceCardsSummary() {
        String text = "";

        switch (savedData.getInt(Card.CARD_DRAWABLES, 1)) {
            case 1:
                text = getString(R.string.settings_classic);
                break;
            case 2:
                text = getString(R.string.settings_abstract);
                break;
            case 3:
                text = getString(R.string.settings_simple);
                break;
            case 4:
                text = getString(R.string.settings_modern);
                break;
            case 5:
                text = getString(R.string.settings_dark);
                break;
        }

        preferenceCards.setSummary(text);
    }

    private void showOrHideStatusBar() {
        if (savedData.getBoolean(getString(R.string.pref_key_hide_status_bar), false))
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}

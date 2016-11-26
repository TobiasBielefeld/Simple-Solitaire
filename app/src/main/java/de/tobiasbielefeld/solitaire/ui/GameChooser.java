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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.*;

 /*
  * This is the main menu with the buttons to load a game
  */

public class GameChooser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
         * initialize stuff and if the corresponding setting is set to true, load the last played game
         */

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_chooser);
        savedData = PreferenceManager.getDefaultSharedPreferences(this);

        String game = MENU;

        if (!getSharedBoolean(getString(R.string.pref_key_start_menu),false)) {
            switch (getSharedString("pref_key_current_game", MENU)) {
                case KLONDIKE:
                    game = KLONDIKE;
                    break;
                case FREECELL:
                    game = FREECELL;
                    break;
                case YUKON:
                    game = YUKON;
                    break;
                case SPIDER:
                    game = SPIDER;
                    break;
            }

            if (!game.equals(MENU)) {
                savedGameData = getSharedPreferences(game, MODE_PRIVATE);
                Intent intent = new Intent(getApplicationContext(), GameManager.class);
                intent.putExtra("game", game);
                startActivity(intent);
            }
        }
    }


    public void onClick(View view) {
        /*
         *  load a game when clicking a button
         */

        //avoid loading two games at once when pressing two buttons at once
        if (!getSharedString("pref_key_current_game",MENU).equals(MENU))
            return;

        String game;

        switch (view.getId()) {
            case R.id.buttonStartKlondike:
            default:
                game = KLONDIKE;
                break;
            case R.id.buttonStartFreecell:
                game = FREECELL;
                break;
            case R.id.buttonStartYukon:
                game = YUKON;
                break;
            case R.id.buttonStartSpider:
                game = SPIDER;
                break;
        }

        //prepare the sharedPreferences and start the GameManager with the game name as a intent
        savedGameData = getSharedPreferences(game, MODE_PRIVATE);
        putSharedString("pref_key_current_game",game);
        Intent intent = new Intent(getApplicationContext(), GameManager.class);
        intent.putExtra("game", game);
        startActivity(intent);
    }

    public void onClick2(View view) {
        //for the fab button
        startActivity(new Intent(getApplicationContext(),Settings.class));
    }

    public void onResume(){
        super.onResume();

        putSharedString("pref_key_current_game",MENU);
        showOrHideStatusBar(this);
        setOrientation(this);
    }
}

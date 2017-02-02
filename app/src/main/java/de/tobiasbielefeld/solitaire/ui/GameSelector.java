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

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomAppCompatActivity;
import de.tobiasbielefeld.solitaire.helper.LocaleChanger;
import de.tobiasbielefeld.solitaire.ui.manual.Manual;
import de.tobiasbielefeld.solitaire.ui.settings.Settings;

import static de.tobiasbielefeld.solitaire.SharedData.*;

 /*
  * This is the main menu with the buttons to load a game
  */

public class GameSelector extends CustomAppCompatActivity {

    ArrayList<LinearLayout> gameLayouts;
    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
         * initialize stuff and if the corresponding setting is set to true, load the last played game
         */
        super.onCreate(savedInstanceState);
        savedSharedData = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_game_chooser_main);

        tableLayout = (TableLayout) findViewById(R.id.tableLayoutGameChooser);
        gameLayouts = lg.loadLayouts(this);

        loadGameList();

        if (!getSharedBoolean(getString(R.string.pref_key_start_menu),false)) {
            int savedGame;

            try {
                savedGame = getSharedInt("pref_key_current_game", 0);
            } catch (Exception e){
                savedSharedData.edit().remove("pref_key_current_game").apply();
                savedGame=0;
            }

            if (savedGame!=0) {
                Intent intent = new Intent(getApplicationContext(), GameManager.class);
                intent.putExtra("game", savedGame);
                startActivityForResult(intent,0);
            }
        }
        else {
            putSharedInt("pref_key_current_game",0);
        }
    }

    private void loadGameList(){
        ArrayList<Integer> result;

        result = getSharedIntList("pref_key_menu_games");

        TableRow row = new TableRow(this);
        int counter = 0;
        int columns;

        if  (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            columns = Integer.parseInt(getSharedString(MENU_COLUMNS_LANDSCAPE,"5"));
        else
            columns = Integer.parseInt(getSharedString(MENU_COLUMNS_PORTRAIT,"3"));

        //clear the complete layout first
        tableLayout.removeAllViewsInLayout();

        for (LinearLayout gameLayout : gameLayouts) {
            TableRow parent = (TableRow) gameLayout.getParent();

            if (parent!=null)
                parent.removeView(gameLayout);
        }

        if (result.size()==12){ //add canfield to list for older version of game
            result.add(1,1);
        }

        //add the game buttons
        for (int i=0;i<gameLayouts.size();i++) {

            if (counter % columns == 0) {
                row = new TableRow(this);
                tableLayout.addView(row);
            }

            if (result.size()==0 || result.size()<(i+1) || result.get(i)==1){
                gameLayouts.get(i).setVisibility(View.VISIBLE);
                row.addView(gameLayouts.get(i));
                counter++;
            }
            else {
                gameLayouts.get(i).setVisibility(View.GONE);
            }
        }

        //add some dummies to the last row, if necessary
        while (row.getChildCount()<columns){
            FrameLayout dummy = new FrameLayout(this);
            dummy.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
            row.addView(dummy);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if the player returns from a game to the main menu, save it.
        putSharedInt("pref_key_current_game",0);
    }

    public void onClick(View view) {
        /*
         *  load a game when clicking a button
         */
        //avoid loading two games at once when pressing two buttons at once
        if (getSharedInt("pref_key_current_game",0)!=0)
            return;

        //prepare the sharedPreferences and start the GameManager with the game name as a intent
        putSharedInt("pref_key_current_game",view.getId());
        Intent intent = new Intent(getApplicationContext(), GameManager.class);
        intent.putExtra("game", view.getId());
        startActivityForResult(intent,0);//*/
    }

    public void onClick2(View view) {
        if (view.getId()==R.id.buttonStartSettings)
            startActivity(new Intent(getApplicationContext(),Settings.class));
        else if (view.getId()==R.id.buttonStartManual)
            startActivity(new Intent(getApplicationContext(),Manual.class));
    }

    public void onResume(){
        super.onResume();
        loadGameList();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleChanger.onAttach(base));
    }
}

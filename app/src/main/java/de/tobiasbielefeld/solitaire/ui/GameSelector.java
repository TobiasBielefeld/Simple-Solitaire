package de.tobiasbielefeld.solitaire.ui;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomAppCompatActivity;
import de.tobiasbielefeld.solitaire.ui.about.AboutActivity;
import de.tobiasbielefeld.solitaire.ui.manual.Manual;
import de.tobiasbielefeld.solitaire.ui.settings.Settings;

import static de.tobiasbielefeld.solitaire.SharedData.*;

public class GameSelector extends CustomAppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnTouchListener{

    ArrayList<LinearLayout> gameLayouts;
    TableLayout tableLayout;
    View animatingButton;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_selector);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        findViewById(R.id.scrollViewGameChooser).setOnTouchListener(this);

        tableLayout = (TableLayout) findViewById(R.id.tableLayoutGameChooser);
        gameLayouts = lg.loadLayouts(this);

        loadGameList();

        if (!getSharedBoolean(getString(R.string.pref_key_start_menu), false)) {
            int savedGame;

            try {
                savedGame = getSharedInt(PREF_KEY_CURRENT_GAME, DEFAULT_CURRENT_GAME);
            } catch (Exception e) { //old version of saving the game
                savedSharedData.edit().remove(PREF_KEY_CURRENT_GAME).apply();
                savedGame = 0;
            }

            if (savedGame != 0) {
                Intent intent = new Intent(getApplicationContext(), GameManager.class);
                intent.putExtra(GAME, savedGame);
                startActivityForResult(intent, 0);
            }
        } else {
            putSharedInt(PREF_KEY_CURRENT_GAME, DEFAULT_CURRENT_GAME);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.item_settings:
                startActivity(new Intent(getApplicationContext(), Settings.class));
                break;
            case R.id.item_manual:
                startActivity(new Intent(getApplicationContext(), Manual.class));
                break;
            case R.id.item_about:
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                break;
            case R.id.item_close:
                finish();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * load the game list of the menu. First clear everything and then add each game, if they aren't
     * set to be hidden. Add the end, add some dummies, so the last row doesn't have less entries.
     */
    private void loadGameList() {
        ArrayList<Integer> result;

        result = getSharedIntList(PREF_KEY_MENU_GAMES);

        TableRow row = new TableRow(this);
        int counter = 0;
        int columns;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            columns = Integer.parseInt(getSharedString(MENU_COLUMNS_LANDSCAPE, DEFAULT_MENU_COLUMNS_LANDSCAPE));
        else
            columns = Integer.parseInt(getSharedString(MENU_COLUMNS_PORTRAIT, DEFAULT_MENU_COLUMNS_PORTRAIT));

        //clear the complete layout first
        tableLayout.removeAllViewsInLayout();

        for (LinearLayout gameLayout : gameLayouts) {
            TableRow parent = (TableRow) gameLayout.getParent();

            if (parent != null)
                parent.removeView(gameLayout);
        }

        //add games to list for older versions of the app
        if (result.size() == 12) { //new canfield game
            result.add(1, 1);
        }
        if (result.size() == 13) { //new grand fathers clock game
            result.add(5, 1);
        }

        //add the game buttons
        for (int i = 0; i < gameLayouts.size(); i++) {

            if (counter % columns == 0) {
                row = new TableRow(this);
                tableLayout.addView(row);
            }

            if (result.size() == 0 || result.size() < (i + 1) || result.get(i) == 1) {
                gameLayouts.get(i).setVisibility(View.VISIBLE);
                ImageView imageView = (ImageView) gameLayouts.get(i).getChildAt(0);
                imageView.setImageBitmap(bitmaps.getMenu(i % 6, i / 6));
                imageView.setOnTouchListener(this);
                row.addView(gameLayouts.get(i));
                counter++;
            } else {
                gameLayouts.get(i).setVisibility(View.GONE);
            }
        }

        //add some dummies to the last row, if necessary
        while (row.getChildCount() < columns) {
            FrameLayout dummy = new FrameLayout(this);
            dummy.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
            row.addView(dummy);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if the player returns from a game to the main menu, save it.
        putSharedInt(PREF_KEY_CURRENT_GAME, DEFAULT_CURRENT_GAME);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadGameList();

        if (navigationView!=null) {
            navigationView.setCheckedItem(R.id.item_close);
        }
    }

    /*
     * Used to make the "button press" animation on the game imageViews. Only start the game if the
     * touch point is still on the imageView and stop the animation when scrolling the scrollView
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (v instanceof ScrollView){

            if (event.getAction() == MotionEvent.ACTION_MOVE && animatingButton!=null) {
               regainButtonSize(animatingButton);

            }

            return false;
        }

        //from here only for the game buttons

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            reduzeButtonSize(v);

        } else if (event.getAction() == MotionEvent.ACTION_UP) {

            regainButtonSize(v);

            float X = event.getX(), Y = event.getY();

            if (X>0 && X < v.getWidth() && Y>0 && Y<v.getHeight()){
                //avoid loading two games at once when pressing two buttons at once
                if (getSharedInt(PREF_KEY_CURRENT_GAME, DEFAULT_CURRENT_GAME) != 0)
                    return true;

                putSharedInt(PREF_KEY_CURRENT_GAME, v.getId());
                Intent intent = new Intent(getApplicationContext(), GameManager.class);
                intent.putExtra(GAME, v.getId());
                startActivityForResult(intent, 0);
            }
        }

        return false;
    }

    /**
     * Shrinks a game button, as a "pressed" animation"
     *
     * @param view The button to shrink
     */
    public void reduzeButtonSize(View view){
        AnimatorSet reducer = (AnimatorSet) AnimatorInflater.loadAnimator(this,R.animator.reduce_size);
        reducer.setTarget(view);
        reducer.start();
        animatingButton = view;
    }

    /**
     * Expands a game button to normal size
     *
     * @param view The view to expand
     */
    public void regainButtonSize(View view){
        AnimatorSet reducer = (AnimatorSet) AnimatorInflater.loadAnimator(this,R.animator.regain_size);
        reducer.setTarget(view);
        reducer.start();
        animatingButton = null;
    }

}

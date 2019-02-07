package de.tobiasbielefeld.solitaire.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomAppCompatActivity;
import de.tobiasbielefeld.solitaire.ui.about.AboutActivity;
import de.tobiasbielefeld.solitaire.ui.manual.Manual;
import de.tobiasbielefeld.solitaire.ui.settings.Settings;

import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.helper.Preferences.*;

public class GameSelector extends CustomAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnTouchListener {

    private TableLayout tableLayout;
    private int menuColumns;
    private ArrayList<Integer> indexes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_selector);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tableLayout = findViewById(R.id.tableLayoutGameChooser);

        if (!prefs.getSavedStartWithMenu()) {
            int savedGame = prefs.getSavedCurrentGame();

            if (savedGame != DEFAULT_CURRENT_GAME) {
                Intent intent = new Intent(getApplicationContext(), GameManager.class);
                intent.putExtra(GAME, savedGame);
                startActivityForResult(intent, 0);
            }
        } else {
            prefs.saveCurrentGame(DEFAULT_CURRENT_GAME);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * load the game list of the menu. First clear everything and then add each game, if they aren't
     * set to be hidden. Add the end, add some dummies, so the last row doesn't have less entries.
     */
    private void loadGameList() {
        ArrayList<Integer> isShownList = lg.getMenuShownList();
        ArrayList<Integer> orderedList = lg.getOrderedGameList();

        TableRow row = new TableRow(this);
        int counter = 0;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            menuColumns = prefs.getSavedMenuColumnsLandscape();
        } else {
            menuColumns = prefs.getSavedMenuColumnsPortrait();
        }

        //clear the complete layout first
        tableLayout.removeAllViewsInLayout();
        indexes.clear();

        int padding = (int) (getResources().getDimension(R.dimen.game_selector_images_padding));
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
        params.weight = 1;

        //add the game buttons
        for (int i = 0; i < lg.getGameCount(); i++) {

            int index = orderedList.indexOf(i);

            if (isShownList.get(index) == 1) {
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(params);
                imageView.setAdjustViewBounds(true);
                imageView.setLongClickable(true);
                imageView.setPadding(padding, padding, padding, padding);

                if (counter % menuColumns == 0) {
                    row = new TableRow(this);
                    tableLayout.addView(row);
                }

                imageView.setImageBitmap(bitmaps.getMenu(index));
                imageView.setOnTouchListener(this);
                indexes.add(i);
                row.addView(imageView);
                counter++;
            }
        }

        //add some dummies to the last row, if necessary
        while (row.getChildCount() < menuColumns) {
            FrameLayout dummy = new FrameLayout(this);
            dummy.setLayoutParams(params);
            row.addView(dummy);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if the player returns from a game to the main menu, save it.
        prefs.saveCurrentGame(DEFAULT_CURRENT_GAME);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadGameList();
    }

    /*
     * Used to make the "button press" animation on the game imageViews. Only start the game if the
     * touch point is still on the imageView and stop the animation when scrolling the scrollView
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //shrink button
            changeButtonSize(v, 0.9f);

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            //regain button size
            changeButtonSize(v, 1.0f);

            float X = event.getX(), Y = event.getY();

            if (X > 0 && X < v.getWidth() && Y > 0 && Y < v.getHeight()) {
                startGame(v);
            }
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            //regain button size
            changeButtonSize(v, 1.0f);
        }

        return false;
    }

    /**
     * changes the button size, according to the second parameter.
     * Used to shrink/expand the menu buttons.
     *
     * @param view  The view to apply the changes
     * @param scale The scale to apply
     */
    private void changeButtonSize(View view, float scale) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "scaleX", scale);
        animX.setDuration(100);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "scaleY", scale);
        animY.setDuration(100);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY);

        if (scale == 1.0) { //expand button with a little delay
            animSetXY.setStartDelay(getResources().getInteger(R.integer.expand_button_anim_delay_ms));
        }

        animSetXY.start();
    }

    /**
     * Starts the clicked game. This uses the total index position of the clicked view to get the
     * game.
     *
     * @param view The clicked view.
     */
    private void startGame(View view) {
        TableRow row = (TableRow) view.getParent();
        TableLayout table = (TableLayout) row.getParent();
        ArrayList<Integer> orderedList = lg.getOrderedGameList();
        int index = indexes.get(table.indexOfChild(row) * menuColumns + row.indexOfChild(view));
        index = orderedList.indexOf(index);

        //avoid loading two games at once when pressing two buttons at once
        if (prefs.getSavedCurrentGame() != DEFAULT_CURRENT_GAME) {
            return;
        }

        prefs.saveCurrentGame(index);
        Intent intent = new Intent(getApplicationContext(), GameManager.class);
        intent.putExtra(GAME, index);
        startActivityForResult(intent, 0);
    }

}

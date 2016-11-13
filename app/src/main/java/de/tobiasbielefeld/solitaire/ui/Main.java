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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.Stack;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/*
 * Main activity initializes the cards and stacks positions and some of my variables.
 * touch events and menu clicks are also handled here
 */

public class Main extends AppCompatActivity implements View.OnTouchListener {

    private long backPressedTime;

    private boolean has_loaded = false;                                                             //used to call save() in onPause() only if load() has been called before
    public Button buttonAutoComplete;
    public TextView mainTextViewTime, mainTextViewScore;
    public RelativeLayout layoutGame;                                                               //contains the game stacks and cards

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                                                         //initialize stuff
        setContentView(R.layout.activity_main);

        mainActivity = this;
        layoutGame = (RelativeLayout) findViewById(R.id.mainRelativeLayoutGame);                    //set layout
        mainTextViewTime = (TextView) findViewById(R.id.mainTextViewTime);
        mainTextViewScore = (TextView) findViewById(R.id.mainTextViewScore);
        buttonAutoComplete = (Button) findViewById(R.id.buttonMainAutoComplete);
        savedData = PreferenceManager.getDefaultSharedPreferences(this);                            //get the shared pref
        editor = savedData.edit();                                                                  //and an editor, otherwise using savedData.edit() would cause problems when loading data
        editor.apply();

        for (int i = 0; i < cards.length; i++)                                                      //initialize objects
            cards[i] = new Card(i);
        for (int i = 0; i < stacks.length; i++)
            stacks[i] = new Stack(i);

        for (int i = 0; i < cards.length; i++) {                                                    //create the cards
            ImageView view = new ImageView(this);                                                   //with a new image view
            view.setId(i);                                                                          //set an id so the id of the view is the same as the card id (needed so when you touch the image view with id=5 is also the card with id=5 and so on
            view.setOnTouchListener(this);                                                          //listener to handle touch events
            cards[i].mView = view;                                                                  //and link the image view to the card
            layoutGame.addView(cards[i].mView);                                                     //and add the card to the layput
        }

        for (Stack stack : stacks) {                                                                //create stacks the same way
            ImageView view = new ImageView(this);

            if (stack.getID()>6 && stack.getID()<=10)                                               //set the background according to the id
                view.setBackgroundResource(R.drawable.background_stack_foundations);
            else if (stack.getID()==12)
                view.setBackgroundResource(R.drawable.background_stack_stock);
            else
                view.setBackgroundResource(R.drawable.background_stack);

            stack.mView = view;
            layoutGame.addView(stack.mView);
        }

        stacks[12].mView.setOnTouchListener(this);                                                  //but only stack 12 (stock) needs an touch listener (when there are no cards on it)

        layoutGame.post(new Runnable() {                                                           //post a runnable to set the dimensions of cards and stacks when the layout has loaded
            @Override
            public void run() {                                                                      //post, because i need the dimensions of layoutGame to set the cards and stacks
                setStackCoordinates();                                                              //set the dimensions
                game.load();                                                                        //afterwards load the game
                has_loaded = true;                                                                  //stuff was loaded, so set it to true
            }
        });

        loadBackgroundColor();
        showOrHideStatusBar();
        setOrientation();
    }

    @Override
    public void onPause() {                                                                         //stop the timer, save the current time
        super.onPause();

        if (has_loaded) {                                                                           //only save when stuff has been loaded before
            timer.save();
            game.save();
        }

        editor.apply();                                                                             //VERY IMPORTANT! apply changes!!!
    }

    @Override
    public void onResume() {                                                                        //resume the timer, set start time to handle the time while paused and start handler
        super.onResume();
        timer.load();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {                                         //handle back button presses
        long BACK_PRESSED_TIME_DELTA = 2000;

        if (keyCode == KeyEvent.KEYCODE_BACK                                                        //if it was the back key and this feature is still activated in the Settings,
                && savedData.getBoolean(getString(R.string.pref_key_confirm_closing_game), true)
                && (System.currentTimeMillis() - backPressedTime > BACK_PRESSED_TIME_DELTA)) {      //and the delta to the last time pressed button is over the max time

            showToast(getString(R.string.game_press_again));                                        //show toast to press again
            backPressedTime = System.currentTimeMillis();                                           //and save the time as pressed
            return true;                                                                            //don't exit the game
        }

        return super.onKeyDown(keyCode, event);                                                     //if the time delta is smaller than the max time, close game
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (data.getIntExtra(getString(R.string.pref_key_background_color), 0) > 0) {
                loadBackgroundColor();
            }

            if (data.getIntExtra(getString(R.string.pref_key_hide_status_bar), 0) > 0) {
                showOrHideStatusBar();
            }

            if (data.getIntExtra(getString(R.string.pref_key_orientation), 0) > 0) {
                setOrientation();
            }
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (stopConditions())                                                                       //if something important happens don't accept input
            return true;

        if (event.getPointerId(0) != 0) {                                                           //if there is another touch point
            if (movingCards.hasCards())                                                             //return the moving cards (otherwise the cards stays on the current touch point and aren't return to old pos
                movingCards.returnToPos();

            return true;                                                                            //and ignore the touch point
        }

        float X = event.getX() + v.getX(), Y = event.getY() + v.getY();                             //get position of touch

            /* ACTION_DOWN */
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (stacks[12].isOnLocation(X, Y)) {                                                    //if the stock was touched

                if (stacks[12].getSize() > 0)                                                       //if it has cards
                    moveToStack(stacks[12].getTopCard(), stacks[11]);                               //move the card to the other stock stack
                else {                                                                              //else move cards to it
                    Stack.noCards();
                    return true;
                }
            }
            else if (cards[v.getId()].isUp())                                                       //else test if another card was touched
                movingCards.add(cards[v.getId()]);                                                  //and add it to movement
        }
            /* ACTION_MOVE */
        else if (event.getAction() == MotionEvent.ACTION_MOVE && movingCards.hasCards()) {          //move cards if there are any
            movingCards.move(X, Y);
        }
            /* ACTION_UP */
        else if (event.getAction() == MotionEvent.ACTION_UP && movingCards.hasCards()) {            //place cards if there are any
            for (Stack stack : stacks) {                                                            //goes through the stacks
                if (stack.isOnLocation(X, Y) && movingCards.first().getStack() != stack
                        && movingCards.first().test(stack)) {                                       //if the cards are moved to a new stack
                    movingCards.moveToDestination(stack);                                           //place them there
                    return true;                                                                    //and return
                }
            }

            movingCards.returnToPos();                                                              //if they aren't placed, return them to their old places
        }
        return true;
    }

    public void menuClick(View view) {                                                              //handles button clicks on the menu
        if (stopConditions())                                                                       //if something important happens don't accept input
            return;

        if (movingCards.hasCards())                                                                 //also return moving cards, to prevent bugs
            movingCards.returnToPos();

        switch (view.getId()) {
            case R.id.mainButtonScores:
                startActivity(new Intent(getApplicationContext(), HighScores.class));               //open high scores activity
                break;
            case R.id.mainButtonUndo:
                recordList.undo();                                                                  //undo last movement
                break;
            case R.id.mainButtonHint:
                hint.show_hint();                                                                   //show a hint
                break;
            case R.id.mainButtonRestart:                                                            //show restart dialog
                DialogFragment newFragment = new RestartDialog();
                newFragment.show(getSupportFragmentManager(), "restart_dialog");
                break;
            case R.id.mainButtonSettings:                                                           //open Settings activity
                startActivityForResult(new Intent(getApplicationContext(), Settings.class), 0);
                break;
            case R.id.buttonMainAutoComplete:
                autoComplete.start();                                                               //start auto complete
                break;
        }
    }

    private void showOrHideStatusBar() {
        if (savedData.getBoolean(getString(R.string.pref_key_hide_status_bar), false))
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void loadBackgroundColor() {                                                            //load the background color
        RelativeLayout layout_background = (RelativeLayout) findViewById(R.id.mainRelativeLayoutGame); //get the relativeLayout first

        if (layout_background != null) {
            switch (savedData.getString(getString(R.string.pref_key_background_color), "2"))  {     //then choose from the saved color
                case "1":
                    layout_background.setBackgroundResource(R.drawable.background_color_blue);
                    break;
                case "2":
                    layout_background.setBackgroundResource(R.drawable.background_color_green);
                    break;
                case "3":
                    layout_background.setBackgroundResource(R.drawable.background_color_red);
                    break;
                case "4":
                    layout_background.setBackgroundResource(R.drawable.background_color_yellow);
                    break;
                case "5":
                    layout_background.setBackgroundResource(R.drawable.background_color_orange);
                    break;
                case "6":
                    layout_background.setBackgroundResource(R.drawable.background_color_purple);
                    break;
            }
        }
    }

    private boolean stopConditions() {                                                              //returns if the player should't be able to do actions (while animating for example)
        return (autoComplete.isRunning() || animate.cardIsAnimating() || hint.isWorking());
    }

    private void setStackCoordinates() {                                                            //set the coordinates of the stacks
        /* initialize the dimensions */
        boolean is_landscape = getResources().getConfiguration().orientation                        //check if landscape mode
                == Configuration.ORIENTATION_LANDSCAPE;

        Card.sWidth = is_landscape ? layoutGame.getWidth() / 10 : layoutGame.getWidth() / 8;        //set the card sWidth
        Card.sHeight = (int) (Card.sWidth * 1.5);                                                   //and the height

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Card.sWidth, Card.sHeight);//generate new layout params

         /* apply dimensions to cards and stacks */
        for (Card card : cards) card.mView.setLayoutParams(params);                                 //and apply them
        for (Stack stack : stacks) stack.mView.setLayoutParams(params);

         /* order the stacks on the screen */
        int spacing = (layoutGame.getWidth() - 7 * Card.sWidth) / 8;                                //spacing between the stacks

        if (spacing > Card.sWidth / 2)                                                              //but should'nt be greater than the card sWidth /2
            spacing = Card.sWidth / 2;

        int startPos = layoutGame.getWidth() / 2 - Card.sWidth / 2 - 3 * Card.sWidth - 3 * spacing; //get the start position of the stacks, so they are placed symmetric to the center of the layout

        for (int i = 0; i < 6; i++) {                                                               //foundation and stock stacks
            stacks[7 + i].mView.setX(startPos + spacing * i + Card.sWidth * i);
            stacks[7 + i].mView.setY((is_landscape ? Card.sWidth / 4 : Card.sWidth / 2) + 1 );      // + 1 at end because otherwise AndroidStudio shows "you shouldn't pass a resource id", i don't know why
        }

        stacks[11].mView.setX(stacks[11].mView.getX() + Card.sWidth + spacing);                     //place trash 1 position to the right (so there is a bigger spacing between foundation and stock
        stacks[12].mView.setX(stacks[12].mView.getX() + Card.sWidth + spacing);                     //also do this with stock

        for (int i = 0; i < 7; i++) {                                                               //set tableau stacks
            stacks[i].mView.setX(startPos + spacing * i + Card.sWidth * i);
            stacks[i].mView.setY(stacks[7].mView.getY() + Card.sHeight +
                    (is_landscape ? Card.sWidth / 4 : Card.sWidth / 2));
        }

        if (savedData.getBoolean(getString(R.string.pref_key_left_handed_mode), false)) {           //if left handed mode is true, mirror all stacks
            for (int i = 0; i < 13; i++)
                stacks[i].mView.setX(layoutGame.getWidth() - stacks[i].mView.getX() - Card.sWidth);
        }

        /* calculate the spacing for cards on a stack*/
        Stack.sDefaultSpacing = Card.sWidth / 2;                                                    //Stack default spacing of the half card sWidth seems pretty good
        Stack.sSpacingMaxHeight = (int) ((layoutGame.getHeight() - stacks[0].mView.getY()));        //set a max height, so the cards won't go over the screen size
    }

    public static class RestartDialog extends DialogFragment {
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.app_name)
                    .setItems(R.array.restart_menu, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // "which" argument contains index of selected item. 0 is new game, 1 is re-deal
                            game.newGame(which);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //just cancel
                        }
                    });

            return builder.create();
        }
    }

    private void setOrientation() {
        switch (savedData.getString("pref_key_orientation","1")){
            case "1": //follow system settings
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                break;
            case "2": //portrait
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case "3": //landscape
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case "4": //landscape upside down
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
        }
    }
}

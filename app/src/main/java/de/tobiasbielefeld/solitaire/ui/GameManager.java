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
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.CustomAppCompatActivity;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.dialogs.RestartDialog;
import de.tobiasbielefeld.solitaire.handler.LoadGameHandler;
import de.tobiasbielefeld.solitaire.helper.Animate;
import de.tobiasbielefeld.solitaire.helper.AutoComplete;
import de.tobiasbielefeld.solitaire.helper.GameLogic;
import de.tobiasbielefeld.solitaire.helper.Hint;
import de.tobiasbielefeld.solitaire.helper.MovingCards;
import de.tobiasbielefeld.solitaire.helper.RecordList;
import de.tobiasbielefeld.solitaire.helper.Scores;
import de.tobiasbielefeld.solitaire.helper.Timer;
import de.tobiasbielefeld.solitaire.ui.settings.Settings;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/*
 * This is like the main activity, handles game input, controls the timer, loads and saves everything
 */

public class GameManager extends CustomAppCompatActivity implements View.OnTouchListener {

    public boolean hasLoaded = false;                                                               //used to call save() in onPause() only if load() has been called before
    public Button buttonAutoComplete;                                                               //button for auto complete
    public TextView mainTextViewTime, mainTextViewScore, mainTextViewRedeals;                       //textViews for time, scores and re-deals
    public RelativeLayout layoutGame;                                                               //contains the game stacks and cards
    public Toast toast;                                                                             //a delicious toast!
    public static int loadCounter=0;                                                                //used to count how many times the onCreate method is called, so I can avoid loading the game multiple times
    public long doubleTapSpeed = 500;      //time delta between two taps in miliseconds
    public Stack tappedStack = null;
    public long firstTapTime;              //stores the time of first tapping on a card


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_manager);

        // load stuff
        layoutGame = (RelativeLayout) findViewById(R.id.mainRelativeLayoutGame);
        mainTextViewTime = (TextView) findViewById(R.id.mainTextViewTime);
        mainTextViewScore = (TextView) findViewById(R.id.mainTextViewScore);
        mainTextViewRedeals = (TextView) findViewById(R.id.textViewRedeals);
        buttonAutoComplete = (Button) findViewById(R.id.buttonMainAutoComplete);

        //initialize my static helper stuff
        final GameManager gm = this;
        recordList = new RecordList(gm);
        movingCards = new MovingCards();
        hint = new Hint();
        scores = new Scores(gm);
        gameLogic = new GameLogic(gm);
        animate = new Animate(gm);
        autoComplete = new AutoComplete(gm);
        timer = new Timer(gm);
        currentGame = lg.loadClass(this,getIntent().getIntExtra("game",1));
        savedGameData = getSharedPreferences(lg.getSharedPrefName(), MODE_PRIVATE);


        if (savedSharedData==null) {
            savedSharedData = PreferenceManager.getDefaultSharedPreferences(this);
        }

        //initialize cards and stacks
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = new Stack(i);
            stacks[i].view = new ImageView(this);
            stacks[i].view.setBackgroundResource(R.drawable.background_stack);
            layoutGame.addView(stacks[i].view);
        }

        for (int i = 0; i < cards.length; i++) {
            cards[i] = new Card(i);
            cards[i].view = new ImageView(this);
            cards[i].view.setId(i);
            cards[i].view.setOnTouchListener(this);
            layoutGame.addView(cards[i].view);
        }


        currentGame.addOnTouchListener(this);


        scores.output();
        loadCounter++;
        //post, because i need the dimensions of layoutGame to set the cards and stacks
        layoutGame.post(new Runnable() {                                                           //post a runnable to set the dimensions of cards and stacks when the layout has loaded
            @Override
            public void run() {
                boolean isLandscape = getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_LANDSCAPE;

                currentGame.setStacks(layoutGame, isLandscape);

                //if left handed mode is true, mirror all stacks
                if (getSharedBoolean(getString(R.string.pref_key_left_handed_mode), false)) {
                    for (Stack stack : stacks)
                        stack.view.setX(layoutGame.getWidth() - stack.view.getX() - Card.width);

                    if (currentGame.hasArrow()) {
                        for (Stack stack : stacks) {
                            if (stack.hasArrow() > 0) {
                                if (stack.hasArrow() == 1) {
                                    stack.view.setBackgroundResource(R.drawable.arrow_right);
                                } else {
                                    stack.view.setBackgroundResource(R.drawable.arrow_left);
                                }
                            }
                        }
                    }
                }

                //calculate the spacing for cards on a stack
                Stack.defaultSpacing = Card.width / 2;

                //setup how the cards on the stacks will be stacked (offset to the previous card)
                //there are 4 possible directions. By default, the tableau stacks are stacked down
                //all other stacks don't have a visible offset
                //use setDirections() in a game to change that
                if (currentGame.directions == null) {
                    for (int i = 0; i <= currentGame.getLastTableauID(); i++) {
                        stacks[i].setSpacingDirection(1);
                    }
                } else {
                    for (int i = 0; i < currentGame.directions.length; i++) {
                        stacks[i].setSpacingDirection(currentGame.directions[i]);
                    }
                }
                //if there are direction borders set (when cards should'nt overlap another stack)  use it.
                //else set the layout height/widht as maximum
                if (currentGame.directionBorders != null) {
                    for (int i = 0; i < currentGame.directionBorders.length; i++) {
                        if (currentGame.directionBorders[i] != -1)    //-1 means no border
                            stacks[i].setSpacingMax(currentGame.directionBorders[i]);
                        else
                            stacks[i].setSpacingMax(layoutGame);
                    }
                } else {
                    for (Stack stack : stacks) {
                        stack.setSpacingMax(layoutGame);
                    }
                }

                //load the game
                loadCounter--;
                if (loadCounter<1){
                    scores.load();
                    LoadGameHandler loadGameHandler = new LoadGameHandler(gm);
                    loadGameHandler.sendEmptyMessageDelayed(0, 200);
                }
            }
        });

    }



    @Override
    public void onPause() {
        super.onPause();
        //ony save if the game has been loaded before
        if (hasLoaded) {
            timer.save();
            gameLogic.save();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        timer.load();
        loadBackgroundColor();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            DialogFragment restartDialog = new RestartDialog();
            restartDialog.show(getSupportFragmentManager(), "restartDialog");

            return true;
        }

        return super.onKeyDown(keyCode,event);
    }

    public boolean onTouch(View v, MotionEvent event) {
        /*
         * handle input like touching cards and stacks and moving cards around
         */

        //if something important happens don't accept input
        if (stopConditions())
            return true;

        //also don't do anything with a second touch point
        if (event.getPointerId(0) != 0) {
            if (movingCards.hasCards())
                movingCards.returnToPos();

            return true;
        }

        float X = event.getX() + v.getX(), Y = event.getY() + v.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (currentGame.hasMainStack() && currentGame.testIfMainStackTouched(X,Y)) {

                if (currentGame.hasLimitedRedeals() && currentGame.dealFromStack().isEmpty()){
                    if (currentGame.getRemainingNumberOfRedeals()==0)
                        return true;
                    else
                        currentGame.incrementRedealCounter(this);
                }

                currentGame.onMainStackTouch();
            }
            else if (cards[v.getId()].isUp() ){//&& currentGame.addCardToMovementTest(cards[v.getId()])) {

                if (getSharedBoolean("pref_key_double_tap_enable", true)) {
                    if (tappedStack != null && tappedStack == cards[v.getId()].getStack() && System.currentTimeMillis() - firstTapTime < doubleTapSpeed) {

                        CardAndStack cardAndStack = null;

                        if (getSharedBoolean("pref_key_double_tap_all_cards",true) && cards[v.getId()].getStack().getID()<=currentGame.getLastTableauID() ){
                            cardAndStack = currentGame.doubleTap(cards[v.getId()].getStack());
                        } else if (currentGame.addCardToMovementTest(cards[v.getId()])){
                            cardAndStack = currentGame.doubleTap(cards[v.getId()]);
                        }

                        if (cardAndStack != null) {
                            movingCards.add(cardAndStack.getCard(), event.getX(), event.getY());
                            movingCards.moveToDestination(cardAndStack.getStack());
                            tappedStack = null;
                            return true;
                        }


                    } else {
                        tappedStack = cards[v.getId()].getStack();
                        firstTapTime = System.currentTimeMillis();
                    }
                }

                if (currentGame.addCardToMovementTest(cards[v.getId()])){
                    movingCards.add(cards[v.getId()], event.getX(), event.getY());
                }
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE && movingCards.hasCards()) {
            movingCards.move(X, Y);
        }
        else if (event.getAction() == MotionEvent.ACTION_UP && movingCards.hasCards()) {
            for (Stack stack : stacks) {
                if (stack.isOnLocation(X, Y) && movingCards.first().getStack() != stack
                        && movingCards.first().test(stack)) {
                    movingCards.moveToDestination(stack);
                    return true;
                }
            }

            //if they aren't placed, return them to their old places
            movingCards.returnToPos();
        }
        return true;
    }

    public void menuClick(View view) {
        //if something important happens don't accept input

        if (view.getId()==R.id.mainButtonRestart) {
            showRestartDialog();
            return;
        }

        if (stopConditions())
            return;

        //also return moving cards, to prevent bugs
        if (movingCards.hasCards())
            movingCards.returnToPos();

        switch (view.getId()) {
            case R.id.mainButtonScores:
                startActivity(new Intent(getApplicationContext(), HighScores.class));               //open high scores activity
                break;
            case R.id.mainButtonUndo:
                recordList.undo();                                                                  //undo last movement
                break;
            case R.id.mainButtonHint:
                hint.showHint();                                                                    //show a hint
                break;
            //case R.id.mainButtonRestart:                                                            //show restart dialog
            //    showRestartDialog();
            //    break;
            case R.id.mainButtonSettings:                                                           //open Settings activity
                startActivity(new Intent(getApplicationContext(), Settings.class));
                break;
            case R.id.buttonMainAutoComplete:
                autoComplete.start();                                                               //start auto complete
                break;
        }
    }

    private void loadBackgroundColor() {
        RelativeLayout layout_background = (RelativeLayout) findViewById(R.id.mainRelativeLayoutGame);

        if (layout_background != null) {
            switch (getSharedString(getString(R.string.pref_key_background_color), "2"))  {
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

    private boolean stopConditions() {
        /*
         *  returns if the player should't be able to do actions (while animating for example)
         */
        return (autoComplete.isRunning() || animate.cardIsAnimating() || hint.isWorking());
    }

    public void showToast(final String text) {
        final GameManager gm = this;
        runOnUiThread(new Runnable() {
            public void run() {
                if (toast == null)
                    toast = Toast.makeText(gm, text, Toast.LENGTH_SHORT);
                else
                    toast.setText(text);

                toast.show();
            }
        });

    }

    public void updateNumberOfRedeals(){
        mainTextViewRedeals.setText(String.format(Locale.getDefault(),"%d",currentGame.getRemainingNumberOfRedeals()));
    }

    public void showRestartDialog(){
        RestartDialog restartDialog = new RestartDialog();
        restartDialog.show(getSupportFragmentManager(), "restartDialog");
    }
}

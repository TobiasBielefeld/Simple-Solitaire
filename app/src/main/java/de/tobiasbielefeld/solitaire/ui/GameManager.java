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
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    public static int loadCounter = 0;                                                                //used to count how many times the onCreate method is called, so I can avoid loading the game multiple times
    public boolean hasLoaded = false;                                                               //used to call save() in onPause() only if load() has been called before
    public Button buttonAutoComplete;                                                               //button for auto complete
    public TextView mainTextViewTime, mainTextViewScore, mainTextViewRedeals;                       //textViews for time, scores and re-deals
    public RelativeLayout layoutGame;                                                               //contains the game stacks and cards
    public Toast toast;                                                                             //a delicious toast!
    public long doubleTapSpeed = 500;      //time delta between two taps in miliseconds
    public Stack tappedStack = null;
    public long firstTapTime;              //stores the time of first tapping on a card


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        currentGame = lg.loadClass(this, getIntent().getIntExtra(GAME, 1));
        savedGameData = getSharedPreferences(lg.getSharedPrefName(), MODE_PRIVATE);
        Stack.loadBackgrounds();

        updateIcons();
        updateMenuBar();


        //initialize cards and stacks
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = new Stack(i);
            stacks[i].view = new ImageView(this);
            stacks[i].view.setImageBitmap(Stack.backgroundDefault);
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
                if (getSharedBoolean(getString(R.string.pref_key_left_handed_mode), DEFAULT_LEFT_HANDED_MODE)) {
                    for (Stack stack : stacks)
                        stack.view.setX(layoutGame.getWidth() - stack.view.getX() - Card.width);

                    if (currentGame.hasArrow()) {
                        for (Stack stack : stacks) {
                            if (stack.hasArrow() > 0) {
                                if (stack.hasArrow() == 1) {
                                    stack.view.setImageBitmap(Stack.arrowRight);
                                } else {
                                    stack.view.setImageBitmap(Stack.arrowLeft);
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
                if (loadCounter < 1) {
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
            restartDialog.show(getSupportFragmentManager(), RESTART_DIALOG);

            return true;
        }

        return super.onKeyDown(keyCode, event);
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
            if (currentGame.hasMainStack() && currentGame.testIfMainStackTouched(X, Y)) {

                if (currentGame.hasLimitedRedeals() && currentGame.dealFromStack().isEmpty()) {
                    if (currentGame.getRemainingNumberOfRedeals() == 0)
                        return true;
                    else
                        currentGame.incrementRedealCounter(this);
                }

                currentGame.onMainStackTouch();
            } else if (cards[v.getId()].isUp()) {//&& currentGame.addCardToMovementTest(cards[v.getId()])) {

                if (getSharedBoolean(getString(R.string.pref_key_double_tap_enable), DEFAULT_DOUBLE_TAP_ENABLE)) {
                    if (tappedStack != null && tappedStack == cards[v.getId()].getStack() && System.currentTimeMillis() - firstTapTime < doubleTapSpeed) {

                        CardAndStack cardAndStack = null;

                        if (getSharedBoolean(getString(R.string.pref_key_double_tap_all_cards), DEFAULT_DOUBLE_TAP_ALL_CARDS) && cards[v.getId()].getStack().getID() <= currentGame.getLastTableauID()) {
                            cardAndStack = currentGame.doubleTap(cards[v.getId()].getStack());
                        } else if (currentGame.addCardToMovementTest(cards[v.getId()])) {
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

                if (currentGame.addCardToMovementTest(cards[v.getId()])) {
                    movingCards.add(cards[v.getId()], event.getX(), event.getY());
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE && movingCards.hasCards()) {
            movingCards.move(X, Y);
        } else if (event.getAction() == MotionEvent.ACTION_UP && movingCards.hasCards()) {
            Stack stack = getIntersectingStack(movingCards.first());

            if (stack != null) {
                if (movingCards.first().getStack() != stack && movingCards.first().test(stack)) {
                    movingCards.moveToDestination(stack);
                    return true;
                }
            }

            //if they aren't placed, return them to their old places
            movingCards.returnToPos();
        }
        return true;
    }


    private Stack getIntersectingStack(Card card) {
        /*
         * Use the rectangles of the card and the stacks to determinate if they intersect. If so,
         * calculate the amount of intersection. Return the stack with the highest intersection rate.
         */

        RectF cardRect = new RectF(card.view.getX(), card.view.getY(), card.view.getX() + card.view.getWidth(), card.view.getY() + card.view.getHeight());

        Stack returnStack = null;
        float overlapArea = 0;

        for (Stack stack : stacks) {
            if (card.getStack() == stack)
                continue;

            //for Pyramid, do not use the empty tableau stacks
            if (currentGame.ignoresEmptyTableauStacks() && stack.getID() <= currentGame.getLastTableauID() && stack.isEmpty())
                continue;

            RectF stackRect = stack.getRect();

            if (RectF.intersects(cardRect, stackRect)) {
                float overlapX = max(0, min(cardRect.right, stackRect.right) - max(cardRect.left, stackRect.left));
                float overlapY = max(0, min(cardRect.bottom, stackRect.bottom) - max(cardRect.top, stackRect.top));

                if (overlapX * overlapY > overlapArea) {
                    overlapArea = overlapX * overlapY;
                    returnStack = stack;
                }
            }
        }

        return returnStack;
    }

    public void menuClick(View view) {
        //if something important happens don't accept input
        if (stopConditions())
            return;

        //also return moving cards, to prevent bugs
        if (movingCards.hasCards())
            movingCards.returnToPos();

        switch (view.getId()) {
            case R.id.mainButtonScores:
                startActivity(new Intent(getApplicationContext(), Statistics.class));               //open high scores activity
                break;
            case R.id.mainButtonUndo:
                recordList.undo();                                                                  //undo last movement
                break;
            case R.id.mainButtonHint:
                hint.showHint();                                                                    //show a hint
                break;
            case R.id.mainButtonRestart:                                                            //show restart dialog
                showRestartDialog();
                break;
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
            switch (getSharedString(getString(R.string.pref_key_background_color), BACKGROUND_COLOR_DEFAULT)) {
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

    public void updateNumberOfRedeals() {
        mainTextViewRedeals.setText(String.format(Locale.getDefault(), "%d", currentGame.getRemainingNumberOfRedeals()));
    }

    public void showRestartDialog() {
        RestartDialog restartDialog = new RestartDialog();
        restartDialog.show(getSupportFragmentManager(), RESTART_DIALOG);
    }

    public void updateIcons() {
        ImageView scores, hint, menu, undo, settings;

        scores = (ImageView) findViewById(R.id.button_scores);
        hint = (ImageView) findViewById(R.id.button_hint);
        menu = (ImageView) findViewById(R.id.button_restart);
        undo = (ImageView) findViewById(R.id.button_undo);
        settings = (ImageView) findViewById(R.id.button_settings);

        switch (getSharedString(getString(R.string.pref_key_icon_theme), DEFAULT_ICON_THEME)) {
            case "Material":
                scores.setImageResource(R.drawable.icon_material_scores);
                hint.setImageResource(R.drawable.icon_material_hint);
                menu.setImageResource(R.drawable.icon_material_menu);
                undo.setImageResource(R.drawable.icon_material_undo);
                settings.setImageResource(R.drawable.icon_material_settings);
                break;
            case "Old":
                scores.setImageResource(R.drawable.icon_old_scores);
                hint.setImageResource(R.drawable.icon_old_hint);
                menu.setImageResource(R.drawable.icon_old_menu);
                undo.setImageResource(R.drawable.icon_old_undo);
                settings.setImageResource(R.drawable.icon_old_settings);
                break;
        }

    }

    public void updateMenuBar() {
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        RelativeLayout.LayoutParams params1;
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout menu = (LinearLayout) findViewById(R.id.linearLayout);
        RelativeLayout gameWindow = (RelativeLayout) findViewById(R.id.mainRelativeLayoutGame);
        RelativeLayout gameOverlay = (RelativeLayout) findViewById(R.id.mainRelativeLayoutGameOverlay);

        if (isLandscape) {
            params1 = new RelativeLayout.LayoutParams((int) getResources().getDimension(R.dimen.menuBarWidht), ViewGroup.LayoutParams.MATCH_PARENT);

            if (sharedStringEquals(getString(R.string.pref_key_menu_bar_position_landscape), DEFAULT_MENU_BAR_POSITION_LANDSCAPE)) {
                params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params2.addRule(RelativeLayout.LEFT_OF, R.id.linearLayout);

            } else {
                params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params2.addRule(RelativeLayout.RIGHT_OF, R.id.linearLayout);
            }
        } else {
            params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.menuBarHeight));

            if (sharedStringEquals(getString(R.string.pref_key_menu_bar_position_portrait), DEFAULT_MENU_BAR_POSITION_PORTRAIT)) {
                params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params2.addRule(RelativeLayout.ABOVE, R.id.linearLayout);

            } else {
                params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params2.addRule(RelativeLayout.BELOW, R.id.linearLayout);
            }
        }

        menu.setLayoutParams(params1);
        gameWindow.setLayoutParams(params2);
        gameOverlay.setLayoutParams(params2);
    }
}

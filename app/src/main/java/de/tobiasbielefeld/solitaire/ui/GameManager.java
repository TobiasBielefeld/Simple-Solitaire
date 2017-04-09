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
import de.tobiasbielefeld.solitaire.classes.CustomImageView;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.dialogs.RestartDialog;
import de.tobiasbielefeld.solitaire.handler.LoadGameHandler;
import de.tobiasbielefeld.solitaire.helper.Animate;
import de.tobiasbielefeld.solitaire.helper.AutoComplete;
import de.tobiasbielefeld.solitaire.helper.CardHighlight;
import de.tobiasbielefeld.solitaire.helper.GameLogic;
import de.tobiasbielefeld.solitaire.helper.Hint;
import de.tobiasbielefeld.solitaire.helper.MovingCards;
import de.tobiasbielefeld.solitaire.helper.RecordList;
import de.tobiasbielefeld.solitaire.helper.Scores;
import de.tobiasbielefeld.solitaire.helper.Timer;
import de.tobiasbielefeld.solitaire.ui.settings.Settings;

import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.classes.Stack.SpacingDirection.DOWN;
import static de.tobiasbielefeld.solitaire.classes.Stack.SpacingDirection.NONE;

/**
 * This is like the main activity, handles game input, controls the timer, loads and saves everything
 */

public class GameManager extends CustomAppCompatActivity implements View.OnTouchListener {

    public static int loadCounter = 0;                                                              //used to count how many times the onCreate method is called, so I can avoid loading the game multiple times
    public boolean hasLoaded = false;                                                               //used to call save() in onPause() only if load() has been called before
    public Button buttonAutoComplete;                                                               //button for auto complete
    public TextView mainTextViewTime, mainTextViewScore, mainTextViewRedeals;                       //textViews for time, scores and re-deals
    public RelativeLayout layoutGame;                                                               //contains the game stacks and cards
    public Toast toast;                                                                             //a delicious toast!
    private final static long DOUBLE_TAP_SPEED = 400;                                               //time delta between two taps in milliseconds
    private long firstTapTime;                                                                       //stores the time of first tapping on a card
    private CardAndStack tapped = null;
    private RelativeLayout mainRelativeLayoutBackground;
    public View highlight;

    /*
     * Set up everything for the game. First get the ui elements, then initialize my helper stuff.
     * Some of them need references to this activity to update ui things. After that, the card and
     * stack array will be initialized. Then the layout of the stacks will be set, but the layout
     * of the relativeLayout of the game needs to be loaded first, so everything of the loading
     * happens in the layout.post() method.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_manager);

        // load stuff
        highlight = findViewById(R.id.card_highlight);
        layoutGame = (RelativeLayout) findViewById(R.id.mainRelativeLayoutGame);
        mainTextViewTime = (TextView) findViewById(R.id.mainTextViewTime);
        mainTextViewScore = (TextView) findViewById(R.id.mainTextViewScore);
        mainTextViewRedeals = (TextView) findViewById(R.id.textViewRedeals);
        buttonAutoComplete = (Button) findViewById(R.id.buttonMainAutoComplete);
        mainRelativeLayoutBackground = (RelativeLayout) findViewById(R.id.mainRelativeLayoutBackground);

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
        cardHighlight = new CardHighlight(gm);
        currentGame = lg.loadClass(this, getIntent().getIntExtra(GAME, 1));
        savedGameData = getSharedPreferences(lg.getSharedPrefName(), MODE_PRIVATE);
        Stack.loadBackgrounds();

        updateIcons();
        updateMenuBar();


        //initialize cards and stacks
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = new Stack(i);
            stacks[i].view = new CustomImageView(this,this, CustomImageView.Object.STACK,i);
            stacks[i].view.setImageBitmap(Stack.backgroundDefault);
            layoutGame.addView(stacks[i].view);
        }

        for (int i = 0; i < cards.length; i++) {
            cards[i] = new Card(i);
            cards[i].view = new CustomImageView(this,this,CustomImageView.Object.CARD,i);
            layoutGame.addView(cards[i].view);
        }

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
                if (getSharedBoolean(PREF_KEY_LEFT_HANDED_MODE, DEFAULT_LEFT_HANDED_MODE)) {
                    for (Stack stack : stacks)
                        stack.view.setX(layoutGame.getWidth() - stack.getX() - Card.width);

                    if (currentGame.hasArrow()) {
                        for (Stack stack : stacks) {
                            stack.applyArrow();
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
                    for (Stack stack : stacks){
                        if (stack.getId() <= currentGame.getLastTableauId()){
                            stack.setSpacingDirection(DOWN);
                        } else {
                            stack.setSpacingDirection(NONE);
                        }
                    }
                } else {
                    for (int i = 0; i < stacks.length; i++) {
                        if (currentGame.directions.length>i) {
                            stacks[i].setSpacingDirection(currentGame.directions[i]);
                        } else {
                            stacks[i].setSpacingDirection(NONE);
                        }
                    }
                }

                //if there are direction borders set (when cards should'nt overlap another stack)  use it.
                //else set the layout height/widht as maximum
                currentGame.applyDirectionBorders(layoutGame);

                //load the game, to prevent multiple loadings, check the counter first. Load the game
                //only if its the last attempt to load
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

    /**
     * Handles key presses. The game shouldn't close when the back button is clicked, so show
     * the restart dialog instead.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            DialogFragment restartDialog = new RestartDialog();
            restartDialog.show(getSupportFragmentManager(), RESTART_DIALOG);

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /*
     * Is the main input handler. Tracks the input position and moves cards according to that.
     * The motion events are put in extra methods, because before it got a bit unclear
     */
    public boolean onTouch(View view, MotionEvent event) {

        CustomImageView v = (CustomImageView) view;
        //if something important happens don't accept input
        if (stopConditions())
            return true;

        //also don't do anything with a second touch point
        if (event.getPointerId(0) != 0) {
            if (movingCards.hasCards()) {
                movingCards.returnToPos();
                resetTappedCard();
            }

            return true;
        }

        //position of the event on the screen
        float X = event.getX() + v.getX(), Y = event.getY() + v.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return motionActionDown(v, event, X, Y);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE && movingCards.hasCards()) {
           return motionActionMove(X, Y);
        } else if (event.getAction() == MotionEvent.ACTION_UP && movingCards.hasCards()) {
            return motionActionUp(X, Y);
        }

        return true;
    }

    /**
     * Contains the code for double tap and tap-to-select movements. It saves the touched card and
     * its stack, and moves the card the next time the screen is touched. Separate between stacks and
     * cards. Because the tap-to-select need to test if a empty stack was touched
     *
     * @param v The tapped image view
     * @param event The motion event
     * @param X The absolute X-coordinate on the game layout
     * @param Y The absolute X-coordinate on the game layout
     * @return True to end the input
     */
    private boolean motionActionDown(CustomImageView v, MotionEvent event, float X, float Y){

        //if the main stack got touched
        if (currentGame.hasMainStack() && currentGame.testIfMainStackTouched(X, Y)) {
            //test if the redeal counter needs to be updated
            if (currentGame.hasLimitedRedeals() && currentGame.getDealStack().isEmpty()) {
                if (currentGame.getRemainingNumberOfRedeals() == 0) {
                    return true;
                } else {
                    currentGame.incrementRedealCounter(this);
                }
            }
            //do what the game wants to be done on a main stack press
            currentGame.onMainStackTouch();
            return resetTappedCard();
        }

        if (v.belongsToStack() && getSharedBoolean(PREF_KEY_TAP_TO_SELECT_ENABLED,DEFAULT_TAP_TO_SELECT_ENABLED) ) {
            if (tapped!=null && tapped.getStack()!= stacks[v.getId()]
                    && tapped.getCard().test(stacks[v.getId()])
                    && currentGame.addCardToMovementTest(tapped.getCard())) {

                movingCards.add(tapped.getCard(), event.getX(), event.getY());
                movingCards.moveToDestination(stacks[v.getId()]);
            }

            return resetTappedCard();

        } else if (v.belongsToCard() && cards[v.getId()].isUp()) {
            if (tapped!=null) {
                //double tap
                if (getSharedBoolean(PREF_KEY_DOUBLE_TAP_ENABLED, DEFAULT_DOUBLE_TAP_ENABLE)
                        && tapped.getStack() == cards[v.getId()].getStack()
                        && System.currentTimeMillis() - firstTapTime < DOUBLE_TAP_SPEED) {

                    CardAndStack cardAndStack = null;

                    if (getSharedBoolean(PREF_KEY_DOUBLE_TAP_ALL_CARDS, DEFAULT_DOUBLE_TAP_ALL_CARDS) && tapped.getStackId() <= currentGame.getLastTableauId()) {
                        cardAndStack = currentGame.doubleTap(tapped.getStack());
                    } else if (currentGame.addCardToMovementTest(tapped.getCard())) {
                        cardAndStack = currentGame.doubleTap(tapped.getCard());
                    }

                    if (cardAndStack != null) {
                        movingCards.add(cardAndStack.getCard(), event.getX(), event.getY());
                        movingCards.moveToDestination(cardAndStack.getStack());

                        return resetTappedCard();
                    }
                }
                //tap to select
                else if (getSharedBoolean(PREF_KEY_TAP_TO_SELECT_ENABLED,DEFAULT_TAP_TO_SELECT_ENABLED)
                        && tapped.getStack() != cards[v.getId()].getStack()
                        && tapped.getCard().test(cards[v.getId()].getStack())
                        && currentGame.addCardToMovementTest(tapped.getCard())) {

                    movingCards.add(tapped.getCard(), event.getX(), event.getY());
                    movingCards.moveToDestination(cards[v.getId()].getStack());

                    return resetTappedCard();
                }
            }

            tapped = new CardAndStack(cards[v.getId()],cards[v.getId()].getStack());

            firstTapTime = System.currentTimeMillis();

            if (currentGame.addCardToMovementTest(tapped.getCard())) {
                movingCards.add(tapped.getCard(), event.getX(), event.getY());
                cardHighlight.set(tapped.getCard());
            }
        }
        return true;
    }

    /**
     * Moves card for drag-and-drop movements, but only if the touch point left the area of the initial
     * point of ActionDown.
     *
     * @param X The absolute X-coordinate on the game layout
     * @param Y The absolute X-coordinate on the game layout
     * @return True to end the input
     */
    private boolean motionActionMove(float X, float Y){
        if (movingCards.moveStarted(X,Y)) {
            movingCards.move(X, Y);
            cardHighlight.move(tapped.getCard());
        }

        return true;
    }

    /**
     * Ends movements, if cards are moving. Also contains the part of the single tap movement.
     *
     * @param X The absolute X-coordinate on the game layout
     * @param Y The absolute X-coordinate on the game layout
     * @return True to end the input
     */
    private boolean motionActionUp(float X, float Y){

        if (movingCards.moveStarted(X,Y)){

            cardHighlight.hide();
            Stack stack = getIntersectingStack(movingCards.first());

            if (stack != null) {    //the card.test() method is already called in getIntersectingStack()
                movingCards.moveToDestination(stack);
            } else {
                movingCards.returnToPos();
            }

            return resetTappedCard();
        } else if (currentGame.isSingleTapEnabled() && tapped.getCard().test(currentGame.getDiscardStack())
                && getSharedBoolean(PREF_KEY_SINGLE_TAP_ENABLE,DEFAULT_SINGLE_TAP_ENABLED)) {

            movingCards.moveToDestination(currentGame.getDiscardStack());
            return resetTappedCard();
        } else {
            movingCards.returnToPos();
            return true;
        }
    }

    /**
     * Use the rectangles of the card and the stacks to determinate if they intersect and if the card
     * can be placed on that stack. If so, save the stack and the amount of intersection.
     * If another stack is also a possible destination AND has a higher intersection rate, save the
     * new stack instead. So at the end, the best possible destination will be returned.
     *
     * It takes one card and tests every stack (expect the stack, where the card is located on)
     *
     * @param card The card to test
     * @return A possible destination with the highest intersection
     */
    private Stack getIntersectingStack(Card card) {

        RectF cardRect = new RectF(card.getX(), card.getY(), card.getX() + card.view.getWidth(), card.getY() + card.view.getHeight());

        Stack returnStack = null;
        float overlapArea = 0;

        for (Stack stack : stacks) {
            if (card.getStack() == stack)
                continue;

            RectF stackRect = stack.getRect();

            if (RectF.intersects(cardRect, stackRect) && card.test(stack)) {
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

    /**
     * Loads the background color, loaded in onResume(). There are two types of background colors:
     * The xml files under drawa
     */
    private void loadBackgroundColor() {

        if (mainRelativeLayoutBackground != null) {
            if (getSharedInt(PREF_KEY_BACKGROUND_COLOR_TYPE,DEFAULT_BACKGROUND_COLOR_TYPE) == 1) {
                switch (getSharedString(PREF_KEY_BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR)) {
                    case "1":
                        mainRelativeLayoutBackground.setBackgroundResource(R.drawable.background_color_blue);
                        break;
                    case "2":
                        mainRelativeLayoutBackground.setBackgroundResource(R.drawable.background_color_green);
                        break;
                    case "3":
                        mainRelativeLayoutBackground.setBackgroundResource(R.drawable.background_color_red);
                        break;
                    case "4":
                        mainRelativeLayoutBackground.setBackgroundResource(R.drawable.background_color_yellow);
                        break;
                    case "5":
                        mainRelativeLayoutBackground.setBackgroundResource(R.drawable.background_color_orange);
                        break;
                    case "6":
                        mainRelativeLayoutBackground.setBackgroundResource(R.drawable.background_color_purple);
                        break;
                }
            } else {
                mainRelativeLayoutBackground.setBackgroundResource(0);
                mainRelativeLayoutBackground.setBackgroundColor(getSharedInt(PREF_KEY_BACKGROUND_COLOR_CUSTOM, DEFAULT_BACKGROUND_COLOR_CUSTOM));
            }
        }
    }

    /**
     * Tests if movements shouldn't be allowed. For example. If a hint is currently shown, don't
     * accept input, or otherwise something will go wrong
     *
     * @return True if no movement is allowed, false otherwise
     */
    private boolean stopConditions() {
        return (autoComplete.isRunning() || animate.cardIsAnimating() || hint.isWorking());
    }

    /**
     * Shows a text as a toast, on ui thread, because some of my static helper stuff use this too.
     *
     * @param text The text to show
     */
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

    /**
     * Updates the menu icon according to the user settings
     */
    public void updateIcons() {
        stacks[5000].isEmpty();
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

    /**
     * Updates the menu bar position according to the user settings
     */
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

    public void menuClick(View view) {
        //if something important happens don't accept input
        if (stopConditions())
            return;

        //also return moving cards, to prevent bugs
        if (movingCards.hasCards())
            movingCards.returnToPos();

        resetTappedCard();

        switch (view.getId()) {
            case R.id.mainButtonScores:         //open high scores activity
                startActivity(new Intent(getApplicationContext(), Statistics.class));
                break;
            case R.id.mainButtonUndo:           //undo last movement
                recordList.undo();
                break;
            case R.id.mainButtonHint:           //show a hint
                hint.showHint();
                break;
            case R.id.mainButtonRestart:        //show restart dialog
                showRestartDialog();
                break;
            case R.id.mainButtonSettings:       //open Settings activity
                startActivity(new Intent(getApplicationContext(), Settings.class));
                break;
            case R.id.buttonMainAutoComplete:   //start auto complete
                autoComplete.start();
                break;
        }
    }

    public void updateNumberOfRedeals() {
        mainTextViewRedeals.setText(String.format(Locale.getDefault(), "%d", currentGame.getRemainingNumberOfRedeals()));
    }

    public void showRestartDialog() {
        RestartDialog restartDialog = new RestartDialog();
        restartDialog.show(getSupportFragmentManager(), RESTART_DIALOG);
    }

    private boolean resetTappedCard(){
        tapped=null;
        cardHighlight.hide();
        return true;
    }
}

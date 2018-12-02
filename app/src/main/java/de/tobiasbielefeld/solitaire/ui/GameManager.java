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

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.CustomAppCompatActivity;
import de.tobiasbielefeld.solitaire.classes.CustomImageView;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.classes.WaitForAnimationHandler;
import de.tobiasbielefeld.solitaire.dialogs.DialogInGameHelpMenu;
import de.tobiasbielefeld.solitaire.dialogs.DialogInGameMenu;
import de.tobiasbielefeld.solitaire.dialogs.DialogWon;
import de.tobiasbielefeld.solitaire.handler.HandlerLoadGame;
import de.tobiasbielefeld.solitaire.helper.Animate;
import de.tobiasbielefeld.solitaire.helper.AutoComplete;
import de.tobiasbielefeld.solitaire.helper.AutoMove;
import de.tobiasbielefeld.solitaire.helper.DealCards;
import de.tobiasbielefeld.solitaire.helper.EnsureMovability;
import de.tobiasbielefeld.solitaire.helper.GameLogic;
import de.tobiasbielefeld.solitaire.helper.Hint;
import de.tobiasbielefeld.solitaire.helper.RecordList;
import de.tobiasbielefeld.solitaire.helper.Scores;
import de.tobiasbielefeld.solitaire.helper.Sounds;
import de.tobiasbielefeld.solitaire.helper.Timer;
import de.tobiasbielefeld.solitaire.ui.settings.Settings;
import de.tobiasbielefeld.solitaire.ui.statistics.StatisticsActivity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.classes.Stack.SpacingDirection.*;
import static de.tobiasbielefeld.solitaire.helper.Preferences.*;

/**
 * This is like the main activity, handles game input, controls the timer, loads and saves everything
 */

public class GameManager extends CustomAppCompatActivity implements View.OnTouchListener {

    private final static long DOUBLE_TAP_SPEED = 400;                          //time delta between two taps in milliseconds
    public boolean hasLoaded = false;                                          //used to call save() in onPause() only if load() has been called before
    public Button buttonAutoComplete;                                          //button for auto complete
    public TextView mainTextViewTime, mainTextViewScore, mainTextViewRecycles; //textViews for time, scores and re-deals
    public RelativeLayout layoutGame;                                          //contains the game stacks and cards
    public ImageView highlight;
    private long firstTapTime;                                                 //stores the time of first tapping on a card
    private CardAndStack tapped = null;
    private RelativeLayout mainRelativeLayoutBackground;
    private boolean activityPaused;
    public ImageView hideMenu;
    public LinearLayout menuBar;

    /*
     * Set up everything for the game. First get the ui elements, then initialize my helper stuff.
     * Some of them need references to this activity to update ui things. After that, the card and
     * stack array will be initialized. Then the layout of the stacks will be set, but the layout
     * of the relativeLayout of the game needs to be loaded first, so everything of the loading
     * happens in the layout.post() method.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_manager);

        /**
         * Initializing stuff
         */

        highlight = findViewById(R.id.card_highlight);
        layoutGame = findViewById(R.id.mainRelativeLayoutGame);
        mainTextViewTime = findViewById(R.id.mainTextViewTime);
        mainTextViewScore = findViewById(R.id.mainTextViewScore);
        mainTextViewRecycles = findViewById(R.id.textViewRecycles);
        buttonAutoComplete = findViewById(R.id.buttonMainAutoComplete);
        mainRelativeLayoutBackground = findViewById(R.id.mainRelativeLayoutBackground);
        hideMenu = findViewById(R.id.mainImageViewResize);
        menuBar = findViewById(R.id.linearLayoutMenuBar);

        //initialize my static helper stuff
        final GameManager gm = this;

        autoMove = new AutoMove(gm);
        hint = new Hint(gm);
        scores = new Scores(gm);
        gameLogic = new GameLogic(gm);
        animate = new Animate(gm);
        autoComplete = new AutoComplete(gm);
        timer = new Timer(gm);
        sounds = new Sounds(gm);
        dealCards = new DealCards(gm);
        ensureMovability = new EnsureMovability();

        if (savedInstanceState != null && savedInstanceState.containsKey(GAME)) {
            currentGame = lg.loadClass(gm, savedInstanceState.getInt(GAME));
        } else {
            currentGame = lg.loadClass(gm, getIntent().getIntExtra(GAME, -1));
        }

        /*
         * Setting up callbacks
         */

        currentGame.setRecycleCounterCallback(this::updateNumberOfRecycles);

        ensureMovability.setShowDialog(dialog -> dialog.show(getSupportFragmentManager(), "DIALOG_ENSURE_MOVABILITY"));

        scores.setCallback(this::updateScore);

        handlerTestAfterMove = new WaitForAnimationHandler(gm, new WaitForAnimationHandler.MessageCallBack() {
            @Override
            public void doAfterAnimation() {
                if (!gameLogic.hasWon()) {
                    currentGame.testAfterMove();
                }

                handlerTestIfWon.sendDelayed();

                if (!autoComplete.isRunning() && !gameLogic.hasWon()) {
                    gameLogic.checkForAutoCompleteButton(false);
                }
            }

            @Override
            public boolean additionalHaltCondition() {
                return false;
            }
        });

        handlerTestIfWon = new WaitForAnimationHandler(gm, new WaitForAnimationHandler.MessageCallBack() {
            @Override
            public void doAfterAnimation() {
                gameLogic.testIfWon();
            }

            @Override
            public boolean additionalHaltCondition() {
                return false;
            }
        });

        /*
         * Setting up game data
         */

        prefs.setGamePreferences(gm);
        Stack.loadBackgrounds();
        recordList = new RecordList(gm);

        //initialize cards and stacks
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = new Stack(i);
            stacks[i].view = new CustomImageView(this, this, CustomImageView.Object.STACK, i);
            stacks[i].forceSetImageBitmap(Stack.backgroundDefault);
            layoutGame.addView(stacks[i].view);
        }

        currentGame.offScreenStack = new Stack(-1);
        currentGame.offScreenStack.setSpacingDirection(NONE);
        currentGame.offScreenStack.view = new CustomImageView(this, null, CustomImageView.Object.STACK, -1);
        layoutGame.addView(currentGame.offScreenStack.view);

        for (int i = 0; i < cards.length; i++) {
            cards[i] = new Card(i);
            cards[i].view = new CustomImageView(this, this, CustomImageView.Object.CARD, i);
            layoutGame.addView(cards[i].view);
        }

        updateMenuBar();
        loadBackgroundColor();
        setUiElementsColor();

        if (prefs.getSavedHideScore()) {
            mainTextViewScore.setVisibility(GONE);
        }

        if (prefs.getSavedHideTime()) {
            mainTextViewTime.setVisibility(GONE);
        }

        scores.output();

        //wait until the game layout dimensions are known, then draw everything
        ViewTreeObserver viewTreeObserver = layoutGame.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    layoutGame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    //noinspection deprecation
                    layoutGame.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                if (savedInstanceState != null) {
                    if (savedInstanceState.containsKey("BUNDLE_ENSURE_MOVABILITY")) {
                        //put the following in a wait handler, to wait after a possible background job
                        //from EnsureMovability is finished.
                        new WaitForAnimationHandler(gm, new WaitForAnimationHandler.MessageCallBack() {
                            @Override
                            public void doAfterAnimation() {
                                initializeLayout(false);
                                gameLogic.load(true);

                                ensureMovability.loadInstanceState(savedInstanceState);
                            }

                            @Override
                            public boolean additionalHaltCondition() {
                                return stopUiUpdates;
                            }
                        }).forceSendNow();
                    } else {
                        if (savedInstanceState.containsKey(getString(R.string.bundle_reload_game))) {
                            initializeLayout(false);
                            gameLogic.load(true);
                        }

                        ensureMovability.loadInstanceState(savedInstanceState);
                        autoComplete.loadInstanceState(savedInstanceState);
                        autoMove.loadInstanceState(savedInstanceState);
                        hint.loadInstanceState(savedInstanceState);
                        dealCards.loadInstanceState(savedInstanceState);
                    }
                } else {
                    initializeLayout(true);
                }
            }
        });
    }

    private void initializeLayout(boolean loadNewGame) {
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        currentGame.setStacks(layoutGame, isLandscape, getApplicationContext());
        currentGame.setOffScreenStack();

        //if left handed mode is true, mirror all stacks
        if (prefs.getSavedLeftHandedMode()) {
            gameLogic.mirrorStacks();
        }

        //calculate the spacing for cards on a stack
        Stack.defaultSpacing = Card.width / 2;

        for (Stack stack : stacks) {
            stack.applyDefaultSpacing();
        }

        //setup how the cards on the stacks will be stacked (offset to the previous card)
        //there are 4 possible directions. By default, the tableau stacks are stacked down
        //all other stacks don't have a visible offset
        //use setDirections() in a game to change that
        if (currentGame.directions == null) {
            for (Stack stack : stacks) {
                if (stack.getId() <= currentGame.getLastTableauId()) {
                    stack.setSpacingDirection(DOWN);
                } else {
                    stack.setSpacingDirection(NONE);
                }
            }
        } else {
            for (int i = 0; i < stacks.length; i++) {
                if (currentGame.directions.length > i) {
                    stacks[i].setSpacingDirection(currentGame.directions[i]);
                } else {
                    stacks[i].setSpacingDirection(NONE);
                }
            }
        }

        //if there are direction borders set (when cards should'nt overlap another stack)  use it.
        //else set the layout height/width as maximum
        currentGame.applyDirectionBorders(layoutGame);

        scores.load();

        updateLimitedRecyclesCounter();

        if (loadNewGame) {
            HandlerLoadGame handlerLoadGame = new HandlerLoadGame();
            handlerLoadGame.sendEmptyMessageDelayed(0, 200);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //ony save if the game has been loaded before
        if (hasLoaded) {
            timer.save();
            gameLogic.save();
        }

        autoComplete.pause();
        autoMove.pause();
        hint.pause();
        ensureMovability.pause();
        dealCards.pause();

        activityPaused = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(getString(R.string.bundle_reload_game), true);
        outState.putInt(GAME, getIntent().getIntExtra(GAME, -1));

        autoComplete.saveInstanceState(outState);
        autoMove.saveInstanceState(outState);
        hint.saveInstanceState(outState);
        ensureMovability.saveInstanceState(outState);
        dealCards.saveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        showOrHideNavBar();

        activityPaused = false;

        timer.load();
        autoComplete.resume();
        autoMove.resume();
        hint.resume();
        ensureMovability.resume();
        dealCards.resume();
    }

    /**
     * Handles key presses. The game shouldn't close when the back button is clicked, so show
     * the restart dialog instead.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (gameLogic.stopConditions()) {
            return false;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showRestartDialog();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Is the main input handler. Tracks the input position and moves cards according to that.
     * The motion events are put in extra methods, because before it got a bit unclear
     */
    public boolean onTouch(View view, MotionEvent event) {
        CustomImageView v = (CustomImageView) view;

        //if something important happens don't accept input
        if (gameLogic.stopConditions() || gameLogic.hasWon()) {
            return true;
        }

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
     * @param v     The tapped image view
     * @param event The motion event
     * @param X     The absolute X-coordinate on the game layout
     * @param Y     The absolute X-coordinate on the game layout
     * @return True to end the input
     */
    private boolean motionActionDown(CustomImageView v, MotionEvent event, float X, float Y) {
        //if the main stack got touched
        if (currentGame.hasMainStack() && currentGame.testIfMainStackTouched(X, Y)) {

            //if no card could be moved, do nothing
            if (currentGame.mainStackTouch() == 0) {
                return true;
            }

            gameLogic.checkForAutoCompleteButton(false);
            handlerTestAfterMove.sendDelayed();
            return resetTappedCard();
        }

        if (v.belongsToStack() && prefs.getSavedTapToSelectEnabled()) {
            if (tapped != null && tapped.getStack() != stacks[v.getId()]
                    && currentGame.addCardToMovementTest(tapped.getCard())) {

                movingCards.add(tapped.getCard(), event.getX(), event.getY());

                if (tapped.getCard().test(stacks[v.getId()])) {
                    movingCards.moveToDestination(stacks[v.getId()]);
                } else {
                    movingCards.reset();
                }
            }

            return resetTappedCard();

        } else if (v.belongsToCard() && cards[v.getId()].isUp()) {
            if (tapped != null) {
                //double tap
                if (prefs.getSavedDoubleTapEnabled() && tapped.getStack() == cards[v.getId()].getStack()
                        && System.currentTimeMillis() - firstTapTime < DOUBLE_TAP_SPEED) {

                    boolean result = doubleTapCalculation(event.getX(), event.getY());

                    //do not directly return from double tap calculation, addCardToMovementTest()
                    // needs to run in case the calculation returns false
                    if (result) {
                        return true;
                    }
                }
                //tap to select
                else if (prefs.getSavedTapToSelectEnabled()
                        && tapped.getStack()
                        != cards[v.getId()].getStack()
                        && currentGame.addCardToMovementTest(tapped.getCard())) {

                    movingCards.add(tapped.getCard(), event.getX(), event.getY());

                    if (tapped.getCard().test(cards[v.getId()].getStack())) {
                        movingCards.moveToDestination(cards[v.getId()].getStack());
                        return resetTappedCard();
                    } else {
                        movingCards.reset();
                    }
                }
            }

            if (currentGame.addCardToMovementTest((cards[v.getId()]))) {
                tapped = new CardAndStack(cards[v.getId()], cards[v.getId()].getStack());

                firstTapTime = System.currentTimeMillis();

                if (currentGame.addCardToMovementTest(tapped.getCard())) {
                    movingCards.add(tapped.getCard(), event.getX(), event.getY());
                    cardHighlight.set(this, tapped.getCard());
                }
            }
        }
        return true;
    }

    private boolean doubleTapCalculation(float X, float Y) {
        CardAndStack cardAndStack = null;

        if (prefs.getSavedDoubleTapAllCards() && tapped.getStackId() <= currentGame.getLastTableauId()) {
            if (prefs.getSavedDoubleTapFoundationFirst() && currentGame.hasFoundationStacks()) {
                cardAndStack = currentGame.doubleTap(tapped.getStack().getTopCard());
            }

            if (cardAndStack == null || cardAndStack.getStackId() <= currentGame.getLastTableauStack().getId()) {
                cardAndStack = currentGame.doubleTap(tapped.getStack());
            }
        } else if (currentGame.addCardToMovementTest(tapped.getCard())) {
            cardAndStack = currentGame.doubleTap(tapped.getCard());
        }

        if (cardAndStack != null) {
            movingCards.reset();
            movingCards.add(cardAndStack.getCard(), X, Y);
            movingCards.moveToDestination(cardAndStack.getStack());

            return resetTappedCard();
        }

        return false;
    }

    /**
     * Moves card for drag-and-drop movements, but only if the touch point left the area of the initial
     * point of ActionDown.
     *
     * @param X The absolute X-coordinate on the game layout
     * @param Y The absolute X-coordinate on the game layout
     * @return True to end the input
     */
    private boolean motionActionMove(float X, float Y) {
        if (movingCards.moveStarted(X, Y)) {
            movingCards.move(X, Y);

            if (tapped != null) {
                cardHighlight.move(this, tapped.getCard());
            }
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
    private boolean motionActionUp(float X, float Y) {

        if (movingCards.moveStarted(X, Y)) {

            cardHighlight.hide(this);
            Stack stack = getIntersectingStack(movingCards.first());

            if (stack != null) {    //the card.test() method is already called in getIntersectingStack()
                movingCards.moveToDestination(stack);
            } else {
                movingCards.returnToPos();
            }

            return resetTappedCard();
        } else if (prefs.getSingleTapAllGames()) {
            boolean result = doubleTapCalculation(X, Y);

            //do not directly return from double tap calculation, movingCards.returnToPos()
            // needs to run in case the calculation returns false
            if (result) {
                return true;
            }
        } else if (currentGame.isSingleTapEnabled() && tapped.getCard().test(currentGame.getDiscardStack())) {
            movingCards.moveToDestination(currentGame.getDiscardStack());
            return resetTappedCard();
        }

        movingCards.returnToPos();
        return true;

    }

    /**
     * Use the rectangles of the card and the stacks to determinate if they intersect and if the card
     * can be placed on that stack. If so, save the stack and the amount of intersection.
     * If another stack is also a possible destination AND has a higher intersection rate, save the
     * new stack instead. So at the end, the best possible destination will be returned.
     * <p>
     * It takes one card and tests every stack (expect the stack, where the card is located on)
     *
     * @param card The card to test
     * @return A possible destination with the highest intersection
     */
    private Stack getIntersectingStack(Card card) {

        RectF cardRect = new RectF(card.getX(), card.getY(),
                card.getX() + card.view.getWidth(),
                card.getY() + card.view.getHeight());

        Stack returnStack = null;
        float overlapArea = 0;

        for (Stack stack : stacks) {
            if (card.getStack() == stack)
                continue;

            RectF stackRect = stack.getRect();

            if (RectF.intersects(cardRect, stackRect)) {
                float overlapX = max(0, min(cardRect.right, stackRect.right) - max(cardRect.left, stackRect.left));
                float overlapY = max(0, min(cardRect.bottom, stackRect.bottom) - max(cardRect.top, stackRect.top));

                if (overlapX * overlapY > overlapArea && card.test(stack)) {
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
            if (prefs.getSavedBackgroundColorType() == 1) {
                switch (prefs.getSavedBackgroundColor()) {
                    case 1:
                        mainRelativeLayoutBackground.setBackgroundResource(R.drawable.background_color_blue);
                        break;
                    case 2:
                        mainRelativeLayoutBackground.setBackgroundResource(R.drawable.background_color_green);
                        break;
                    case 3:
                        mainRelativeLayoutBackground.setBackgroundResource(R.drawable.background_color_red);
                        break;
                    case 4:
                        mainRelativeLayoutBackground.setBackgroundResource(R.drawable.background_color_yellow);
                        break;
                    case 5:
                        mainRelativeLayoutBackground.setBackgroundResource(R.drawable.background_color_orange);
                        break;
                    case 6:
                        mainRelativeLayoutBackground.setBackgroundResource(R.drawable.background_color_purple);
                        break;
                }
            } else {
                mainRelativeLayoutBackground.setBackgroundResource(0);
                mainRelativeLayoutBackground.setBackgroundColor(prefs.getSavedBackgroundCustomColor());
            }
        }
    }

    private void setUiElementsColor() {
        int textColor = prefs.getSavedTextColor();

        mainTextViewTime.setTextColor(textColor);
        mainTextViewScore.setTextColor(textColor);
        hideMenu.setColorFilter(textColor);
        highlight.setColorFilter(textColor);

        for (Stack stack : stacks) {
            stack.view.setColorFilter(textColor);
        }

        currentGame.textViewSetColor(textColor);
    }

    public void applyGameLayoutMargins(RelativeLayout.LayoutParams params, boolean isLandscape) {
        int savedValue;
        int margin = 0;

        if (isLandscape) {
            savedValue = prefs.getSavedGameLayoutMarginsLandscape();
        } else {
            savedValue = prefs.getSavedGameLayoutMarginsPortrait();
        }

        switch (savedValue) {
            case 1:
                margin = (int) getResources().getDimension(R.dimen.game_layout_margins_small);
                break;
            case 2:
                margin = (int) getResources().getDimension(R.dimen.game_layout_margins_medium);
                break;
            case 3:
                margin = (int) getResources().getDimension(R.dimen.game_layout_margins_large);
                break;
        }

        params.setMargins(margin, 0, margin, 0);

    }

    public void updateGameLayout() {
        updateMenuBar();

        //wait until the game layout dimensions are known, then draw everything
        ViewTreeObserver viewTreeObserver = layoutGame.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    layoutGame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    //noinspection deprecation
                    layoutGame.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                initializeLayout(false);

                if (gameLogic.hasWon()) {
                    for (Card card : cards) {
                        card.setLocationWithoutMovement(layoutGame.getWidth(), 0);
                    }
                } else {
                    for (Stack stack : stacks) {
                        stack.updateSpacingWithoutMovement();
                    }
                }
            }
        });
    }

    /**
     * Updates the menu bar position according to the user settings
     */
    public void updateMenuBar() {
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        //params for the menu bar
        RelativeLayout.LayoutParams params1;

        //params for the gameLayout
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        applyGameLayoutMargins(params2, isLandscape);

        //params for the game overlay
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        LinearLayout menu = findViewById(R.id.linearLayoutMenuBar);
        RelativeLayout gameWindow = findViewById(R.id.mainRelativeLayoutGame);
        RelativeLayout gameOverlayLower = findViewById(R.id.mainRelativeLayoutGameOverlayLower);
        RelativeLayout gameOverlayUpper = findViewById(R.id.mainRelativeLayoutGameOverlay);

        if (isLandscape) {
            params1 = new RelativeLayout.LayoutParams((int) getResources().getDimension(R.dimen.menuBarWidht), ViewGroup.LayoutParams.MATCH_PARENT);

            if (prefs.getSavedMenuBarPosLandscape().equals(DEFAULT_MENU_BAR_POSITION_LANDSCAPE)) {
                params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params2.addRule(RelativeLayout.LEFT_OF, R.id.linearLayoutMenuBar);
                params3.addRule(RelativeLayout.LEFT_OF, R.id.linearLayoutMenuBar);
            } else {
                params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params2.addRule(RelativeLayout.RIGHT_OF, R.id.linearLayoutMenuBar);
                params3.addRule(RelativeLayout.RIGHT_OF, R.id.linearLayoutMenuBar);
            }
        } else {
            params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.menuBarHeight));

            if (prefs.getSavedMenuBarPosPortrait().equals(DEFAULT_MENU_BAR_POSITION_PORTRAIT)) {
                params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params2.addRule(RelativeLayout.ABOVE, R.id.linearLayoutMenuBar);
                params3.addRule(RelativeLayout.ABOVE, R.id.linearLayoutMenuBar);

            } else {
                params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params2.addRule(RelativeLayout.BELOW, R.id.linearLayoutMenuBar);
                params3.addRule(RelativeLayout.BELOW, R.id.linearLayoutMenuBar);
            }
        }


        menu.setLayoutParams(params1);
        gameWindow.setLayoutParams(params2);
        gameOverlayLower.setLayoutParams(params2);
        gameOverlayUpper.setLayoutParams(params3);

        menuBar.setVisibility(prefs.getHideMenuBar() ? GONE : VISIBLE);
        updateHideMenuButton(isLandscape);
    }

    public void menuClick(View view) {
        //if something important happens don't accept input
        if (gameLogic.stopConditions()) {
            return;
        }
        //also return moving cards, to prevent bugs
        if (movingCards.hasCards()) {
            movingCards.returnToPos();
        }

        resetTappedCard();

        switch (view.getId()) {
            case R.id.mainImageViewResize:
                if (menuBar.getVisibility() == VISIBLE) {
                    menuBar.setVisibility(GONE);
                    prefs.saveHideMenuBar(true);
                } else {
                    menuBar.setVisibility(VISIBLE);
                    prefs.saveHideMenuBar(false);
                }

                menuBar.post(() -> {
                    gameLogic.save();
                    updateGameLayout();
                });
                break;
            case R.id.mainButtonScores:         //open high scores activity
                startActivity(new Intent(getApplicationContext(), StatisticsActivity.class));
                break;
            case R.id.mainButtonUndo:           //undo last movement
                if (!gameLogic.hasWon()) {
                    recordList.undo();
                }
                break;
            case R.id.mainButtonHint:           //show a hint
                showHelpDialog();
                break;
            case R.id.mainButtonRestart:        //show restart dialog
                showRestartDialog();
                break;
            case R.id.mainButtonSettings:       //open Settings activity
                Intent i = new Intent(this, Settings.class);
                startActivityForResult(i, 1);
                break;
            case R.id.buttonMainAutoComplete:   //start auto complete
                autoComplete.start();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.hasExtra(getString(R.string.intent_update_game_layout))) {
                    updateGameLayout();
                }
                if (data.hasExtra(getString(R.string.intent_background_color))) {
                    loadBackgroundColor();
                }
                if (data.hasExtra(getString(R.string.intent_update_menu_bar))) {
                    updateMenuBar();
                }
                if (data.hasExtra(getString(R.string.intent_text_color))) {
                    setUiElementsColor();
                }
                if (data.hasExtra(getString(R.string.intent_update_score_visibility))) {
                    mainTextViewScore.setVisibility(prefs.getSavedHideScore() ? GONE : VISIBLE);
                }
                if (data.hasExtra(getString(R.string.intent_update_time_visibility))) {
                    mainTextViewTime.setVisibility(prefs.getSavedHideTime() ? GONE : VISIBLE);
                }
            }
        }
    }

    private void updateHideMenuButton(boolean isLandscape) {
        boolean menuBarVisible = menuBar.getVisibility() == VISIBLE;

        if (prefs.getHideMenuButton()) {
            hideMenu.setVisibility(GONE);
        } else {
            hideMenu.setVisibility(VISIBLE);

            if (!isLandscape) {
                if (prefs.getSavedMenuBarPosPortrait().equals("bottom")) {
                    hideMenu.setImageResource(menuBarVisible
                            ? R.drawable.icon_arrow_down
                            : R.drawable.icon_arrow_up);
                } else {
                    hideMenu.setImageResource(menuBarVisible
                            ? R.drawable.icon_arrow_up
                            : R.drawable.icon_arrow_down);
                }
            } else {
                if (prefs.getSavedMenuBarPosLandscape().equals("right")) {
                    hideMenu.setImageResource(menuBarVisible
                            ? R.drawable.icon_arrow_right
                            : R.drawable.icon_arrow_left);
                } else {
                    hideMenu.setImageResource(menuBarVisible
                            ? R.drawable.icon_arrow_left
                            : R.drawable.icon_arrow_right);
                }
            }
        }
    }

    private void updateNumberOfRecycles() {
        if (!stopUiUpdates) {
            mainTextViewRecycles.post(() ->
                    mainTextViewRecycles.setText(String.format(Locale.getDefault(), "%d",
                            currentGame.getRemainingNumberOfRecycles())));
        }
    }

    private void updateScore(final long score, final String dollar) {
        if (stopUiUpdates) {
            return;
        }

        mainTextViewScore.post(() -> mainTextViewScore.setText(String.format("%s: %s %s",
                getString(R.string.game_score), score, dollar)));
    }

    /**
     * do not show the dialog while the activity is paused. This would cause a force close
     */
    public void showRestartDialog() {
        try {
            DialogInGameMenu dialogInGameMenu = new DialogInGameMenu();
            dialogInGameMenu.show(getSupportFragmentManager(), RESTART_DIALOG);
        } catch (Exception e) {
            Log.e("showRestartDialog: ", e.toString());
        }
    }

    public void showHelpDialog() {
        try {
            DialogInGameHelpMenu dialog = new DialogInGameHelpMenu();
            dialog.show(getSupportFragmentManager(), "HELP_MENU");
        } catch (Exception e) {
            Log.e("showHelpDialog: ", e.toString());
        }
    }

    /**
     * do not show the dialog while the activity is paused. This would cause a force close
     */
    public void showWonDialog() {

        try {
            DialogWon dialogWon = new DialogWon();
            dialogWon.show(getSupportFragmentManager(), WON_DIALOG);
        } catch (Exception e) {
            Log.e("showWonDialog: ", e.toString());
        }
    }

    private boolean resetTappedCard() {
        tapped = null;
        cardHighlight.hide(this);
        return true;
    }

    /**
     * just to let the win animation handler know, if the game was paused (due to screen rotation)
     * so it can halt
     */
    public boolean isActivityPaused() {
        return activityPaused;
    }

    public void updateLimitedRecyclesCounter() {
        if (currentGame.hasLimitedRecycles() && !currentGame.hidesRecycleCounter()) {
            mainTextViewRecycles.setVisibility(VISIBLE);
            mainTextViewRecycles.setX(currentGame.getMainStack().getX());
            mainTextViewRecycles.setY(currentGame.getMainStack().getY());
        } else {
            mainTextViewRecycles.setVisibility(GONE);
        }
    }

    /**
     * set the current game to 0, otherwise the menu would load the current game again,
     * because last played game will start
     */
    @Override
    public void finish() {
        prefs.saveCurrentGame(DEFAULT_CURRENT_GAME);
        super.finish();
    }

    /**
     * Enables the fullscreen immersive mode. Only works on Kitkat and above. Also start a listener
     * in case the fullscreen mode is deactivated from the settings, so the game layout has to redraw.
     */
    private void showOrHideNavBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();

            if (prefs.getSavedImmersiveMode()) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            } else {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
    }
}

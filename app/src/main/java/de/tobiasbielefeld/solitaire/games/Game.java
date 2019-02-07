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

package de.tobiasbielefeld.solitaire.games;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.CallSuper;
import android.support.v4.widget.TextViewCompat;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.helper.RecordList;
import de.tobiasbielefeld.solitaire.helper.Sounds;

import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.games.Game.testMode2.*;

/**
 * Abstract class for all the games. See the DUMMY GAME for detailed explanation of everything!
 * (And of course the javadoc comments)
 */

public abstract class Game {

    //stack not visibile on the screen, used to remove cards from a game
    public Stack offScreenStack;

    public int[] cardDrawablesOrder = new int[]{1, 2, 3, 4};
    public Stack.SpacingDirection[] directions;
    public int[] directionBorders;
    private int dealFromID = -1;

    private int[] discardStackIDs = new int[]{-1};
    private int[] mainStackIDs = new int[]{-1};

    private boolean hasLimitedRecycles = false;
    private boolean hasFoundationStacks = false;
    private boolean hasMainStacks = false;
    private boolean hasDiscardStacks = false;
    private int firstMainStackID = -1;
    private int firstDiscardStackID = -1;
    private int lastTableauID = -1;
    private int lastFoundationID = -1;
    private int recycleCounter = 0;
    private int totalRecycles = 0;
    private int textViewColor = 0;
    private boolean hasArrow = false;
    private boolean singleTapEnabled = false;
    private boolean bonusEnabled = true;
    private boolean pointsInDollar = false;
    private boolean hideRecycleCounter = false;
    private int hintCosts = 25;
    private int undoCosts = 25;
    private ArrayList<TextView> textViews = new ArrayList<>();
    private testMode mixCardsTestMode = testMode.DOESNT_MATTER;
    private RecycleCounterCallback recycleCounterCallback;

    // some methods used by other classes

    /**
     * Used eg. if the player gets stuck and can't move any further: Mix all cards randomly by
     * exchanging them with other cards. The games can exclude cards to mix, like all cards on the
     * foundation, or complete sequences.
     */
    public void mixCards() {
        Random random = getPrng();
        ArrayList<Card> cardsToMix = new ArrayList<>();
        int counter;
        Card cardToChange;

        //getHighScore the cards to mix
        for (Card card : cards) {
            if (!excludeCardFromMixing(card)) {
                cardsToMix.add(card);
            }
        }

        //exchange cards. A bit like Fisher-Yate Shuffle, but the iterating array doesn't change.
        for (int i = cardsToMix.size() - 1; i >= 0; i--) {

            if (prefs.getSavedUseTrueRandomisation()) {
                cardToChange = cardsToMix.get(random.nextInt(i + 1));
            } else {
                //choose a new card as long the chosen card is too similar to the previous and following card in the array
                //(same value or color) also limit the loop to max 10 iterations to avoid infinite loops
                counter = 0;

                do {
                    cardToChange = cardsToMix.get(random.nextInt(i + 1));
                    counter++;
                }
                while ( //the card below cardToChange shouldn't be too similar (but only if there is a card below)
                        (!cardToChange.isFirstCard() && (cardToChange.getCardBelow().getValue() == cardsToMix.get(i).getValue() || cardToChange.getCardBelow().getColor() == cardsToMix.get(i).getColor())
                                //the card on top cardToChange shouldn't be too similar (but only if there is a card on top)
                                || !cardToChange.isTopCard() && (cardToChange.getCardOnTop().getValue() == cardsToMix.get(i).getValue() || cardToChange.getCardOnTop().getColor() == cardsToMix.get(i).getColor()))
                                //and the loop shouldn't take too long
                                && counter < 10);
            }

            cardToChange.getStack().exchangeCard(cardToChange, cardsToMix.get(i));
        }

        sounds.playSound(Sounds.names.DEAL_CARDS);

        //After every card got a new place, update the card image views
        for (Stack stack : stacks) {
            stack.updateSpacing();
        }

        //delete the record list, otherwise undoing movements would result in strange behavior
        recordList.reset();
        handlerTestAfterMove.sendDelayed();
    }

    public void dealNewGame() {
        dealCards();
        load();

        switch (prefs.getDeveloperOptionDealCorrectSequences()) {
            case 1: //alternating color
                flipAllCardsUp();

                for (int i = 0; i < (cards.length / 13); i++) {
                    for (int j = 0; j < 13; j++) {
                        int color = (j % 2 == 0) ? i : (i == 0) ? (cards.length / 13) - 1 : i - 1;
                        int cardIndex = (13 * (color + 1)) - j - 1;
                        cards[cardIndex].removeFromCurrentStack();
                        moveToStack(cards[cardIndex], stacks[i], OPTION_NO_RECORD);
                    }
                }

                break;
            case 2: //same family
                flipAllCardsUp();

                for (int i = 0; i < (cards.length / 13); i++) {
                    for (int j = 0; j < 13; j++) {
                        int cardIndex = (13 * (i + 1)) - j - 1;
                        cards[cardIndex].removeFromCurrentStack();
                        moveToStack(cards[cardIndex], stacks[i], OPTION_NO_RECORD);
                    }
                }

                break;
            case 3: //reversed alternating color
                flipAllCardsUp();

                for (int i = 0; i < (cards.length / 13); i++) {
                    for (int j = 0; j < 13; j++) {
                        int color = (j % 2 == 0) ? i : (i == 0) ? (cards.length / 13) - 1 : i - 1;
                        int cardIndex = 13 * color + j;
                        cards[cardIndex].removeFromCurrentStack();
                        moveToStack(cards[cardIndex], stacks[i], OPTION_NO_RECORD);
                    }
                }

                break;
            case 4: //reversed same family
                flipAllCardsUp();

                for (int i = 0; i < (cards.length / 13); i++) {
                    for (int j = 0; j < 13; j++) {
                        int cardIndex = 13 * i + j;
                        cards[cardIndex].removeFromCurrentStack();
                        moveToStack(cards[cardIndex], stacks[i], OPTION_NO_RECORD);
                    }
                }

                break;
            default:
                //nothing, developer option not set
                break;
        }
    }


    /**
     * Called to test where the given card can be moved to
     *
     * @param card The card to test
     * @return A destination, if the card can be moved, null otherwise
     */
    public CardAndStack doubleTap(Card card) {
        CardAndStack cardAndStack = null;
        Stack destination;

        destination = doubleTapTest(card);

        if (destination != null) {
            cardAndStack = new CardAndStack(card, destination);
        }

        return cardAndStack;
    }

    /**
     * Called to test whether a card of this stack can be placed somewhere else, or not
     *
     * @param stack The stack to test
     * @return A destination, if the card can be moved, null otherwise
     */
    public CardAndStack doubleTap(Stack stack) {
        CardAndStack cardAndStack = null;
        Stack destination = null;

        for (int i = stack.getFirstUpCardPos(); i < stack.getSize(); i++) {
            if (addCardToMovementTest(stack.getCard(i))) {
                destination = doubleTapTest(stack.getCard(i));
            }

            if (destination != null) {
                if (destination.isEmpty()) {
                    if (cardAndStack == null) {
                        cardAndStack = new CardAndStack(stack.getCard(i), destination);
                    }
                } else {
                    cardAndStack = new CardAndStack(stack.getCard(i), destination);
                    break;
                }
            }
        }

        return cardAndStack;
    }

    //methods games must implement

    /**
     * Sets the layouts and position of the stacks on the screen.
     *
     * @param layoutGame  The layout, where the stacks and cards are showed in. Used to calculate
     *                    the width/height
     * @param isLandscape Shows if the screen is in landscape mode, so the games can set up
     *                    different layouts for this
     */
    abstract public void setStacks(RelativeLayout layoutGame, boolean isLandscape, Context context);

    /**
     * Tests if the currently played game is won. Called after every movement. If the game is won,
     * the score will be saved and win animation started.
     *
     * @return True if won, false otherwise
     */
    abstract public boolean winTest();

    /**
     * Deals the initial layout of cards at game start.
     */
    abstract public void dealCards();

    /**
     * Tests a card if it can be placed on the given stack.
     *
     * @param stack The destination of the card
     * @param card  The card to test
     * @return True if it can placed, false otherwise
     */
    abstract public boolean cardTest(Stack stack, Card card);

    /**
     * Tests if the card can be added to the movement to place on another stack.
     * Games have to implement this method.
     *
     * @param card The card to test
     * @return True if it can be added, false otherwise
     */
    abstract public boolean addCardToMovementGameTest(Card card);

    /**
     * Checks every card of the game, if one can be moved as a hint.
     *
     * @param visited List of cards, which are already shown as hint
     * @return The card and the destination
     */
    abstract public CardAndStack hintTest(ArrayList<Card> visited);

    /**
     * Uses the given card and the movement (given as the stack id's) to update the current score.
     * <p>
     * CAUTION: If you only want to handle scoring, you don't need to think of the undo case. Undo movement
     * will this call normally but subtract the result from the current score. isUndoMovement is only useful
     * if you need to take care of other stuff
     *
     * @param cards          The moved cards
     * @param originIDs      The id's of the origin stacks
     * @param destinationIDs The id's of the destination stacks
     * @param isUndoMovement if set to true, the movement is called from a undo
     * @return The points to be added to the current score
     */
    abstract public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs, boolean isUndoMovement);

    /**
     * Put what happens on a main stack touch here, for example move a card to the discard stack.
     */
    abstract public int onMainStackTouch();

    public int mainStackTouch() {
        if (hasLimitedRecycles() && getDealStack().isEmpty() && discardStacksContainCards()) {
            if (getRemainingNumberOfRecycles() == 0) {
                return 0;
            } else {
                incrementRecycleCounter();
            }
        }

        int sound = onMainStackTouch();

        switch (sound) {
            case 1:     //single card moved
                sounds.playSound(Sounds.names.CARD_SET);
                break;
            case 2:     //moved cards back to mainstack
                sounds.playSound(Sounds.names.DEAL_CARDS);
                break;
            default:    //no cards moved
                break;
        }

        return sound;
    }

    private boolean discardStacksContainCards() {

        for (Stack stack : currentGame.getDiscardStacks()) {
            if (!stack.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Is the method a game needs to implement for the double tap test. Test where the given card
     * can be placed
     *
     * @param card The card to test
     * @return A destination, if the card can be moved, null otherwise
     */
    abstract Stack doubleTapTest(Card card);

    /**
     * Tests when a autocomplete can be started.
     *
     * @return True if the auto complete can be started, false otherwise
     */
    public boolean autoCompleteStartTest() {
        return false;
    }

    /**
     * Is the first phase of the autocomplete, which waits for a card movement to end, before
     * starting the next movement. Used for movements around the tableau
     *
     * @return A card and a destination stack if possible, null otherwise
     */
    public CardAndStack autoCompletePhaseOne() {
        return null;
    }

    /**
     * Is the second phase of the autocomplete, it doesnt wait for card movements to end, will be called
     * faster and faster until every card was moved.
     *
     * @return A card and a destination stack if possible, null otherwise
     */
    public CardAndStack autoCompletePhaseTwo() {
        return null;
    }

    public boolean saveRecentScore() {
        return false;
    }

    //stuff that games can override if necessary

    /**
     * Gets executed in onPause() of the gameManager, save stuff to sharedPrefs here, if necessary
     */
    public void save() {
    }

    /**
     * Gets executed on game starts, load stuff from sharedPrefs or set other values here, if necessary.
     */
    public void load() {
    }

    /**
     * Gets executed after a undo movement. I use it in Calculation-Game to update the text views
     * from the foundation stacks
     */
    public void afterUndo() {
    }

    /**
     * Does stuff on game reset. By default, it resets the recycle counter (if there is one).
     * If games need to reset additional stuff, put it here
     */
    @CallSuper
    public void reset() {
        if (hasLimitedRecycles) {
            recycleCounter = 0;
            recycleCounterCallback.updateTextView();
        }
    }

    /**
     * Tests if the main stack got touched. It iterates through all main stacks
     * (eg. Spider uses 5 main stacks). You can also override it like in Pyramid
     *
     * @param X The X-coordinate of the touch event
     * @param Y The Y-coordinate of the touch event
     * @return True if the main stack got touched, false otherwise
     */
    public boolean testIfMainStackTouched(float X, float Y) {
        for (int id : mainStackIDs) {
            if (stacks[id].isOnLocation(X, Y)) {
                return true;
            }
        }

        return false;
    }

    /**
     * If the game needs to execute code after every card movement, write it here
     */
    public void testAfterMove() {
    }

    /**
     * use this method to do something with the score, when the game is won or canceled (new game started)
     * So you can do other stuff for the high score list. For example, a game in Vegas is already won, when
     * the player makes profit, not only when all cards could be played on the foundation
     * <p>
     * Return false, if you want the  addNewScore() method to break, so possible high scores won't
     * be saved. (eg in Vegas, if the player keeps the current balance, only save high score when
     * the balance is resetting). Return false other wise (default)
     */
    public boolean processScore(long currentScore) {
        return true;
    }

    /**
     * Use this to add stuff to the statistics screen of the game, like longest run.
     * Save and load the data withing the game. It will be shown in a textView under the
     * "your win rate" text
     * IMPORTANT: Also implement deleteAdditionalStatisticsData() for reseting the data!
     * <p>
     *
     * @param res   The resources to get the string id's
     * @param title the view for the title of your data, eg "Longest run"
     * @param value the view for the value of the data
     * @return True, if you actually set something, false to ignore this method
     */
    public boolean setAdditionalStatisticsData(Resources res, TextView title, TextView value) {
        return false;
    }

    /**
     * Reset the additional statistics data, if there are any
     */
    public void deleteAdditionalStatisticsData() {
    }

    /*
     * gets called when starting a new game, or when a game is won
     */
    public void onGameEnd() {
    }

    /*
     * this method tests cards, if they are excluded from the card mixing function. (Eg. cards on the foundation)
     * You can override it to customise the behavior. Eg this method in the game Golf is empty, because no
     * cards should be excluded there
     */
    protected boolean excludeCardFromMixing(Card card) {
        Stack stack = card.getStack();

        if (!card.isUp()) {
            return false;
        }

        if (foundationStacksContain(stack.getId())) {
            return true;
        }

        //do not exclude anything, if the testMode is null
        if (mixCardsTestMode == null) {
            return false;
        }

        if (card.getIndexOnStack() == 0 && stack.getSize() == 1) {
            return false;
        }

        int indexToTest = card.getIndexOnStack() - (card.isTopCard() && stack.getSize() > 1 ? 1 : 0);

        return testCardsUpToTop(stack, indexToTest, mixCardsTestMode);
    }

    /**
     * Create a textView and add it to the given layout (game content). Used to add custom texts
     * to a game. This also sets the text apperance to AppCompat and the gravity to center.
     * The width and height is also measured, so you can use it directly.
     *
     * @param width   The width to apply to the
     * @param layout  he textView will be added to this layout
     * @param context Context to create view
     */
    protected void addTextViews(int count, int width, RelativeLayout layout, Context context) {

        for (int i = 0; i < count; i++) {
            TextView textView = new TextView(context);
            textView.setWidth(width);
            TextViewCompat.setTextAppearance(textView, R.style.TextAppearance_AppCompat);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(textViewColor);
            layout.addView(textView);
            textView.measure(0, 0);
            textViews.add(textView);
        }
    }

    /**
     * mirrors the textViews, if there are any. Used for left handed mode
     */
    public void mirrorTextViews(RelativeLayout layoutGame) {
        for (TextView textView : textViews) {
            textView.setX(layoutGame.getWidth() - textView.getX() - Card.width);
        }
    }

    /**
     * tests card from startPos to stack top if the cards are in the right order
     * (For example, first a red 10, then a black 9, then a red 8 and so on)
     * set mode to true if the card color has to alternate, false otherwise
     *
     * @param stack    The stack to test
     * @param startPos The start index of the cards to test
     * @param mode     Shows which order the colors should have
     * @return True if the cards are in the correct order, false otherwise
     */
    protected boolean testCardsUpToTop(Stack stack, int startPos, testMode mode) {

        for (int i = startPos; i < stack.getSize() - 1; i++) {
            Card bottomCard = stack.getCard(i);
            Card upperCard = stack.getCard(i + 1);

            if (!bottomCard.isUp() || !upperCard.isUp()) {
                return false;
            }

            switch (mode) {
                case ALTERNATING_COLOR:     //eg. black on red
                    if ((bottomCard.getColor() % 2 == upperCard.getColor() % 2) || (bottomCard.getValue() != upperCard.getValue() + 1)) {
                        return false;
                    }
                    break;
                case SAME_COLOR:            //eg. black on black
                    if ((bottomCard.getColor() % 2 != upperCard.getColor() % 2) || (bottomCard.getValue() != upperCard.getValue() + 1)) {
                        return false;
                    }
                    break;
                case SAME_FAMILY:           //eg spades on spades
                    if ((bottomCard.getColor() != upperCard.getColor()) || (bottomCard.getValue() != upperCard.getValue() + 1)) {
                        return false;
                    }
                    break;
                case DOESNT_MATTER:
                    if (bottomCard.getValue() != upperCard.getValue() + 1) {
                        return false;
                    }
                    break;
            }

        }

        return true;
    }

    /**
     * Sets the number of limited recycles for this game. Use -1 as the parameter to disable
     * the limited recycles.
     *
     * @param number The maximum number of recycles
     */
    protected void setLimitedRecycles(int number) {
        if (number >= 0) {
            hasLimitedRecycles = true;
            totalRecycles = number;
            hideRecycleCounter = number == 0;
        } else {
            hasLimitedRecycles = false;
        }
    }

    /**
     * Use this to set the cards width according to the last two values.
     * second last is for portrait mode, last one for landscape.
     * the game width will be divided by these values according to orientation to use as card widths.
     * Card height is 1.5*width and the dimensions are applied to every card and stack
     *
     * @param layoutGame     The layout, where the cards are located in
     * @param isLandscape    Shows if the phone is currently in landscape mode
     * @param portraitValue  The limiting number of card in the biggest row of the layout
     * @param landscapeValue The limiting number of cards in the biggest column of the layout
     */
    protected void setUpCardWidth(RelativeLayout layoutGame, boolean isLandscape, int portraitValue, int landscapeValue) {
        Card.width = isLandscape ? layoutGame.getWidth() / (landscapeValue) : layoutGame.getWidth() / (portraitValue);
        Card.height = (int) (Card.width * 1.5);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Card.width, Card.height);
        for (Card card : cards) card.view.setLayoutParams(params);
        for (Stack stack : stacks) stack.view.setLayoutParams(params);
    }

    // stuff that the games should use to set up other stuff

    /**
     * use this to automatically set up the dimensions (then the call of setUpCardWidth() isn't necessary).
     * It will take the layout, a value for width and a value for height. The values
     * represent the limiting values for the orientation. For example : There are 7 rows, so 7
     * stacks have to fit on the horizontal axis, but also 4 cards in the height. The method uses
     * these values to calculate the right dimensions for the cards, so everything fits fine on the screen
     *
     * @param layoutGame    The layout, where the cards are located in
     * @param cardsInRow    The limiting number of card in the biggest row of the layout
     * @param cardsInColumn The limiting number of cards in the biggest column of the layout
     */
    protected void setUpCardDimensions(RelativeLayout layoutGame, int cardsInRow, int cardsInColumn) {

        int testWidth1, testHeight1, testWidth2, testHeight2;

        testWidth1 = layoutGame.getWidth() / cardsInRow;
        testHeight1 = (int) (testWidth1 * 1.5);

        testHeight2 = layoutGame.getHeight() / cardsInColumn;
        testWidth2 = (int) (testHeight2 / 1.5);

        if (testHeight1 < testHeight2) {
            Card.width = testWidth1;
            Card.height = testHeight1;
        } else {
            Card.width = testWidth2;
            Card.height = testHeight2;
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Card.width, Card.height);
        for (Card card : cards) card.view.setLayoutParams(params);
        for (Stack stack : stacks) stack.view.setLayoutParams(params);
    }

    /**
     * Returns the calculated horizontal spacing for the layout. It takes the layout width minus the card widths,
     * then divides the remaining space with the divider. So the game can know how big the spaces are
     * between the card stacks for a good layout.
     *
     * @param layoutGame    The layout where the cards are located in.
     * @param numberOfCards The number of cards in a row
     * @param divider       The amount of spaces you want to have between the cards
     * @return The spacing value
     */
    protected int setUpHorizontalSpacing(RelativeLayout layoutGame, int numberOfCards, int divider) {
        return min(Card.width / 2, (layoutGame.getWidth() - numberOfCards * Card.width) / (divider));
    }

    /**
     * Returns the calculated vertical spacing for the layout. It takes the layout width minus the card widths,
     * then divides the remaining space with the divider. So the game can know how big the spaces are
     * between the card stacks for a good layout.
     *
     * @param layoutGame    The layout where the cards are located in.
     * @param numberOfCards The number of cards in a row
     * @param divider       The amount of spaces you want to have between the cards
     * @return The spacing value
     */
    protected int setUpVerticalSpacing(RelativeLayout layoutGame, int numberOfCards, int divider) {
        return min(Card.width / 2, (layoutGame.getHeight() - numberOfCards * Card.height) / (divider));
    }

    /**
     * Sets up the number of decks used by the game. One deck contains 52 cards, so the game can use
     * a multiple of this.
     *
     * @param number The number of decks to apply
     */
    protected void setNumberOfDecks(int number) {
        cards = new Card[52 * number];
        gameLogic.randomCards = new Card[cards.length];
    }

    /**
     * Sets up how many stacks the game has.
     *
     * @param number The number to apply
     */
    protected void setNumberOfStacks(int number) {
        stacks = new Stack[number];
    }

    /**
     * Sets the given stack ids as the main stacks, also sets the first one as the dealing stack.
     *
     * @param IDs The stack ids to apply.
     */
    protected void setMainStackIDs(int... IDs) {
        hasMainStacks = true;
        mainStackIDs = IDs;
        dealFromID = IDs[0];
        firstMainStackID = dealFromID;
    }

    /**
     * Sets the given stack ids as the foundation stacks
     *
     * @param IDs The stack ids to apply.
     */
    protected void setFoundationStackIDs(int... IDs) {
        hasFoundationStacks = true;
        lastFoundationID = IDs[IDs.length - 1];
    }

    /**
     * Sets the given stack ids as the tableau stacks
     *
     * @param IDs The stack ids to apply.
     */
    protected void setTableauStackIDs(int... IDs) {
        lastTableauID = IDs[IDs.length - 1];
    }

    /**
     * Sets the given stack ids as discard stacks.
     *
     * @param IDs The stack ids to apply.
     */
    protected void setDiscardStackIDs(int... IDs) {
        discardStackIDs = IDs;
        firstDiscardStackID = IDs[0];
        hasDiscardStacks = true;
    }

    /**
     * Sets a stack id to the dealing stack. Used if there is no main stack.
     *
     * @param id The stack id to apply.
     */
    protected void setDealFromID(int id) {
        dealFromID = id;
    }

    protected void disableMainStack() {
        mainStackIDs = new int[]{-1};
        hasMainStacks = false;
    }

    /**
     * Set the direction, in which the cards on the stack should be stacked. The parameter is an
     * int list to have shorter call of the method
     *
     * @param newDirections The list of directions to be applied
     */
    protected void setDirections(int... newDirections) {
        directions = new Stack.SpacingDirection[newDirections.length];

        for (int i = 0; i < newDirections.length; i++) {
            switch (newDirections[i]) {
                case 0:
                default:
                    directions[i] = Stack.SpacingDirection.NONE;
                    break;
                case 1:
                    directions[i] = Stack.SpacingDirection.DOWN;
                    break;
                case 2:
                    directions[i] = Stack.SpacingDirection.UP;
                    break;
                case 3:
                    directions[i] = Stack.SpacingDirection.LEFT;
                    break;
                case 4:
                    directions[i] = Stack.SpacingDirection.RIGHT;
                    break;
            }
        }
    }

    protected void setDirectionBorders(int... stackIDs) {
        directionBorders = stackIDs;
    }

    /**
     * Sets the background of a stack to an arrow (left handed mode will reverse the direction)
     *
     * @param stack The stack to apply
     */
    protected void setArrow(Stack stack) {
        hasArrow = true;
        stack.setArrow(Stack.ArrowDirection.LEFT);
    }

    /**
     * Sets the card families. So the games can set for example every family to be Spades, used
     * for easier difficulties. Values go from 1 to 4
     *
     * @param p1 Color for the first family
     * @param p2 Color for the second family
     * @param p3 Color for the third family
     * @param p4 Color for the fourth family
     */
    protected void setCardFamilies(int p1, int p2, int p3, int p4) throws ArrayIndexOutOfBoundsException {
        if (p1 < 1 || p2 < 1 || p3 < 1 || p4 < 1 || p1 > 4 || p2 > 4 || p3 > 4 || p4 > 4) {
            throw new ArrayIndexOutOfBoundsException("Card families can be between 1 and 4");
        }

        cardDrawablesOrder = new int[]{p1, p2, p3, p4};
    }

    /**
     * Tests if the given card is above the same card as the top card on the other stack.
     * "Same card" means same value and depending on the mode: Same color or same family.
     *
     * @param card       The card to test
     * @param otherStack The stack to test
     * @param mode       Shows which color the other card should have
     * @return True if it is the same card (under the given conditions), false otherwise
     */
    public boolean sameCardOnOtherStack(Card card, Stack otherStack, testMode2 mode) {
        Stack origin = card.getStack();

        if (card.getIndexOnStack() > 0 && origin.getCard(card.getIndexOnStack() - 1).isUp() && otherStack.getSize() > 0) {
            Card cardBelow = origin.getCard(card.getIndexOnStack() - 1);

            if (mode == SAME_VALUE_AND_COLOR) {
                return cardBelow.getValue() == otherStack.getTopCard().getValue() && cardBelow.getColor() % 2 == otherStack.getTopCard().getColor() % 2;
            } else if (mode == SAME_VALUE_AND_FAMILY) {
                return cardBelow.getValue() == otherStack.getTopCard().getValue() && cardBelow.getColor() == otherStack.getTopCard().getColor();
            } else if (mode == SAME_VALUE) {
                return cardBelow.getValue() == otherStack.getTopCard().getValue();
            }
        }

        return false;
    }

    public boolean movementDoneRecently(Card card, Stack destination) {
        for (int i = recordList.entries.size() - 1; i >= recordList.entries.size() - 5 && i > 0; i--) {
            RecordList.Entry entry = recordList.entries.get(i);

            for (int j = 0; j < entry.getCurrentCards().size(); j++) {
                Card cardInList = entry.getCurrentCards().get(j);
                Stack originInList = entry.getCurrentOrigins().get(j);

                if (card == cardInList && destination == originInList) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Applies the direction borders, which were set using setDirectionBorders().
     * This will be automatically called when a game starts.
     *
     * @param layoutGame Used to set the border according to the screen dimensions
     */
    public void applyDirectionBorders(RelativeLayout layoutGame) {
        if (directionBorders != null) {
            for (int i = 0; i < directionBorders.length; i++) {
                if (directionBorders[i] != -1)    //-1 means no border
                    stacks[i].setSpacingMax(directionBorders[i]);
                else stacks[i].setSpacingMax(layoutGame);
            }
        } else {
            for (Stack stack : stacks) {
                stack.setSpacingMax(layoutGame);
            }
        }
    }

    /*
     * If no card could be found, try to move the longest correct sequence from the stacks to
     * an empty one.
     */
    protected CardAndStack findBestSequenceToMoveToEmptyStack(testMode mode) {

        Card cardToMove = null;
        int sequenceLength = 0;
        Stack emptyStack = null;

        //find an empty stack to move to.
        for (int i = 0; i < 10; i++) {
            if (stacks[i].isEmpty()) {
                emptyStack = stacks[i];
            }
        }

        if (emptyStack == null) {
            return null;
        }

        for (int i = 0; i < 10; i++) {
            Stack sourceStack = stacks[i];

            if (sourceStack.isEmpty() || foundationStacksContain(i)) {
                continue;
            }

            for (int j = sourceStack.getFirstUpCardPos(); j < sourceStack.getSize(); j++) {
                if (testCardsUpToTop(sourceStack, j, mode)) {
                    Card card = sourceStack.getCard(j);

                    if (j != 0 && cardTest(emptyStack, card)) {
                        int length = sourceStack.getSize() - j;

                        if (length > sequenceLength) {
                            cardToMove = card;
                            sequenceLength = length;
                        }
                    }

                    break;
                }

            }
        }

        if (cardToMove != null && !movementDoneRecently(cardToMove, emptyStack)) {
            return new CardAndStack(cardToMove, emptyStack);
        }

        return null;
    }

    protected int getPowerMoveCount(int[] cellIDs, int[] stackIDs, boolean movingToEmptyStack) {
        //thanks to matejx for providing this formula
        int numberOfFreeCells = 0;
        int numberOfFreeTableauStacks = 0;

        for (int id : cellIDs) {
            if (stacks[id].isEmpty()) {
                numberOfFreeCells++;
            }
        }

        for (int id : stackIDs) {
            if (stacks[id].isEmpty()) {
                numberOfFreeTableauStacks++;
            }
        }

        if (movingToEmptyStack && numberOfFreeTableauStacks > 0) {
            numberOfFreeTableauStacks--;
        }

        return (numberOfFreeCells + 1) * (1 << numberOfFreeTableauStacks);
    }

    /**
     * Little overload method to not need to specify wrap, so it's set to false.
     * <p>
     * See the other canCardBePlaced() method below this one.
     */
    protected boolean canCardBePlaced(Stack stack, Card card, testMode mode, testMode3 direction) {
        return canCardBePlaced(stack, card, mode, direction, false);
    }

    /**
     * Little method to test if a given card can be placed on the given stack.
     * <p>
     * Use the other canCardBePlaced() method to not explicitly specify wrap, so it's default set to false
     *
     * @param stack     The destination stack
     * @param card      The card to move
     * @param mode      Which color the cards should have
     * @param direction which direction the cards are played
     * @param wrap      set to true if an ace can be placed on a king (ascending) or vice versa(descending)
     * @return true if the card can be placed on the stack, false otherwise
     */
    protected boolean canCardBePlaced(Stack stack, Card card, testMode mode, testMode3 direction, boolean wrap) {

        if (stack.isEmpty()) {
            return true;
        }

        int topCardColor = stack.getTopCard().getColor();
        int topCardValue = stack.getTopCard().getValue();
        int cardColor = card.getColor();
        int cardValue = card.getValue();

        if (direction == testMode3.DESCENDING) {   //example move a 8 on top of a 9
            switch (mode) {
                case SAME_COLOR:
                    return topCardColor % 2 == cardColor % 2 && (topCardValue == cardValue + 1 || (wrap && topCardValue == 1 && cardValue == 13));
                case ALTERNATING_COLOR:
                    return topCardColor % 2 != cardColor % 2 && (topCardValue == cardValue + 1 || (wrap && topCardValue == 1 && cardValue == 13));
                case SAME_FAMILY:
                    return topCardColor == cardColor && (topCardValue == cardValue + 1 || (wrap && topCardValue == 1 && cardValue == 13));
                case DOESNT_MATTER:
                    return topCardValue == cardValue + 1 || (wrap && topCardValue == 1 && cardValue == 13);
            }
        } else {                                //example move a 9 on top of a 8
            switch (mode) {
                case SAME_COLOR:
                    return topCardColor % 2 == cardColor % 2 && (topCardValue == cardValue - 1 || (wrap && topCardValue == 13 && cardValue == 1));
                case ALTERNATING_COLOR:
                    return topCardColor % 2 != cardColor % 2 && (topCardValue == cardValue - 1 || (wrap && topCardValue == 13 && cardValue == 1));
                case SAME_FAMILY:
                    return topCardColor == cardColor && (topCardValue == cardValue - 1 || (wrap && topCardValue == 13 && cardValue == 1));
                case DOESNT_MATTER:
                    return topCardValue == cardValue - 1 || (wrap && topCardValue == 1 && cardValue == 13);
            }
        }

        return false; //can't be reached
    }

    public Stack getMainStack() throws ArrayIndexOutOfBoundsException {
        if (mainStackIDs[0] == -1) {
            throw new ArrayIndexOutOfBoundsException("No main stack specified");
        }

        return stacks[mainStackIDs[0]];
    }

    public int getLastTableauId() throws ArrayIndexOutOfBoundsException {
        if (lastTableauID == -1) {
            throw new ArrayIndexOutOfBoundsException("No last tableau stack specified");
        }

        return lastTableauID;
    }

    public Stack getLastTableauStack() throws ArrayIndexOutOfBoundsException {
        if (lastTableauID == -1) {
            throw new ArrayIndexOutOfBoundsException("No last tableau stack specified");
        }

        return stacks[lastTableauID];
    }

    public void setNumberOfRecycles(String key, String defaultValue) {
        int recycles = prefs.getSavedNumberOfRecycles(key, defaultValue);
        setLimitedRecycles(recycles);

        if (recycleCounterCallback != null) {
            recycleCounterCallback.updateTextView();
        }
    }

    protected void disableBonus() {
        bonusEnabled = false;
    }

    protected void setPointsInDollar() {
        pointsInDollar = true;
    }

    protected void setUndoCosts(int costs) {
        undoCosts = costs;
    }

    protected void setHintCosts(int costs) {
        hintCosts = costs;
    }


    //some getters,setters and simple methods, games should'nt override these
    public Stack getDiscardStack() throws ArrayIndexOutOfBoundsException {
        if (firstDiscardStackID == -1) {
            throw new ArrayIndexOutOfBoundsException("No discard stack specified");
        }

        return stacks[firstDiscardStackID];
    }

    public ArrayList<Stack> getDiscardStacks() throws ArrayIndexOutOfBoundsException {
        ArrayList<Stack> discardStacks = new ArrayList<>();

        for (int id : discardStackIDs) {
            if (id == -1) {
                throw new ArrayIndexOutOfBoundsException("No discard stack specified");
            }

            discardStacks.add(stacks[id]);
        }

        return discardStacks;
    }

    protected void setLastTableauID(int id) {
        lastTableauID = id;
    }

    public boolean hasMainStack() {
        return hasMainStacks;
    }

    public Stack getDealStack() {
        return stacks[dealFromID];
    }

    public boolean hasDiscardStack() {
        return hasDiscardStacks;
    }

    public boolean hasLimitedRecycles() {
        return hasLimitedRecycles;
    }

    public boolean hasFoundationStacks() {
        return hasFoundationStacks;
    }

    public int getRemainingNumberOfRecycles() {
        int remaining = totalRecycles - recycleCounter;

        return remaining > 0 ? remaining : 0;
    }

    public void incrementRecycleCounter() {
        recycleCounter++;
        recycleCounterCallback.updateTextView();
    }

    public void decrementRecycleCounter() {
        recycleCounter--;
        recycleCounterCallback.updateTextView();
    }

    public void saveRecycleCount() {
        prefs.saveRedealCount(recycleCounter);
    }

    public void loadRecycleCount() {
        recycleCounter = prefs.getSavedRecycleCounter(totalRecycles);
        recycleCounterCallback.updateTextView();
    }

    public boolean hasArrow() {
        return hasArrow;
    }

    public void toggleRecycles(boolean value) {
        hasLimitedRecycles = value;
    }

    public void setSingleTapEnabled() {
        singleTapEnabled = true;
    }

    public boolean isSingleTapEnabled() {
        return singleTapEnabled && prefs.getSavedSingleTapSpecialGames();
    }

    public void flipAllCardsUp() {
        for (Card card : cards)
            card.flipUp();
    }

    public boolean isBonusEnabled() {
        return bonusEnabled;
    }

    public boolean isPointsInDollar() {
        return pointsInDollar;
    }

    public int getUndoCosts() {
        return undoCosts;
    }

    public int getHintCosts() {
        return hintCosts;
    }

    public enum testMode {
        SAME_COLOR, ALTERNATING_COLOR, DOESNT_MATTER, SAME_FAMILY
    }

    public enum testMode2 {
        SAME_VALUE_AND_COLOR, SAME_VALUE_AND_FAMILY, SAME_VALUE
    }

    protected enum testMode3 {
        ASCENDING, DESCENDING
    }

    public boolean mainStacksContain(int id) {
        return hasMainStack() && id >= firstMainStackID;
    }

    public boolean discardStacksContain(int id) {
        return hasDiscardStack() && id >= firstDiscardStackID && id < firstMainStackID;
    }

    public boolean hidesRecycleCounter() {
        return hideRecycleCounter;
    }

    public boolean tableauStacksContain(int ID) {
        return ID <= getLastTableauId();
    }

    public boolean foundationStacksContain(int ID) {
        return hasFoundationStacks && ID > getLastTableauId() && ID <= getLastFoundationID();
    }

    public int getLastFoundationID() {
        return lastFoundationID;
    }

    public boolean addCardToMovementTest(Card card) {
        return prefs.isDeveloperOptionPlayEveryCardEnabled() || addCardToMovementGameTest(card);
    }

    protected void setMixingCardsTestMode(testMode mode) {
        mixCardsTestMode = mode;
    }

    public int getMainStackId() {
        return mainStackIDs[0];
    }

    public void setRecycleCounterCallback(RecycleCounterCallback callback) {
        recycleCounterCallback = callback;
    }

    protected void textViewSetText(int index, String text) {
        if (!stopUiUpdates) {
            textViews.get(index).setText(text);
        }
    }

    protected void textViewPutAboveStack(int index, Stack stack) {
        textViews.get(index).setX(stack.getX());
        textViews.get(index).setY(stack.getY() - textViews.get(index).getMeasuredHeight());
    }

    public void textViewSetColor(int color) {
        textViewColor = color;

        for (TextView view : textViews) {
            view.setTextColor(color);
        }
    }

    public interface RecycleCounterCallback {
        void updateTextView();

    }

    public CardAndStack hintTest() {
        ArrayList<Card> emptyList = new ArrayList<>(3);

        return hintTest(emptyList);
    }

    public void setOffScreenStack() {
        offScreenStack.setX(-2 * Card.width);
        offScreenStack.setY(-2 * Card.height);
    }
}

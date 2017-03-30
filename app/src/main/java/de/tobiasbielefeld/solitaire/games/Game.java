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

import android.widget.RelativeLayout;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Abstract class for all the games. See the DUMMY GAME for detailed explanation of everything!
 * (And of course the javadoc comments)
 */

public abstract class Game {

    final static protected boolean SAME_COLOR = false;
    final static protected boolean ALTERNATING_COLOR = true;

    final static protected int SAME_VALUE_AND_COLOR = 0;
    final static protected int SAME_VALUE_AND_FAMILY = 1;

    public int[] cardDrawablesOrder = new int[]{1, 2, 3, 4};
    public Stack.SpacingDirection[] directions;
    public int[] directionBorders;
    private boolean hasMainStack = false;
    private int dealFromID = -1;
    private int mainStackID = -1;
    private boolean hasDiscardStack = false;
    private boolean hasLimitedRedeals = false;
    private int discardStackID = -1;
    private int lastTableauID = -1;
    private int redealCounter = 0;
    private int totalRedeals = 0;
    private boolean hasArrow = false;
    private boolean singleTapeEnabled = false;

    // some methods used by other classes

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
     * @param layoutGame The layout, where the stacks and cards are showed in. Used to calculate
     *                   the widht/height
     * @param isLandscape Shows if the screen is in landscape mode, so the games can set up
     *                    different layouts for this
     */
    abstract public void setStacks(RelativeLayout layoutGame, boolean isLandscape);

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
     * @param card The card to test
     * @return True if it can placed, false otherwise
     */
    abstract public boolean cardTest(Stack stack, Card card);

    /**
     * Tests if the card can be added to the movement to place on another stack.
     *
     * @param card The card to test
     * @return True if it can be added, false otherwise
     */
    abstract public boolean addCardToMovementTest(Card card);

    /**
     * Checks every card of the game, if one can be moved as a hint.
     *
     * @return The card and the destination
     */
    abstract public CardAndStack hintTest();

    /**
     * Uses the given card and the movement (given as the stack id's) to update the current score.
     *
     * @param cards The moved cards
     * @param originIDs The id's of the origin stacks
     * @param destinationIDs The id's of the destination stacks
     * @return The points to be added to the current score
     */
    abstract public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs);

    /**
     * Put what happens on a main stack touch here, for example move a card to the discard stack.
     */
    abstract public void onMainStackTouch();

    /**
     * Is the method a game needs to implement for the double tap test. Test where the given card
     * can be placed
     *
     * @param card The card to test
     * @return  A destination, if the card can be moved, null otherwise
     */
    abstract Stack doubleTapTest(Card card);

    //stuff that games can override if necessary

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
     * @return  A card and a destination stack if possible, null otherwise
     */
    public CardAndStack autoCompletePhaseTwo() {
        return null;
    }

    /**
     * Gets executed in onPause() of the gameManager, save stuff to sharedPrefs here, if necessary
     */
    public void save() {
    }

    /**
     * Gets executed on game starts, load stuff from sharedPrefs here, if necessary.
     */
    public void load() {
    }

    /**
     * Does stuff on game reset. By default, it resetes the redeal counter (if there is one).
     * If games need to reset additional stuff, put it here
     *
     * @param gm A reference to the game manager, to update the ui redeal counter
     */
    public void reset(GameManager gm) {
        if (hasLimitedRedeals) {
            redealCounter = 0;

            gm.updateNumberOfRedeals();
        }
    }

    /**
     * Tests if the main stack got touched. It can be overriden if there are for example
     * multiple main stacks, like in Spider
     *
     * @param X The X-coordinate of the touch event
     * @param Y The Y-coordinate of the touch event
     * @return True if the main stack got touched, false otherwise
     */
    public boolean testIfMainStackTouched(float X, float Y) {
        return getMainStack().isOnLocation(X, Y);
    }

    /**
     * If the game needs to execute code after every card movement, write it here
     */
    public void testAfterMove() {
    }

    // stuff that the games should use to set up other stuff

    /**
     * tests card from startPos to stack top if the cards are in the right order
     * (For example, first a red 10, then a black 9, then a red 8 and so on)
     * set mode to true if the card color has to alternate, false otherwise
     *
     * @param stack The stack to test
     * @param startPos The start index of the cards to test
     * @param mode True means alternating color, false means same color
     * @return True if the cards are in the correct order, false otherwiese
     */
    protected boolean testCardsUpToTop(Stack stack, int startPos, boolean mode) {


        for (int i = startPos; i < stack.getSize() - 1; i++) {
            Card bottomCard = stack.getCard(i);
            Card upperCard = stack.getCard(i + 1);

            if (mode == ALTERNATING_COLOR) {  //alternating color
                if ((bottomCard.getColor() % 2 == upperCard.getColor() % 2) || (bottomCard.getValue() != upperCard.getValue() + 1))
                    return false;
            } else {    //same color
                if ((bottomCard.getColor() != upperCard.getColor()) || (bottomCard.getValue() != upperCard.getValue() + 1))
                    return false;
            }

        }

        return true;
    }

    /**
     * Sets the number of limited redeals for this game. Use a zero as the parameter to disable
     * the limited redeals.
     *
     * @param number The maximum number of redeals
     */
    protected void setLimitedRedeals(int number) {
        if (number >= 0) {
            hasLimitedRedeals = true;
            totalRedeals = number;
        } else {
            hasLimitedRedeals = false;
        }
    }

    /**
     * Use this to set the cards width according to the last two values.
     * second last is for portrait mode, last one for landscape.
     * the game width will be divided by these values according to orientation to use as card widths.
     * Card height is 1.5*width and the dimensions are applied to every card and stack
     *
     * @param layoutGame The layout, where the cards are located in
     * @param isLandscape Shows if the phone is currently in landscape mode
     * @param portraitValue The limiting number of card in the biggest row of the layout
     * @param landscapeValue The limiting number of cards in the biggest column of the layout
     */
    protected void setUpCardWidth(RelativeLayout layoutGame, boolean isLandscape, int portraitValue, int landscapeValue) {
        Card.width = isLandscape ? layoutGame.getWidth() / (landscapeValue) : layoutGame.getWidth() / (portraitValue);
        Card.height = (int) (Card.width * 1.5);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Card.width, Card.height);
        for (Card card : cards) card.view.setLayoutParams(params);
        for (Stack stack : stacks) stack.view.setLayoutParams(params);
    }

    /**
     * use this to automatically set up the dimensions (then the call of setUpCardWidth() isn't necessary).
     * It will take the layout, a value for width and a value for height. The values
     * represent the limiting values for the orientation. For example : There are 7 rows, so 7
     * stacks have to fit on the horizontal axis, but also 4 cards in the height. The method uses
     * these values to calculate the right dimensions for the cards, so everything fits fine on the screen
     *
     * @param layoutGame The layout, where the cards are located in
     * @param portraitValue The limiting number of card in the biggest row of the layout
     * @param landscapeValue The limiting number of cards in the biggest column of the layout
     */
    protected void setUpCardDimensions(RelativeLayout layoutGame, int portraitValue, int landscapeValue) {

        int testWidth1, testHeight1, testWidth2, testHeight2;

        testWidth1 = layoutGame.getWidth() / portraitValue;
        testHeight1 = (int) (testWidth1 * 1.5);

        testHeight2 = layoutGame.getHeight() / landscapeValue;
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
     * Returns the calculated spacing for the layout. It takes the layout width minus the card widths,
     * then divides the remaining space with the divider. So the game can know how big the spaces are
     * between the card stacks for a good layout.
     *
     * @param layoutGame The layout where the cards are located in.
     * @param numberOfCards The number of cards in a row
     * @param divider The amount of spaces you want to have between the cards
     * @return The spacing value
     */
    protected int setUpSpacing(RelativeLayout layoutGame, int numberOfCards, int divider) {
        return min(Card.width / 2, (layoutGame.getWidth() - numberOfCards * Card.width) / (divider));
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
     * Sets the given stack id as the first main stack, also sets it as the dealing stack.
     * Every stack with this id and above will be treated as a main stack
     *
     * @param id The stack id to apply.
     */
    protected void setFirstMainStackID(int id) {
        hasMainStack = true;
        mainStackID = id;
        dealFromID = id;
    }

    /**
     * Sets the given stack id as the first discard stack.
     * Every stack with this id and above, but below the main stack id's will be treated as a discard stack.
     *
     * @param id The stack id to apply.
     */
    protected void setFirstDiscardStackID(int id) {
        hasDiscardStack = true;
        discardStackID = id;
    }

    /**
     * Sets a stack id to the dealing stack. Used if there is no main stack.
     *
     * @param id The stack id to apply.
     */
    protected void setDealFromID(int id) {
        dealFromID = id;
    }

    /**
     * Set the direction, in which the cards on the stack should be stacked. The parameter is an
     * int list to have shorter call of the method
     * @param newDirections The list of directions to be applied
     */
    protected void setDirections(int... newDirections) {
        directions = new Stack.SpacingDirection[newDirections.length];

        for (int i=0;i<newDirections.length;i++){
            switch (newDirections[i]){
                case 0:default:
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
     * @param direction The default direction of the arrow LEFT or RIGHT
     */
    protected void setArrow(Stack stack, Stack.ArrowDirection direction) {
        hasArrow = true;
        stack.setArrow(direction);
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
    protected void setCardFamilies(int p1, int p2, int p3, int p4) throws ArrayIndexOutOfBoundsException{
        if (p1<1 || p2<1 || p3<1 || p4<1 || p1>4 || p2>4 || p3>4 || p4>4){
            throw new ArrayIndexOutOfBoundsException("Card families can be between 1 and 4");
        }

        cardDrawablesOrder = new int[]{p1, p2, p3, p4};
    }

    /**
     * Tests if the given card is above the same card as the top card on the other stack.
     * "Same card" means same value and depending on the mode: Same color or same family.
     *
     * @param card The card to test
     * @param otherStack The stack to test
     * @param mode True means same value and color, False means Same value and family
     * @return True if it is the same card (under the given conditions), false otherwise
     */
    protected boolean sameCardOnOtherStack(Card card, Stack otherStack, int mode) {
        Stack origin = card.getStack();

        if (card.getIndexOnStack() > 0 && origin.getCard(card.getIndexOnStack() - 1).isUp() && otherStack.getSize() > 0) {
            Card cardBelow = origin.getCard(card.getIndexOnStack() - 1);

            if (mode == SAME_VALUE_AND_COLOR) {
                if (cardBelow.getValue() == otherStack.getTopCard().getValue() && cardBelow.getColor() % 2 == otherStack.getTopCard().getColor() % 2)
                    return true;
            } else if (mode == SAME_VALUE_AND_FAMILY) {
                if (cardBelow.getValue() == otherStack.getTopCard().getValue() && cardBelow.getColor() == otherStack.getTopCard().getColor())
                    return true;
            }
        }

        return false;
    }

    //some getters,setters and simple methods, games should'nt override these

    public Stack getMainStack() throws ArrayIndexOutOfBoundsException{
        if (mainStackID == -1) {
            throw new ArrayIndexOutOfBoundsException("No main stack specified");
        }

        return stacks[mainStackID];
    }

    public int getLastTableauID() throws ArrayIndexOutOfBoundsException{
        if (lastTableauID == -1) {
            throw new ArrayIndexOutOfBoundsException("No last tableau stack specified");
        }

        return lastTableauID;
    }

    public Stack getLastTableauStack() throws ArrayIndexOutOfBoundsException{
        if (lastTableauID == -1) {
            throw new ArrayIndexOutOfBoundsException("No last tableau stack specified");
        }

        return stacks[lastTableauID];
    }

    public Stack getDiscardStack() throws ArrayIndexOutOfBoundsException{
        if (discardStackID == -1) {
            throw new ArrayIndexOutOfBoundsException("No discard stack specified");
        }

        return stacks[discardStackID];
    }

    protected void setLastTableauID(int id) {
        lastTableauID = id;
    }

    public boolean hasMainStack() {
        return hasMainStack;
    }

    public Stack getDealStack() {
        return stacks[dealFromID];
    }

    public boolean hasDiscardStack() {
        return hasDiscardStack;
    }

    public boolean hasLimitedRedeals() {
        return hasLimitedRedeals;
    }

    public int getRemainingNumberOfRedeals() {
        return totalRedeals - redealCounter;
    }

    public void incrementRedealCounter(GameManager gm) {
        redealCounter++;
        gm.updateNumberOfRedeals();
    }

    public void decrementRedealCounter(GameManager gm) {
        redealCounter--;
        gm.updateNumberOfRedeals();
    }

    public void saveRedealCount() {
        putInt(GAME_REDEAL_COUNT, redealCounter);
    }

    public void loadRedealCount(GameManager gm) {
        redealCounter = getInt(GAME_REDEAL_COUNT, totalRedeals);
        gm.updateNumberOfRedeals();
    }

    public boolean hasArrow() {
        return hasArrow;
    }

    public void toggleRedeals() {
        hasLimitedRedeals = !hasLimitedRedeals;
    }

    public void setSingleTapeEnabled(boolean value){
        singleTapeEnabled = value;
    }

    public boolean isSingleTapEnabled(){
        return singleTapeEnabled;
    }
}

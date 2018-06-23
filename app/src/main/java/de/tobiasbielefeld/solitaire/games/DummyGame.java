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
import android.widget.RelativeLayout;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.classes.Stack.ArrowDirection.LEFT;
import static de.tobiasbielefeld.solitaire.games.Game.testMode.ALTERNATING_COLOR;
import static de.tobiasbielefeld.solitaire.games.Game.testMode.SAME_FAMILY;
import static de.tobiasbielefeld.solitaire.games.Game.testMode2.*;
import static de.tobiasbielefeld.solitaire.games.Game.testMode3.ASCENDING;
import static de.tobiasbielefeld.solitaire.games.Game.testMode3.DESCENDING;

/**
 * Dummy Game, so you can see what to do if you want to add more games
 * <p>
 * Notice:
 * don't use stack.getTopCard() on empty stacks! check with isEmpty() to avoid it
 * <p>
 * To add a new game, also include it here:
 * - in LoadGame class, it handles all the loading of a game
 * - in strings.xml as the shown name (use "games_" as a prefix, like the other games)
 * - add new button to fragment_manual_games.xml
 * - in strings-manual.xml add a new manual entry for the game
 * - add a entry to the dialog_menu_show_games.xml
 * - and of course, include a button in the activity_game_chooser.xml
 * <p>
 * The stacks array should be in this order:
 * - first the tableau stacks
 * - then the foundation stacks (if any)
 * - then the discard stacks (if any)
 * - at last the main stacks (if any)
 * <p>
 * because I use the ids to determinate if cards should flip
 * for example: Because the main stacks are always the last ones,
 * every card added to a stack with an id >= the firstMainStack id will be flipped down
 */

@SuppressWarnings("all")
public class DummyGame extends Game {

    /*
     *  Initialise stuff in the constructor!
     */
    public DummyGame() {
        //set how may cards you need. One deck contains 52 cards
        setNumberOfDecks(1);

        //set how many stacks you have, the array will be initialized with the given value
        setNumberOfStacks(13);

        //if you game has a main stack, set its id here. Cards will be automatically dealt from there.
        setMainStackIDs(12);

        //if there is a discard stack, set it's id, because I need the id for the undo movement
        //(cards should be faced up when returning to discard)
        setDiscardStackIDs(11);

        //if your game has NO main stack, set where the cards are dealt from
        setDealFromID(1);

        //set the tableau. All cards from stacks 0 to the last tableau stack
        //are stacked with an offset, so you can see every card on it.
        //Every card on a stack with a higher id will be put exactly on the stacks coordinates,
        //without an offset.
        setLastTableauID(6);

        //use this to set your card families in the following way:
        //1=clubs 2=hearts 3=spades 4=diamonds
        //if you don't call it, the default will be used: 1,2,3,4.
        //if you have more than one deck, the following decks are set the same way
        //for example: in Spider I want to set the difficulty: Easy would mean
        //every family is the same color, so I use this:
        setCardFamilies(1, 1, 1, 1);

        //you can set up how the cards on a stack are stacked. With an offset to the right, down and so on
        //Put an int array with values for each stack that should getHighScore a direction
        //int value at array pos 0 will be assigned to stack[0] and so on
        //tableau stacks are set to "down" by default, so you don't need to call this method for them
        //but if you use this method, don't forget to set the tableau stacks to "1" again
        //
        //the directions are:
        // 0 no visible offset (like the main stacks)
        // 1 down (like every tableau stack)
        // 2 up
        // 3 left (like the discard stack on Golf
        // 4 right (like the stack on golf in left handed mode
        setDirections(new int[]{1, 1, 1, 0, 0, 1, 1, 3});

        //use this if the cards on a stack should'nt overlap another stack (if the other stack is in the stacking direction.
        //Pass the id of the other stack in the array. A 1 on index 0 means, that stacks[0] should'nt overlap stacks[1].
        // A -1 stands for no border, so the border will be the screen width/height
        setDirectionBorders(new int[]{1, 1, 1, -1, -1});

        //sets an arrow as the background of a stack, use the constants LEFT and RIGHT for the direction.
        //it will automatically flip the direction, if left handed mode is enabled
        setArrow(stacks[1], LEFT);

        //if your game needs to have limited recycles, so after a few tries of moving the cards from
        //the discard stack back to the main stack, it won't be possible anymore, use this method
        //It will automatically show the remaining recycles on the main stack.
        setLimitedRecycles(5);

        //if you used setLimitedRecycles(), you can also use toggleRecycles like this to disable the limitation
        //from the settings
        /*if (!getSavedGAMENAMELimitedRecycles){        //add a method to Preferences.java for this
            toggleRecycles();
        } */
    }

    /*
     * METHODS YOU CAN USE: (I put them here, to not cause an Compiler error)
     */
    private void methodsYouCanUse() {

        //test the cards of a given stack from the gives index up to top if the cards are in the
        //right order. Use for the card order the constants SAME_FAMILIY, SAME_COLOR or ALTERNATING_COLOR
        testCardsUpToTop(stacks[5], 5, testMode.SAME_FAMILY);

        //used in movements: detect if the top card on the given stack is equal to the give card.
        //you can use this for example to test in a hint: do not show hints if the card on the other
        //stack has the same value/color, because that would be useless.
        //You can use the constants SAME_VALUE_AND_COLOR and SAME_VALUE_AND_FAMILY
        sameCardOnOtherStack(cards[5], stacks[2], SAME_VALUE_AND_COLOR);

        //use this
        canCardBePlaced(stacks[2], cards[2], ALTERNATING_COLOR, DESCENDING);

        canCardBePlaced(stacks[2], cards[2], SAME_FAMILY, ASCENDING);

        //BEFORE USING THESE METHODS:
        //you have to call setNumberOfDecks() in the constructor, or else the returned classes aren't
        //initialized yet

        //return the mainStack
        getMainStack();

        //return the last tableau stack or it's id
        getLastTableauId();
        getLastTableauStack();

        //return the stack, where cards are dealt from
        getDealStack();

        //return the discard stack
        getDiscardStack();
    }

    /*
     *  here you need to set the cards and stacks on the screen with the dimensions.
     *  You getHighScore the game layout to use its width and height and you getHighScore a boolean value
     *  to show if the phone is currently in landscape
     *
     *  Here is an example code you should follow
     */
    public void setStacks(RelativeLayout layoutGame, boolean isLandscape, Context context) {

        //use this to set the cards width according to last two values.
        //second last is for portrait mode, last one for landscape.
        //the layout width will be divided by these values according to orientation to use as card widths.
        //Card height is 1.5*widht and the dimensions are applied to every card and stack
        //
        //use values as +1, +2 added to the number of stacks in the longest row of your layout, so there is
        //enough space left to use as spacing between the stacks.
        setUpCardWidth(layoutGame, isLandscape, 7 + 1, 7 + 2);

        //use this to automatically set up the dimensions (then the call above isn't nessessary).
        //It will take the layout and a value for width and one value for height. The values
        //represent the limiting values for the orientation. For example here: There are 7 rows, so 7
        //stacks have to fit on the horizontal axis, but also 4 cards in the height. The method uses
        //these values to calculate the right dimensions for the cards, so everything fits fine on the screen
        setUpCardDimensions(layoutGame, 7, 4);

        //now we order the stacks on the field. First calculate a spacing variable, to know how much
        //space will be between the stacks. It just uses the layout width minus the number of stacks
        //in a row, divided with the number of spaces between the stacks (which should be the number
        //of stacks +1) It also uses a maximum value of Card.widht/2, so the cards won't be too far apart
        int spacing = setUpHorizontalSpacing(layoutGame, 7, 8);

        //now getHighScore the start position to place the stacks, so they are centered around the middle of
        //the screen. I use this way: Get the half of the layout width, minus how many stacks are on the
        //left to it times the card width, minus how many spacings are left to it times the spacing
        //width. (Do not use the spacing from the left screen edge to the first stack).
        //So it should look like this:
        int startPos = layoutGame.getWidth() / 2 - 3 * Card.width - 3 * spacing;
        //Then set the stack coordinates like this:
        //X cor is the start pos + loop index times (spacing + card width)
        //Y cor can be like in the example code. In landscape use a bit less spacing from the
        //screen edge. The +1 is only so Android Studio doesnt show an error
        for (int i = 0; i < 6; i++) {
            stacks[i].setX(startPos + i * (spacing + Card.width));
            stacks[i].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1);
        }
        //Also set other stacks like the main pile or something
        stacks[6].setX(stacks[0].getX());
        stacks[6].setY(stacks[0].getY() + Card.height + spacing);

        //Last step: Set the drawables of the stacks. Default one is just gray.
        //So maybe show on some a big A for ace or make them transparent or something
        stacks[6].setImageBitmap(Stack.background1);               //shows an A
        stacks[6].setImageBitmap(Stack.backgroundTalon);           //shows a circle arrow
        stacks[6].setImageBitmap(Stack.backgroundTransparent);     //no background at all
    }

    /*
     * Put your calculation to test if the game is won here.
     * It will be called on every card move. Return false for Not won, and true for won
     */
    public boolean winTest() {
        //For example on Klondike all foundation stacks have to be full, so everyone of them
        //needs to habe 13 cards. If not, game isn't won yet. If yes, game is won
        for (int i = 7; i <= 10; i++)
            if (stacks[i].getSize() != 13)
                return false;

        return true;
    }

    /*
     * Put how to deal cards here: All cards are set to the main stack or the dealStack, if set.
     * The cards will be faced down by default too, so flip them up if needed.
     */
    public void dealCards() {
        //Simple example: Deal the first card from the main stack to another one.
        //Use the OPTION_NO_RECORD, or else the player can undo this movement.
        //After that, flip the card up
        moveToStack(getDealStack().getTopCard(), stacks[0], OPTION_NO_RECORD);
        stacks[0].getTopCard().flipUp();
    }

    /*
     * tests if the mainstack got touched. For nearly every game it's the same, because they only have
     * one main stack. But Spider is a bit special, it has multiple main stacks. If your game has only
     * one main stack, don't override it
     */
    @Override
    public boolean testIfMainStackTouched(float X, float Y) {
        return currentGame.getMainStack().isOnLocation(X, Y);
    }

    /*
     * Put here what happens if the player touches the main stack, if there is any.
     * If there is no main stack, leave it empty
     */
    public int onMainStackTouch() {

        //first test the coordinates if they are really on the main stack.
        //(I put this test here because in Spider Solitaire, the cards from "main" are placed on
        // multiple stacks).
        if (getMainStack().getSize() > 0) {                                                         //if it has cards
            moveToStack(getMainStack().getTopCard(), getDiscardStack());                            //move the card to the discard stack
            return 1;
        }
        //if it empty, do something like move all cards from the discard pile to the main
        // Stack again. In this example from Klondike, the cards are moved in reversed order than
        //they would be movend with the moveToStack(), so I move them one by one and update
        //the recordList and score
        else if (stacks[11].getSize() != 0) {                                                       //if there are cards on stack11 which can be moved
            recordList.add(stacks[11].currentCards);                                                //save the record in normal order

            while (stacks[11].getSize() > 0)                                                        //then place the top card from stack11 to stack12 until it is empty
                moveToStack(stacks[11].getTopCard(), getMainStack(), OPTION_NO_RECORD);

            return 2;
        }

        return 0;
    }

    /*
     * Test a card if you can place it on a stack.
     * Tested every time if the player wants to place a card on some stack
     */
    public boolean cardTest(Stack stack, Card card) {
        //Example from Klondike.

        if (stack.getId() < 7) {                                                                    //tableau stacks
            if (stack.isEmpty()) {                                                                   //if it's empty, you can place a king
                return card.getValue() == 13;
            } else {
                return canCardBePlaced(stack, card, ALTERNATING_COLOR, DESCENDING);                 //Cards on the tableau can be placed in alternating color and descending order
            }
        } else if (stack.getId() < 11 && movingCards.hasSingleCard()) {                             //if its an foundation stack and only one card is moving
            if (stack.isEmpty()) {                                                                  //place if it's an ace
                return card.getValue() == 1;
            } else {
                return canCardBePlaced(stack, card, SAME_FAMILY, ASCENDING);                        //Cards on the foundation can be placed in same family and ascending order
            }
        } else
            return false;
    }

    /*
     * Test if the player can even pick up a card (faced down cards are never moved)
     * If yes, every card from the touched card to the stack top card will be moved
     */
    public boolean addCardToMovementGameTest(Card card) {
        //in case of Klondike it's easy: If a card is faced up, every card on top of it in the stack
        //has the correct order. So return true if a card is up. But because faced down cards aren't even
        //tested here, just return true
        return true;
    }

    /*
     * Called up to three times when pressing the hint button.
     * Test the cards if a hint can be shown
     * If so, return the card and then the destination stack.
     * If no card can be found, return null at the end of the method, so the hint will stop.
     *
     * Use hint.hasVisited(card) to getHighScore if the card has been visited, so it won't result in an endless loop
     */
    public CardAndStack hintTest(ArrayList<Card> visited) {
        //Short example from Klondike
        Card card;                                                                                  //card to test

        for (int i = 0; i <= 6; i++) {                                                              //loop through every stack on the tableau as origin

            Stack origin = stacks[i];                                                               //set the stack as origin

            if (origin.isEmpty() || !origin.getTopCard().isUp())                                    //continue if it's empty or no card is flipped up
                continue;

            /* last card of a stack to move to the foundation */
            card = origin.getTopCard();                                                             //in this part, getHighScore the top card of a stack

            if (!visited.contains(card)) {                                                          //if this card hasn't been visited
                for (int j = 7; j <= 10; j++) {                                                     //loop through every foundation stack as destination
                    if (card.test(stacks[j])) {                                                     //then test

                        return new CardAndStack(card, stacks[j]);
                    }
                }
            }

        }

        return null;
    }

    /*
    * test when to show the auto complete button.
    * return true if it can be shown, false otherwise.
    * It's called on every card movement
    *
    * don't override it, if there is no auto complete
    */
    public boolean autoCompleteStartTest() {
        //Example from Klondike: If every card is faced up, return true. Return false otherwiese
        for (int i = 0; i < 7; i++)
            if (stacks[i].getSize() > 0 && !stacks[i].getCard(0).isUp())
                return false;

        return true;
    }

    /*
     *  AutoComplete Phase One: Move cards around on the tableau. To the foundations is phase two.
     *  Because I need to wait until a card reaches the destination if the cards moves to a foundation field
     *  (or any field with visible spacing). Else the counter in animation isn't set right and the
     *  game doesn't respond anymore.
     *
     *  Return the card and the stack (or the id's of them) as a new 'CardAndStack'.
     *  This will move every card from the returned card up to the origin stack top
     *
     *  If you have a phase one movement, override it. else it returns null by default
     */
    public CardAndStack autoCompletePhaseOne() {
        //Example Code: Only game using this is Golf, because the discard stack is no foundation field,
        // the calculation has to be in phase one

        if (!getMainStack().isEmpty()) {
            getMainStack().getTopCard().flipUp();
            return new CardAndStack(getMainStack().getTopCard(), getDiscardStack());
        }

        return null;    //don't forget to return null at the end! so phase two can start
    }

    /*
     * AutoComplete Phase Two: Move cards to the foundation field (or stacks with no visible spacing)
     * The speed will increase with each movement (as a nice effect). So this is incompatible with tableau stacks
     *
     *  Return the card and the stack (or the id's of them) as a new 'CardAndStack'.
     *  This will only move the returned card to the destination
     *
     *  If you have a phase two movement, override it. else it returns null by default
     */
    public CardAndStack autoCompletePhaseTwo() {
        for (int i = 7; i <= 10; i++) {                                                             //foundation fields
            Stack destination = stacks[i];                                                          //getHighScore the destination for more visibility

            for (int j = 0; j <= 6; j++) {                                                          //tableau fields
                Stack origin = stacks[j];                                                           //getHighScore the origin for more visibility

                if (origin.getSize() > 0 && origin.getTopCard().test(destination)) {                //test if there are still cards on it and if the card test is successful
                    return new CardAndStack(origin.getTopCard(), destination);                      //and return
                }
            }

            for (int j = 11; j <= 12; j++) {                                                        //stock
                Stack origin = stacks[j];                                                           //getHighScore the origin for more visibility

                for (int k = 0; k < origin.getSize(); k++) {                                        //loop through every card
                    if (origin.getCard(k).test(destination)) {                                      //then test every card
                        origin.getCard(k).flipUp();                                                 //because cards are from stock, flip up
                        return new CardAndStack(origin.getCard(k), destination);
                    }
                }
            }
        }

        return null;  //don't forget to return null at the end! so the win animation can start
    }

    /*
     * Handle scoring here. It will be called every time a card or multiple cards are moved.
     * The method gets the array of cards which are moved, IDs of origin and destination stack and if its a single card movement as boolean.
     * Then test everything and return the points as integers. Return 0 at end for no update of score!
     *
     * Undo movements will invert the points, but also set -25 points for using undo
     */
    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs, boolean isUndoMovement) {
        int originID = originIDs[0];
        int destinationID = destinationIDs[0];

        if ((originID < 7 || originID == 14) && destinationID >= 7 && destinationID <= 10)          //transfer from tableau to foundations
            return 60;
        if ((originID == 11 || originID == 12 || originID == 13) && destinationID < 7)              //stock to tableau
            return 45;
        if (destinationID < 7 && originID >= 7 && originID <= 10)                                   //foundation to tableau
            return -75;
        if (originID == destinationID)                                                              //turn a card over
            return 25;
        if (originID >= 11 && originID < 14 && destinationID == 14)                                 //returning cards to stock
            return -200;

        return 0;
    }

    /*
     * Use this if you want to run something custom after every movement (right after the end
     * of the animation. For example in Spider, i have to test if a card family is complete
     */
    public void testAfterMove() {
    }

    /*
     *  use this if you need to reset something on a game start. Call super if your games
     *  has limited recycles to reset them automatically
     */
    public void reset() {
        super.reset();
    }


    /*
     * method for double tap movements. By default it will return null, so double tap is disabled.
     * this method will be called with every single card, until a non empty stack is returned (or
     * every card was tested) So it prefers non empty stacks over empty stacks. You can use
     * sameCardOnOtherStack() to prevent senseless movements.
     */
    public Stack doubleTapTest(Card card) {

        //example code from klondike

        //foundation stacks
        if (card.isTopCard()) {
            for (int j = 7; j < 11; j++) {
                if (card.test(stacks[j]))
                    return stacks[j];
            }
        }

        //tableau stacks
        for (int j = 0; j < 7; j++) {

            if (card.getStackId() < 7 && sameCardOnOtherStack(card, stacks[j], SAME_VALUE_AND_COLOR))
                continue;

            if (card.getValue() == 13 && card.isFirstCard() && card.getStackId() <= 6)
                continue;

            if (card.test(stacks[j])) {
                return stacks[j];
            }
        }

        //empty tableau stacks
        for (int j = 0; j < 7; j++) {
            if (stacks[j].isEmpty() && card.test(stacks[j]))
                return stacks[j];
        }

        return null;
    }

    /*
     * save and load values, to save custom stuff. save will be called in every onPause().
     * load will be called on a game start
     */
    public void save() {

    }

    public void load() {

    }


    //called after undo movement
    public void afterUndo(){
        //check stuff here
    }

    /*
     * This method controls the card mixing. It gets a card, checks if it should be excluded from
     * the mixing, in this case, return true. The standard way is, that faced down cards should
     * always be mixed, and cards on the foundation stack never. You can also exclude correct
     * sequences on the tableau or something like that.
     */
    @Override
    protected boolean excludeCardFromMixing(Card card) {
        /*
         * this is the content of the default method, you dont need to override it, if you don't
         * change anything
         */

        Stack stack = card.getStack();

        if (!card.isUp()) {
            return false;
        }

        if (foundationStacksContain(stack.getId())){
            return true;
        }

        return false;
    }
}

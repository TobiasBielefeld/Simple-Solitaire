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

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.Stack;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Dummy Game, so you can see what to do if you want to add more games
 *
 * Notice:
 * don't use stack.getTopCard() on empty stacks! check with isEmpty() to avoid it
 *
 * To add a new game, also include it (create a string in SharedData with the game name)
 *  - in GameChooser onClick() switch statements
 *  - in GameManager onCreate() switch statement
 *  - and of course, include a button in the activity_game_chooser.xml
 *
 *  The stacks array should be in this order:
 *  - first the tableau stacks
 *  - then the foundation stacks (if any)
 *  - then the discard stacks (if any)
 *  - at last the main stacks (if any)
 *
 *  because i use the ids to determinate if cards should flip
 *  for example: Because the main stacks are always the last ones,
 *  every card added to a stack with an ID greater than the FirstMainStack ID will be flipped down
 */

@SuppressWarnings("all")
public class DummyGame extends Game {

    /*
     *  Initialise stuff in the constructor!
     */
    public  DummyGame() {
        //set how may cards you need. One deck contains 52 cards
        setNumberOfDecks(1);

        //set how many stacks you have, the array will be initialized with the given value
        setNumberOfStacks(13);

        //if you game has a main stack, set its ID here. Cards will be automatically dealt from there.
        setFirstMainStackID(12);

        //if there is a discard stack, set it's ID, because i need the ID for the undo movement
        //(cards should be faced up when returning to discard)
        setFirstDiscardStackID(11);

        //if your game has NO main stack, set where the cards are dealt from
        setDealFromID(1);

        //set the tablues. All cards from stacks 0 to the last tableau stack
        //are stacked with an offset, so you can see every card on it.
        //Every card on a stack with a higher ID will be put exactly on the stacks coordinates,
        //without an offset.
        setLastTableauID(6);

        //use this to set your cards. Set each family in the following way:
        //1=clubs 2=hearts 3=spades 4=diamonds
        //if you don't call it, the default will be used: every family has a different color (1,2,3,4)
        //if you have more than one deck, the following decks are set the same way
        //for example: in Spider i want to set the difficulty: Easy would mean
        //every family is the same color, so i use this:
        setCardDrawables(1,1,1,1);

        //you can set up how the cards on a stack are stacked. which an offset to right, down and so on
        //So use this method, put an int array with values for each stack that should get a direction
        //int value at array pos 0 will be assigned to stack[0] and so on
        //tableau stacks are set to "down" by default, so you don't need to call this method for them
        //but if you want to change anything, don't forget to set the tableau stacks to "1" again
        //
        //the directions are:
        // 0 no visible offset (like the main stacks)
        // 1 down (like every tableau stack)
        // 2 up
        // 3 left (like the discard stack on Golf
        // 4 right (like the stack on golf in left handed mode
        setDirections(new int[]{1,1,1,0,0,1,1,3});
    }

    /*
     *  here you need to set the cards and stacks on the screen with the dimensions.
     *  You get the game layout to use its width and height and you get a boolean value
     *  to show if the phone is currently in landscape, so can change the layout
     *
     *  Here is an example like code you should follow
     */
    public void setStacks(RelativeLayout layoutGame, boolean isLandscape) {

        //set your card width first, because the height is just 1.5 times the width.
        //take the layout width and divide it width the number of stacks in a row + 1,
        //so there is enough place for the spacings. You can make the spacings bigger, for example
        //in landscape mode, so the cards don't seem to big
        Card.width = isLandscape ? layoutGame.getWidth() / (7+2) : layoutGame.getWidth() / (7+1);
        //don't forget to set the height!!
        Card.height = (int) (Card.width * 1.5);

        //now apply these dimensions to the cards and stacks, i used an extra params variable
        //so its more clean. you can copy this part
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Card.width, Card.height);
        for (Card card : cards) card.view.setLayoutParams(params);
        for (Stack stack : stacks) stack.view.setLayoutParams(params);

        //no we order the stacks on the field. First calculate a spacing variable, to know how much
        //space will be between the stacks. Just use the layout width minus the number of stacks
        //in a row, divided with the number of spaces between the stacks (which should be the number
        //of stacks +1)
        int spacing = (layoutGame.getWidth() - 7 * Card.width) / (7+1);
        //after that, we check if the spacing is bigger than a fixed value and set it to it if so.
        //So the spacing between the stacks won't be to much
        if (spacing > Card.width / 2)
            spacing = Card.width / 2;
        //no get the start position to place the stacks, so they are centered around the middle of
        //the screen. I use this way: Get the half of the layout width, minus how many stacks are
        //left to it times the card width, minus how many spacings are left to it times the spacing
        //width. (Do not use the spacing from the left screen edge to the first stack).
        //So it should look like this:
        int startPos = layoutGame.getWidth()/2 - 3*Card.width - 3*spacing;
        //Then set the stack coordinates like this:
        //X cor is the start pos + loop index times (spacing + card width)
        //Y cor can be like in the example code. In landscape use a bit less spacing from the
        //screen edge. The +1 is only so Android Studio doesnt show an error
        for (int i = 0; i < 6; i++) {
            stacks[i].view.setX(startPos + i* (spacing+ Card.width) );
            stacks[i].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1 );
        }
        //Also set other stacks like the main pile or something
        stacks[6].view.setX(stacks[0].view.getX());
        stacks[6].view.setY(stacks[0].view.getY() + Card.height + spacing);

        //Last step: Set the drawables of the stacks. Default one is just gray.
        //So maybe show on some an big A for ace or make them transparent or something
        stacks[6].view.setBackgroundResource(R.drawable.background_stack_ace);
        stacks[6].view.setBackgroundResource(R.drawable.background_stack_stock);
        stacks[6].view.setBackgroundResource(R.drawable.background_stack_transparent);
    }

    /*
     * Put your calculation to test if the game is won here.
     * It will be called on every card move. Return false for Not won, and true for won
     */
    public boolean winTest(){
        //For example on Klondike all foundation stacks have to be full, so everyone of them
        //needs to habe 13 cards. If not, game isn't won yet. If yes, game is won
        for (int i = 7; i <= 10; i++)
            if (stacks[i].getSize() != 13)
                return false;

        return true;
    }

    /*
     * Put how to deal cards here: All cards are set to the main stack or the dealFromID stack, if set.
     * The cards will be faced down by default to, so flip them up if needed.
     */
    public void dealCards(){
        //Simple example: Deal the first card from the main stack to another one.
        //Use the OPTION_NO_RECORD, or else the player can undo this movement.
        //After that, flip the card up
        moveToStack(dealFromStack().getTopCard(), stacks[0], OPTION_NO_RECORD);
        stacks[0].getTopCard().flipUp();
    }

    /*
     * tests if the mainstack got touched. For nearly every game it's the same, because they only have
     * one main stack. But Spider is a bit special, it
     */
    @Override
    public boolean testIfMainStackTouched(float X, float Y){
        return currentGame.getMainStack().isOnLocation(X,Y);
    }

    /*
     * Put what happens if the player touches the main stack, if there is any.
     * If there is no main stack, leave it empty
     */
    public void onMainStackTouch(){

        //first test the coordinates of they are really on the main stack.
        //(I put this test here because in Spider Solitaire, the cards from "main" are placed on
        // multiple stacks).
        if (getMainStack().getSize() > 0) {                                                         //if it has cards
            moveToStack(getMainStack().getTopCard(), stacks[11]);                                   //move the card to the other stock stack
            stacks[11].getTopCard().flipUp();
        }
        //if it empty, do something like move all cards from the discard pile to the main
        // Stack again. In this example from Klondike, the cards are moved in reversed order than
        //they would be movend with the moveToStack(), so i move them one by one and update
        //the recordList and score
        else if (stacks[11].getSize() != 0) {                                                       //if there are cards on stack11 which can be moved
            recordList.add(stacks[11].currentCards);                                                //save the record in normal order

            while (stacks[11].getSize() > 0)                                                        //then place the top card from stack11 to stack12 until it is empty
                moveToStack(stacks[11].getTopCard(), getMainStack(), OPTION_NO_RECORD);
        }
    }

    /*
     * Test a card if you can place it on a stack.
     * Tested every time if the player wants to place a card on some stack
     */
    public boolean cardTest(Stack stack, Card card){
        //Example from Klondike.
        if (stack.getID() < 7) {                                                                    //tableau stacks
            if (stack.isEmpty())                                                                    //if there is no card
                return card.getValue() == 13;                                                       //you can place a king
            else
                return (stack.getTopCard().getColor() % 2 != card.getColor() % 2)                   //else different Color. black is uneven and red even
                        && (stack.getTopCard().getValue() == card.getValue() + 1);                  //and the Value must match

        } else if (stack.getID() < 11 && movingCards.getSize() < 2) {                               //if its an foundation stack and only one card is moving (<2 because for eg undo, movingCards size is zero)
            if (stack.isEmpty())                                                                    //if there is no card, you can place an ace
                return card.getValue() == 1;
            else
                return (stack.getTopCard().getColor() == card.getColor())                           //else has to be the same colour
                        && (stack.getTopCard().getValue() == card.getValue() - 1);                  // and Value has to match
        } else                                                                                      //else not place-able
            return false;
    }

    /*
     * Test if the player can even pick up a card (faced down cards are never moved)
     * If yes, every card from the touched card to the stack top card will be moved
     */
    public boolean addCardToMovementTest(Card card){
        //in case of Klondike it's easy: If a card is faced up, every card on top on it in the stack
        //has the right order. So return true if a card is up. But because faced down cards aren't even
        //tested here, just return true
        return true;
    }

    /*
     * Called up to three times when pressing the hint button.
     * Test the cards if there can be shown something as a hint.
     * If so, return the ID of the card and then the ID of the destination stack in a int array
     * (In this order!!). If no card can be found, return null at the end of the method, so
     * the hint will stop.
     *
     * Use hint.hasVisited(card) to get if the card has been visited, so it won't result in an endless loop
     */
    public int[] hintTest(){
        //Short example from Klonsike
        Card card;                                                                                  //card to test

        for (int i = 0; i <= 6; i++) {                                                              //loop through every stack on the tableau as origin

            Stack origin = stacks[i];                                                               //set the stack as origin

            if (origin.isEmpty() || !origin.getTopCard().isUp())                                    //continue if it's empty or no card is flipped up
                continue;

            /* last card of a stack to move to the foundation */
            card = origin.getTopCard();                                                             //in this part, get the top card of a stack

            if (!hint.hasVisited(card)) {                                                           //if this card hasn't been visited
                for (int j = 7; j <= 10; j++) {                                                     //loop through every foundation stack as destination
                    if (card.test(stacks[j])) {                                                     //then test

                        return new int[]{card.getID(),j};
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
    * Or just always return false if you don't the button in the game
    */
    public boolean autoCompleteStartTest() {
        //Example from Klondike: If every card is faced up, return true. Return false otherwiese
        for (int i = 0; i < 7; i++)
            if (stacks[i].getSize() > 0 && !stacks[i].getCard(0).isUp())
                return true;

        return false;
    }

    /*
     *  Put what in autoComplete have to be tested here.
     *  Return the card ID and the destination stack ID, so the card can be moved there
     *  Return null at then end. Then the auto complete stops if no new card is found.
     *  It will call the testIfWon() method then
     */
    public int[] autoCompleteMoveTest() {
        //Klondike test: Test every top card of a stack with the foundation cards and return the
        //stack and card IDs if the card test is sucessfull.
        for (int i = 7; i <= 10; i++) {                                                             //foundation fields
            Stack destination = stacks[i];                                                          //get the destination for more visibility

            for (int j = 0; j <= 6; j++) {                                                          //tableau fields
                Stack origin = stacks[j];                                                           //get the origin for more visibility

                if (origin.getSize() > 0 && origin.getTopCard().test(destination)) {                //test if there are still cards on it and if the card test is successful
                    return new int[]{origin.getTopCard().getID(), destination.getID()};             //and return
                }
            }

            for (int j = 11; j <= 12; j++) {                                                        //stock
                Stack origin = stacks[j];                                                           //get the origin for more visibility

                for (int k = 0; k < origin.getSize(); k++) {                                        //loop through every card
                    if (origin.getCard(k).test(destination)) {                                      //then test every card
                        origin.getCard(k).flipUp();                                                 //because cards are from stock, flip up
                        return new int[]{origin.getCard(k).getID(), destination.getID()};
                    }
                }
            }
        }

        return null;
    }

    /*
     * Handle scoring here. It will be called every time a card or multiple cards are moved.
     * The method gets the array of cards which are moved, IDs of origin and destination stack and if its a single card movement as boolean.
     * Then test everything and return the points as integers. Return 0 at end for no update of score!
     *
     * Undo movements will invert the points, but also set -25 points for using undo
     */
    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs){
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
    @Override
    public void testAfterMove(){
    }
}

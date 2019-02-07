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

package de.tobiasbielefeld.solitaire.classes;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Contains everything related to cards. The view is a custom image view, which overrides some
 * methods for animations. The drawable files are also updated here
 */

public class Card {

    public enum movements {INSTANT, NONE, DEFAULT}

    public static int width, height;                      //width and height calculated in relation of the screen dimensions in Main activity
    public static Bitmap background;
    private static Bitmap[] drawables = new Bitmap[52];
    public CustomImageView view;                          //the image view of the card, for easier code not private
    private int color;                                    //1=clubs 2=hearts 3=Spades 4=diamonds
    private int value;                                    //1=ace 2,3,4,5,6,7,8,9,10, 11=joker 12=queen 13=king
    private Stack stack;                                  //saves the stack where the card is placed
    private int id;                                       //internal id
    private boolean isUp;                                 //indicates if the card is placed upwards or backwards
    private boolean isInvisible;
    private PointF oldLocation = new PointF();            //old location so cards can be moved back if they can't placed on a new stack

    public static int ACE = 1;
    public static int JOKER = 11;
    public static int QUEEN = 12;
    public static int KING = 13;

    //no enum, I want to explicitly set the values, because they are saved in the sharedPref and
    private static final int STATE_FACED_DOWN = 0;
    public static final int STATE_FACED_UP = 1;
    public static final int STATE_INVISIBLE = 2;

    /**
     * Sets id, color and value. The cards are initialized at game start with a for loop.
     * <p>
     * The color range is 1 to 4 and depends on the cardDrawableOrder, which is set to
     * 1 for the first 13 cards, 2 for the following 13 cards and so on.
     * After 52 cards (= one deck) it repeats. The value range is from 1 to 13 (= Ace to King).
     *
     * @param id The position in the cards array
     */
    public Card(int id) {
        this.id = id;
        color = currentGame.cardDrawablesOrder[(id % 52) / 13];
        value = (id % 13) + 1;
    }

    public void setImageBitmap(Bitmap bitmap) {
        if (!stopUiUpdates) {
            view.setImageBitmap(bitmap);
        }
    }

    /**
     * Sets the card drawables according to set preferences. Each card theme has one drawable file
     * with 52 cards in it. These will be loaded in bitmaps and applied to the cards. The bitmap array
     * has the same order like the cards array. If the fourColor theme is enabled, Clubs and Diamonds
     * use another row in the bitmap file.
     */
    public static void updateCardDrawableChoice() {
        boolean fourColors = prefs.getSavedFourColorMode();

        for (int i = 0; i < 13; i++) {
            drawables[i] = bitmaps.getCardFront(i, fourColors ? 1 : 0);
            drawables[13 + i] = bitmaps.getCardFront(i, 2);
            drawables[26 + i] = bitmaps.getCardFront(i, 3);
            drawables[39 + i] = bitmaps.getCardFront(i, fourColors ? 5 : 4);
        }

        if (cards == null) {
            return;
        }

        for (Card card : cards) {
            if (card.isUp()) {
                card.setCardFront();
            }
        }
    }

    /**
     * Loads the card backgrounds for the bitmap file and applies them.
     */
    public static void updateCardBackgroundChoice() {
        int positionX = prefs.getSavedCardBackground();
        int positionY = prefs.getSavedCardBackgroundColor();
        background = bitmaps.getCardBack(positionX, positionY);

        if (cards == null) {
            return;
        }

        for (Card card : cards) {
            if (!card.isUp()) {
                card.setCardBack();
            }
        }
    }

    /**
     * Save the card direction (up/down) as a string list.
     */
    public static void save() {
        List<Integer> list = new ArrayList<>(cards.length);

        for (Card card : cards) {
            int state = card.isUp ? STATE_FACED_UP : STATE_FACED_DOWN;

            if (card.isInvisible) {
                state = STATE_INVISIBLE;
            }

            list.add(state);
        }

        prefs.saveCards(list);
    }

    /**
     * Load the card direction (up/down) from a string list and applies the data.
     */
    public static void load() {
        List<Integer> list = prefs.getSavedCards();

        for (int i = 0; i < cards.length; i++) {
            switch (list.get(i)) {
                case STATE_FACED_UP:
                    cards[i].flipUp();
                    break;
                case STATE_FACED_DOWN:
                    cards[i].flipDown();
                    break;
                case STATE_INVISIBLE:
                    cards[i].view.setVisibility(View.GONE);
                    cards[i].isInvisible = true;
                    //cards[i].removeFromGame();
                    break;
            }
        }
    }

    /**
     * Sets the card front side from the bitmap array. The position is calculated with the card
     * color and value.
     */
    public void setCardFront() {
        setImageBitmap(drawables[(color - 1) * 13 + value - 1]);
    }

    /**
     * Sets the card background, there is only one background for all cards.
     */
    public void setCardBack() {
        setImageBitmap(background);
    }

    /**
     * Updates the color of the card. It is only used when a custom color order is set up
     * (like in Spider for different difficulties).
     */
    public void setColor() {
        color = currentGame.cardDrawablesOrder[(id % 52) / 13];
    }

    /**
     * Moves a card to the given coordinates (if not already there). This will use a translate
     * Animation and no interaction with cards/buttons is possible during the movement.
     *
     * @param pX The x-coordinate of the destination
     * @param pY The y-coordinate of the destination
     */
    public void setLocation(float pX, float pY) {

        if (isInvisible) {
            setLocationWithoutMovement(pX, pY);
        }

        if (!stopUiUpdates) {
            if (view.getX() != pX || view.getY() != pY) {
                animate.moveCard(this, pX, pY);
            }
        }
    }

    /**
     * Sets the location instantly WITHOUT a movement.
     *
     * @param pX The x-coordinate of the destination
     * @param pY The y-coordinate of the destination
     */
    public void setLocationWithoutMovement(float pX, float pY) {
        if (!stopUiUpdates) {
            view.bringToFront();
            view.setX(pX);
            view.setY(pY);
        }
    }

    /**
     * Saves the current location of the card as the old location, so it can be reverted if
     * necessary.
     */
    public void saveOldLocation() {
        oldLocation.x = view.getX();
        oldLocation.y = view.getY();
    }

    /**
     * reverts the current location to the saved one.
     */
    public void returnToOldLocation() {
        view.setX(oldLocation.x);
        view.setY(oldLocation.y);
    }

    /**
     * Sets the direction to up and updates the drawable.
     */
    public void flipUp() {
        isUp = true;

        if (!stopUiUpdates) {
            setCardFront();
        }
    }

    /**
     * Sets the direction to down and updates the drawable.
     */
    public void flipDown() {
        isUp = false;

        if (!stopUiUpdates) {
            setCardBack();
        }
    }

    /**
     * Sets the direction to the opposite of the current direction.
     */
    public void flip() {
        if (isUp())
            flipDown();
        else
            flipUp();
    }

    /**
     * Sets the direction to the opposite of the current direction, but with an animation.
     * This also updates the score (movement from the current stack to the same stack is counted
     * as a flip) and sets a new record in the record list.
     */
    public void flipWithAnim() {
        if (isUp()) {
            isUp = false;
            //sounds.playSound(Sounds.names.CARD_FLIP_BACK);
            scores.undo(this, getStack());

            if (!stopUiUpdates) {
                animate.flipCard(this, false);
            }
        } else {
            isUp = true;
            //sounds.playSound(Sounds.names.CARD_FLIP);
            scores.move(this, getStack());
            recordList.addFlip(this);

            if (!stopUiUpdates) {
                animate.flipCard(this, true);
            }
        }
    }

    /**
     * Tests if this card can be placed on a stack:
     * Only possible if: the cardTest returns true, the card and the top card on the destination are
     * up, and no auto complete is running.
     *
     * @param destination The destination stack to test the card on
     * @return True if movement is possible, false otherwise
     */
    public boolean test(Stack destination) {
        if (prefs.isDeveloperOptionMoveCardsEverywhereEnabled()) {
            return true;
        }

        return !((!isUp() || (destination.getSize() != 0 && !destination.getTopCard().isUp())) && !autoComplete.isRunning()) && currentGame.cardTest(destination, this);
    }

    public int getColor() {
        return color;
    }

    public boolean isTopCard() {
        return getStack().getTopCard() == this;
    }

    public boolean isFirstCard() {
        return getStack().getCard(0) == this;
    }

    public int getIndexOnStack() {
        return getStack().getIndexOfCard(this);
    }

    public boolean isUp() {                                                                         //returns if the card is up
        return isUp;
    }

    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public Stack getStack() {
        return stack;
    }

    public void setStack(Stack stack) {
        this.stack = stack;
    }

    public float getX() {
        return view.getX();
    }

    public void setX(float X) {
        view.setX(X);
    }

    public float getY() {
        return view.getY();
    }

    public void setY(float Y) {
        view.setY(Y);
    }

    public int getStackId() {
        return stack.getId();
    }

    public boolean isInvisible() {
        return isInvisible;
    }

    public void removeFromCurrentStack() {
        if (stack != null) {
            stack.removeCard(this);
            stack = null;
        }
    }

    public Card getCardOnTop() {
        if (getIndexOnStack() < stack.getSize() - 1) {
            return stack.getCard(getIndexOnStack() + 1);
        } else {
            return this;
        }
    }

    public Card getCardBelow() {
        return getIndexOnStack() == 0 ? this : stack.getCard(getIndexOnStack() - 1);
    }

    public void bringToFront() {
        if (!stopUiUpdates) {
            view.bringToFront();
        }
    }

    public void removeFromGame() {
        view.setVisibility(View.GONE);
        isInvisible = true;
        moveToStack(this, currentGame.offScreenStack, OPTION_NO_RECORD);
    }

    public void addBackToGame(Stack moveTo) {
        isInvisible = false;
        flipUp();
        view.setVisibility(View.VISIBLE);
        moveToStack(this, moveTo);
    }
}
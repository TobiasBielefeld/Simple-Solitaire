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

import java.util.ArrayList;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/*
 *  Contains everything related to cards. it uses a picture view for drawable,
 *  like loading or saving cards orientation and setting the drawable files
 */

public class Card {

    public static int width, height;                                                                //width and height calculated in relation of the screen dimensions in Main activity
    public static Bitmap background;
    private static Bitmap[] drawables = new Bitmap[52];
    public CustomImageView view;                                                                          //the image view of the card, for easier code not private
    private int color;                                                                              //1=clubs 2=hearts 3=Spades 4=diamonds
    private int value;                                                                              //1=ace 2,3,4,5,6,7,8,9,10, 11=joker 12=queen 13=king
    private Stack stack;                                                                            //saves the stack where the card is placed
    private int ID;                                                                                 //internal id
    private boolean isUp;                                                                           //indicates if the card is placed upwards or backwards
    private PointF oldLocation = new PointF();                                                      //old location so cards can be moved back if they can't placed on a new stack

    public Card(int ID) {
        /*
         *  set ID color and value. color depends on the cardDrawableOrder. But by default the
         *  order is 1,2,3,4. Value is calculated like that because the IDs are in the right order:
         *  from ace to king, then again with the next color
         */
        this.ID = ID;
        color = currentGame.cardDrawablesOrder[(ID % 52) / 13];
        value = (ID % 13) + 1;
    }

    public static void updateCardDrawableChoice() {
        boolean fourColors = getSharedBoolean(PREF_KEY_4_COLOR_MODE, DEFAULT_4_COLOR_MODE);

        for (int i = 0; i < 13; i++) {
            drawables[i] = bitmaps.getCardFront(i, fourColors ? 1 : 0);
            drawables[13 + i] = bitmaps.getCardFront(i, 2);
            drawables[26 + i] = bitmaps.getCardFront(i, 3);
            drawables[39 + i] = bitmaps.getCardFront(i, fourColors ? 5 : 4);
        }

        if (cards != null) {
            for (Card card : cards)
                if (card.isUp())
                    card.setCardFront();
        }
    }

    public static void updateCardBackgroundChoice() {

        int position = getSharedInt(CARD_BACKGROUND, DEFAULt_CARD_BACKGROUND) - 1;
        background = bitmaps.getCardBack(position % 8, position / 8);

        if (cards != null) {
            for (Card card : cards)
                if (!card.isUp())
                    card.setCardBack();
        }
    }

    public static void save() {
        ArrayList<Integer> list = new ArrayList<>();

        for (Card card : cards)
            list.add(card.isUp ? 1 : 0);

        putIntList(CARDS, list);
    }

    public static void load() {
        ArrayList<Integer> list = getIntList(CARDS);

        for (int i = 0; i < cards.length; i++) {

            if (list.get(i) == 1)
                cards[i].flipUp();
            else
                cards[i].flipDown();
        }
    }

    public void setCardFront() {
        view.setImageBitmap(drawables[(color - 1) * 13 + value - 1]);
    }

    public void setCardBack() {
        view.setImageBitmap(background);
    }

    public void setColor() {
        //update the color, used in Spider after loading the preference
        color = currentGame.cardDrawablesOrder[(ID % 52) / 13];
    }

    public int getID() {
        return ID;
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

    public void setLocation(float pX, float pY) {
        //if not already there, animate the moving
        if (view.getX() != pX || view.getY() != pY)
            animate.moveCard(this, pX, pY);
    }

    public void setLocationWithoutMovement(float pX, float pY) {
        view.bringToFront();
        view.setX(pX);
        view.setY(pY);
    }

    public void saveOldLocation() {
        oldLocation.x = view.getX();
        oldLocation.y = view.getY();
    }

    public void returnToOldLocation() {
        view.setX(oldLocation.x);
        view.setY(oldLocation.y);
    }

    public void flipUp() {
        isUp = true;
        setCardFront();
    }

    public void flipDown() {
        isUp = false;
        setCardBack();
    }

    public void flip() {
        if (isUp())
            flipDown();
        else
            flipUp();
    }

    public void flipWithAnim() {
        if (isUp()) {
            isUp = false;
            scores.undo(this, getStack());
            animate.flipCard(this, false);
        } else {
            isUp = true;
            scores.move(this, getStack());
            recordList.addFlip(this);
            animate.flipCard(this, true);
        }
    }

    public boolean isUp() {                                                                         //returns if the card is up
        return isUp;
    }

    public boolean test(Stack stack) {
        /*
         *  test if this card can be placed on a stack:
         *  Not possible when:
         *  the card is not faced up OR the top card on the stack is not faced up OR Autocomplete is running
         *  Possible when:
         *  cardTest() from the current game returns true
         */
        return !((!isUp() || (stack.getSize() != 0 && !stack.getTopCard().isUp())) && !autoComplete.isRunning()) && currentGame.cardTest(stack, this);

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
}
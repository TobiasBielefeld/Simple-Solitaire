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
import android.graphics.RectF;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/*
 *  Contains everything around a stack
 */

public class Stack {

    public static float defaultSpacing;                                                               //The default space between cards, will be calculated in onCreate of the Main activity
    public static Bitmap backgroundDefault, backgroundTalon, background1, background2, background3,  //bitmaps for the stack background images
            background4, background5, background6, background7, background8, background9, background10,
            background11, background12, background13, arrowLeft, arrowRight, backgroundTransparent;
    public ImageView view;                                                                          //Background of the stack
    public ArrayList<Card> currentCards = new ArrayList<>();                                        //the array of cards on the stack
    private int ID;                                                                                 //ID: 0 to 6 tableau. 7 to 10 foundations. 11 and 12 discard and Main stack
    private float spacing;                                                                          //direction in which the cards on the stacks are ordered (top, down, left, right)
    private int spacingDirection;
    private int hasArrow;
    private float spacingMax;

    public Stack(int ID) {                                                                          //Constructor: set ID
        this.ID = ID;
    }

    public static void loadBackgrounds() {
        backgroundDefault = bitmaps.getStackBackground(0, 0);
        backgroundTalon = bitmaps.getStackBackground(1, 0);
        background1 = bitmaps.getStackBackground(2, 0);
        background2 = bitmaps.getStackBackground(3, 0);
        background3 = bitmaps.getStackBackground(4, 0);
        background4 = bitmaps.getStackBackground(5, 0);
        background5 = bitmaps.getStackBackground(6, 0);
        background6 = bitmaps.getStackBackground(7, 0);
        background7 = bitmaps.getStackBackground(8, 0);
        background8 = bitmaps.getStackBackground(0, 1);
        background9 = bitmaps.getStackBackground(1, 1);
        background10 = bitmaps.getStackBackground(2, 1);
        background11 = bitmaps.getStackBackground(3, 1);
        background12 = bitmaps.getStackBackground(4, 1);
        background13 = bitmaps.getStackBackground(5, 1);
        arrowLeft = bitmaps.getStackBackground(6, 1);
        arrowRight = bitmaps.getStackBackground(7, 1);
        backgroundTransparent = bitmaps.getStackBackground(8, 1);
    }

    public void reset() {                                                                           //removes all cards
        currentCards.clear();
    }

    public void addCard(Card card) {
        card.setStack(this);
        currentCards.add(card);

        updateSpacing();

        if (currentGame.hasMainStack() && ID >= currentGame.getMainStack().getID()) {
            card.flipDown();
        } else if (currentGame.hasDiscardStack() && ID >= currentGame.getDiscardStack().getID()) {
            card.flipUp();
        }
    }

    public void removeCard(Card card) {
        currentCards.remove(currentCards.indexOf(card));
        updateSpacing();
    }

    public Card getCard(int index) {                                                                //get card from index
        return currentCards.get(index);
    }

    public Card getTopCard() {
        if (isEmpty()) {
            Log.e("Stack.getTopCard()", "Stack is empty so there is no card to return! Test with isEmpty()!");
        }

        return currentCards.get(currentCards.size() - 1);
    }

    public Card getCardFromTop(int index) {
        //returns cards in reversed order
        if (isEmpty()) {
            Log.e("Stack.getCardFromTop()", "Stack is empty so there is no card to return! Testing with isEmpty()!");
        }

        return currentCards.get(currentCards.size() - 1 - index);
    }

    public int getID() {                                                                            //gets the ID
        return ID;
    }

    public int getIndexOfCard(Card card) {
        return currentCards.indexOf(card);
    }

    public int getSize() {                                                                          //return how many cards are on the stack
        return currentCards.size();
    }

    public boolean isOnLocation(float pX, float pY) {
        /*
         * returns if a position matches the stack coordinates
         * Use the current stack coordinates and the position of the top card for it
         */
        PointF topPoint = getPosition(0);

        switch (spacingDirection) {
            default://no spacing
                return pX >= view.getX() && pX <= view.getX() + Card.width
                        && pY >= view.getY() && pY <= view.getY() + Card.height;
            case 1: //down
                topPoint.y += Card.height;
                return pX >= view.getX() && pX <= view.getX() + Card.width
                        && pY >= view.getY() && pY <= topPoint.y;
            case 2: //up
                return pX >= view.getX() && pX <= view.getX() + Card.width
                        && pY >= topPoint.y && pY <= view.getY() + Card.height;
            case 3: //left
                return pX >= topPoint.x && pX <= view.getX() + Card.width
                        && pY >= view.getY() && pY <= view.getY() + Card.height;
            case 4: //right
                topPoint.x += Card.width;
                return pX >= view.getX() && pX <= topPoint.x
                        && pY >= view.getY() && pY <= view.getY() + Card.height;
        }
    }

    public void save() {
        ArrayList<Integer> list = new ArrayList<>();

        for (Card card : currentCards)
            list.add(card.getID());

        putIntList(STACK + ID, list);
    }

    public void load() {
        reset();

        ArrayList<Integer> list = getIntList(STACK + ID);

        for (Integer i : list) {
            addCard(cards[i]);
            cards[i].view.bringToFront();
        }
    }

    private void updateSpacing() {
        /*
         * update spacing according to the direction. Left and right are reversed for left handed mode
         */

        switch (spacingDirection) {
            default: //no spacing
                if (!isEmpty())
                    getTopCard().setLocation(view.getX(), view.getY());
                break;
            case 1: //down
                spacing = min((spacingMax - view.getY()) / (currentCards.size() + 1), defaultSpacing);
                for (int i = 0; i < currentCards.size(); i++)
                    currentCards.get(i).setLocation(view.getX(), view.getY() + spacing * i);
                break;
            case 2: //up
                spacing = min((view.getY() - spacingMax) / (currentCards.size() + 1), defaultSpacing);
                for (int i = 0; i < currentCards.size(); i++)
                    currentCards.get(i).setLocation(view.getX(), view.getY() - spacing * i);
                break;
            case 3: //left
                if (getSharedBoolean("pref_key_left_handed_mode", false)) {
                    spacing = min((spacingMax - view.getX()) / (currentCards.size() + 1), defaultSpacing);
                    for (int i = 0; i < currentCards.size(); i++)
                        currentCards.get(i).setLocation(view.getX() + spacing * i, view.getY());
                } else {
                    spacing = min((view.getX() - spacingMax) / (currentCards.size() + 1), defaultSpacing);
                    for (int i = 0; i < currentCards.size(); i++)
                        currentCards.get(i).setLocation(view.getX() - spacing * i, view.getY());
                }
                break;
            case 4: //right
                if (getSharedBoolean("pref_key_left_handed_mode", false)) {
                    spacing = min((view.getX() - spacingMax) / (currentCards.size() + 1), defaultSpacing);
                    for (int i = 0; i < currentCards.size(); i++)
                        currentCards.get(i).setLocation(view.getX() - spacing * i, view.getY());
                } else {
                    spacing = min((spacingMax - view.getX()) / (currentCards.size() + 1), defaultSpacing);
                    for (int i = 0; i < currentCards.size(); i++)
                        currentCards.get(i).setLocation(view.getX() + spacing * i, view.getY());
                }
                break;
        }
    }

    public Card getFirstUpCard() {
        for (Card card : currentCards)
            if (card.isUp())
                return card;

        return null;
    }

    public int getFirstUpCardPos() {
        for (int i = 0; i < currentCards.size(); i++) {
            if (currentCards.get(i).isUp())
                return i;
        }

        return -1;
    }

    public PointF getPosition(int offset) {
        //get the position a new top card would have according to the spacing and offset (used for hints)
        switch (spacingDirection) {
            default://no spacing
                return new PointF(view.getX(), view.getY());
            case 1: //down
                return new PointF(view.getX(), view.getY() + spacing * (currentCards.size() + offset));
            case 2: //up
                return new PointF(view.getX(), view.getY() - spacing * (currentCards.size() + offset));
            case 3: //left
                return new PointF(view.getX() - spacing * (currentCards.size() + offset), view.getY());
            case 4: //right
                return new PointF(view.getX() + spacing * (currentCards.size() + offset), view.getY());
        }
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public void setSpacingDirection(int value) {
        spacingDirection = value;
    }

    public void setArrow(int direction) {
        hasArrow = direction;
    }

    public int hasArrow() {
        return hasArrow;
    }

    public void setSpacingMax(int index) {
        Stack stack = stacks[index];

        switch (spacingDirection) {
            default: //no spacing
                break;
            case 1: //down
                spacingMax = stack.view.getY() - Card.height;
                break;
            case 2: //up
                spacingMax = stack.view.getY() + Card.height;
                break;
            case 3: //left
                if (getSharedBoolean("pref_key_left_handed_mode", false)) {
                    spacingMax = stack.view.getX() - Card.width;
                } else {
                    spacingMax = stack.view.getX() + Card.width;
                }
                break;
            case 4: //right
                if (getSharedBoolean("pref_key_left_handed_mode", false)) {
                    spacingMax = stack.view.getX() + Card.width;
                } else {
                    spacingMax = stack.view.getX() - Card.width;
                }
                break;
        }
    }

    public void setSpacingMax(RelativeLayout layoutGame) {

        switch (spacingDirection) {
            default: //no spacing
                break;
            case 1: //down
                spacingMax = (float) (layoutGame.getHeight() - Card.height);
                break;
            case 2: //up
                spacingMax = 0;
                break;
            case 3: //left
                if (getSharedBoolean("pref_key_left_handed_mode", false)) {
                    spacingMax = layoutGame.getWidth() - Card.width;
                } else {
                    spacingMax = 0;
                }
                break;
            case 4: //right
                if (getSharedBoolean("pref_key_left_handed_mode", false)) {
                    spacingMax = 0;
                } else {
                    spacingMax = layoutGame.getWidth() - Card.width;
                }
                break;
        }
    }

    public void mirrorStack(RelativeLayout layoutGame) {

        view.setX(layoutGame.getWidth() - view.getX() - Card.width);

        for (int j = 0; j < getSize(); j++) {
            Card card = getCard(j);
            card.setLocationWithoutMovement(layoutGame.getWidth() -
                    card.view.getX() - Card.width, card.view.getY());
        }

        if (spacingDirection == 3 || spacingDirection == 4) {
            if (currentGame.directionBorders != null && currentGame.directionBorders[getID()] != -1)    //-1 means no border
                setSpacingMax(currentGame.directionBorders[getID()]);
            else
                setSpacingMax(layoutGame);
        }
    }

    public RectF getRect() {

        if (isEmpty()) {
            return new RectF(view.getX(), view.getY(), view.getX() + view.getWidth(), view.getY() + view.getHeight());
        }

        switch (spacingDirection) {
            default://no spacing
                return new RectF(view.getX(), view.getY(), view.getX() + view.getWidth(), view.getY() + view.getHeight());
            case 1: //down
                return new RectF(view.getX(), view.getY(), view.getX() + view.getWidth(), getTopCard().view.getY() + view.getHeight());
            case 2: //up
                return new RectF(view.getX(), getTopCard().view.getY(), view.getX() + view.getWidth(), view.getY() + view.getHeight());
            case 3: //left
                return new RectF(getTopCard().view.getX(), view.getY(), view.getX() + view.getWidth(), view.getY() + view.getHeight());
            case 4: //right
                return new RectF(view.getX(), view.getY(), getTopCard().view.getX() + view.getWidth(), view.getY() + view.getHeight());
        }
    }
}

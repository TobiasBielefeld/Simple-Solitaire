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

import android.graphics.PointF;
import android.widget.ImageView;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.helper.CardDrawables.*;

/*
 *  Contains everything related to cards. it uses a picture view for drawable,
 *  like loading or saving cards orientation and setting the drawable files
 */

public class Card {

    public static int width, height;                                                                //width and height calculated in relation of the screen dimensions in Main activity
    public static int[] drawables;                                                                  //array with the drawables of the cards
    public static int background;                                                                   //background drawable of the cards
    public ImageView view;                                                                          //the image view of the card, for easier code not private
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
        color = currentGame.cardDrawablesOrder[(ID%52) / 13];
        value = (ID%13) +1;
    }

    public void setColor(){
        //update the color, used in Spider after loading the preference
        color = currentGame.cardDrawablesOrder[(ID%52) / 13];
    }

    public static void updateCardDrawableChoice(){
        //get
        switch (getSharedInt(CARD_DRAWABLES, 1)) {
            case 1:
                drawables = sDrawablesBasic;
                break;
            case 2:
                drawables = sDrawablesClassic;
                break;
            case 3:
                drawables = sDrawablesAbstract;
                break;
            case 4:
                drawables = sDrawablesSimple;
                break;
            case 5:
                drawables = sDrawablesModern;
                break;
            case 6:
                drawables = sDrawablesDark;
                break;
        }
        //apply
        if (cards!=null) {
            for (Card card : cards)
                if (card.isUp())
                    card.view.setImageResource(drawables[(card.getColor() - 1) *13 + card.getValue() - 1]);
        }
    }

    public static void updateCardBackgroundChoice() {
        //get
        switch (getSharedInt(CARD_BACKGROUND, 1)) {
            case 1:
                background = R.drawable.background_1;
                break;
            case 2:
                background = R.drawable.background_2;
                break;
            case 3:
                background = R.drawable.background_3;
                break;
            case 4:
                background = R.drawable.background_4;
                break;
            case 5:
                background = R.drawable.background_5;
                break;
            case 6:
                background = R.drawable.background_6;
                break;
            case 7:
                background = R.drawable.background_7;
                break;
            case 8:
                background = R.drawable.background_8;
                break;
            case 9:
                background = R.drawable.background_9;
                break;
            case 10:
                background = R.drawable.background_10;
                break;
            case 11:
                background = R.drawable.background_11;
                break;
            case 12:
                background = R.drawable.background_12;
                break;
        }
        //apply
        if (cards!=null) {
            for (Card card : cards)
                if (!card.isUp()) card.view.setImageResource(background);
        }
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
        //view.bringToFront();

        //if not already there, animate the moving
        if (view.getX() != pX || view.getY() != pY)
            animate.moveCard(this, pX, pY);
    }

    public void setLocationWithoutMovement(float pX,float pY) {
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
        //set the coresponding drawable as image
        view.setImageResource(drawables[(getColor() - 1) *13 + getValue() - 1]);
    }

    public void flipDown() {
        isUp = false;
        view.setImageResource(background);
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

    public static void save() {
        ArrayList<Integer> list = new ArrayList<>();

        for (Card card: cards)
            list.add(card.isUp ? 1 : 0);

        putIntList(CARDS,list);
    }

    public static void load() {

        ArrayList<Integer> list = getIntList(CARDS);

        for (int i=0;i<cards.length;i++) {

            if (list.get(i)==1)
                cards[i].flipUp();
            else
                cards[i].flipDown();
        }
    }

    public int getColor(){
        return color;
    }

    public boolean isTopCard(){
        return getStack().getTopCard()==this;
    }

    public boolean isFirstCard(){
        return getStack().getCard(0)==this;
    }

    public int getIndexOnStack() {
        return getStack().getIndexOfCard(this);
    }
}
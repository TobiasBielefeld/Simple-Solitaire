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

package de.tobiasbielefeld.solitaire.helper;

import android.view.View;
import android.widget.RelativeLayout;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static android.view.View.GONE;

/**
 * Sets a semi transparent rectangle behind the card to highlight it, so the user knows which card
 * he tapped.
 */

public class CardHighlight {

    private View view;
    private boolean moveStarted;
    int padding, width, height;

    public CardHighlight(GameManager gm){
        view = gm.findViewById(R.id.card_highlight);
    }

    /**
     * Sets the size of the highlighting and moves it behin the cards.
     *
     * @param card The card on the stack to highlight
     */
    public void set(Card card){
        Stack stack = card.getStack();

        padding = (int) (Card.width*0.25);
        width = Card.width + padding;
        height = (int) (stack.getTopCard().view.getY() + Card.height - card.view.getY() + padding);

        view.setLayoutParams(new RelativeLayout.LayoutParams(width,height));
        view.setX(card.view.getX()- padding/2);
        view.setY(card.view.getY()- padding/2);
        view.setVisibility(View.VISIBLE);
        view.bringToFront();

        for (int i= card.getIndexOnStack();i<stack.getSize();i++){
            stack.getCard(i).view.bringToFront();
        }

        moveStarted = false;
    }

    /**
     * For drag and drop movements: Updates the position of the view behind the card.
     * It needs to update the height of the highlighted area, because the MovingCard.move() method
     * changes the offset of the cards.
     *
     * @param card The card to highlight
     */
    public void move(Card card){
        if (!moveStarted){
            moveStarted = true;

            height = (int) (card.getStack().getTopCard().view.getY() + Card.height - card.view.getY() + padding);
            view.setLayoutParams(new RelativeLayout.LayoutParams(width,height));
        }

        view.setX(card.view.getX() - padding/2);
        view.setY(card.view.getY() - padding/2);
    }

    public void hide(){
        view.setVisibility(GONE);
    }
}

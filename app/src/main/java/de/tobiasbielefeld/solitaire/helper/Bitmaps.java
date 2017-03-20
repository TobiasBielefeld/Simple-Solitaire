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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Here is the code to load the individual pictures from the sets located in drawable-nodpi.
 * This way, i have a lot less picture files.
 */

public class Bitmaps {

    int menuWidth, menuHeight, stackBackgroundWidth, stackBackgroundHeight,
            cardBackWidth, cardBackHeight, cardFrontWidth, cardFrontHeight,
            cardPreviewWidth, cardPreviewHeight, cardPreview2Width, cardPreview2Height;
    private Resources res;
    private Bitmap menu, stackBackground, cardBack, cardFront, cardPreview, cardPreview2;
    private int savedCardTheme;

    public boolean checkResources(){
        return res != null;
    }
    public void setResources(Resources res) {
        this.res = res;
    }

    public Bitmap getMenu(int posX, int posY) {

        if (menu == null) {
            menu = BitmapFactory.decodeResource(res, R.drawable.backgrounds_menu);
            menuWidth = menu.getWidth() / 6;
            menuHeight = menu.getHeight() / 3;
        }

        return Bitmap.createBitmap(menu, posX * menuWidth, posY * menuHeight, menuWidth, menuHeight);
    }

    public Bitmap getStackBackground(int posX, int posY) {

        if (stackBackground == null) {
            stackBackground = BitmapFactory.decodeResource(res, R.drawable.backgrounds_stacks);
            stackBackgroundWidth = stackBackground.getWidth() / 9;
            stackBackgroundHeight = stackBackground.getHeight() / 2;
        }

        return Bitmap.createBitmap(stackBackground, posX * stackBackgroundWidth, posY * stackBackgroundHeight, stackBackgroundWidth, stackBackgroundHeight);
    }

    public Bitmap getCardFront(int posX, int posY) {

        if (cardFront == null || savedCardTheme != getSharedInt(CARD_DRAWABLES, 1)) {

            savedCardTheme = getSharedInt(CARD_DRAWABLES, 1);
            int resID;

            switch (savedCardTheme) {
                case 1:
                default:
                    resID = R.drawable.cards_basic;
                    break;
                case 2:
                    resID = R.drawable.cards_classic;
                    break;
                case 3:
                    resID = R.drawable.cards_abstract;
                    break;
                case 4:
                    resID = R.drawable.cards_simple;
                    break;
                case 5:
                    resID = R.drawable.cards_modern;
                    break;
                case 6:
                    resID = R.drawable.cards_oxygen_dark;
                    break;
                case 7:
                    resID = R.drawable.cards_oxygen_light;
                    break;
                case 8:
                    resID = R.drawable.cards_poker;
                    break;
            }

            cardFront = BitmapFactory.decodeResource(res, resID);
            cardFrontWidth = cardFront.getWidth() / 13;
            cardFrontHeight = cardFront.getHeight() / 6;
        }

        return Bitmap.createBitmap(cardFront, posX * cardFrontWidth, posY * cardFrontHeight, cardFrontWidth, cardFrontHeight);
    }

    public Bitmap getCardBack(int posX, int posY) {

        if (cardBack == null) {
            cardBack = BitmapFactory.decodeResource(res, R.drawable.backgrounds_cards);
            cardBackWidth = cardBack.getWidth() / 8;
            cardBackHeight = cardBack.getHeight() / 2;
        }

        return Bitmap.createBitmap(cardBack, posX * cardBackWidth, posY * cardBackHeight, cardBackWidth, cardBackHeight);
    }

    public Bitmap getCardPreview(int posX, int posY) {

        if (cardPreview == null) {
            cardPreview = BitmapFactory.decodeResource(res, R.drawable.card_previews);
            cardPreviewWidth = cardPreview.getWidth() / 8;
            cardPreviewHeight = cardPreview.getHeight() / 2;
        }

        return Bitmap.createBitmap(cardPreview, posX * cardPreviewWidth, posY * cardPreviewHeight, cardPreviewWidth, cardPreviewHeight);
    }

    public Bitmap getCardPreview2(int posX, int posY) {

        if (cardPreview2 == null) {
            cardPreview2 = BitmapFactory.decodeResource(res, R.drawable.card_previews_2);
            cardPreview2Width = cardPreview2.getWidth() / 8;
            cardPreview2Height = cardPreview2.getHeight() / 2;
        }

        return Bitmap.createBitmap(cardPreview2, posX * cardPreview2Width, posY * cardPreview2Height, cardPreview2Width, cardPreview2Height);
    }
}

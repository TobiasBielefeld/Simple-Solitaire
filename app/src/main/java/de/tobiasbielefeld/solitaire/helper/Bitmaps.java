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
 * Here is the code to load the individual pictures from the bitmaps located in drawables-nodpi.
 * The bitmaps will first be decoded and the width/height of each individual card of the packets
 * will be set.
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

    /**
     * Gets the menu previews
     *
     * @param posX X-coordinate of the preview in the file
     * @param posY Y-coordinate of the preview in the file
     * @return a single bitmap
     */
    public Bitmap getMenu(int posX, int posY) {

        if (menu == null) {
            menu = BitmapFactory.decodeResource(res, R.drawable.backgrounds_menu);
            menuWidth = menu.getWidth() / 6;
            menuHeight = menu.getHeight() / 3;
        }

        return Bitmap.createBitmap(menu, posX * menuWidth, posY * menuHeight, menuWidth, menuHeight);
    }

    /**
     * Gets the stack backgrounds
     *
     * @param posX X-coordinate of the background in the file
     * @param posY Y-coordinate of the background in the file
     * @return a single bitmap
     */
    public Bitmap getStackBackground(int posX, int posY) {

        if (stackBackground == null) {
            stackBackground = BitmapFactory.decodeResource(res, R.drawable.backgrounds_stacks);
            stackBackgroundWidth = stackBackground.getWidth() / 9;
            stackBackgroundHeight = stackBackground.getHeight() / 2;
        }

        return Bitmap.createBitmap(stackBackground, posX * stackBackgroundWidth,
                posY * stackBackgroundHeight, stackBackgroundWidth, stackBackgroundHeight);
    }

    /**
     * Gets the card themes, according to the preference
     *
     * @param posX X-coordinate of the card in the file
     * @param posY Y-coordinate of the card in the file
     * @return a single bitmap of the card
     */
    public Bitmap getCardFront(int posX, int posY) {

        if (cardFront == null || savedCardTheme != getSharedInt(CARD_DRAWABLES, 1)) {

            savedCardTheme = getSharedInt(CARD_DRAWABLES, 1);
            int resID;

            switch (savedCardTheme) {
                default:
                case 1:
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

        return Bitmap.createBitmap(cardFront, posX * cardFrontWidth,
                posY * cardFrontHeight, cardFrontWidth, cardFrontHeight);
    }

    /**
     * Gets the card backgrounds
     *
     * @param posX X-coordinate of the background in the file
     * @param posY Y-coordinate of the background in the file
     * @return a single bitmap
     */
    public Bitmap getCardBack(int posX, int posY) {

        if (cardBack == null) {
            cardBack = BitmapFactory.decodeResource(res, R.drawable.backgrounds_cards);
            cardBackWidth = cardBack.getWidth() / 8;
            cardBackHeight = cardBack.getHeight() / 3;
        }

        return Bitmap.createBitmap(cardBack, posX * cardBackWidth,
                posY * cardBackHeight, cardBackWidth, cardBackHeight);
    }

    /**
     * Gets the preview of the card themes.
     *
     * @param posX X-coordinate of the preview in the file
     * @param posY Y-coordinate of the preview in the file
     * @return a single bitmap
     */
    public Bitmap getCardPreview(int posX, int posY) {

        if (cardPreview == null) {
            cardPreview = BitmapFactory.decodeResource(res, R.drawable.card_previews);
            cardPreviewWidth = cardPreview.getWidth() / 8;
            cardPreviewHeight = cardPreview.getHeight() / 2;
        }

        return Bitmap.createBitmap(cardPreview, posX * cardPreviewWidth,
                posY * cardPreviewHeight, cardPreviewWidth, cardPreviewHeight);
    }

    /**
     * Gets the card preview shown in the preference screen. It uses the same file as getCardPreview
     * put it only returns the King-image.
     *
     * @param posX X-coordinate of the preview in the file
     * @param posY Y-coordinate of the preview in the file
     * @return a single bitmap
     */
    public Bitmap getCardPreview2(int posX, int posY) {

        posX = posX*2 + 1;

        if (cardPreview2 == null) {
            cardPreview2 = BitmapFactory.decodeResource(res, R.drawable.card_previews);
            cardPreview2Width = cardPreview2.getWidth() / 16;
            cardPreview2Height = cardPreview2.getHeight() / 2;
        }

        return Bitmap.createBitmap(cardPreview2, posX * cardPreview2Width,
                posY * cardPreview2Height, cardPreview2Width, cardPreview2Height);
    }
}

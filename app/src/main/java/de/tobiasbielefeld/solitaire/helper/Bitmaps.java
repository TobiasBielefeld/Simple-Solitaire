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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Here is the code to load the individual pictures from the bitmaps located in drawables-nodpi.
 * The bitmaps will first be decoded and the width/height of each individual card of the packets
 * will be set.
 */

public class Bitmaps {

    static int NUM_CARD_THEMES = 10;
    static int NUM_CARD_BACKGROUNDS = 10;

    int menuWidth, menuHeight, stackBackgroundWidth, stackBackgroundHeight,
            cardBackWidth, cardBackHeight, cardFrontWidth, cardFrontHeight,
            cardPreviewWidth, cardPreviewHeight, cardPreview2Width, cardPreview2Height;
    private Resources res;
    private Bitmap menu, menuText, stackBackground, cardBack, cardFront, cardPreview, cardPreview2;
    private Bitmap[] menuBitMaps;
    private int savedCardTheme;

    public boolean checkResources() {
        return res != null;
    }

    public void setResources(Resources res) {
        this.res = res;
    }

    /**
     * Gets the menu previews
     *
     * @param index The position of the game, as in the order the user set up in the settings
     * @return a single bitmap
     */
    public Bitmap getMenu(int index) {
        Bitmap bitmap;

        if (menuBitMaps == null){
            menuBitMaps = new Bitmap[lg.getGameCount()];
        } else if (menuBitMaps[index]!=null){
            return menuBitMaps[index];
        }

        if (menu == null) {
            menu = BitmapFactory.decodeResource(res, R.drawable.backgrounds_menu);
            menuWidth = menu.getWidth() / 6;
            menuHeight = menu.getHeight() / 3;
        }

        if (menuText == null){
            menuText = BitmapFactory.decodeResource(res, R.drawable.backgrounds_menu_text);
        }

        int posX = index%6;
        int posY = index/6;

        try {
            //just the preview of the game itself
            Bitmap gamePicture = Bitmap.createBitmap(menu, posX * menuWidth, posY * menuHeight, menuWidth, menuHeight);
            //get the game name picture
            Bitmap gameText = drawTextToBitmap(lg.getGameName(res,index));
            //append both parts
            bitmap = putTogether(gamePicture,gameText);
        } catch (Exception e){
            Log.e("Bitmap.getMenu()","No picture for current game available\n" + e.toString());
            bitmap = BitmapFactory.decodeResource(res, R.drawable.no_picture_available);
        }

        menuBitMaps[index] = bitmap;

        return bitmap;
    }

    /*
     * draw text on the pictures.
     *
     * Thanks to this article for the code!
     * https://www.skoumal.net/en/android-drawing-multiline-text-on-bitmap/
     */
    private Bitmap drawTextToBitmap(String text) {

        // prepare canvas
        float scale = res.getDisplayMetrics().density;
        Bitmap bitmap = Bitmap.createBitmap(menuText);

        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();

        if(bitmapConfig == null) {                                                                  //set default bitmap config if none
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }

        bitmap = bitmap.copy(bitmapConfig, true);                                                   //make bitmap mutable
        Canvas canvas = new Canvas(bitmap);

        TextPaint paint=new TextPaint(Paint.ANTI_ALIAS_FLAG);                                       //new antialiased Paint
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);                                              //text shadow
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));                        //set bold
        paint.setColor(Color.rgb(0,0,0));                                                           //set black color

        int textWidth = canvas.getWidth() - (int) (5 * scale);                                      //set text width to canvas width minus 5dp padding
        int textHeight;
        int textScale = 80;
        StaticLayout textLayout;

        //try to generate the text with the biggest size possible first. If the text height is greater
        //than the bitmap height, shrink it and try again. minimum scale factor is set to 10 (very small)
        do {
            paint.setTextSize(textScale);

            textLayout = new StaticLayout(text, paint, textWidth,                                   // nit StaticLayout for text
                    Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

            textHeight = textLayout.getHeight();                                                    //get height of multiline text

            textScale--;                                                                            //reduce text size for possible next iteration
        } while(textHeight >= bitmap.getHeight() && textScale >10);

        // get position of text's top left corner
        float x = (bitmap.getWidth() - textWidth)/2;
        float y = (bitmap.getHeight() - textHeight)/2;

        // draw text to the Canvas center
        canvas.save();
        canvas.translate(x, y);
        textLayout.draw(canvas);
        canvas.restore();

        return bitmap;
    }

    /*
     * puts two bitmaps vertically together
     */
    private static Bitmap putTogether(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight() + bmp2.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, 0,0, null);
        canvas.drawBitmap(bmp2, 0, bmp1.getHeight(), null);
        return bmOverlay;
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

        if (cardFront == null || savedCardTheme != prefs.getSavedCardTheme()) {

            savedCardTheme = prefs.getSavedCardTheme();
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
                case 9:
                    resID = R.drawable.cards_paris;
                    break;
                case 10:
                    resID = R.drawable.cards_dondorf;
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
            cardBackWidth = cardBack.getWidth() / NUM_CARD_BACKGROUNDS;
            cardBackHeight = cardBack.getHeight() / 4;
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
            cardPreviewWidth = cardPreview.getWidth() / NUM_CARD_THEMES;
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

        posX = posX * 2 + 1;

        if (cardPreview2 == null) {
            cardPreview2 = BitmapFactory.decodeResource(res, R.drawable.card_previews);
            cardPreview2Width = cardPreview2.getWidth() / (NUM_CARD_THEMES * 2);
            cardPreview2Height = cardPreview2.getHeight() / 2;
        }

        return Bitmap.createBitmap(cardPreview2, posX * cardPreview2Width,
                posY * cardPreview2Height, cardPreview2Width, cardPreview2Height);
    }

    /**
     * Resets the menu preview. Used after changing the locale, so the correct new previews will be shown
     */
    public void resetMenuPreviews() {
        menuBitMaps = null;
    }
}

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

import android.content.Context;

/**
 * Custom image view to prevent bugs. Setting an animation listener to the translate anim to move
 * cards has a problem: The onAnimationEnd() isn't called always, so in that case the corresponding
 * card will still be marked as animating and the app doesn't respond anymore. (For example: Rotate
 * the screen, so the cards will be dealt again, then turn off the screen. After turning it on again,
 * the app doesn't respond, because one card is still set to be animating.)
 * <p>
 * This answer on StackOverflow http://stackoverflow.com/a/5110476/7016229 stated, that there is a
 * bug in the animationListener, the solution is to create a custom imageView class and override
 * the onAnimationStart() and onAnimationEnd() methods. I still had the problem that the movement
 * produces flickering. This was solved by setting animation.setFillEnabled(true) before starting
 * the animation
 * <p>
 * There is also another problem: In very rare cases, where card is moved to multiple locations
 * (first to stack x, then to stack y, then...) in a single loop, the location of the image view
 * isn't updated properly. My solution is to do the calculation part first and THEN move the image views.
 */

public class CustomImageView extends android.support.v7.widget.AppCompatImageView {

    private boolean animating, moveAtEnd;
    private float destX, destY;

    private boolean isCard, isStack;

    public CustomImageView(Context context) {
        super(context);
    }

    /*
     * Sets the necessary data to this object. The ontouchListener is set to all image Views, because
     * the tap-to-select movement needs that.
     */
    public CustomImageView(Context context, OnTouchListener listener, Object object, int ID) {
        super(context);

        if (listener != null) {
            setOnTouchListener(listener);
        }

        setId(ID);

        switch (object) {
            case CARD:
                isCard = true;
                break;
            case STACK:
                isStack = true;
        }
    }

    @Override
    protected void onAnimationStart() {
        super.onAnimationStart();
        animating = true;
    }

    /**
     * ends the animation. If a destination is set, also move it there
     */
    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
        animating = false;

        if (moveAtEnd) {
            moveAtEnd = false;
            clearAnimation();
            setX(destX);
            setY(destY);
        }
    }

    /**
     * Sets a destination to apply at the end of a animation. Only used for the Translate Anim of
     * card movements
     *
     * @param pX The X-coordinate of the destination
     * @param pY The X-coordinate of the destination
     */
    public void setDestination(float pX, float pY) {
        moveAtEnd = true;
        destX = pX;
        destY = pY;
    }

    public void stopAnim() {
        animating = false;
        clearAnimation();
    }

    public boolean isAnimating() {
        return animating;
    }

    public boolean belongsToCard() {
        return isCard;
    }

    public boolean belongsToStack() {
        return isStack;
    }

    public enum Object {
        CARD, STACK
    }
}

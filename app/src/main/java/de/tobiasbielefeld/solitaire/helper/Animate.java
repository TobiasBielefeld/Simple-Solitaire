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

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.Stack;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/*
 * class for all card animations. Like moving cards and fading them out and in for hints.
 */

public class Animate {

    private int mCardIsAnimating=0;                                                                   //if greater than zero, some card is animating

    void wonAnimation() {                                                                           //animation played when game is won. it flies cards out the screen
        int direction = 0;                                                                          //direction is the side of the screen (right, left or bottom)
        int counter = 0;                                                                            //counter contains the x or y coordinate, so the cards are like in a mass wehn flying out the screen

        for (Card card : cards) {                                                                   //use every card
            switch (direction) {
                case 0: default:                                                                    //right side
                    card.setLocation(main_activity.layoutGame.getWidth(), counter);
                    counter += Card.sHeight;

                    if (counter >= main_activity.layoutGame.getHeight()) {                          //if the counter reached the maximum height, switch to the next side
                        direction = 1;
                        counter = 0;
                    }

                    break;
                case 1:                                                                             //bottom side
                    card.setLocation(counter, main_activity.layoutGame.getHeight() + Card.sHeight);
                    counter += Card.sWidth;

                    if (counter >= main_activity.layoutGame.getWidth()) {                           //if the counter reached the maximum width, switch to the next side
                        direction = 2;
                        counter = 0;
                    }

                    break;
                case 2:                                                                             //left side
                    card.setLocation(-Card.sWidth, counter);
                    counter += Card.sHeight;

                    if (counter >= main_activity.layoutGame.getHeight()) {                          //if the counter reached the maximum height, switch to the next side
                        direction = 0;
                        counter = 0;
                    }

                    break;
            }
        }
    }

    void cardHint(final Card card, final int offset, final Stack stack) {                    //animation for card hints, they are moved to the destination stack, faded out and faded in on the origin stack
        card.mView.bringToFront();                                                                  //first bring the card to front
        card.saveOldLocation();                                                                     //save the old location
        float dist_x = stack.mView.getX() - card.mView.getX();                                      //get the distance in x and y...
        float dist_y = stack.getYPosition(offset) - card.mView.getY();                              //...because TranslateAnimation takes deltaX and deltaY, and not absolut values
        int distance = (int) Math.sqrt((double) ((dist_x * dist_x) + (dist_y * dist_y)));           //calculate the distance from start to end, to use it als animation duration

        TranslateAnimation animation = new TranslateAnimation(                                      //make new animation
                0,                                                                                  //delta start x
                stack.mView.getX() - card.mView.getX(),                                             //delta destination x
                0,                                                                                  //delta start y
                stack.getYPosition(offset) - card.mView.getY());                                    //delta destination y (getYPosition with offset, because cards on the tableau have spacings

        animation.setDuration((distance * 100) / Card.sWidth);                                      //set the duration with the distance normed to the card width (so speed is on all display sizes the same
        animation.setAnimationListener(new Animation.AnimationListener() {                          //set an animation listener for start and end
            public void onAnimationStart(Animation animation) {                                     //on start, increment the animating status (will be decremented in end of showCard())
                mCardIsAnimating++;
            }

            public void onAnimationEnd(Animation animation) {                                       //on end, set the card to the destination coordinates and start the hide animation
                card.mView.setX(stack.mView.getX());                                                //and set the coordinates (without mView.bringToFront())
                card.mView.setY(stack.getYPosition(offset));
                hideCard(card);                                                                     //hide animation
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });

        card.mView.startAnimation(animation);                                                       //finally start the animation
    }

    private void hideCard(final Card card) {                                                        //fade a card out, and start the show animation afterwards
        Animation card_fade_out = AnimationUtils.loadAnimation(                                     //make new fade out animation
                main_activity.getApplicationContext(), R.anim.card_fade_out);

        card_fade_out.setAnimationListener(new Animation.AnimationListener() {                      //add a animation listener which hides the card after the animation
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {                                       //on end, hide the card and start the show animation
                card.mView.setVisibility(View.INVISIBLE);
                showCard(card);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });

        card.mView.startAnimation(card_fade_out);
    }

    private void showCard(final Card card) {
        Animation card_fade_in = AnimationUtils.loadAnimation(                                      //new fade in animation
                main_activity.getApplicationContext(), R.anim.card_fade_in);

        card_fade_in.setAnimationListener(new Animation.AnimationListener() {                       //add a listener
            public void onAnimationStart(Animation animation) {                                     //return to the old position and make the card visible
                card.returnToOldLocation();
                card.mView.setVisibility(View.VISIBLE);
            }

            public void onAnimationEnd(Animation animation) {                                       //at end, decrement the animating status
                mCardIsAnimating--;
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });

        card.mView.startAnimation(card_fade_in);                                                    //start animation
    }

    public void moveCard(final Card card, final float pX, final float pY) {
        final View view = card.mView;

        TranslateAnimation animation = new TranslateAnimation(                                      //set the coordinates for animation. From current position (0,0) to (pX,pY)
                0,
                pX - view.getX(),
                0,
                pY - view.getY());

        int distance = (int) Math.sqrt(Math.pow(pX - view.getX(), 2) + Math.pow(pY - view.getY(), 2));//calculate distance between start and end point
        animation.setDuration(distance * 100 / Card.sWidth);                                        //and set is as duration, normed to the card width. So movement has on different display sizes the same speed
        animation.setAnimationListener(new Animation.AnimationListener() {                          //add a listener
            public void onAnimationStart(Animation animation) {                                     //on start, increment the status
                mCardIsAnimating++;
            }

            public void onAnimationEnd(Animation animation) {                                       //on end set the coordinates and decrement the status
                view.clearAnimation();                                                              //clear the animation (or else screen flickering occures
                view.setX(pX);                                                                      //and set the view position
                view.setY(pY);
                mCardIsAnimating--;                                                                 //decrement the status
            }

            public void onAnimationRepeat(Animation animation) {
            }

        });

        view.startAnimation(animation);                                                             //start animation
    }

    public boolean cardIsAnimating() {                                                              //return if something is animating
        return mCardIsAnimating != 0;
    }

    void reset() {                                                                           //reset the animation status
        mCardIsAnimating = 0;
    }

    public void flipCard(final Card card, final boolean mode) {                                     //first part of the flip animation
        AnimatorSet shrinkSet = (AnimatorSet) AnimatorInflater.loadAnimator(
                main_activity, R.animator.card_to_middle);
        shrinkSet.addListener(new Animator.AnimatorListener() {
            public void onAnimationStart(Animator animation) {
            }

            public void onAnimationEnd(Animator animation) {
                flipCard2(card, mode);
            }

            public void onAnimationCancel(Animator animation) {
            }

            public void onAnimationRepeat(Animator animation) {
            }
        });

        shrinkSet.setTarget(card.mView);
        shrinkSet.start();
    }

    private void flipCard2(final Card card, final boolean mode) {                                   //second part of the flip animation
        AnimatorSet growSet = (AnimatorSet) AnimatorInflater.loadAnimator(
                main_activity, R.animator.card_from_middle);
        growSet.addListener(new Animator.AnimatorListener() {
            public void onAnimationStart(Animator animation) {
                if (mode)   //flip up
                    card.mView.setImageResource(Card.sDrawables[card.getID()]);
                else //flip down
                    card.mView.setImageResource(Card.sBackground);
            }

            public void onAnimationEnd(Animator animation) {
            }

            public void onAnimationCancel(Animator animation) {
            }

            public void onAnimationRepeat(Animator animation) {
            }
        });

        growSet.setTarget(card.mView);
        growSet.start();
    }

    void showAutoCompleteButton() {
        Animation fade_in = AnimationUtils.loadAnimation(                                           //new fade in animation
                main_activity.getApplicationContext(), R.anim.button_fade_in);

        fade_in.setAnimationListener(new Animation.AnimationListener() {                            //add a listener
            public void onAnimationStart(Animation animation) {                                     //return to the old position and make the card visible

                main_activity.buttonAutoComplete.setVisibility(View.VISIBLE);
            }

            public void onAnimationEnd(Animation animation) {}

            public void onAnimationRepeat(Animation animation) {}
        });

        main_activity.buttonAutoComplete.startAnimation(fade_in);                                   //start animation
    }
}
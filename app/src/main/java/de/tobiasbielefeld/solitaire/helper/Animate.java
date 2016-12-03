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
import android.graphics.PointF;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import java.util.Random;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.handler.AfterWonHandler;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/*
 * class for all card animations. Like moving cards and fading them out and in for hints.
 */

public class Animate{

    public AfterWonHandler afterWonHandler;
    private int cardIsAnimating=0;                                                                  //if greater than zero, some card is animating
    private GameManager gm;

    public Animate(GameManager gm){
        this.gm = gm;
        afterWonHandler = new AfterWonHandler(gm);
    }

    public void wonAnimation() {
        int direction = 0;
        int counter = 0;                                                                            //counter contains the x or y coordinate, so the cards are flying out the screen
        Random rand = new Random();

        for (Card card : cards) {
            switch (direction) {
                case 0: default://right side
                    card.setLocation(gm.layoutGame.getWidth(), counter);
                    counter += 3*Card.height;

                    if (counter >= gm.layoutGame.getHeight()) {
                        direction = 1;
                        counter = rand.nextInt(Card.height);
                    }

                    break;
                case 1://bottom side
                    card.setLocation(counter, gm.layoutGame.getHeight() + Card.height);
                    counter += 3*Card.width;

                    if (counter >= gm.layoutGame.getWidth()) {
                        direction = 2;
                        counter = rand.nextInt(Card.width);
                    }

                    break;
                case 2://left side
                    card.setLocation(-Card.width, counter);
                    counter += 3*Card.height;

                    if (counter >= gm.layoutGame.getHeight()) {
                        direction = 0;
                        counter = rand.nextInt(Card.height);
                    }

                    break;
            }
        }

        afterWonHandler.sendEmptyMessageDelayed(0,1000);
    }

    public void cardHint(final Card card, final int offset, final Stack stack) {
        card.view.bringToFront();
        card.saveOldLocation();
        PointF pointAtStack = stack.getPosition(offset);
        float dist_x = pointAtStack.x - card.view.getX();
        float dist_y = pointAtStack.y - card.view.getY();
        int distance = (int) Math.sqrt((double) ((dist_x * dist_x) + (dist_y * dist_y)));

        TranslateAnimation animation = new TranslateAnimation(
                0,
                dist_x,
                0,
                dist_y);

        animation.setDuration((distance * 100) / Card.width);
        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {                                     //on start, increment the animating status (will be decremented in end of showCard())
                cardIsAnimating++;
            }

            public void onAnimationEnd(Animation animation) {
                PointF pointAtStack = stack.getPosition(offset);
                card.view.setX(pointAtStack.x);
                card.view.setY(pointAtStack.y);
                hideCard(card);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });

        card.view.startAnimation(animation);
    }

    private void hideCard(final Card card) {
        Animation card_fade_out = AnimationUtils.loadAnimation(
                gm.getApplicationContext(), R.anim.card_fade_out);

        card_fade_out.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                card.view.setVisibility(View.INVISIBLE);
                showCard(card);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });

        card.view.startAnimation(card_fade_out);
    }

    private void showCard(final Card card) {
        Animation card_fade_in = AnimationUtils.loadAnimation(
                gm.getApplicationContext(), R.anim.card_fade_in);

        card_fade_in.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                card.returnToOldLocation();
                card.view.setVisibility(View.VISIBLE);
            }

            public void onAnimationEnd(Animation animation) {                                       //at end, decrement the animating status
                cardIsAnimating--;
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });

        card.view.startAnimation(card_fade_in);
    }

    public void moveCard(final Card card, final float pX, final float pY) {
        final View view = card.view;

        TranslateAnimation animation = new TranslateAnimation(
                0,
                pX - view.getX(),
                0,
                pY - view.getY());

        int distance = (int) Math.sqrt(Math.pow(pX - view.getX(), 2) + Math.pow(pY - view.getY(), 2));
        animation.setDuration(distance * 100 / Card.width);
        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {                                     //on start, increment the status
                cardIsAnimating++;
            }

            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                view.setX(pX);
                view.setY(pY);
                cardIsAnimating--;
            }

            public void onAnimationRepeat(Animation animation) {
            }

        });

        view.startAnimation(animation);
    }

    public boolean cardIsAnimating() {
        return cardIsAnimating != 0;
    }

    public void reset() {
        cardIsAnimating = 0;
    }

    public void flipCard(final Card card, final boolean mode) {
        AnimatorSet shrinkSet = (AnimatorSet) AnimatorInflater.loadAnimator(
                gm, R.animator.card_to_middle);
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

        shrinkSet.setTarget(card.view);
        shrinkSet.start();
    }

    private void flipCard2(final Card card, final boolean mode) {
        AnimatorSet growSet = (AnimatorSet) AnimatorInflater.loadAnimator(
                gm, R.animator.card_from_middle);
        growSet.addListener(new Animator.AnimatorListener() {
            public void onAnimationStart(Animator animation) {
                if (mode)   //flip up
                    card.view.setImageResource(Card.drawables[(card.getColor() - 1) *13 + card.getValue() - 1]);
                else //flip down
                    card.view.setImageResource(Card.background);
            }

            public void onAnimationEnd(Animator animation) {
            }

            public void onAnimationCancel(Animator animation) {
            }

            public void onAnimationRepeat(Animator animation) {
            }
        });

        growSet.setTarget(card.view);
        growSet.start();
    }

    public void showAutoCompleteButton() {
        Animation fade_in = AnimationUtils.loadAnimation(
                gm.getApplicationContext(), R.anim.button_fade_in);

        fade_in.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {

                gm.buttonAutoComplete.setVisibility(View.VISIBLE);
            }

            public void onAnimationEnd(Animation animation) {}

            public void onAnimationRepeat(Animation animation) {}
        });

        gm.buttonAutoComplete.startAnimation(fade_in);
    }
}
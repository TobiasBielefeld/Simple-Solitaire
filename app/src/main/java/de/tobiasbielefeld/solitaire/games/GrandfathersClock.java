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

package de.tobiasbielefeld.solitaire.games;

import android.content.Context;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.Stack;

import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.games.Game.testMode.*;
import static de.tobiasbielefeld.solitaire.games.Game.testMode2.*;
import static de.tobiasbielefeld.solitaire.games.Game.testMode3.*;

/**
 * Grandfathers Clock. First game with a completely different layout for portrait and landscape.
 * It is very easy to win.
 */

public class GrandfathersClock extends Game {

    //to know which card gets on a empty foundation field
    int[] foundationCardOrder = new int[]{7, 8, 9, 10, 11, 6, 12, 5, 4, 3, 2, 13};
    //which family is placed on the foundation fields
    int[] foundationFamilyOrder = new int[]{2, 3, 0, 1, 2, 1, 3, 0, 3, 2, 1, 0};

    public GrandfathersClock() {
        setNumberOfDecks(1);
        setNumberOfStacks(21);

        setTableauStackIDs(0,1,2,3,4,5,7);
        setFoundationStackIDs(8,9,10,11,12,13,14,15,16,17,18,19);
        setDealFromID(20);
        setMixingCardsTestMode(testMode.DOESNT_MATTER);
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape, Context context) {

        if (isLandscape) {
            setStacksLandscape(layoutGame);
        } else {
            setStacksPortrait(layoutGame);
        }

        //set foundation backgrounds
        stacks[18].setImageBitmap(Stack.background2);
        stacks[17].setImageBitmap(Stack.background3);
        stacks[16].setImageBitmap(Stack.background4);
        stacks[15].setImageBitmap(Stack.background5);
        stacks[13].setImageBitmap(Stack.background6);
        stacks[8].setImageBitmap(Stack.background7);
        stacks[9].setImageBitmap(Stack.background8);
        stacks[10].setImageBitmap(Stack.background9);
        stacks[11].setImageBitmap(Stack.background10);
        stacks[12].setImageBitmap(Stack.background11);
        stacks[14].setImageBitmap(Stack.background12);
        stacks[19].setImageBitmap(Stack.background13);
        stacks[20].setImageBitmap(Stack.backgroundTransparent);
    }

    private void setStacksPortrait(RelativeLayout layoutGame) {
        //stacking shouldn't go over the clock layout
        setDirectionBorders(-1, -1, -1, -1, -1, -1, -1, -1);

        // initialize the dimensions
        setUpCardDimensions(layoutGame, 9, 10);

        //calculate spacing and start position of cards
        int spacing = setUpHorizontalSpacing(layoutGame, 8, 9);
        int verticalSpacing = setUpVerticalSpacing(layoutGame, 9, 10);

        //first foundation stacks in a circle
        int startPosX = (int) (layoutGame.getWidth() / 2 - 2.5 * Card.width - 2 * spacing);
        int startPosY = Card.width / 2;

        stacks[8].setX(startPosX);
        stacks[8].setY(startPosY + 6 * verticalSpacing);

        stacks[9].setX(startPosX + Card.width + spacing);
        stacks[9].setY(startPosY + 3 * verticalSpacing);

        stacks[10].setX(startPosX + 2 * Card.width + 2 * spacing);
        stacks[10].setY(startPosY);

        stacks[11].setX(startPosX + 3 * Card.width + 3 * spacing);
        stacks[11].setY(startPosY + 3 * verticalSpacing);

        stacks[12].setX(startPosX + 4 * Card.width + 4 * spacing);
        stacks[12].setY(startPosY + 6 * verticalSpacing);

        //

        stacks[13].setX(stacks[8].getX() - Card.width / 2);
        stacks[13].setY(stacks[8].getY() + Card.height + verticalSpacing);

        stacks[14].setX(stacks[12].getX() + Card.width / 2);
        stacks[14].setY(stacks[12].getY() + Card.height + verticalSpacing);

        //

        startPosY = (int) (stacks[13].getY() + Card.height + verticalSpacing);

        stacks[15].setX(stacks[8].getX());
        stacks[15].setY(startPosY);

        stacks[16].setX(stacks[9].getX());
        stacks[16].setY(startPosY + 3 * verticalSpacing);

        stacks[17].setX(stacks[10].getX());
        stacks[17].setY(startPosY + 6 * verticalSpacing);

        stacks[18].setX(stacks[11].getX());
        stacks[18].setY(startPosY + 3 * verticalSpacing);

        stacks[19].setX(stacks[12].getX());
        stacks[19].setY(startPosY);

        //deal stack
        stacks[20].setX(stacks[10].getX());
        stacks[20].setY(stacks[13].getY());

        //then tableau stacks
        startPosX = layoutGame.getWidth() / 2 - spacing / 2 - 4 * Card.width - 3 * spacing;
        startPosY = (int) stacks[17].getY() + Card.height + Card.height / 2;

        for (int i = 0; i < 8; i++) {
            stacks[i].setX(startPosX + spacing * i + Card.width * i);
            stacks[i].setY(startPosY);
        }
    }

    private void setStacksLandscape(RelativeLayout layoutGame) {
        //stacking shouldn't go over the clock layout
        setDirectionBorders(4, 4, 4, 4, -1, -1, -1, -1);

        // initialize the dimensions
        setUpCardDimensions(layoutGame, 12, 6);

        //calculate spacing and start position of cards
        int spacing = setUpHorizontalSpacing(layoutGame, 11, 12);
        int verticalSpacing = setUpVerticalSpacing(layoutGame, 5, 7);

        //foundation stacks in a circle
        int startPosX = (layoutGame.getWidth() - 10 * Card.width - 9 * spacing) / 2 + Card.width / 2;
        int startPosY = layoutGame.getHeight() / 2 - Card.height / 2 - 7 * verticalSpacing - Card.height;

        stacks[8].setX(startPosX);
        stacks[8].setY(startPosY + 6 * verticalSpacing);

        stacks[9].setX(startPosX + Card.width + spacing);
        stacks[9].setY(startPosY + 3 * verticalSpacing);

        stacks[10].setX(startPosX + 2 * Card.width + 2 * spacing);
        stacks[10].setY(startPosY);

        stacks[11].setX(startPosX + 3 * Card.width + 3 * spacing);
        stacks[11].setY(startPosY + 3 * verticalSpacing);

        stacks[12].setX(startPosX + 4 * Card.width + 4 * spacing);
        stacks[12].setY(startPosY + 6 * verticalSpacing);

        //

        stacks[13].setX(stacks[8].getX() - Card.width / 2);
        stacks[13].setY(stacks[8].getY() + Card.height + verticalSpacing);

        stacks[14].setX(stacks[12].getX() + Card.width / 2);
        stacks[14].setY(stacks[12].getY() + Card.height + verticalSpacing);

        //

        startPosY = (int) (stacks[13].getY() + Card.height + verticalSpacing);

        stacks[15].setX(stacks[8].getX());
        stacks[15].setY(startPosY);

        stacks[16].setX(stacks[9].getX());
        stacks[16].setY(startPosY + 3 * verticalSpacing);

        stacks[17].setX(stacks[10].getX());
        stacks[17].setY(startPosY + 6 * verticalSpacing);

        stacks[18].setX(stacks[11].getX());
        stacks[18].setY(startPosY + 3 * verticalSpacing);

        stacks[19].setX(stacks[12].getX());
        stacks[19].setY(startPosY);

        startPosX = (int) (stacks[14].getX() + Card.width + 2 * spacing);
        startPosY = Card.height / 2;

        //deal stack
        stacks[20].setX(stacks[10].getX());
        stacks[20].setY(stacks[13].getY());

        //tableau stacks
        for (int i = 0; i < 4; i++) {
            stacks[i].setX(startPosX + spacing * i + Card.width * i);
            stacks[i].setY(startPosY);
        }
        for (int i = 0; i < 4; i++) {
            stacks[4 + i].setX(startPosX + spacing * i + Card.width * i);
            stacks[4 + i].setY((layoutGame.getHeight() - Card.height) / 2 + Card.height / 2);
        }
    }

    public void dealCards() {
        flipAllCardsUp();

        for (int i = 0; i < foundationCardOrder.length; i++) {
            Card cardToMove = cards[foundationFamilyOrder[i] * 13 + foundationCardOrder[i] - 1];
            moveToStack(cardToMove, stacks[8 + i], OPTION_NO_RECORD);
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 5; j++) {
                moveToStack(getDealStack().getTopCard(), stacks[i], OPTION_NO_RECORD);
            }
        }
    }

    public int onMainStackTouch() {
        //no main stack
        return 0;
    }

    public boolean cardTest(Stack stack, Card card) {
        //there is a invisible deal stack in the middle of the clock, which shouldn't be used for the movement
        if (card.getStackId() > getLastTableauId() || stack.getId() == getDealStack().getId()) {
            return false;
        }

        if (stack.getId() <= getLastTableauId()) {
            //if there are as many cards moving as free stacks, and one of the free stacks was chosen, don't move
            int movingCards = card.getStack().getSize() - card.getIndexOnStack();

            return movingCards <= getPowerMoveCount(stack.isEmpty()) && canCardBePlaced(stack, card, DOESNT_MATTER, DESCENDING);
        } else {
            return movingCards.hasSingleCard() && canCardBePlaced(stack, card, SAME_FAMILY, ASCENDING, true);
        }
    }

    public boolean addCardToMovementGameTest(Card card) {
        Stack sourceStack = card.getStack();

        int startPos = max(sourceStack.getSize() - getPowerMoveCount(false), card.getStack().getIndexOfCard(card));

        return card.getStack().getIndexOfCard(card) >= startPos && testCardsUpToTop(sourceStack, startPos, DOESNT_MATTER);
    }

    public CardAndStack hintTest(ArrayList<Card> visited) {
        for (int i = 0; i < 8; i++) {

            Stack sourceStack = stacks[i];

            if (sourceStack.isEmpty()) {
                continue;
            }

            int startPos = max(sourceStack.getSize() - getPowerMoveCount(false), 0);

            for (int j = startPos; j < sourceStack.getSize(); j++) {
                Card cardToMove = sourceStack.getCard(j);

                if (visited.contains(cardToMove) || !testCardsUpToTop(sourceStack, j, DOESNT_MATTER)) {
                    continue;
                }

                if (cardToMove.isTopCard()) {
                    for (int k = 0; k < 12; k++) {
                        if (cardToMove.test(stacks[8+k])) {
                            return new CardAndStack(cardToMove, stacks[8+k]);
                        }
                    }
                }

                for (int k = 0; k < 8; k++) {
                    Stack destStack = stacks[k];

                    if (i == k || destStack.isEmpty()) {
                        continue;
                    }

                    if (cardToMove.test(destStack)) {
                        if (sameCardOnOtherStack(cardToMove, destStack, SAME_VALUE)) {
                            continue;
                        }

                        return new CardAndStack(cardToMove, destStack);
                    }
                }
            }
        }

        return findBestSequenceToMoveToEmptyStack(DOESNT_MATTER);
    }

    public Stack doubleTapTest(Card card) {
        //first foundation
        if (card.isTopCard()) {
            for (int j = 0; j < 12; j++) {
                if (cardTest(stacks[8 + j], card)) {
                    return stacks[8 + j];
                }
            }
        }

        //then non empty fields
        for (int j = 0; j < 8; j++) {
            if (cardTest(stacks[j], card) && !stacks[j].isEmpty()
                    && !(card.getStackId() <= getLastTableauId() && sameCardOnOtherStack(card, stacks[j], SAME_VALUE))) {
                return stacks[j];
            }
        }

        //then the empty fields
        for (int j = 0; j < 8; j++) {
            if (stacks[j].isEmpty() && cardTest(stacks[j], card)) {
                return stacks[j];
            }
        }

        return null;
    }

    public boolean winTest() {
        for (int i = 0; i < 8; i++) {
            if (!stacks[i].isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public boolean autoCompleteStartTest() {
        for (int i = 0; i < 8; i++) {
            if (!testCardsUpToTop(stacks[i], 0, DOESNT_MATTER)) {
                return false;
            }
        }

        return true;
    }

    public CardAndStack autoCompletePhaseOne() {
        return null;
    }

    public CardAndStack autoCompletePhaseTwo() {
        for (int i = 0; i < 8; i++) {
            if (stacks[i].isEmpty()) {
                continue;
            }

            Card cardToTest = stacks[i].getTopCard();

            for (int j = 0; j < 12; j++) {
                if (cardTest(stacks[8 + j], cardToTest)) {
                    return new CardAndStack(cardToTest, stacks[8 + j]);
                }
            }
        }

        return null;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs, boolean isUndoMovement) {
        //anywhere to foundation
        if (destinationIDs[0] >= 8 && destinationIDs[0] < 19) {
            return 50;
        } else {
            return 0;
        }

    }

    private int getPowerMoveCount(boolean movingToEmptyStack){
        return getPowerMoveCount(new int[]{}, new int[]{0,1,2,3,4,5,6,7}, movingToEmptyStack);
    }
}

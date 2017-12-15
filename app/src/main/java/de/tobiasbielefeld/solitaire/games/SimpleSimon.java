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

/**
 * Simple Simon Game! It's nearly like Spider, but with less cards and all cards are
 * already faced up at start. This game extends Spider, so some methods aren't declared here.
 */

public class SimpleSimon extends Spider {

    public SimpleSimon() {
        setNumberOfDecks(1);
        setNumberOfStacks(14);

        setTableauStackIDs(0,1,2,3,4,5,6,7,8,9);
        setFoundationStackIDs(10,11,12,13);
        setDealFromID(0);

        //because this game inherits from Spider, I have to disable the main stack from it
        disableMainStack();
        setMixingCardsTestMode(testMode.DOESNT_MATTER);
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape, Context context) {
        //initialize the dimensions
        setUpCardWidth(layoutGame, isLandscape, 11, 12);
        int spacing = setUpHorizontalSpacing(layoutGame, 10, 11);
        int startPos = layoutGame.getWidth() / 2 - 2 * Card.width - (int) (1.5 * spacing);

        //foundation stacks
        for (int i = 0; i < 4; i++) {
            stacks[10 + i].setX(startPos + spacing * i + Card.width * i);
            stacks[10 + i].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1);
        }

        //tableau stacks
        startPos = layoutGame.getWidth() / 2 - 5 * Card.width - 4 * spacing - spacing / 2;
        for (int i = 0; i < 10; i++) {
            stacks[i].setX(startPos + spacing * i + Card.width * i);
            stacks[i].setY(stacks[10].getY() + Card.height + (isLandscape ? Card.width / 4 : Card.width / 2) + 1);
        }
    }

    public boolean winTest() {
        return (stacks[10].getSize() == 13 && stacks[11].getSize() == 13 && stacks[12].getSize() == 13 && stacks[13].getSize() == 13);
    }

    public void dealCards() {
        flipAllCardsUp();

        for (int i = 1; i < 7; i++) {
            for (int j = 0; j < 1 + i; j++) {
                moveToStack(getDealStack().getTopCard(), stacks[i], OPTION_NO_RECORD);
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                moveToStack(getDealStack().getTopCard(), stacks[7 + i], OPTION_NO_RECORD);
            }
        }
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs, boolean isUndoMovement) {
        if (destinationIDs[0] >= 10 && destinationIDs[0] < 14) {
            return 200;
        } else {
            return 0;
        }
    }

    public int onMainStackTouch() {
        //no main stack so empty
        return 0;
    }

    public boolean autoCompleteStartTest() {
        for (int i = 0; i < 10; i++) {
            if (stacks[i].getSize() > 0 && (stacks[i].getFirstUpCardPos() != 0 || !testCardsUpToTop(stacks[i], 0, SAME_FAMILY))) {
                return false;
            }
        }

        return true;
    }

    public CardAndStack autoCompletePhaseOne() {

        for (int i = 0; i < 10; i++) {
            Stack sourceStack = stacks[i];

            if (sourceStack.isEmpty()) {
                continue;
            }

            Card cardToMove = sourceStack.getCard(0);

            for (int k = 0; k < 10; k++) {
                Stack destStack = stacks[k];

                if (i == k || destStack.isEmpty() || destStack.getTopCard().getColor() != cardToMove.getColor()) {
                    continue;
                }

                if (cardToMove.test(destStack)) {
                    return new CardAndStack(cardToMove, destStack);
                }
            }
        }

        return null;
    }
}

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

import android.widget.RelativeLayout;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.Stack;

import static de.tobiasbielefeld.solitaire.SharedData.DEFAULT_VEGAS_DRAW;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_VEGAS_DRAW;
import static de.tobiasbielefeld.solitaire.SharedData.PREF_KEY_VEGAS_DRAW_OLD;
import static de.tobiasbielefeld.solitaire.SharedData.scores;
import static de.tobiasbielefeld.solitaire.SharedData.stacks;

/**
 * Vegas game! It's like Klondike, but with some changes and different scoring.
 */

public class Vegas extends Klondike {

    public Vegas(){
        super();

        PREF_KEY_DRAW_OLD = PREF_KEY_VEGAS_DRAW_OLD;
        PREF_KEY_DRAW = PREF_KEY_VEGAS_DRAW;
        DEFAULT_DRAW = DEFAULT_VEGAS_DRAW;
    }

    @Override
    public void dealCards() {
        super.dealCards();

        scores.update(-52);
    }

    @Override
    public int onMainStackTouch() {
        return realOnMainStackTouch(false);
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape) {
        super.setStacks(layoutGame,isLandscape);

        stacks[14].view.setImageBitmap(Stack.backgroundDefault);
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs, boolean isUndoMovement) {
        int originID = originIDs[0];
        int destinationID = destinationIDs[0];

        if (destinationID >= 7 && destinationID <= 10){
            return 5;
        }

        return 0;
    }
}

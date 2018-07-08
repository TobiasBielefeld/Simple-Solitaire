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

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.HelperCardMovement;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.animate;
import static de.tobiasbielefeld.solitaire.SharedData.currentGame;
import static de.tobiasbielefeld.solitaire.SharedData.gameLogic;
import static de.tobiasbielefeld.solitaire.SharedData.handlerTestAfterMove;
import static de.tobiasbielefeld.solitaire.SharedData.logText;
import static de.tobiasbielefeld.solitaire.SharedData.max;
import static de.tobiasbielefeld.solitaire.SharedData.moveToStack;
import static de.tobiasbielefeld.solitaire.SharedData.prefs;
import static de.tobiasbielefeld.solitaire.SharedData.scores;
import static de.tobiasbielefeld.solitaire.SharedData.sounds;

/*
 *
 */

public class DealCards extends HelperCardMovement {

    private int phase = 1;

    public DealCards(GameManager gm) {
        super(gm, "DEAL_CARDS");
    }

    public void start() {
        phase = 1;
        super.start();
    }

    @Override
    protected void saveState(Bundle bundle) {
        bundle.putInt("BUNDLE_DEAL_CARDS_PHASE", phase);
    }

    @Override
    protected void loadState(Bundle bundle) {
        phase = bundle.getInt("BUNDLE_DEAL_CARDS_PHASE");
    }

    @Override
    protected void moveCard() {
        switch (phase){
            case 1:
                currentGame.dealNewGame();
                sounds.playSound(Sounds.names.DEAL_CARDS);
                phase = 2;
                nextIteration();
                break;
            case 2: default:
                handlerTestAfterMove.sendNow();
                stop();
                break;
        }
    }
}

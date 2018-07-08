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

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.HelperCardMovement;
import de.tobiasbielefeld.solitaire.games.Pyramid;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.currentGame;
import static de.tobiasbielefeld.solitaire.SharedData.movingCards;
import static de.tobiasbielefeld.solitaire.SharedData.prefs;
import static de.tobiasbielefeld.solitaire.SharedData.showToast;

/**
 * if the last card on the tableau is flipped up, the auto complete can be run. it simply test
 * every card from the tableau and the stock if they can be placed on the foundation.
 * it continues until the last card was moved to the foundation. after that,
 * the win animation will be started
 */

public class AutoMove extends HelperCardMovement {

    private boolean testAfterMove = false;
    private boolean movedFirstCard = false;
    private boolean mainStackAlreadyFlipped = false;

    public AutoMove(GameManager gm){
        super(gm,"AUTO_MOVE");
    }

    @Override
    public void start(){
        movedFirstCard = false;
        testAfterMove = false;
        mainStackAlreadyFlipped = false;

        super.start();
    }

    @Override
    protected void saveState(Bundle bundle) {
    }

    @Override
    protected void loadState(Bundle bundle) {
    }

    @Override
    protected boolean stopCondition() {
        return currentGame.winTest();
    }

    @Override
    protected void moveCard() {

        if (testAfterMove) {
            currentGame.testAfterMove();
            testAfterMove = false;
            nextIteration();
        }
        else {
            CardAndStack cardAndStack = currentGame.hintTest();

            if (cardAndStack != null) {
                mainStackAlreadyFlipped = false;
                movedFirstCard = true;
                movingCards.reset();

                //needed because in Pyramid, I save in cardTest() if cards need to move to the waste stack
                //TODO manage this in another way
                if (currentGame instanceof Pyramid){
                    currentGame.cardTest(cardAndStack.getStack(),cardAndStack.getCard());
                }

                movingCards.add(cardAndStack.getCard(), 0, 0);
                movingCards.moveToDestination(cardAndStack.getStack());

                testAfterMove = true;
                nextIteration();
            }
            else if (prefs.getImproveAutoMove() && currentGame.hasMainStack()) {
                switch (currentGame.mainStackTouch()){
                    case 0:
                        stop();
                    case 1:
                        testAfterMove = true;
                        nextIteration();
                        break;
                    case 2:
                        if (mainStackAlreadyFlipped) {
                            stop();
                        } else {
                            mainStackAlreadyFlipped = true;
                            testAfterMove = true;
                            nextIteration();
                        }
                        break;
                }
            }
            else {
                if (!movedFirstCard) {
                    showToast(gm.getString(R.string.dialog_no_movement_possible),gm);
                }

                stop();
            }
        }
    }
}

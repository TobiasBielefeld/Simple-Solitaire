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

import android.view.View;

import de.tobiasbielefeld.solitaire.handler.AutoCompleteHandler;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.animate;

/*
 *  if the last card on the tableau is flipped up, the auto complete can be run. it simply test
 *  every card from the tableau and the stock if they can be placed on the foundation.
 *  it continues until the last card was moved to the foundation. after that,
 *  the win animation will be started
 */

public class AutoComplete {

    public AutoCompleteHandler autoCompleteHandler = new AutoCompleteHandler();
    private boolean running = false;                                                                  //shows if the autocomplete is still running
    private boolean buttonShown = false;
    private GameManager gm;

    public AutoComplete(GameManager gm) {
        this.gm = gm;
    }

    public void reset() {
        autoCompleteHandler.reset();
        hideButton();
        running = false;
    }

    public void start() {
        running = true;
        hideButton();
        autoCompleteHandler.reset();
        autoCompleteHandler.sendEmptyMessage(0);
    }

    public boolean isRunning() {
        return running;
    }

    public boolean buttonIsShown() {
        return buttonShown;
    }

    public void showButton() {
        buttonShown = true;
        animate.showAutoCompleteButton();
    }

    public void hideButton() {
        buttonShown = false;
        gm.buttonAutoComplete.setVisibility(View.GONE);
    }
}

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

import de.tobiasbielefeld.solitaire.*;

/*
 *  I put the drawable arrays in a extra file so the Card.java remains more clearly.
 */

public class CardDrawables
{
    public final static int[] sDrawablesClassic = {
            R.drawable.classic_clubs_1, R.drawable.classic_clubs_2, R.drawable.classic_clubs_3, R.drawable.classic_clubs_4, R.drawable.classic_clubs_5, R.drawable.classic_clubs_6,
            R.drawable.classic_clubs_7, R.drawable.classic_clubs_8, R.drawable.classic_clubs_9, R.drawable.classic_clubs_10, R.drawable.classic_clubs_11, R.drawable.classic_clubs_12, R.drawable.classic_clubs_13,

            R.drawable.classic_hearts_1, R.drawable.classic_hearts_2, R.drawable.classic_hearts_3, R.drawable.classic_hearts_4, R.drawable.classic_hearts_5, R.drawable.classic_hearts_6,
            R.drawable.classic_hearts_7, R.drawable.classic_hearts_8, R.drawable.classic_hearts_9, R.drawable.classic_hearts_10, R.drawable.classic_hearts_11, R.drawable.classic_hearts_12, R.drawable.classic_hearts_13,

            R.drawable.classic_spades_1, R.drawable.classic_spades_2, R.drawable.classic_spades_3, R.drawable.classic_spades_4, R.drawable.classic_spades_5, R.drawable.classic_spades_6,
            R.drawable.classic_spades_7, R.drawable.classic_spades_8, R.drawable.classic_spades_9, R.drawable.classic_spades_10, R.drawable.classic_spades_11, R.drawable.classic_spades_12, R.drawable.classic_spades_13,

            R.drawable.classic_diamonds_1, R.drawable.classic_diamonds_2, R.drawable.classic_diamonds_3, R.drawable.classic_diamonds_4, R.drawable.classic_diamonds_5, R.drawable.classic_diamonds_6,
            R.drawable.classic_diamonds_7, R.drawable.classic_diamonds_8, R.drawable.classic_diamonds_9, R.drawable.classic_diamonds_10, R.drawable.classic_diamonds_11, R.drawable.classic_diamonds_12, R.drawable.classic_diamonds_13
    };

    public final static int[] sDrawablesAbstract = {
            R.drawable.abstract_clubs_1, R.drawable.abstract_clubs_2, R.drawable.abstract_clubs_3, R.drawable.abstract_clubs_4, R.drawable.abstract_clubs_5, R.drawable.abstract_clubs_6,
            R.drawable.abstract_clubs_7, R.drawable.abstract_clubs_8, R.drawable.abstract_clubs_9, R.drawable.abstract_clubs_10, R.drawable.abstract_clubs_11, R.drawable.abstract_clubs_12, R.drawable.abstract_clubs_13,

            R.drawable.abstract_hearts_1, R.drawable.abstract_hearts_2, R.drawable.abstract_hearts_3, R.drawable.abstract_hearts_4, R.drawable.abstract_hearts_5, R.drawable.abstract_hearts_6,
            R.drawable.abstract_hearts_7, R.drawable.abstract_hearts_8, R.drawable.abstract_hearts_9, R.drawable.abstract_hearts_10, R.drawable.abstract_hearts_11, R.drawable.abstract_hearts_12, R.drawable.abstract_hearts_13,

            R.drawable.abstract_spades_1, R.drawable.abstract_spades_2, R.drawable.abstract_spades_3, R.drawable.abstract_spades_4, R.drawable.abstract_spades_5, R.drawable.abstract_spades_6,
            R.drawable.abstract_spades_7, R.drawable.abstract_spades_8, R.drawable.abstract_spades_9, R.drawable.abstract_spades_10, R.drawable.abstract_spades_11, R.drawable.abstract_spades_12, R.drawable.abstract_spades_13,

            R.drawable.abstract_diamonds_1, R.drawable.abstract_diamonds_2, R.drawable.abstract_diamonds_3, R.drawable.abstract_diamonds_4, R.drawable.abstract_diamonds_5, R.drawable.abstract_diamonds_6,
            R.drawable.abstract_diamonds_7, R.drawable.abstract_diamonds_8, R.drawable.abstract_diamonds_9, R.drawable.abstract_diamonds_10, R.drawable.abstract_diamonds_11, R.drawable.abstract_diamonds_12, R.drawable.abstract_diamonds_13
    };

    public final static int[] sDrawablesSimple = {
            R.drawable.simple_clubs_1, R.drawable.simple_clubs_2, R.drawable.simple_clubs_3, R.drawable.simple_clubs_4, R.drawable.simple_clubs_5, R.drawable.simple_clubs_6,
            R.drawable.simple_clubs_7, R.drawable.simple_clubs_8, R.drawable.simple_clubs_9, R.drawable.simple_clubs_10, R.drawable.simple_clubs_11, R.drawable.simple_clubs_12, R.drawable.simple_clubs_13,

            R.drawable.simple_hearts_1, R.drawable.simple_hearts_2, R.drawable.simple_hearts_3, R.drawable.simple_hearts_4, R.drawable.simple_hearts_5, R.drawable.simple_hearts_6,
            R.drawable.simple_hearts_7, R.drawable.simple_hearts_8, R.drawable.simple_hearts_9, R.drawable.simple_hearts_10, R.drawable.simple_hearts_11, R.drawable.simple_hearts_12, R.drawable.simple_hearts_13,

            R.drawable.simple_spades_1, R.drawable.simple_spades_2, R.drawable.simple_spades_3, R.drawable.simple_spades_4, R.drawable.simple_spades_5, R.drawable.simple_spades_6,
            R.drawable.simple_spades_7, R.drawable.simple_spades_8, R.drawable.simple_spades_9, R.drawable.simple_spades_10, R.drawable.simple_spades_11, R.drawable.simple_spades_12, R.drawable.simple_spades_13,

            R.drawable.simple_diamonds_1, R.drawable.simple_diamonds_2, R.drawable.simple_diamonds_3, R.drawable.simple_diamonds_4, R.drawable.simple_diamonds_5, R.drawable.simple_diamonds_6,
            R.drawable.simple_diamonds_7, R.drawable.simple_diamonds_8, R.drawable.simple_diamonds_9, R.drawable.simple_diamonds_10, R.drawable.simple_diamonds_11, R.drawable.simple_diamonds_12, R.drawable.simple_diamonds_13
    };

    public final static int[] sDrawablesModern = {
            R.drawable.modern_clubs_1, R.drawable.modern_clubs_2, R.drawable.modern_clubs_3, R.drawable.modern_clubs_4, R.drawable.modern_clubs_5, R.drawable.modern_clubs_6,
            R.drawable.modern_clubs_7, R.drawable.modern_clubs_8, R.drawable.modern_clubs_9, R.drawable.modern_clubs_10, R.drawable.modern_clubs_11, R.drawable.modern_clubs_12, R.drawable.modern_clubs_13,

            R.drawable.modern_hearts_1, R.drawable.modern_hearts_2, R.drawable.modern_hearts_3, R.drawable.modern_hearts_4, R.drawable.modern_hearts_5, R.drawable.modern_hearts_6,
            R.drawable.modern_hearts_7, R.drawable.modern_hearts_8, R.drawable.modern_hearts_9, R.drawable.modern_hearts_10, R.drawable.modern_hearts_11, R.drawable.modern_hearts_12, R.drawable.modern_hearts_13,

            R.drawable.modern_spades_1, R.drawable.modern_spades_2, R.drawable.modern_spades_3, R.drawable.modern_spades_4, R.drawable.modern_spades_5, R.drawable.modern_spades_6,
            R.drawable.modern_spades_7, R.drawable.modern_spades_8, R.drawable.modern_spades_9, R.drawable.modern_spades_10, R.drawable.modern_spades_11, R.drawable.modern_spades_12, R.drawable.modern_spades_13,

            R.drawable.modern_diamonds_1, R.drawable.modern_diamonds_2, R.drawable.modern_diamonds_3, R.drawable.modern_diamonds_4, R.drawable.modern_diamonds_5, R.drawable.modern_diamonds_6,
            R.drawable.modern_diamonds_7, R.drawable.modern_diamonds_8, R.drawable.modern_diamonds_9, R.drawable.modern_diamonds_10, R.drawable.modern_diamonds_11, R.drawable.modern_diamonds_12, R.drawable.modern_diamonds_13
    };

    public final static int[] sDrawablesDark = {
            R.drawable.dark_clubs_1, R.drawable.dark_clubs_2, R.drawable.dark_clubs_3, R.drawable.dark_clubs_4, R.drawable.dark_clubs_5, R.drawable.dark_clubs_6,
            R.drawable.dark_clubs_7, R.drawable.dark_clubs_8, R.drawable.dark_clubs_9, R.drawable.dark_clubs_10, R.drawable.dark_clubs_11, R.drawable.dark_clubs_12, R.drawable.dark_clubs_13,

            R.drawable.dark_hearts_1, R.drawable.dark_hearts_2, R.drawable.dark_hearts_3, R.drawable.dark_hearts_4, R.drawable.dark_hearts_5, R.drawable.dark_hearts_6,
            R.drawable.dark_hearts_7, R.drawable.dark_hearts_8, R.drawable.dark_hearts_9, R.drawable.dark_hearts_10, R.drawable.dark_hearts_11, R.drawable.dark_hearts_12, R.drawable.dark_hearts_13,

            R.drawable.dark_spades_1, R.drawable.dark_spades_2, R.drawable.dark_spades_3, R.drawable.dark_spades_4, R.drawable.dark_spades_5, R.drawable.dark_spades_6,
            R.drawable.dark_spades_7, R.drawable.dark_spades_8, R.drawable.dark_spades_9, R.drawable.dark_spades_10, R.drawable.dark_spades_11, R.drawable.dark_spades_12, R.drawable.dark_spades_13,

            R.drawable.dark_diamonds_1, R.drawable.dark_diamonds_2, R.drawable.dark_diamonds_3, R.drawable.dark_diamonds_4, R.drawable.dark_diamonds_5, R.drawable.dark_diamonds_6,
            R.drawable.dark_diamonds_7, R.drawable.dark_diamonds_8, R.drawable.dark_diamonds_9, R.drawable.dark_diamonds_10, R.drawable.dark_diamonds_11, R.drawable.dark_diamonds_12, R.drawable.dark_diamonds_13
    };
}

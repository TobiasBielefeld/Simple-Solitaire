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

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.Stack;

import static de.tobiasbielefeld.solitaire.SharedData.OPTION_UNDO;
import static de.tobiasbielefeld.solitaire.SharedData.cards;
import static de.tobiasbielefeld.solitaire.SharedData.editor;
import static de.tobiasbielefeld.solitaire.SharedData.moveToStack;
import static de.tobiasbielefeld.solitaire.SharedData.savedData;
import static de.tobiasbielefeld.solitaire.SharedData.scores;
import static de.tobiasbielefeld.solitaire.SharedData.stacks;

/*
 *  Manages the records, so the player can undo movements. for that it has an entry subclass
 *  which has a variable amount of cards, so multiple cards can be undo at once
 */

public class RecordList {

    final private static String RECORD_LIST_ENTRY_ = "recordListEntry_";                            //final strings for saving / loading data
    final private static String RECORD_LIST_ENTRIES_SIZE = "recordLostEntriesSize";
    final private static String _HAS_FLIP_CARD = "_hasFlipCard";
    final private static String _FLIP_CARD = "_flipCard";
    final private static String _SIZE = "_size";
    final private static String _ORIGIN = "_origin";
    final private static String _CARD_ = "_card_";

    private final static int MAX_RECORDS = 20;                                                      //set how many times the player can undo moves
    private ArrayList<Entry> mEntries = new ArrayList<>();                                          //and make a new array list out of entries

    void reset() {                                                                                  //delete the content on reset
        mEntries.clear();
    }

    public void add(ArrayList<Card> cards) {                                                        //adds movement cards to the record
        if (mEntries.size() == MAX_RECORDS)                                                         //if the maximum is reached...
            mEntries.remove(0);                                                                     //remove the first entry

        mEntries.add(new Entry(cards));                                                             //add the new entry
    }

    public void undo() {                                                                            //undo one record
        if (!mEntries.isEmpty()) {                                                                  //if the entries aren't empty
            scores.update(Scores.UNDO);                                                             //update the score
            mEntries.get(mEntries.size() - 1).undo();                                               //undo the last movement
            mEntries.remove(mEntries.size() - 1);                                                   //and delete it
        }
    }

    public void addFlip(Card card) {                                                                //add a card to flip (call it after the normal record list add)
        if (mEntries.size() > 0)                                                                    //if the size is greater than zero (just to get sure)
            mEntries.get(mEntries.size() - 1).addFlip(card);                                        //add the card to the last entry
    }

    void save() {                                                                                   //saves the current state of the recordList
        editor.putInt(RECORD_LIST_ENTRIES_SIZE, mEntries.size());                                   //save the entries array size

        for (int i = 0; i < mEntries.size(); i++) {                                                 //then loop through every entry
            editor.putBoolean(RECORD_LIST_ENTRY_ + i +                                              //also save if it has a flip card
                    _HAS_FLIP_CARD, mEntries.get(i).hasFlipCard());

            if (mEntries.get(i).hasFlipCard())                                                      //and save the value if so
                editor.putInt(RECORD_LIST_ENTRY_ + i +
                        _FLIP_CARD, mEntries.get(i).mFlipCard.getID());

            editor.putInt(RECORD_LIST_ENTRY_ + i + _SIZE, mEntries.get(i).mCurrentCards.size());    //save card array size of the entry
            editor.putInt(RECORD_LIST_ENTRY_ + i + _ORIGIN, mEntries.get(i).mOrigin.getID());       //and the origin

            for (int j = 0; j < mEntries.get(i).mCurrentCards.size(); j++)                          //then loop through every card
                editor.putInt(RECORD_LIST_ENTRY_ + i +                                              //and save the id
                        _CARD_ + j, mEntries.get(i).mCurrentCards.get(j).getID());
        }
    }

    void load() {                                                                                   //load cards in the recordList
        reset();                                                                                    //get sure there aren't already records
        int size = savedData.getInt(RECORD_LIST_ENTRIES_SIZE, -1);                                  //get the entries size

        for (int i = 0; i < size; i++) {                                                            //then loop through every entry
            int entry_size = savedData.getInt(RECORD_LIST_ENTRY_ + i + _SIZE, -1);                  //get the card array size
            int origin_ID = savedData.getInt(RECORD_LIST_ENTRY_ + i + _ORIGIN, -1);                 //and the origin of the cards
            ArrayList<Card> card_array = new ArrayList<>();                                         //generate new array list for the cards

            for (int j = 0; j < entry_size; j++) {                                                  //then loop through the card array
                int card_ID = savedData.getInt(RECORD_LIST_ENTRY_ + i + _CARD_ + j, -1);            //and get the card id
                card_array.add(cards[card_ID]);                                                     //finally add the cards to the new array list
            }

            mEntries.add(new Entry(card_array, stacks[origin_ID]));                                 //add the new array list with the origin to the entries

            if (savedData.getBoolean(RECORD_LIST_ENTRY_ + i + _HAS_FLIP_CARD, false))               //check if it has a flip card
                addFlip(cards[savedData.getInt(RECORD_LIST_ENTRY_ + i + _FLIP_CARD, -1)]);          //and load if so
        }
    }

    private class Entry {                                                                           //entry class for the records, because there can be a variable amount of moved cards
        private ArrayList<Card> mCurrentCards = new ArrayList<>();                                  //array for the cards
        private Card mFlipCard;                                                                     //add a card to flip
        private Stack mOrigin;                                                                      //saves the origin stack of the cards

        Entry(ArrayList<Card> cards) {                                                              //add cards to the entry
            mCurrentCards.addAll(cards);                                                            //add the new array
            mOrigin = mCurrentCards.get(0).getStack();                                              //get the origin and save it
            mFlipCard = null;                                                                       //no flip card
        }

        Entry(ArrayList<Card> cards, Stack origin) {                                                //add cards to the entry (from game load)
            mCurrentCards.addAll(cards);                                                            //add the new array
            mOrigin = origin;                                                                       //get the origin and save it
            mFlipCard = null;                                                                       //no flip card
        }

        void addFlip(Card card) {                                                                   //add a card to flip
            mFlipCard = card;
        }

        void undo() {                                                                               //undo this movement
            moveToStack(mCurrentCards, mOrigin, OPTION_UNDO);                                       //undo is without record but with score.undo()

            if (mFlipCard != null)                                                                  //if there is a card to flip
                mFlipCard.flipWithAnim();                                                           //flip it
        }

        boolean hasFlipCard() {                                                                     //returns if the entry has a card to flip
            return mFlipCard != null;
        }
    }
}

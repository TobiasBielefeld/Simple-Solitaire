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

import static de.tobiasbielefeld.solitaire.SharedData.*;

/*
 *  Manages the records, so the player can undo movements. for that it has an entry subclass
 *  which has a variable amount of cards, so multiple cards can be undo at once
 */

public class RecordList {

    private final static int MAX_RECORDS = 20;
    private ArrayList<Entry> entries = new ArrayList<>();

    public void reset() {                                                                                  //delete the content on reset
        entries.clear();
    }

    /*
     * a lot of overloaded add methods
     */

    public void add(ArrayList<Card> cards) {
        if (entries.size() == MAX_RECORDS)
            entries.remove(0);

        entries.add(new Entry(cards));
    }

    public void add(ArrayList<Card> cards, Stack origin) {
        if (entries.size() == MAX_RECORDS)
            entries.remove(0);

        entries.add(new Entry(cards,origin));
    }

    public void add(ArrayList<Card> cards, ArrayList<Stack> origins) {
        if (entries.size() == MAX_RECORDS)
            entries.remove(0);

        entries.add(new Entry(cards,origins));
    }

    public void addAtEndOfLastEntry(ArrayList<Card> cards, ArrayList<Stack> origins){
        if (entries.size()==0)
            entries.add(new Entry(cards,origins));
        else
            entries.get(entries.size()-1).addAtEnd(cards,origins);
    }

    public void addInFrontOfLastEntry(ArrayList<Card> cards, ArrayList<Stack> origins){
        if (entries.size()==0)
            entries.add(new Entry(cards,origins));
        else
            entries.get(entries.size()-1).addInFront(cards,origins);
    }

    public void undo() {
        if (!entries.isEmpty()) {
            scores.update(-25);
            entries.get(entries.size() - 1).undo();
            entries.remove(entries.size() - 1);
        }
    }

    public void addFlip(Card card) {
        /*
         * a flip card will be added to the last entry.
         * so it can be flipped down in a undo
         */
        if (entries.size() > 0)
            entries.get(entries.size() - 1).addFlip(card);
    }

    public void save() {
        //save each entry
        putInt(RECORD_LIST_ENTRIES_SIZE, entries.size());

        for (int i=0;i<entries.size();i++){
            entries.get(i).save(Integer.toString(i));
        }
    }

    public void load() {
        //load each entry, reset first to get sure the entries are empty
        reset();

        int size = getInt(RECORD_LIST_ENTRIES_SIZE, -1);

        for (int i = 0; i < size; i++) {
            entries.add(new Entry(Integer.toString(i)));
        }
    }


    private class Entry {
        private ArrayList<Card> currentCards = new ArrayList<>();
        private ArrayList<Stack> currentOrigins = new ArrayList<>();
        private Card flipCard;

        Entry(String pos) {
            /*
             * used to load an entry after (re)starting the game
             */
            load(pos);
        }

        Entry(ArrayList<Card> cards) {
            currentCards.addAll(cards);

            for (Card card : cards)
                currentOrigins.add(card.getStack());

            flipCard = null;
        }

        Entry(ArrayList<Card> cards, Stack origin) {
            currentCards.addAll(cards);

            for (int i=0;i<currentCards.size();i++)
                currentOrigins.add(origin);

            flipCard = null;
        }

        Entry(ArrayList<Card> cards, ArrayList<Stack> origins) {
            currentCards.addAll(cards);
            currentOrigins.addAll(origins);

            flipCard = null;
        }

        void save(String pos){
            putInt(RECORD_LIST_ENTRY + pos + SIZE,currentCards.size());

            for (int i=0;i< currentCards.size();i++){
                putInt(RECORD_LIST_ENTRY + pos + CARD + i,currentCards.get(i).getID());
                putInt(RECORD_LIST_ENTRY + pos + ORIGIN + i,currentOrigins.get(i).getID());
            }

            putInt(RECORD_LIST_ENTRY + pos + FLIP_CARD, hasFlipCard()? flipCard.getID() : -1);

        }

        void load(String pos){
            int size = getInt(RECORD_LIST_ENTRY + pos + SIZE,-1);
            int flipCardID = getInt(RECORD_LIST_ENTRY + pos + FLIP_CARD,-1);

            for (int i=0;i<size;i++){
                int cardID = getInt(RECORD_LIST_ENTRY + pos + CARD + i, -1);
                int stackID = getInt(RECORD_LIST_ENTRY + pos + ORIGIN + i, -1);

                currentCards.add(cards[cardID]);
                currentOrigins.add(stacks[stackID]);
            }

            if (flipCardID>0)
                addFlip(cards[flipCardID]);
        }

        void addFlip(Card card) {                                                                   //add a card to flip
            flipCard = card;
        }

        void undo() {
            moveToStack(currentCards, currentOrigins, OPTION_UNDO);

            if (flipCard != null)
                flipCard.flipWithAnim();
        }

        void addInFront(ArrayList<Card> cards, ArrayList<Stack> stacks){
            ArrayList<Card> tempCards = currentCards;
            ArrayList<Stack> tempOrigins = currentOrigins;

            currentCards = cards;
            currentOrigins = stacks;

            currentCards.addAll(tempCards);
            currentOrigins.addAll(tempOrigins);
        }
        void addAtEnd(ArrayList<Card> cards, ArrayList<Stack> stacks){
            currentCards.addAll(cards);
            currentOrigins.addAll(stacks);
        }

        boolean hasFlipCard() {                                                                     //returns if the entry has a card to flip
            return flipCard != null;
        }
    }
}

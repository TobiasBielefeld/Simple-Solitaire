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
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/*
 *  Manages the records, so the player can undo movements. for that it has an entry subclass
 *  which has a variable amount of cards, so multiple cards can be undo at once
 */

public class RecordList {

    private final static int MAX_RECORDS = 20;
    private ArrayList<Entry> entries = new ArrayList<>();
    private GameManager gm;

    public RecordList(GameManager gm){
        this.gm = gm;
    }

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

    public void addAtEndOfLastEntry(Card card, Stack origin){
        ArrayList<Card> cards = new ArrayList<>();
        ArrayList<Stack> origins = new ArrayList<>();

        cards.add(card);
        origins.add(origin);

        addAtEndOfLastEntry(cards,origins);
    }

    public void addAtEndOfLastEntry(ArrayList<Card> cards, Stack origin){
        ArrayList<Stack> origins = new ArrayList<>();

        for (int i=0;i<cards.size();i++)
            origins.add(origin);

        addAtEndOfLastEntry(cards,origins);
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

    public Entry getLastEntry(){
        return entries.get(entries.size()-1);
    }

    public void deleteLast(){
        entries.remove(entries.size() - 1);
    }

    public class Entry {
        private ArrayList<Card> currentCards = new ArrayList<>();
        private ArrayList<Stack> currentOrigins = new ArrayList<>();
        private ArrayList<Card> flipCards = new ArrayList<>();

        public ArrayList<Card> getCurrentCards(){
            return currentCards;
        }

        public ArrayList<Stack> getCurrentOrigins(){
            return currentOrigins;
        }

        public ArrayList<Card> getFlipCards(){
            return flipCards;
        }

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
        }

        Entry(ArrayList<Card> cards, Stack origin) {
            currentCards.addAll(cards);

            for (int i=0;i<currentCards.size();i++)
                currentOrigins.add(origin);
        }

        Entry(ArrayList<Card> cards, ArrayList<Stack> origins) {
            currentCards.addAll(cards);
            currentOrigins.addAll(origins);
        }

        void save(String pos){
            ArrayList<Integer> listCards = new ArrayList<>();
            ArrayList<Integer> listFlipCards = new ArrayList<>();
            ArrayList<Integer> listOrigins = new ArrayList<>();

            for (int i=0;i<currentCards.size();i++) {
                listCards.add(currentCards.get(i).getID());
                listOrigins.add(currentOrigins.get(i).getID());

            }

            putIntList(RECORD_LIST_ENTRY + pos + CARD,listCards);
            putIntList(RECORD_LIST_ENTRY + pos + ORIGIN,listOrigins);

            for (Card card : flipCards){
                listFlipCards.add(card.getID());
            }

            putIntList(RECORD_LIST_ENTRY + pos + FLIP_CARD, listFlipCards);
            //putInt(RECORD_LIST_ENTRY + pos + FLIP_CARD, hasFlipCard()? flipCard.getID() : -1);

        }

        void load(String pos){

            ArrayList<Integer> cardList = getIntList(RECORD_LIST_ENTRY + pos + CARD);
            ArrayList<Integer> originList = getIntList(RECORD_LIST_ENTRY + pos + ORIGIN);

            for (int i=0;i<cardList.size();i++){
                currentCards.add(cards[cardList.get(i)]);
                currentOrigins.add(stacks[originList.get(i)]);
            }

            //compability to older way of saving: changed from one possible flip card to multiple
            try {   //new way
                ArrayList<Integer> flipCardList = getIntList(RECORD_LIST_ENTRY + pos + FLIP_CARD);

                for (Integer i : flipCardList){
                    flipCards.add(cards[i]);
                }
            } catch (Exception e) { //old way
                int flipCardID = getInt(RECORD_LIST_ENTRY + pos + FLIP_CARD,-1);

                if (flipCardID>0)
                    addFlip(cards[flipCardID]);
            }
        }

        void addFlip(Card card) {                                                                   //add a card to flip
            flipCards.add(card);
        }

        void undo() {

            if (currentGame.hasLimitedRedeals() && currentOrigins.get(0)==currentGame.getDiscardStack() && currentCards.get(0).getStack()==currentGame.dealFromStack()) {
                currentGame.decrementRedealCounter(gm);
            }

            moveToStack(currentCards, currentOrigins, OPTION_UNDO);

            for (Card card : flipCards)
                card.flipWithAnim();
        }

        void addInFront(ArrayList<Card> cards, ArrayList<Stack> stacks){
            ArrayList<Card> tempCards = currentCards;
            ArrayList<Stack> tempOrigins = currentOrigins;

            currentCards = cards;
            currentOrigins = stacks;

            //if some cards which are added were already in currentCards, replace their origins with
            //the original one
            for (int i=0;i<tempCards.size();i++){
                if (currentCards.contains(tempCards.get(i))){
                    currentOrigins.add(currentCards.indexOf(tempCards.get(i)),tempOrigins.get(i));
                } else {
                    currentCards.add(tempCards.get(i));
                    currentOrigins.add(tempOrigins.get(i));
                }
            }
        }

        void addAtEnd(ArrayList<Card> cards, ArrayList<Stack> stacks){

            for (int i=0;i<cards.size();i++){
                if (!currentCards.contains(cards.get(i))){
                    currentCards.add(cards.get(i));
                    currentOrigins.add(stacks.get(i));
                }
            }

            //currentCards.addAll(cards);
            //currentOrigins.addAll(stacks);
        }
    }
}

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
import de.tobiasbielefeld.solitaire.classes.WaitForAnimationHandler;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Manages the records, so the player can undo movements. for that it has an entry subclass
 * which has a variable amount of cards, so multiple cards can be undo at once
 */

public class RecordList {

    public static int maxRecords;
    public ArrayList<Entry> entries = new ArrayList<>();
    private WaitForAnimationHandler handler;

    private boolean isWorking = false;

    public void reset() {                                                                                  //delete the content on reset
        entries.clear();
    }


    public RecordList(GameManager gm){
        setMaxRecords();

        handler = new WaitForAnimationHandler(gm, new WaitForAnimationHandler.MessageCallBack() {
            @Override
            public void doAfterAnimation() {
                handleMessage();
            }

            @Override
            public boolean additionalHaltCondition() {
                return false;
            }
        });
    }

    /**
     * Adds entries of the card list, if the maximum number of records was reached, delete
     * the last one. The origin of the cards will be the current stack
     *
     * @param cards The card list to add
     */
    public void add(ArrayList<Card> cards) {
        if (entries.size() >= maxRecords) {
            entries.remove(0);
        }

        entries.add(new Entry(cards));
    }

    /**
     * Adds entries of the card list, if the maximum number of records was reached, delete
     * the last one. This version also takes a stack as origin of the cards
     *
     * @param cards  The card list to add
     * @param origin Other stack as origin, where the cards can be returned to
     */
    public void add(ArrayList<Card> cards, Stack origin) {
        if (entries.size() >= maxRecords) {
            entries.remove(0);
        }

        entries.add(new Entry(cards, origin));
    }


    /**
     * Adds entries of the card list, if the maximum number of records was reached, delete
     * the last one. This version also takes a stack array list as origins, so every card can
     * have a different origin stack
     *
     * @param cards   the card list to add
     * @param origins Other stacks as origin, where the cards can be returned to
     */
    public void add(ArrayList<Card> cards, ArrayList<Stack> origins) {
        if (entries.size() >= maxRecords) {
            entries.remove(0);
        }

        entries.add(new Entry(cards, origins));
    }

    /**
     * Adds more cards to the last entry but as the first cards of that entry, so these cards will be
     * moved at first, if the record is undone
     *
     * @param cards   Multiple cards to add
     * @param origins Origin stacks of these cards
     */
    public void addToLastEntry(ArrayList<Card> cards, ArrayList<Stack> origins) {
        if (entries.size() == 0) {
            entries.add(new Entry(cards, origins));
        } else {
            entries.get(entries.size() - 1).addInFront(cards, origins);
        }
    }

    /**
     * Adds more cards to the last entry but as the first cards of that entry, so these cards will be
     * moved at first, if the record is undone
     *
     * @param card   Single cards to add
     * @param origin Origin stack of these cards
     */
    public void addToLastEntry(Card card, Stack origin) {
        ArrayList<Card> cards = new ArrayList<>();
        ArrayList<Stack> origins = new ArrayList<>();

        cards.add(card);
        origins.add(origin);

        addToLastEntry(cards,origins);
    }

    /**
     * reverts one record, this will delete that record from the list and takes 25 points away
     * from the current score
     */
    public void undo() {
        if (!entries.isEmpty()) {
            isWorking = true;
            sounds.playSound(Sounds.names.CARD_RETURN);

            if (!prefs.getDisableUndoCosts()) {
                scores.update(-currentGame.getUndoCosts());
            }

            entries.get(entries.size() - 1).undo();

            int amount = prefs.getSavedTotalNumberUndos() + 1;
            prefs.saveTotalNumberUndos(amount);
        }
    }

    public void undoMore() {
        if (!entries.isEmpty()) {
            entries.get(entries.size() - 1).undoMore();
        }
    }

    /**
     * a flip card will be added to the last entry.
     * so it can be flipped down in a undo
     *
     * @param card The card to add
     */
    public void addFlip(Card card) {

        if (entries.size() > 0)
            entries.get(entries.size() - 1).addFlip(card);
    }

    /**
     * Saves every entry
     */
    public void save() {
        prefs.saveRecordListEntriesSize(entries.size());

        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).save(Integer.toString(i));
        }
    }

    /**
     * load the saved entries. Calling the Entry constructor with a string will load
     * its content from the shared Pref
     */
    public void load() {
        reset();

        for (int i = 0; i < prefs.getSavedRecordListEntriesSize(); i++) {
            entries.add(new Entry(Integer.toString(i)));
        }
    }

    public void deleteLast() {
        if (entries.size() > 0) {
            entries.remove(entries.size() - 1);
        }
    }

    public boolean hasMoreToUndo(){
        if (entries.isEmpty()){
            return false;
        }

        if (entries.get(entries.size()-1).hasMoreToDo()){
            return true;
        } else {
            entries.remove(entries.size() - 1);
            isWorking = false;

            //check if the undo movement makes autocomplete undoable
            if (autoComplete.buttonIsShown() && !currentGame.autoCompleteStartTest()) {
                autoComplete.hideButton();
            }

            currentGame.afterUndo();
            return false;
        }
    }

    public boolean isWorking(){
        return isWorking;
    }

    public static class Entry {
        private ArrayList<Integer> moveOrder = new ArrayList<>();
        private ArrayList<Card> currentCards = new ArrayList<>();
        private ArrayList<Stack> currentOrigins = new ArrayList<>();
        private ArrayList<Card> flipCards = new ArrayList<>();

        private boolean alreadyDecremented = false;

        public ArrayList<Card> getCurrentCards(){
            return currentCards;
        }

        public ArrayList<Stack> getCurrentOrigins(){
            return currentOrigins;
        }

        /**
         * This constructor is used to load saved entries.
         *
         * @param pos The index of the saved entry to load
         */
        Entry(String pos) {
            ArrayList<Integer> cardList = prefs.getSavedRecordListCards(pos);
            ArrayList<Integer> originList = prefs.getSavedRecordListOrigins(pos);
            ArrayList<Integer> orderList = prefs.getSavedRecordListOrders(pos);

            for (int i = 0; i < cardList.size(); i++) {
                currentCards.add(cards[cardList.get(i)]);
                currentOrigins.add(stacks[originList.get(i)]);

                if (orderList.size()>i){
                    moveOrder.add(orderList.get(i));
                } else {
                    moveOrder.add(0);
                }
            }

            //compatibility to older way of saving: changed from one possible flip card to multiple
            try { //new way
                ArrayList<Integer> flipCardList = prefs.getSavedRecordListFlipCards(pos);

                for (Integer i : flipCardList) {
                    flipCards.add(cards[i]);
                }
            } catch (Exception e) { //old way
                int flipCardID = prefs.getSavedFlipCardId(pos);

                if (flipCardID > 0)
                    addFlip(cards[flipCardID]);
            }
        }

        /**
         * Create a new entry with the given cards. Origins will be the current card positions
         *
         * @param cards The cards to add
         */
        Entry(ArrayList<Card> cards) {
            currentCards.addAll(cards);

            for (Card card : cards) {
                currentOrigins.add(card.getStack());
                moveOrder.add(0);
            }
        }

        /**
         * Create a new entry with the given cards. Origin will be applied for all cards
         *
         * @param cards  The cards to add
         * @param origin The origin of the cards
         */
        Entry(ArrayList<Card> cards, Stack origin) {
            currentCards.addAll(cards);

            for (int i = 0; i < currentCards.size(); i++) {
                currentOrigins.add(origin);
                moveOrder.add(0);
            }
        }

        /**
         * Create a new entry with the given cards and origins
         *
         * @param cards   The cards to add
         * @param origins The orgins of the cards
         */
        Entry(ArrayList<Card> cards, ArrayList<Stack> origins) {
            currentCards.addAll(cards);
            currentOrigins.addAll(origins);

            for (int i = 0; i < currentCards.size(); i++) {
                moveOrder.add(0);
            }
        }

        /**
         * Saves the current entry in the shared pref. It needs to save the IDS of the cards and the
         * size of the array lists. Loading happens in one of the constructors
         *
         * @param pos The index of this entry in the array list
         */
        void save(String pos) {
            ArrayList<Integer> listCards = new ArrayList<>();
            ArrayList<Integer> listFlipCards = new ArrayList<>();
            ArrayList<Integer> listOrigins = new ArrayList<>();

            for (int i = 0; i < currentCards.size(); i++) {
                listCards.add(currentCards.get(i).getId());
                listOrigins.add(currentOrigins.get(i).getId());
            }

            prefs.saveRecordListCards(listCards,pos);
            prefs.saveRecordListOrigins(listOrigins,pos);
            prefs.saveRecordListOrders(moveOrder,pos);

            for (Card card : flipCards) {
                listFlipCards.add(card.getId());
            }

            prefs.saveRecordListFlipCards(listFlipCards,pos);
        }


        /**
         * Undos the latest entry.
         */
        void undo() {
            alreadyDecremented = false;

            for (Card card : flipCards) {
                card.flipWithAnim();
            }

            recordList.handler.sendDelayed();
        }

        /**
         * This contains the actual card movements. It will undo the movements of the cards
         * with the lowest move order and remove them from the list.
         *
         * This method is called from a handler. With each call, the lowest order will be used, until
         * all cards are away. So the movements are tiered.
         */
        void undoMore() {
            //Check if the movement resulted in a increment of the redeal counter, if so, revert it
            if (currentGame.hasLimitedRecycles() && !alreadyDecremented)  {
                ArrayList<Stack> discardStacks = currentGame.getDiscardStacks();

                for (int i=0;i<currentCards.size();i++){

                    if (currentCards.get(i).getStack() == currentGame.getDealStack() && discardStacks.contains(currentOrigins.get(i))) {
                        currentGame.decrementRecycleCounter();
                        alreadyDecremented = true;
                        break;
                    }
                }
            }

            ArrayList<Card> cardsWorkCopy = new ArrayList<>();
            ArrayList<Stack> originsWorkCopy = new ArrayList<>();
            ArrayList<Integer> moveOrderWorkCopy = new ArrayList<>();

            int minMoveOrder = min(moveOrder);

            for (int i =0;i<currentCards.size();i++){
                if (moveOrder.get(i) == minMoveOrder) {
                    cardsWorkCopy.add(currentCards.get(i));
                    originsWorkCopy.add(currentOrigins.get(i));
                    moveOrderWorkCopy.add(moveOrder.get(i));
                }
            }

            moveToStack(cardsWorkCopy,originsWorkCopy, OPTION_UNDO);

            for (int i=0;i<cardsWorkCopy.size();i++){
                currentCards.remove(cardsWorkCopy.get(i));
                currentOrigins.remove(originsWorkCopy.get(i));
                moveOrder.remove(moveOrderWorkCopy.get(i));
            }
        }

        /**
         * Adds cards in front of this entry. The move order of every current card will be increased
         * by 1 and the new cards get the order 0.
         *
         * @param cards  The cards to add
         * @param stacks The origins of the cards to add
         */
        void addInFront(ArrayList<Card> cards, ArrayList<Stack> stacks) {
            ArrayList<Card> tempCards = currentCards;
            ArrayList<Stack> tempOrigins = currentOrigins;
            ArrayList<Integer> tempMoveOrders = moveOrder;

            currentCards = new ArrayList<>(cards);
            currentOrigins = new ArrayList<>(stacks);
            moveOrder = new ArrayList<>();

            for (int i=0;i<currentCards.size();i++){
                moveOrder.add(0);
            }

            //Check for each card, if it is already in the entry
            for (int i = 0; i < tempCards.size(); i++) {
                currentCards.add(tempCards.get(i));
                currentOrigins.add(tempOrigins.get(i));
                moveOrder.add(tempMoveOrders.get(i)+1);                                             //increment the orders by one
            }
        }


        void addFlip(Card card) {                                                                   //add a card to flip
            flipCards.add(card);
        }

        boolean hasMoreToDo(){
            return currentCards.size()!=0;
        }
    }

    public void setMaxRecords(){
        maxRecords = prefs.getSavedMaxNumberUndos();

        while (entries.size() > maxRecords) {
            entries.remove(0);
        }
    }

    private void handleMessage(){
        if (recordList.hasMoreToUndo()){
            recordList.undoMore();
            handler.sendDelayed();
        }
    }
}

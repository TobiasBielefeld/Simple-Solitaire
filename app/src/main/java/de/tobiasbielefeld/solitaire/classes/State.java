package de.tobiasbielefeld.solitaire.classes;

import java.util.ArrayList;
import java.util.List;

import static de.tobiasbielefeld.solitaire.SharedData.currentGame;

/**
 * Created by tobias on 14.04.18.
 */

public class State{

    private static int TRACE_MAX_LENGTH = 50;

    public ReducedStack[] stacks;
    public ReducedCard[] cards;
    public List<Entry> trace;
    public int id;
    public boolean alreadyFlipped = false;

    public State(Card[] normalCards, Stack[] normalStacks, int id){
        cards = new ReducedCard[normalCards.length];
        stacks = new ReducedStack[normalStacks.length];

        for (int i = 0; i < normalCards.length; i++){
            cards[i] = new ReducedCard(normalCards[i]);
        }

        for (int i = 0; i < normalStacks.length; i++){
            stacks[i] = new ReducedStack(normalStacks[i]);
        }

        trace = new ArrayList<>(TRACE_MAX_LENGTH);
        this.id = id;
    }

    public State(State original){
        cards = new ReducedCard[original.cards.length];
        stacks = new ReducedStack[original.stacks.length];
        trace = new ArrayList<>();
        alreadyFlipped = original.alreadyFlipped;

        for (int i = 0; i < original.cards.length; i++){
            cards[i] = new ReducedCard(original.cards[i]);
        }

        for (int i = 0; i < original.stacks.length; i++){
            stacks[i] = new ReducedStack(original.stacks[i]);
        }

        for (Entry entry : original.trace){
            trace.add(new Entry(entry));
        }

        //this.trace = new ArrayList<>(original.trace);

        this.id = original.id;
    }

    public class Entry {
        public int card;
        public int destination;
        public int origin;

        Entry(int cardId, int destId, int originId) {
            card = cardId;
            destination = destId;
            origin = originId;
        }

        Entry(Entry original) {
            card = original.card;
            destination = original.destination;
            origin = original.origin;
        }
    }

    public class ReducedCard{
        public int color;                                                                          //1=clubs 2=hearts 3=Spades 4=diamonds
        public int value;                                                                          //1=ace 2,3,4,5,6,7,8,9,10, 11=joker 12=queen 13=king
        public int stackId;                                                                        //saves the stack where the card is placed
        public int id;                                                                             //internal id
        public boolean isUp;                                                                       //indicates if the card is placed upwards or backwards

        ReducedCard(Card card){
            color = card.getColor();
            value = card.getValue();
            stackId = card.getStackId();
            id = card.getId();
            isUp = card.isUp();
        }

        ReducedCard(ReducedCard card){
            color = card.getColor();
            value = card.getValue();
            stackId = card.getStackId();
            id = card.getId();
            isUp = card.isUp();
        }

        public ReducedStack getStack(){
            return stacks[stackId];
        }

        public int getStackId(){
            return stackId;
        }

        public int getId(){
            return id;
        }

        public int getIndexOnStack(){
            return stacks[stackId].currentCards.indexOf(this);
        }

        public void removeFromCurrentStack(){
            if (stackId!=-1) {
                stacks[stackId].removeCard(this);
                stackId = -1;
            }
        }

        public void setStack(int id){
            stackId = id;
        }

        public void flipUp(){
            isUp = true;
        }

        public boolean isUp(){
            return isUp;
        }

        public int getColor(){
            return color;
        }

        public int getValue(){
            return value;
        }

        public boolean isTopCard(){
            return stacks[stackId].currentCards.indexOf(this) == stacks[stackId].getSize()-1;
        }
    }

    public class ReducedStack{
        public ArrayList<ReducedCard> currentCards = new ArrayList<>();
        private int id;

        ReducedStack(Stack stack){
            id = stack.getId();

            for (Card card : stack.currentCards){
                currentCards.add(cards[card.getId()]);
            }
        }

        ReducedStack(ReducedStack stack){
            id = stack.getId();

            for (ReducedCard card : stack.currentCards){
                currentCards.add(cards[card.getId()]);
            }
        }

        public int getFirstUpCardPos() {
            for (int i = 0; i < currentCards.size(); i++) {
                if (currentCards.get(i).isUp())
                    return i;
            }

            return -1;
        }

        public int getSize(){
            return currentCards.size();
        }

        public ReducedCard getTopCard() {
            return currentCards.get(currentCards.size() - 1);
        }

        public ReducedCard getCard(int index){
            return currentCards.get(index);
        }

        public void removeCard(ReducedCard card) {
            currentCards.remove(card);
        }

        public void removeCard(int index) {
            currentCards.remove(index);
        }

        public void addCard(ReducedCard card) {
            card.setStack(id);
            currentCards.add(card);

            if (currentGame.mainStacksContain(id)) {
                card.isUp = false;
            } else if (currentGame.discardStacksContain(id)){
                card.isUp = true;
            }
        }

        public int getId(){
            return id;
        }

        public boolean isEmpty(){
            return currentCards.size()==0;
        }
    }

    public State deepCopy(){
        return new State(this);
    }

    public void addTrace(int cardId, int destinationId, int originId){
        if (trace.size() == TRACE_MAX_LENGTH){
            trace.remove(0);
        }

        //counter ++;
        trace.add(new Entry(cardId,destinationId,originId));
    }
}
package de.tobiasbielefeld.solitaire.helper;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Random;

import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.classes.State;
import de.tobiasbielefeld.solitaire.games.Game;

import static de.tobiasbielefeld.solitaire.SharedData.currentGame;

import static de.tobiasbielefeld.solitaire.SharedData.logText;
import static de.tobiasbielefeld.solitaire.SharedData.stacks;

/**
 * Created by tobias on 20.03.18.
 */

public class EnsureMovability extends AsyncTask<Object, Void, Boolean>{

    private static int MIN_POSSIBLE_MOVEMENTS =50;
    private static int MAX_TIME_MILLIS = 500;

    Random random = new Random();

    private ArrayList<PossibleMovement> possibleMovements = new ArrayList<>();


    @Override
    protected Boolean doInBackground(Object... objects) {
        Stack[] normalStacks = (Stack[]) objects[0];
        Card[] normalCards = (Card[]) objects[1];

        long maxTime = System.currentTimeMillis() + MAX_TIME_MILLIS;

        State state = new State(normalCards, normalStacks, 0);

        while (true) {
            if (System.currentTimeMillis() > maxTime){
                return false;
            }

            logText("trace size: "+state.trace.size());

            if (state.trace.size() >= MIN_POSSIBLE_MOVEMENTS) {
                return true;
            }

            possibleMovements.clear();

            moveCard(state);

            logText("possible movements: "+possibleMovements.size());

            if (possibleMovements.size() > 0) {
                int index = random.nextInt(possibleMovements.size());
                PossibleMovement movement = possibleMovements.get(index);
                moveToStack(movement.state, movement.moveTo, movement.cardsToMove);
            } else {
                state = new State(normalCards, normalStacks, 0);
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        logText(""+result);
    }




    private static class PossibleMovement{

        State state;
        int moveTo;
        int[] cardsToMove;

        PossibleMovement(State state, int moveTo, int[] cardsToMove){
            this.state = state;
            this.moveTo = moveTo;
            this.cardsToMove = cardsToMove;
        }
    }

    private void moveCard(State state){

        for (int i=0;i<state.stacks.length;i++){

            //do not check cards on the foundation stack
            if (currentGame.foundationStacksContain(i)){
                continue;
            }

            for (int j=0;j<state.stacks[i].getSize();j++){
                State.ReducedCard cardToMove = state.stacks[i].getCard(j);

                if (cardToMove.isUp() && currentGame.addCardToMovementGameTest(cardToMove,state.stacks)){

                    for (int k=0;k < stacks.length;k++){
                        //for (int k=state.stacks.length-1;k>=0;k--){
                        State.ReducedStack destination = state.stacks[k];

                        if (i!=k && currentGame.cardTest(destination,cardToMove)){

                            //if moving to foundation, but the card isn't on top of the stack (moving multiple cards to foundation)
                            if (currentGame.foundationStacksContain(k) && !cardToMove.isTopCard()){
                                continue;
                            }
                            //moving around the tableau
                            else if (currentGame.tableauStacksContain(i) && j == 0 && destination.isEmpty()){
                                continue;
                            }
                            //avoid moving cards between stacks, eg moving a nine lying on a ten moving to another then, moving it back and so on...
                            else if (currentGame.tableauStacksContain(i) && currentGame.sameCardOnOtherStack(cardToMove,state.stacks[k], Game.testMode2.SAME_VALUE_AND_COLOR)) {
                                continue;
                            }
                            else if (alreadyMoved(state,cardToMove.getId(),k)){
                                continue;
                            }//*/

                            int size = state.stacks[i].getSize() - j;

                            int[] cardsToMove = new int[size];

                            for (int l = 0; l < size; l++) {
                                cardsToMove[l] = cardToMove.getStack().getCard(j + l).getId();
                            }

                            possibleMovements.add(new PossibleMovement(state, k, cardsToMove));
                            //moveToStack(state, k, cardsToMove);
                        }
                    }
                }
            }
        }
    }

    private boolean alreadyMoved(State state, int cardId, int destinationId){

        if (!currentGame.tableauStacksContain(cardId)) {
            return false;
        }

        for (int j=state.trace.size()-1;j>=0;j--){
            State.Entry trace = state.trace.get(j);

            if (trace.card == cardId && trace.origin == destinationId){
                return true;
            }
        }


        return false;
    }

    public void moveToStack(State state,  int destinationId, int... cardIds) {

        state.addTrace(cardIds[0], destinationId, state.cards[cardIds[0]].getStackId());

        State.ReducedCard firstCard = state.cards[cardIds[0]];
        State.ReducedStack destination = state.stacks[destinationId];

        int indexOfFirstCard = firstCard.getIndexOnStack();


        //if the moving card is on the tableau, flip the card below it up
        if (indexOfFirstCard>0 && currentGame.tableauStacksContain(firstCard.getStackId())) {
            firstCard.getStack().getCard(indexOfFirstCard-1).flipUp();
        }


        for (int i : cardIds){
            state.cards[i].removeFromCurrentStack();
            destination.addCard( state.cards[i]);
        }


        //run(newState);
    }
}

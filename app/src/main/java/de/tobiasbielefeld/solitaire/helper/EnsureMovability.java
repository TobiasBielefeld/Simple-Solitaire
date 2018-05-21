package de.tobiasbielefeld.solitaire.helper;

import android.os.AsyncTask;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.dialogs.DialogEnsureMovability;
import de.tobiasbielefeld.solitaire.games.Pyramid;

import static de.tobiasbielefeld.solitaire.SharedData.OPTION_NO_RECORD;
import static de.tobiasbielefeld.solitaire.SharedData.currentGame;

import static de.tobiasbielefeld.solitaire.SharedData.gameLogic;
import static de.tobiasbielefeld.solitaire.SharedData.hint;
import static de.tobiasbielefeld.solitaire.SharedData.logText;
import static de.tobiasbielefeld.solitaire.SharedData.moveToStack;

import static de.tobiasbielefeld.solitaire.SharedData.prefs;
import static de.tobiasbielefeld.solitaire.SharedData.stopMovements;

/**
 * Ensures that at least MIN_POSSIBLE_MOVEMENTS amount of movements are possible at the start of a game.
 * It uses the Game.hintTest() method to find possible movements. If not enough movements are found
 * or the timer runs out, a new game will be dealt and the test restarts.
 *
 * Everything happens inside the async task, the user only sees a spinning wait wheel. While the tests
 * run, the stopMovements variable in SharedData is set to true, so cards won't move visible, but
 * in the background they are assigned to other stacks and so on.
 *
 * IMPORTANT: The Game.hintTest() does NOT return every possible movement! For example in SimpleSimon:
 * If a Hearts 9 lies on a Clubs 10 and could be moved to a Diamonds 10, it won't be shown. If it
 * could be moved to a Hearts 10, this would be shown. This decision was made to not show redundant
 * movements.
 *
 * This class will probably use another way to find movements (see the commented blocks) but it
 * doesn't work correctly yet.
 * TODO: Make the new way working!
 */

public class EnsureMovability extends AsyncTask<Object, Void, Boolean>{

    private DialogEnsureMovability dialog;

    private int counter = 0;
    private boolean mainStackAlreadyFlipped = false;
    /*private ArrayList<PossibleMovement> possibleMovements = new ArrayList<>();
    private Random random = new Random();
    private List<Entry> trace = new ArrayList<>(MIN_POSSIBLE_MOVEMENTS);

    public static class Entry {
        public int card;
        public int destination;
        public int origin;

        public Entry(int cardId, int destId, int originId) {
            card = cardId;
            destination = destId;
            origin = originId;
        }
    }//*/


    @Override
    protected Boolean doInBackground(Object... objects) {
        int minPossibleMovements = prefs.getSavedEnsureMovabilityMinMoves();

        dialog = (DialogEnsureMovability) objects[0];

        while (true) {
            if (isCancelled()){
                return false;
            }

            if (counter == minPossibleMovements || currentGame.winTest()){
                return true;
            }

            CardAndStack cardAndStack = currentGame.hintTest();

            if (cardAndStack != null) {

                Stack destination = cardAndStack.getStack();
                Card card = cardAndStack.getCard();
                Stack origin = card.getStack();

                int size = origin.getSize() - card.getIndexOnStack();

                ArrayList<Card> cardsToMove = new ArrayList<>(size);

                for (int l = card.getIndexOnStack(); l < origin.getSize(); l++) {
                    cardsToMove.add(origin.getCard(l));
                }

                //TODO manage this in another way
                if (currentGame instanceof Pyramid){
                    currentGame.cardTest(destination,card);
                }

                //logText("Moving " + cardsToMove.get(0).getValue() + " to stack " + cardsToMove.get(0).getStackId());
                //logText("Counter: " + counter);
                moveToStack(cardsToMove, destination);

                if (origin.getSize() > 0 && origin.getId() <= currentGame.getLastTableauId() && !origin.getTopCard().isUp()) {
                    origin.getTopCard().flip();
                }

                currentGame.testAfterMove();

                mainStackAlreadyFlipped = false;
                counter ++;
            }  else if (currentGame.hasMainStack()){
                int result = currentGame.mainStackTouch();

                if (result == 0 || (result == 2 && mainStackAlreadyFlipped)) {
                    nextTry();
                } else if (result == 2){
                    mainStackAlreadyFlipped = true;
                }

            } else {
                nextTry();
            }


            /*if (trace.size() == MIN_POSSIBLE_MOVEMENTS) {
                return true;
            }

            possibleMovements.clear();

            moveCard();

            logText("possible movements: " + possibleMovements.size());

            if (possibleMovements.size() > 0) {
                int index = random.nextInt(possibleMovements.size());
                PossibleMovement movement = possibleMovements.get(index);

                ArrayList<Card> cardsToMove = new ArrayList<>();

                for (int i : movement.cardsToMove) {
                    cardsToMove.add(cards[i]);
                }

                SharedData.moveToStack(cardsToMove, stacks[movement.moveTo]);

                trace.add(new Entry(movement.cardsToMove[0], movement.moveTo, cards[movement.cardsToMove[0]].getStackId()));
            } else if (currentGame.hasMainStack()){
                currentGame.onMainStackTouch();
            } else {
                return false;
            }//*/
        }
    }

    private void nextTry(){
        //logText("starting new game");

        if (isCancelled()){
            return;
        }

        counter = 0;
        mainStackAlreadyFlipped = false;
        gameLogic.newGameForEnsureMovability();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        stopMovements = false;

        if (result) {

            try {
                dialog.dismiss();
            } catch (IllegalStateException ignored){
                //Meh
            }

            gameLogic.redeal();
        }

    }

    @Override
    protected void onCancelled() {
        //will be called after the user presses the "cancel" button in the dialog and after
        //executing doInBackground() the last time
        stopMovements = false;
        gameLogic.redeal();
    }

    /*private static class PossibleMovement{

        int moveTo;
        int[] cardsToMove;

        PossibleMovement(int moveTo, int[] cardsToMove){
            this.moveTo = moveTo;
            this.cardsToMove = cardsToMove;
        }
    }

    private void moveCard(){

        for (int i=0;i<stacks.length;i++){

            //do not check cards on the foundation stack
            if (currentGame.foundationStacksContain(i)){
                continue;
            }

            for (int j=0;j<stacks[i].getSize();j++){
                Card cardToMove = stacks[i].getCard(j);

                if (cardToMove.isUp() && currentGame.addCardToMovementGameTest(cardToMove)){

                    for (int k=0;k < stacks.length;k++){
                        //for (int k=state.stacks.length-1;k>=0;k--){
                        Stack destination = stacks[k];

                        if (i!=k && currentGame.cardTest(destination,cardToMove)){

                            //if moving to foundation, but the card isn't on top of the stack (moving multiple cards to foundation)
                            if (currentGame.foundationStacksContain(k) && !cardToMove.isTopCard()){
                                continue;
                            }
                            //moving around the tableau
                            else if (currentGame.tableauStacksContain(i) && j == 0 && destination.isEmpty()){
                                continue;
                            }
                            //avoid moving cards between stacks, eg moving a nine lying on a ten moving to another ten, moving it back and so on...
                            else if (currentGame.tableauStacksContain(i) && currentGame.sameCardOnOtherStack(cardToMove, destination, Game.testMode2.SAME_VALUE_AND_COLOR)) {
                                continue;
                            }
                            else if (alreadyMoved(cardToMove.getId(),k)){
                                continue;
                            }

                            int size = stacks[i].getSize() - j;

                            int[] cardsToMove = new int[size];

                            for (int l = 0; l < size; l++) {
                                cardsToMove[l] = cardToMove.getStack().getCard(j + l).getId();
                            }

                            possibleMovements.add(new PossibleMovement(k, cardsToMove));
                        }
                    }
                }
            }
        }
    }

    private boolean alreadyMoved(int cardId, int destinationId){

        if (!currentGame.tableauStacksContain(cardId)) {
            return false;
        }

        for (int j=trace.size()-1;j>=0;j--){
            Entry entry = trace.get(j);

            if (entry.card == cardId && entry.origin == destinationId){
                return true;
            }
        }

        return false;
    }//*/
}

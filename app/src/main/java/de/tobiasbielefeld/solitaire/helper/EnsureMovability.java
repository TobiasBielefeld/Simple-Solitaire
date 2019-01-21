package de.tobiasbielefeld.solitaire.helper;

import android.os.AsyncTask;
import android.os.Bundle;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.SharedData;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.dialogs.DialogEnsureMovability;
import de.tobiasbielefeld.solitaire.games.Pyramid;

import static de.tobiasbielefeld.solitaire.SharedData.currentGame;

import static de.tobiasbielefeld.solitaire.SharedData.ensureMovability;
import static de.tobiasbielefeld.solitaire.SharedData.gameLogic;
import static de.tobiasbielefeld.solitaire.SharedData.moveToStack;

import static de.tobiasbielefeld.solitaire.SharedData.prefs;
import static de.tobiasbielefeld.solitaire.SharedData.stopUiUpdates;

/**
 * Ensures that at least MIN_POSSIBLE_MOVEMENTS amount of movements are possible at the start of a game.
 * It uses the Game.hintTest() method to find possible movements. If not enough movements are found
 * or the timer runs out, a new game will be dealt and the test restarts.
 *
 * Everything happens inside the async task, the user only sees a spinning wait wheel. While the tests
 * run, the stopUiUpdates variable in SharedData is set to true, so cards won't move visibly, but
 * in the background they are assigned to other stacks and so on.
 *
 * IMPORTANT: The Game.hintTest() does NOT return every possible movement! For example in SimpleSimon:
 * If a Hearts 9 lies on a Clubs 10 and could be moved to a Diamonds 10, it won't be shown. If it
 * could be moved to a Hearts 10, this would be shown. This decision was made to not show redundant
 * movements.
 */

public class EnsureMovability {

    FindMoves findMoves ;
    DialogEnsureMovability dialog;

    private boolean paused = false;

    private ShowDialog showDialog;

    public void setShowDialog(ShowDialog callback){
        showDialog = callback;
    }

    public void start(){
        dialog = new DialogEnsureMovability();
        showDialog.show(dialog);

        findMoves = new FindMoves();
        findMoves.execute();
    }

    public void stop(){
        dialog.dismiss();
        findMoves.cancel(true);
    }

    public boolean isRunning(){
        return SharedData.stopUiUpdates;
    }

    public void pause(){
        if (isRunning()) {
            paused = true;
            dialog.dismiss();
            findMoves.interrupt();
        }
    }

    public void saveInstanceState(Bundle bundle){
        if (isRunning() || paused){
            bundle.putBoolean("BUNDLE_ENSURE_MOVABILITY", true);
        }
    }

    public void loadInstanceState(Bundle bundle){
        if (bundle.containsKey("BUNDLE_ENSURE_MOVABILITY")){
            gameLogic.newGame();
        }
    }

    public void resume(){
        if (paused) {
            paused = false;
            gameLogic.load(true);
            gameLogic.newGame();
        }
    }

    private void dismissDialog(){
        dialog.dismiss();
    }

    private static class FindMoves extends AsyncTask<Object, Void, Boolean> {
        private int counter = 0;
        private boolean mainStackAlreadyFlipped = false;
        private boolean isInterrupted = false;

        @Override
        protected Boolean doInBackground(Object... objects) {
            int minPossibleMovements = prefs.getSavedEnsureMovabilityMinMoves();

            try {
                while (true) {
                    if (isCancelled()) {
                        return false;
                    }

                    if (counter == minPossibleMovements || currentGame.winTest()) {
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
                        if (currentGame instanceof Pyramid) {
                            currentGame.cardTest(destination, card);
                        }

                        //logText("Moving " + cardsToMove.get(0).getValue() + " to stack " + cardsToMove.get(0).getStackId());
                        //logText("Counter: " + counter);
                        moveToStack(cardsToMove, destination);

                        if (origin.getSize() > 0 && origin.getId() <= currentGame.getLastTableauId() && !origin.getTopCard().isUp()) {
                            origin.getTopCard().flip();
                        }

                        currentGame.testAfterMove();

                        mainStackAlreadyFlipped = false;
                        counter++;
                    } else if (currentGame.hasMainStack()) {
                        int result = currentGame.mainStackTouch();

                        if (result == 0 || (result == 2 && mainStackAlreadyFlipped)) {
                            nextTry();
                        } else if (result == 2) {
                            mainStackAlreadyFlipped = true;
                        }

                    } else {
                        nextTry();
                    }
                }
            } catch (Exception e) {
                stopUiUpdates = false;
                return false;
            }
        }

        private void nextTry() {
            if (isCancelled()) {
                return;
            }

            counter = 0;
            mainStackAlreadyFlipped = false;
            gameLogic.newGameForEnsureMovability();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            stopUiUpdates = false;

            if (result && !isInterrupted) {
                try {
                    ensureMovability.dismissDialog();
                } catch (IllegalStateException ignored) {
                    //Meh
                }

                gameLogic.redeal();
            }
        }

        @Override
        protected void onCancelled() {
            //will be called after the user presses the "cancel" button in the dialog and after
            //executing doInBackground() the last time

            stopUiUpdates = false;

            if (!isInterrupted) {
                gameLogic.redeal();
            }
        }

        public void interrupt() {
            isInterrupted = true;
            cancel(true);
        }

    }

    public interface ShowDialog{
        void show(DialogEnsureMovability dialog);
    }
}

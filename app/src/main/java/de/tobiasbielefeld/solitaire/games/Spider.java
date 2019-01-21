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

package de.tobiasbielefeld.solitaire.games;

import android.content.Context;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.SharedData;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.Stack;

import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.games.Game.testMode.*;
import static de.tobiasbielefeld.solitaire.games.Game.testMode2.*;
import static de.tobiasbielefeld.solitaire.games.Game.testMode3.*;

/**
 * Spider Solitaire! A bit special game, because it has 2 card decks, the card families depend
 * on the chosen difficulty. The game has 10 tableau stacks, 8 foundation stacks and 4 main stacks
 */

public class Spider extends Game {

    public Spider() {
        setNumberOfDecks(2);
        setNumberOfStacks(23);

        setTableauStackIDs(0,1,2,3,4,5,6,7,8,9);
        setFoundationStackIDs(10,11,12,13,14,15,16,17);
        setMainStackIDs(18,19,20,21,22);

        setMixingCardsTestMode(testMode.SAME_FAMILY);
    }

    public CardAndStack hintTest(ArrayList<Card> visited) {
        for (int i = 0; i < 10; i++) {
            Stack sourceStack = stacks[i];

            if (sourceStack.isEmpty()) {
                continue;
            }

            for (int j = sourceStack.getFirstUpCardPos(); j < sourceStack.getSize(); j++) {
                Card cardToMove = sourceStack.getCard(j);

                if (visited.contains(cardToMove) || !testCardsUpToTop(sourceStack, j, SAME_FAMILY)) {
                    continue;
                }

                Stack returnStack = null;

                for (int k = 0; k < 10; k++) {
                    Stack destStack = stacks[k];

                    if (i == k || destStack.isEmpty()) {
                        continue;
                    }

                    if (cardToMove.test(destStack)) {
                        //if the card above has the correct value, and the card on destination is not the same family as the cardToMove, don't move it
                        if (j > 0 && sourceStack.getCard(j - 1).isUp() && sourceStack.getCard(j - 1).getValue() == cardToMove.getValue() + 1
                                && destStack.getTopCard().getColor() != cardToMove.getColor()) {
                            continue;
                        }

                        //if the card is already on the same card as on the other stack, don't return it
                        if (sameCardOnOtherStack(cardToMove, destStack, SAME_VALUE_AND_FAMILY)) {
                            continue;
                        }

                        if (j == 0 && destStack.getTopCard().getValue() == cardToMove.getValue() + 1 &&  destStack.getTopCard().getColor() != cardToMove.getColor()){
                            continue;
                        }

                        //try to prefer stacks with a top card of the same family as the moving card
                        if (returnStack == null || (destStack.getTopCard().getColor() != returnStack.getTopCard().getColor() && destStack.getTopCard().getColor() == cardToMove.getColor())) {
                            returnStack = destStack;
                        }

                        //return new CardAndStack(cardToMove, destStack);
                    }
                }

                if (returnStack != null) {
                    return new CardAndStack(cardToMove, returnStack);
                }
            }
        }

        return findBestSequenceToMoveToEmptyStack(SAME_FAMILY);
    }

    public Stack doubleTapTest(Card card) {
        Card cardBelow = null;

        if (card.getIndexOnStack() > 0) {
            cardBelow = card.getStack().getCard(card.getIndexOnStack() - 1);
        }

        Stack returnStack = null;
        //tableau stacks
        for (int k = 0; k < 10; k++) {
            Stack destStack = stacks[k];

            if (card.getStackId() == k || destStack.isEmpty()) {
                continue;
            }

            if (cardBelow != null && cardBelow.isUp() && cardBelow.getValue() == card.getValue() + 1 && destStack.getTopCard().getColor() != card.getColor()) {
                continue;
            }

            if (card.test(destStack) && !sameCardOnOtherStack(card, destStack, SAME_VALUE_AND_FAMILY)) {

                //try to prefer stacks with a top card of the same family as the moving card
                if (returnStack == null || (destStack.getTopCard().getColor() != returnStack.getTopCard().getColor() && destStack.getTopCard().getColor() == card.getColor())) {
                    returnStack = destStack;
                }
            }
        }

        if (returnStack != null) {
            return returnStack;
        }

        //empty stacks
        for (int k = 0; k < 10; k++) {
            if (stacks[k].isEmpty() && card.test(stacks[k])) {
                return stacks[k];
            }
        }

        return null;
    }

    public void testAfterMove() {
        /*
         * after a move, test if somewhere is a complete card family, if so, move it to foundations
         */
        for (int i = 0; i < 10; i++) {
            Stack currentStack = stacks[i];

            if (currentStack.isEmpty() || currentStack.getTopCard().getValue() != 1) {
                continue;
            }

            for (int j = currentStack.getFirstUpCardPos(); j < currentStack.getSize(); j++) {
                if (j == -1) {
                    break;
                }

                Card cardToTest = currentStack.getCard(j);

                if (cardToTest.getValue() == 13 && testCardsUpToTop(currentStack, j, SAME_FAMILY)) {
                    Stack foundationStack = stacks[10];

                    while (!foundationStack.isEmpty()) {
                        foundationStack = stacks[foundationStack.getId() + 1];
                    }

                    ArrayList<Card> cards = new ArrayList<>();
                    ArrayList<Stack> origins = new ArrayList<>();

                    for (int k = j; k < currentStack.getSize(); k++) {
                        cards.add(currentStack.getCard(k));
                        origins.add(currentStack);
                    }

                    recordList.addToLastEntry(cards, origins);
                    moveToStack(cards, foundationStack, OPTION_NO_RECORD);

                    //turn the card below up, if there is one
                    if (!currentStack.isEmpty() && !currentStack.getTopCard().isUp()) {
                        currentStack.getTopCard().flipWithAnim();
                    }

                    scores.update(200);
                    break;
                }
            }
        }
    }

    public boolean addCardToMovementGameTest(Card card) {
        //do not accept cards from foundation and test if the cards are in the right order.
        return card.getStackId() < 10 && currentGame.testCardsUpToTop(card.getStack(), card.getIndexOnStack(), SAME_FAMILY);
    }

    public boolean cardTest(Stack stack, Card card) {
        return stack.getId() < 10 && currentGame.canCardBePlaced(stack, card, DOESNT_MATTER, DESCENDING);
    }

    public void setStacks(RelativeLayout layoutGame, boolean isLandscape, Context context) {
        //initialize the dimensions
        setUpCardWidth(layoutGame, isLandscape, 11, 12);
        int spacing = setUpHorizontalSpacing(layoutGame, 10, 11);
        int startPos = layoutGame.getWidth() - Card.width - 5 * Card.width / 2;
        //main stacks
        for (int i = 0; i < 5; i++) {
            stacks[18 + i].setX(startPos + i * Card.width / 2);
            stacks[18 + i].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1);
            stacks[18 + i].setImageBitmap(Stack.backgroundTransparent);
        }
        //foundation stacks
        for (int i = 0; i < 8; i++) {
            stacks[10 + i].setX(Card.width / 2 + i * Card.width / 2);
            stacks[10 + i].view.setY((isLandscape ? Card.width / 4 : Card.width / 2) + 1);
            stacks[10 + i].setImageBitmap(Stack.backgroundTransparent);
        }
        //tableau stacks
        startPos = layoutGame.getWidth() / 2 - 5 * Card.width - 4 * spacing - spacing / 2;
        for (int i = 0; i < 10; i++) {
            stacks[i].setX(startPos + spacing * i + Card.width * i);
            stacks[i].setY(stacks[18].getY() + Card.height + (isLandscape ? Card.width / 4 : Card.width / 2) + 1);
        }
        //set card families depending on settings
        loadCards();
    }

    public boolean winTest() {
        //if every foundation stacks is full, game is won
        for (int i = 0; i < 8; i++)
            if (stacks[10 + i].getSize() != 13)
                return false;

        return true;
    }

    public void dealCards() {
        //when starting a new game, load the difficulty preference in the "old" preference
        prefs.saveSpiderDifficultyOld();
        loadCards();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 5; j++) {
                moveToStack(getMainStack().getTopCard(), stacks[i], OPTION_NO_RECORD);
            }

            if (i < 4) {
                moveToStack(getMainStack().getTopCard(), stacks[i], OPTION_NO_RECORD);
            }

            stacks[i].flipTopCardUp();
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                moveToStack(getMainStack().getTopCard(), stacks[18 + i], OPTION_NO_RECORD);
            }
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                if (stacks[18+i].getSize()>j) {
                    stacks[18 + i].getCard(j).bringToFront();
                }
            }
        }
    }

    public int onMainStackTouch() {
        /*
         * first getHighScore the current main stack, then deal the cards from it to the tableau.
         * with the reversed record option
         */
        int currentMainStackID = 22;

        while (currentMainStackID > 17 && stacks[currentMainStackID].isEmpty())
            currentMainStackID--;

        //id below 18 means all main stacks are empty
        if (currentMainStackID >= 18) {

            ArrayList<Card> cards = new ArrayList<>();
            ArrayList<Stack> destinations = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                cards.add(stacks[currentMainStackID].getCardFromTop(i));
                stacks[currentMainStackID].getCardFromTop(i).flipUp();
                destinations.add(stacks[i]);
            }

            moveToStack(cards, destinations, OPTION_REVERSED_RECORD);

            //test if a card family is now full
            handlerTestAfterMove.sendDelayed();
            return 1;
        }

        return 0;
    }

    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs, boolean isUndoMovement) {
        int points = 0;
        boolean foundation = false;

        for (int i = 0; i < originIDs.length; i++) {
            if (originIDs[i] == destinationIDs[i])
                points += 25;

            if (!foundation && destinationIDs[i] >= 10 && destinationIDs[i] < 18) {
                points += 200;
                foundation = true;
            }
        }

        return points;
    }

    private void loadCards() {
        /*
         * load the card families depending on the preference
         */
        switch (prefs.getSavedSpiderDifficultyOld()) {
            case "1":
                setCardFamilies(3, 3, 3, 3);
                break;
            case "2":
                setCardFamilies(2, 3, 2, 3);
                break;
            case "4":
                setCardFamilies(1, 2, 3, 4);
                break;
        }

        //and update the cards!
        for (Card card : cards) {
            card.setColor();
        }

        Card.updateCardDrawableChoice();
    }

    public boolean autoCompleteStartTest() {
        for (int i = 0; i < 4; i++)
            if (!stacks[18 + i].isEmpty())
                return false;

        for (int i = 0; i < 10; i++)
            if (stacks[i].getSize() > 0 && (stacks[i].getFirstUpCardPos() != 0 || !testCardsUpToTop(stacks[i], 0, SAME_FAMILY)))
                return false;

        return true;
    }

    public CardAndStack autoCompletePhaseOne() {

        for (int i = 0; i < 10; i++) {
            Stack sourceStack = stacks[i];

            if (sourceStack.isEmpty())
                continue;

            Card cardToMove = sourceStack.getCard(0);

            for (int k = 0; k < 10; k++) {
                Stack destStack = stacks[k];
                if (i == k || destStack.isEmpty() || destStack.getTopCard().getColor() != cardToMove.getColor()) {
                    continue;
                }

                if (cardToMove.test(destStack)) {
                    return new CardAndStack(cardToMove, destStack);
                }
            }
        }

        return null;
    }
}

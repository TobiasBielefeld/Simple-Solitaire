package de.tobiasbielefeld.solitaire.games;

import android.content.Context;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.Stack;

import static de.tobiasbielefeld.solitaire.SharedData.*;

public class Maze extends Game {
    private static final int ROWS = 6;
    private static final int COLS = 9;

    /**
     * How many points to grant for each pair of cards in order.
     */
    private static final int POINTS_PER_ORDERED_PAIR = 25;

    private int undoCount = 0;

    public Maze() {
        setNumberOfDecks(1);
        setNumberOfStacks(ROWS * COLS);
        setDealFromID(0);
        setLastTableauID(ROWS * COLS - 1);
        setDiscardStackIDs(ROWS * COLS);
    }

    @Override
    public void setStacks(RelativeLayout layoutGame, boolean isLandscape, Context context) {
        setUpCardDimensions(layoutGame, COLS + 1, ROWS + 1);

        int spacing = min(
                setUpHorizontalSpacing(layoutGame, COLS, COLS + 1),
                setUpVerticalSpacing(layoutGame, ROWS, ROWS + 1));

        int startX = (layoutGame.getWidth() - COLS * Card.width - (COLS + 1) * spacing) / 2;
        int startY = (layoutGame.getHeight() - ROWS * Card.height - (ROWS + 1) * spacing) / 2;

        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                int stackIdx = row * COLS + col;
                stacks[stackIdx].setX(startX + (col + 1) * spacing + col * Card.width);
                stacks[stackIdx].setY(startY + (row + 1) * spacing + row * Card.height);
            }
        }
    }

    @Override
    public boolean winTest() {
        return countCardsInOrder() == 48;
    }

    @Override
    public void dealCards() {
        flipAllCardsUp();

        // Deal all cards, skipping the last column of the first two rows.
        // (Note that we also skip the first stack, because it is the deal
        // stack. It gets whatever card is left there at the end.)
        int nextRow = 0;
        int nextCol = 1;
        while (stacks[0].getSize() > 1) {
            Card cardToMove = stacks[0].getTopCard();

            if (cardToMove.getValue() == 13) {
                cardToMove.removeFromGame();
            } else {
                moveToStack(cardToMove, stacks[nextRow * COLS + nextCol], OPTION_NO_RECORD);
            }

            int colsInRow = nextRow < 2 ? COLS - 1 : COLS;
            ++nextCol;
            if (nextCol >= colsInRow) {
                ++nextRow;
                nextCol = 0;
            }
        }

        updateScore();
    }

    @Override
    public int onMainStackTouch() {
        // There is no main stack, so do nothing.
        return 0;
    }

    @Override
    public boolean cardTest(Stack stack, Card card) {
        if (!stack.isEmpty() || stack.getId() > getLastTableauId()) {
            return false;
        }

        Stack prevStack = stacks[(stack.getId() + getLastTableauId()) % (getLastTableauId() + 1)];
        Stack nextStack = stacks[(stack.getId() + 1) % (getLastTableauId() + 1)];

        if (!prevStack.isEmpty() && areCardsInOrder(prevStack.getTopCard(), card)) {
            // Allow appending to a sequence, or starting a new sequence after
            // a Queen.
            return true;
        } else // Allow prepending to a sequence.
// Everything else is disallowed.
            return !nextStack.isEmpty() && nextStack.getTopCard().getValue() != 1 && areCardsInOrder(card, nextStack.getTopCard());
    }

    @Override
    public boolean addCardToMovementGameTest(Card card) {
        // Anything on the tableau can be moved.
        return true;
    }

    @Override
    public CardAndStack hintTest(ArrayList<Card> visited) {
        ArrayList<Stack> gaps = getGaps();

        // Test every card to see if it can be moved to any gap.
        for (int i = 0; i <= getLastTableauId(); ++i) {
            if (stacks[i].isEmpty()) {
                continue;
            }

            Card card = stacks[i].getTopCard();

            Stack prevStack = stacks[(i + getLastTableauId()) % (getLastTableauId() + 1)];
            Stack nextStack = stacks[(i + 1) % (getLastTableauId() + 1)];

            if (visited.contains(card) || (!nextStack.isEmpty() && areCardsInOrder(card, nextStack.getTopCard()))
                    || (!prevStack.isEmpty() && areCardsInOrder(prevStack.getTopCard(), card))) {
                continue;
            }

            for (Stack stack : gaps) {
                if (card.test(stack)) {
                    return new CardAndStack(card, stack);
                }
            }
        }

        return null;
    }

    @Override
    public int addPointsToScore(ArrayList<Card> cards, int[] originIDs, int[] destinationIDs, boolean isUndoMovement) {
        if (isUndoMovement) {
            ++undoCount;
        }

        // The score is updated in testAfterMove() and afterUndo(), because
        // those functions have access to the tableau state after the move.
        return 0;
    }

    @Override
    public void testAfterMove() {
        updateScore();
    }

    @Override
    public void afterUndo() {
        updateScore();
    }

    @Override
    Stack doubleTapTest(Card card) {
        ArrayList<Stack> gaps = getGaps();

        for (Stack stack : gaps) {
            if (card.test(stack)) {
                return stack;
            }
        }

        return null;
    }

    @Override
    protected boolean excludeCardFromMixing(Card card) {
        // Mixing probably doesn't make sense for this game.
        return true;
    }

    /**
     * Test if two cards are in the correct order.
     */
    private boolean areCardsInOrder(Card first, Card second) {
        if (first.getValue() == 12) {
            // End of one sequence, start of another.
            return second.getValue() == 1;
        } else {
            return first.getColor() == second.getColor() && first.getValue() + 1 == second.getValue();
        }
    }

    /**
     * Count how many pairs of cards are in order.
     */
    private int countCardsInOrder() {
        ArrayList<Card> orderedCards = new ArrayList<>();
        for (int i = 0; i <= getLastTableauId(); ++i) {
            if (!stacks[i].isEmpty()) {
                orderedCards.add(stacks[i].getTopCard());
            }
        }

        // Test every pair of cards (wrapping around at the end).
        int inOrder = 0;
        for (int i = 0; i < orderedCards.size(); ++i) {
            if (areCardsInOrder(orderedCards.get(i), orderedCards.get((i + 1) % orderedCards.size()))) {
                ++inOrder;
            }
        }

        return inOrder;
    }

    /**
     * Update the score to reflect the current tableau state.
     */
    private void updateScore() {
        long newScore = countCardsInOrder() * POINTS_PER_ORDERED_PAIR - undoCount * getUndoCosts();
        scores.update(newScore - scores.getScore());
    }

    /**
     * Make list of potential destination stacks.
     */
    private ArrayList<Stack> getGaps() {
        ArrayList<Stack> gaps = new ArrayList<>();
        for (int i = 0; i <= getLastTableauId(); ++i) {
            if (stacks[i].isEmpty()) {
                gaps.add(stacks[i]);
            }
        }

        return gaps;
    }
}

package nl.gogognome.gogochess.gui;

import nl.gogognome.gogochess.logic.Board;
import nl.gogognome.gogochess.logic.Move;
import nl.gogognome.gogochess.logic.ai.ArtificalIntelligenceCanceledException;
import nl.gogognome.gogochess.logic.ai.ArtificialIntelligence;
import nl.gogognome.gogochess.logic.ai.ProgressListener;
import nl.gogognome.gogochess.logic.ai.RecursiveSearchAI;
import nl.gogognome.gogochess.logic.movenotation.MoveNotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

public class AiController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private final ArtificialIntelligence ai;
    private final MoveNotation moveNotation;

    private Consumer<Integer> percentageConsumer = percentage -> {};
    private Consumer<Move> computerMoveConsumer = move -> {};

    private final ProgressListener progressListener = new ProgressListener()
            .withProgressUpdateConsumer(this::setPercentage)
            .withBestMovesConsumer(bestMoves -> setNextExpectedOpponentsMove(bestMoves.size() >= 2 ? bestMoves.get(1) : null));

    private long aiStartTime;
    private long aiMaxEndTime;
    private long lastTimeMaxDepthDeltaWasChanged;
    private AIThinkingLimit thinkingLimit = AIThinkingLimit.seconds(15);

    private boolean computerThinksDuringOpponentsTurn;
    private Move nextExpectedOpponentsMove;
    private Move expectedOpponentsMove;
    private Move responseToExpectedOpponentsMove;

    private final Object lock = new Object();
    private Semaphore thinkingSemaphore = new Semaphore(1);

    @Inject
    public AiController(ArtificialIntelligence ai, MoveNotation moveNotation) {
        this.ai = ai;
        this.moveNotation = moveNotation;
    }

    void setPercentageConsumer(Consumer<Integer> percentageConsumer) {
        this.percentageConsumer = percentageConsumer;
    }

    AIThinkingLimit getThinkingLimit() {
        return thinkingLimit;
    }

    void setThinkingLimit(AIThinkingLimit thinkingLimit) {
        this.thinkingLimit = thinkingLimit;
    }

    void setComputerMoveConsumer(Consumer<Move> computerMoveConsumer) {
        this.computerMoveConsumer = computerMoveConsumer;
    }

    private void setNextExpectedOpponentsMove(Move nextExpectedOpponentsMove) {
        synchronized (lock) {
            this.nextExpectedOpponentsMove = nextExpectedOpponentsMove;
        }
    }

    private void setPercentage(int percentage) {
        if (!computerThinksDuringOpponentsTurn) {
            percentageConsumer.accept(percentage);
        }

        if (percentage > 10) {
            updateMaxDepthDelta(percentage);
        }
    }

    void startThinking(Move lastMove) {
        synchronized (lock) {
            if (expectedOpponentsMove != null && lastMove.getBoardMutations().equals(expectedOpponentsMove.getBoardMutations())) {
                if (computerThinksDuringOpponentsTurn) {
                    logger.debug("Computer started thinking for the correct move of the opponent.");
                    computerThinksDuringOpponentsTurn = false;
                    return;
                }
                if (responseToExpectedOpponentsMove != null) {
                    logger.debug("Computer already finished thinking for the correct move of the opponent.");
                    computerMoveConsumer.accept(responseToExpectedOpponentsMove);
                    expectedOpponentsMove = null;
                    responseToExpectedOpponentsMove = null;
                    return;
                }
            }

            if (computerThinksDuringOpponentsTurn) {
                logger.debug("Cancel computer thinking in opponents turn because the opponent made a different move than expected.");
                ai.cancel();
                computerThinksDuringOpponentsTurn = false;
            }

            letComputerThinkOfBestResponseTo(lastMove);
        }
    }

    void startThinkingDuringOpponentsTurn() {
        synchronized (lock) {
            if (nextExpectedOpponentsMove == null) {
                return;
            }

            expectedOpponentsMove = nextExpectedOpponentsMove;
            computerThinksDuringOpponentsTurn = true;
            letComputerThinkOfBestResponseTo(expectedOpponentsMove);
        }
    }

    private void letComputerThinkOfBestResponseTo(Move move) {
        try {
            thinkingSemaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.submit(() -> computerThinking(move));
    }

    private void computerThinking(Move lastMove) {
        try {
            String formattedMove = lastMove.getPrecedingMove() == null ? "board setup" : moveNotation.format(lastMove);
            logger.debug("Start thinking for response to " + formattedMove +
                    (computerThinksDuringOpponentsTurn ? " during opponents turn" : ""));

            setupSearchParameters();

            Board boardForArtificialIntelligence = new Board();
            boardForArtificialIntelligence.process(lastMove);
            Move move = ai.nextMove(
                    boardForArtificialIntelligence,
                    boardForArtificialIntelligence.currentPlayer(),
                    progressListener);

            logEndOfSearch();

            synchronized (lock) {
                if (computerThinksDuringOpponentsTurn) {
                    logger.debug("Store response to expected move " + moveNotation.format(lastMove) + ": " + moveNotation.format(move));
                    responseToExpectedOpponentsMove = move;
                    computerThinksDuringOpponentsTurn = false;
                } else {
                    logger.debug("Computer move: " + moveNotation.format(move));
                    computerMoveConsumer.accept(move);
                }
            }
        } catch (ArtificalIntelligenceCanceledException e) {
            logger.debug("Canceled thinking");
        } catch (Exception e) {
            logger.error("Problem occurred: " + e.getMessage(), e);
        } finally {
            thinkingSemaphore.release();
        }
    }

    private void setupSearchParameters() {
        aiStartTime = System.currentTimeMillis();
        // Initialize max seconds in case the user toggles from level to time while the computer is thinking.
        switch (thinkingLimit.getUnit()) {
            case SECONDS:
                int maxSecondsToThink = thinkingLimit.getUnit() == AIThinkingLimit.Unit.SECONDS ? thinkingLimit.getValue() : 15;
                aiMaxEndTime = aiStartTime + maxSecondsToThink * 1000;
                break;

            case LEVEL:
                if (ai instanceof RecursiveSearchAI) {
                    ((RecursiveSearchAI) ai).setMaxDepth(thinkingLimit.getValue());
                }
                break;
            default:
               throw new IllegalStateException("Unknown unit encountered: " + thinkingLimit.getValue());
        }
    }

    private void logEndOfSearch() {
        long aiEndTime = System.currentTimeMillis();
        logger.debug("Computer has thought for " + (aiEndTime - aiStartTime) / 1000  + " seconds");
    }


    private void updateMaxDepthDelta(Integer percentage) {
        if (thinkingLimit.getUnit() != AIThinkingLimit.Unit.SECONDS) {
            return;
        }

        long now = System.currentTimeMillis();

        int maxDepthDelta = 0;
        int durationPercentage = (int) (100 * ((now - aiStartTime)) / (aiMaxEndTime - aiStartTime));
        if (durationPercentage > percentage) {
            maxDepthDelta--;
        }
        if (durationPercentage > 2 * percentage) {
            maxDepthDelta--;
        }
        if (durationPercentage > 4 * percentage) {
            maxDepthDelta--;
        }
        if (percentage > 2 * durationPercentage) {
            maxDepthDelta++;
        }
        if (percentage > 4 * durationPercentage) {
            maxDepthDelta++;
        }
        if (maxDepthDelta != progressListener.getMaxDepthDelta().get() && (lastTimeMaxDepthDeltaWasChanged + 1000 < now)) {
            progressListener.getMaxDepthDelta().set(maxDepthDelta);
            lastTimeMaxDepthDeltaWasChanged = now;
            logger.debug("Set max depth delta to " + maxDepthDelta + " because AI percentage is "
                    + percentage + "% and duration percentage is " + durationPercentage + "%.");
        }
    }

    void cancelThinking() {
        ai.cancel();
        try {
            thinkingSemaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        thinkingSemaphore.release();
    }

    void onClose() {
        cancelThinking();
        executorService.shutdownNow();
    }
}

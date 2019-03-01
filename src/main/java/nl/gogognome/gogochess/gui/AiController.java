package nl.gogognome.gogochess.gui;

import static com.google.common.primitives.Ints.*;
import static java.util.stream.Collectors.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import javax.inject.*;
import org.slf4j.*;
import nl.gogognome.gogochess.logic.*;
import nl.gogognome.gogochess.logic.ai.*;
import nl.gogognome.gogochess.logic.movenotation.*;

public class AiController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
    private final ArtificialIntelligence ai;
    private final MoveNotation moveNotation;
    private final ScheduledFuture<?> tickFuture;

    /**
     * Indicates if this controller has been closed. Once closed, no methods on this instance must be called anymore.
     */
    private boolean closed;

    private Consumer<Integer> percentageConsumer = percentage -> {};
    private Consumer<Move> computerMoveConsumer = move -> {};

    private final ProgressListener progressListener = new ProgressListener()
            .withProgressUpdateConsumer(this::onSetPercentage)
            .withBestMovesConsumer(this::onBestMovesReceived);

    private long aiStartTime;
    private long aiMaxEndTime;
    private long lastTimeMaxDepthDeltaWasChanged;
    private AIThinkingLimit thinkingLimit = AIThinkingLimit.seconds(15);

    private boolean computerThinksDuringOpponentsTurn;
    private Move nextExpectedOpponentsMove;
    private Move expectedOpponentsMove;
    private Move responseToExpectedOpponentsMove;

    /**
     * The computer is currently thinking.
     */
    private Semaphore thinkingSemaphore = new Semaphore(1);
    private int lastKnownPercentage;
    private int initialMaxDepthForTimeLimit = 2;
    private List<Integer> lastInitialMaxDepths = new ArrayList<>(asList(2, 2, 2, 2));

    private BlockingDeque<Runnable> actionQueue = new LinkedBlockingDeque<>();
    private boolean actionQueueTerminated;
    private final Future<?> actionQueueFuture;

    @Inject
    public AiController(ArtificialIntelligence ai, MoveNotation moveNotation) {
        this.ai = ai;
        this.moveNotation = moveNotation;
        tickFuture = executorService.scheduleWithFixedDelay(this::onTick, 0, 1, TimeUnit.SECONDS);
        actionQueueFuture = executorService.submit(this::actionQueueHandler);
    }

    private void actionQueueHandler() {
        logger.debug("Action queue handler started");
        while (!actionQueueTerminated) {
            try {
                Runnable runnable = actionQueue.takeFirst();
                runnable.run();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        logger.debug("Action queue handler finished");
    }

    private void scheduleAction(Runnable runnable) {
        if (actionQueueTerminated) {
            throw new IllegalStateException("The action queue has been terminated already");
        }
        actionQueue.add(runnable);
    }

    private void onTick() {
        try {
            if (!isComputerThinking()) {
                return;
            }

            updateMaxDepthDelta(lastKnownPercentage);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private boolean isComputerThinking() {
        if (thinkingSemaphore.tryAcquire()) {
            thinkingSemaphore.release();
            return false;
        }
        return true;
    }

    void setPercentageConsumer(Consumer<Integer> percentageConsumer) {
        scheduleAction(() -> this.percentageConsumer = percentageConsumer);
    }

    void setComputerMoveConsumer(Consumer<Move> computerMoveConsumer) {
        scheduleAction(() -> this.computerMoveConsumer = computerMoveConsumer);
    }

    AIThinkingLimit getThinkingLimit() {
        return thinkingLimit;
    }

    void setThinkingLimit(AIThinkingLimit thinkingLimit) {
        this.thinkingLimit = thinkingLimit;
    }

    private void onSetPercentage(int percentage) {
        scheduleAction(() -> setPercentage(percentage));
    }

    private void onBestMovesReceived(List<Move> bestMoves) {
        scheduleAction(() -> {
            logger.info("Best moves found: " + bestMoves.stream().map(moveNotation::format).collect(joining(", ")));
            nextExpectedOpponentsMove = (bestMoves.size() >= 2) ? bestMoves.get(1) : null;
        });
    }

    private void setPercentage(int percentage) {
        lastKnownPercentage = percentage;
        if (!computerThinksDuringOpponentsTurn) {
            percentageConsumer.accept(percentage);
        }
    }

    void onStartThinking(Move lastMove) {
        scheduleAction(() -> startThinking(lastMove));
    }

    private void startThinking(Move lastMove) {
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

    void onStartThinkingDuringOpponentsTurn() {
        scheduleAction(this::startThinkingDuringOpponentsTurn);
    }

    private void startThinkingDuringOpponentsTurn() {
        if (nextExpectedOpponentsMove == null) {
            return;
        }

        expectedOpponentsMove = nextExpectedOpponentsMove;
        computerThinksDuringOpponentsTurn = true;
        letComputerThinkOfBestResponseTo(expectedOpponentsMove);
    }

    private void letComputerThinkOfBestResponseTo(Move move) {
        if (move.getStatus().isGameOver()) {
            return;
        }

        try {
            thinkingSemaphore.acquire();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
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

            scheduleAction(() -> onEndOfSearch(lastMove, move));
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
        progressListener.getMaxDepthDelta().set(0);
        switch (thinkingLimit.getUnit()) {
            case SECONDS:
                int maxSecondsToThink = thinkingLimit.getUnit() == AIThinkingLimit.Unit.SECONDS ? thinkingLimit.getValue() : 15;
                aiMaxEndTime = aiStartTime + maxSecondsToThink * 1000;
                ((RecursiveSearchAI) ai).setMaxDepth(initialMaxDepthForTimeLimit);
                break;

            case LEVEL:
                if (ai instanceof RecursiveSearchAI) {
                    ((RecursiveSearchAI) ai).setMaxDepth(thinkingLimit.getValue());
                }
                // Initialize maxEndTime in case the user toggles from level to time while the computer is thinking.
                aiMaxEndTime = aiStartTime + 15 * 1000;
                break;
            default:
               throw new IllegalStateException("Unknown unit encountered: " + thinkingLimit.getValue());
        }
    }

    private void onEndOfSearch(Move lastMove, Move move) {
        long aiEndTime = System.currentTimeMillis();
        int actualSeconds = (int) ((aiEndTime - aiStartTime) / 1000);
        logger.debug("Computer has thought for " + actualSeconds + " seconds");

        updateInitialMaxDepth(actualSeconds, progressListener.getMaxDepthDelta().get());

        if (computerThinksDuringOpponentsTurn) {
            logger.debug("Store response to expected move " + moveNotation.format(lastMove) + ": " + moveNotation.format(move) + ", value: " + move.getValue());
            responseToExpectedOpponentsMove = move;
            computerThinksDuringOpponentsTurn = false;
        } else {
            logger.debug("Computer move: " + moveNotation.format(move) + ", value: " + move.getValue());
            computerMoveConsumer.accept(move);
        }
    }

    private void updateInitialMaxDepth(int actualSeconds, int lastMaxDepthDelta) {
        if (thinkingLimit.getUnit() != AIThinkingLimit.Unit.SECONDS) {
            return;
        }

        lastInitialMaxDepths.add(calculateNextInitialMaxDepth(actualSeconds, lastMaxDepthDelta));
        lastInitialMaxDepths.remove(0);
        int nextInitialMaxDepth = (int) (lastInitialMaxDepths.stream()
                .mapToInt(v -> v)
                .average()
                .orElse(initialMaxDepthForTimeLimit) + 0.5);
        logger.debug("Last initial max depths " + lastInitialMaxDepths);
        if (nextInitialMaxDepth != initialMaxDepthForTimeLimit) {
            logger.debug("Changed initial max depth from " + initialMaxDepthForTimeLimit + " to " + nextInitialMaxDepth);
            initialMaxDepthForTimeLimit = nextInitialMaxDepth;
        }
    }

    private int calculateNextInitialMaxDepth(int actualSeconds, int lastMaxDepthDelta) {
        int nextInitialMaxDepth = initialMaxDepthForTimeLimit;
        if (actualSeconds <= 0.8f * thinkingLimit.getValue()) {
            nextInitialMaxDepth++;
        }
        if (actualSeconds <= 0.5f * thinkingLimit.getValue()) {
            nextInitialMaxDepth++;
        }
        if (actualSeconds >= 1.2f * thinkingLimit.getValue()) {
            nextInitialMaxDepth--;
        }
        if (actualSeconds >= 1.5f * thinkingLimit.getValue()) {
            nextInitialMaxDepth--;
        }
        if (actualSeconds >= 2.0f * thinkingLimit.getValue()) {
            nextInitialMaxDepth--;
        }

        if (lastMaxDepthDelta <= -2) {
            nextInitialMaxDepth--;
        }

        return nextInitialMaxDepth;
    }

    private void updateMaxDepthDelta(int percentage) {
        if (thinkingLimit.getUnit() != AIThinkingLimit.Unit.SECONDS) {
            return;
        }

        long now = System.currentTimeMillis();
        long duration = now - aiStartTime;
        if (duration < 500 || lastTimeMaxDepthDeltaWasChanged + 100 > now) {
            return;
        }

        long targetDuration = aiMaxEndTime - aiStartTime;
        int durationPercentage = (int) (100 * duration / targetDuration);
        float timeLeftPercentage = 100 - durationPercentage;
        float workLeftPercentage = 100 - percentage;
        if (workLeftPercentage <= 10) {
            return;
        }

        int currentMaxDepthDelta = progressListener.getMaxDepthDelta().get();
        int maxDepthDelta = timeLeftPercentage > 0
                ? (int) Math.round(Math.log(timeLeftPercentage / workLeftPercentage) / Math.log(2))
                : currentMaxDepthDelta-1;

        if (-2 <= maxDepthDelta && maxDepthDelta <= 0 && maxDepthDelta != currentMaxDepthDelta) {
            progressListener.getMaxDepthDelta().set(maxDepthDelta);
            lastTimeMaxDepthDeltaWasChanged = now;
            logger.debug("Duration " + duration + ", Target duration: " + targetDuration + ", Duration %: " + durationPercentage + ", %: " + percentage + ", New delta: " + maxDepthDelta);
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
        if (closed) {
            throw new IllegalStateException("close() has been called before!");
        }

        tickFuture.cancel(false);
        scheduleAction(() -> actionQueueTerminated = true);
        actionQueueFuture.cancel(false);
        cancelThinking();
        executorService.shutdownNow();
        closed = true;
    }
}

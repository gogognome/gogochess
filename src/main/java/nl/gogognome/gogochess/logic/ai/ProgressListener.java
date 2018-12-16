package nl.gogognome.gogochess.logic.ai;

import nl.gogognome.gogochess.logic.Move;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * The artificial intelligence notifies a progress listener about the progress of finding the next move.
 * It also offers the progress listener to modify the max depth to influence the performance of the artificial
 * intelligence.
 */
public class ProgressListener {

    private Consumer<Integer> progressUpdateConsumer = percentage -> {};

    private Consumer<List<Move>> bestMovesConsumer = bestMoves -> {};

    private final AtomicInteger maxDepthDelta = new AtomicInteger();

    /**
     * @param progressUpdateConsumer consumer that receives updates on the thinking process. The Integer value represents
     *                       a percentage and will be in the range [0..100]
     */
    public ProgressListener withProgressUpdateConsumer(Consumer<Integer> progressUpdateConsumer) {
        this.progressUpdateConsumer = progressUpdateConsumer;
        return this;
    }

    public Consumer<Integer> getProgressUpdateConsumer() {
        return progressUpdateConsumer;
    }

    /**
     * @param bestMovesConsumer consumer that receives updates on the best moves forward discovered during the thinking process
     */
    public ProgressListener withBestMovesConsumer(Consumer<List<Move>> bestMovesConsumer) {
        this.bestMovesConsumer = bestMovesConsumer;
        return this;
    }

    public void consumeBestMoves(List<Move> bestMoves) {
        bestMovesConsumer.accept(bestMoves);
    }

    /**
     * @return this value is added to the max depth setting for each move analyzed by the arttificial intelligence
     */
    public AtomicInteger getMaxDepthDelta() {
        return maxDepthDelta;
    }

}

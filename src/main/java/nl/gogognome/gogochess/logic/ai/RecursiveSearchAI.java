package nl.gogognome.gogochess.logic.ai;

/**
 * Artificial intelligence implementations that use recursion or otherwise search a tree should implement this
 * interface to let clients control the maximum depth of the recursion or tree search.
 */
public interface RecursiveSearchAI {

    /**
     * @param maxDepth the maximum depth of the search. Must be a positive number.
     */
    void setMaxDepth(int maxDepth);
}

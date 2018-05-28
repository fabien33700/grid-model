package org.gridmodel.core.model;

/**
 * Represents an extraction context for a custom column, which means
 *   the context provided for each value in order to transform them in a custom column.
 * @author Fabien
 */
public interface ExtractionContext {
    /**
     * Gives the name of the data source.
     * @return A String representing the source name
     */
    String sourceName();

    /**
     * Gives the current column name
     * @return The column name
     */
    String currentColumnName();

    /**
     * Gives the current row index
     * @return The current row index
     */
    Long currentRowIndex();

    /**
     * A helper method to quickly build an anonymous ExtractionContext instance.
     * @param sourceName The source name
     * @param currentColumnName The current column name
     * @param currentRowIndex The current row index
     * @return An ExtractionContext instance
     */
    static ExtractionContext build(String sourceName,
                                   String currentColumnName,
                                   long currentRowIndex)
    {
        return new ExtractionContext() {
            @Override public String sourceName() { return sourceName; }
            @Override public String currentColumnName() { return currentColumnName; }
            @Override public Long currentRowIndex() { return currentRowIndex; }
        };
    }
}

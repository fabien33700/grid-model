package org.flatbase.query;

import static java.lang.String.format;

public class QueryException extends RuntimeException {
    private final static String EX_UNASSIGNABLE_PARAM =
            "The given parameter type is not compatible with the queried column one.\n" +
                    "\tReference value type : %s\n" +
                    "\tColumn name : %s\n" +
                    "\tExpected column type : %s\n";

    public QueryException(String referenceType,
                          String indexType, String columnName) {
        super(format(EX_UNASSIGNABLE_PARAM,
                referenceType, columnName, indexType));
    }
}

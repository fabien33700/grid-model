package org.flatbase.misc;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class Utils {

    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static String format(String format, Object ... args) {
        final String PLACEHOLDER = "{}";
        Iterator<?> it = asList(args).iterator();
        String result = format;

        while (it.hasNext() && result.contains(PLACEHOLDER)) {
            Object arg = it.next();
            String repr = arg == null ? "" : arg.toString();
            result = result.replaceFirst(Pattern.quote(PLACEHOLDER), repr);
        }

        return result;
    }

    public static <T, R> R orElse(Optional<T> value, Function<T, R> mapper, R defaultValue) {
        if (value.isPresent()) {
            R result = mapper.apply(value.get());
            if (result != null) return result;
        }
        return defaultValue;
    }

    public static List<String> parseIncompatibleTypes(ClassCastException ex) {
        final String REGEX = "(.*) cannot be cast to (.*)";
        Matcher m = Pattern.compile(REGEX).matcher(ex.getMessage());

        return m.find() ? asList(m.group(1), m.group(2)) : emptyList();
    }

}

package org.flatbase.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class Utils {
    private static Optional<Object> optional;
    private static Function<Object, Object> mapper;
    private static Object defaultValue;

    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static List<String> split(String line, String separator) {
        return Stream.of(line.split(separator))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public static String format(String format, Object ... args) {
        final String PLACEHOLDER = "{}";
        Iterator<Object> objects = asList(args).iterator();
        String result = format;

        while (objects.hasNext() && result.contains(PLACEHOLDER)) {
            Object arg = objects.next();
            String repr = arg == null ? "" : arg.toString();
            result = result.replaceFirst(Pattern.quote(PLACEHOLDER), repr);
        }

        return result;
    }

    public static <T, R> R orElse(Optional<T> optional, Function<T, R> mapper, R defaultValue) {
        return optional.isPresent() ? mapper.apply(optional.get()) : defaultValue;
    }

}

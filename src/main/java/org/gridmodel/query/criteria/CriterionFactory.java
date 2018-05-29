package org.gridmodel.query.criteria;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.stream.Collectors;

public class CriterionFactory {

    private static <T> List<Long> extract(NavigableMap<T, Set<Long>> nmap) {
        return nmap.values()
                .stream()
                .flatMap(Set::stream)
                .collect(Collectors.toList());
    }

    public static <T extends Comparable<T>> CriterionOperation<T>
    greaterOrEqual(T referenceValue) {
        return new CriterionOperation<>(" >= " + referenceValue,
                nmap -> extract(nmap.tailMap(referenceValue, true)));
    }

    public static <T extends Comparable<T>> CriterionOperation<T>
    greaterThan(T referenceValue) {
        return new CriterionOperation<>(" > " + referenceValue,
                nmap -> extract(nmap.tailMap(referenceValue, false)));
    }

    public static <T extends Comparable<T>> CriterionOperation<T>
    lesserOrEqual(T referenceValue) {
        return new CriterionOperation<>(" <= " + referenceValue,
                nmap -> extract(nmap.headMap(referenceValue, true)));
    }

    public static <T extends Comparable<T>> CriterionOperation<T>
    lesserThan(T referenceValue) {
        return new CriterionOperation<>(" < " + referenceValue,
                nmap -> extract(nmap.headMap(referenceValue, false)));
    }

    public static <T extends Comparable<T>> CriterionOperation<T>
    includedIn(List<T> referenceValues) {
        return new CriterionOperation<>(" in " + referenceValues.toString(),
                nmap -> nmap.entrySet().stream()
                    .filter(entry -> referenceValues.contains(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .flatMap(Set::stream)
                    .collect(Collectors.toList()));
    }

    public static <T extends Comparable<T>> CriterionOperation<T>
    equalTo(T referenceValue) {
        return new CriterionOperation<>(" = " + referenceValue,
                nmap -> extract(nmap
                    .tailMap(referenceValue, true)
                    .headMap(referenceValue, true)));
    }

    public static <T extends Comparable<T>> CriterionOperation<T>
    between(T begin, T end, boolean inclusive) {
        return new CriterionOperation<>(" between(" + begin + ", " + end + ")",
                nmap -> extract(nmap
                    .tailMap(begin, inclusive)
                    .headMap(end, inclusive)));
    }

    public static <T extends Comparable<T>> CriterionOperation<T>
    between(T begin, T end) {
        return between(begin, end, true);
    }

    public static CriterionOperation<?> ofDay(LocalDate day) {
        return new CriterionOperation<>(" of day " + day, between(
                LocalDateTime.of(day, LocalTime.of(0, 0)),
                LocalDateTime.of(day, LocalTime.of(23, 59)), true)
            .getFunction());
    }
}

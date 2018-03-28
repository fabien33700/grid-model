package org.flatbase.query.criteria;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NavigableMap;
import java.util.stream.Collectors;

public class CriterionFactory {

    private static List<Long> extract(NavigableMap<Object,List<Long>> nmap) {
        return nmap.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public static CriterionOperation greaterOrEqual(Object referenceValue) {
        return new CriterionOperation(" >= " + referenceValue, 
                nmap -> extract(nmap.tailMap(referenceValue, true)));
    }



    public static CriterionOperation greaterThan(Object referenceValue) {
        return new CriterionOperation(" > " + referenceValue,
                nmap -> extract(nmap.tailMap(referenceValue, false)));
    }

    public static CriterionOperation lesserOrEqual(Object referenceValue) {
        return new CriterionOperation(" <= " + referenceValue,
                nmap -> extract(nmap.headMap(referenceValue, true)));
    }

    public static CriterionOperation lesserThan(Object referenceValue) {
        return new CriterionOperation(" < " + referenceValue,
                nmap -> extract(nmap.headMap(referenceValue, false)));
    }

    public static CriterionOperation equalTo(Object referenceValue) {
        return new CriterionOperation(" = " + referenceValue,
                nmap -> extract(nmap
                    .tailMap(referenceValue, true)
                    .headMap(referenceValue, true)));
    }

    public static CriterionOperation between(Object begin, Object end, boolean inclusive) {
        return new CriterionOperation(" between(" + begin + ", " + end + ")",
                nmap -> extract(nmap
                    .tailMap(begin, inclusive)
                    .headMap(end, inclusive)));
    }

    public static CriterionOperation between(Object begin, Object end) {
        return between(begin, end, true);
    }

    public static CriterionOperation ofDay(LocalDate day) {
        return new CriterionOperation(" of day " + day, between(
                LocalDateTime.of(day, LocalTime.of(0, 0)),
                LocalDateTime.of(day, LocalTime.of(23, 59)), true)
            .getFunction());
    }
}

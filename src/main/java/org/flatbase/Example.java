package org.flatbase;

import org.flatbase.core.Flatbase;
import org.flatbase.core.Instance;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.flatbase.query.criteria.CriterionFactory.between;
import static org.flatbase.query.criteria.CriterionFactory.greaterThan;

public class Example {

    /*
     * TODO Commentaire
     * TODO Création dépôt Git + licence + push
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        Instance db = Flatbase.instance()
                .index("time", Long::parseLong, Long::compareTo)
                .index("cpu0", Double::parseDouble, Double::compareTo)
                .index("cpu1", Double::parseDouble, Double::compareTo)
                .enableDebug()
                .inject(Example.class,
                    "/srv-DC-london_global.csv", "/esx-alger-01_global.csv");

        List<Object> results = db
                .query("time", "cpu0")
                .where("time", between(1515020510L, 1515106910L))

                /* more complexs queries
                .and("cpu0", greaterThan(60.0d))
                .exclusiveOr("cpu1", greaterThan(50.0d))*/

                .list();
        //results.forEach(System.out::println);
    }
}

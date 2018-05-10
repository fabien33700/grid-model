package org.flatbase;

import org.flatbase.core.Flatbase;
import org.flatbase.core.Instance;
import org.flatbase.model.Row;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.flatbase.query.criteria.CriterionFactory.between;
import static org.flatbase.query.criteria.CriterionFactory.greaterThan;

public class Example {

    /*
     * TODO Commentaires
     * TODO Passer de Map<String, String> à Map<String, Object> pour la représentation des enregistrements -- OK
     * TODO Création dépôt Git + licence + push
     * TODO Application du pattern Builder et Composite pour la construction de requête -- Abandon
     * TODO Revoir le fonctionnement de l'IndexStructure -- Ok
     * TODO Ajout d'un système type ResultSet, fonction d'adaptation dans cet objet
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        Instance db = Flatbase.instance()
                .index("time", Long::parseLong)
                .index("cpu0", Double::parseDouble)
                .index("cpu1", Double::parseDouble)
                .inject(Example.class,
                    "/srv-DC-london_global.csv", "/esx-alger-01_global.csv");

        List<Row> results = db
                .query("time", "cpu0")
                .where("time", between(1515020510L, 1515106910L))

                /* more complexs queries */
                .and("cpu0", greaterThan(80.0d))
                .exclusiveOr("cpu1", greaterThan(50.0d))
                //.list(row -> new ServerInfo(row.get("time"), row.get("cpu0")))
                .list();

        //results.forEach(System.out::println);
    }
}

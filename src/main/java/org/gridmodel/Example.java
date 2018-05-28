package org.gridmodel;

import org.gridmodel.core.GridModel;
import org.gridmodel.core.Instance;
import org.gridmodel.query.results.ResultStore;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.gridmodel.core.Adapters.asLong;
import static org.gridmodel.query.criteria.CriterionFactory.between;

public class Example {

    public static void main(String[] args) throws IOException, URISyntaxException {

        Instance instance = GridModel.instance()
                .index("time", asLong())
                .custom("server_id", ctx ->
                        ctx.sourceName().replaceAll(".csv", ""))
                .append(Example.class,
                    "/srv-DC-london_global.csv", "/esx-alger-01_global.csv");

        ResultStore r = instance.query()
            .where("time", between(1515020510L, 1515106910L))
            .fetch();

        try (FileWriter fw = new FileWriter("test.csv")) {
            fw.write(r.asCsv());
            fw.flush();
        }
    }
}

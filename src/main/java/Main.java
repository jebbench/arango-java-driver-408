import com.arangodb.ArangoDB;
import com.arangodb.mapping.ArangoJack;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.kotlin.KotlinModule;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class Main {

    static String json;
    static boolean insertFixture = true;

    static {
        try {
            json = new String(Main.class.getResourceAsStream("json.json").readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) throws JsonProcessingException {

        // Object mapper for reading the fixture file
        final ObjectMapper mapper = new ObjectMapper()
                .registerModule(new KotlinModule())
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // Configure Arango
        final ArangoJack arangoJack = new ArangoJack();
        arangoJack.configure(it -> it
            .registerModule(new KotlinModule())
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        );

        final ArangoDB arangoDB = new ArangoDB.Builder().user("root")
                .serializer(arangoJack)
                .build();

        if(insertFixture) {
            final Element fixture = mapper.readValue(json, Element.class);
            if (arangoDB.db("testdb").collection("element").documentExists(fixture.getId())) {
                arangoDB.db("testdb").collection("element").deleteDocument(fixture.getId());
            }
            arangoDB.db("testdb").collection("element").insertDocument(fixture);
        }

        // Query
        final HashMap<String, Object> params = new HashMap<>();
        params.put("jobIds", List.of("23c52a19-a4b1-05b0-e093-4860a0b6336d"));

        final String query = "FOR s IN `element`\n" +
                "FILTER s.jobId IN @jobIds\n" +
                "SORT s.sortWeight ASC\n" +
                "RETURN s";

        Element result = arangoDB.db("testdb").query(
                query,
                params,
                Element.class
        ).first();

        // Results
        final boolean testPasses = ((BigDecimal) result.getAttributes().get("int_attr_0").getValue()).scale() == 0;
        System.out.println("Pass: " + testPasses);
        System.out.println("Actual: " + result.getAttributes().get("int_attr_0").getValue() + " (expected 10 without any decimals)");
        System.out.println(mapper.writeValueAsString(result));


        System.exit(testPasses ? 0 : 1);
    }

}

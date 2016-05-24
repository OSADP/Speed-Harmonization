package gov.dot.fhwa.saxton.speedharm.executive.main;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import gov.dot.fhwa.saxton.speedharm.api.models.AlgorithmManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Root Spring BOOT Rest Controller
 * Displays basic information about the server and it's API.
 */

@RestController
public class Executive {

    @Autowired
    ApplicationContext ctx;

    @Autowired
    AlgorithmManager algorithmManager;

    Logger log = LogManager.getLogger();

    @RequestMapping("/rest")
    public String index() {
        JsonFactory factory = new JsonFactory();
        StringWriter sw = new StringWriter();
        try {
            JsonGenerator generator = factory.createGenerator(sw);
            generator.writeStartObject();
            generator.writeStringField("apiVersion", "v1.0.0");
            generator.writeStringField("applicationName", "STOL Speed Harmonization TO 22/26");

            generator.writeArrayFieldStart("availableAlgorithms");
            for (Class algo: algorithmManager.getAvailableAlgorithms()) {
                generator.writeString(algo.getName());
            }
            generator.writeEndArray();

            generator.writeFieldName("_links");
            generator.writeStartObject();
            generator.writeStringField("vehicles", "/rest/vehicles");
            generator.writeStringField("experiments", "/rest/experiments");
            generator.writeStringField("algorithms", "/rest/algorithms");
            generator.writeStringField("infrastructure", "/rest/infrastructure");
            generator.writeEndObject();
            generator.writeEndObject();
            generator.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("REST API Index requested...");

        return sw.toString();
    }
}

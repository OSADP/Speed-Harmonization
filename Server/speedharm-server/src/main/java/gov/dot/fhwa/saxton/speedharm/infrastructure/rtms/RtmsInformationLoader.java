package gov.dot.fhwa.saxton.speedharm.infrastructure.rtms;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.dot.fhwa.saxton.speedharm.api.objects.Infrastructure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads data about available RTMS units from a file in the local directory or from a file in the resource folder
 */
public class RtmsInformationLoader {
    private Logger log = LogManager.getLogger();
    private List<Infrastructure> rtmsInformation = new ArrayList<>();
    private InputStream input;


    public RtmsInformationLoader(File input) {
        try {
            this.input = new FileInputStream(input);
        } catch (FileNotFoundException e) {
            this.input = null;
        }
    }

    public RtmsInformationLoader(InputStream input) {
        this.input = input;
    }

    public List<Infrastructure> getRtmsInformation() {
        return rtmsInformation;
    }

    public List<Infrastructure> load() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // If we encountered an error reading the file, just return empty
        if (input != null) {
            rtmsInformation = mapper.readValue(input, new TypeReference<List<Infrastructure>>() {});
        } else {
            rtmsInformation = new ArrayList<>();
        }

        log.info("Loaded information about " + rtmsInformation.size() + " RTMS units.");
        return rtmsInformation;
    }
}

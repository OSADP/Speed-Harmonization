package gov.dot.fhwa.saxton.speedharm.executive.main;

import gov.dot.fhwa.saxton.speedharm.api.objects.Infrastructure;
import gov.dot.fhwa.saxton.speedharm.infrastructure.InfrastructureProvider;
import gov.dot.fhwa.saxton.speedharm.infrastructure.rtms.RTMSProvider;
import gov.dot.fhwa.saxton.speedharm.infrastructure.rtms.RtmsInformationLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring configuration file for using real RTMS data.
 */
@Configuration
@Profile("!mockRtms")
public class RtmsConfiguration {

    @Autowired
    private RtmsInformationLoader rtmsInformationLoader;

    private Logger log = LogManager.getLogger();

    @Bean
    public InfrastructureProvider getRTMSProvider() {
        // Initialize all the RTMS units
        // TODO: Figure out a better way to do this, maybe YAML configuration?

        List<Infrastructure> rtmsUnits = new ArrayList<>();
        try {
            rtmsUnits = rtmsInformationLoader.load();
        } catch (IOException e) {
            log.warn("Unable to properly load RTMS data!!!");
        }

        return new RTMSProvider(rtmsUnits);
    }
}

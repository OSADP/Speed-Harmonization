package gov.dot.fhwa.saxton.speedharm.executive.main;

import gov.dot.fhwa.saxton.speedharm.api.objects.Infrastructure;
import gov.dot.fhwa.saxton.speedharm.infrastructure.InfrastructureProvider;
import gov.dot.fhwa.saxton.speedharm.infrastructure.replayrtms.CsvReplayRtmsProvider;
import gov.dot.fhwa.saxton.speedharm.infrastructure.rtms.RtmsInformationLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring config for the replayRtms configuration
 */
@Profile("replayRtms")
@Configuration
public class ReplayRtmsConfiguration {
    private Logger log = LogManager.getLogger();

    @Bean
    public InfrastructureProvider getRTMSProvider(RtmsInformationLoader rtmsInformationLoader) {
        List<Infrastructure> rtmsUnits = new ArrayList<>();
        try {
            rtmsUnits = rtmsInformationLoader.load();
        } catch (IOException e) {
            log.warn("Unable to properly load RTMS data!!!");
        }

        return new CsvReplayRtmsProvider(rtmsUnits);
    }
}

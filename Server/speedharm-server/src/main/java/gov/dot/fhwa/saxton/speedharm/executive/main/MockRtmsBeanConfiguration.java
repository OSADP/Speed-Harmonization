package gov.dot.fhwa.saxton.speedharm.executive.main;

import gov.dot.fhwa.saxton.speedharm.api.objects.Infrastructure;
import gov.dot.fhwa.saxton.speedharm.infrastructure.InfrastructureProvider;
import gov.dot.fhwa.saxton.speedharm.infrastructure.mockrtms.MockRtmsModel;
import gov.dot.fhwa.saxton.speedharm.infrastructure.mockrtms.MockRtmsProvider;
import gov.dot.fhwa.saxton.speedharm.infrastructure.mockrtms.models.RandomRtmsModel;
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
 * Spring configuration file for using mock RTMS data instead of the real data.
 */

@Configuration
@Profile("randomRtms")
public class MockRtmsBeanConfiguration {
    private Logger log = LogManager.getLogger();

    @Autowired
    private RtmsInformationLoader rtmsInformationLoader;

    @Bean
    public InfrastructureProvider getRTMSProvider() {

        MockRtmsModel model = new RandomRtmsModel();

        List<Infrastructure> simRtmsUnits = new ArrayList<>();
        try {
            simRtmsUnits = rtmsInformationLoader.load();
        } catch (IOException e) {
            log.warn("Unable to load simulated RTMS configuration data!!!");
        }

        MockRtmsProvider provider = new MockRtmsProvider(simRtmsUnits, model);
        return provider;
    }
}

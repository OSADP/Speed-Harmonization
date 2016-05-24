package gov.dot.fhwa.saxton.speedharm.executive.main;

import gov.dot.fhwa.saxton.speedharm.api.models.*;
import gov.dot.fhwa.saxton.speedharm.dataprocessing.ClockSkewCompensator;
import gov.dot.fhwa.saxton.speedharm.executive.algorithms.AlgorithmExecutor;
import gov.dot.fhwa.saxton.speedharm.infrastructure.InfrastructureProvider;
import gov.dot.fhwa.saxton.speedharm.infrastructure.rtms.RtmsInformationLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main entrance point and configuration class for the Speed Harmonization Server
 *
 * This class provides the necessary Spring configuration beans for the application to work, then spins up an instance
 * of the context and lets it run. This is the primary entrypoint for execution of the server.
 */

@Configuration
@EnableAutoConfiguration
@ComponentScan("gov.dot.fhwa.saxton.speedharm")
@EntityScan(basePackages = {"gov.dot.fhwa.saxton.speedharm.persistence.entities",
                            "org.springframework.data.jpa.convert.threeten"}) // Bring in the JSR-310 Converters
@EnableJpaRepositories("gov.dot.fhwa.saxton.speedharm.persistence.repositories")
@EnableTransactionManagement
@Import(MockRtmsBeanConfiguration.class)
public class StolInfrastructureServer {

    @Autowired
    InfrastructureProvider rtmsProvider;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("#{'${infrastructure.rtms_ids}'.split(',')}")
    private List<String> rtmsIDs;

    private static final String CONFIG_FILE_NAME = "rtms.json";

    @Autowired
    private ApplicationContext ctx;

    @Bean
    public VehicleManager getVehicleManager() {
        return new VehicleManager();
    }

    @Bean
    public ExperimentManager getExperimentManager() {
        return new ExperimentManager();
    }

    @Bean
    public AlgorithmManager getAlgorithmManager() {
        return new AlgorithmManager();
    }

    @Bean
    public AlgorithmExecutor getAlgorithmExecutor() {
        return new AlgorithmExecutor();
    }

    @Bean
    public InfrastructureManager getInfrastructureManager() {
        List<InfrastructureProvider> infrastructureProviders = new ArrayList<>();

        // Dependencies/enabled providers.
        // TODO: Consider doing this the same way PODE does it, scan for type and then check for enable flag in config
        infrastructureProviders.add(rtmsProvider);

        return new InfrastructureManager(infrastructureProviders);
    }


    @Bean
    public VehicleStatusManager getVehicleStatusManager() {
        return new VehicleStatusManager();
    }

    @Bean
    public ClockSkewCompensator getClockSkewCompensator() {
        return new ClockSkewCompensator();
    }

    @Bean
    public RtmsInformationLoader getRtmsInformationLoader() {
        File config = new File(CONFIG_FILE_NAME);
        if (config.exists()) {
            return new RtmsInformationLoader(config);
        } else {
            return new RtmsInformationLoader(StolInfrastructureServer.class.getResourceAsStream("/" + CONFIG_FILE_NAME));
        }
    }


    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(StolInfrastructureServer.class, args);
    }
}

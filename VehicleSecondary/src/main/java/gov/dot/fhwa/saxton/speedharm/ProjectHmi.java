package gov.dot.fhwa.saxton.speedharm;

import gov.dot.fhwa.saxton.carmasecondary.logger.ILogger;
import gov.dot.fhwa.saxton.carmasecondary.logger.LoggerManager;
import gov.dot.fhwa.saxton.carmasecondary.utils.AppConfig;
import gov.dot.fhwa.saxton.speedharm.config.ProjectApplicationContext;
import gov.dot.fhwa.saxton.speedharm.datamgmt.MainDataMgr;
import gov.dot.fhwa.saxton.speedharm.datamgmt.ServerIncomingMgr;
import gov.dot.fhwa.saxton.speedharm.datamgmt.ServerOutgoingMgr;
import gov.dot.fhwa.saxton.speedharm.services.ProjectService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@ComponentScan(basePackages = {"gov.dot.fhwa.saxton"})
@EnableAutoConfiguration
@SpringBootApplication
public class ProjectHmi {

    //Don't want to make this a bean with an accessor, like items below, because spring boot seems to inherently
    // create a messaging template already, and creating this as a bean confuses it.
    SimpMessagingTemplate template = new SimpMessagingTemplate(new ExecutorSubscribableChannel());

    @Bean
    RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    MainDataMgr getMainDataMgr() { return new MainDataMgr(); }

    @Bean
    ServerOutgoingMgr getOutgointMgr() { return new ServerOutgoingMgr(); }

    @Bean
    ServerIncomingMgr getIncomingMgr() { return new ServerIncomingMgr(); }


    public static void main(String[] args) {

        //get the app's config parameters
        AppConfig config = (AppConfig)AppConfig.getInstance();
        try {
            config.loadFile("speedharm.properties");
        } catch (Exception e) {
            System.err.println("Unable to read the properties file: " + e.toString());
            System.exit(1);
        }

        // set our context for non spring managed classes
        ConfigurableApplicationContext context = SpringApplication.run(ProjectHmi.class, args);
        ProjectApplicationContext.getInstance().setApplicationContext(context);

        //SimulatedDviExecutorService service = context.getBean(SimulatedDviExecutorService.class);
        ProjectService service = context.getBean(ProjectService.class);
        ProjectApplicationContext.getInstance().setService(service);

        DateTime now = new DateTime();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("YYYYMMddHHmmss");
        String logName = fmt.print(now);

        LoggerManager.setOutputFile(config.getProperty("log.path") + logName + ".log");
        LoggerManager.setVersionId(ProjectVersion.getInstance());
        LoggerManager.setRealTimeOutput(config.getBooleanValue("log.stdout"));
        ILogger logger = LoggerManager.getLogger(ProjectHmi.class);

        logger.infof("MAIN", "####### Speed harm server started ########");
        try   {
            LoggerManager.writeToDisk();
        }
        catch(IOException ioe)   {
            System.err.println("Error writing log to disk: " + ioe.getMessage());
        }

        try {
            service.start();
        } catch (Exception e) {
            logger.error("MAIN", "Exception thrown by service object: " + e.toString());
            try {
                LoggerManager.writeToDisk();
            } catch (IOException e1) {
                System.err.println("Error trapped in main: " + e.toString());
                System.err.println("Error writing log to disk: " + e1.getMessage());
            }
            System.exit(1);
        }
    }

}
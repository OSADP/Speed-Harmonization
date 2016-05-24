package gov.dot.fhwa.saxton.speedharm.persistence.converters;

import gov.dot.fhwa.saxton.speedharm.algorithms.IAlgorithm;
import gov.dot.fhwa.saxton.speedharm.api.objects.AlgorithmInformation;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.AlgorithmEntity;

import java.util.Map;

/**
 * Converts between representations of algorithm instances.
 *
 * Used to convert between database ORM classes and REST JSON classes when needed
 * for display to the user or storage in the database.
 */
public class AlgorithmConverter {


    public static AlgorithmInformation databaseToWeb(Map.Entry<Long, IAlgorithm> entry) {
        AlgorithmInformation ai = databaseToWeb(entry.getValue());
        ai.setId(entry.getKey());

        return ai;
    }

    public static AlgorithmInformation databaseToWeb(IAlgorithm ae) {
        AlgorithmInformation out = new AlgorithmInformation();
        out.setClassName(ae.getClass().getName());
        out.setVersionString(ae.getAlgorithmVersion());
        return out;
    }

    public static AlgorithmInformation databaseToWeb(AlgorithmEntity ae) {
        AlgorithmInformation ai = new AlgorithmInformation();
        ai.setClassName(ae.getClassName());
        ai.setStartTime(ae.getStartTime());
        ai.setEndTime(ae.getEndTime());
        ai.setVersionString(ae.getVersionId());
        ai.setId(ae.getId());

        return ai;
    }

    public static AlgorithmEntity webToDatabase(AlgorithmInformation ai) {
        AlgorithmEntity ae = new AlgorithmEntity();
        ae.setClassName(ai.getClassName());
        ae.setEndTime(ai.getEndTime());
        ae.setStartTime(ai.getStartTime());
        ae.setVersionId(ai.getVersionString());
        ae.setId(ai.getId());

        return ae;
    }
}

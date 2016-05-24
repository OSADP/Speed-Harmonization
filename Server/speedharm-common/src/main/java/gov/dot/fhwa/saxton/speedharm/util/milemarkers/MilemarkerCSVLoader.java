package gov.dot.fhwa.saxton.speedharm.util.milemarkers;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads a set of milemarker data from a csv file on disk
 */
public class MilemarkerCSVLoader {

    /**
     * Convert the rows of a CSV table into a list of {@link MilemarkerPoint } for use with {@link MilemarkerConverter}.
     * Improperly formed rows (less than 3 columns or unable to be converted to Double with {@link Double#parseDouble(String)}
     * will simply be excluded from the output set. If no valid rows (or no rows at all) are found, an empty list will be
     * returned.
     *
     * @param input A File object pointing to the CSV file to be loaded
     * @return A list of MilemarkerPoint objects parsed out of the input file
     * @throws IOException In the event an issue occurs reading the file
     */
    public static List<MilemarkerPoint> load(File input) throws IOException {
        List<MilemarkerPoint> points = new ArrayList<>();
        Reader in = new FileReader(input);

        for (CSVRecord record : CSVFormat.DEFAULT.parse(in)) {
            MilemarkerPoint p = convertRow(record);
            if (p != null) {
                points.add(p);
            }
        }

        return points;
    }

    private static MilemarkerPoint convertRow(CSVRecord record) {
        if (record.size() == 3) {
            // We have the right number of entries
            try {
                // Parse each column into a double and return the new object
                MilemarkerPoint p = new MilemarkerPoint(Double.parseDouble(record.get(0)),
                        Double.parseDouble(record.get(1)),
                        Double.parseDouble(record.get(2)));

                return p;
            } catch (Exception e) {
                // Couldn't parse one or more columns, return null to signal improperly formed row
                return null;
            }
        }

        // Invalid number of columns, return null
        return null;
    }
}

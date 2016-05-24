package gov.dot.fhwa.saxton.speedharm;

import gov.dot.fhwa.saxton.carmasecondary.utils.CarmaSecondaryVersion;
import gov.dot.fhwa.saxton.carmasecondary.utils.IComponentVersion;

public class ProjectVersion implements IComponentVersion {

    ///// singleton management

    private ProjectVersion() { }

    private static class ProjectVersionHolder {
        private static final IComponentVersion _instance = new ProjectVersion();
    }

    public static IComponentVersion getInstance()
    {
        return ProjectVersionHolder._instance;
    }

    ///// version info for the Speed Harm project-specific code

    private static final String     name = "Speed Harmonization";
    private static final String     suffix = "";
    private static final String     revisionString = "1.0";
    private static final int        majorRev = 1;
    private static final int        minorRev = 0;

    public int majorRevision() { return majorRev; }

    public int minorRevision() { return minorRev; }

    public String componentName() { return name; }

    public String revisionString() { return revisionString; }

    public String suffix() { return suffix; }

    /**
     * Provides a string that describes the full version info for this component.
     * However, it does not address any dependency component.
     * @return
     */
    @Override
    public String toString() {
        return name + " " + revisionString + suffix;
    }

    public String toStringWithDependencies() {
        String res = toString();
        res += ", including \n    ";
        res += CarmaSecondaryVersion.getInstance().toStringWithDependencies();

        return res;
    }
}

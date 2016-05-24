package gov.dot.fhwa.saxton.speedharm.ui;

/**
 * This class represents a UI message sent to the client via WebSockets.  It is converted to JSON.
 */
public class UiMessage {

    //initialize all members to their default values
    // all the automation states use values:  0=manual, 1=full automation, 2=automated but ignoring speed cmd
    private int         source_ = 1; //1 is the normal loop message
    private int         ownSpeed_ = 0; //mph
    private int         spdCmd_ = 0; //mph
    private int         conf_ = 0; //percent
    private int         ownAuto_ = 0;
    private boolean     ownConnection_ = false;
    private int         numPartners_ = 0;
    private int         v1Auto_ = 0;
    private int         v2Auto_ = 0;
    private int         v3Auto_ = 0;
    private int         v4Auto_ = 0;
    private String      ownName_ = "";
    private String      v1Name_ = "";
    private String      v2Name_ = "";
    private String      v3Name_ = "";
    private String      v4Name_ = "";
    private String      version_ = "";

    public UiMessage() { }

    /**
     * Constructor for internally generated messages
     */
    public UiMessage(String version) {
        source_ = 4; //4 indicates internally generated
        if (version != null) {
            version_ = version;
        }
    }

    //getters

    public int getSource() { return source_; }

    public String getOwnName() { return ownName_; }

    public int getOwnSpeed() { return ownSpeed_; }

    public int getSpdCmd() { return spdCmd_; }

    public int getConf() { return conf_; }

    public int getOwnAuto() { return ownAuto_; }

    public boolean getOwnConnection() { return ownConnection_; }

    public int getNumPartners() {
        numPartners_ = 0;
        if (v1Name_.length() > 0) ++numPartners_;
        if (v2Name_.length() > 0) ++numPartners_;
        if (v3Name_.length() > 0) ++numPartners_;
        if (v4Name_.length() > 0) ++numPartners_;
        return numPartners_;
    }

    public int getOtherStates() {
        //pack the bitflags for other vehicle automation states
        int otherStates =   (v1Auto_       & 0x00000003) | ((v2Auto_ << 2) & 0x0000000c)
                         | ((v3Auto_ << 4) & 0x00000030) | ((v4Auto_ << 6) & 0x000000c0);
        return otherStates;
    }

    public String getV1Name() { return v1Name_; }

    public String getV2Name() { return v2Name_; }

    public String getV3Name() { return v3Name_; }

    public String getV4Name() { return v4Name_; }

    public String getVersion() { return version_; }

    //setters

    public void setOwnName(String name) { ownName_ = name; }

    public void setOwnSpeed(int speed ) { ownSpeed_ = speed; }

    public void setSpdCmd(int cmd) { spdCmd_ = cmd; }

    public void setConf(int conf) { conf_ = conf; }

    public void setOwnAuto(int state) { ownAuto_ = state; }

    public void setOwnConnection(boolean conn) { ownConnection_ = conn; }

    public void setV1Auto(int state) { v1Auto_ = state; }

    public void setV2Auto(int state) { v2Auto_ = state; }

    public void setV3Auto(int state) { v3Auto_ = state; }

    public void setV4Auto(int state) { v4Auto_ = state; }

    public void setV1Name(String name) { v1Name_ = name; }

    public void setV2Name(String name) { v2Name_ = name; }

    public void setV3Name(String name) { v3Name_ = name; }

    public void setV4Name(String name) { v4Name_ = name; }

    @Override
    public String toString() {
        //ensure that the number of partner vehicles has been computed
        getNumPartners();

        //                                      0       1        2       3        4       5
        String msg = String.format("UIMessage [ src=%d, ownN=%s, spd=%d, ownA=%d, cmd=%d, conf=%d, "
        //                             6        7       8       9       10      11       12       13
                                    + "numP=%d, v1n=%s, v2n=%s, v3n=%s, v4n=%s, othS=%d, conn=%b, ver=%s ]",
                                    source_, ownName_, ownSpeed_, ownAuto_, spdCmd_, conf_,
                                    numPartners_, v1Name_, v2Name_, v3Name_, v4Name_, getOtherStates(), ownConnection_, version_);
        return msg;
    }
}

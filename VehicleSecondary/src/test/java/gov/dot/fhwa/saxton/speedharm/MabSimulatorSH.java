package gov.dot.fhwa.saxton.speedharm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Arrays;

/**
 * Purpose:  to simulate both incoming and outgoing UDP traffic in the MAB Speed Harm code as it talks to the secondary processor.
 */
public class MabSimulatorSH {

    //////////////////////////////////////////////////////////
    //Inner class to handle the incoming messages from the HMI

    public class InputThread implements Runnable {

        private DatagramSocket  socket_;
        private boolean         done_ = false;


        public void setup(DatagramSocket socket) {
            socket_ = socket;
            new Thread(this).start();
        }

        @Override
        public void run() {

            //loop until the shutdown signal comes
            while (!done_) {
                byte[] buf = new byte[32];
                DatagramPacket packet = new DatagramPacket(buf, 32);

                try {
                    socket_.receive(packet);
                    int bytesRead = packet.getLength();
                    if (bytesRead > 0) {
                        byte[] message = new byte[bytesRead];
                        message = Arrays.copyOf(packet.getData(), bytesRead);
                        displayMessage(message);
                    }
                } catch (IOException e) {
                    //move on; we don't expect messages to arrive very often
                }

                try {
                    Thread.sleep(10);
                }catch (Exception e) {
                }
            }
        }

        private void displayMessage(byte[] m) {
            System.out.print("Message received from HMI: ");
            for (int i = 0;  i < m.length;  ++i) {
                String out;
                out = String.format("%02x ", m[i]);
                System.out.print(out);
            }
            System.out.println(" ");
        }

        public void shutdown() {
            done_ = true;
        }
    }


    ////////////////////////////////////////////////////////
    //Inner class to handle the outgoing messages to the HMI

    public class OutputThread implements Runnable {

        private DatagramSocket      socket_;
        private InetSocketAddress   iAddr_;
        private MabSimulatorSH parent_;

        public class StatusMsg {
            byte[] content_ = new byte[20];

            public StatusMsg() {
                for (int i = 0;  i < 20;  ++i) { content_[i] = 0; }
            }

            public StatusMsg(int id, double dist, double relSpd, double ownSpd, double ownAccel, double lat, double lon,
                      double heading, int auto) {

                content_[0] = (byte)id;

                int d = (int)(10.0*dist);
                insert2Bytes(content_, 2, d);

                int r = (int)(100.0*relSpd);
                insert2Bytes(content_, 4, r);

                setOwnSpeed(ownSpd);

                int a = (int)(1000.0*ownAccel);
                insert2Bytes(content_, 8, a);

                int latint = (int)(10000000.0*lat);
                insert4Bytes(content_, 10, latint);

                int lonint = (int)(10000000.0*lon);
                insert4Bytes(content_, 14, lonint);

                int h = (int)(10.0*heading);
                insert2Bytes(content_, 18, h);

                setAutomationState(auto);
            }

            public byte[] getContent() {
                return content_;
            }

            public void setOwnSpeed(double val) {
                int s = (int)(100.0*val);
                insert2Bytes(content_, 6, s);
            }

            public void setAutomationState(int state) {
                content_[1] = (byte)state;
            }

            private void insert2Bytes(byte[] array, int start, int word) {
                array[start] = (byte)(word & 0x000000ff);
                array[start+1] = (byte)((word >> 8) & 0x000000ff);
            }

            private void insert4Bytes(byte[] array, int start, int word) {
                array[start] = (byte)(word & 0x000000ff);
                array[start+1] = (byte)((word >> 8) & 0x000000ff);
                array[start+2] = (byte)((word >> 16) & 0x000000ff);
                array[start+3] = (byte)((word >> 24) & 0x000000ff);
            }
        }

        private StatusMsg[] sequence = {
                //            ID  dist  relspd ownSpd  ownAccel  lat         lon         heading  ownAuto
                new StatusMsg(99, 41.2,  0.45, 20.187,  0.02408, 36.2456671, -77.700123, -178.35, 1),
                new StatusMsg(99, 41.2,  0.41, 20.166,  0.02401, 36.2456631, -77.700126, -178.34, 1),
                new StatusMsg(96, 41.2,  0.29, 20.031,  0.022,   36.2456600, -77.700128, -178.33, 2),
                new StatusMsg(98, 41.2,  0.20, 19.989,  0.34890, 36.2456585, -77.700131, -178.00, 2),
                new StatusMsg(0, 41.2,  0.11, 19.589,  0.01818, 36.2456544, -77.700134, -177.78, 2),
                new StatusMsg(97, 41.2,  0.02, 19.007,  0.00032, 36.2456502, -77.700138, -177.12, 0),
                new StatusMsg(99, 41.2, -0.03, 18.385,  0.02408, 36.2456469, -77.700140, -179.90, 0),
                new StatusMsg(99, 41.2, -0.14, 18.4,   -0.00044, 36.2456439, -77.700141,  180.0,  0),
                new StatusMsg(99, 41.2, -0.27, 13.374, -0.64857, 36.2456405, -77.700143,  178.33, 1),
                new StatusMsg(99, 41.2, -0.28,  9.740, -1.99452, 36.2456380, -77.700147,  174.55, 1)
        };

        public void setup(DatagramSocket socket, InetSocketAddress addr, MabSimulatorSH parent) {
            socket_ = socket;
            parent_ = parent;
            iAddr_ = addr;

            new Thread(this).start();
        }

        @Override
        public void run() {

            //set up for manual user control of the output data
            byte[] buf = null;

            //loop until exit command
            int cmd = 0;
            do {
                //this is the nominal message
                //                            ID  dist  relspd ownSpd  ownAccel  lat         lon         heading  ownAuto
                StatusMsg msg = new StatusMsg(98, 41.2,  0.45, 20.187,  0.02408, 36.2456671, -77.700123, -178.35, 1);
                boolean rapid = false;

                cmd = getMainMenuSelection();

                //execute the user's command
                switch (cmd) {
                    case 0:
                        buf = null;
                        break;

                    case 1: //nominal
                        buf = msg.getContent();
                        break;

                    case 2: //automation off
                        msg.setAutomationState(0);
                        buf = msg.getContent();
                        break;

                    case 3: //automation ignoring
                        msg.setAutomationState(2);
                        buf = msg.getContent();
                        break;

                    case 4: //low speed
                        msg.setOwnSpeed(2.8);
                        buf = msg.getContent();
                        break;

                    case 5: //high speed
                        msg.setOwnSpeed(31.113);
                        buf = msg.getContent();
                        break;

                    case 6: //off-nominal
                        //                               ID  dist  relspd ownSpd ownAccel  lat         lon         heading  ownAuto
                        StatusMsg offNom = new StatusMsg(0, 6000.0, -92.3, 0.1,   2.36,    35.99283, -78.2839501,  14.5,     1);
                        buf = offNom.getContent();
                        break;

                    case 9: //rapid-fire a pile of messages
                        rapid = true;
                        break;

                    default:
                        break;
                }

                //broadcast it on the specified UDP port
                try {
                    //if we're sending a single message then
                    if (buf != null  &&  !rapid) {
                        int length = buf.length;
                        DatagramPacket packet = new DatagramPacket(buf, length, iAddr_);
                        socket_.send(packet);

                    }else if (rapid) {
                        fireRapidSequence();
                    }
                } catch (IOException e) {
                    System.out.println("IO Exception sending message: " + e.getMessage());
                    e.printStackTrace();
                    socket_.close();
                    return;
                } catch (Exception ee) {
                    System.out.println("Exception sending message: " + ee.getMessage());
                    ee.printStackTrace();
                    socket_.close();
                    return;
                }

            } while(cmd != 0);

            System.out.println("Shutting down.");
            socket_.close();
            parent_.shutdown();
        }

        private int getMainMenuSelection() {

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            int cmd;
            String input = "";

            //display the menu of commands available
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                //don't worry about it
            }
            System.out.println(" ");
            System.out.println("Select an action:");
            System.out.println("0) Exit");
            System.out.println("1) Send nominal status          ");
            System.out.println("2) Send own automation off      ");
            System.out.println("3) Send own automation ignoring ");
            System.out.println("4) Send low speed               ");
            System.out.println("5) Send high speed              ");
            System.out.println("6) All params off-nominal       ");
            System.out.println("9) Rapid sequence");

            //wait for a command
            try {
                input = in.readLine();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            cmd = Integer.valueOf(input);
            return cmd;
        }

        private void fireRapidSequence() {
            int i = 0;

            try{
                for (i = 0;  i < sequence.length;  ++i) {
                    DatagramPacket p = new DatagramPacket(sequence[i].getContent(), 20, iAddr_);
                    socket_.send(p);
                    Thread.sleep(130);
                }
            } catch (Exception ee) {
                System.out.println("Exception in rapid-fire message: " + ee.getMessage());
                //ee.printStackTrace();
                socket_.close();
                return;
            }
        }
    }


    //////////////////////////////////////////////////////
    //main program

    private InputThread             input_ = null;
    private OutputThread            output_ = null;
    private DatagramSocket          socket_ = null;
    private InetSocketAddress       iAddr_ = null;

    public static void main(String[] args) {
        String host;
        if (args.length > 0){
            host = args[0];
        }else {
            host = "localhost";
        }

        int port = 5002;
        if (args.length > 1) {
            port = Integer.valueOf(args[1]);
        }

        new MabSimulatorSH().doit(host, port);
    }

    public void doit(String host, int port) {
        socket_ = startup(host, port);
        try {
            socket_.setSoTimeout(20);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        input_ = new InputThread();
        input_.setup(socket_);

        output_ = new OutputThread();
        output_.setup(socket_, iAddr_, this);
    }

    private DatagramSocket startup(String host, int port) {
        //set up the network environment
        InetAddress addr = null;
        try {
            if (host == "localhost"){
                addr = InetAddress.getLocalHost();
            }else {
                addr = InetAddress.getByName(host);
            }
        } catch (UnknownHostException e) {
            System.out.println("Host exception in startup: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(port);
            socket.setSoTimeout(10);
            iAddr_ = new InetSocketAddress(host, port);
        } catch (SocketException e1) {
            System.out.println("Datagram exception in network startup: " + e1.getMessage());
            e1.printStackTrace();
            return null;
        }
        System.out.println("Simulated MAB ready for messages on " + host + ":" + port);

        return socket;
    }

    private void shutdown() {
        input_.shutdown();
        socket_.close();
    }
}

https://tutorcs.com
WeChat: cstutorcs
QQ: 749389476
Email: tutorcs@163.com
/**
    Sample code for Receiver
    Python 3
    Usage: 
        - You need to compile it first: javac Sender.java
        - then run it: java Sender 9000 10000 FileToReceived.txt 1000 1
    coding: utf-8

    Notes:
        Try to run the server first with the command:
            java Receiver 10000 9000 FileReceived.txt 1 1
        Then run the sender:
            java Sender 9000 10000 FileToReceived.txt 1000 1

 */

import java.net.*;
import java.io.*;
import java.util.logging.*;

public class Sender {
    /** 
        The Sender will be able to connect the Receiver via UDP
        :param sender_port: the UDP port number to be used by the sender to send PTP segments to the receiver
        :param receiver_port: the UDP port number on which receiver is expecting to receive PTP segments from the sender
        :param filename: the name of the text file that must be transferred from sender to receiver using your reliable transport protocol.
        :param max_win: the maximum window size in bytes for the sender window.
        :param rot: the value of the retransmission timer in milliseconds. This should be an unsigned integer.
    */

    private final int senderPort;
    private final int receiverPort;
    private final InetAddress senderAddress;
    private final InetAddress receiverAddress;
    private final DatagramSocket senderSocket;
    private final String filename;
    private final int maxWin;
    private final int rto;

    private final int BUFFERSIZE = 1024;

    public Sender(int senderPort, int receiverPort, String filename, int maxWin, int rto) throws IOException {
        this.senderPort = senderPort;
        this.receiverPort = receiverPort;
        this.senderAddress = InetAddress.getByName("127.0.0.1");
        this.receiverAddress = InetAddress.getByName("127.0.0.1");
        this.filename = filename;
        this.maxWin = maxWin;
        this.rto = rto;

        // init the UDP socket
        Logger.getLogger(Sender.class.getName()).log(Level.INFO, "The sender is using the address {0}:{1}", new Object[] { senderAddress, senderPort });
        this.senderSocket = new DatagramSocket(senderPort, senderAddress);

        // start the listening sub-thread
        Thread listenThread = new Thread(this::listen);
        listenThread.start();

        // todo add codes here
    }

    public void ptpOpen() throws IOException {
        // todo add/modify codes here
        // send a greeting message to receiver
        String message = "Greetings! COMP3331.";
        byte[] sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receiverAddress, receiverPort);
        senderSocket.send(sendPacket);
    }

    public void ptpSend() throws IOException {
        // todo add codes here
    }

    public void ptpClose() {
        // todo add codes here
        senderSocket.close();
    }

    public void listen() {
        byte[] receiveData = new byte[BUFFERSIZE];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            // listen to incoming packets from receiver
            while (true) {
                senderSocket.receive(receivePacket);
                String incomingMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                Logger.getLogger(Sender.class.getName()).log(Level.INFO, "received reply from receiver: {0}", incomingMessage);
            }
        } catch (IOException e) {
            // error while listening, stop the thread
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, "Error while listening", e);
        }
    }

    public void run() throws IOException {
        // todo add/modify codes here
        ptpOpen();
        ptpSend();
        // ptpClose();
    }

    public static void main(String[] args) throws IOException {
        Logger.getLogger(Sender.class.getName()).setLevel(Level.ALL);

        if (args.length != 5) {
            System.err.println("\n===== Error usage, java Sender senderPort receiverPort FileReceived.txt maxWin rto ======\n");
            System.exit(0);
        }

        Sender sender = new Sender(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]));
        sender.run();
    }
}


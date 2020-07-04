package chat.client;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client implements Runnable {
    private Socket socket = null;
    private Thread thread = null;
    private ObjectInputStream console = null;
    private Scanner scanner = new Scanner(System.in);
    private ObjectOutputStream streamOut = null;
    private ChatClientThread client = null;

    public Client(String serverName, int serverPort) { // System.out.println("Establishing connection. Please wait ...");
        try {
            socket = new Socket(serverName, serverPort);
            //  System.out.println("Connected: " + socket);
            start();
        } catch (UnknownHostException uhe) {
            System.out.println("Host unknown: " + uhe.getMessage());
        } catch (IOException ioe) {
            ioe.getStackTrace();
        }
    }
    public void run() {
        try {
            String input;
            while(!(input = console.readUTF()).equals("/exit")) {
                streamOut.writeUTF(input);
                streamOut.flush();
            }
        } catch (Exception e) {

        }
        while (thread != null) {
            try {
                    streamOut.writeUTF(console.readUTF());
                    streamOut.flush();
            } catch (Exception ixe) {
                ixe.getStackTrace();
                stop();
            }
        }
    }
    public void handle(String msg) {
        if (msg.equals("/exit")) { //System.out.println("Good bye. Press RETURN to exit ...");
            stop();
        } else
            System.out.println(msg);
    }
    public void start() throws IOException {
        System.out.println("Client started!");
        System.out.println("Server: authorize or register");
        console = new ObjectInputStream(System.in);
        streamOut = new ObjectOutputStream(socket.getOutputStream());

        if (thread == null) {
            client = new ChatClientThread(this, socket);
            thread = new Thread(this);
            thread.start();
        }
    }
    public void stop() {
        client.close();

    }
    public static void main(String args[]) {
        Client client = null;

        client = new Client("localhost", 4998);
    }
}
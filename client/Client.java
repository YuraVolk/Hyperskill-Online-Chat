package chat.client;

import chat.server.Connection;

import java.io.*;
import java.net.*;
import java.util.List;

public class Client implements Runnable {

    public static void main(String[] args) {

        String host;
        if (args.length > 0)
            host = args[0];
        else
            host = "localhost";

        int port;
        if (args.length > 1)
            port = Integer.parseInt(args[1]);
        else
            port = 4444;

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            Thread input = new Thread(() -> {
                String msg;
                try {
                    while ((msg = in.readLine()) != null) {
                        System.out.println(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            input.start();

            String userName = "User" + ((int) (Math.random() * 200));

            String msg;
            try {
                while ((msg = stdIn.readLine()) != null) {
                    for (int i = 0; i < msg.length(); i++)
                        System.out.print("\b");
                    out.write(userName + ": " + msg + "\n");
                    out.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static final long IDLE_TIME = 10;

    private Connection connection;
    private boolean     alive;
    private Thread      t;

    private List<Client> clientList;

    public Client(Connection connection, List<Client> clientList) {
        this.connection = connection;
        this.clientList = clientList;
        alive = false;
    }

    public synchronized void startSession() {

        if (alive)
            return;

        alive = true;

        t = new Thread(this);
        t.start();

    }

    public synchronized void closeSession() {

        if (!alive)
            return;

        alive = false;

        try {
            connection.close();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        while (connection.isAlive()) {

            String in = connection.read();
            if (in != null) {
                System.out.println(in);
                for (Client c : clientList) {
                    c.send(in);
                }
            } else {
                try {
                    Thread.sleep(IDLE_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public void send(String msg) {

        connection.write(msg + "\n");
        connection.flush();
    }

}
package chat.server;

import chat.Database;
import chat.Message;
import chat.MessageList;
import javafx.util.Pair;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;
import java.util.*;

import java.net.ServerSocket;

/*
 * A chat server that delivers public and private messages and files.
 */
public class Server {

    // The server socket.
    private static ServerSocket serverSocket = null;
    // The client socket.
    private static Socket clientSocket = null;
    public static Database database = new Database();
    public static List<Pair<String, String>> messages = new ArrayList<>();
    public static ArrayList<clientThread> clients = new ArrayList<clientThread>();


    public static void main(String args[]) throws IOException {

        // The default port number.
        int portNumber = 1234;
        Database base;

        try {
            FileInputStream fileIn = new FileInputStream("db.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            base = (Database) in.readObject();
            in.close();
            fileIn.close();
            database = base;
        } catch (Exception i) {

        }



        if (args.length < 1)
        {

            //System.out.println("No port specified by user.\nServer is running using default port number=" + portNumber);

        }
        else
        {
            portNumber = Integer.valueOf(args[0]).intValue();

            //System.out.println("Server is running using specified port number=" + portNumber);
        }

        /*
         * Open a server socket on the portNumber (default 1234).
         */
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
        //    System.out.println("Server Socket cannot be created");
        }

        /*
         * Create a client socket for each connection and pass it to a new client
         * thread.
         */

        database.addUserChat("admin", "12345678");

        int clientNum = 1;
        while (true) {
            try {

                clientSocket = serverSocket.accept();
                clientThread curr_client =  new clientThread(clientSocket, clients);
                clients.add(curr_client);
                curr_client.start();
                //  System.out.println("Client "  + clientNum + " is connected!");
                clientNum++;

            } catch (IOException e) {

             //   System.out.println("Client could not be connected");
            }


        }

    }
}

/*
 * This client thread class handles individual clients in their respective threads
 * by opening a separate input and output streams.
 */
class clientThread extends Thread implements Comparable<clientThread> {

    private String clientName = null;
    private ObjectInputStream is = null;
    private ObjectOutputStream os = null;
    private Socket clientSocket = null;
    private final ArrayList<clientThread> clients;
    private boolean close = false;
    boolean authorized = false;
    private int receptientId = -1;
    private int thisId = -1;
    String chateeName = "";

    public clientThread(Socket clientSocket, ArrayList<clientThread> clients) {

        this.clientSocket = clientSocket;
        this.clients = clients;

    }


    public void stopProcess() {
        close = true;
    }



    public void run() {

        ArrayList<clientThread> clients = this.clients;

        try {
            /*
             * Create input and output streams for this client.
             */
            is = new ObjectInputStream(clientSocket.getInputStream());
            os = new ObjectOutputStream(clientSocket.getOutputStream());

            String name = "" + Math.random();
            boolean previousAnotherName = false;
         /*   while (true) {

                synchronized(this)
                {
                    if (!previousAnotherName) {
                        this.os.writeObject("Server: write your name");
                        this.os.flush();
                    }

                    name = ((String) this.is.readObject()).trim();

                    boolean usernameExists = false;

                    for (clientThread curr_client : clients)
                    {
                        if (curr_client.clientName != null) {

                            if (curr_client.clientName.substring(1).equals(name)) {
                                usernameExists = true;
                            }
                        }
                    }

                    if (!usernameExists) {
                        break;
                    } else {
                        previousAnotherName = true;
                        this.os.writeObject("Server: This name is already in use! Choose another one");
                        this.os.flush();
                    }
                }
            }*/

            /* Welcome the new the client. */
         //   Server.database.addUserChat(name, "password");
        //    System.out.println(Server.database.getCorrespondenceMessages());
            //System.out.println("Client Name is " + name);

                /*this.os.writeObject("*** Welcome " + name + " to our chat room ***\nEnter /quit to leave the chat room");
                this.os.flush();*/

                /*this.os.writeObject("Directory Created");
                this.os.flush();*/
           /* synchronized(this)
            {

                for (clientThread curr_client : clients)
                {
                    if (curr_client != null && curr_client == this) {
                        clientName = "@" + name;
                        break;
                    }
                }

            /*  for (clientThread curr_client : clients) {
                    if (curr_client != null && curr_client != this) {
                        curr_client.os.writeObject(name + " has joined");
                        curr_client.os.flush();

                    }

                }
            }*/

            /* Start the conversation. */

            while (true) {

            /*  this.os.writeObject("");
                this.os.flush();*/
               /* System.out.println(Server.database.getCorrespondenceMessages());
                System.out.println(Server.database.getUsers());*/
                String line = (String) is.readObject();


                if (line.startsWith("/exit")) {
                    authorized = false;
                    break;
                } else if (line.startsWith("/registration")) {
                    String[] words = line.split(" ");
                    if (Server.database.usernameExists(words[1])) {
                        this.os.writeObject("Server: this login is already in use!");
                        this.os.flush();
                    } else {
                        if (words[2].length() < 8) {
                            this.os.writeObject("Server: the password is too short!");
                            this.os.flush();
                        } else {
                            Server.database.addUserChat(words[1], words[2]);
                            this.os.writeObject("Server: you are registered successfully!");
                            this.os.flush();

                            //AUTH
                            authorized = true;
                            name = words[1];
                            clientName = words[1];
                            thisId = Server.database.getUserPosition(clientName);
                        }
                    }
                } else if (line.startsWith("/auth")) {
                    String[] words = line.split(" ");


                    if (Server.database.usernameExists(words[1])) {
                        if (Server.database.getPassword(words[1]).equals(words[2])) {
                            authorized = true;
                            this.os.writeObject("Server: you are authorized successfully!");
                            name = words[1];
                            clientName = words[1];
                            thisId = Server.database.getUserPosition(clientName);
                        } else {
                            this.os.writeObject("Server: incorrect password!");
                        }
                        this.os.flush();
                    } else {
                        this.os.writeObject("Server: incorrect login!");
                        this.os.flush();
                    }
                } else if (line.startsWith("/chat")) {
                    if (!authorized) {
                        this.os.writeObject("Server: you are not in the chat!");
                        this.os.flush();
                    } else {

                        String[] words = line.split(" ");
                        int addresseeId = Server.database.getUserPosition(words[1]);

                        if (addresseeId == -1) {
                            this.os.writeObject("Server: the user is not online!");
                            this.os.flush();
                        } else {
                            receptientId = addresseeId;
                            chateeName = words[1];
                            MessageList messageList = Server.database.getMessages(thisId, receptientId);
                         //   System.out.println(messageList);
                            for (MessageList.Message messageObject : messageList.getMessagesList()) {
                                this.os.writeObject(messageObject.getMessage());
                                this.os.flush();
                            }
                        }
                    }
                } else if (line.startsWith("/list")) {
                    List<clientThread> onlineUsers = new ArrayList<>();
                    for (clientThread thread : clients) {
                        if (thread.authorized && thread != this) {
                            onlineUsers.add(thread);
                        }
                    }

                    Collections.sort(onlineUsers);

                    if (onlineUsers.size() == 0) {
                        this.os.writeObject("Server: no one online");
                    } else {
                        StringBuilder builder = new StringBuilder();
                        builder.append("Server: online:");
                        for (clientThread user : onlineUsers) {
                            builder.append(" ");
                            builder.append(user.clientName);
                        }
                        this.os.writeObject(builder.toString());
                    }
                    this.os.flush();

                } else if (line.startsWith("/")) {
                    this.os.writeObject("Server: incorrect command!");
                    this.os.flush();
                } else {
                    if (!authorized) {
                        this.os.writeObject("Server: you are not in the chat!");
                    } else {
                        if (receptientId == -1) {
                            this.os.writeObject("Server: use /list command to choose an user to text!");
                        } else {
                            unicast(line, chateeName, receptientId, thisId);
                            this.os.writeObject("" + name + ": " + line);
                        }

                     //   System.out.println(receptientId);
                    }
                    this.os.flush();

                }

            }

            /* Terminate the Session for a particluar user */

            this.os.writeObject("/stop");
            this.os.flush();
            //System.out.println(name + " disconnected.");
            clients.remove(this);


            synchronized(this) {

                if (!clients.isEmpty()) {

                    for (clientThread curr_client : clients) {


                    /*  if (curr_client != null && curr_client != this && curr_client.clientName != null) {
                            curr_client.os.writeObject("*** The user " + name + " disconnected ***");
                            curr_client.os.flush();
                        }*/




                    }
                }
            }


            this.is.close();
            this.os.close();
            clientSocket.close();

        } catch (IOException e) {

            //  System.out.println("User Session terminated");

        } catch (ClassNotFoundException e) {

            //  System.out.println("Class Not Found");
        }
    }



    /**** This function transfers message or files to all the client except a particular client connected to the server ***/

    void blockcast(String line, String name) throws IOException, ClassNotFoundException {

        String[] words = line.split(":", 2);

        /* Transferring a File to all the clients except a particular client */


        if (words.length > 1 && words[1] != null) {
            words[1] = words[1].trim();
            if (!words[1].isEmpty()) {
                synchronized (this){
                    for (clientThread curr_client : clients) {
                        if (curr_client != null && curr_client != this && curr_client.clientName != null
                                && !curr_client.clientName.equals("@"+words[0].substring(1))) {
                            curr_client.os.writeObject("<" + name + "> " + words[1]);
                            curr_client.os.flush();


                        }
                    }
                    /* Echo this message to let the user know the blocked message was sent.*/

                    this.os.writeObject(">>Blockcast message sent to everyone except "+words[0].substring(1));
                    this.os.flush();
                    //  System.out.println("Message sent by "+ this.clientName.substring(1) + " to everyone except " + words[0].substring(1));
                }
            }
        }

    }

    /**** This function transfers message or files to all the client connected to the server ***/

    void broadcast(String line, String name, boolean writeHistory) throws IOException, ClassNotFoundException {

        if (writeHistory) {
            Server.messages.add(new Pair<>(name, line));
        }
        /* Transferring a File to all the clients */

        if (line.split("\\s")[0].toLowerCase().equals("sendfile"))
        {

            byte[] file_data = (byte[]) is.readObject();
            synchronized(this){
                for (clientThread curr_client : clients) {
                    if (curr_client != null && curr_client.clientName != null)
                    {
                        curr_client.os.writeObject("Sending_File:"+line.split("\\s",2)[1].substring(line.split("\\s",2)[1].lastIndexOf(File.separator)+1));
                        curr_client.os.writeObject(file_data);
                        curr_client.os.flush();

                    }
                }

                this.os.writeObject("Broadcast file sent successfully");
                this.os.flush();
                //System.out.println("Broadcast file sent by " + this.clientName.substring(1));
            }
        }

        else
        {
            /* Transferring a message to all the clients */

            synchronized(this){

                for (clientThread curr_client : clients) {

                    if (curr_client != null && curr_client.clientName != null)
                    {

                        curr_client.os.writeObject("" + name + ": " + line);
                        curr_client.os.flush();

                    }
                }

                //  this.os.writeObject("Broadcast message sent successfully.");
                this.os.flush();
                //  System.out.println("Broadcast message sent by " + this.clientName.substring(1));
            }

        }

    }

    /**** This function transfers message or files to a particular client connected to the server ***/

    void unicast(String line, String name, int receptientId, int thisClientId) throws IOException, ClassNotFoundException {



        /* Transferring File to a particular client */



        for (clientThread curr_client : clients) {

            if (curr_client != null && curr_client.clientName != null
                    && curr_client.clientName.equals(name)) {
                if (curr_client.chateeName.equals(this.clientName)) {
                    curr_client.os.writeObject("" + this.clientName + ": " + line);
                    curr_client.os.flush();
                }

                Server.database.addMessage(this.clientName, line, thisClientId, receptientId);
                        /*  System.out.println(this.clientName.substring(1) + " transferred a private message to client "+ curr_client.clientName.substring(1));

                            /* Echo this message to let the sender know the private message was sent.

                            this.os.writeObject("Private Message sent to " + curr_client.clientName.substring(1));*/
                this.os.flush();
                break;
            }


        }
    }


    @Override
    public int compareTo(clientThread clientThread) {
        return this.clientName.compareTo(clientThread.clientName);
    }
}
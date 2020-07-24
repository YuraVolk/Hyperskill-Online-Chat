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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            database.addUserChat("admin", "12345678");
            database.addAdministrator("admin");
        }



        if (args.length < 1)
        {

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
                System.out.println(Server.database);
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
                            if (Server.database.isBanned(words[1])) {
                                this.os.writeObject("Server: you are banned!");
                            } else {
                                authorized = true;
                                this.os.writeObject("Server: you are authorized successfully!");
                                name = words[1];
                                clientName = words[1];
                                thisId = Server.database.getUserPosition(clientName);
                            }

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
                           /* List<MessageList.Message> lastMessages = messageList.getMessagesList().subList(
                                    Math.max(messageList.getMessagesList().size() - 10, 0),
                                    messageList.getMessagesList().size());*/

                           List<MessageList.Message> messages = new ArrayList<>();
                           List<MessageList.Message> unreadMessages = new ArrayList<>();

                            for (MessageList.Message messageObject : messageList.getMessagesList()) {
                                if (messageObject.isUnread()) {
                                    unreadMessages.add(messageObject);
                                } else {
                                    messages.add(messageObject);
                                }
                            /*   this.os.writeObject(messageObject.getMessage());
                                this.os.flush();*/
                            }

                            List<MessageList.Message> finalMessages = Stream.concat(messages.stream(),
                                    unreadMessages.stream())
                                    .collect(Collectors.toList());

                            finalMessages = finalMessages.subList(Math.max(finalMessages.size() - 25, 0),
                                    finalMessages.size());

                            System.out.println(messages);
                            System.out.println(unreadMessages);
                            System.out.println(finalMessages);

                            for (MessageList.Message message : finalMessages) {
                                this.os.writeObject(message.getMessage());
                                message.setRead();
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

                } else if (line.startsWith("/kick")) {
                    if (!authorized) {
                        this.os.writeObject("Server: you are not in the chat!");
                    } else {
                        String[] words = line.split(" ", 2);
                        if (Server.database.getUserStatus(this.clientName).equals("user")) {
                            this.os.writeObject("Server: you are not a moderator or an admin!");
                        } else {
                            if (words[1].equals(this.clientName)) {
                                this.os.writeObject("Server: you can’t kick yourself!");
                                this.os.flush();
                            } else {
                                this.os.writeObject(String.format("Server: %s was kicked!", words[1]));
                                for (clientThread thread : clients) {
                                    if (thread.clientName.equals(words[1])) {
                                        thread.authorized = false;
                                        thread.os.writeObject("Server: you have been kicked out of the server!");
                                    }
                                }
                                Server.database.ban(words[1]);
                            }

                        }
                    }
                    this.os.flush();
                } else if (line.startsWith("/grant")) {
                    if (!authorized) {
                        this.os.writeObject("Server: you are not in the chat!");
                    } else {
                        String[] words = line.split(" ", 2);
                        System.out.println(Server.database.getUserStatus(this.clientName));
                        if (!Server.database.getUserStatus(this.clientName).equals("admin")) {
                            this.os.writeObject("Server: you are not an admin!");
                        } else {
                            if (Server.database.getUserStatus(words[1]).equals("moderator")) {
                                this.os.writeObject("Server: this user is already a moderator!");
                            } else {
                                this.os.writeObject(String.format("Server: %s as the new moderator!", words[1]));
                                for (clientThread thread : clients) {
                                    if (thread.clientName.equals(words[1])) {
                                        thread.os.writeObject("Server: you are the new moderator now!");
                                    }
                                }
                                Server.database.addModerator(words[1]);
                            }


                        }
                    }
                    this.os.flush();
                } else if (line.startsWith("/revoke")) {
                    if (!authorized) {
                        this.os.writeObject("Server: you are not in the chat!");
                    } else {
                        String[] words = line.split(" ", 2);
                        System.out.println(Server.database.getUserStatus(this.clientName));
                        if (!Server.database.getUserStatus(this.clientName).equals("admin")) {
                            this.os.writeObject("Server: you are not an admin!");
                        } else {
                            if (!Server.database.getUserStatus(words[1]).equals("moderator")) {
                                this.os.writeObject("Server: this user is not a moderator!");
                            } else {
                                this.os.writeObject(String.format("Server: %s is no longer moderator!", words[1]));
                                for (clientThread thread : clients) {
                                    if (thread.clientName.equals(words[1])) {
                                        thread.os.writeObject("Server: you are no longer a moderator!");
                                    }
                                }
                                Server.database.removeModerator(words[1]);
                            }

                        }
                    }
                    this.os.flush();
                } else if (line.startsWith("/history")) {
                    if (!authorized) {
                        this.os.writeObject("Server: you are not in the chat!");
                    } else {
                        String[] words = line.split(" ", 2);

                        if (!words[1].matches("-?\\d+(.\\d+)?")) {
                            this.os.writeObject(String.format("Server: %s is not a number!", words[1]));
                            this.os.flush();
                        } else {
                            int number = Integer.parseInt(words[1]);

                            if (number > 25) {
                                number = 25;
                            }

                            MessageList messageList = Server.database.getMessages(thisId, receptientId);

                            if (number > messageList.getMessagesList().size()) {
                                number = messageList.getMessagesList().size();
                            }

                            List<MessageList.Message> lastMessages = messageList.getMessagesList().subList(
                                    Math.max(messageList.getMessagesList().size() - number, 0),
                                    messageList.getMessagesList().size());

                            for (MessageList.Message messageObject : lastMessages) {
                                this.os.writeObject(messageObject.getMessage());
                                this.os.flush();
                            }
                        }

                    }
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

    private boolean isUserOnline(String name) {
        for (clientThread thread : clients) {
            if (thread.authorized && thread.clientName.equals(name) && thread.chateeName.equals(this.clientName)) {
                return true;
            }
        }

        return false;
    }

    void unicast(String line, String name, int receptientId, int thisClientId) throws IOException {



        /* Transferring File to a particular client */

        Server.database.addMessage(this.clientName, line, thisClientId, receptientId,
                isUserOnline(chateeName));

        for (clientThread curr_client : clients) {

            if (curr_client != null && curr_client.clientName != null
                    && curr_client.clientName.equals(name)) {
                if (curr_client.chateeName.equals(this.clientName)) {
                    curr_client.os.writeObject("" + this.clientName + ": " + line);
                    curr_client.os.flush();
                }




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
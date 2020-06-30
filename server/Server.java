package chat.server;

import java.net.*;
import java.io.*;

public class Server implements Runnable
{  private ChatServerThread clients[] = new ChatServerThread[50];
    private ServerSocket server = null;
    private Thread       thread = null;
    private int clientCount = 0;

    public Server(int port)
    {  try
    { // System.out.println("Binding to port " + port + ", please wait  ...");
        server = new ServerSocket(port);
    //    System.out.println("Server started: " + server);
        start(); }
    catch(IOException ioe)
    {  System.out.println("Can not bind to port " + port + ": " + ioe.getMessage()); }
    }
    public void run()
    {  while (thread != null)
    {  try
    { // System.out.println("Waiting for a client ...");
        addThread(server.accept()); }
    catch(IOException ioe)
    {  System.out.println("Server accept error: " + ioe); stop(); }
    }
    }
    public void start()  {if (thread == null)
    {  thread = new Thread(this);
        thread.start();
    } }
    public void stop()   {if (thread != null)
    {  thread.stop();
        thread = null;
    } }
    private int findClient(int ID)
    {  for (int i = 0; i < clientCount; i++)
        if (clients[i].getID() == ID)
            return i;
        return -1;
    }
    public synchronized void handle(int ID, String input, int uuID)
    {  if (input.equals("/exit"))
    {  clients[findClient(ID)].send("/exit");
        remove(ID, uuID); }
    else
        for (int i = 0; i < clientCount; i++)

            clients[i].send("Count is " + input.split(" ").length);
            System.out.println("Client " + uuID + ": " + input);
            System.out.println("Sent to client " + uuID + ": Count is " + input.split(" ").length);
    }
    public synchronized void remove(int ID, int uuID)
    {  int pos = findClient(ID);
        if (pos >= 0)
        {  ChatServerThread toTerminate = clients[pos];
            System.out.println("Client " + uuID + " disconnected!");
            if (pos < clientCount-1)
                for (int i = pos+1; i < clientCount; i++)
                    clients[i-1] = clients[i];
            clientCount--;
            try
            {  toTerminate.close(); }
            catch(IOException ioe)
            {  System.out.println("Error closing thread: " + ioe); }
            toTerminate.stop(); }
    }
    private void addThread(Socket socket)
    {  if (clientCount < clients.length)
    { // System.out.println("Client accepted: " + socket);
        clients[clientCount] = new ChatServerThread(this, socket);
        try
        {  clients[clientCount].open();
            clients[clientCount].start();
            clientCount++; }
        catch(IOException ioe)
        {  System.out.println("Error opening thread: " + ioe); } }
    else
        System.out.println("Client refused: maximum " + clients.length + " reached.");
    }
    public static void main(String args[]) {
        Server server = new Server(4998);
    }
}
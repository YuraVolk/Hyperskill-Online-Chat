package chat.client;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client implements Runnable
{  private Socket socket              = null;
    private Thread thread              = null;
    private DataInputStream  console   = null;
    private Scanner scanner = new Scanner(System.in);
    private DataOutputStream streamOut = null;
    private ChatClientThread client    = null;

    public Client(String serverName, int serverPort)
    { // System.out.println("Establishing connection. Please wait ...");
        try
        {  socket = new Socket(serverName, serverPort);
          //  System.out.println("Connected: " + socket);
            start();
        }
        catch(UnknownHostException uhe)
        {  System.out.println("Host unknown: " + uhe.getMessage()); }
        catch(IOException ioe)
        {  System.out.println("Unexpected exception: " + ioe.getMessage()); }
    }
    public void run()
    {  while (thread != null)
    {  try
    {
        streamOut.writeUTF(scanner.nextLine());
        streamOut.flush();
    }
    catch(IOException ioe)
    {  System.out.println("Sending error: " + ioe.getMessage());
        stop();
    }
    }
    }
    public void handle(String msg)
    {  if (msg.equals("/exit"))
    {  //System.out.println("Good bye. Press RETURN to exit ...");
        stop();

    }
    else
        System.out.println(msg);
    }
    public void start() throws IOException
    {  console   = new DataInputStream(System.in);
        streamOut = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client started!");
        if (thread == null)
        {  client = new ChatClientThread(this, socket);
            thread = new Thread(this);
            thread.start();
        }
    }
    public void stop()
    {
        client.close();
        System.exit(0);
    }
    public static void main(String args[])
    {  Client client = null;

    client = new Client("localhost", 4998);
    }
}
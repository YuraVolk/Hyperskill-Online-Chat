package chat.server;

import java.net.*;
import java.io.*;

public class ChatServerThread extends Thread
{  private Server       server    = null;
    private Socket           socket    = null;
    private int              ID        = -1;
    private static int uuIDCounter = 0;
    private int uuId;
    private DataInputStream  streamIn  =  null;
    private DataOutputStream streamOut = null;

    public ChatServerThread(Server _server, Socket _socket)
    {  super();
        server = _server;
        socket = _socket;
        ID     = socket.getPort();
        uuIDCounter++;
        uuId = uuIDCounter;
    }
    public void send(String msg)
    {   try
    {  streamOut.writeUTF(msg);
        streamOut.flush();
    }
    catch(IOException ioe)
    {  System.out.println(ID + " ERROR sending: " + ioe.getMessage());
        server.remove(ID, uuId);
        stop();
    }
    }
    public int getID()
    {  return ID;
    }

    public int getUuId() {
        return uuId;
    }

    public void run()
    {  System.out.println("Client " + uuId + " connected!");
        while (true)
        {  try
        {  server.handle(ID, streamIn.readUTF(), uuId);
        }
        catch(IOException ioe)
        {  System.out.println(ID + " ERROR reading: " + ioe.getMessage());
            server.remove(ID, uuId);
            stop();
        }
        }
    }
    public void open() throws IOException
    {  streamIn = new DataInputStream(new
            BufferedInputStream(socket.getInputStream()));
        streamOut = new DataOutputStream(new
                BufferedOutputStream(socket.getOutputStream()));
    }
    public void close() throws IOException
    {
        uuIDCounter--;
        if (socket != null)    socket.close();
        if (streamIn != null)  streamIn.close();
        if (streamOut != null) streamOut.close();
    }
}
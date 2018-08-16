import java.io.*;
import java.net.*;

public class ServerListener implements Runnable
{
	int serverPort;
	
	

	public ServerListener(int serverPort) {
		super();
		this.serverPort = serverPort;
	}



	@Override
	public void run()
	{
		
		try
		{
			ServerSocket serverSocket = new ServerSocket(6789);
			System.out.println("Server started");
			while(true) 
			{
				Socket connectionSocket = serverSocket.accept();
				BufferedReader serverReader = new BufferedReader( new InputStreamReader( connectionSocket.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				String line = serverReader.readLine();
				System.out.println("input from client :" + line);
				// spawn thread to handle request
				Thread serverRequestHandler = new Thread(new ServerRequestHandler(line, outToClient));
				//outToClient.writeBytes("done" + '\n');
				serverRequestHandler.start();
			}
		}
		catch(Exception e)
		{
			System.out.println("exception in server " + e.getMessage());
		}
	      

	    
		
	}

}

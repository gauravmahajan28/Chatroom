import java.io.*;
import java.net.*;

public class Client {

	public static void main(String[] args) throws Exception
	{
		// TODO Auto-generated method stub
		// start two threads ,one to send commands to server
		// other to listen for incoming messages
		String clientName = args[0];
		String serverIp = args[1];
		int serverPort = Integer.parseInt(args[2]);
		String clientIp = args[3];
		int clientPort = Integer.parseInt(args[4]);
		
		
		//register client
		Socket clientSocket = new Socket(serverIp, serverPort);
	    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
	    outToServer.writeBytes("REGISTER-" + clientName + "-" + clientIp + ":" + clientPort + '\n');
	    BufferedReader inFromServer = new BufferedReader( new InputStreamReader( clientSocket.getInputStream()));
	    String response = inFromServer.readLine();
	    
	    if(response.startsWith("FALSE"))
	    {
	    	System.out.println("message from server :" + response.split("-")[1]);
	    	return;
	    }
	    
		System.out.println("message from server :" + response.split("-")[1]);
	    
		Thread commandSender = new Thread(new CommandSender(serverIp, serverPort, clientName, clientIp, clientPort));
		commandSender.start();
		
		Thread tcpClientListener = new Thread(new TcpClientListener(clientName, clientPort));
		tcpClientListener.start();
		
		Thread udpClientListener = new Thread(new UdpClientListener(clientName, clientPort));
		udpClientListener.start();
	     
	}

}

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class UdpClientListener implements Runnable{

	String clientName;
	int clientPort;
	
	
	
	public UdpClientListener(String clientName, int clientPort) {
		super();
		this.clientName = clientName;
		this.clientPort = clientPort;
	}



	@Override
	public void run() {
		// TODO Auto-generated method stub
		try
		{
			DatagramSocket serverSocket = new DatagramSocket(clientPort);
			byte[] receive = new byte[65535];
			DatagramPacket receivedPacket = null;
			receivedPacket = new DatagramPacket(receive, receive.length);
			while(true) 
			{
				 // Step 2 : create a DatgramPacket to receive the data.
			
	 
	            // Step 3 : revieve the data in byte buffer.
	            serverSocket.receive(receivedPacket);
	            String received = new String(
	                    receivedPacket.getData(), 0, receivedPacket.getLength());
	            System.out.println("Received : " + received);
	 
	            String fileName = received.split("-")[1];
	            String fromUser = received.split("-")[2];
	            
	            System.out.println("Receiving file : " + fileName + "  from : " + fromUser);
	       	 
	            // Clear the buffer after every message.
	            receive = new byte[65535];
			
	            
	            String currentDir = System.getProperty("user.dir");
				System.out.println(" current directory is :" + currentDir);
		        FileOutputStream fileOutputStream = new FileOutputStream(currentDir + "//" + fileName);
		        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
		        
		        serverSocket.receive(receivedPacket);
		        received = new String(
	                    receivedPacket.getData(), 0, receivedPacket.getLength());
		       // System.out.println("received : " + received);
		        bufferedOutputStream.write(received.getBytes(), 0, received.getBytes().length); 
		        bufferedOutputStream.flush();
		        bufferedOutputStream.close();
		        System.out.println(" FILE RECEIVED SUCCESSFULLY ");	
	            
			}
		}
		catch(Exception e)
		{
			System.out.println("error getting data :" + e.getMessage());
		}
		
	}

}

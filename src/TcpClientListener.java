import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class TcpClientListener implements Runnable{

	String clientName;
	int clientPort;
	
	
	
	public TcpClientListener(String clientName, int clientPort) {
		super();
		this.clientName = clientName;
		this.clientPort = clientPort;
	}



	@Override
	public void run() {
		// TODO Auto-generated method stub
		try
		{
			ServerSocket clientListenerSocket = new ServerSocket(clientPort);
			while(true) 
			{
				Socket connectionSocket = clientListenerSocket.accept();
				BufferedReader clientReader = new BufferedReader( new InputStreamReader( connectionSocket.getInputStream()));
				String line = clientReader.readLine();
				
				if(line.startsWith("MESSAGE"))
				{
					String message = line.split("-")[1];
					String fromUser = line.split("-")[2];
					System.out.println(" message received from "  + fromUser + ":" + message);
				}
				else if(line.startsWith("FILE"))
				{
					String fileName = line.split("-")[1];
					String fromUser = line.split("-")[2];
					System.out.println("receiving file : " + fileName + " from "  + fromUser);
					
					
					//Initialize the FileOutputStream to the output file's full path.
					String currentDir = System.getProperty("user.dir");
					System.out.println(" current directory is :" + currentDir);
			        FileOutputStream fileOutputStream = new FileOutputStream(currentDir + "//" + fileName);
			        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
			        InputStream inputStream = connectionSocket.getInputStream();
			      
			        int count;
			        byte[] buffer = new byte[8192]; // or 4096, or more
			        Thread.sleep(1000);
			        while ((count = inputStream.read(buffer)) > 0)
			        {
			        	Thread.sleep(1000);
			        	bufferedOutputStream.write(buffer, 0, count);
			        	System.out.println("writing to file");
			        	Thread.sleep(1000);
			        }
			        
			        bufferedOutputStream.flush();
					        
			        bufferedOutputStream.flush(); 
			        bufferedOutputStream.close();
			        
			        System.out.println(" FILE RECEIVED SUCCESSFULLY ");	
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("error getting data :" + e.getMessage());
		}
		
	}

}

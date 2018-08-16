import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CommandSender implements Runnable {


	String serverIp;
	int serverPort;
	String clientName;
	String clientIp;
	int clientPort;
	
	public CommandSender(String serverIp, int serverPort, String clientName, String clientIp, int clientPort) {
		super();
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		this.clientName = clientName;
		this.clientIp = clientIp;
		this.clientPort = clientPort;
	}

	
	
	
	@Override
	public void run() 
	{
		try
		{
			while(true)
			{
				// TODO Auto-generated method stub
				System.out.println("Enter command to send to server");
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
				String command = bufferedReader.readLine();
				
				if(command.startsWith("reply"))
				{
					Socket clientSocket = new Socket(serverIp, serverPort);
				    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
				    BufferedReader inFromServer = new BufferedReader( new InputStreamReader( clientSocket.getInputStream()));
				    outToServer.writeBytes(command + "-" + clientName +  '\n');
				    String responseFromServer  = inFromServer.readLine();
				    System.out.println("FROM SERVER: " + responseFromServer + "\n");
				    clientSocket.close();	
				    
				    if(!responseFromServer.startsWith("FALSE"))
				    {
				    	
				    	System.out.println("message to be sent to :" + responseFromServer);
						List<String> ipPort = new ArrayList<String>();
						String[] ips = responseFromServer.split("-");
						for(String ip : ips)
						{
							if(ip.equals("TRUE") || ip.equals("FALSE") || ip.isEmpty())
								continue;
							ipPort.add(ip);
						}
				    	
				    	if(command.endsWith("tcp"))
						{
							// sending file using tcp
							String fileName = command.split(" ")[1];
							System.out.println("filename :" + fileName);
							URL resource = this.getClass().getResource(fileName);
							File file = new File(resource.toURI());
							if(!file.exists() || file.isDirectory()) 
							{ 
							   System.out.println("file not found ! or it is not valid file");
							   continue;
							}
							else
							{
								System.out.println("file exists");
								// TRUE-localhost:9001-
								for(String clientToSendMessage : ipPort)
								{
									String clientIp = clientToSendMessage.split(":")[0];
									int clientPort = Integer.parseInt(clientToSendMessage.split(":")[1]);
									Socket messageSocket = new Socket(clientIp, clientPort);
								    DataOutputStream outToClient = new DataOutputStream(messageSocket.getOutputStream());
								    outToClient.writeBytes("FILE-" + fileName + "-" + clientName +  '\n');
								    System.out.println("MESSAGE SENT TO :" + clientToSendMessage + "\n");
								    
								    FileInputStream fileInputStream = new FileInputStream(file);
							        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream); 
							        outToClient.flush();
							        
							      //Get socket's output stream
							        OutputStream outputStream = messageSocket.getOutputStream();
							                
							        //Read File Contents into contents array 
							
							        int count;
							        byte[] buffer = new byte[8192]; // or 4096, or more
							        while ((count = bufferedInputStream.read(buffer)) > 0)
							        {
							        	outputStream.write(buffer, 0, count);
							        	System.out.println("sendinf file");
							        }
							        
							        outputStream.flush();
							        System.out.println("file sent successfully");
							        
							        
							        //File transfer done. Close the socket connection! 
								    messageSocket.close();	
								}
							}
						}
						else if(command.endsWith("udp"))
						{
							// sending file using tcp
							String fileName = command.split(" ")[1];
							URL resource = this.getClass().getResource(fileName);
							File file = new File(resource.toURI());
							if(!file.exists() || file.isDirectory()) 
							{ 
							   System.out.println("file not found ! or it is not valid file");
							   continue;
							}
							else
							{
								// TRUE-localhost:9001-
								for(String clientToSendMessage : ipPort)
								{
									String clientIp = clientToSendMessage.split(":")[0];
									int clientPort = Integer.parseInt(clientToSendMessage.split(":")[1]);
									System.out.println("trying to create udp socket for :" + clientIp + ":" + clientPort);
									DatagramSocket ds = new DatagramSocket();
								    String message = "FILE-" + fileName + "-" + clientName +  '\n';
								    DatagramPacket DpSend = new DatagramPacket(message.getBytes(), message.getBytes().length, InetAddress.getByName(clientIp), clientPort);
								    ds.send(DpSend);
								    System.out.println("MESSAGE SENT TO :" + clientToSendMessage + "\n");
								    
								    FileInputStream fileInputStream = new FileInputStream(file);
							        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream); 
							               
							        //Read File Contents into contents array 
							        byte[] contents;
							        long fileLength = file.length(); 
							        long current = 0;
							         
							        long start = System.nanoTime();
							        while(current!=fileLength)
							        { 
							            int size = 10000;
							            if(fileLength - current >= size)
							                current += size;    
							            else
							            { 
							                size = (int)(fileLength - current); 
							                current = fileLength;
							            } 
							            contents = new byte[size]; 
							            bufferedInputStream.read(contents, 0, size); 
							            DatagramPacket packet = new DatagramPacket(contents, contents.length, InetAddress.getByName(clientIp), clientPort);
									    ds.send(packet);
									   
							            System.out.print("Sending file ... "+(current*100)/fileLength+"% complete!");
							        }   
							       
							        //File transfer done. Close the socket connection! 
							        ds.close();
								}
							}
						}
						else
						{
							String message = command.replaceFirst("reply", "");
							
							// TRUE-localhost:9001-
							for(String clientToSendMessage : ipPort)
							{
								String clientIp = clientToSendMessage.split(":")[0];
								int clientPort = Integer.parseInt(clientToSendMessage.split(":")[1]);
								Socket messageSocket = new Socket(clientIp, clientPort);
							    DataOutputStream outToClient = new DataOutputStream(messageSocket.getOutputStream());
							    outToClient.writeBytes("MESSAGE-" + message + "-" + clientName +  '\n');
							    System.out.println("MESSAGE SENT TO :" + clientToSendMessage + "\n");
							    messageSocket.close();	
							}
							// sending message
						}				    	
				    }				    
				}
				else
				{
					Socket clientSocket = new Socket(serverIp, serverPort);
				    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
				    BufferedReader inFromServer = new BufferedReader( new InputStreamReader( clientSocket.getInputStream()));
				    outToServer.writeBytes(command + "-" + clientName +  '\n');
				    String responseFromServer  = inFromServer.readLine();
				    System.out.println("FROM SERVER: " + responseFromServer + "\n");
				    clientSocket.close();		
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("error in sending message to server " + e.getMessage());
		}
	}

}

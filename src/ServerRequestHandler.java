import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.sun.security.ntlm.Server;

public class ServerRequestHandler implements Runnable
{

	String command;
	DataOutputStream responseWriter;
	
	
	public ServerRequestHandler(String command, DataOutputStream responseWriter) {
		super();
		this.command = command;
		this.responseWriter = responseWriter;
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		try
		{
			if(command.startsWith("REGISTER"))
			{
				String clientName = command.split("-")[1];
				String clientIpPort = command.split("-")[2];
				System.out.println("client connected ! : " + clientName + " with ip port :" + clientIpPort);
				if(ServerDataStructure.connectedClients.containsKey(clientName))
					responseWriter.writeBytes("FALSE-Client Name Already Present !" + '\n');
				else
				{
					ServerDataStructure.connectedClients.put(clientName, "EMPTY");
					ServerDataStructure.clientIpPort.put(clientName, clientIpPort);
					responseWriter.writeBytes("TRUE-Client Registered !" + '\n');
				}
			}
			else if(command.startsWith("create chatroom"))
			{
				String clientName = command.split("-")[1];
				String createChatroomCommand = command.split("-")[0];
				System.out.println("client connected ! : " + clientName);
				String chatroomName = createChatroomCommand.split(" ")[2];
				System.out.println("trying to create chat room :" + chatroomName);
				
				// char room already present
				if(ServerDataStructure.chatRoomClients.containsKey(chatroomName))
				{
					responseWriter.writeBytes("FALSE-Chat room already present !" + '\n');
				}
				else
				{
					// add client in new chatroom
					Map<String, Boolean> clients = new HashMap<String, Boolean>();
					clients.put(clientName, true);
					ServerDataStructure.chatRoomClients.put(chatroomName, clients);
					
					// client is not associated with any chat room
					if(ServerDataStructure.connectedClients.get(clientName).equals("EMPTY"))
					{
						ServerDataStructure.connectedClients.put(clientName, chatroomName);
						responseWriter.writeBytes("TRUE-" + clientName + " is switched from EMPTY to "  + chatroomName + "!" + '\n');
						
					}
					// client is associated with other chat room
					else
					{
						String prevChatRoom = ServerDataStructure.connectedClients.get(clientName);
						ServerDataStructure.chatRoomClients.get(prevChatRoom).remove(clientName);
						responseWriter.writeBytes("TRUE-" + clientName + " is switched from " + prevChatRoom + " to "  + chatroomName + "!" + '\n');
					}
				}
			}
			else if(command.startsWith("list chatrooms"))
			{
				String chatrooms = "";
				for(String chatroom : ServerDataStructure.chatRoomClients.keySet())
					chatrooms += chatroom + "-";
				chatrooms += "\n";
				responseWriter.writeBytes("TRUE-" + chatrooms + '\n');
			}
			else if(command.startsWith("join"))
			{
				String clientName = command.split("-")[1];
				String createChatroomCommand = command.split("-")[0];
				System.out.println("client connected ! : " + clientName);
				String chatroomName = createChatroomCommand.split(" ")[1];
				System.out.println("trying to add user to chat room :" + chatroomName);
				
				//checking if chat room is present
				if(!ServerDataStructure.chatRoomClients.containsKey(chatroomName))
				{
					responseWriter.writeBytes("FALSE-Chat room is not present !" + '\n');	
				}
				else
				{
					if(ServerDataStructure.connectedClients.get(clientName).equals("EMPTY"))
					{
						ServerDataStructure.connectedClients.put(clientName, chatroomName);
						ServerDataStructure.chatRoomClients.get(chatroomName).put(clientName, true);
						responseWriter.writeBytes("TRUE-" + clientName + " added  in " + chatroomName + " !" + '\n');
					}
					else
					{
						// user is already in chatroom
						if(ServerDataStructure.connectedClients.get(clientName).equals(chatroomName))
						{
							responseWriter.writeBytes("FALSE-" + clientName + " is already present  in " + chatroomName + " !" + '\n');
						}
						else
						{
							// remove from previous chat room and add in new chatroom
							String prevChatRoom = ServerDataStructure.connectedClients.get(clientName);
							ServerDataStructure.chatRoomClients.get(prevChatRoom).remove(clientName);
							ServerDataStructure.connectedClients.put(clientName, chatroomName);
							ServerDataStructure.chatRoomClients.get(chatroomName).put(clientName, true);
							responseWriter.writeBytes("TRUE-" + clientName + " joined " + chatroomName + " and left " + prevChatRoom + " !" + '\n');
						}
					}	
				}
			}
			else if(command.startsWith("leave"))
			{
				String clientName = command.split("-")[1];
				System.out.println("client connected ! : " + clientName);
				String currentChatRoom = ServerDataStructure.connectedClients.get(clientName);
				
				if(currentChatRoom.equals("EMPTY"))
				{
					responseWriter.writeBytes("FALSE-" + clientName + " is not associated with any chatroom!" + '\n');
				}
				else
				{
					ServerDataStructure.connectedClients.put(clientName, "EMPTY");
					ServerDataStructure.chatRoomClients.get(currentChatRoom).remove(clientName);
					if(ServerDataStructure.chatRoomClients.get(currentChatRoom).keySet().size() == 0)
					{
						ServerDataStructure.chatRoomClients.remove(currentChatRoom);
					}
					responseWriter.writeBytes("TRUE-" + clientName + " is removed from " + currentChatRoom + '\n');
				}
			}
			else if(command.startsWith("list users"))
			{
				String clientName = command.split("-")[1];
				System.out.println("client connected ! : " + clientName);
				String currentChatRoom = ServerDataStructure.connectedClients.get(clientName);
				
				if(currentChatRoom.equals("EMPTY"))
				{
					responseWriter.writeBytes("FALSE-" + clientName + " is not associated with any chatroom!" + '\n');
				}
				else
				{
					String users = "";
					for(String user : ServerDataStructure.chatRoomClients.get(currentChatRoom).keySet())
					{
						users += "-" + user;
					}
					responseWriter.writeBytes("TRUE-" + users + '\n');
				}
			}
			else if(command.startsWith("add"))
			{
				String clientName = command.split("-")[1];
				System.out.println("client connected ! : " + clientName);
				String userToBeAdded = command.split("-")[0].split(" ")[1];
				
				String currentChatRoom = ServerDataStructure.connectedClients.get(clientName);
				
				if(currentChatRoom.equals("EMPTY"))
				{
					responseWriter.writeBytes("FALSE-" + clientName + " is not associated with any chatroom!" + '\n');
				}
				else if(!ServerDataStructure.connectedClients.containsKey(userToBeAdded))
				{
					responseWriter.writeBytes("FALSE-" + userToBeAdded + " does not exist !" + '\n');
				}
				else if(!(ServerDataStructure.connectedClients.get(userToBeAdded).equals("EMPTY")))
				{
					responseWriter.writeBytes("FALSE-" + userToBeAdded + " is associated with chat room " + ServerDataStructure.connectedClients.get(userToBeAdded) + '\n');
				}
				else
				{
					ServerDataStructure.connectedClients.put(userToBeAdded, currentChatRoom);
					ServerDataStructure.chatRoomClients.get(currentChatRoom).put(userToBeAdded, true);
					responseWriter.writeBytes("TRUE-" + userToBeAdded + " is added to chat room " + currentChatRoom + '\n');
				}
			}
			else if(command.startsWith("reply"))
			{
				String clientName = command.split("-")[1];
				System.out.println("client connected ! : " + clientName);
				String currentChatRoom = ServerDataStructure.connectedClients.get(clientName);
				
				if(currentChatRoom.equals("EMPTY"))
				{
					responseWriter.writeBytes("FALSE-" + clientName + " is not associated with any chatroom!" + '\n');
				}
				else if(ServerDataStructure.chatRoomClients.get(currentChatRoom).keySet().size() == 1)
				{
					responseWriter.writeBytes("FALSE-" + clientName + " is the only one in chatroom!" + '\n');
				}
				else
				{
					String users = "";
					for(String user : ServerDataStructure.chatRoomClients.get(currentChatRoom).keySet())
					{
						if(!user.equals(clientName))
							users += ServerDataStructure.clientIpPort.get(user) + "-";
					}
					responseWriter.writeBytes("TRUE-" + users + '\n');
				}
			}
			else
			{
				responseWriter.writeBytes("FALSE-Command Not Found !" + '\n');
			}
		}
		catch(Exception e)
		{
			System.out.println("error :" + e.getMessage());
		}
	}
	

}

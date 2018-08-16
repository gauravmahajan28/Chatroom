import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerDataStructure 
{
	// entry key : name of client, value : name of chat room 
	public static Map<String,String> connectedClients = new HashMap<String, String>();
	
	// chat room name to map of clients ( for fast access )
	public static Map<String, Map<String, Boolean>> chatRoomClients = new HashMap<String, Map<String, Boolean>>();
	
	// entry to map client to ip and port 
	public static Map<String,String> clientIpPort = new HashMap<String, String>();
		
	public static int maxClientCount;
}

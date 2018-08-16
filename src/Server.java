public class Server 
{
	public static void main(String[] args) 
	{
		int maxNumberOfClients = Integer.parseInt(args[0]);
		int serverPort = Integer.parseInt(args[1]);
		ServerDataStructure.maxClientCount = maxNumberOfClients;
		// start listener thread
		Thread serverListener = new Thread(new ServerListener(serverPort));
		serverListener.start();
	}

}

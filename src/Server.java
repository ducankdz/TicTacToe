import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	private static final int port = 5055;
	private static List<ClientHandler> clients = new ArrayList<>();
	
	public static void main(String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Server is running ...");
			
			while(true) {
				Socket socket = serverSocket.accept();
				System.out.println("Client " + socket.getInetAddress().getHostAddress() + " joined the room.");
				ClientHandler client = new ClientHandler(socket,new Server(),clients.size()+1);
				clients.add(client);
				client.start();
				
				if (clients.size() == 2) {
	                clients.get(0).setOpponent(clients.get(1));
	                clients.get(1).setOpponent(clients.get(0));
	                clients.get(0).startGame();
	            }
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public void broadcastMessage(String msg) {
		for(ClientHandler clientHandler: clients) {
			clientHandler.sendMessage(msg);
		}
	}
}

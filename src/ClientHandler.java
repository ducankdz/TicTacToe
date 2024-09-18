import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread{
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private ClientHandler opponent;
	private int playerId;
	private Server server;
	
	public ClientHandler(Socket socket, Server server, int playerId) {
		this.socket = socket;
        this.playerId = playerId;
        this.server = server;
        try {
			this.out = new PrintWriter(socket.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ClientHandler getOpponent() {
		return opponent;
	}

	public void setOpponent(ClientHandler opponent) {
		this.opponent = opponent;
	}
	
	public void startGame() {
        out.println("START " + playerId);
    }
	
	@Override
	public void run() {
		try {
			out.println("ID: " + playerId);
			out.flush();
			
			String input;
			while((input = in.readLine()) != null) {
				if(opponent != null) {
					server.broadcastMessage(input);
					System.out.println(input);
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Connection error: " + e.getMessage());
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void sendMessage(String msg) {
		out.println(msg);
		out.flush();
	}

}

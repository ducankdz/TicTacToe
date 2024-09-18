import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;

public class CaroClient extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton[][] buttons = new JButton[3][3];
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int playerId;
    private boolean myTurn = false;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Scanner sc = new Scanner(System.in);
					System.out.print("Enter address: ");
					String address = sc.nextLine();
					CaroClient frame = new CaroClient(address);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CaroClient(String address) {
		setTitle("Caro");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 352, 424);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 3, 0, 0));
		for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 40));
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].addActionListener(this);
                add(buttons[i][j]);
            }
        }
		
		try {
			socket = new Socket(address,5055);
			out = new PrintWriter(socket.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
	        new Thread(() -> {
	        	String response;
	        	try {
					while((response = in.readLine()) != null) {
						if(response.startsWith("ID")) {
							String[] parts = response.trim().split("\\s+");
							playerId = Integer.parseInt(parts[1]);
							myTurn = (playerId == 1);
	                        setTitle("Caro Game - Player " + playerId + (myTurn ? " (Your Turn)" : ""));
						}
						else if(response.contains("MOVE")) {
							String[] parts = response.trim().split("\\s+");
							int id = Integer.parseInt(parts[0]);
							int row = Integer.parseInt(parts[2]);
	                        int col = Integer.parseInt(parts[3]);
	                        
	                        buttons[row][col].setText(id == 1 ? "X" : "O");
	                        int res = result();
	                        if (res == 1 || res == 0) {
	                            int option = JOptionPane.showOptionDialog(
	                                this, 
	                                res == 1 ? "Player " + id + " won the game!" : "Draw!",
	                                "Game Over", 
	                                JOptionPane.YES_NO_OPTION, 
	                                JOptionPane.QUESTION_MESSAGE, 
	                                null, 
	                                new String[]{"Restart", "Exit"}, // Các tùy chọn cho người dùng
	                                "Restart" // Mặc định là "Restart"
	                            );

	                            if (option == JOptionPane.YES_OPTION) {
	                                // Người dùng chọn "Restart"
	                                restartGame();
	                            } else {
	                                // Người dùng chọn "Exit"
	                                System.exit(0);
	                            }
	                        }
	                        else {
	                        	if(id != playerId) {
		                        	myTurn = true;
		                        }
		                        setTitle("Caro Game - Player " + playerId + (myTurn ? " (Your Turn)" : ""));
	                        }
	                    }
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        }).start();;
		} catch (Exception e2) {
			// TODO: handle exception
			e2.printStackTrace();
		}
		
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
    	for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
            	if (myTurn && buttons[i][j].getText().equals("") && (e.getSource() instanceof JButton) && (((JButton) e.getSource()).equals(buttons[i][j]))) {
            		buttons[i][j].setText(playerId == 1 ? "X" : "O");
            		out.println(playerId + " MOVE " + i + " " + j);
            		myTurn = false;
            	}
            }
        } 
        
    }
	public int result() {
	    // Kiểm tra hàng ngang
	    for (int i = 0; i < 3; i++) {
	        if (!buttons[i][0].getText().equals("") &&
	            buttons[i][0].getText().equals(buttons[i][1].getText()) &&
	            buttons[i][0].getText().equals(buttons[i][2].getText())) {
	            return 1;
	        }
	    }

	    // Kiểm tra hàng dọc
	    for (int i = 0; i < 3; i++) {
	        if (!buttons[0][i].getText().equals("") &&
	            buttons[0][i].getText().equals(buttons[1][i].getText()) &&
	            buttons[0][i].getText().equals(buttons[2][i].getText())) {
	            return 1;
	        }
	    }

	    // Kiểm tra đường chéo chính
	    if (!buttons[0][0].getText().equals("") &&
	        buttons[0][0].getText().equals(buttons[1][1].getText()) &&
	        buttons[0][0].getText().equals(buttons[2][2].getText())) {
	        return 1;
	    }

	    // Kiểm tra đường chéo phụ
	    if (!buttons[0][2].getText().equals("") &&
	        buttons[0][2].getText().equals(buttons[1][1].getText()) &&
	        buttons[0][2].getText().equals(buttons[2][0].getText())) {
	        return 1;
	    }
	    
	    if(drawGame()) {
	    	return 0;
	    }
	    
	    return -1;
	}
	
	public boolean drawGame() {
		for (int i = 0; i < 3; i++) {
	        for (int j = 0; j < 3; j++) {
	            if(buttons[i][j].getText().equals("")) {
	            	return false;
	            }
	        }
	    }
		return true;
	}
	
	public void restartGame() {
	    for (int i = 0; i < 3; i++) {
	        for (int j = 0; j < 3; j++) {
	            buttons[i][j].setText("");
	        }
	    }
	    
	    
	    myTurn = (playerId == 1); 
	    setTitle("Caro Game - Player " + playerId + (myTurn ? " (Your Turn)" : ""));
	}

}  
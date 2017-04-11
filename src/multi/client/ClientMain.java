package multi.client;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import db.DBManager;

public class ClientMain extends JFrame implements ItemListener, ActionListener{
	JPanel p_north, p_south;
	Choice choice;
	JTextField t_port, t_input;
	JButton bt_connect;
	JTextArea area;
	JScrollPane scroll;
	DBManager manager;
	ArrayList<Chat> list = new ArrayList<Chat>();	
	String ip;
	int port;
	Socket socket;
	BufferedReader buffr;
	BufferedWriter buffw;
	ClientThread ct;
	String nickName="조코비치";


	public ClientMain() {
		p_north = new JPanel();
		p_south = new JPanel();
		choice = new Choice();
		t_port = new JTextField("7777", 5);
		t_input = new JTextField(20);
		bt_connect = new JButton("접속");
		area = new JTextArea();
		scroll = new JScrollPane(area);
		manager = DBManager.getInstance();

		p_north.add(choice);
		p_north.add(t_port);
		p_north.add(bt_connect);

		p_south.add(t_input);

		add(p_north, BorderLayout.NORTH);
		add(scroll);
		add(p_south, BorderLayout.SOUTH);
		
		loadIP();
		for(int i=0; i<list.size(); i++){
			choice.add(list.get(i).getName());
		}
		setTitle(list.get(0).getIp());
		
		//초이스랑 리스너 연결!
		choice.addItemListener(this);
		//버튼에 리스너 연결
		bt_connect.addActionListener(this);
		//텍스트필드 리스너 연결
		t_input.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				int key = e.getKeyCode();
				
				if(key==KeyEvent.VK_ENTER){
					String msg = t_input.getText();
					ct.send(msg);	//전송				
					t_input.setText(""); //입력한 글씨 지우기
				}
			}
		});


		setBounds(300, 100, 300, 400);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void loadIP() {
		Connection con = manager.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select * from chat order by chat_id asc";

		try {
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				Chat dto = new Chat();
				dto.setChat_id(rs.getInt("chat_id"));
				dto.setName(rs.getString("name"));
				dto.setIp(rs.getString("ip"));
				list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			manager.disConnect(con);
		}
	}
	
	public void itemStateChanged(ItemEvent e) {
		Choice ch=(Choice)e.getSource();
		int i=ch.getSelectedIndex();
		ip = list.get(i).getIp();
		this.setTitle(ip);
		
	}
	
	//서버에 접속을 시도한다.
	public void connect(){
		try {
			port = Integer.parseInt(t_port.getText());
			socket = new Socket(ip, port);
			
			
			//대화를 담당할 클라이언트 측의 쓰레드 생성 및 가동
			ct = new ClientThread(socket,this);
			ct.start();
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}
		
	
	
	public void actionPerformed(ActionEvent e) {
		connect();
	}

	public static void main(String[] args) {
		new ClientMain();
	}
}

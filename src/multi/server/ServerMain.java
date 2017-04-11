package multi.server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerMain extends JFrame implements ActionListener, Runnable{
	JPanel p_north; 
	JTextField t_port;
	JButton bt_start;
	JTextArea area;
	JScrollPane scroll;
	int port=7777;
	Thread thread;
	ServerSocket server;
	Socket socket;
	Vector<ServerThread> list = new Vector<ServerThread>();//멀티캐스팅을 위해서는 현재 서버에 몇명이 들어오고 나가는지를 체크할 저장소가 필요하며, 유연해야 하므로 컬렉션 계열로 선언하자!
	
	
	public ServerMain() {
		p_north = new JPanel();
		t_port = new JTextField(Integer.toString(port) ,10);
		bt_start = new JButton("가동");
		area = new JTextArea();
		scroll = new JScrollPane(area);
		p_north.add(t_port);
		p_north.add(bt_start);
		add(p_north, BorderLayout.NORTH);
		add(scroll);
		
		bt_start.addActionListener(this);
		
		setBounds(600,100,300,400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public void startServer(){
		try {
			port = Integer.parseInt(t_port.getText());
			server = new ServerSocket(port);
			area.append("서버생성완료\n");
			
			while(true){
				socket=server.accept();
				String ip=socket.getInetAddress().getHostAddress();
				area.append(ip+"사용자발견\n");
				
				//접속자마다 쓰레드를 하나씩 할당해서 대화할 수 있도록 한다
				//또한 멀티캐스팅을 위해서 각 써버쓰레드(접속자 아바타)를 벡터에 담아둔다.
				ServerThread st = new ServerThread(socket, this);
				list.add(st);
				area.append("현재 접속자는 "+list.size()+"명\n");
				st.start();
				
				socket.isClosed();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		startServer();
		
	}
	
	public void actionPerformed(ActionEvent e) {
		thread =new Thread(this);
		thread.start();
	}
	
	public static void main(String[] args) {
		new ServerMain();
	}
}
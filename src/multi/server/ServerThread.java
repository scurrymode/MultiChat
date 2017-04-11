/*
 * ������ Ŭ���̾�Ʈ�� 1:1�� ��ȭ�� ���� ������
 * */

package multi.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JTextArea;

public class ServerThread extends Thread {
	Socket socket;
	ServerMain main;
	BufferedReader buffr;
	BufferedWriter buffw;
	JTextArea area;
	boolean flag =true;

	// �����κ��� ������ �޾ƿ���!
	public ServerThread(Socket socket, ServerMain main) {
		this.socket = socket;
		this.main = main;
		try {
			buffr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Ŭ���̾�Ʈ�� �޼��� �ޱ�

	public void listen() {
		String msg = null;
		try {
			msg = buffr.readLine(); //���
			main.area.append(msg+"\n");
			send(msg); //�ٽú�����

		} catch (Exception e) {
			//�� �����带 ������� ���ϰ� run�� ���߰� ��(������)�� �׿������� �Ѵ�.
			flag = false;
			main.list.remove(this);
			
			main.area.append("1�� ������ ���� �����ڴ� "+main.list.size()+"���Դϴ�.\n");
			//e.printStackTrace();
		}

	}

	public void send(String msg) {
		try {
			for(int i=0;i<main.list.size();i++){
				ServerThread st=main.list.elementAt(i);
				st.buffw.write(msg+"\n");
				st.buffw.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while(flag){
			listen();
		}

	}

}

package Chatting;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ChatServerGUI_v1 extends JFrame implements ActionListener, WindowListener {
	JTextArea ta_chat;
	JTable tblUser;
	DefaultTableModel modelUser;
	JTextField tf;
	HashMap<String, PrintWriter> map;
	HashMap<String, HashSet<String>> channel;

	public ChatServerGUI_v1() {
		init();
		serverSetting();
	}

	void init() { // ui설정
		this.setSize(500, 500);
//		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("서버");
		this.addWindowListener(this);

		ta_chat = new JTextArea("===채팅내용===\n");
		ta_chat.setEditable(false);
		String[] hdUser = {"접속자"};
		String[][] ctUser = {};
		modelUser = new DefaultTableModel(ctUser, hdUser);
		tblUser = new JTable(modelUser);
		JScrollPane sp_chat = new JScrollPane(ta_chat);
		JScrollPane sp_user = new JScrollPane(tblUser);
		sp_user.setPreferredSize(new Dimension(100, 400));
		tf = new JTextField();
		tf.addActionListener(this);

		this.add(sp_chat);
		this.add(sp_user, "East");
		this.add(tf, "South");

		this.setVisible(true);
	}

	void serverSetting() { // 서버관련
		try {
			ServerSocket server = new ServerSocket(5000);
			ta_chat.append("접속을 기다립니다.\n");
			map = new HashMap<>();
			channel = new HashMap<>();

			while (true) { // 이론상 접속자를 계속 받을 수 있게 계속 대기 하기 위해서
				Socket socket = server.accept();
				// 접속을 하면 스레드 실행
				(new ChatThreadGUI(socket, ta_chat, modelUser, map, channel)).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void broadcast(String str) {
		// 접속한 모두에게 글씨 보내기 주로 게임으로 친다면 공지사항 게임내 전체챗, 정보는 map이 다 들고 있음
		for (Map.Entry<String, PrintWriter> entry : map.entrySet()) {
			entry.getValue().println(str);
			entry.getValue().flush();
		}	// map안에 있는 모든 PrintWriter = out 에게 글씨 보냄 - 방송, 전체챗
	}

	public static void main(String[] args) {
		new ChatServerGUI_v1();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ta_chat.append("<서버> : " + tf.getText() + "\n");
		broadcast("<서버> : " + tf.getText());
		if(tf.getText().equals("/quit")){
			System.exit(0);
		}
		tf.setText("");
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		broadcast("<서버> : " + "/quit");
		System.exit(0);
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
	}

}

// 채팅 스레드
class ChatThreadGUI extends Thread {
	// 이스레드는 클라이언트로 부터 들어오는 내용을 ta_chat에다가 추가하는 일을 함.
	Socket socket;
	JTextArea ta_chat;
	DefaultTableModel modelUser;
	HashMap<String, PrintWriter> map;
	HashMap<String, HashSet<String>> channel;

	PrintWriter out;
	BufferedReader in;
	String id;

	public ChatThreadGUI(Socket socket, JTextArea ta_chat, DefaultTableModel modelUser, HashMap<String, PrintWriter> map, HashMap<String, HashSet<String>> channel) {
		this.socket = socket;
		this.ta_chat = ta_chat;
		this.modelUser = modelUser;
		this.map = map;
		this.channel = channel;

		try {
			out = new PrintWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			id = in.readLine().replace(" ", "");
			modelUser.setRowCount(modelUser.getRowCount()+1);
			modelUser.setValueAt(id, modelUser.getRowCount()-1, 0);
			ta_chat.append("[" + id + "] 님이 접속 했습니다.\n");

			map.put(id, out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	void broadcast(String str) {
		for (Map.Entry<String, PrintWriter> entry : map.entrySet()) {
			entry.getValue().println(str);
			entry.getValue().flush();
		}
	}
	
	void chBroadcast(String msg) {
		String[] ch = msg.split(" ");
		String chmsg = "";
		for (int i = 1; i < ch.length; i++) {
			chmsg += ch[i];
			if(i < ch.length-1) {
				chmsg += " ";
			}
		}
		if(channel.get(ch[0]).contains(id)) {
			Iterator<String> iter = channel.get(ch[0]).iterator();
			while(iter.hasNext()) {
				String chid = iter.next();
				map.get(chid).println("[" + ch[0].replace("/", "") + "][" + id + "] : " + chmsg);
				map.get(chid).flush();
			}
		}else {
			map.get(id).println("미가입 채널 입니다.");
			map.get(id).flush();
		}
	}
	
	void createChannel(String ch) {
		if(channel.containsKey(ch)) {
			map.get(id).println("이미 존재하는 채널입니다.");
			map.get(id).flush();
		}else {
			channel.put(ch, new HashSet<>());
			channel.get(ch).add(id);
			map.get(id).println("[" + ch + "]이 생성되었습니다.");
			map.get(id).flush();
		}
	}
	
	void joinChannel(String ch) {
		if(channel.containsKey(ch)) {
			channel.get(ch).add(id);
			map.get(id).println("[" + ch + "]에 입장하였습니다.");
			map.get(id).flush();
		}else {
			map.get(id).println("존재하지 않는 채널입니다.");
			map.get(id).flush();
		}
	}
	void whisper(String msg) {
		String[] wis = msg.split(" ");
		String wmsg = "";
		for (int i = 2; i < wis.length; i++) {
			wmsg += wis[i];
			if(i < wis.length-1) {
				wmsg += " ";
			}
		}
		if(map.containsKey(wis[1])) {
			ta_chat.append("[" + id + "님이 " + wis[1] + "님에게 보낸 귓속말] : " + wmsg + "\n");
			map.get(id).println("[" + wis[1] + "님에게 보낸 귓속말] : " + wmsg);
			map.get(id).flush();
			map.get(wis[1]).println("[" + id + "님의 귓속말] : " + wmsg);
			map.get(wis[1]).flush();
		}else {
			map.get(id).println("상대방이 존재하지 않습니다.");
			map.get(id).flush();
		}
	}
	
	void userSync() {
		String userlist = "";
		for (int i = 0; i < modelUser.getRowCount(); i++) {
			userlist += modelUser.getValueAt(i, 0) + " ";
			userlist.substring(0, userlist.lastIndexOf(" "));
		}
		broadcast("<서버> : /userlistsync " + userlist);
	}
	
	//전준형 - 자리비움
	void awayfromkeyboard() {
		for (int i = 0; i < modelUser.getRowCount(); i++) {
			if(modelUser.getValueAt(i, 0).equals(id)) {
				modelUser.setValueAt(id+"_자리비움", i, 0);
			}
		}
		userSync();
	}
	
	void backtokeyboard() {
		for (int i = 0; i < modelUser.getRowCount(); i++) {
			if(modelUser.getValueAt(i, 0).equals(id+"_자리비움")) {
				modelUser.setValueAt(id, i, 0);
			}
		}
		userSync();
	}
	//
	
	@Override
	public void run() {
		try {
			userSync();
			String msg = null;
			while((msg = in.readLine()) != null) {
				if(msg.equals("/quit")) {
					break;
				}else if(msg.split(" ")[0].equals("/w")) {
					whisper(msg);
				}else if(msg.split(" ")[0].equals("/create")) {
					createChannel("/" + msg.split(" ")[1]);
				}else if(msg.split(" ")[0].equals("/join")) {
					joinChannel("/" + msg.split(" ")[1]);
				}else if(channel.containsKey(msg.split(" ")[0])) {
					chBroadcast(msg);
				}
				
				// 전준형 - 자리비움
				else if(msg.equals("/afk")) {
					awayfromkeyboard();
				}
				else if(msg.equals("/back")){
					backtokeyboard();
				}
				//
				
				else {
					ta_chat.append("[" + id + "] : " + msg + "\n");
					broadcast("[" + id + "] : " + msg);	// 다른 클라이언트가 서버로 글을 보내면 서버가 읽어서 접속한 모두에게 보냄
				}
			}
			map.remove(id);
			ta_chat.append("[" + id + "] 님이 퇴장하셨습니다.\n");
			for (int i = 0; i < modelUser.getRowCount(); i++) {
				// 전준형 - 자리비움
				if(modelUser.getValueAt(i, 0).equals(id)
					|| modelUser.getValueAt(i, 0).equals(id+"_자리비움")) {
					modelUser.removeRow(i);
					break;
				}
				// 자리비움 상태에서도 나갈수 있도록 조건 추가
			}
			userSync();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
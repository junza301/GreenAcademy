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
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
	HashMap<String, ArrayList<String>> channel;

	public ChatServerGUI_v1() {
		init();
		serverSetting();
	}

	void init() { // ui����
		this.setSize(500, 500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("����");
		this.addWindowListener(this);

		ta_chat = new JTextArea("===ä�ó���===\n");
		ta_chat.setEditable(false);
		String[] hdUser = {"������"};
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

	void serverSetting() { // ��������
		try {
			ServerSocket server = new ServerSocket(5000);
			ta_chat.append("������ ��ٸ��ϴ�.\n");
			map = new HashMap<>();
			channel = new HashMap<>();

			while (true) { // �̷л� �����ڸ� ��� ���� �� �ְ� ��� ��� �ϱ� ���ؼ�
				Socket socket = server.accept();
				// ������ �ϸ� ������ ����
				(new ChatThreadGUI(socket, ta_chat, modelUser, map, channel)).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void broadcast(String str) {
		// ������ ��ο��� �۾� ������ �ַ� �������� ģ�ٸ� �������� ���ӳ� ��üê, ������ map�� �� ��� ����
		for (Map.Entry<String, PrintWriter> entry : map.entrySet()) {
			entry.getValue().println(str);
			entry.getValue().flush();
		}	// map�ȿ� �ִ� ��� PrintWriter = out ���� �۾� ���� - ���, ��üê
	}

	public static void main(String[] args) {
		new ChatServerGUI_v1();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(tf.getText().equals("/quit")){
			System.exit(0);
		}else {
			ta_chat.append("<����> : " + tf.getText() + "\n");
			broadcast("<����> : " + tf.getText());
		}
		tf.setText("");
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		broadcast("<����> : " + "/quit");
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

// ä�� ������
class ChatThreadGUI extends Thread {
	// �̽������ Ŭ���̾�Ʈ�� ���� ������ ������ ta_chat���ٰ� �߰��ϴ� ���� ��.
	Socket socket;
	JTextArea ta_chat;
	DefaultTableModel modelUser;
	HashMap<String, PrintWriter> map;
	HashMap<String, ArrayList<String>> channel;

	PrintWriter out;
	BufferedReader in;
	String id;

	public ChatThreadGUI(Socket socket, JTextArea ta_chat, DefaultTableModel modelUser, HashMap<String, PrintWriter> map, HashMap<String, ArrayList<String>> channel) {
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
			ta_chat.append("[" + id + "] ���� ���� �߽��ϴ�.\n");

			map.put(id, out);
			
			map.get(id).println("Chat������ ���Ű��� ȯ���մϴ�.\n(��ɾ�Ȯ�� : /help)");
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
		ch[0] = ch[0].replace("/", "");
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
				map.get(chid).println("[" + ch[0] + "][" + id + "] : " + chmsg);
				map.get(chid).flush();
			}
		}else {
			map.get(id).println("�̰��� ä�� �Դϴ�.");
			map.get(id).flush();
		}
	}
	
	void createChannel(String ch) {
		if(channel.containsKey(ch)) {
			map.get(id).println("�̹� �����ϴ� ä���Դϴ�.");
			map.get(id).flush();
		}else {
			channel.put(ch, new ArrayList<>());
			channel.get(ch).add(id);
			map.get(id).println("ä�� [" + ch + "]�� �����Ǿ����ϴ�.");
			map.get(id).flush();
			ta_chat.append(id + "���� ä�� [" + ch + "]�� �����Ͽ����ϴ�.\n");
			
			chSync();
		}
	}
	
	void joinChannel(String ch) {
		if(channel.containsKey(ch)) {
			channel.get(ch).add(id);
			map.get(id).println("[" + ch + "]�� �����Ͽ����ϴ�.");
			map.get(id).flush();
			ta_chat.append(id + "���� [" + ch + "]�� �����Ͽ����ϴ�.\n");
			
			chSync();
		}else {
			map.get(id).println("�������� �ʴ� ä���Դϴ�.");
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
			ta_chat.append("[" + id + "���� " + wis[1] + "�Կ��� ���� �ӼӸ�] : " + wmsg + "\n");
			map.get(id).println("[" + wis[1] + "�Կ��� ���� �ӼӸ�] : " + wmsg);
			map.get(id).flush();
			map.get(wis[1]).println("[" + id + "���� �ӼӸ�] : " + wmsg);
			map.get(wis[1]).flush();
		}else {
			map.get(id).println("������ �������� �ʽ��ϴ�.");
			map.get(id).flush();
		}
	}
	
	void chSync() {
		String chlist = "";
		for (Map.Entry<String, ArrayList<String>> entry : channel.entrySet()) {
			chlist += entry.getKey() + "," + entry.getValue().get(0) + "," + entry.getValue().size() + "||";
		}
		if(!chlist.equals("")) {
			chlist = chlist.substring(0, chlist.lastIndexOf("||"));
		}
		
		broadcast("<����> : /chlistsync " + chlist);
	}
	
	void userSync() {
		String userlist = "";
		for (int i = 0; i < modelUser.getRowCount(); i++) {
			userlist += modelUser.getValueAt(i, 0) + " ";
		}
		if(!userlist.equals("")) {
			userlist = userlist.substring(0, userlist.lastIndexOf(" "));
		}
		broadcast("<����> : /userlistsync " + userlist);
	}
	
	//// ������ - �ڸ����
	void awayfromkeyboard() {
		for (int i = 0; i < modelUser.getRowCount(); i++) {
			if(modelUser.getValueAt(i, 0).equals(id)) {
				modelUser.setValueAt(id+"_�ڸ����", i, 0);
			}
		}
		userSync();
	}
	
	void backtokeyboard() {
		for (int i = 0; i < modelUser.getRowCount(); i++) {
			if(modelUser.getValueAt(i, 0).equals(id+"_�ڸ����")) {
				modelUser.setValueAt(id, i, 0);
			}
		}
		userSync();
	}
	////
	
	/////�����-ģ���߰�
	void friendAdd(String fd){
		if(id.equals(fd)){
			map.get(id).println("�߸��� �Է��Դϴ�.");
			map.get(id).flush();
		}else if(map.containsKey(fd)) {
			int result = JOptionPane.showConfirmDialog(null, "["+id+"]���� ģ����û�� �����ðڽ��ϱ�?","ģ����û",JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION){
				map.get(id).println("[ "+fd+" ]�԰� ģ���� �Ǿ����ϴ�");
				map.get(id).flush();
				map.get(fd).println("[ "+id+" ]�԰� ģ���� �Ǿ����ϴ�");
				map.get(fd).flush();
			}else{
				map.get(id).println("[ "+fd+" ]���� ģ������ �Ͽ����ϴ�");
				map.get(id).flush();
				map.get(fd).println("[ "+id+" ]�Ը� ģ������ �߽��ϴ�");
				map.get(fd).flush();
				}
			}else {
				map.get(id).println("������ �������� �ʽ��ϴ�.");
				map.get(id).flush();
			}
		}
	/////ģ�����
	void friendlist(){
		map.get(id).println("ģ�����");
		map.get(id).flush();
	}
	/////
	
	///// ������ - ���ܱ��
	void isBlockManager(String blockState, String blockId) {
		if(id.equals(blockId)) {	// �ڱ� �ڽ��� �����Ϸ�������
			map.get(id).println("�ڽ��� ������ �� �����ϴ�.");
		} else if(map.containsKey(blockId)) {	// ���� ��밡 �����Ҷ�
			String state = blockState.equals("/block")?"<����>/block ":"<����>/unblock ";
			map.get(id).println(state + blockId);
			ta_chat.append("["+id+"]���� ["+blockId+"]���� "+(state.equals("/block ")?"�����Ͽ����ϴ�.\n":"���������Ͽ����ϴ�.\n"));
		} else {	// ���� ��밡 �������� ������
			map.get(id).println("������ �������� �ʽ��ϴ�.");
		}
		map.get(id).flush();
	}
	/////
	
	@Override
	public void run() {
		try {
			userSync();
			String msg = null;
			while((msg = in.readLine()) != null) {
				if(msg.equals("/quit")) {
					break;
				}else if(msg.indexOf("/w ") == 0) {
					whisper(msg);
				}else if(msg.indexOf("/create ") == 0) {
					createChannel(msg.split(" ")[1]);
				}else if(msg.indexOf("/join ") == 0) {
					joinChannel(msg.split(" ")[1]);
				}else if(channel.containsKey(msg.split(" ")[0].replace("/", ""))) {
					chBroadcast(msg);
				}else if(msg.equals("/help")){
			               map.get(id).println("/create : ä�λ���\n"
			               		+ "/join : ä�ΰ���\n"
			               		+ "/w : �ӼӸ�\n"
			               		+ "/fa : ģ���߰�\n"
			               		+ "/fl : ģ�����\n"
			               		+ "/block : ����\n"
			               		+ "/unblock : ��������\n"
			               		+ "/blocklist : ���ܸ��\n");
			               map.get(id).flush();
				//// ������ - �ڸ����
				}else if(msg.equals("/afk")) {
					awayfromkeyboard();
				}
				else if(msg.equals("/back")){
					backtokeyboard();
				////
					
				/////�����-ģ���߰�
				}else if(msg.indexOf("/fa ") == 0){
					friendAdd(msg.split(" ")[1]);
				/////ģ�����
				}else if(msg.equals("/fl")){
					friendlist();
				/////
					
				///// ������ - ���ܱ��
				}else if(msg.indexOf("/block ") == 0 || msg.indexOf("/unblock ") == 0) {
					isBlockManager(msg.split(" ")[0].equals("/block")?"/block":"/unblock", msg.split(" ")[1]);
				}else if(msg.equals("/blocklist")) {
					map.get(id).println("<����>/blocklist");
					map.get(id).flush();
				/////
				}else {
					ta_chat.append("[" + id + "] : " + msg + "\n");
					broadcast("[" + id + "] : " + msg);	// �ٸ� Ŭ���̾�Ʈ�� ������ ���� ������ ������ �о ������ ��ο��� ����
				}
			}
			map.remove(id);
			ta_chat.append("[" + id + "] ���� �����ϼ̽��ϴ�.\n");
			for (int i = 0; i < modelUser.getRowCount(); i++) {
				if(modelUser.getValueAt(i, 0).equals(id) || modelUser.getValueAt(i, 0).equals(id+"_�ڸ����")) {	//// ������ - �ڸ���� ���¿����� ������ �ֵ��� ���� �߰�
					modelUser.removeRow(i);
					break;
				}
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
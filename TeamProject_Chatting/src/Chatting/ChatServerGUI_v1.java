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

	void init() { // ui설정
		this.setSize(500, 500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		if(tf.getText().equals("/quit")){
			System.exit(0);
		}else {
			ta_chat.append("<서버> : " + tf.getText() + "\n");
			broadcast("<서버> : " + tf.getText());
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
	HashMap<String, ArrayList<String>> channel;

	PrintWriter out;
	BufferedReader in;
	String id;
	///// 이재봉
	String fmsgs;
	/////

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
			ta_chat.append("[" + id + "] 님이 접속 했습니다.\n");

			map.put(id, out);
			
			map.get(id).println("Chat서버에 오신것을 환영합니다.\n(명령어확인 : /help)");
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
		String[] ch = msg.split("%#%");
		if(ch[1].indexOf("/sync") == 0) {
			Iterator<String> iter = channel.get(ch[0]).iterator();
			while(iter.hasNext()) {
				String chid = iter.next();
				map.get(chid).println("@" + ch[0].replace("/", "") + "%#%" + ch[1]);
				map.get(chid).flush();
			}
		}else {
			if(channel.get(ch[0]).contains(id)) {
				Iterator<String> iter = channel.get(ch[0]).iterator();
				while(iter.hasNext()) {
					String chid = iter.next();
					map.get(chid).println("@" + ch[0].replace("/", "") + "%#%[" + id + "] : " + ch[1]);
					map.get(chid).flush();
				}
				ta_chat.append("@" + ch[0].replace("/", "") + "[" + id + "] : " + ch[1] + "\n");
			}else {
				map.get(id).println("미가입 채널 입니다.");
				map.get(id).flush();
			}
		}
	}
	
	void createChannel(String ch) {
		if(channel.containsKey(ch)) {
			map.get(id).println("이미 존재하는 채널입니다.");
			map.get(id).flush();
		}else {
			ch = ch.replace("@", "");
			channel.put(ch, new ArrayList<>());
			channel.get(ch).add(id);
			map.get(id).println("채널 [" + ch.replace("/", "") + "]가 생성되었습니다.");
			map.get(id).flush();
			ta_chat.append(id + "님이 채널 [" + ch.replace("/", "") + "]을 생성하였습니다.\n");
			map.get(id).println("<서버>/chopen " + ch.replace("/", ""));
			map.get(id).flush();
			chSync();
		}
	}
	
	void joinChannel(String ch) {
		if(channel.containsKey(ch)) {
			if(channel.get(ch).contains(id)) {
				map.get(id).println("이미 가입된 채널입니다.");
				map.get(id).flush();
			}else {
				channel.get(ch).add(id);
				map.get(id).println("[" + ch.replace("/", "") + "]에 입장하였습니다.");
				map.get(id).flush();
				ta_chat.append(id + "님이 [" + ch.replace("/", "") + "]에 입장하였습니다.\n");
				map.get(id).println("<서버>/chopen " + ch.replace("/", ""));
				map.get(id).flush();
				chSync();
			}
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
	
	void chSync() {
		String chlist = "";
		for (Map.Entry<String, ArrayList<String>> entry : channel.entrySet()) {
			String chuser = "";
			chlist += entry.getKey().replace("/", "") + "%@%" + entry.getValue().get(0) + "%@%" + entry.getValue().size() + "%!%";
			for (int i = 0; i < entry.getValue().size(); i++) {
				chuser += entry.getValue().get(i) + " ";
			}
			if(!chuser.equals("")) {
				chuser = chuser.substring(0, chuser.lastIndexOf(" "));
			}
			chBroadcast(entry.getKey() + "%#%/sync " + chuser);
		}
		if(!chlist.equals("")) {
			chlist = chlist.substring(0, chlist.lastIndexOf("%!%"));
		}
		
		broadcast("<서버>/chlistsync " + chlist);
	}
	
	void userSync() {
		String userlist = "";
		for (int i = 0; i < modelUser.getRowCount(); i++) {
			userlist += modelUser.getValueAt(i, 0) + " ";
		}
		if(!userlist.equals("")) {
			userlist = userlist.substring(0, userlist.lastIndexOf(" "));
		}
		broadcast("<서버>/userlistsync " + userlist);
	}
	
	void chClose(String ch) {
		if(channel.get(ch).get(0) == id) {
			channel.remove(ch);
			ta_chat.append("채널" + "[" + ch.replace("/", "") + "]이 종료되었습니다.\n");
		}
		chSync();
	}
	
	void chExit(String ch) {
		chBroadcast(ch + "%#%" + id + "님이 퇴장하셨습니다.");
		channel.get(ch).remove(id);
		ta_chat.append(id + "님이" + "[" + ch.replace("/", "") + "]에서 퇴장하였습니다.\n");
		if(channel.get(ch).size() == 0) {
			channel.remove(ch);
			ta_chat.append("채널" + "[" + ch.replace("/", "") + "]이 종료되었습니다.\n");
		}
		chSync();
	}
	
	void chKick(String msg) {
		String ch = msg.split("%#%")[0];
		String kick = msg.split("%#%")[1];
		kick = kick.replace("/kick ", "");
		if(channel.get(ch).get(0) == id) {
			if(id.equals(kick)) {
				map.get(id).println("@" + ch.replace("/", "") + "%#% 자신은 강퇴할수 없습니다.");
				map.get(id).flush();
			}else {
				for (int i = 1; i < channel.get(ch).size(); i++) {
					if(channel.get(ch).get(i).equals(kick)) {
						channel.get(ch).remove(i);
						chBroadcast(ch + "%#%" + kick + "님이 강퇴당하셨습니다.");
						break;
					}
				}
			}
		}
		chSync();
	}
	
	//// 전준형 - 자리비움
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
	////
	
	/////이재봉-친구추가
	void friendAdd(String fd){
		if(id.equals(fd)){
			map.get(id).println("잘못된 입력입니다.");
			map.get(id).flush();
		}else if(map.containsKey(fd)) {
			int result = JOptionPane.showConfirmDialog(null, "["+id+"]님의 친구신청을 받으시겠습니까?","친구신청",JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION){
				map.get(id).println("[ "+fd+" ]님과 친구가 되었습니다");
				map.get(id).flush();
				map.get(fd).println("[ "+id+" ]님과 친구가 되었습니다");
				map.get(fd).flush();
			}else{
				map.get(id).println("[ "+fd+" ]님이 친구거절 하였습니다");
				map.get(id).flush();
				map.get(fd).println("[ "+id+" ]님를 친구거절 했습니다");
				map.get(fd).flush();
				}
			}else {
				map.get(id).println("상대방이 존재하지 않습니다.");
				map.get(id).flush();
			}
		}
	/////친구목록
	void friendlist(){
		map.get(id).println("친구목록");
		map.get(id).flush();
	}
	/////친구메시지
	void friendMessage(String msg){
		String[] msgs = msg.split(" ");
		String  fmsg = " ";
		for (int i = 1; i < msgs.length; i++) {
			fmsg += msgs[i];
			if(i < msgs.length-1) {
				fmsg += " ";
			}
		}
		map.get(id).println("/fm");
		map.get(id).flush();
		fmsgs = fmsg;
		}
	void returnFriend(String msg){
			ta_chat.append("[" + id + "]님이 친구들에게 보낸 귓속말 : "+fmsgs+"\n");
			map.get(id).println("친구들에게 보낸 귓속말] : " + fmsgs);
			map.get(id).flush();
			for(int i = 1; i < msg.split(" ").length; i++){
			map.get(msg.split(" ")[i]).println("[" + id + "님의 귓속말] : "+fmsgs);
			map.get(msg.split(" ")[i]).flush();
			}
	}
	/////
	
	///// 윤성록 - 차단기능
	void isBlockManager(String blockState, String blockId) {
		if(id.equals(blockId)) {	// 자기 자신을 차단하려했을때
			map.get(id).println("자신을 차단할 순 없습니다.");
		} else if(map.containsKey(blockId)) {	// 차단 상대가 존재할때
			String state = blockState.equals("/block")?"<서버>/block ":"<서버>/unblock ";
			map.get(id).println(state + blockId);
			ta_chat.append("["+id+"]님이 ["+blockId+"]님을 "+(state.equals("<서버>/block ")?"차단하였습니다.\n":"차단해제하였습니다.\n"));
		} else {	// 차단 상대가 존재하지 않을때
			map.get(id).println("상대방이 존재하지 않습니다.");
		}
		map.get(id).flush();
	}
	/////
	
	@Override
	public void run() {
		try {
			userSync();
			chSync();
			String msg = null;
			while((msg = in.readLine()) != null) {
				if(msg.equals("/quit")) {
					break;
				}else if(msg.indexOf("/w ") == 0) {
					whisper(msg);
				}else if(msg.indexOf("/create ") == 0) {
					createChannel("/" + msg.split(" ")[1]);
				}else if(msg.indexOf("/join ") == 0) {
					joinChannel("/" + msg.split(" ")[1]);
				}else if(channel.containsKey(msg.split("%#%")[0])) {
					String[] ch = msg.split("%#%");
					if(ch[1].equals("/sync")) {
						chSync();
					}else if(ch[1].equals("/close")) {
						chClose(ch[0]);
					}else if(ch[1].indexOf("/kick") == 0) {
						chKick(msg);
					}else if(ch[1].equals("/exit")) {
						chExit(ch[0]);
					}else {
						chBroadcast(msg);
					}
				}else if(msg.equals("/help")){
			               map.get(id).println("/create : 채널생성\n"
			               		+ "/join : 채널가입\n"
			               		+ "/w : 귓속말\n"
			               		+ "/fa : 친구추가\n"
			               		+ "/fl : 친구목록\n"
			               		+ "/block : 차단\n"
			               		+ "/unblock : 차단해제\n"
			               		+ "/blocklist : 차단목록\n");
			               map.get(id).flush();
				//// 전준형 - 자리비움
				}else if(msg.equals("/afk")) {
					awayfromkeyboard();
				}
				else if(msg.equals("/back")){
					backtokeyboard();
				////
					
				/////이재봉-친구추가
				}else if(msg.indexOf("/fa ") == 0){
					friendAdd(msg.split(" ")[1]);
				/////친구목록
				}else if(msg.equals("/fl")){
					friendlist();
				/////친구메시지
				}else if(msg.indexOf("/fm ") == 0){
					friendMessage(msg);
				}else if(msg.indexOf("/rf ") == 0 ){
					returnFriend(msg);
				/////
					
				///// 윤성록 - 차단기능
				}else if(msg.indexOf("/block ") == 0 || msg.indexOf("/unblock ") == 0) {
					isBlockManager(msg.split(" ")[0].equals("/block")?"/block":"/unblock", msg.split(" ")[1]);
				}else if(msg.equals("/blocklist")) {
					map.get(id).println("<서버>/blocklist");
					map.get(id).flush();
				/////
				}else {
					ta_chat.append("[" + id + "] : " + msg + "\n");
					broadcast("[" + id + "] : " + msg);	// 다른 클라이언트가 서버로 글을 보내면 서버가 읽어서 접속한 모두에게 보냄
				}
				ta_chat.setCaretPosition(ta_chat.getDocument().getLength());
			}
			map.remove(id);
			ta_chat.append("[" + id + "] 님이 퇴장하셨습니다.\n");
			for (int i = 0; i < modelUser.getRowCount(); i++) {
				if(modelUser.getValueAt(i, 0).equals(id) || modelUser.getValueAt(i, 0).equals(id+"_자리비움")) {	//// 전준형 - 자리비움 상태에서도 나갈수 있도록 조건 추가
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
package Chatting;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ChatClientGUI_v1 extends JFrame implements ActionListener, WindowListener {
	JTextArea ta;
	JTextField tf;
	JButton btnSent, btnMake;
	JTable tblCh, tblUser;
	DefaultTableModel modelCh, modelUser;
	PrintWriter out;
	BufferedReader in;
	Socket socket;
	String id;
	//// 전준형 - 자리비움
	ClientThreadAFK afk; // afk 쓰레드
	boolean is_afk = false;	//현재 자리비움인지 아닌지
	////
	
	
	///// 이재봉 - 친구 리스트
	ArrayList<String> friendlist;
	/////

	public ChatClientGUI_v1() {
		init();
		clientSetting();
	}

	void init() {
		this.setSize(500, 500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("클라이언트");
		this.addWindowListener(this);

		JPanel pnlCenter = new JPanel(new GridLayout(0, 1));
		String[] hdList = {"방제목", "방장", "현재인원"};
		String[][] ctList = {};
		modelCh = new DefaultTableModel(ctList, hdList){
			public boolean isCellEditable(int i, int c){ 
				return false; 
				} 
			};
		tblCh = new JTable(modelCh);
		JScrollPane sp_list = new JScrollPane(tblCh);
		JPanel pnlChat = new JPanel(new BorderLayout());
		ta = new JTextArea("===채팅내용===\n");
		ta.setEditable(false);
		JScrollPane sp_ta = new JScrollPane(ta);
		JPanel pnlTf = new JPanel(new BorderLayout());
		tf = new JTextField();
		tf.addActionListener(this);
		JPanel pnlBtn = new JPanel(new GridLayout(1, 0));
		btnSent = new JButton("보내기");
		btnSent.addActionListener(this);
		btnMake = new JButton("입장");
		btnMake.addActionListener(this);
		pnlBtn.add(btnSent);
		pnlBtn.add(btnMake);
		
		pnlTf.add(tf);
		pnlTf.add(pnlBtn, "East");

		pnlChat.add(sp_ta);
		pnlChat.add(pnlTf, "South");
		
		pnlCenter.add(sp_list);
		pnlCenter.add(pnlChat);
		
		String[] hdUser = {"접속자"};
		String[][] ctUser = {};
		modelUser = new DefaultTableModel(ctUser, hdUser){
			public boolean isCellEditable(int i, int c){ 
				return false; 
				} 
			};
		tblUser = new JTable(modelUser);
		JScrollPane sp_user = new JScrollPane(tblUser);
		sp_user.setPreferredSize(new Dimension(100, 400));
		
		this.add(pnlCenter);
		this.add(sp_user, "East");
		
		this.setVisible(true);
	}

	void clientSetting() {
		String ip = JOptionPane.showInputDialog("접속할 IP 입력", "127.0.0.1");
		id = JOptionPane.showInputDialog("아이디  입력\n(띄어쓰기 적용X)");
		
		///// 이재봉
		friendlist = new ArrayList<>();
		/////
		try {
			socket = new Socket(ip, 5000);
			out = new PrintWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println(id);
			out.flush();
			
			(new ClientThreadGUI(in, out, ta, socket, id, modelUser, modelCh, friendlist)).start();
			// 서버가 보내는 내용을 계속 받기 위한 스레드 실행
			
			//// 전준형 - 자리비움
			afk = new ClientThreadAFK(out, is_afk, this);
			afk.start();
			//// afk쓰레드 시작
		} catch (IOException e) {
			ta.append("접속에 실패하였습니다.\n");
			for (int i = 5; i > 0; i--) {
				try {
					ta.append(i + "초 후 종료됩니다...\n");
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			System.exit(0);
		}
	}
	
	void makeRoom() {
		if(tblCh.getSelectedRow() > -1) {
			String ch = tblCh.getValueAt(tblCh.getSelectedRow(), 0) + "";
			out.println("/join " + ch);
			out.flush();
		}else {
			int result = JOptionPane.showConfirmDialog(this, "방이 선택되지 않았습니다.\n방을 생성하시겠습니까?", "Confirm", JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION) {
				String title = JOptionPane.showInputDialog("방제목 입력");
				out.println("/create " + title);
				out.flush();
			}
		}
	}

	public static void main(String[] args) {
		new ChatClientGUI_v1();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnMake) {
			makeRoom();
		}else {
			if(out != null) {
				out.println(tf.getText());
				out.flush();
				
				if(tf.getText().equals("/quit")){
					System.exit(0);
				}
				
				tf.setText("");
			}
		}
		
		//// 전준형 - 자리비움
		if(is_afk) {
			out.println("/back");
			out.flush();
			is_afk = false;
			afk.afk_count = 0;
			afk.is_afk = false;
		} else {
			afk.afk_count = 0;
		}
		//// 무슨 행동이든 했으면 자리비움 해제하고 자리비움 카운트 0으로 만들기
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if(out != null) {
			out.println("/quit");
			out.flush();
		}
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

class ClientThreadGUI extends Thread {
	BufferedReader in;
	PrintWriter out;
	JTextArea ta;
	Socket socket;
	String id;
	DefaultTableModel modelCh, modelUser;
	HashMap<String, JTextArea> chta;
	HashMap<String, DefaultTableModel> chuser;
	
	///// 이재봉
	ArrayList<String> friendlist;
	/////
	
	///// 윤성록 - 차단기능
	ArrayList<String> blockList = new ArrayList<>();	// 차단 목록
	/////

	public ClientThreadGUI(BufferedReader in, PrintWriter out, JTextArea ta, Socket socket, String id, DefaultTableModel modelUser, DefaultTableModel modelCh, ArrayList<String> friendlist) {
		this.in = in;
		this.out = out;
		this.ta = ta;
		this.socket = socket;
		this.id = id;
		this.modelCh = modelCh;
		this.modelUser = modelUser;
		
		chta = new HashMap<>();
		chuser = new HashMap<>();
		
		///// 이재봉
		this.friendlist = friendlist;
		/////
		
	}

	@Override
	public void run() {
		String msg = null;
		try {
			while((msg = in.readLine()) != null) {
				if(msg.equals("<서버> : /kick " + id)) {
					out.println("/quit");
					out.flush();
					ta.append("강퇴당하셨습니다.\n");
					break;
				}else if(msg.equals("<서버> : /quit"))  {
					ta.append("서버가 종료되었습니다.\n");
					break;
				}else if(msg.indexOf("<서버>/chlistsync") == 0) {
					modelCh.setRowCount(0);
					msg = msg.replace("<서버>/chlistsync ", "");
					if(msg.indexOf("%!%") > -1) {
						String[] list = msg.split("%!%");
						for (int i = 0; i < list.length; i++) {
							String[] ch = list[i].split("%@%");
							modelCh.addRow(ch);
						}
					}else if(msg.indexOf("%@%") > -1) {
						String[] ch = msg.split("%@%");
						modelCh.addRow(ch);
					} 
				}else if(msg.indexOf("<서버>/userlistsync") == 0) {
					modelUser.setRowCount(0);
					msg = msg.replace("<서버>/userlistsync", "");
					String[] list = msg.split(" ");
					for (int i = 1; i < list.length; i++) {
						modelUser.addRow(new String[]{list[i]});
					}
				}else if(msg.indexOf("<서버>/chopen ") == 0) {
					msg = msg.replace("<서버>/chopen ", "");
					chta.put(msg, new JTextArea());
					DefaultTableModel tempmodel = new DefaultTableModel((new String[][]{}), (new String[]{"접속자"})){
						public boolean isCellEditable(int i, int c){ 
							return false; 
							}
						};
					chuser.put(msg, tempmodel);
					new PopRoom(msg, out, id, chta.get(msg), chuser.get(msg));
					out.println("/" + msg + "%#%/sync");
				}else if(msg.indexOf("@") == 0) {
					String ch = msg.split("%#%")[0];
					ch = ch.replace("@", "");
					msg = msg.replace("%#%", "");
					if(msg.indexOf("@" + ch + "/sync") == 0) {
						chuser.get(ch).setRowCount(0);
						String[] list = msg.replace("@" + ch + "/sync ", "").split(" ");
						for (int i = 0; i < list.length; i++) {
							chuser.get(ch).addRow(new String[]{list[i]});
						}
					}else if(msg.indexOf("@" + ch + "/close") == 0) {
					// 클로즈되면 팝업창 닫기
					}else {
						chta.get(ch).append(msg.replace("@" + ch, "") + "\n");
					}
				///// 이재봉 - 친구추가
				}else if(msg.split(" ")[0].equals("[")&&msg.split(" ")[2].equals("]님과")){
					friendlist.add(msg.split(" ")[1]);
					ta.append(msg + "\n");
					ta.setCaretPosition(ta.getDocument().getLength());
				/////친구목록
				}else if(msg.equals("친구목록")){	
					String s;
					ta.append("친구목록: \n");
					Iterator<String> e = friendlist.iterator();
					while(e.hasNext()){
						s = e.next();
						ta.append(s+"\n");
					}
				/////친구메시지
				}else if(msg.equals("/fm")){
					String s = "";
					Iterator<String> e = friendlist.iterator();
					while(e.hasNext()){
						s += e.next()+" ";
					}
					out.println("/rf "+ s);
					out.flush();
				/////
					
				///// 윤성록 - 차단기능
				}else if(msg.indexOf("<서버>/blocklist") == 0) {
					String isBlockList = "";
					if(!blockList.isEmpty()) {
						for(int i=0; i<blockList.size(); i++) {
							isBlockList += blockList.get(i) + ", ";
						}
						isBlockList = isBlockList.substring(0, isBlockList.lastIndexOf(", "));
					} 
					ta.append("차단명단 : " + isBlockList + "\n");
				}
				else if(msg.indexOf("<서버>/block ") == 0 || msg.indexOf("<서버>/unblock ") == 0) {
					String blockId = msg.indexOf("<서버>/block")==0?msg.split(" ")[1]:null;
					ta.append("["+msg.split(" ")[1]+"]님을 "+(blockId!=null?"차단하였습니다.\n":"차단해제하였습니다.\n"));
					if(blockId!=null) blockList.add("["+msg.split(" ")[1]+"]");
					else blockList.remove("["+msg.split(" ")[1]+"]");
				}
				else if(blockList.contains(msg.split("님")[0] + "]")) { 
					continue;	// 차단자가 귓속말을 했을때 내 채팅창에 안보이게
				}
				else if(blockList.contains(msg.split(" ")[0])) {
					continue;	// 차단자가 전체채팅을 했을때 내 채팅창에 안보이게 
				/////
				}else {
					ta.append(msg + "\n");
				}
				ta.setCaretPosition(ta.getDocument().getLength());	// 글씨가 자동으로 추가될 때 스크롤바가 안움직이는거 수정
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(in != null) {
					in.close();
				}
				if(out != null) {
					out.close();
				}
				if(socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (int i = 5; i > 0; i--) {
			try {
				ta.append(i + "초 후 종료됩니다...\n");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.exit(0);
	}
}

class PopRoom extends JDialog implements ActionListener, WindowListener {
	String title;
	DefaultTableModel modelChUser;
	JTable tblChUser;
	JTextArea ta;
	JTextField tf;
	JButton btn;
	PrintWriter out;
	String id;
	
	public PopRoom(String title, PrintWriter out, String id, JTextArea ta, DefaultTableModel modelChUser) {
		this.title = title;
		this.out = out;
		this.id = id;
		this.ta = ta;
		this.modelChUser = modelChUser;
		init();
	}
	
	void init() {
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setSize(500, 500);
		this.setTitle(title);
		this.addWindowListener(this);
		
		ta.setEditable(false);
		
		tblChUser = new JTable(modelChUser);
		JScrollPane sp = new JScrollPane(tblChUser);
		sp.setPreferredSize(new Dimension(100, 400));
		
		JPanel pnl = new JPanel(new BorderLayout());
		tf = new JTextField();
		tf.addActionListener(this);
		btn = new JButton("보내기");
		btn.addActionListener(this);
		pnl.add(tf);
		pnl.add(btn, "East");
		
		this.add(ta);
		this.add(sp, "East");
		this.add(pnl, "South");
		
		this.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		out.println("/" + title + "%#%" + tf.getText());
		out.flush();
		
		if(tf.getText().equals("/exit")){
			this.dispose();
		}
		
		tf.setText("");
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		out.println("/" + title + "%#%/exit");
		out.flush();
		this.dispose();
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

//// 전준형 - 자리비움
class ClientThreadAFK extends Thread {
	PrintWriter out;
	int afk_count;
	boolean is_afk;
	ChatClientGUI_v1 main;
	
	public ClientThreadAFK(PrintWriter out, boolean is_afk, ChatClientGUI_v1 main) {
		this.out = out;
		this.is_afk = is_afk;
		this.main = main;
		afk_count = 0;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(1000);
				if(!is_afk) {	
					afk_count++;
					if(afk_count == 3600) {
						is_afk = true;
						main.is_afk = true;
						out.println("/afk");
						out.flush();
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
////
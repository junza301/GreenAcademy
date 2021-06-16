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
	JTable tblList, tblUser;
	DefaultTableModel modelList, modelUser;
	PrintWriter out;
	BufferedReader in;
	Socket socket;
	String id;

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
		modelList = new DefaultTableModel(ctList, hdList);
		tblList = new JTable(modelList);
		JScrollPane sp_list = new JScrollPane(tblList);
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
		btnMake = new JButton("방생성");
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
		modelUser = new DefaultTableModel(ctUser, hdUser);
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
		try {
			socket = new Socket(ip, 5000);
			out = new PrintWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println(id);
			out.flush();
			
			(new ClientThreadGUI(in, out, ta, socket, id, modelUser)).start();
			// 서버가 보내는 내용을 계속 받기 위한 스레드 실행
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			ta.append("접속에 실패하였습니다.\n");
			for (int i = 5; i > 0; i--) {
				try {
					ta.append(i + "초 후 종료됩니다...\n");
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			System.exit(0);
		}
	}
	
	void makeRoom() {
		String title = JOptionPane.showInputDialog("방제목 입력");
		modelList.addRow(new String[]{title, id, "1"});
		new PopRoom(this, title);
	}

	public static void main(String[] args) {
		new ChatClientGUI_v1();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnMake) {
			makeRoom();
		}else {
			out.println(tf.getText());
			out.flush();
			
			if(tf.getText().equals("/quit")){
				System.exit(0);
			}
			
			tf.setText("");
		}
//		tf.selectAll();
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
	DefaultTableModel modelUser;

	public ClientThreadGUI(BufferedReader in, PrintWriter out, JTextArea ta, Socket socket, String id, DefaultTableModel modelUser) {
		this.in = in;
		this.out = out;
		this.ta = ta;
		this.socket = socket;
		this.id = id;
		this.modelUser = modelUser;
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
				}else if(msg.indexOf("<서버> : /userlistsync") == 0) {
					modelUser.setRowCount(0);
					msg = msg.replace("<서버> : /userlistsync", "");
					String[] list = msg.split(" ");
					for (int i = 1; i < list.length; i++) {
						modelUser.addRow(new String[]{list[i]});
					}
				}else {
					ta.append(msg + "\n");
					ta.setCaretPosition(ta.getDocument().getLength());	// 글씨가 자동으로 추가될 때 스크롤바가 안움직이는거 수정
				}
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

class PopRoom extends JDialog implements ActionListener {
	ChatClientGUI_v1 frame;
	String title;
	
	public PopRoom(ChatClientGUI_v1 frame, String title) {
		super(frame, true);
		this.frame = frame;
		this.title = title;
		init();
	}
	
	void init() {
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setSize(500, 500);
		this.setTitle(title);
		
		this.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
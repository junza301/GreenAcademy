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
	//// ������ - �ڸ����
	ClientThreadAFK afk; // afk ������
	boolean is_afk = false;	//���� �ڸ�������� �ƴ���
	////
	
	
	
	///// ����� - ģ�� ����Ʈ
	ArrayList<String> friendlist;
	/////

	public ChatClientGUI_v1() {
		init();
		clientSetting();
	}

	void init() {
		this.setSize(500, 500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Ŭ���̾�Ʈ");
		this.addWindowListener(this);

		JPanel pnlCenter = new JPanel(new GridLayout(0, 1));
		String[] hdList = {"������", "����", "�����ο�"};
		String[][] ctList = {};
		modelCh = new DefaultTableModel(ctList, hdList){
			public boolean isCellEditable(int i, int c){ 
				return false; 
				} 
			};
		tblCh = new JTable(modelCh);
		JScrollPane sp_list = new JScrollPane(tblCh);
		JPanel pnlChat = new JPanel(new BorderLayout());
		ta = new JTextArea("===ä�ó���===\n");
		ta.setEditable(false);
		JScrollPane sp_ta = new JScrollPane(ta);
		JPanel pnlTf = new JPanel(new BorderLayout());
		tf = new JTextField();
		tf.addActionListener(this);
		JPanel pnlBtn = new JPanel(new GridLayout(1, 0));
		btnSent = new JButton("������");
		btnSent.addActionListener(this);
		btnMake = new JButton("����");
		btnMake.addActionListener(this);
		pnlBtn.add(btnSent);
		pnlBtn.add(btnMake);
		
		pnlTf.add(tf);
		pnlTf.add(pnlBtn, "East");

		pnlChat.add(sp_ta);
		pnlChat.add(pnlTf, "South");
		
		pnlCenter.add(sp_list);
		pnlCenter.add(pnlChat);
		
		String[] hdUser = {"������"};
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
		String ip = JOptionPane.showInputDialog("������ IP �Է�", "127.0.0.1");
		id = JOptionPane.showInputDialog("���̵�  �Է�\n(���� ����X)");
		
		///// �����
		friendlist = new ArrayList<>();
		/////
		try {
			socket = new Socket(ip, 5000);
			out = new PrintWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println(id);
			out.flush();
			
			(new ClientThreadGUI(in, out, ta, socket, id, modelUser, modelCh, friendlist)).start();
			// ������ ������ ������ ��� �ޱ� ���� ������ ����
			
			//// ������ - �ڸ����
			afk = new ClientThreadAFK(out, is_afk, this);
			afk.start();
			//// afk������ ����
		} catch (IOException e) {
			ta.append("���ӿ� �����Ͽ����ϴ�.\n");
			for (int i = 5; i > 0; i--) {
				try {
					ta.append(i + "�� �� ����˴ϴ�...\n");
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
			int result = JOptionPane.showConfirmDialog(this, "���� ���õ��� �ʾҽ��ϴ�.\n���� �����Ͻðڽ��ϱ�?", "Confirm", JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION) {
				String title = JOptionPane.showInputDialog("������ �Է�");
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
		
		//// ������ - �ڸ����
		if(is_afk) {
			out.println("/back");
			out.flush();
			is_afk = false;
			afk.afk_count = 0;
			afk.is_afk = false;
		} else {
			afk.afk_count = 0;
		}
		//// ���� �ൿ�̵� ������ �ڸ���� �����ϰ� �ڸ���� ī��Ʈ 0���� �����
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
	
	///// �����
	ArrayList<String> friendlist;
	/////
	
	///// ������ - ���ܱ��
	ArrayList<String> blockList = new ArrayList<>();	// ���� ���
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
		
		///// �����
		this.friendlist = friendlist;
		/////
		
	}

	@Override
	public void run() {
		String msg = null;
		try {
			while((msg = in.readLine()) != null) {
				if(msg.equals("<����> : /kick " + id)) {
					out.println("/quit");
					out.flush();
					ta.append("������ϼ̽��ϴ�.\n");
					break;
				}else if(msg.equals("<����> : /quit"))  {
					ta.append("������ ����Ǿ����ϴ�.\n");
					break;
				}else if(msg.indexOf("<����>/chlistsync") == 0) {
					modelCh.setRowCount(0);
					msg = msg.replace("<����>/chlistsync ", "");
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
				}else if(msg.indexOf("<����>/userlistsync") == 0) {
					modelUser.setRowCount(0);
					msg = msg.replace("<����>/userlistsync", "");
					String[] list = msg.split(" ");
					for (int i = 1; i < list.length; i++) {
						modelUser.addRow(new String[]{list[i]});
					}
				}else if(msg.indexOf("<����>/chopen ") == 0) {
					msg = msg.replace("<����>/chopen ", "");
					chta.put(msg, new JTextArea());
					DefaultTableModel tempmodel = new DefaultTableModel((new String[][]{}), (new String[]{"������"})){
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
					// Ŭ����Ǹ� �˾�â �ݱ�
					}else {
						chta.get(ch).append(msg.replace("@" + ch, "") + "\n");
					}
				///// ����� - ģ���߰�
				}else if(msg.split(" ")[0].equals("[")&&msg.split(" ")[2].equals("]�԰�")){
					friendlist.add(msg.split(" ")[1]);
					ta.append(msg + "\n");
					ta.setCaretPosition(ta.getDocument().getLength());
				/////ģ�����
				}else if(msg.equals("ģ�����")){	
					String s;
					ta.append("ģ�����: \n");
					Iterator<String> e = friendlist.iterator();
					while(e.hasNext()){
						s = e.next();
						ta.append(s+"\n");
					}
				/////ģ���޽���
				}else if(msg.equals("/fm")){
					String s = "";
					Iterator<String> e = friendlist.iterator();
					while(e.hasNext()){
						s += e.next()+" ";
					}
					out.println("/rf "+ s);
					out.flush();
				/////
					
				///// ������ - ���ܱ��
				}else if(msg.indexOf("<����>/blocklist") == 0) {
					String isBlockList = "";
					if(!blockList.isEmpty()) {
						for(int i=0; i<blockList.size(); i++) {
							isBlockList += blockList.get(i) + ", ";
						}
						isBlockList = isBlockList.substring(0, isBlockList.lastIndexOf(", "));
					} 
					ta.append("���ܸ�� : " + isBlockList + "\n");
				}
				else if(msg.indexOf("<����>/block ") == 0 || msg.indexOf("<����>/unblock ") == 0) {
					String blockId = msg.indexOf("<����>/block")==0?msg.split(" ")[1]:null;
					ta.append("["+msg.split(" ")[1]+"]���� "+(blockId!=null?"�����Ͽ����ϴ�.\n":"���������Ͽ����ϴ�.\n"));
					if(blockId!=null) blockList.add("["+msg.split(" ")[1]+"]");
					else blockList.remove("["+msg.split(" ")[1]+"]");
				}
				else if(blockList.contains(msg.split("��")[0] + "]")) { 
					continue;	// �����ڰ� �ӼӸ��� ������ �� ä��â�� �Ⱥ��̰�
				}
				else if(blockList.contains(msg.split(" ")[0])) {
					continue;	// �����ڰ� ��üä���� ������ �� ä��â�� �Ⱥ��̰� 
				/////
				}else {
					ta.append(msg + "\n");
				}
				ta.setCaretPosition(ta.getDocument().getLength());	// �۾��� �ڵ����� �߰��� �� ��ũ�ѹٰ� �ȿ����̴°� ����
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
				ta.append(i + "�� �� ����˴ϴ�...\n");
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
		btn = new JButton("������");
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

//// ������ - �ڸ����
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
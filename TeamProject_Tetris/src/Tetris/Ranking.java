package Tetris;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyStore.Entry;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class Ranking extends JDialog implements ActionListener {
	JButton btn;
	DefaultTableModel model;
	JTable tbl;
	JPanel pnl;
	final String FILE_NAME = "Ranking.txt";

	public Ranking(Point xy) {
		init();
		setLocation(xy);
		ReadRank();
	}
	public Ranking(int score, String nickname, Point xy) {
		init();
		setLocation(xy);
		ReadRank();
		WriteRank(score,nickname);
	}

	public void init() {
		setTitle(" < RANKING >");
		setSize(300, 500);

		btn = new JButton("CLOSE");
		btn.setPreferredSize(new Dimension(0,36));
		btn.setFocusPainted(false);
		btn.addActionListener(this);

		String[] header = { "Rank", "ID", "Score" };
		String[][] contents = {};
		model = new DefaultTableModel(contents, header);
		tbl = new JTable(model);
		JScrollPane sp = new JScrollPane(tbl);
		pnl = new JPanel(new BorderLayout());
		pnl.add(sp, "Center");
		pnl.add(btn, "South");	
		this.add(pnl);

		// ���̺� ������ ���� cellRenderer ���� 
		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
		// ������ ��� ���ķ� ����
		cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		// ������ ���̺��� ColumnModel�� ������
		TableColumnModel tblAligenment = tbl.getColumnModel();
		// �ݺ����� �̿��Ͽ� ���̺��� ��� ���ķ� ����
		for (int i=0; i<tblAligenment.getColumnCount(); i++) 
			tblAligenment.getColumn(i).setCellRenderer(cellRenderer);

		tbl.setRowHeight(41);	// Row ũ�� ����
		tbl.setEnabled(false);	// cell Ŭ�� �ȵǰ� �ϱ�
		tbl.setShowVerticalLines(false);		// ���� �� �Ⱥ��̰� �ϱ�
		//tbl.setShowHorizontalLines(false);	// ���� �� �Ⱥ��̰� �ϱ�
		tbl.getTableHeader().setReorderingAllowed(false); 	// Header �̵� �Ұ�
		tbl.getTableHeader().setResizingAllowed(false); 	// Header ũ�� ���� �Ұ�
		
		// ���̺� �� �÷��� ���� ũ�� ���� (��ŷ�� ���ڸ����ϱ� �̸�,������ ���� �� ����)
		tbl.getColumnModel().getColumn(0).setPreferredWidth(80);
		tbl.getColumnModel().getColumn(1).setPreferredWidth(120);
		tbl.getColumnModel().getColumn(2).setPreferredWidth(120);
		
		this.setResizable(false);
		this.setVisible(true);
	}

	void ReadRank(){
		File f = new File("Ranking.txt");
		FileReader fr = null;
		BufferedReader br = null;
		String l = null;

		try{
			model.setRowCount(0);
			fr = new FileReader(f);
			br = new BufferedReader(fr);
			while((l=br.readLine())!=null){
				String[] str = l.split("/");
				model.addRow(str);
			}
		}catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	void WriteRank(int score, String nickname){
		File f = new File("Ranking.txt");
		FileWriter fw = null;
		PrintWriter pw = null;
		int cnt = 1;	// ��ũ ���� ����

		Map<String, Integer> map = new HashMap<>();
		for(int i=0; i<tbl.getRowCount(); i++) {
			// ReadRank()�� ���� �ҷ��� ���̺��� �������� �ʿ� ���
			map.put(tbl.getValueAt(i, 1)+"", Integer.parseInt(tbl.getValueAt(i, 2)+""));
		}
		map.put(nickname, score);	
		
		List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
		list.sort(Map.Entry.comparingByValue());	// �������� ����
		Collections.reverse(list);	// ��ŷ�� ���� �ٽ� ������������ ����
		
		try {
			fw = new FileWriter(f);
			pw = new PrintWriter(fw);

			model.setRowCount(0);
			for(int i=0; i<list.size(); i++) {
				String rank = cnt+"";
				String key = list.get(i).getKey();
				String value = list.get(i).getValue()+"";

				String[] total = {rank,key,value};
				model.addRow(total);	

				pw.println(rank+"/"+key+"/"+value);
				// ��ũ ������ ���Ͽ� ����

				if(cnt == 10)	// ��ũ 10�������
					break;
				cnt++;
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally{
			if(pw!=null){
				pw.close();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn) {
			dispose();
		}
	}
}
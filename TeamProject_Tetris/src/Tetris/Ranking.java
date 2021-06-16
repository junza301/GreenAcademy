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

		// 테이블 정렬을 위한 cellRenderer 생성 
		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
		// 정렬을 가운데 정렬로 지정
		cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		// 정렬할 테이블의 ColumnModel을 가져옴
		TableColumnModel tblAligenment = tbl.getColumnModel();
		// 반복문을 이용하여 테이블을 가운데 정렬로 지정
		for (int i=0; i<tblAligenment.getColumnCount(); i++) 
			tblAligenment.getColumn(i).setCellRenderer(cellRenderer);

		tbl.setRowHeight(41);	// Row 크기 조절
		tbl.setEnabled(false);	// cell 클릭 안되게 하기
		tbl.setShowVerticalLines(false);		// 세로 선 안보이게 하기
		//tbl.setShowHorizontalLines(false);	// 가로 선 안보이게 하기
		tbl.getTableHeader().setReorderingAllowed(false); 	// Header 이동 불가
		tbl.getTableHeader().setResizingAllowed(false); 	// Header 크기 조절 불가
		
		// 테이블 각 컬럼의 가로 크기 조절 (랭킹은 한자릿수니까 이름,점수에 비해 좀 좁게)
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
		int cnt = 1;	// 랭크 정렬 변수

		Map<String, Integer> map = new HashMap<>();
		for(int i=0; i<tbl.getRowCount(); i++) {
			// ReadRank()를 통해 불러온 테이블의 정보들을 맵에 담기
			map.put(tbl.getValueAt(i, 1)+"", Integer.parseInt(tbl.getValueAt(i, 2)+""));
		}
		map.put(nickname, score);	
		
		List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
		list.sort(Map.Entry.comparingByValue());	// 내림차순 정렬
		Collections.reverse(list);	// 랭킹을 위해 다시 오름차순으로 역순
		
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
				// 랭크 정보를 파일에 저장

				if(cnt == 10)	// 랭크 10명까지만
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
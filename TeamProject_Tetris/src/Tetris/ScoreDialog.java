package Tetris;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ScoreDialog extends JDialog implements ActionListener {
	MainController mcr;
	JLabel scorelbl, scName;
	JTextField scoretf;
	JButton dlbt1;
	JPanel scpn,dlbtpn;
	int score;

	public ScoreDialog(MainController mcr){
		super(mcr, "", true);
		this.mcr = mcr;
		Font font = new Font("맑은 고딕",Font.BOLD,15);

		setTitle(" < GameOver >");
		getContentPane().setPreferredSize(new Dimension(140, 400));
		setLayout(null);
		setSize(380,180);
		setLocationRelativeTo(this.mcr);

		scorelbl = new JLabel();
		scorelbl.setText("SCORE : "+mcr.score.getText());
		scorelbl.setBounds(45,0,300,40);
		scorelbl.setFont(font);
		scorelbl.setHorizontalAlignment(JLabel.CENTER);
		this.add(scorelbl,"North");

		score = Integer.parseInt(mcr.score.getText());

		scName = new JLabel("ID를 입력해주세요 : ");
		scName.setFont(font);
		scoretf = new JTextField(7);

		scpn = new JPanel();
		scpn.add(scName);
		scpn.add(scoretf);
		scpn.setBounds(40,40,300,40);
		this.add(scpn);

		dlbt1 = new JButton("확인");
		dlbt1.setFocusPainted(false);
		dlbt1.addActionListener(this);

		dlbtpn = new JPanel();
		dlbtpn.add(dlbt1);
		dlbtpn.setBounds(45,90,300,40);
		this.add(dlbtpn,"South");

		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) { 
				dispose();
				new StartGame(mcr.getLocation());
				mcr.dispose();
			}
		});
		this.setResizable(false);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==dlbt1) {
			// 작성 순서 중요. 프레임이 2개라서 겹쳐지는 문제로 인해 작성순서가 바뀌면 프레임을 덮어버리는 문제가 생김
			dispose();
			StartGame sg = new StartGame(this.mcr.getLocation());
			new Ranking(score, scoretf.getText(), sg.getLocation());
			mcr.dispose();
		}
	}
}
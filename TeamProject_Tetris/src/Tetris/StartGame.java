package Tetris;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class StartGame extends JFrame implements ActionListener{
	JButton btnRk, btnSt;
	ImageIcon icon;

	public StartGame() {
		init();
	}
	public StartGame(Point xy) {
		init();
		this.setLocation(xy);
	}
	public void init(){
		this.setTitle("TETRIS");
		this.setSize(290,500);
		this.setDefaultCloseOperation(3);
		this.setLayout(null);
		this.setLocation(400, 100);

		icon = new ImageIcon("image/background.png");
		JPanel background = new JPanel() {
            public void paintComponent(Graphics g) {
                g.drawImage(icon.getImage(), 0, 0, null);
                setOpaque(false);
                super.paintComponent(g);
            }
        };
        background.setSize(290,500);
        this.add(background);
        background.setLayout(null);
		
		btnRk = new JButton("RANKING");
		btnRk.setBounds(35,370,100,50);
		btnRk.setFocusPainted(false);
		btnRk.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 14));
		btnRk.addActionListener(this);
		btnSt = new JButton("START");
		btnSt.setBounds(150,370,100,50);
		btnSt.setFocusPainted(false);
		btnSt.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 14));
		btnSt.addActionListener(this);
		
		background.add(btnRk);
		background.add(btnSt);
		
		this.setResizable(false);
		this.setVisible(true);
	}
		
	public static void main(String[] args) {
		new StartGame();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==btnRk) {
			new Ranking(this.getLocation());
		} else if(e.getSource()==btnSt) {
			new MainController(this.getLocation());
			this.dispose();
		}
	}
}

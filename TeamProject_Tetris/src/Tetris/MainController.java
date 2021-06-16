package Tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MainController extends JFrame implements Runnable, KeyListener {
	private final int BOARD_ROW = 14;
	private final int BOARD_COL = 7;

	JLabel[][] lbl; // �׸��� ���̾ƿ��� �� ĭ
	Figure now; // ���� ����
	int type = 0; // ���� ���� Ÿ��(����, ����)
	boolean is_figure = false; // ���� ������ �����ϴ���
	int dir; // �̵� ����. 0:�Ʒ�, 1:����, 2:������
	int[][] dir_weight = { { 1, 0 }, { 0, -1 }, { 0, 1 } };
	// �̵� ���⿡ ���� ����ġ. �ش� dir�� �°� row,col ��ǥ�� ���� �����ش�.
	int[] checkRow = new int[3]; // ������ row�� �ε����� ��´�.

	JPanel infoPnl;
	JLabel score;
	JLabel level;
	JLabel[][] nextBlockLbl;
	int nextBlockType;

	int GameSpeed = 1000; // ó�� ���� ���ǵ�� 1�ʿ� ��ĭ �̵�
	int update_count = 0; // �����带 �ѹ� �������� 1�� ����
	int update_timing = 5; // update_count�� update_timing�� �����ϸ� �ӵ� ����

	// ������ �� ȸ���� ���
	int hook_idx = 0;
	int hook_dir = 0;
	int[][] hook_dir_weight = { { 1, 0 }, { 0, -1 }, { -1, 0 }, { 0, 1 } };

	// ������� �� ȸ���� ���
	int stick_dir1 = 0;
	int stick_dir2 = 2;
	int[][] stick_dir_weight = { { 1, 1 }, { -1, 1 }, { -1, -1 }, { 1, -1 } };

	int colorType, nextColoyType; // ���, ������, �ϴû�, �����, �����
	Color[] color = { new Color(120, 199, 82), new Color(221, 65, 50), new Color(121, 221, 221),
			new Color(205, 168, 209), new Color(249, 224, 61) };

	public MainController(Point xy) {
		setSize(380, 590);
		setDefaultCloseOperation(3);
		this.setResizable(false);
		setLocation(xy);

		JPanel gamePnl = new JPanel(new GridLayout(BOARD_ROW, BOARD_COL));
		lbl = setArrayLabel(lbl, BOARD_ROW, BOARD_COL, gamePnl);
		this.add(gamePnl);

		Color textColor = new Color(50, 50, 50);
		Color infoColor = new Color(100, 100, 100);

		infoPnl = new JPanel(new GridLayout(8, 0));
		infoPnl.setPreferredSize(new Dimension(100, 450));

		JLabel nextTextLbl = null;
		nextTextLbl = setTextLabel(nextTextLbl, "NEXT", textColor);
		// ���� �� �����ֱ�
		JPanel nextBlockPnl = new JPanel(new GridLayout(3, 3));
		nextBlockPnl.setBackground(infoColor);
		nextBlockLbl = setArrayLabel(nextBlockLbl, 3, 3, nextBlockPnl);

		infoPnl.add(nextBlockPnl);

		JLabel lvTextLbl = null;
		lvTextLbl = setTextLabel(lvTextLbl, "LEVEL", textColor);
		// ���� ���� (GameSpeed �� ���� ���)
		level = setTextLabel(level, "1", infoColor);

		JLabel scoreTextLbl = null;
		scoreTextLbl = setTextLabel(scoreTextLbl, "SCORE", textColor);
		// ���� ����
		score = setTextLabel(score, "0", infoColor);

		JLabel helpTextLbl = null;
		helpTextLbl = setTextLabel(helpTextLbl, "HELP", textColor);
		JLabel helpLbl = null;
		helpLbl = setTextLabel(helpLbl, "<html>�� : ȸ��<br>�� �� ��<br>Space Bar</html>", infoColor);
		helpLbl.setFont(new Font("���� ���", Font.BOLD, 15));

		this.add(infoPnl, "East");

		this.addKeyListener(this);
		(new Thread(this)).start();
		setVisible(true);
	}

	public JLabel[][] setArrayLabel(JLabel[][] lbl, int row, int col, JPanel pnl) {
		lbl = new JLabel[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				lbl[i][j] = new JLabel();
				lbl[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
				lbl[i][j].setOpaque(true);
				lbl[i][j].setBackground(Color.BLACK);
				pnl.add(lbl[i][j]);
			}
		}
		return lbl;
	}

	public JLabel setTextLabel(JLabel lbl, String str, Color color) {
		lbl = new JLabel(str, JLabel.CENTER);
		lbl.setOpaque(true);
		lbl.setBackground(color);
		lbl.setForeground(Color.WHITE);
		lbl.setFont(new Font("���� ���", Font.BOLD, 20));
		infoPnl.add(lbl);
		return lbl;
	}

	public void nextBlock() {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				nextBlockLbl[i][j].setBackground(Color.BLACK);

		Figure tmp = new Figure(nextBlockType, color[nextColoyType]);
		for (int i = 0; i < 3; i++) {
			nextBlockLbl[tmp.block[i].row + 1][tmp.block[i].col - 2].setBackground(tmp.block[i].color);
		}
		tmp = null;
	}

	@Override
	public void run() {
		while (true) {
			try {
				if (!is_figure) { // ���� ������ ���ٸ� ������ ����
					if (type == 0) {
						if (!is_black(0, 2) || !is_black(0, 3) || !is_black(0, 4)) {
							// ���� ���� ��ġ�� ���� �ִٸ� ��������
							gameOver();
							break;
						}
					} else if (type == 1) {
						if (!is_black(0, 2) || !is_black(0, 3) || !is_black(1, 3)) {
							// ���� ���� ��ġ�� ���� �ִٸ� ��������
							gameOver();
							break;
						}
					}
					now = make_figure();
					hook_idx = 0;
					hook_dir = 0;
					stick_dir1 = 0;
					stick_dir2 = 2;

					nextBlockType = (type + 1) % 2; // 0,1 ���ʴ�� ����
					// nextBlockType = (int)(Math.random()*2); // ���� ����
					nextBlock();
				}

				Thread.sleep(GameSpeed); // 1�ʸ��� ���� �̵�
				speed_update();

				if (is_figure) { // ���� ������ �ִٸ�, �浹 �˻� �� �̵�
					dir = 0; // dir 0�� �Ʒ�
					if (is_collision()) {
						is_figure = false; // �浹�ߴٸ� �ش� ������ ����
						check_figure(); // �浹�ߴٸ� ���� �˻�
						type = nextBlockType; // ���� ������ ����� �� Ÿ��
					} else {
						move_figure(); // �浹 ���ߴٸ� dir �������� �̵�
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void speed_update() {
		update_count++; // update_count ����
		if (update_count >= update_timing && GameSpeed > 100) { // update_count�� update_timing�� �����ϸ� �ӵ�����
			GameSpeed -= 100; // ������ ���� �ð��� ������ ���̱� (�������� �ӵ� ����)
			update_count = 0; // update_count �ʱ�ȭ
			update_timing += 1; // ������ ���� �ð��� ������ update_count�� ���� �����ϴ� update_timing�� ����
			level.setText((Integer.parseInt(level.getText()) + 1) + "");
			// �ӵ� ������������ ���� ���
		}
	}

	private boolean is_collision() {
		int[][] end_board = { { BOARD_ROW - 1, -1 }, { -1, 0 }, { -1, BOARD_COL - 1 } };
		// ���� ���� ��. dir�� ���� �ش��ϴ� ������ ���� �����ߴ��� �˻�
		// 0(�Ʒ�)��� row���� 9���� ���. col�� �������
		// 1(����)�̶�� col���� 0���� ���. row�� �������
		// 2(������)�̶�� col���� 4���� ���. row�� �������

		for (int i = 0; i < 3; i++) { // ���� �� �� ������ �˻�
			int blockRow = now.block[i].row; // ���� ���� ��ǥ
			int blockCol = now.block[i].col;

			if (blockRow == end_board[dir][0] || blockCol == end_board[dir][1]) {
				// ���� ���� �����ڸ��� ��Ҵٸ� �浹
				for (int j = 0; j < 3; j++) {// �浹�ߴٸ�, �浹�� ������ row�� ����
					checkRow[j] = now.block[j].row;
					// lbl[now.block[j].row][now.block[j].col].setBackground(Color.GRAY);
				}
				return true;
			}

			// ���� �� ��������� ����� �ƴϰ�, ���� ������ ���� �ƴ϶�� �浹
			// ������⿡ �ٸ� ���� �ְ� �� �ڽ��� �ƴ϶�� �ٸ� ������ �浹 üũ
			if (!is_black(blockRow + dir_weight[dir][0], blockCol + dir_weight[dir][1])
					&& !is_mine(blockRow + dir_weight[dir][0], blockCol + dir_weight[dir][1])) {
				for (int j = 0; j < 3; j++) {// �浹�ߴٸ�, �浹�� ������ row�� ����
					checkRow[j] = now.block[j].row;
					// lbl[now.block[j].row][now.block[j].col].setBackground(Color.GRAY);
				}
				return true;
			}
			// �̵� ���⿡ ���� ����ġ. �ش� dir�� �°� x,y ��ǥ�� ���� �����ش�.
			// 0(�Ʒ�)��� row���� +1 col���� +0.
			// 1(����)�̶�� row���� +0 col���� -1.
			// 2(������)�̶�� row���� +0, col���� +1.
		}
		return false;
	}

	// ��ü �� ȸ�� ����
	public void rotateBlock() {
		for (int i = 0; i < 3; i++) { // ���� ���� ���� �����
			lbl[now.block[i].row][now.block[i].col].setBackground(Color.BLACK);
		}

		if (type == 0)
			stickCollisionCheck(); // �浹 üũ
		else if (type == 1)
			rotate_hook();

		for (int i = 0; i < 3; i++) { // ȸ���� ������� �ٽ� ���� ĥ�ϱ�
			lbl[now.block[i].row][now.block[i].col].setBackground(now.block[i].color);
		}
	}

	// ����� �� �浹 üũ
	public void stickCollisionCheck() {
		if ((now.block[1].row == 0 && !is_black(now.block[1].row + 2, now.block[1].col))
				|| (now.block[1].row == (BOARD_ROW - 1) && !is_black(now.block[1].row - 2, now.block[1].col))) {
			// �� �� ���� ���� : õ�� or �ٴڿ� ���� ������� ��ȯ ������ ���ٸ� ��ȯ X
			return;
		} else if ((now.block[1].row == 0 || !is_black(now.block[1].row - 1, now.block[1].col))
				&& is_black(now.block[1].row + 1, now.block[1].col)
				&& is_black(now.block[1].row + 2, now.block[1].col)) {
			// 1 : õ�忡 �ٰų� �ε���1�� �� ���� ���� �ְ� �ε���1�� �Ʒ� 2ĭ�� ����ִ� ��� ��� �������� �� ���·� ��ȯ
			changeStickType(1);
		} else if ((now.block[1].row == (BOARD_ROW - 1) || !is_black(now.block[1].row + 1, now.block[1].col))
				&& is_black(now.block[1].row - 1, now.block[1].col)
				&& is_black(now.block[1].row - 2, now.block[1].col)) {
			// 2 : �ٴڿ� �ٰų� �ε���1�� �� �Ʒ��� ���� �ְ� �ε���1�� ���� 2ĭ�� ����ִ� ��� ��� �������� �� ���·� ��ȯ
			changeStickType(2);
		} else if ((now.block[1].col == 0 || !is_black(now.block[1].row, now.block[1].col - 1))
				&& now.block[1].col + 2 <= (BOARD_COL - 1) && is_black(now.block[1].row, now.block[1].col + 1)
				&& is_black(now.block[1].row, now.block[1].col + 2)) {
			// 3 : ���ʿ� �� or ������ ���� ������ ��� ĭ (idx:1) �������� �� ���·� ��ȯ
			changeStickType(3);
		} else if ((now.block[1].col == (BOARD_COL - 1) || !is_black(now.block[1].row, now.block[1].col + 1))
				&& now.block[1].col - 2 >= 0 && (is_black(now.block[1].row, now.block[1].col - 1)
						&& is_black(now.block[1].row, now.block[1].col - 2))) {
			// 4 : �����ʿ� ���� ������ ��� ĭ (idx:1) �������� �� ���·� ��ȯ
			changeStickType(4);
		} else if ((now.block[1].col > 0 && now.block[1].col < (BOARD_COL - 1))
				&& is_black(now.block[0].row + stick_dir_weight[stick_dir1][0],
						now.block[0].col + stick_dir_weight[stick_dir1][1])
				&& is_black(now.block[2].row + stick_dir_weight[stick_dir2][0],
						now.block[2].col + stick_dir_weight[stick_dir2][1])) {
			// 5 : �翷�� �� ���(�ε��� ����)�� �ƴϰ� �翷�� ���� ������ (��� ��) ��� ĭ (idx:1) �������� + ���·� ��ȯ
			changeStickType(5);
		}
	}

	// �浹 üũ�� ���� Ÿ�� ����
	public void changeStickType(int rotateType) {
		rotateStick(); // ��(�ǱԾ�) ȸ��
		if (rotateType == 1) {
			for (int i = 0; i < 3; i++) { // ȸ����Ų�� �� ���(�ε�������)������ ����
				now.block[i].row += 1; // �Ʒ������� ��ĭ�� �̵�
			}
		} else if (rotateType == 2) {
			for (int i = 0; i < 3; i++) {
				now.block[i].row -= 1; // �������� ��ĭ�� �̵�
			}
		} else if (rotateType == 3) {
			for (int i = 0; i < 3; i++) {
				now.block[i].col += 1; // ������ ���� ��ĭ�� �̵�
			}
		} else if (rotateType == 4) {
			for (int i = 0; i < 3; i++) {
				now.block[i].col -= 1; // ���� ���� ��ĭ�� �̵�
			}
		} else if (rotateType == 5) {
			// ȸ����Ų�� �߰������� ����. ���� �ʿ�� �ۼ�.
		}
	}

	// ����� �� ȸ����Ű��
	public void rotateStick() { // 0��°�� 2��° �ε��� ���� �̵����� ȸ���Ѵ�.
		now.block[0].row += stick_dir_weight[stick_dir1][0];
		now.block[0].col += stick_dir_weight[stick_dir1][1];
		now.block[2].row += stick_dir_weight[stick_dir2][0];
		now.block[2].col += stick_dir_weight[stick_dir2][1];
		// ���� �̵��Կ� ���� dir ���ʸ� ���� �ٲ��ش�.
		stick_dir1 = (stick_dir1 + 1) % 4;
		stick_dir2 = (stick_dir2 + 1) % 4;
	}

	private void rotate_hook() {
		if (!is_hook_collision())
			move_hook_block();
	}

	private boolean is_hook_collision() {
		int blockRow = now.block[hook_idx].row;
		int blockCol = now.block[hook_idx].col;

		if (!is_black(blockRow + hook_dir_weight[hook_dir][0], blockCol + hook_dir_weight[hook_dir][1])) {
			return true;
		} else
			return false;
	}

	private void move_hook_block() {
		now.block[hook_idx].row += hook_dir_weight[hook_dir][0]; // ���� �� ��ǥ��, �ش��ϴ� dir�� row ����ġ��ŭ �����ֱ�
		now.block[hook_idx].col += hook_dir_weight[hook_dir][1]; // ���� �� ��ǥ��, �ش��ϴ� dir�� col ����ġ��ŭ �����ֱ�
		hook_idx = (hook_idx + 1) % 3;
		hook_dir = (hook_dir + 1) % 4;
	}

	// �ش� row�� ������ �� ������ Ȯ��
	private void check_figure() {
		boolean is_erase = true; // �ش� row�� ������ �� �������� Ȯ��
		ArrayList<Integer> erase_row = new ArrayList<>(); // ������ �� row�� ����Ʈ

		for (int i = 0; i < 3; i++) {
			is_erase = true;
			int blockRow = checkRow[i]; // Ȯ���ؾ��� row�� �浹 ��� ������ row
			for (int j = 0; j < BOARD_COL; j++) {
				int blockCol = j; // Ȯ���ؾ��� col�� ��ü

				if (is_black(blockRow, blockCol)) { // �� ĭ�̶� ����̶�� �� ������ ����� �ȵ�
					is_erase = false;
					break;
				}
			}
			if (is_erase && !erase_row.contains(blockRow)) { // ������ �ϴ� �����̰�, �ش� ������ list�� ���ٸ�
				erase_row.add(blockRow);
			}
		}
		if (!erase_row.isEmpty()) { // ������ �� ���� �ִٸ� ���� �޼ҵ�
			erase_figure(erase_row);
		}
	}

	private void erase_figure(ArrayList<Integer> erase_row) {
		Collections.sort(erase_row); // �������� row �������� ����

		for (int now_row : erase_row) { // ������ �� row ������� �ٲٱ�
			for (int i = 0; i < BOARD_COL; i++) {
				lbl[now_row][i].setBackground(Color.BLACK);
			}
		}

		int erase_count = erase_row.size(); // ���� row�� ��
		int i = erase_row.get(erase_count - 1); // ä��� ������ row
		if (erase_count == 2 && erase_row.get(1) - erase_row.get(0) == 2) { // ���� ������ ����,�Ʒ��ٸ� ������ ��
			for (int j = 0; j < BOARD_COL; j++) {
				lbl[i][j].setBackground(lbl[i - 1][j].getBackground()); // �� ����� ������� ��ĭ ������ ���� �����ϰ� 2�� ����
				lbl[i - 1][j].setBackground(Color.BLACK);
			}
			i--;
		}
		for (; i >= erase_count; i--) { // ä��� ������ row���� ����. ���� ���� �� ��ŭ ��� ĭ �Ʒ��� ������
			for (int j = 0; j < BOARD_COL; j++) { // ���� �� ��ŭ�� �� ĭ�� ���� ������ ĥ�ϱ�
				lbl[i][j].setBackground(lbl[i - erase_count][j].getBackground());
			}
		}
		for (i = 0; i < erase_row.size(); i++) { // �� ������ ���� �� ��ŭ ������� ä���
			for (int j = 0; j < BOARD_COL; j++) {
				lbl[i][j].setBackground(Color.BLACK);
			}
		}
		// ���� �ٿ� ���� ���� �߰�. 1�� = 100��, 2�� = 300��, 3�� = 500��
		int now_score = Integer.parseInt(score.getText());
		switch (erase_count) {
		case 1:
			now_score += 100;
			score.setText(now_score + "");
			break;
		case 2:
			now_score += 300;
			score.setText(now_score + "");
			break;
		case 3:
			now_score += 500;
			score.setText(now_score + "");
			break;
		}
	}

	// ���� �̵�
	private void move_figure() {
		for (int i = 0; i < 3; i++) {
			lbl[now.block[i].row][now.block[i].col].setBackground(Color.BLACK);
			now.block[i].row += dir_weight[dir][0]; // ���� �� ��ǥ��, �ش��ϴ� dir�� row ����ġ��ŭ �����ֱ�
			now.block[i].col += dir_weight[dir][1]; // ���� �� ��ǥ��, �ش��ϴ� dir�� col ����ġ��ŭ �����ֱ�
			// �� �̵�, dir => 0:�Ʒ�, 1:����, 2:������
		}
		for (int i = 0; i < 3; i++) {
			lbl[now.block[i].row][now.block[i].col].setBackground(now.block[i].color);
		}
	}

	// �浹�Ҷ����� �Ʒ� �������� ���� �̵�
	public void blockFastDown() {
		for (int i = 0; i < BOARD_ROW; i++) {
			dir = 0;
			if (is_collision()) {
				is_figure = false;
				check_figure();
				type = nextBlockType;
				break;
			}
			move_figure();
		}
	}

	// �ش� ��ǥ�� ������� �˻�
	private boolean is_black(int row, int col) {
		return lbl[row][col].getBackground() == Color.BLACK ? true : false;
	}

	// �ش� ��ǥ�� ���� ������ ������ �˻�
	private boolean is_mine(int row, int col) {
		for (int i = 0; i < 3; i++) {
			int blockRow = now.block[i].row;
			int blockCol = now.block[i].col;

			if (row == blockRow && col == blockCol)
				return true;
		}
		return false;
	}

	// ���ο� ���� ����
	private Figure make_figure() {
		colorType = (colorType + 1) % 5;
		nextColoyType = (nextColoyType != 4) ? colorType + 1 : 0;
		Figure tmp = new Figure(type, color[colorType]);
		for (int i = 0; i < 3; i++) {
			int blockRow = tmp.block[i].row;
			int blockCol = tmp.block[i].col;
			Color blockColor = tmp.block[i].color;
			lbl[blockRow][blockCol].setBackground(blockColor);
		}
		is_figure = true;
		return tmp;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (is_figure) {
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				rotateBlock();
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				dir = 0;
				if (!is_collision()) {
					move_figure();
				}
			} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				dir = 1;
				if (!is_collision()) {
					move_figure();
				}
			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				dir = 2;
				if (!is_collision()) {
					move_figure();
				}
			} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				blockFastDown();
			}
		}
	}

	private void gameOver() {
		new ScoreDialog(this);
	}

	public static void main(String[] args) {
		new StartGame();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
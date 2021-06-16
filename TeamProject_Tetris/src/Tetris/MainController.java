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

	JLabel[][] lbl; // 그리드 레이아웃의 각 칸
	Figure now; // 현재 도형
	int type = 0; // 현재 도형 타입(일자, ㄱ자)
	boolean is_figure = false; // 현재 도형이 존재하는지
	int dir; // 이동 방향. 0:아래, 1:왼쪽, 2:오른쪽
	int[][] dir_weight = { { 1, 0 }, { 0, -1 }, { 0, 1 } };
	// 이동 방향에 따른 가중치. 해당 dir에 맞게 row,col 좌표에 값을 더해준다.
	int[] checkRow = new int[3]; // 삭제할 row의 인덱스를 담는다.

	JPanel infoPnl;
	JLabel score;
	JLabel level;
	JLabel[][] nextBlockLbl;
	int nextBlockType;

	int GameSpeed = 1000; // 처음 시작 스피드는 1초에 한칸 이동
	int update_count = 0; // 쓰레드를 한번 돌때마다 1씩 증가
	int update_timing = 5; // update_count가 update_timing에 도달하면 속도 변경

	// 갈고리형 블럭 회전시 사용
	int hook_idx = 0;
	int hook_dir = 0;
	int[][] hook_dir_weight = { { 1, 0 }, { 0, -1 }, { -1, 0 }, { 0, 1 } };

	// 막대기형 블럭 회전시 사용
	int stick_dir1 = 0;
	int stick_dir2 = 2;
	int[][] stick_dir_weight = { { 1, 1 }, { -1, 1 }, { -1, -1 }, { 1, -1 } };

	int colorType, nextColoyType; // 녹색, 빨강색, 하늘색, 보라색, 노란색
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
		// 다음 블럭 보여주기
		JPanel nextBlockPnl = new JPanel(new GridLayout(3, 3));
		nextBlockPnl.setBackground(infoColor);
		nextBlockLbl = setArrayLabel(nextBlockLbl, 3, 3, nextBlockPnl);

		infoPnl.add(nextBlockPnl);

		JLabel lvTextLbl = null;
		lvTextLbl = setTextLabel(lvTextLbl, "LEVEL", textColor);
		// 게임 레벨 (GameSpeed 에 맞춰 상승)
		level = setTextLabel(level, "1", infoColor);

		JLabel scoreTextLbl = null;
		scoreTextLbl = setTextLabel(scoreTextLbl, "SCORE", textColor);
		// 게임 점수
		score = setTextLabel(score, "0", infoColor);

		JLabel helpTextLbl = null;
		helpTextLbl = setTextLabel(helpTextLbl, "HELP", textColor);
		JLabel helpLbl = null;
		helpLbl = setTextLabel(helpLbl, "<html>↑ : 회전<br>← ↓ →<br>Space Bar</html>", infoColor);
		helpLbl.setFont(new Font("맑은 고딕", Font.BOLD, 15));

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
		lbl.setFont(new Font("맑은 고딕", Font.BOLD, 20));
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
				if (!is_figure) { // 현재 도형이 없다면 도형을 생성
					if (type == 0) {
						if (!is_black(0, 2) || !is_black(0, 3) || !is_black(0, 4)) {
							// 도형 생성 위치에 블럭이 있다면 게임종료
							gameOver();
							break;
						}
					} else if (type == 1) {
						if (!is_black(0, 2) || !is_black(0, 3) || !is_black(1, 3)) {
							// 도형 생성 위치에 블럭이 있다면 게임종료
							gameOver();
							break;
						}
					}
					now = make_figure();
					hook_idx = 0;
					hook_dir = 0;
					stick_dir1 = 0;
					stick_dir2 = 2;

					nextBlockType = (type + 1) % 2; // 0,1 차례대로 생성
					// nextBlockType = (int)(Math.random()*2); // 랜덤 생성
					nextBlock();
				}

				Thread.sleep(GameSpeed); // 1초마다 도형 이동
				speed_update();

				if (is_figure) { // 현재 도형이 있다면, 충돌 검사 후 이동
					dir = 0; // dir 0은 아래
					if (is_collision()) {
						is_figure = false; // 충돌했다면 해당 도형은 고정
						check_figure(); // 충돌했다면 삭제 검사
						type = nextBlockType; // 다음 블럭으로 예약된 블럭 타입
					} else {
						move_figure(); // 충돌 안했다면 dir 방향으로 이동
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void speed_update() {
		update_count++; // update_count 증가
		if (update_count >= update_timing && GameSpeed > 100) { // update_count가 update_timing에 도달하면 속도변경
			GameSpeed -= 100; // 쓰레드 갱신 시간을 반으로 줄이기 (떨어지는 속도 증가)
			update_count = 0; // update_count 초기화
			update_timing += 1; // 쓰레드 갱신 시간이 빨라져 update_count도 빨리 증가하니 update_timing도 증가
			level.setText((Integer.parseInt(level.getText()) + 1) + "");
			// 속도 빨라질때마다 레벨 상승
		}
	}

	private boolean is_collision() {
		int[][] end_board = { { BOARD_ROW - 1, -1 }, { -1, 0 }, { -1, BOARD_COL - 1 } };
		// 게임 판의 끝. dir에 따라 해당하는 방향의 끝에 도달했는지 검사
		// 0(아래)라면 row값이 9인지 고려. col은 상관없음
		// 1(왼쪽)이라면 col값이 0인지 고려. row는 상관없음
		// 2(오른쪽)이라면 col값이 4인지 고려. row는 상관없음

		for (int i = 0; i < 3; i++) { // 도형 속 각 블럭마다 검사
			int blockRow = now.block[i].row; // 현재 블럭의 좌표
			int blockCol = now.block[i].col;

			if (blockRow == end_board[dir][0] || blockCol == end_board[dir][1]) {
				// 현재 블럭이 가장자리에 닿았다면 충돌
				for (int j = 0; j < 3; j++) {// 충돌했다면, 충돌시 블럭들의 row값 저장
					checkRow[j] = now.block[j].row;
					// lbl[now.block[j].row][now.block[j].col].setBackground(Color.GRAY);
				}
				return true;
			}

			// 현재 블럭 진행방향이 흰색이 아니고, 현재 도형의 블럭이 아니라면 충돌
			// 진행방향에 다른 블럭이 있고 나 자신이 아니라면 다른 블럭과의 충돌 체크
			if (!is_black(blockRow + dir_weight[dir][0], blockCol + dir_weight[dir][1])
					&& !is_mine(blockRow + dir_weight[dir][0], blockCol + dir_weight[dir][1])) {
				for (int j = 0; j < 3; j++) {// 충돌했다면, 충돌시 블럭들의 row값 저장
					checkRow[j] = now.block[j].row;
					// lbl[now.block[j].row][now.block[j].col].setBackground(Color.GRAY);
				}
				return true;
			}
			// 이동 방향에 따른 가중치. 해당 dir에 맞게 x,y 좌표에 값을 더해준다.
			// 0(아래)라면 row값에 +1 col값에 +0.
			// 1(왼쪽)이라면 row값에 +0 col값에 -1.
			// 2(오른쪽)이라면 row값에 +0, col값에 +1.
		}
		return false;
	}

	// 전체 블럭 회전 제어
	public void rotateBlock() {
		for (int i = 0; i < 3; i++) { // 기존 블럭의 색을 지우고
			lbl[now.block[i].row][now.block[i].col].setBackground(Color.BLACK);
		}

		if (type == 0)
			stickCollisionCheck(); // 충돌 체크
		else if (type == 1)
			rotate_hook();

		for (int i = 0; i < 3; i++) { // 회전된 모양으로 다시 색을 칠하기
			lbl[now.block[i].row][now.block[i].col].setBackground(now.block[i].color);
		}
	}

	// 막대기 블럭 충돌 체크
	public void stickCollisionCheck() {
		if ((now.block[1].row == 0 && !is_black(now.block[1].row + 2, now.block[1].col))
				|| (now.block[1].row == (BOARD_ROW - 1) && !is_black(now.block[1].row - 2, now.block[1].col))) {
			// 그 외 제한 사항 : 천장 or 바닥에 블럭이 닿았을때 변환 공간이 없다면 변환 X
			return;
		} else if ((now.block[1].row == 0 || !is_black(now.block[1].row - 1, now.block[1].col))
				&& is_black(now.block[1].row + 1, now.block[1].col)
				&& is_black(now.block[1].row + 2, now.block[1].col)) {
			// 1 : 천장에 붙거나 인덱스1번 블럭 위로 블럭이 있고 인덱스1번 아래 2칸이 비어있는 경우 가운데 기준으로 ㅜ 형태로 변환
			changeStickType(1);
		} else if ((now.block[1].row == (BOARD_ROW - 1) || !is_black(now.block[1].row + 1, now.block[1].col))
				&& is_black(now.block[1].row - 1, now.block[1].col)
				&& is_black(now.block[1].row - 2, now.block[1].col)) {
			// 2 : 바닥에 붙거나 인덱스1번 블럭 아래로 블럭이 있고 인덱스1번 위로 2칸이 비어있는 경우 가운데 기준으로 ㅗ 형태로 변환
			changeStickType(2);
		} else if ((now.block[1].col == 0 || !is_black(now.block[1].row, now.block[1].col - 1))
				&& now.block[1].col + 2 <= (BOARD_COL - 1) && is_black(now.block[1].row, now.block[1].col + 1)
				&& is_black(now.block[1].row, now.block[1].col + 2)) {
			// 3 : 왼쪽에 벽 or 정착된 블럭이 있을때 가운데 칸 (idx:1) 기준으로 ㅏ 형태로 변환
			changeStickType(3);
		} else if ((now.block[1].col == (BOARD_COL - 1) || !is_black(now.block[1].row, now.block[1].col + 1))
				&& now.block[1].col - 2 >= 0 && (is_black(now.block[1].row, now.block[1].col - 1)
						&& is_black(now.block[1].row, now.block[1].col - 2))) {
			// 4 : 오른쪽에 벽이 있을때 가운데 칸 (idx:1) 기준으로 ㅓ 형태로 변환
			changeStickType(4);
		} else if ((now.block[1].col > 0 && now.block[1].col < (BOARD_COL - 1))
				&& is_black(now.block[0].row + stick_dir_weight[stick_dir1][0],
						now.block[0].col + stick_dir_weight[stick_dir1][1])
				&& is_black(now.block[2].row + stick_dir_weight[stick_dir2][0],
						now.block[2].col + stick_dir_weight[stick_dir2][1])) {
			// 5 : 양옆이 벽 통과(인덱스 에러)가 아니고 양옆에 블럭이 없으면 (흰색 라벨) 가운데 칸 (idx:1) 기준으로 + 형태로 변환
			changeStickType(5);
		}
	}

	// 충돌 체크에 따라 타입 변경
	public void changeStickType(int rotateType) {
		rotateStick(); // 블럭(피규어) 회전
		if (rotateType == 1) {
			for (int i = 0; i < 3; i++) { // 회전시킨뒤 벽 통과(인덱스에러)방지를 위해
				now.block[i].row += 1; // 아래쪽으로 한칸씩 이동
			}
		} else if (rotateType == 2) {
			for (int i = 0; i < 3; i++) {
				now.block[i].row -= 1; // 위쪽으로 한칸씩 이동
			}
		} else if (rotateType == 3) {
			for (int i = 0; i < 3; i++) {
				now.block[i].col += 1; // 오른쪽 으로 한칸씩 이동
			}
		} else if (rotateType == 4) {
			for (int i = 0; i < 3; i++) {
				now.block[i].col -= 1; // 왼쪽 으로 한칸씩 이동
			}
		} else if (rotateType == 5) {
			// 회전시킨뒤 추가사항이 없음. 추후 필요시 작성.
		}
	}

	// 막대기 블럭 회전시키기
	public void rotateStick() { // 0번째와 2번째 인덱스 블럭만 이동시켜 회전한다.
		now.block[0].row += stick_dir_weight[stick_dir1][0];
		now.block[0].col += stick_dir_weight[stick_dir1][1];
		now.block[2].row += stick_dir_weight[stick_dir2][0];
		now.block[2].col += stick_dir_weight[stick_dir2][1];
		// 블럭이 이동함에 따라 dir 차례를 같이 바꿔준다.
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
		now.block[hook_idx].row += hook_dir_weight[hook_dir][0]; // 현재 블럭 좌표에, 해당하는 dir의 row 가중치만큼 더해주기
		now.block[hook_idx].col += hook_dir_weight[hook_dir][1]; // 현재 블럭 좌표에, 해당하는 dir의 col 가중치만큼 더해주기
		hook_idx = (hook_idx + 1) % 3;
		hook_dir = (hook_dir + 1) % 4;
	}

	// 해당 row가 지워야 할 줄인지 확인
	private void check_figure() {
		boolean is_erase = true; // 해당 row가 지워야 할 라인인지 확인
		ArrayList<Integer> erase_row = new ArrayList<>(); // 지워야 할 row의 리스트

		for (int i = 0; i < 3; i++) {
			is_erase = true;
			int blockRow = checkRow[i]; // 확인해야할 row는 충돌 당시 블럭들의 row
			for (int j = 0; j < BOARD_COL; j++) {
				int blockCol = j; // 확인해야할 col은 전체

				if (is_black(blockRow, blockCol)) { // 한 칸이라도 흰색이라면 그 라인은 지우면 안됨
					is_erase = false;
					break;
				}
			}
			if (is_erase && !erase_row.contains(blockRow)) { // 지워야 하는 라인이고, 해당 라인이 list에 없다면
				erase_row.add(blockRow);
			}
		}
		if (!erase_row.isEmpty()) { // 지워야 할 줄이 있다면 삭제 메소드
			erase_figure(erase_row);
		}
	}

	private void erase_figure(ArrayList<Integer> erase_row) {
		Collections.sort(erase_row); // 지워야할 row 오름차순 정렬

		for (int now_row : erase_row) { // 지워야 할 row 흰색으로 바꾸기
			for (int i = 0; i < BOARD_COL; i++) {
				lbl[now_row][i].setBackground(Color.BLACK);
			}
		}

		int erase_count = erase_row.size(); // 지운 row의 수
		int i = erase_row.get(erase_count - 1); // 채우기 시작할 row
		if (erase_count == 2 && erase_row.get(1) - erase_row.get(0) == 2) { // 일자 블럭으로 윗줄,아랫줄만 없앴을 때
			for (int j = 0; j < BOARD_COL; j++) {
				lbl[i][j].setBackground(lbl[i - 1][j].getBackground()); // 안 사라진 가운뎃줄을 한칸 내리고 전과 동일하게 2줄 삭제
				lbl[i - 1][j].setBackground(Color.BLACK);
			}
			i--;
		}
		for (; i >= erase_count; i--) { // 채우기 시작할 row부터 시작. 지운 줄의 수 만큼 모든 칸 아래로 내리기
			for (int j = 0; j < BOARD_COL; j++) { // 지운 줄 만큼의 윗 칸과 같은 색으로 칠하기
				lbl[i][j].setBackground(lbl[i - erase_count][j].getBackground());
			}
		}
		for (i = 0; i < erase_row.size(); i++) { // 맨 위쪽은 지운 줄 만큼 흰색으로 채우기
			for (int j = 0; j < BOARD_COL; j++) {
				lbl[i][j].setBackground(Color.BLACK);
			}
		}
		// 지운 줄에 따라 점수 추가. 1줄 = 100점, 2줄 = 300점, 3줄 = 500점
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

	// 도형 이동
	private void move_figure() {
		for (int i = 0; i < 3; i++) {
			lbl[now.block[i].row][now.block[i].col].setBackground(Color.BLACK);
			now.block[i].row += dir_weight[dir][0]; // 현재 블럭 좌표에, 해당하는 dir의 row 가중치만큼 더해주기
			now.block[i].col += dir_weight[dir][1]; // 현재 블럭 좌표에, 해당하는 dir의 col 가중치만큼 더해주기
			// 블럭 이동, dir => 0:아래, 1:왼쪽, 2:오른쪽
		}
		for (int i = 0; i < 3; i++) {
			lbl[now.block[i].row][now.block[i].col].setBackground(now.block[i].color);
		}
	}

	// 충돌할때까지 아래 방향으로 빠른 이동
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

	// 해당 좌표가 흰색인지 검사
	private boolean is_black(int row, int col) {
		return lbl[row][col].getBackground() == Color.BLACK ? true : false;
	}

	// 해당 좌표가 현재 도형의 블럭인지 검사
	private boolean is_mine(int row, int col) {
		for (int i = 0; i < 3; i++) {
			int blockRow = now.block[i].row;
			int blockCol = now.block[i].col;

			if (row == blockRow && col == blockCol)
				return true;
		}
		return false;
	}

	// 새로운 도형 생성
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
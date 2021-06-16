package Tetris;

import java.awt.Color;

public class Figure {
	int type;		// ���� ����
	Block [] block;
	
	public Figure(int type, Color color) {
		this.type = type;
		block = new Block[3];
		if(type == 0) {							// ��
			block[0] = new Block(0,2, color);
			block[1] = new Block(0,3, color);
			block[2] = new Block(0,4, color);
		} 
		else if(type == 1) {					// ��
			block[0] = new Block(0,2, color);
			block[1] = new Block(0,3, color);
			block[2] = new Block(1,3, color);
		} 
	}
}

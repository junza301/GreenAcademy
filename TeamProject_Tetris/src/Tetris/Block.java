package Tetris;

import java.awt.Color;

public class Block {
	Color color;
	int row, col;
	
	public Block(int row, int col, Color color) {
		this.color = color;
		this.row = row;
		this.col = col;
	}
}

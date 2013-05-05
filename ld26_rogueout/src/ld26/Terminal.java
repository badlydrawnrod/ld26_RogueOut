package ld26;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

class Terminal {
	private SpriteBatch spriteBatch;
	private int numColumns;
	private int numRows;
	private BitmapFont font;
	private float fontWidth;
	private float fontHeight;
	private float maxX;
	private float minX;
	private float maxY;
	private float minY;

	public Terminal(SpriteBatch spriteBatch, float screenWidth, float screenHeight, BitmapFont font) {
		this.spriteBatch = spriteBatch;
		this.font = font;
        this.fontWidth = font.getSpaceWidth();
        this.fontHeight = font.getLineHeight() - font.getAscent();
        numColumns = (int) (screenWidth / fontWidth);
        numRows = (int) (screenHeight / fontHeight);
        maxX = (numColumns * fontWidth) / 2;
        minX = -maxX;
        maxY = (numRows * fontHeight) / 2;
        minY = -maxY;
	}

	public void writeString(String s, int col, int row) {
		font.draw(spriteBatch, s, screenX(col), screenY(row));
	}

	public int width() {
		return numColumns;
	}

	public int height() {
		return numRows;
	}

	public float screenX(int col) {
		return minX + fontWidth * col;
	}
	
	public float screenY(int row) {
		return minY + fontHeight * (row + 1);
	}

	public int terminalX(float screenX) {
		return (int) ((screenX - minX) / fontWidth);
	}

	public int terminalY(float screenY) {
		return (int) ((screenY - minY) / fontHeight);
	}
}

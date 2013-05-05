package ld26;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Coin {

	private Bat bat;
	private SpriteBatch spriteBatch;
	private BitmapFont font;
	private float x;
	private float y;
	private String appearance = "$";
	private Sprite potionSprite;
	private float fontWidth;
	private float fontHeight;
	private float dy;
	private RogueOut game;

	public Coin(RogueOut game, Bat bat, SpriteBatch spriteBatch, BitmapFont font, float x, float y) {
		this.game = game;
		this.bat = bat;
		this.spriteBatch = spriteBatch;
		this.font = font;
        Pixmap pixmap = new Pixmap(64, 64, Format.RGBA8888);  
        pixmap.setColor(Color.WHITE);  
        pixmap.fillRectangle(0, 0, 64, 64);  
        potionSprite = new Sprite(new Texture(pixmap));  
        potionSprite.setColor(new Color(Color.BLACK));
        pixmap.dispose();
        fontWidth = font.getSpaceWidth();
        fontHeight = font.getLineHeight() - font.getAscent();
        this.x = x;
        this.y = y;
        this.dy = -200;
	}

	public boolean update(float delta) {
		y += dy * delta;
		if (right() >= bat.left() && left() <= bat.right() && top() >= bat.bottom() && bottom() <= bat.top()) {
			game.onHitCoin(this);
			return false;
		}
		if (top() < bat.bottom()) {
			return false;
		}
		return true;
	}
	
	public void draw() {
		potionSprite.setPosition(x, y);
		potionSprite.setSize(fontWidth * appearance.length(), fontHeight);
		potionSprite.draw(spriteBatch);
		font.draw(spriteBatch, appearance, x, y + fontHeight);
	}
	
	public float left() {
		return x;
	}
	
	public float right() {
		return x + fontWidth * appearance.length();
	}
	
	public float bottom() {
		return y;
	}
	
	public float top() {
		return y + fontHeight;
	}
}

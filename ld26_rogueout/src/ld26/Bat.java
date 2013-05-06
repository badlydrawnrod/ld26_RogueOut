package ld26;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

class Bat {
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private float x;
    private float y;
    private String appearance = "BBBBBB";
    private Sprite batSprite;
    private float fontWidth;
    private float fontHeight;
    private float minX;
    private float maxX;

    public Bat(SpriteBatch spriteBatch, BitmapFont font, float minX,
            float maxX, float y) {
        this.spriteBatch = spriteBatch;
        this.font = font;
        Pixmap pixmap = new Pixmap(64, 64, Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fillRectangle(0, 0, 64, 64);
        batSprite = new Sprite(new Texture(pixmap));
        batSprite.setColor(new Color(Color.BLACK));
        pixmap.dispose();
        fontWidth = font.getSpaceWidth();
        fontHeight = font.getLineHeight() - font.getAscent();
        this.minX = minX;
        this.maxX = maxX;
        this.y = y;
    }

    public void update(float delta, float mouseX) {
        x = Math.max(minX, mouseX);
        x = Math.min(maxX - fontWidth * appearance.length(), x);
    }

    public void draw(boolean isBlinded) {
        batSprite.setPosition(x, y);
        batSprite.setSize(fontWidth * appearance.length(), fontHeight);
        batSprite.draw(spriteBatch);
        if (!isBlinded) {
            font.draw(spriteBatch, appearance, x, y + fontHeight);
        }
    }

    public float left() {
        return x;
    }

    public float right() {
        return x + fontWidth * appearance.length();
    }

    public float middle() {
        return x + fontWidth * appearance.length() * 0.5f;
    }

    public float top() {
        return y + fontHeight;
    }

    public float width() {
        return fontWidth * appearance.length();
    }

    public float bottom() {
        return y;
    }
}
package ld26;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

class Ball {
	private SpriteBatch spriteBatch;
	private BitmapFont font;
	private float x;
	private float y;
	private float dx;
	private float dy;
	private String appearance = "@";
	private Sprite ballSprite;
	private float fontWidth;
	private float fontHeight;
	private boolean isInPlay;
	private Dungeon dungeon;
	private Bat bat;
	private int hitPoints;
	private int maxHitPoints;
	private Toast toast;
	private int xp;
	private int gold;
	private StatusRow statusRow;
	private float angleMultiplier;
	private RogueOut game;
	private float stateTime;
	
	public Ball(RogueOut game, Dungeon dungeon, Bat bat, SpriteBatch spriteBatch, BitmapFont font) {
		this.game = game;
		this.dungeon = dungeon;
		this.bat = bat;
		this.spriteBatch = spriteBatch;
		this.font = font;
        Pixmap pixmap = new Pixmap(64, 64, Format.RGBA8888);  
        pixmap.setColor(Color.WHITE);  
        pixmap.fillRectangle(0, 0, 64, 64);  
        ballSprite = new Sprite(new Texture(pixmap));  
        ballSprite.setColor(new Color(Color.BLACK));
        pixmap.dispose();
        fontWidth = font.getSpaceWidth();
        fontHeight = font.getLineHeight() - font.getAscent();
        x = 0;
        y = 0;
        isInPlay = false;
        maxHitPoints = 10;
        hitPoints = 10;
	}
	
	public void removeFromPlay() {
		isInPlay = false;
	}
	
	public void setSpeed(float dx, float dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	public void update(float delta, boolean shouldMove) {
		if (!isInPlay && shouldMove) {
			isInPlay = true;
			stateTime = 0;
			angleMultiplier = 1.0f;
			if (MathUtils.random(0.0f, 1.0f) < 0.5f) {
				dx = -dx;
			}
		}
		stateTime += delta;
		if (isInPlay) {
			float speedMult = 0.9f + 0.1f * dungeon.level();

			// After 30 seconds, the ball starts to speed up.
			speedMult += Math.max(0, Math.min(2, (stateTime - 30) / 120.0f)); 
			
			// The ball is in play, so let it bounce around.
			float mdy = dy * speedMult;
			y += mdy * delta;
			if (offScreen()) {
				hitPoints = 0;
				toast("You fall into the void. You die!");
			}
			else {
				if (hit() || hitBat()) {
					dy = -dy;
					y -= mdy * delta;
				}
				float mdx = dx * angleMultiplier * speedMult;
				x += mdx * delta;
				if (hit()) {
					dx = -dx;
					x -= mdx * delta;
				}
			}
			if (hitPoints <= 0) {
				isInPlay = false;
				game.playSound("die");
			}
		}
		else if (hitPoints > 0) {
			toast("Click to start level " + dungeon.level());
			// The ball is not in play so clamp it to the top of the bat.
			x = bat.middle() - fontWidth / 2;
			y = bat.top();
		}
	}
	
	private boolean offScreen() {
		return !dungeon.isValid(x, y);
	}

	private boolean hit() {
		return  check(x, y) ||
				check(x + fontWidth - 1, y) ||
				check(x + fontWidth - 1, y + fontHeight - 1) ||
				check(x, y + fontHeight - 1);
	}
	
	private boolean check(float x, float y) {
		Entity e = dungeon.entityAt(x, y);
		if (e == null) return false;

		boolean isStrong = game.isStrong();
		int damageCaused = isStrong && e.isMonster()
				? e.hitPoints()
				: MathUtils.random(1, 6);
		if (e.isMonster()) {
			game.playSound("hit");
		}
		if (e.isMonster() || e.isDestructible()) {
			boolean isAlive = e.hit(damageCaused);
			String message = e.hitMessage(damageCaused);
			toast(message);
			if (!isAlive) {
				xp += e.xp();
				e.remove();
				if (e.isMonster()) {
					game.onMonsterKilled(e);
					game.playSound("kill");
					notifyStatus();
				}
			}
		}
		
		int damage = e.damage();
		hitPoints -= damage;
		if (damage > 0) {
			game.playSound("hurt");
			String message = e.damageMessage(damage);
			if (hitPoints <= 0) {
				message += " You die!";
			}
			toast(message);
			notifyStatus();
		}
		
		return !(isStrong && e.isMonster());
	}
	
	private void toast(String message) {
		if (toast != null) {
			toast.toast(message, hitPoints > 0 ? 2 : 5);
		}
	}

	private void notifyStatus() {
		if (statusRow != null) {
			statusRow.updateMessage();
		}
	}
	
	private boolean hitBat() {
		// If the ball is moving down then check if it hit the bat.
		if (dy < 0) {
			float bottom = y;
			float left = x;
			float right = x + fontWidth * appearance.length();
			if (right >= bat.left() && left <= bat.right() && bottom <= bat.top() && bottom - dy > bat.top()) {
				float middle = x + fontWidth * appearance.length() * 0.5f;
				float batMiddle = bat.middle();
				float dist = 2 * Math.abs(middle - batMiddle) / bat.width();
				angleMultiplier = 0.5f + dist * 0.5f;
				return true;
			}
		}
		return false;
	}
	
	public void draw(boolean isInvisible) {
		ballSprite.setPosition(x, y);
		ballSprite.setSize(fontWidth * appearance.length(), fontHeight);
		ballSprite.draw(spriteBatch);
		if (!isInvisible) {
			font.draw(spriteBatch, appearance, x, y + fontHeight);
		}
	}

	public void setToast(Toast toast) {
		this.toast = toast;
	}
	
	public void setStatusRow(StatusRow statusRow) {
		this.statusRow = statusRow;
	}

	public int hitPoints() {
		return hitPoints;
	}
	
	public int xp() {
		return xp;
	}
	
	public int gold() {
		return gold;
	}

	public int maxHitPoints() {
		return maxHitPoints;
	}

	public void heal() {
		hitPoints = maxHitPoints;
		notifyStatus();
	}

	public void addGold(int value) {
		gold += value;
		notifyStatus();
	}
}

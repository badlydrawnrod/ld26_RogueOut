package ld26;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class RogueOut extends com.badlogic.gdx.Game {

    private enum Mode {
        SplashScreen, Playing, Death
    };

    private OrthographicCamera camera;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private ViewportScaler scaler;
    private VirtualViewport viewport;
    private MessageRow messageRow;
    private Dungeon dungeon;
    private StatusRow statusRow;
    private Terminal terminal;
    private Bat bat;
    private float mouseX;
    private float mouseY;
    private float mouseMin;
    private float mouseMax;
    private Ball ball;
    private float screenWidth;
    private float screenHeight;
    private Mode mode;
    private float stateTime;
    private float killTime;
    private List<Potion> potions;
    private List<Coin> coins;
    private Music splashScreenMusic;
    private Music gameMusic;
    private Map<String, Sound> sounds;

    private String[] splashScreen = {
            "+--------------------------------------------------------+",
            "| RRRR    OOO    GGG   U   U  EEEEE   OOO   U   U  TTTTT |",
            "| R   R  O   O  G   G  U   U  E      O   O  U   U    T   |",
            "| R   R  O   O  G      U   U  E      O   O  U   U    T   |",
            "| RRRR   O   O  G GGG  U   U  EEEE   O   O  U   U    T   |",
            "| R R    O   O  G G G  U   U  E      O   O  U   U    T   |",
            "| R  R   O   O  G   G  U   U  E      O   O  U   U    T   |",
            "| R   R   OOO    GGG    UUU   EEEEE   OOO    UUU     T   |",
            "|                                                        |",
            "|                                                        |",
            "|                The unholy offspring of                 |",
            "|                  Rogue and Breakout.                   |",
            "|                                                        |",
            "|           (now with hi tech sound effects)             |",
            "|                                                        |",
            "|                                                        |",
            "|                Click the mouse to Enter                |",
            "|                    ...if you dare!                     |",
            "+--------------------------------------------------------+",
            "                    A badly drawn game                    ",
            "                    for Ludum Dare 26                     ", };

    private String[] deathScreen = { "     _________________",
            "    /                 \\", "   /                   \\",
            "  /                     \\", "  | RRRR   IIIII  PPPP  |",
            "  | R   R    I    P   P |", "  | R   R    I    P   P |",
            "  | RRRR     I    PPPP  |", "  | R R      I    P     |",
            "  | R  R     I    P     |", "  | R   R  IIIII  P     |",
            "  |                     |", "  |    Level: %5d     |",
            "  |       XP: %5d     |", "  |     Gold: %5d     |",
            "  |                     |", "///////////////////////////", };

    private float confusedTime;
    private float speedTime;
    private float slowTime;
    private float paralysedTime;
    private float blindedTime;
    private float invisibleTime;
    private float strongTime;
    private boolean isEscapePressed;
    private boolean wasEscapePressed;
    private boolean isLeftPressed;
    private boolean wasLeftPressed;

    public RogueOut(float screenWidth, float screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void create() {
        scaler = new ViewportScaler(screenWidth, screenHeight);

        // Create the camera.
        camera = new OrthographicCamera(Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());
        camera.position.set(0f, 0f, 0f);

        // Create the sprite batch.
        spriteBatch = new SpriteBatch();

        // Load the font.
        font = new BitmapFont(Gdx.files.internal("assets/data/consolas32.fnt"),
                false);
        font.setUseIntegerPositions(false);
        font.setColor(0, 192.0f / 255, 0, 1);

        // Create the terminal.
        terminal = new Terminal(spriteBatch, screenWidth, screenHeight, font);

        // Calculate mouse boundaries.
        mouseMin = terminal.screenX(1);
        mouseMax = terminal.screenX(terminal.width() - 1);
        mouseX = 0;
        mouseY = terminal.screenY(1);

        // Allow the game to get mouse input, even when it's outside of the
        // window.
        Gdx.input.setCursorCatched(true);

        // Load the music.
        splashScreenMusic = Gdx.audio.newMusic(Gdx.files
                .internal("assets/data/loop.ogg"));
        gameMusic = Gdx.audio.newMusic(Gdx.files
                .internal("assets/data/gameLoop.ogg"));

        // Load the sounds.
        sounds = new HashMap<String, Sound>();
        sounds.put("blindness", loadSound("blindness"));
        sounds.put("slowness", loadSound("slow"));
        sounds.put("confusion", loadSound("confusion"));
        sounds.put("paralysis", loadSound("paralysis"));
        sounds.put("invisibility", loadSound("invisible"));
        sounds.put("strength", loadSound("strength"));
        sounds.put("healing", loadSound("healing"));
        sounds.put("speed", loadSound("speed"));
        sounds.put("hit", loadSound("hit"));
        sounds.put("kill", loadSound("monsterDie"));
        sounds.put("hurt", loadSound("ouch"));
        sounds.put("die", loadSound("playerDie"));
        sounds.put("coins", loadSound("coins"));

        startSplashScreen();
    }

    private Sound loadSound(String filename) {
        return Gdx.audio.newSound(Gdx.files.internal("assets/data/" + filename
                + ".ogg"));
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        // Create a viewport, scaled to the new size.
        Vector2 size = scaler.scale(width, height);
        float viewportWidth = size.x;
        float viewportHeight = size.y;
        viewport = new VirtualViewport(viewportWidth, viewportHeight);

        // Resize the camera to match the viewport's dimensions.
        camera.setToOrtho(false, viewport.width(), viewport.height());
        camera.update();

        // Centre the camera on the viewport.
        camera.position.set(0f, 0f, 0f);
    }

    @Override
    public void render() {
        update();
        super.render();
        draw();
    }

    private void update() {
        wasEscapePressed = isEscapePressed;
        isEscapePressed = Gdx.input.isKeyPressed(Keys.ESCAPE);
        wasLeftPressed = isLeftPressed;
        isLeftPressed = Gdx.input.isButtonPressed(Buttons.LEFT);

        float delta = Gdx.graphics.getDeltaTime();
        stateTime += delta;
        switch (mode) {
        case SplashScreen:
            updateSplashscreen(delta);
            break;
        case Playing:
            updatePlaying(delta);
            break;
        case Death:
            updateDeath(delta);
            break;
        }
    }

    private void updateSplashscreen(float delta) {
        if (wasEscapePressed && !isEscapePressed) {
            Gdx.app.exit();
        }

        if (wasLeftPressed && !isLeftPressed) {
            startGame();
        }
    }

    private void updateDeath(float delta) {
        if (wasEscapePressed && !isEscapePressed) {
            startSplashScreen();
        }
        else if (wasLeftPressed && !isLeftPressed) {
            startSplashScreen();
        }
    }

    private void updatePlaying(float delta) {
        if (wasEscapePressed && !isEscapePressed) {
            startSplashScreen();
            return;
        }

        boolean wasBallAlive = ball.hitPoints() > 0;
        if (wasBallAlive) {
            boolean isParalysed = stateTime < paralysedTime;
            if (!isParalysed) {
                bat.update(delta, updateMouse());
            }
            float speedMultiplier = (stateTime >= speedTime) ? 1.0f : 2.0f;
            float slowMultiplier = (stateTime >= slowTime) ? 1.0f : 0.5f;
            ball.update(delta * speedMultiplier * slowMultiplier,
                    !dungeon.isEmpty() && wasLeftPressed && !isLeftPressed);
            if (dungeon.isEmpty()) {
                ball.removeFromPlay();
            }
            if (wasBallAlive && ball.hitPoints() <= 0) {
                killTime = stateTime + 2;
            }
            for (int i = potions.size() - 1; i >= 0; i--) {
                Potion potion = potions.get(i);
                if (!potion.update(delta)) {
                    potions.remove(i);
                }
            }
            for (int i = coins.size() - 1; i >= 0; i--) {
                Coin coin = coins.get(i);
                if (!coin.update(delta)) {
                    coins.remove(i);
                }
            }
            dungeon.update(delta);
        }
        else if (stateTime >= killTime) {
            startDeathScreen();
        }
        messageRow.update(delta);
    }

    private void draw() {
        Gdx.gl.glClearColor(0f, 0f, 0.0f, 1f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        switch (mode) {
        case SplashScreen:
            drawSplashscreen();
            break;
        case Playing:
            drawPlaying();
            break;
        case Death:
            drawDeath();
            break;
        }
        spriteBatch.end();
    }

    private void drawDeath() {
        for (int i = 0, n = deathScreen.length; i < n; i++) {
            String line = deathScreen[i];
            if (line.contains("Level")) {
                line = String.format(line, dungeon.level());
            }
            else if (line.contains("XP")) {
                line = String.format(line, ball.xp());
            }
            else if (line.contains("Gold")) {
                line = String.format(line, ball.gold());
            }
            terminal.writeString(line, 17, terminal.height() - 4 - i);
        }
    }

    private void drawSplashscreen() {
        for (int i = 0, n = splashScreen.length; i < n; i++) {
            terminal.writeString(splashScreen[i], 1, terminal.height() - 2 - i);
        }
    }

    private void drawPlaying() {
        messageRow.draw();
        dungeon.draw();
        for (Potion potion : potions) {
            potion.draw();
        }
        for (Coin coin : coins) {
            coin.draw();
        }
        boolean isBlinded = stateTime < blindedTime;
        bat.draw(isBlinded);
        boolean isInvisible = stateTime < invisibleTime;
        ball.draw(isInvisible);
        statusRow.draw();
    }

    private float updateMouse() {
        float multiplier = (stateTime >= confusedTime) ? 1 : -1;
        mouseX += Gdx.input.getDeltaX() * multiplier;
        mouseX = Math.max(mouseMin, mouseX);
        mouseX = Math.min(mouseMax, mouseX);
        return mouseX;
    }

    private void startSplashScreen() {
        mode = Mode.SplashScreen;
        gameMusic.stop();
        splashScreenMusic.setLooping(true);
        splashScreenMusic.play();
    }

    private void startGame() {
        mode = Mode.Playing;
        splashScreenMusic.stop();
        gameMusic.setLooping(true);
        gameMusic.play();
        messageRow = new MessageRow(terminal);
        dungeon = new Dungeon(terminal);
        statusRow = new StatusRow(dungeon, terminal);
        bat = new Bat(spriteBatch, font, mouseMin, mouseMax, mouseY);
        ball = new Ball(this, dungeon, bat, spriteBatch, font);
        ball.setSpeed(200, 200);
        ball.setToast(messageRow);
        ball.setStatusRow(statusRow);
        statusRow.setBall(ball);
        statusRow.updateMessage();
        dungeon.setStatusRow(statusRow);
        potions = new ArrayList<Potion>();
        coins = new ArrayList<Coin>();
        mouseX = 0.0f;
        speedTime = 0;
        slowTime = 0;
        confusedTime = 0;
        paralysedTime = 0;
        blindedTime = 0;
        invisibleTime = 0;
        strongTime = 0;
    }

    private void startDeathScreen() {
        mode = Mode.Death;
        gameMusic.stop();
    }

    public void onMonsterKilled(Entity e) {
        if (dungeon.isEmpty()) {
            return;
        }
        float x = dungeon.screenX(e.x() + e.width() / 2);
        float y = dungeon.screenY(e.y());
        switch (MathUtils.random(4)) {
        case 0:
            addPotion(x, y);
            break;
        case 1:
            addCoin(x, y);
            break;
        }
    }

    private void addPotion(float x, float y) {
        Potion potion = new Potion(this, bat, spriteBatch, font, x, y);
        potions.add(potion);
    }

    private void addCoin(float x, float y) {
        Coin coin = new Coin(this, bat, spriteBatch, font, x, y);
        coins.add(coin);
    }

    public void onHitPotion(Potion potion) {
        switch (MathUtils.random(1, 8)) {
        case 1:
            // It's a potion of speed.
            speedTime = stateTime + 7;
            slowTime = 0;
            toast("You drink a potion of speed. You speed up.");
            playSound("speed");
            break;
        case 2:
            // It's a potion of slowness.
            slowTime = stateTime + 7;
            speedTime = 0;
            toast("You drink a potion of slowness. You slow down.");
            playSound("slowness");
            break;
        case 3:
            // It's a potion of confusion.
            confusedTime = stateTime + 7;
            toast("You drink a potion of confusion. Who? What? Where?");
            playSound("confusion");
            break;
        case 4:
            // It's a potion of paralysis.
            paralysedTime = stateTime + 3;
            toast("You drink a potion of paralysis. You can't move.");
            playSound("paralysis");
            break;
        case 5:
            // It's a potion of blindness.
            blindedTime = stateTime + 7;
            toast("You drink a potion of blindness. You can't see your bat.");
            playSound("blindness");
            break;
        case 6:
            // It's a potion of invisibility.
            invisibleTime = stateTime + 7;
            toast("You drink a potion of invisibility. You can't see yourself.");
            playSound("invisibility");
            break;
        case 7:
            // It's a potion of strength.
            strongTime = stateTime + 5;
            toast("You drink a potion of strength. You are very strong.");
            playSound("strength");
            break;
        case 8:
            // It's a potion of healing.
            ball.heal();
            toast("You drink a potion of healing. You feel much better.");
            playSound("healing");
            break;
        }
    }

    public void playSound(String name) {
        Sound sound = sounds.get(name);
        sound.play();
    }

    private void toast(String message) {
        messageRow.toast(message, 5);
    }

    public boolean isStrong() {
        return stateTime < strongTime;
    }

    public void onHitCoin(Coin coin) {
        int value = MathUtils.random(5, 50 + 10 * dungeon.level());
        ball.addGold(value);
        toast("You find " + value + " gold pieces.");
        playSound("coins");
    }
}

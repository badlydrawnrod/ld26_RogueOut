package ld26;

class StatusRow extends TerminalWriter {

    private String format = "Dungeon Level: [%2d]   HP: [%2d/%2d]   XP: [%3d]   Gold: [%5d]";
    private String message;
    private Dungeon dungeon;
    private Ball ball;

    public StatusRow(Dungeon dungeon, Terminal terminal) {
        super(terminal, terminal.width(), 1, 0);
        this.dungeon = dungeon;
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }

    public void updateMessage() {
        message = String.format(format, dungeon.level(), ball.hitPoints(),
                ball.maxHitPoints(), ball.xp(), ball.gold());
    }

    @Override
    public void draw() {
        write(message, 0, 0);
    }
}
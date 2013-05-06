package ld26;

abstract class TerminalWriter {
    protected final Terminal terminal;
    protected final int width;
    protected final int height;
    private final int ofs;

    public TerminalWriter(Terminal terminal, int width, int height, int ofs) {
        this.terminal = terminal;
        this.width = width;
        this.height = height;
        this.ofs = ofs;
    }

    abstract public void draw();

    protected void write(String s, int x, int y) {
        terminal.writeString(s, x, y + ofs);
    }
}
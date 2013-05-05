package ld26;

class MessageRow extends TerminalWriter implements Toast {

	private String message;
	private float expiryTime;
	
	public MessageRow(Terminal terminal) {
		super(terminal, terminal.width(), 1, terminal.height() - 1);
		expiryTime = 0;
	}

	public void update(float delta) {
		if (expiryTime > 0) {
			expiryTime -= delta;
		}
	}
	
	@Override
	public void toast(String message, float duration) {
		this.message = message;
		this.expiryTime = duration;
	}
	
	@Override
	public void draw() {
		if (expiryTime > 0) {
			write(message, 0, 0);
		}
	}
}

package ld26;

class Dungeon extends TerminalWriter {

	private final Entity[] grid;
	private int numEntities;
	private int level;
	private StatusRow statusRow;
	
	public Dungeon(Terminal terminal) {
		super(terminal, terminal.width(), terminal.height() - 2, 1);
		grid = new Entity[width * height];
		clearGrid();
		level = 0;
	}
	
	public void setStatusRow(StatusRow statusRow) {
		this.statusRow = statusRow;
	}

	public void update(float delta) {
		if (isEmpty()) {
			restock();
			updateStatus();
		}
	}
	
	private void updateStatus() {
		if (statusRow != null) {
			statusRow.updateMessage();
		}
	}

	public boolean isEmpty() {
		return numEntities == 0;
	}
	
	public void restock() {
		level++;
		clearGrid();
		
		// Add some orcs and goblins.
		for (int y = height / 2; y < height - 2; y += 3) {
			for (int x = 1; x < width - 4; x += 6) {
				BaseEntity monster;
				if (y < 11 * height / 16) {
					monster = makeGoblin();
				}
				else {
					monster = makeOrc();
				}
				put(monster, x, y);
			}
		}
		for (int x = 1; x < width - 4; x += 6) {
			put(makeAcidBlob(), x, height / 2 - 2);
			put(makeAcidBlob(), x + 3, height / 2 - 2);
		}
	}

	private void clearGrid() {
		for (int i = 0, n = grid.length; i < n; i++) {
			grid[i] = null;
		}
		numEntities = 0;
		
		// Add the ceiling and floor.
		for (int x = 1; x < width - 1; x += 2) {
			put(makeHorizontalWall(), x, height - 1);
			put(makeTrap(), x, 0);
		}
		
		// Add the top corners.
		put(makeCorner(), 0, height - 1);
		put(makeCorner(), width - 1, height - 1);
		
		// Add the side walls.
		for (int y = 0; y < height - 1; y++) {
			put(makeVerticalWall(), 0, y);
			put(makeVerticalWall(), width - 1, y);
		}
	}

	@Override
	public void draw() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Entity entity = get(x, y);
				if (entity == null) {
					write(".", x, y);
				}
				else {
					write(entity.appearance(), x, y);
				}
			}
		}
	}
	
	private Entity get(int x, int y) {
		return grid[x + y * width];
	}
	
	private void put(Entity entity, int x, int y) {
		if (entity.isMonster()) {
			numEntities++;
		}
		for (int i = x, n = x + entity.width(); i < n; i++) {
			grid[i + y * width] = entity;
		}
		entity.setPosition(x, y);
	}
	
	public void remove(Entity entity) {
		if (entity.isMonster()) {
			numEntities--;
		}
		for (int i = entity.x(), n = entity.x() + entity.width(); i < n; i++) {
			grid[i + entity.y() * width] = null;
		}
	}

	public Entity entityAt(float screenX, float screenY) {
		int cx = terminal.terminalX(screenX);
		int cy = terminal.terminalY(screenY) - 1;
		if (cx >= 0 && cx < width && cy >= 0 && cy < height) {
			return get(cx, cy);
		}
		return null;
	}

	private BaseEntity makeGoblin() {
		return new BaseEntity(this, "gggg", "the goblin", 0)
			.isMonster(true)
			.level(0);
	}

	private BaseEntity makeOrc() {
		return new BaseEntity(this, "oooo", "the orc", 0)
			.isMonster(true)
			.level(1);
	}

	private BaseEntity makeAcidBlob() {
		return new BaseEntity(this, "aa", "the acid blob", 0)
			.isMonster(true)
			.level(1);
	}

	private BaseEntity makeDwarfLord() {
		return new BaseEntity(this, "hhhh", "the dwarf lord", 0)
			.isMonster(true)
			.level(4);
	}
	
	private BaseEntity makeTrap() {
		return new BaseEntity(this, "^^", "a trap", 1)
			.setDestructible(1);
	}

	private BaseEntity makeHorizontalWall() {
		return new BaseEntity(this, "--");
	}

	private BaseEntity makeCorner() {
		return new BaseEntity(this, "+");
	}

	private BaseEntity makeVerticalWall() {
		return new BaseEntity(this, "|");
	}

	public boolean isValid(float screenX, float screenY) {
		int cx = terminal.terminalX(screenX);
		int cy = terminal.terminalY(screenY) - 1;
		return cx >= 0 && cx < width && cy >= 0 && cy < height;
	}
	
	public float screenX(int x) {
		return terminal.screenX(x);
	}
	
	public float screenY(int y) {
		return terminal.screenY(y);
	}

	public int level() {
		return level;
	}
}

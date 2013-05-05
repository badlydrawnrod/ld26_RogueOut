package ld26;

interface Entity {
	public String appearance();
	public int damage();
	public String damageMessage(int damage);
	public int width();
	public void setPosition(int x, int y);
	public boolean isMonster();
	public boolean hit(int damage);
	public String hitMessage(int damage);
	public void remove();
	public int x();
	public int y();
	public int xp();
	public boolean isDestructible();
	public int hitPoints();
}

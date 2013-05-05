package ld26;

import com.badlogic.gdx.math.Vector2;

public class ViewportScaler {
	private final float w;
	private final float h;
	private final float minAspect;
	private final float maxAspect;
	private final Vector2 result;
	
	public ViewportScaler(float w, float h) {
		this(w, h, w / h, w / h);
	}
	
	public ViewportScaler(float w, float h, float minAspect, float maxAspect) {
		this.w = w;
		this.h = h;
		this.minAspect = minAspect;
		this.maxAspect = maxAspect;
		this.result = new Vector2();
	}
	
	public Vector2 scale(float w, float h) {
		float aspect = w / h;
		if (aspect <= maxAspect) {
			// Keep h constant, changing w. If aspect < minAspect then this results in letterboxing at the top and
			// bottom of the viewport.
			result.set(this.h * Math.max(aspect,  minAspect), this.h);
		}
		else {
			// Keep w constant, changing h. This results in letterboxing at the sides of the viewport.
			result.set(this.w, this.w / maxAspect);
		}
		return result;
	}
}

package ld26;

import com.badlogic.gdx.Gdx;

/**
 * @author Gemserk
 * see: http://blog.gemserk.com/2013/02/13/our-solution-to-handle-multiple-screen-sizes-in-android-part-two/
 */
public class VirtualViewport {  
    
    float virtualWidth;  
    float virtualHeight;  
  
    public float virtualWidth() {  
        return virtualWidth;  
    }  
  
    public float virtualHeight() {  
        return virtualHeight;  
    }  
  
    public VirtualViewport(float virtualWidth, float virtualHeight) {  
        this(virtualWidth, virtualHeight, false);  
    }  
  
    public VirtualViewport(float virtualWidth, float virtualHeight, boolean shrink) {  
        this.virtualWidth = virtualWidth;  
        this.virtualHeight = virtualHeight;  
    }  
  
    public float width() {  
        return width(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());  
    }  
  
    public float height() {  
        return height(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());  
    }  
  
    /** 
     * Returns the view port width to let all the virtual view port to be shown on the screen. 
     *  
     * @param screenWidth 
     *            The screen width. 
     * @param screenHeight 
     *            The screen Height. 
     */  
    public float width(float screenWidth, float screenHeight) {  
        float virtualAspect = virtualWidth / virtualHeight;  
        float aspect = screenWidth / screenHeight;  
        if (aspect > virtualAspect || (Math.abs(aspect - virtualAspect) < 0.01f)) {  
            return virtualHeight * aspect;  
        } else {  
            return virtualWidth;  
        }  
    }  
  
    /** 
     * Returns the view port height to let all the virtual view port to be shown on the screen. 
     *  
     * @param screenWidth 
     *            The screen width. 
     * @param screenHeight 
     *            The screen Height. 
     */  
    public float height(float screenWidth, float screenHeight) {  
        float virtualAspect = virtualWidth / virtualHeight;  
        float aspect = screenWidth / screenHeight;  
        if (aspect > virtualAspect || (Math.abs(aspect - virtualAspect) < 0.01f)) {  
            return virtualHeight;  
        } else {  
            return virtualWidth / aspect;  
        }  
    }  
}
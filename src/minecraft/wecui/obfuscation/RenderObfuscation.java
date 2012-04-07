package wecui.obfuscation;

import net.minecraft.src.RenderHelper;
import net.minecraft.src.Tessellator;

/**
 * Singleton obfuscation class for dealing
 * with tesselator rendering. 
 * 
 * @author yetanotherx
 * @author lahwran
 * 
 * @obfuscated
 */
public class RenderObfuscation {

    protected Tessellator tess;
    
    protected RenderObfuscation() {
        tess = Tessellator.instance;
    }
    
    public void startDrawing(int type) {
        tess.startDrawing(type);
    }

    public void addVertex(double x, double y, double z) {
        tess.addVertex(x, y, z);
    }

    public void finishDrawing() {
        tess.draw();
    }
    
    public static void disableLighting() {
        RenderHelper.disableStandardItemLighting();
    }

    public static void enableLighting() {
        RenderHelper.enableStandardItemLighting();
    }

    public static RenderObfuscation getInstance() {
        return RenderObfuscationHolder.INSTANCE;
    }

    protected static class RenderObfuscationHolder {
        protected static final RenderObfuscation INSTANCE = new RenderObfuscation();
    }
}

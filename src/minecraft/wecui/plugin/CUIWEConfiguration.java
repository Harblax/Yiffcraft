package wecui.plugin;

import com.sk89q.worldedit.util.PropertiesConfiguration;
import java.io.File;

import de.doridian.yiffcraft.Yiffcraft;
import net.minecraft.client.Minecraft;
import wecui.WorldEditCUI;
import wecui.obfuscation.Obfuscation;

/**
 * Stub class for WorldEdit configuration. This places the
 * configuration file in the mods/ directory.
 * 
 * @author yetanotherx
 */
public class CUIWEConfiguration extends PropertiesConfiguration {

    protected WorldEditCUI controller;
    protected File directory;

	@Override
	public File getWorkingDirectory() {
		return Minecraft.getMinecraftDir();
	}

	public CUIWEConfiguration(WorldEditCUI controller) {
        super(new File(Obfuscation.getWorldEditCUIDir(), "WorldEdit-config.txt"));
        this.controller = controller;
    }
    
}

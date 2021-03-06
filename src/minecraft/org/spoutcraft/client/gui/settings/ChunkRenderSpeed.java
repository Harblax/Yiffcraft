/*
 * This file is part of Spoutcraft (http://www.spout.org/).
 *
 * Spoutcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spoutcraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spoutcraft.client.gui.settings;

import org.spoutcraft.client.config.ConfigReader;
import org.spoutcraft.spoutcraftapi.event.screen.ButtonClickEvent;

public class ChunkRenderSpeed extends AutomatedButton{
	public ChunkRenderSpeed() {
		
		setTooltip("Chunk Render Speed\nControls how fast chunks will render in new areas.\n Faster rendering may adversly affect FPS.");
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		ConfigReader.chunkRenderPasses *= 2;
		if (ConfigReader.chunkRenderPasses > 16) {
			ConfigReader.chunkRenderPasses = 1;
		}
		else if (ConfigReader.chunkRenderPasses < 1) {
			ConfigReader.chunkRenderPasses = 1;
		}
		ConfigReader.write();
	}

	public String getText() {
		switch(ConfigReader.chunkRenderPasses) {
			case 16: return "Render Speed: Very Fast";
			case 8: return "Render Speed: Fast";
			case 4: return "Render Speed: Average";
			case 2: return "Render Speed: Slow";
			case 1: return "Render Speed: Slowest";
		}
		return "Error, Unknown Setting!";
	}
}

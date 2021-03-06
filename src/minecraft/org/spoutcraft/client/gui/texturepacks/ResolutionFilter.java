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
package org.spoutcraft.client.gui.texturepacks;

import java.util.ArrayList;
import java.util.List;

import org.spoutcraft.client.SpoutClient;
import org.spoutcraft.client.gui.database.UrlElement;
import org.spoutcraft.spoutcraftapi.gui.GenericComboBox;

public class ResolutionFilter extends GenericComboBox implements UrlElement {
	TexturePacksDatabaseModel model = SpoutClient.getInstance().getTexturePacksDatabaseModel();

	private int possibilities[] = {
			8, 16, 32, 64, 128, 256, 512, 1024
	};

	public ResolutionFilter() {
		List<String> list = new ArrayList<String>();
		list.add("All");
		for (int r:possibilities) {
			list.add(r+"x"+r);
		}
		setItems(list);
	}

	public boolean isActive() {
		return getSelectedRow() != 0;
	}

	public String getUrlPart() {
		return "resolution="+possibilities[getSelectedRow()-1];
	}

	public void clear() {
		setSelection(0);
	}

	@Override
	public void onSelectionChanged(int item, String text) {
		model.updateUrl();
	}

	@Override
	public String getText() {
		return "Resolution: "+getSelectedItem();
	}
}

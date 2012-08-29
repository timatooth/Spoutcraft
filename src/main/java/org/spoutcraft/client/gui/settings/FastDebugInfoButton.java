/*
 * This file is part of Spoutcraft.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spoutcraft is licensed under the GNU Lesser General Public License.
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
import org.spoutcraft.spoutcraftapi.gui.GenericButton;

public class FastDebugInfoButton extends GenericButton {
	public FastDebugInfoButton() {
		setTooltip("Fast Debug Info\nFancy - default debug info screen, slower\nFast - debug info screen without lagometer, faster\nFPS Only - Shows only the frames per second, hiding debug information.");
	}

	@Override
	public String getText() {
		switch (ConfigReader.fastDebug) {
			case 0: return "Debug Info: Fancy";
			case 1: return "Debug Info: Fast";
			case 2: return "Debug Info: FPS Only";
		}
		return "Unknown State: " + ConfigReader.fastDebug;
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		ConfigReader.fastDebug++;
		if (ConfigReader.fastDebug > 2) {
			ConfigReader.fastDebug = 0;
		}
		ConfigReader.write();
	}
}

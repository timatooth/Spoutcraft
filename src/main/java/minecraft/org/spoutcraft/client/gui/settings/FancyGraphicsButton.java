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

import java.util.List;

import net.minecraft.client.Minecraft;

import org.spoutcraft.client.config.ConfigReader;
import org.spoutcraft.spoutcraftapi.event.screen.ButtonClickEvent;
import org.spoutcraft.spoutcraftapi.gui.CheckBox;

public class FancyGraphicsButton extends AutomatedButton {
	public boolean custom = false;
	private List<CheckBox> linkedButtons = null;
	public FancyGraphicsButton() {
		setTooltip("Visual quality\nFast  - lower quality, faster\nFancy - higher quality, slower\nChanges the appearance of clouds, leaves, water,\nshadows and grass sides.");
	}

	@Override
	public String getText() {
		return "Graphics: " + (custom ? "Custom" : (ConfigReader.fancyGraphics ? "Fancy" :"Fast"));
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		ConfigReader.fancyGraphics = !ConfigReader.fancyGraphics;
		for (CheckBox check : linkedButtons) {
			if (check.isChecked() != ConfigReader.fancyGraphics) {
				check.setChecked(ConfigReader.fancyGraphics);
				check.onButtonClick(event);
			}
		}
		Minecraft.theMinecraft.gameSettings.fancyGraphics = ConfigReader.fancyGraphics;
		if (Minecraft.theMinecraft.theWorld != null) {
			Minecraft.theMinecraft.renderGlobal.updateAllRenderers();
		}
		custom = false;
	}

	public void setLinkedButtons(List<CheckBox> linked) {
		linkedButtons = linked;
		for (CheckBox check : linkedButtons) {
			if (check.isChecked() != ConfigReader.fancyGraphics) {
				custom = true;
				break;
			}
		}
	}
}

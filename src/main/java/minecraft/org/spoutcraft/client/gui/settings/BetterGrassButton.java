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

import net.minecraft.client.Minecraft;

import org.spoutcraft.client.config.ConfigReader;
import org.spoutcraft.spoutcraftapi.event.screen.ButtonClickEvent;

public class BetterGrassButton extends AutomatedButton {
	public BetterGrassButton() {
		setTooltip("Better Grass\nOFF - default side grass texture, fastest\nFast - full side grass texture, slower\nFancy - dynamic side grass texture, slowest");
	}

	@Override
	public String getText() {
		switch(ConfigReader.betterGrass) {
			case 0: return "Better Grass: OFF";
			case 1: return "Better Grass: Fast";
			case 2: return "Better Grass: Fancy";
		}
		return "Unknown State: " + ConfigReader.betterGrass;
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		ConfigReader.betterGrass++;
		if (ConfigReader.betterGrass > 2) {
			ConfigReader.betterGrass = 0;
		}
		ConfigReader.write();

		if (Minecraft.theMinecraft.theWorld != null) {
			Minecraft.theMinecraft.renderGlobal.updateAllRenderers();
		}
	}
}

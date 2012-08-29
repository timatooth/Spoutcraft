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
import net.minecraft.src.ScaledResolution;

import org.spoutcraft.client.SpoutClient;
import org.spoutcraft.client.config.ConfigReader;
import org.spoutcraft.spoutcraftapi.event.screen.ButtonClickEvent;
import org.spoutcraft.spoutcraftapi.gui.GenericButton;

public class GuiScaleButton extends GenericButton {
	GameSettingsScreen parent;
	public GuiScaleButton(GameSettingsScreen parent) {
		this.parent = parent;
		setTooltip("Alters how the size of the display scales.");
	}

	@Override
	public String getText() {
		switch (ConfigReader.guiScale) {
			case 0: return "GUI Scale: Auto";
			case 1: return "GUI Scale: Small";
			case 2: return "GUI Scale: Normal";
			case 3: return "GUI Scale: Large";
		}
		return "Unknown State: " + ConfigReader.guiScale;
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		ConfigReader.guiScale += 1;
		ConfigReader.guiScale &= 3;
		Minecraft.theMinecraft.gameSettings.guiScale = ConfigReader.guiScale;
		ConfigReader.write();

		//Redisplay the video screen.
		ScaledResolution var3 = new ScaledResolution(Minecraft.theMinecraft.gameSettings, Minecraft.theMinecraft.displayWidth, Minecraft.theMinecraft.displayHeight);
		int width = var3.getScaledWidth();
		int height = var3.getScaledHeight();
		SpoutClient.getHandle().currentScreen.setWorldAndResolution(Minecraft.theMinecraft, width, height);
	}
}

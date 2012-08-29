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
package org.spoutcraft.client.gui.texturepacks;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.pclewis.mcpatcher.mod.TextureUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.src.FontRenderer;
import net.minecraft.src.TexturePackBase;
import net.minecraft.src.TexturePackImplementation;
import net.minecraft.src.TexturePackList;

import org.bukkit.ChatColor;
import org.spoutcraft.client.SpoutClient;
import org.spoutcraft.spoutcraftapi.Spoutcraft;
import org.spoutcraft.spoutcraftapi.gui.ListWidget;
import org.spoutcraft.spoutcraftapi.gui.ListWidgetItem;
import org.spoutcraft.spoutcraftapi.gui.MinecraftTessellator;

public class TexturePackItem implements ListWidgetItem {
	protected final static Map<String, Integer> texturePackSize = new HashMap<String, Integer>();
	protected volatile static TexturePackSizeThread activeThread = null;

	private TexturePackImplementation pack;
	private ListWidget widget;
	private TexturePackList packList = SpoutClient.getHandle().texturePackList;
	int id = -1;
	private String title = null;
	protected volatile int tileSize = -1;

	public TexturePackItem(TexturePackImplementation pack) {
		this.setPack(pack);
		synchronized(texturePackSize) {
			if (!texturePackSize.containsKey(getName())) {
				calculateTexturePackSize(pack, this);
			} else {
				tileSize = texturePackSize.get(getName());
			}
		}
	}

	public void setListWidget(ListWidget widget) {
		this.widget = widget;
	}

	public ListWidget getListWidget() {
		return widget;
	}

	public int getHeight() {
		return 29;
	}

	public void render(int x, int y, int width, int height) {
		updateQueue();

		MinecraftTessellator tessellator = Spoutcraft.getTessellator();
		FontRenderer font = SpoutClient.getHandle().fontRenderer;

		font.drawStringWithShadow(getName(), x+29, y+2, 0xffffffff);
		font.drawStringWithShadow(pack.func_77531_d(), x+29, y+11, 0xffaaaaaa);
		font.drawStringWithShadow(pack.func_77537_e(), x+29, y+20, 0xffaaaaaa);

		String sTileSize;
		if (tileSize != -1) {
			sTileSize = tileSize + "x";
		} else {
			sTileSize = ChatColor.YELLOW + "Calculating...";
		}
		int w = font.getStringWidth(sTileSize);
		font.drawStringWithShadow(sTileSize, width - 5 - w, y + 2, 0xffaaaaaa);

		//TODO: Show database information (author/member who posted it)

		pack.func_77535_b(SpoutClient.getHandle().renderEngine);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		tessellator.startDrawingQuads();
		tessellator.setColorOpaque(255,255,255);
		tessellator.addVertexWithUV(x + 2, y + 27, 0.0D, 0.0D, 1.0D);
		tessellator.addVertexWithUV(x + 27, y + 27, 0.0D, 1.0D, 1.0D);
		tessellator.addVertexWithUV(x + 27, y + 2, 0.0D, 1.0D, 0.0D);
		tessellator.addVertexWithUV(x + 2, y + 2, 0.0D, 0.0D, 0.0D);
		tessellator.draw();
	}

	public void onClick(int x, int y, boolean doubleClick) {
		if (doubleClick) {
			select();
		}
	}

	public void setPack(TexturePackImplementation pack) {
		this.pack = pack;
	}

	public TexturePackImplementation getPack() {
		return pack;
	}

	public String getName() {
		if (title == null) {
			String name = pack.func_77538_c();
			int suffix = name.lastIndexOf(".zip");
			if (suffix != -1) {
				name = name.substring(0, suffix);
			}
			int db = name.lastIndexOf(".id_");
			if (db != -1) {
				try {
					id = Integer.valueOf(name.substring(db + 4, name.length()));
				} catch(NumberFormatException e) {}
				name = name.substring(0, db);
			}
			name = name.replaceAll("_", " ");
			title = name;
		}
		return title;
	}

	public void select() {
		packList.setTexturePack(getPack());
		SpoutClient.getHandle().renderEngine.refreshTextures();
	}

	private static void updateQueue() {
		if (activeThread == null) {
			Thread thread = queued.poll();
			if (thread != null) {
				thread.start();
			}
		}
	}

	private static LinkedList<TexturePackSizeThread> queued = new LinkedList<TexturePackSizeThread>();
	private static void calculateTexturePackSize(TexturePackImplementation texturePack, TexturePackItem item) {
		if (activeThread == null) {
			activeThread = new TexturePackSizeThread(texturePack, item);
			activeThread.start();
		} else {
			queued.add(new TexturePackSizeThread(texturePack, item));
		}
	}
}

class TexturePackSizeThread extends Thread {
	TexturePackImplementation texturePack;
	TexturePackItem item;
	TexturePackSizeThread(TexturePackImplementation texturePack, TexturePackItem item) {
		this.texturePack = texturePack;
		this.item = item;
	}

	@Override
	public void run() {
		item.tileSize = TextureUtils.getTileSize(texturePack);
		synchronized(TexturePackItem.texturePackSize) {
			TexturePackItem.texturePackSize.put(getName(), item.tileSize);
		}

		TexturePackItem.activeThread = null;
	}
}

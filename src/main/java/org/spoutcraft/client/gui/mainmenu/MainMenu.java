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
package org.spoutcraft.client.gui.mainmenu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.src.GuiScreen;

import org.bukkit.ChatColor;

import org.spoutcraft.client.SpoutClient;
import org.spoutcraft.client.config.ConfigReader;
import org.spoutcraft.client.gui.MCRenderDelegate;
import org.spoutcraft.client.gui.addon.GuiAddonsLocal;
import org.spoutcraft.client.gui.settings.GameSettingsScreen;
import org.spoutcraft.client.io.CustomTextureManager;
import org.spoutcraft.client.io.FileUtil;
import org.spoutcraft.client.special.Holiday;
import org.spoutcraft.client.special.Resources;
import org.spoutcraft.spoutcraftapi.Spoutcraft;
import org.spoutcraft.spoutcraftapi.addon.Addon;
import org.spoutcraft.spoutcraftapi.gui.Button;
import org.spoutcraft.spoutcraftapi.gui.Color;
import org.spoutcraft.spoutcraftapi.gui.GenericButton;
import org.spoutcraft.spoutcraftapi.gui.GenericLabel;
import org.spoutcraft.spoutcraftapi.gui.GenericTexture;
import org.spoutcraft.spoutcraftapi.gui.Label;
import org.spoutcraft.spoutcraftapi.gui.RenderPriority;
import org.spoutcraft.spoutcraftapi.gui.Texture;
import org.spoutcraft.spoutcraftapi.gui.WidgetAnchor;

public class MainMenu extends GuiScreen {

	public static String mcVersion = "1.3.2";
	final static List<String> splashes = new ArrayList<String>(1000);
	Button singleplayer, multiplayer, textures, addons, about, options, fastLogin, quit;
	Texture background, logo;
	Label splashText, buildNumber, animate, debugText;
	static String timeOfDay = "";
	final static List<String> backgrounds = new ArrayList<String>();
	//Animate click delay
	private static final int CLICK_DELAY = 7;
	int clickDelay = 0;
	//debug
	long lastTime = System.currentTimeMillis();
	int lastFPS = 0;
	int fpsDelay = 0;

	public MainMenu() {
		splashText = new GenericLabel(getSplashText());
		fastLogin = new GenericButton(ChatColor.GREEN + "Fast Login");
		fastLogin.setVisible(ConfigReader.fastLogin);

		updateBackgrounds();

		Holiday holiday = Resources.getHoliday();
		if (holiday != null) {
			splashText.setText(holiday.getSplash());
		}

		//Randomize background order
		Random rand = new Random();
		//Randomize by swapping the first background with a random background in the list
		//Repeat sufficient times
		for (int i = 0; i < backgrounds.size() * 2; i++) {
			int newIndex = rand.nextInt(backgrounds.size());
			String temp = backgrounds.get(0);
			backgrounds.set(0, backgrounds.get(newIndex));
			backgrounds.set(newIndex, temp);
		}

		if (backgrounds.size() == 0) {
			System.out.println("Failed to find any backgrounds for " + timeOfDay);
			backgrounds.add("/res/background/day/day1.jpg");
		}

		background = new BackgroundTexture(backgrounds);
	}

	private static void updateBackgrounds() {
		if (!timeOfDay.equals(getTimeFolder())) {
			timeOfDay = getTimeFolder();
		} else {
			return;
		}

		int picture = 1;
		int pass = 0;
		StringBuilder builder = new StringBuilder();
		backgrounds.clear();
		while (true) {
			builder.append("/res/background/");
			builder.append(timeOfDay);
			builder.append("/");
			builder.append(timeOfDay);
			builder.append(picture);
			builder.append(pass == 0 ? ".png" : ".jpg");
			if (CustomTextureManager.getTextureFromJar(builder.toString()) != null) {
				backgrounds.add(builder.toString());
				picture++;
				pass = 0;
			} else if (pass == 0) {
				pass++;
			} else {
				break;
			}
			builder.setLength(0); //reset
		}
	}

	private static String getSplashText() {
		BufferedReader br = null;
		try {
			if (splashes.isEmpty()) {
				File splashTextFile = new File(FileUtil.getConfigDir(), "splashes.txt");
				//refresh every day
				if (!splashTextFile.exists() || (System.currentTimeMillis() - splashTextFile.lastModified() > (1L * 24 * 60 * 60 * 1000))) {
					URL test = new URL("http://cdn.spout.org/splashes.txt");
					HttpURLConnection urlConnect = (HttpURLConnection) test.openConnection();
					System.setProperty("http.agent", "");
					urlConnect.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");

					File temp = new File(FileUtil.getConfigDir(), "splashes.temp");
					if (temp.exists()) {
						temp.delete();
					}
					
					splashTextFile.delete();
					
					FileUtils.copyInputStreamToFile(urlConnect.getInputStream(), temp);
					FileUtils.moveFile(temp, splashTextFile);
				}
				br = new BufferedReader(new InputStreamReader(new FileInputStream(splashTextFile)));
				String line;
				splashes.clear();
				while ((line = br.readLine()) != null) {
					splashes.add(line);
				}
				br.close();
			}
			return splashes.get((new Random()).nextInt(splashes.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return "I <3 Spout's "+e.getMessage();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception ignore) {
				}
			}
		}
	}

	private static String getTimeFolder() {
		int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (hours < 6) {
			return "night";
		}
		if (hours < 12) {
			return "day";
		}
		if (hours < 20) {
			return "evening";
		}
		return "night";
	}

	public void initGui() {
		Addon spoutcraft = Spoutcraft.getAddonManager().getAddon("Spoutcraft");
		int textWidth;

		fastLogin.setGeometry(width - 110, height - 205, 100, 20);

		singleplayer = new GenericButton("Singleplayer");
		singleplayer.setGeometry(width - 110, height - 180, 100, 20);

		multiplayer = new GenericButton("Multiplayer");
		multiplayer.setGeometry(width - 110, height - 155, 100, 20);

		textures = new GenericButton("Textures");
		textures.setGeometry(width - 110, height - 130, 100, 20);

		addons = new GenericButton("Addons");
		addons.setGeometry(width - 110, height - 105, 100, 20);

		buildNumber = new GenericLabel(mcVersion + " b" + SpoutClient.getClientVersion());
		textWidth = Spoutcraft.getRenderDelegate().getMinecraftFont().getTextWidth(buildNumber.getText());
		buildNumber.setTextColor(new Color(0x6CC0DC));
		buildNumber.setGeometry(Math.min(90 - textWidth, width - 296 - textWidth), height - 99, 75, 20);

		about = new GenericButton("About");
		about.setGeometry(Math.min(98, width - 288), height - 105, 51, 20);

		options = new GenericButton("Options");
		options.setGeometry(Math.min(159, width - 227), height - 105, 51, 20);

		quit = new GenericButton("Quit");
		quit.setGeometry(Math.min(220, width - 166), height - 105, 51, 20);

		background.setGeometry(0, 0, width, height);
		background.setPriority(RenderPriority.Highest);
		background.setAnchor(WidgetAnchor.TOP_LEFT);
		background.setLocal(true);

		splashText.setGeometry(Math.min(100, width - 245), height - 135, 200, 12);
		splashText.setTextColor(new Color(0x6CC0DC));
		textWidth = Spoutcraft.getRenderDelegate().getMinecraftFont().getTextWidth(splashText.getText());
		float scale = ((width - 225F) / textWidth);
		splashText.setScale(Math.min(1.5F, scale));

		logo = new ScaledTexture("/res/logo/spoutcraft.png");
		((ScaledTexture) logo).setScale(Math.min(1F, (width - 135F) / 256F));
		logo.setGeometry(15, height - 185, 256, 64);
		logo.setLocal(true);
		logo.setDrawAlphaChannel(true);

		animate = new GenericLabel(ChatColor.ITALIC + "Animate");
		textWidth = Spoutcraft.getRenderDelegate().getMinecraftFont().getTextWidth(animate.getText());
		textWidth *= 75;
		textWidth /= 100;
		animate.setGeometry(width - textWidth - 2, height - 8, textWidth, 10);
		animate.setScale(0.75F);
		switch (ConfigReader.mainMenuState) {
			case 1:
				animate.setTextColor(new Color(0x00EE00));
				break;
			case 2:
				animate.setTextColor(new Color(0xFFFF00));
				break;
			case 3:
				animate.setTextColor(new Color(0xFF0000));
				break;
		}

		debugText = new GenericLabel();
		debugText.setTextColor(new Color(0xFFE303));
		debugText.setGeometry(1, 1, 12, 100);
		debugText.setVisible(false);

		this.getScreen().attachWidgets(spoutcraft, singleplayer, multiplayer, textures, addons, buildNumber, about, options, background, logo, splashText, fastLogin, quit, animate, debugText);
	}

	@Override
	public void buttonClicked(Button btn) {
		if (singleplayer == btn) {
			mc.displayGuiScreen(new org.spoutcraft.client.gui.singleplayer.GuiWorldSelection(this));
		}
		if (multiplayer == btn) {
			mc.displayGuiScreen(new org.spoutcraft.client.gui.server.GuiFavorites(this));
		}
		if (textures == btn) {
			mc.displayGuiScreen(new org.spoutcraft.client.gui.texturepacks.GuiTexturePacks());
		}
		if (addons == btn) {
			this.mc.displayGuiScreen(new GuiAddonsLocal());
		}
		if (about == btn) {
			this.mc.displayGuiScreen(new org.spoutcraft.client.gui.about.GuiNewAbout(this));
		}
		if (options == btn) {
			mc.displayGuiScreen(new GameSettingsScreen(this));
		}
		if (quit == btn) {
			mc.shutdownMinecraftApplet();
		}
		if (fastLogin == btn) {
			ConfigReader.fastLogin = !ConfigReader.fastLogin;
			ConfigReader.write();
			fastLogin.setText((ConfigReader.fastLogin ? ChatColor.GREEN : ChatColor.RED) + "Fast Login");
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float scroll) {
		super.drawScreen(mouseX, mouseY, scroll);

		if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
			mc.displayGuiScreen(new org.spoutcraft.client.gui.server.GuiFavorites(this));
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			mc.displayGuiScreen(new org.spoutcraft.client.gui.singleplayer.GuiWorldSelection(this));
		} else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			mc.displayGuiScreen(new GuiAddonsLocal());
		} else if (Keyboard.isKeyDown(Keyboard.KEY_T)) {
			mc.displayGuiScreen(new org.spoutcraft.client.gui.texturepacks.GuiTexturePacks());
		} else if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
			mc.displayGuiScreen(new GameSettingsScreen(this));
		}

		long time = System.currentTimeMillis();
		if (fpsDelay > 0) {
			fpsDelay--;
		} else {
			long diff = time - lastTime;
			int fps = (int) (1000 / Math.max(1, diff));
			lastFPS = fps;
			fpsDelay = CLICK_DELAY;
		}

		lastTime = time;

		debugText.setVisible(false);
		if (Keyboard.isKeyDown(Keyboard.KEY_F3)) {
			debugText.setVisible(true);
			debugText.setText("FPS: " + lastFPS);
		}

		if (clickDelay > 0) {
			clickDelay--;
		}
		if (Mouse.isButtonDown(0) && this.isInBoundingRect(animate, mouseX, mouseY) && clickDelay == 0) {
			ConfigReader.mainMenuState++;
			if (ConfigReader.mainMenuState > 3) {
				ConfigReader.mainMenuState = 1;
			}
			ConfigReader.write();
			switch (ConfigReader.mainMenuState) {
				case 1:
					animate.setTextColor(new Color(0x00EE00));
					break;
				case 2:
					animate.setTextColor(new Color(0xFFFF00));
					break;
				case 3:
					animate.setTextColor(new Color(0xFF0000));
					break;
			}
			clickDelay = CLICK_DELAY;
		}

		GL11.glEnable(GL11.GL_BLEND);
	}
}

class ScaledTexture extends GenericTexture {

	float scale;

	ScaledTexture(String path) {
		super(path);
	}

	public ScaledTexture setScale(float scale) {
		this.scale = scale;
		return this;
	}

	@Override
	public void render() {
		GL11.glPushMatrix();
		GL11.glScalef(scale, 1F, 1F);
		super.render();
		GL11.glPopMatrix();
	}
}

class BackgroundTexture extends GenericTexture {

	static final int PAN_TIME = 600;
	static final int EXTRA_PAN_TIME = 150;
	static final int HEIGHT_PERCENT = 70;
	static final int WIDTH_PERCENT = 75;
	final List<String> backgrounds;
	final Random rand = new Random();
	int maxPanTime = PAN_TIME;
	int panTime = PAN_TIME;
	int picture = -1;
	boolean zoomIn = false;

	BackgroundTexture(List<String> backgrounds) {
		super(backgrounds.get(0));
		this.backgrounds = backgrounds;
		cycleBackground();
	}

	public void cycleBackground() {
		picture++;
		if (picture >= backgrounds.size()) {
			picture = 0;
		}
		setUrl(backgrounds.get(picture));
		maxPanTime = PAN_TIME + rand.nextInt(EXTRA_PAN_TIME);
		zoomIn = rand.nextBoolean();
		panTime = zoomIn ? 0 : maxPanTime;
	}

	@Override
	public void render() {
		org.newdawn.slick.opengl.Texture tex = CustomTextureManager.getTextureFromJar(getUrl());
		GL11.glPushMatrix();
		if (tex != null) {
			if (ConfigReader.mainMenuState != 3) {
				animate(tex);
			} else {
				((MCRenderDelegate) Spoutcraft.getRenderDelegate()).drawTexture(tex, (int) this.getActualWidth(), (int) this.getActualHeight(), white, false, -1, -1, false, GL11.GL_LINEAR);
			}
		}
		GL11.glPopMatrix();
	}
	private static final Color white = new Color(1.0F, 1.0F, 1.0F);

	private void animate(org.newdawn.slick.opengl.Texture tex) {
		int adjustedX = ((100 - HEIGHT_PERCENT) / 2) * tex.getImageHeight() * panTime;
		adjustedX /= maxPanTime;
		adjustedX /= 100;

		int adjustedY = ((100 - WIDTH_PERCENT) / 2) * tex.getImageWidth() * panTime;
		adjustedY /= maxPanTime;
		adjustedY /= 100;

		int adjustedHeight = tex.getImageHeight() - adjustedX;

		int adjustedWidth = tex.getImageWidth() - adjustedY;

		GL11.glScaled(this.getActualWidth() / (adjustedWidth - adjustedX), this.getActualHeight() / (adjustedHeight - adjustedY), 1D);
		GL11.glTranslatef(-adjustedX, -adjustedY, 0F);
		((MCRenderDelegate) Spoutcraft.getRenderDelegate()).drawTexture(tex, adjustedWidth, adjustedHeight, white, false, -1, -1, ConfigReader.mainMenuState == 1, GL11.GL_NEAREST);

		if (zoomIn && panTime < maxPanTime) {
			panTime++;
		} else if (!zoomIn && panTime > 0) {
			panTime--;
		} else {
			cycleBackground();
		}
	}
}

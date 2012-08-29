package net.minecraft.src;

import java.util.List;
import net.minecraft.client.Minecraft;
//Spout Start
import org.bukkit.ChatColor;
//Spout End
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.spoutcraft.client.config.ConfigReader;
import org.spoutcraft.spoutcraftapi.Spoutcraft;

public class GuiEditSign extends GuiScreen {
	/** The title string that is displayed in the top-center of the screen. */
	protected String screenTitle;

	/** Reference to the sign object. */
	private TileEntitySign entitySign;

	/** Counts the number of screen updates. */
	private int updateCounter;

	/** The number of the line that is being edited. */
	private int editLine;

	/**
	 * This String is just a local copy of the characters allowed in text
	 * rendering of minecraft.
	 */
	private static final String allowedCharacters;

	// Spout start
	private int editColumn = 0;
	// Spout end

	public GuiEditSign(TileEntitySign par1TileEntitySign) {
		screenTitle = "Edit sign message:";
		editLine = 0;
		entitySign = par1TileEntitySign;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui() {
		controlList.clear();
		Keyboard.enableRepeatEvents(true);
		controlList.add(new GuiButton(0, width / 2 - 100, height / 4 + 120, "Done"));
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat
	 * events
	 */
	public void onGuiClosed() {
		// Spout start
		entitySign.lineBeingEdited = -1;
		entitySign.columnBeingEdited = -1;
		entitySign.recalculateText();
		// Colorize text
		if (sendAsUnicode()) {
			for (int i = 0; i < entitySign.signText.length; i++) {
				if (entitySign.signText[i] != null)
					entitySign.signText[i] = entitySign.signText[i].replaceAll("(&([a-fA-F0-9]))", "\u00A7$2");
			}
		}
		// Spout end
		Keyboard.enableRepeatEvents(false);

		if (mc.theWorld.isRemote) {
			mc.getSendQueue().addToSendQueue(new Packet130UpdateSign(entitySign.xCoord, entitySign.yCoord, entitySign.zCoord, entitySign.signText));
		}
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		updateCounter++;
	}

	/**
	 * Fired when a control is clicked. This is the equivalent of
	 * ActionListener.actionPerformed(ActionEvent e).
	 */
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (!par1GuiButton.enabled) {
			return;
		}

		if (par1GuiButton.id == 0) {
			// Spout start
			if (!Spoutcraft.hasPermission("spout.client.signcolors")) {
				for (int i = 0; i < entitySign.signText.length; i++) {
					entitySign.signText[i] = ChatColor.stripColor(entitySign.signText[i]);
				}
			}
			// Spout end
			entitySign.onInventoryChanged();
			mc.displayGuiScreen(null);
		}
	}

	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	// Spout - rewritten method
	// Spout Start
	protected void keyTyped(char var1, int var2) {
		if (var2 == 200) { // up
			this.editLine = this.editLine - 1 & 3;
			editColumn = entitySign.signText[editLine].length();
		}

		if (var2 == 208 || var2 == 28) { // down
			this.editLine = this.editLine + 1 & 3;
			editColumn = entitySign.signText[editLine].length();
		}
		if (var2 == 205) { // right
			editColumn++;
			if (editColumn > entitySign.signText[editLine].length()) {
				editColumn--;
			}
		}
		if (var2 == 203) {// left
			editColumn--;
			if (editColumn < 0) {
				editColumn = 0;
			}
		}
		if (var2 == 14 && this.entitySign.signText[this.editLine].length() > 0) { // backsp
			String line = entitySign.signText[editLine];
			int endColumnStart = Math.min(editColumn, line.length());
			String before = "";
			if (endColumnStart > 0) {
				before = line.substring(0, endColumnStart);
			}
			String after = "";
			if (line.length() - editColumn > 0) {
				after = line.substring(editColumn, line.length());
			}
			if (before.length() > 0) {
				before = before.substring(0, before.length() - 1);
				line = before + after;
				entitySign.signText[editLine] = line;
				endColumnStart--;
				editColumn = endColumnStart;
				if (editColumn < 0) {
					editColumn = 0;
				}
			}
		}
		if ((allowedCharacters.indexOf(var1) > -1 || var1 > 32) && this.entitySign.signText[this.editLine].length() < 15) { // enter
			String line = entitySign.signText[editLine];

			// prevent out of bounds on the substring call
			int endColumnStart = Math.min(editColumn, line.length());
			String before = "";
			if (endColumnStart > 0) {
				before = line.substring(0, endColumnStart);
			}
			String after = "";
			if (line.length() - endColumnStart > 0) {
				after = line.substring(endColumnStart, line.length());
			}
			before += var1;
			line = before + after;
			entitySign.signText[editLine] = line;
			endColumnStart++;
			editColumn = endColumnStart;
		}
		if (var2 == 211) // del
		{
			String line = entitySign.signText[editLine];
			String before = line.substring(0, editColumn);
			String after = "";
			if (line.length() - editColumn > 0) {
				after = line.substring(editColumn, line.length());
			}
			if (after.length() > 0) {
				after = after.substring(1, after.length());
				line = before + after;
				entitySign.signText[editLine] = line;
			}
		}
		entitySign.recalculateText();
	}

	// Spout End

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int x, int y, float z) {
		drawDefaultBackground();
		drawCenteredString(fontRenderer, screenTitle, width / 2, 40, 0xffffff);

		// Spout Start
		if (org.spoutcraft.client.config.ConfigReader.showChatColors) {
			for (int c = 0; c < 16; c++) {
				ChatColor value = ChatColor.getByCode(c);
				String name = value.name().toLowerCase();
				boolean lastUnderscore = true;
				String parsedName = "";
				for (int chr = 0; chr < name.length(); chr++) {
					char ch = name.charAt(chr);
					if (lastUnderscore) {
						ch = Character.toUpperCase(ch);
					}
					if (ch == '_') {
						lastUnderscore = true;
						ch = ' ';
					} else {
						lastUnderscore = false;
					}
					parsedName += ch;
				}
				char code = (char) ('0' + c);
				if (c >= 10) {
					code = (char) ('a' + c - 10);
				}
				fontRenderer.drawStringWithShadow("&" + code + " - " + value + parsedName, width - 90, 70 + c * 10, 0xffffffff);
			}
		}
		// Spout end

		GL11.glPushMatrix();
		GL11.glTranslatef(width / 2, 0.0F, 50F);
		float f = 93.75F;
		GL11.glScalef(-f, -f, -f);
		GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
		Block block = entitySign.getBlockType();

		if (block == Block.signPost) {
			float f1 = (float) (entitySign.getBlockMetadata() * 360) / 16F;
			GL11.glRotatef(f1, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(0.0F, -1.0625F, 0.0F);
		} else {
			int i = entitySign.getBlockMetadata();
			float f2 = 0.0F;

			if (i == 2) {
				f2 = 180F;
			}

			if (i == 4) {
				f2 = 90F;
			}

			if (i == 5) {
				f2 = -90F;
			}

			GL11.glRotatef(f2, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(0.0F, -1.0625F, 0.0F);
		}

		// Spout start
		// if(this.updateCounter / 6 % 2 == 0) {
		this.entitySign.lineBeingEdited = this.editLine;
		entitySign.columnBeingEdited = editColumn;
		// }
		// Spout end

		TileEntityRenderer.instance.renderTileEntityAt(entitySign, -0.5D, -0.75D, -0.5D, 0.0F);
		// Spout start
		this.entitySign.lineBeingEdited = -1;
		entitySign.columnBeingEdited = -1;
		// Spout end
		GL11.glPopMatrix();
		super.drawScreen(x, y, z);
	}

	// Spout start
	public boolean sendAsUnicode() {
		return !this.mc.theWorld.isRemote;
	}
	// Spout end

	static {
		allowedCharacters = ChatAllowedCharacters.allowedCharacters;
	}
}

package net.minecraft.src;

//Spout HD Start
import com.pclewis.mcpatcher.mod.TextureUtils;
import com.pclewis.mcpatcher.mod.TileSize;
//Spout HD End
import net.minecraft.src.RenderEngine;
import org.lwjgl.opengl.GL11;

public class TextureFX {
	//Spout HD Start
	public byte[] imageData;
	public int iconIndex;
	public boolean anaglyphEnabled;
	public int textureId;
	public int tileSize;
	public int tileImage;
	//Spout HD End

	public TextureFX(int par1) {
		// Spout HD start
		this.imageData = new byte[TileSize.int_numBytes];
		this.anaglyphEnabled = false;
		this.textureId = 0;
		this.tileSize = 1;
		this.tileImage = 0;
		// Spout HD end
		this.iconIndex = par1;
	}

	public void onTick() {}

	public void bindImage(RenderEngine par1RenderEngine) {
		if (TextureUtils.bindImageBegin()) { // Spout HD
		if (this.tileImage == 0) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, par1RenderEngine.getTexture("/terrain.png"));
		} else if (this.tileImage == 1) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, par1RenderEngine.getTexture("/gui/items.png"));
		}
		// Spout HD start
			TextureUtils.bindImageEnd();
		}
		// Spout HD end
	}
}

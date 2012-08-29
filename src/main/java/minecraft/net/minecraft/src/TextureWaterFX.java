package net.minecraft.src;

import com.pclewis.mcpatcher.mod.TileSize; // Spout HD 

public class TextureWaterFX extends TextureFX {
	// Spout HD start
	/** red RGB value for water texture */
	protected float[] red;

	/** green RGB value for water texture */
	protected float[] green;

	/** blue RGB value for water texture */
	protected float[] blue;

	/** alpha RGB value for water texture */
	protected float[] alpha;
	private int tickCounter;
	// Spout HD end

	public TextureWaterFX() {
		super(Block.waterMoving.blockIndexInTexture);
		// Spout HD start
		this.red = new float[TileSize.int_numPixels];
		this.green = new float[TileSize.int_numPixels];
		this.blue = new float[TileSize.int_numPixels];
		this.alpha = new float[TileSize.int_numPixels];
		this.tickCounter = 0;
		// Spout HD end
	}

	public void onTick() {
		++this.tickCounter;
		int var1;
		int var2;
		float var3;
		int var5;
		int var6;
		// Spout HD start
		for (var1 = 0; var1 < TileSize.int_size; ++var1) {
			for (var2 = 0; var2 < TileSize.int_size; ++var2) {
				// Spout HD end
				var3 = 0.0F;

				for (int var4 = var1 - 1; var4 <= var1 + 1; ++var4) {
					// Spout HD start
					var5 = var4 & TileSize.int_sizeMinus1;
					var6 = var2 & TileSize.int_sizeMinus1;
					var3 += this.red[var5 + var6 * TileSize.int_size];
					// Spout HD end
				}

				this.green[var1 + var2 * TileSize.int_size] = var3 / 3.3F + this.blue[var1 + var2 * TileSize.int_size] * 0.8F; // Spout HD 
			}
		}
		// Spout HD start
		for (var1 = 0; var1 < TileSize.int_size; ++var1) {
			for (var2 = 0; var2 < TileSize.int_size; ++var2) {
				this.blue[var1 + var2 * TileSize.int_size] += this.alpha[var1 + var2 * TileSize.int_size] * 0.05F;

				if (this.blue[var1 + var2 * TileSize.int_size] < 0.0F) {
					this.blue[var1 + var2 * TileSize.int_size] = 0.0F;
				}

				this.alpha[var1 + var2 * TileSize.int_size] -= 0.1F;

				if (Math.random() < 0.05D) {
					this.alpha[var1 + var2 * TileSize.int_size] = 0.5F;
					// Spout HD end
				}
			}
		}

		float[] var12 = this.green;
		this.green = this.red;
		this.red = var12;

		for (var2 = 0; var2 < TileSize.int_numPixels; ++var2) { // Spout HD 
			var3 = this.red[var2];

			if (var3 > 1.0F) {
				var3 = 1.0F;
			}

			if (var3 < 0.0F) {
				var3 = 0.0F;
			}

			float var13 = var3 * var3;
			var5 = (int)(32.0F + var13 * 32.0F);
			var6 = (int)(50.0F + var13 * 64.0F);
			int var7 = 255;
			int var8 = (int)(146.0F + var13 * 50.0F);

			if (this.anaglyphEnabled) {
				int var9 = (var5 * 30 + var6 * 59 + var7 * 11) / 100;
				int var10 = (var5 * 30 + var6 * 70) / 100;
				int var11 = (var5 * 30 + var7 * 70) / 100;
				var5 = var9;
				var6 = var10;
				var7 = var11;
			}

			this.imageData[var2 * 4 + 0] = (byte)var5;
			this.imageData[var2 * 4 + 1] = (byte)var6;
			this.imageData[var2 * 4 + 2] = (byte)var7;
			this.imageData[var2 * 4 + 3] = (byte)var8;
		}
	}
}

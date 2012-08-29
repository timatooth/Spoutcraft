package net.minecraft.src;

//Spout HD Start
import com.pclewis.mcpatcher.mod.TextureUtils;
import com.pclewis.mcpatcher.mod.TileSize;
//Spout HD End
import java.awt.image.BufferedImage;
import java.io.IOException;
import net.minecraft.client.Minecraft;

public class TextureCompassFX extends TextureFX {

	private Minecraft mc;
	//Spout HD Start
	private int[] compassIconImageData; // Spout HD
	//Spout HD End
	private double field_76868_i;
	private double field_76866_j;

	public TextureCompassFX(Minecraft par1Minecraft) {
		super(Item.compass.getIconFromDamage(0));
		this.compassIconImageData = new int[TileSize.int_numPixels]; // Spout HD
		this.mc = par1Minecraft;
		this.tileImage = 1;

		try {
			//Spout HD Start
			BufferedImage var2 = TextureUtils.getResourceAsBufferedImage((Object)Minecraft.class, "/gui/items.png");
			int var3 = this.iconIndex % 16 * TileSize.int_size;
			int var4 = this.iconIndex / 16 * TileSize.int_size;
			var2.getRGB(var3, var4, TileSize.int_size, TileSize.int_size, this.compassIconImageData, 0, TileSize.int_size);
			//Spout HD End
		} catch (IOException var5) {
			var5.printStackTrace();
		}

	}

	public void onTick() {
		//Spout HD Start
		for (int var1 = 0; var1 < TileSize.int_numPixels; ++var1) {
			//Spout HD End
			int var2 = this.compassIconImageData[var1] >> 24 & 255;
			int var3 = this.compassIconImageData[var1] >> 16 & 255;
			int var4 = this.compassIconImageData[var1] >> 8 & 255;
			int var5 = this.compassIconImageData[var1] >> 0 & 255;
			if (this.anaglyphEnabled) {
				int var6 = (var3 * 30 + var4 * 59 + var5 * 11) / 100;
				int var7 = (var3 * 30 + var4 * 70) / 100;
				int var8 = (var3 * 30 + var5 * 70) / 100;
				var3 = var6;
				var4 = var7;
				var5 = var8;
			}

			this.imageData[var1 * 4 + 0] = (byte)var3;
			this.imageData[var1 * 4 + 1] = (byte)var4;
			this.imageData[var1 * 4 + 2] = (byte)var5;
			this.imageData[var1 * 4 + 3] = (byte)var2;
		}

		double var20 = 0.0D;
		if (this.mc.theWorld != null && this.mc.thePlayer != null) {
			ChunkCoordinates var21 = this.mc.theWorld.getSpawnPoint();
			double var23 = (double)var21.posX - this.mc.thePlayer.posX;
			double var25 = (double)var21.posZ - this.mc.thePlayer.posZ;
			var20 = (double)(this.mc.thePlayer.rotationYaw - 90.0F) * Math.PI / 180.0D - Math.atan2(var25, var23);

			if (!this.mc.theWorld.provider.isSurfaceWorld()) {
				var20 = Math.random() * Math.PI * 2.0D;
			}
		}

		double var22;
		for (var22 = var20 - this.field_76868_i; var22 < -Math.PI; var22 += (Math.PI * 2D)) {
			;
		}

		while (var22 >= Math.PI) {
			var22 -= (Math.PI * 2D);
		}

		if (var22 < -1.0D) {
			var22 = -1.0D;
		}

		if (var22 > 1.0D) {
			var22 = 1.0D;
		}

		this.field_76866_j += var22 * 0.1D;
		this.field_76866_j *= 0.8D;
		this.field_76868_i += this.field_76866_j;
		double var24 = Math.sin(this.field_76868_i);
		double var26 = Math.cos(this.field_76868_i);

		int var9;
		int var10;
		int var11;
		int var12;
		int var13;
		int var14;
		int var15;
		int var17;
		short var16;
		int var19;
		int var18;
		//Spout HD start
		for (var9 = TileSize.int_compassCrossMin; var9 <= TileSize.int_compassCrossMax; ++var9) {
			var10 = (int)(TileSize.double_compassCenterMax + var26 * (double)var9 * 0.3D);
			var11 = (int)(TileSize.double_compassCenterMin - var24 * (double)var9 * 0.3D * 0.5D);
			var12 = var11 * TileSize.int_size + var10;
			//Spout HD end
			var13 = 100;
			var14 = 100;
			var15 = 100;
			var16 = 255;
			if (this.anaglyphEnabled) {
				var17 = (var13 * 30 + var14 * 59 + var15 * 11) / 100;
				var18 = (var13 * 30 + var14 * 70) / 100;
				var19 = (var13 * 30 + var15 * 70) / 100;
				var13 = var17;
				var14 = var18;
				var15 = var19;
			}

			this.imageData[var12 * 4 + 0] = (byte)var13;
			this.imageData[var12 * 4 + 1] = (byte)var14;
			this.imageData[var12 * 4 + 2] = (byte)var15;
			this.imageData[var12 * 4 + 3] = (byte)var16;
		}

		//Spout HD Start
		for (var9 = TileSize.int_compassNeedleMin; var9 <= TileSize.int_compassNeedleMax; ++var9) {
			var10 = (int)(TileSize.double_compassCenterMax + var24 * (double)var9 * 0.3D);
			var11 = (int)(TileSize.double_compassCenterMin + var26 * (double)var9 * 0.3D * 0.5D);
			var12 = var11 * TileSize.int_size + var10;
			//Spout HD End
			var13 = var9 >= 0 ? 255 : 100;
			var14 = var9 >= 0 ? 20 : 100;
			var15 = var9 >= 0 ? 20 : 100;
			var16 = 255;
			if (this.anaglyphEnabled) {
				var17 = (var13 * 30 + var14 * 59 + var15 * 11) / 100;
				var18 = (var13 * 30 + var14 * 70) / 100;
				var19 = (var13 * 30 + var15 * 70) / 100;
				var13 = var17;
				var14 = var18;
				var15 = var19;
			}

			this.imageData[var12 * 4 + 0] = (byte)var13;
			this.imageData[var12 * 4 + 1] = (byte)var14;
			this.imageData[var12 * 4 + 2] = (byte)var15;
			this.imageData[var12 * 4 + 3] = (byte)var16;
		}
	}
}

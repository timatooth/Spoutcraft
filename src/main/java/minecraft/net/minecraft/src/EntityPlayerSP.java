package net.minecraft.src;

import net.minecraft.client.Minecraft;
//Spout start
import org.bukkit.ChatColor;
import org.spoutcraft.client.SpoutClient;
import org.spoutcraft.client.config.ConfigReader;
import org.spoutcraft.client.gui.minimap.GuiAddWaypoint;
import org.spoutcraft.client.gui.minimap.GuiOverviewMap;
import org.spoutcraft.client.packet.PacketRenderDistance;
import org.spoutcraft.client.player.ClientPlayer;
import org.spoutcraft.client.special.Resources;
import org.spoutcraft.spoutcraftapi.Spoutcraft;
import org.spoutcraft.spoutcraftapi.util.FixedLocation;
//Spout end

public class EntityPlayerSP extends EntityPlayer {

	//public MovementInput movementInput; //Spout moved to EntityPlayer
	protected Minecraft mc;
	protected int sprintToggleTimer = 0;
	public float renderArmYaw;
	public int sprintingTicksLeft = 0;
	public float renderArmPitch;
	public float prevRenderArmYaw;
	public float prevRenderArmPitch;
	private MouseFilter field_71162_ch = new MouseFilter();
	private MouseFilter field_21904_bK = new MouseFilter();
	private MouseFilter field_21902_bL = new MouseFilter();
	//Spout start
	public FixedLocation lastClickLocation = null;
	private KeyBinding fogKey = null;
	//Spout end

	public EntityPlayerSP(Minecraft par1Minecraft, World par2World, Session par3Session, int par4) {
		super(par2World);
		this.mc = par1Minecraft;
		this.dimension = par4;
		//Spout start
		if (par3Session != null && par3Session.username != null && par3Session.username.length() > 0) {
			this.skinUrl = "http://static.spout.org/skin/" + ChatColor.stripColor(par3Session.username) + ".png";
			this.vip = Resources.getVIP(ChatColor.stripColor(par3Session.username));
			if (vip != null) {
				this.displayName = vip.getTitle();
			} else {
				displayName = par3Session.username;
			}
		}
		//Spout end

		this.username = par3Session.username;
		//Spout start
		spoutEntity = ClientPlayer.getInstance();
		((ClientPlayer) spoutEntity).setPlayer(this);
		SpoutClient.getInstance().player = (ClientPlayer) spoutEntity;
		this.worldObj.releaseEntitySkin(this);
		this.worldObj.obtainEntitySkin(this);
		//Spout end
	}

	public void moveEntity(double par1, double par3, double par5) {
		super.moveEntity(par1, par3, par5);
	}

	public void updateEntityActionState() {
		super.updateEntityActionState();
		this.moveStrafing = this.movementInput.moveStrafe;
		this.moveForward = this.movementInput.moveForward;
		this.isJumping = this.movementInput.jump || this.isTreadingWater(); //Spout
		this.prevRenderArmYaw = this.renderArmYaw;
		this.prevRenderArmPitch = this.renderArmPitch;
		this.renderArmPitch = (float)((double)this.renderArmPitch + (double)(this.rotationPitch - this.renderArmPitch) * 0.5D);
		this.renderArmYaw = (float)((double)this.renderArmYaw + (double)(this.rotationYaw - this.renderArmYaw) * 0.5D);
	}

	protected boolean isClientWorld() {
		return true;
	}

	public void onLivingUpdate() {
		if (this.sprintingTicksLeft > 0 && !runToggle) { //Spout
			--this.sprintingTicksLeft;
			if (this.sprintingTicksLeft == 0) {
				this.setSprinting(false);
			}
		}

		if (this.sprintToggleTimer > 0) {
			--this.sprintToggleTimer;
		}

		if (this.mc.playerController.func_78747_a()) {
			this.posX = this.posZ = 0.5D;
			this.posX = 0.0D;
			this.posZ = 0.0D;
			this.rotationYaw = (float)this.ticksExisted / 12.0F;
			this.rotationPitch = 10.0F;
			this.posY = 68.5D;
		} else {
			if (!this.mc.statFileWriter.hasAchievementUnlocked(AchievementList.openInventory)) {
				//this.mc.guiAchievement.queueAchievementInformation(AchievementList.openInventory); //Spout this was bugging me
			}

			this.prevTimeInPortal = this.timeInPortal;
			if (this.inPortal) {
				if (this.mc.currentScreen != null) {
					this.mc.displayGuiScreen((GuiScreen)null);
				}

				if (this.timeInPortal == 0.0F) {
					this.mc.sndManager.playSoundFX("portal.trigger", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
				}

				this.timeInPortal += 0.0125F;
				if (this.timeInPortal >= 1.0F) {
					this.timeInPortal = 1.0F;
				}

				this.inPortal = false;
			} else if (this.isPotionActive(Potion.confusion) && this.getActivePotionEffect(Potion.confusion).getDuration() > 60) {
				this.timeInPortal += 0.006666667F;
				if (this.timeInPortal > 1.0F) {
					this.timeInPortal = 1.0F;
				}
			} else {
				if (this.timeInPortal > 0.0F) {
					this.timeInPortal -= 0.05F;
				}

				if (this.timeInPortal < 0.0F) {
					this.timeInPortal = 0.0F;
				}
			}

			if (this.timeUntilPortal > 0) {
				--this.timeUntilPortal;
			}
			
			//Spout start
			boolean wasFlightUp = movementInput.flyingUp;
			//Spout end

			boolean var1 = this.movementInput.jump;
			float var2 = 0.8F;
			boolean var3 = this.movementInput.moveForward >= var2;
			this.movementInput.updatePlayerMoveState(this); //Spout - kept parameter
			if (this.isUsingItem()) {
				this.movementInput.moveStrafe *= 0.2F;
				this.movementInput.moveForward *= 0.2F;
				this.sprintToggleTimer = 0;
			}

			if (this.movementInput.sneak && this.ySize < 0.2F) {
				this.ySize = 0.2F;
			}

			this.pushOutOfBlocks(this.posX - (double)this.width * 0.35D, this.boundingBox.minY + 0.5D, this.posZ + (double)this.width * 0.35D);
			this.pushOutOfBlocks(this.posX - (double)this.width * 0.35D, this.boundingBox.minY + 0.5D, this.posZ - (double)this.width * 0.35D);
			this.pushOutOfBlocks(this.posX + (double)this.width * 0.35D, this.boundingBox.minY + 0.5D, this.posZ - (double)this.width * 0.35D);
			this.pushOutOfBlocks(this.posX + (double)this.width * 0.35D, this.boundingBox.minY + 0.5D, this.posZ + (double)this.width * 0.35D);
			boolean var4 = (float)this.getFoodStats().getFoodLevel() > 6.0F || this.capabilities.allowFlying;
			if (this.onGround && !var3 && this.movementInput.moveForward >= var2 && !this.isSprinting() && var4 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness)) {
				if (this.sprintToggleTimer == 0) {
					this.sprintToggleTimer = 7;
				} else {
					this.setSprinting(true);
					this.sprintToggleTimer = 0;
				}
			}

			if (this.isSneaking()) {
				this.sprintToggleTimer = 0;
			}

			if (this.isSprinting() && (this.movementInput.moveForward < var2 || this.isCollidedHorizontally || !var4)) {
				this.setSprinting(false);
			}

			if (this.capabilities.allowFlying && !wasFlightUp && this.movementInput.flyingUp) { //Spout
				if (this.flyToggleTimer == 0) {
					this.flyToggleTimer = 7;
				} else {
					this.capabilities.isFlying = !this.capabilities.isFlying;
					this.sendPlayerAbilities();
					this.flyToggleTimer = 0;
				}
			}

			if (this.capabilities.isFlying) {
				//Spout start
				if (this.movementInput.flyingDown) { 
					this.motionY -= 0.15D * ConfigReader.flightSpeedMultiplier;
				}

				if (this.movementInput.flyingUp) {
					this.motionY += 0.15D * ConfigReader.flightSpeedMultiplier;
				}
				//Spout end
			}

			super.onLivingUpdate();
			if (this.onGround && this.capabilities.isFlying) {
				this.capabilities.isFlying = false;
				this.sendPlayerAbilities();
			}
		}
	}

	public float getFOVMultiplier() {
		float var1 = 1.0F;
		if (this.capabilities.isFlying) {
			var1 *= 1.1F;
		}

		//Spout start
		float landMoveFactor = this.speedOnGround * ((isSprinting() || runToggle) ? 1.3F : 1F);
		var1 *= (landMoveFactor * this.getSpeedModifier() / this.speedOnGround + 1.0F) / 2.0F;
		//Spout end
		if (this.isUsingItem() && this.getItemInUse().itemID == Item.bow.shiftedIndex) {
			int var2 = this.getItemInUseDuration();
			float var3 = (float)var2 / 20.0F;
			if (var3 > 1.0F) {
				var3 = 1.0F;
			} else {
				var3 *= var3;
			}

			var1 *= 1.0F - var3 * 0.15F;
		}

		return var1;
	}

	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeEntityToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("Score", this.score);
	}

	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readEntityFromNBT(par1NBTTagCompound);
		this.score = par1NBTTagCompound.getInteger("Score");
	}

	public void closeScreen() {
		super.closeScreen();
		this.mc.displayGuiScreen((GuiScreen)null);
	}

	public void displayGUIBook(ItemStack par1ItemStack) {
		Item var2 = par1ItemStack.getItem();

		if (var2 == Item.writtenBook) {
			this.mc.displayGuiScreen(new GuiScreenBook(this, par1ItemStack, false));
		} else if (var2 == Item.writableBook) {
			this.mc.displayGuiScreen(new GuiScreenBook(this, par1ItemStack, true));
		}
	}

	public void displayGUIEditSign(TileEntitySign par1TileEntitySign) {
		this.mc.displayGuiScreen(new GuiEditSign(par1TileEntitySign));
	}

	public void displayGUIChest(IInventory par1IInventory) {
		this.mc.displayGuiScreen(new GuiChest(this.inventory, par1IInventory));
	}

	public void displayGUIWorkbench(int par1, int par2, int par3) {
		this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.worldObj, par1, par2, par3));
	}

	public void displayGUIEnchantment(int par1, int par2, int par3) {
		this.mc.displayGuiScreen(new GuiEnchantment(this.inventory, this.worldObj, par1, par2, par3));
	}

	public void displayGUIFurnace(TileEntityFurnace par1TileEntityFurnace) {
		this.mc.displayGuiScreen(new GuiFurnace(this.inventory, par1TileEntityFurnace));
	}

	public void displayGUIBrewingStand(TileEntityBrewingStand par1TileEntityBrewingStand) {
		this.mc.displayGuiScreen(new GuiBrewingStand(this.inventory, par1TileEntityBrewingStand));
	}

	public void displayGUIDispenser(TileEntityDispenser par1TileEntityDispenser) {
		this.mc.displayGuiScreen(new GuiDispenser(this.inventory, par1TileEntityDispenser));
	}

	public void displayGUIMerchant(IMerchant par1IMerchant) {
		this.mc.displayGuiScreen(new GuiMerchant(this.inventory, par1IMerchant, this.worldObj));
	}

	public void onCriticalHit(Entity par1Entity) {
		this.mc.effectRenderer.addEffect(new EntityCrit2FX(this.mc.theWorld, par1Entity));
	}

	public void onEnchantmentCritical(Entity par1Entity) {
		EntityCrit2FX var2 = new EntityCrit2FX(this.mc.theWorld, par1Entity, "magicCrit");
		this.mc.effectRenderer.addEffect(var2);
	}

	public void onItemPickup(Entity par1Entity, int par2) {
		this.mc.effectRenderer.addEffect(new EntityPickupFX(this.mc.theWorld, par1Entity, this, -0.5F));
	}

	public void sendChatMessage(String par1Str) {}

	public boolean isSneaking() {
		return this.movementInput.sneak && !this.sleeping;
	}

	public void setHealth(int par1) {
		int var2 = this.getHealth() - par1;
		if (var2 <= 0) {
			this.setEntityHealth(par1);
			if (var2 < 0) {
				this.hurtResistantTime = this.maxHurtResistantTime / 2;
			}
		} else {
			this.lastDamage = var2;
			this.setEntityHealth(this.getHealth());
			this.hurtResistantTime = this.maxHurtResistantTime;
			this.damageEntity(DamageSource.generic, var2);
			this.hurtResistantTime = this.maxHurtResistantTime = 10;
		}
	}

	public void addChatMessage(String par1Str) {
		this.mc.ingameGUI.addChatMessageTranslate(par1Str); // Spout - keep old chat GUI
	}

	public void addStat(StatBase par1StatBase, int par2) {
		if (par1StatBase != null) {
			if (par1StatBase.isAchievement()) {
				Achievement var3 = (Achievement)par1StatBase;
				if (var3.parentAchievement == null || this.mc.statFileWriter.hasAchievementUnlocked(var3.parentAchievement)) {
					if (!this.mc.statFileWriter.hasAchievementUnlocked(var3)) {
						this.mc.guiAchievement.queueTakenAchievement(var3);
					}

					this.mc.statFileWriter.readStat(par1StatBase, par2);
				}
			} else {
				this.mc.statFileWriter.readStat(par1StatBase, par2);
			}
		}
	}

	private boolean isBlockTranslucent(int par1, int par2, int par3) {
		return this.worldObj.isBlockNormalCube(par1, par2, par3);
	}

	protected boolean pushOutOfBlocks(double par1, double par3, double par5) {
		int var7 = MathHelper.floor_double(par1);
		int var8 = MathHelper.floor_double(par3);
		int var9 = MathHelper.floor_double(par5);
		double var10 = par1 - (double)var7;
		double var12 = par5 - (double)var9;
		if (this.isBlockTranslucent(var7, var8, var9) || this.isBlockTranslucent(var7, var8 + 1, var9)) {
			boolean var14 = !this.isBlockTranslucent(var7 - 1, var8, var9) && !this.isBlockTranslucent(var7 - 1, var8 + 1, var9);
			boolean var15 = !this.isBlockTranslucent(var7 + 1, var8, var9) && !this.isBlockTranslucent(var7 + 1, var8 + 1, var9);
			boolean var16 = !this.isBlockTranslucent(var7, var8, var9 - 1) && !this.isBlockTranslucent(var7, var8 + 1, var9 - 1);
			boolean var17 = !this.isBlockTranslucent(var7, var8, var9 + 1) && !this.isBlockTranslucent(var7, var8 + 1, var9 + 1);
			byte var18 = -1;
			double var19 = 9999.0D;
			if (var14 && var10 < var19) {
				var19 = var10;
				var18 = 0;
			}

			if (var15 && 1.0D - var10 < var19) {
				var19 = 1.0D - var10;
				var18 = 1;
			}

			if (var16 && var12 < var19) {
				var19 = var12;
				var18 = 4;
			}

			if (var17 && 1.0D - var12 < var19) {
				var19 = 1.0D - var12;
				var18 = 5;
			}

			float var21 = 0.1F;
			if (var18 == 0) {
				this.motionX = (double)(-var21);
			}

			if (var18 == 1) {
				this.motionX = (double)var21;
			}

			if (var18 == 4) {
				this.motionZ = (double)(-var21);
			}

			if (var18 == 5) {
				this.motionZ = (double)var21;
			}
		}

		return false;
	}
	
	public boolean isSprinting() {
		return super.isSprinting();
	}

	public void setSprinting(boolean par1) {
		super.setSprinting(par1);
		this.sprintingTicksLeft = par1 ? 600 : 0;
	}

	public void setXPStats(float par1, int par2, int par3) {
		this.experience = par1;
		this.experienceTotal = par2;
		this.experienceLevel = par3;
	}

	public void sendChatToPlayer(String par1Str) {
		//this.mc.ingameGUI.getChatGUI().printChatMessage(par1Str); // Spout removed
	}

	public boolean canCommandSenderUseCommand(String par1Str) {
		return this.worldObj.getWorldInfo().areCommandsAllowed();
	}
	
	//Spout
	public boolean canSprint() {
		return this.getFoodStats().getFoodLevel() > 6.0F;
	}
	
	@Override
	public void handleKeyPress(int key, boolean keyReleased) {
		if (keyReleased) {
			final GameSettings settings = SpoutClient.getHandle().gameSettings;
			
			//Fog toggle
			if (key == settings.keyBindToggleFog.keyCode) {
				byte view = (byte)settings.renderDistance;
				byte newView = (byte) SpoutClient.getInstance().getActivePlayer().getNextRenderDistance().getValue();
				fogKey = settings.keyBindToggleFog;
				settings.keyBindToggleFog = new KeyBinding("key.fog", -1);
				if (view != newView) {
					settings.renderDistance = newView;
					if (this instanceof EntityClientPlayerMP && Spoutcraft.getClient().isSpoutEnabled()) {
						SpoutClient.getInstance().getPacketManager().sendSpoutPacket(new PacketRenderDistance((byte)newView));
					}
				}
			}
			
			//Sneak toggle
			else if (key == settings.keySneakToggle.keyCode) {
				sneakToggle = !sneakToggle;
				if (sneakToggle) {
					runToggle = false;
					setSprinting(false);
					treadWaterToggle = false;
				}
			}
			
			//Run toggle
			else if (key == settings.keyRunToggle.keyCode) {
				runToggle = !runToggle;
				setSprinting(runToggle);
				if (runToggle) {
					sneakToggle = false;
					treadWaterToggle = false;
				}
			}
			
			//Water tread
			else if (key == settings.keyTreadWaterToggle.keyCode) {
				treadWaterToggle = !treadWaterToggle;
				if (treadWaterToggle) {
					runToggle = false;
					setSprinting(false);
					sneakToggle = false;
				}
			}

			//Auto forward
			else if (key == settings.keyAutoForward.keyCode) {
				autoforwardToggle = !autoforwardToggle;
				autoBackwardToggle = false;
			}
			else if (key == settings.keyBindForward.keyCode && autoforwardToggle) {
				autoforwardToggle = false;
				autoBackwardToggle = false;
			}
			
			//Auto backward
			else if (key == settings.keyAutoBackward.keyCode) {
				autoBackwardToggle = !autoBackwardToggle;
				autoforwardToggle = false;
			}
			else if (key == settings.keyBindBack.keyCode && autoBackwardToggle) {
				autoBackwardToggle = false;
				autoforwardToggle = false;
			}
			
			//Overview map
			else if (key == settings.keyWaypoint.keyCode){
				if(Spoutcraft.hasPermission("spout.client.overviewmap")) {
					mc.displayGuiScreen(new GuiOverviewMap());
				}
			}
			
			else if (key == settings.keyHideChat.keyCode){
				if (Spoutcraft.getActivePlayer() != null) {
					Spoutcraft.getActivePlayer().getMainScreen().getChatTextBox().setVisible(!Spoutcraft.getActivePlayer().getMainScreen().getChatTextBox().isVisible());
				}
			}
			
			if (this.capabilities.allowFlying && key == settings.keyFlyToggle.keyCode) {
				this.capabilities.isFlying = !this.capabilities.isFlying;
				if (this.capabilities.isFlying) {
					posY += 0.1D;
					this.motionY += 0.15D;
				}
			}
		}
	}
	
	@Override
	public void onUpdate() {
		if (fogKey != null) {
			SpoutClient.getHandle().gameSettings.keyBindToggleFog = fogKey;
			fogKey = null;
		}
		super.onUpdate();
	}
	//Spout end
}

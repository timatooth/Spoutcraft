package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderVillager extends RenderLiving {
	protected ModelVillager field_40295_c;

	public RenderVillager() {
		super(new ModelVillager(0.0F), 0.5F);
		this.field_40295_c = (ModelVillager)this.mainModel;
	}

	protected int func_77053_a(EntityVillager par1EntityVillager, int par2, float par3) {
		return -1;
	}

	public void renderVillager(EntityVillager par1EntityVillager, double par2, double par4, double par6, float par8, float par9) {
		super.doRenderLiving(par1EntityVillager, par2, par4, par6, par8, par9);
	}

	protected void func_77055_a(EntityVillager par1EntityVillager, double par2, double par4, double par6) {}

	protected void func_77051_a(EntityVillager par1EntityVillager, float par2) {
		super.renderEquippedItems(par1EntityVillager, par2);
	}

	protected void func_77052_b(EntityVillager par1EntityVillager, float par2) {
		float var3 = 0.9375F;
		if (par1EntityVillager.getGrowingAge() < 0) {
			var3 = (float)((double)var3 * 0.5D);
			this.shadowSize = 0.25F;
		} else {
			this.shadowSize = 0.5F;
		}

		GL11.glScalef(var3, var3, var3);
	}

	protected void passSpecialRender(EntityLiving par1EntityLiving, double par2, double par4, double par6) {
		this.func_77055_a((EntityVillager)par1EntityLiving, par2, par4, par6);
	}

	protected void preRenderCallback(EntityLiving par1EntityLiving, float par2) {
		this.func_77052_b((EntityVillager)par1EntityLiving, par2);
	}

	protected int shouldRenderPass(EntityLiving par1EntityLiving, int par2, float par3) {
		return this.func_77053_a((EntityVillager)par1EntityLiving, par2, par3);
	}

	protected void renderEquippedItems(EntityLiving par1EntityLiving, float par2) {
		this.func_77051_a((EntityVillager)par1EntityLiving, par2);
	}

	public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
		this.renderVillager((EntityVillager)par1EntityLiving, par2, par4, par6, par8, par9);
	}

	public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		this.renderVillager((EntityVillager)par1Entity, par2, par4, par6, par8, par9);
	}
}

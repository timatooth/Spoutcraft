package net.minecraft.src;

import org.spoutcraft.client.entity.CraftSnowball;

public class EntitySnowball extends EntityThrowable {
	public EntitySnowball(World par1World) {
		super(par1World);
		//Spout start
		this.spoutEntity = new CraftSnowball(this);
		//Spout end
	}

	public EntitySnowball(World par1World, EntityLiving par2EntityLiving) {
		super(par1World, par2EntityLiving);
		//Spout start
		this.spoutEntity = new CraftSnowball(this);
		//Spout end
	}

	public EntitySnowball(World par1World, double par2, double par4, double par6) {
		super(par1World, par2, par4, par6);
		//Spout start
		this.spoutEntity = new CraftSnowball(this);
		//Spout end
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	protected void onImpact(MovingObjectPosition par1MovingObjectPosition) {
		if (par1MovingObjectPosition.entityHit != null) {
			byte var2 = 0;

			if (par1MovingObjectPosition.entityHit instanceof EntityBlaze) {
				var2 = 3;
			}

			par1MovingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.thrower), var2);
		}

		for (int var3 = 0; var3 < 8; ++var3) {
			this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
		}

		if (!this.worldObj.isRemote) {
			this.setDead();
		}
	}
}

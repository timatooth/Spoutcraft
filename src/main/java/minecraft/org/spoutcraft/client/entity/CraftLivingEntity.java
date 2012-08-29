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
package org.spoutcraft.client.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;

import org.spoutcraft.spoutcraftapi.block.Block;
import org.spoutcraft.spoutcraftapi.entity.Entity;
import org.spoutcraft.spoutcraftapi.entity.EntitySkinType;
import org.spoutcraft.spoutcraftapi.entity.LivingEntity;
import org.spoutcraft.spoutcraftapi.util.BlockIterator;
import org.spoutcraft.spoutcraftapi.util.FixedLocation;
import org.spoutcraft.spoutcraftapi.util.MutableLocation;

public class CraftLivingEntity extends CraftEntity implements LivingEntity {
	public CraftLivingEntity(EntityLiving living) {
		super(living);
	}

	public EntityLiving getEntityLiving() {
		return (EntityLiving) handle;
	}

	public int getHealth() {
		return getEntityLiving().health;
	}

	public void setHealth(int health) {
		getEntityLiving().health = health;
	}

	public double getEyeHeight() {
		return getEntityLiving().getEyeHeight();
	}

	public double getEyeHeight(boolean ignoreSneaking) {
		if (ignoreSneaking) {
			return getEntityLiving().height;
		}
		return getEyeHeight();
	}

	public FixedLocation getEyeLocation() {
		return new MutableLocation(getWorld(), handle.posX, handle.posY + getEyeHeight(), handle.posZ);
	}

	private List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance, int maxLength) {
		if (maxDistance > 120) {
			maxDistance = 120;
		}
		ArrayList<Block> blocks = new ArrayList<Block>();
		Iterator<Block> itr = new BlockIterator(this, maxDistance);
		while (itr.hasNext()) {
			Block block = itr.next();
			blocks.add(block);
			if (maxLength != 0 && blocks.size() > maxLength) {
				blocks.remove(0);
			}
			int id = block.getTypeId();
			if (transparent == null) {
				if (id != 0) {
					break;
				}
			} else {
				if (!transparent.contains((byte) id)) {
					break;
				}
			}
		}
		return blocks;
	}

	public List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance) {
		return getLineOfSight(transparent, maxDistance, 0);
	}

	public Block getTargetBlock(HashSet<Byte> transparent, int maxDistance) {
		List<Block> blocks = getLineOfSight(transparent, maxDistance, 1);
		return blocks.get(0);
	}

	public List<Block> getLastTwoTargetBlocks(HashSet<Byte> transparent, int maxDistance) {
		return getLineOfSight(transparent, maxDistance, 2);
	}

	public boolean isInsideVehicle() {
		return handle.ridingEntity != null;
	}

	public boolean leaveVehicle() {
		if (isInsideVehicle()) {
			handle.mountEntity(null);
			return true;
		}
		return false;
	}

	public int getRemainingAir() {
		return getEntityLiving().getAir();
	}

	public void setRemainingAir(int ticks) {
		if (ticks < 0) {
			throw new IllegalArgumentException("The Remaining Air can not be below 0");
		}
		getEntityLiving().setAir(ticks);
	}

	public int getMaximumAir() {
		return getEntityLiving().maxAir;
	}

	public void setMaximumAir(int ticks) {
		if (ticks <= 0) {
			throw new IllegalArgumentException("The Maximum Air can not be below 1");
		}
		getEntityLiving().maxAir = ticks;
	}

	public void damage(int amount) {
		getEntityLiving().damageEntity(net.minecraft.src.DamageSource.generic, amount);
	}

	public void damage(int amount, Entity source) {
		net.minecraft.src.DamageSource reason = net.minecraft.src.DamageSource.generic;

		if (source instanceof org.spoutcraft.spoutcraftapi.entity.HumanEntity) {
			reason = net.minecraft.src.DamageSource.causePlayerDamage(((CraftHumanEntity)source).getMCPlayer());
		} else if (source instanceof LivingEntity) {
			reason = net.minecraft.src.DamageSource.causeMobDamage(((CraftLivingEntity)source).getEntityLiving());
		}

		getEntityLiving().damageEntity(reason, amount);
	}

	public int getMaximumNoDamageTicks() {
		return getEntityLiving().maxHurtResistantTime;
	}

	public void setMaximumNoDamageTicks(int ticks) {
		getEntityLiving().maxHurtResistantTime = ticks;
	}

	public int getLastDamage() {
		return getEntityLiving().lastDamage;
	}

	public void setLastDamage(int damage) {
		getEntityLiving().lastDamage = damage;
	}

	public int getNoDamageTicks() {
		return getEntityLiving().hurtResistantTime;
	}

	public void setNoDamageTicks(int ticks) {
		getEntityLiving().hurtResistantTime = ticks;
	}

	public String getTitle() {
		return getEntityLiving().displayName;
	}

	public void setTitle(String title) {
		getEntityLiving().displayName = title;
	}

	public void resetTitle() {
		getEntityLiving().displayName = null;
		if (handle instanceof EntityPlayer) {
			getEntityLiving().displayName = ((EntityPlayer)handle).username;
		}
	}

	@Override
	public void setSkin(String skinURI, EntitySkinType type) {
		getEntityLiving().setCustomTexture(skinURI, type.getId());
	}
}

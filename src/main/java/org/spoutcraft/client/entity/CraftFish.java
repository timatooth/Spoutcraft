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

import net.minecraft.src.EntityFishHook;
import net.minecraft.src.EntityPlayer;

import org.spoutcraft.spoutcraftapi.entity.Fish;
import org.spoutcraft.spoutcraftapi.entity.LivingEntity;

public class CraftFish extends AbstractProjectile implements Fish {
	public CraftFish(EntityFishHook entity) {
		super(entity);
	}

	@Override
	public String toString() {
		return "CraftFish";
	}

	public LivingEntity getShooter() {
		if (((EntityFishHook) handle).angler != null) {
			return (LivingEntity) ((EntityFishHook) handle).angler.spoutEntity;
		}
		return null;
	}

	public void setShooter(LivingEntity shooter) {
		if (shooter instanceof CraftHumanEntity) {
			((EntityFishHook) handle).angler = (EntityPlayer) ((CraftHumanEntity) shooter).handle;
		}
	}
}

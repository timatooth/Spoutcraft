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

import net.minecraft.src.EntitySlime;

import org.spoutcraft.spoutcraftapi.entity.Slime;

public class CraftSlime extends CraftLivingEntity implements Slime {
	public CraftSlime(EntitySlime entity) {
		super(entity);
	}

	@Override
	public String toString() {
		return "CraftSlime";
	}

	public EntitySlime getHandle() {
		return (EntitySlime)handle;
	}

	public int getSize() {
		return getHandle().getSlimeSize();
	}

	public void setSize(int size) {
		getHandle().setSlimeSize(size);
	}
}

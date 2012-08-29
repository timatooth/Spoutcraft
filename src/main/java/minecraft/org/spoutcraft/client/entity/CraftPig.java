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

import net.minecraft.src.EntityPig;

import org.spoutcraft.spoutcraftapi.entity.Pig;

public class CraftPig extends CraftAnimals implements Pig {
	public CraftPig(EntityPig entity) {
		super(entity);
	}

	public boolean hasSaddle() {
		return getHandle().getSaddled();
	}

	public void setSaddle(boolean saddled) {
		getHandle().setSaddled(saddled);
	}

	public EntityPig getHandle() {
		return (EntityPig) handle;
	}

	@Override
	public String toString() {
		return "CraftPig";
	}
}

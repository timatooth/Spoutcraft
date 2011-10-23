/*
 * This file is part of Spoutcraft (http://wiki.getspout.org/).
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
package org.getspout.spout.item;

import org.spoutcraft.spoutcraftapi.Spoutcraft;
import org.spoutcraft.spoutcraftapi.block.design.BlockDesign;
import org.spoutcraft.spoutcraftapi.material.block.GenericCustomBlock;

public class ServerCustomBlock extends GenericCustomBlock{

	public ServerCustomBlock(String name, boolean isOpaque, BlockDesign design, int customMetaData) {
		super(Spoutcraft.getAddonManager().getAddon(design.getTextureAddon()), name, isOpaque, design, customMetaData);
	}
	
	@Override
	public int getCustomId() {
		return this.getCustomMetaData();
	}

}

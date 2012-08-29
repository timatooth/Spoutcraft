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
package org.spoutcraft.client.packet;

import java.io.IOException;

import net.minecraft.src.*;

import org.spoutcraft.client.SpoutClient;
import org.spoutcraft.spoutcraftapi.io.SpoutInputStream;
import org.spoutcraft.spoutcraftapi.io.SpoutOutputStream;

public class PacketSkinURL implements SpoutPacket {
	public int entityId;
	public String skinURL;
	public String cloakURL;
	public boolean release = true;
	public PacketSkinURL() {
	}

	public PacketSkinURL(int id, String skinURL, String cloakURL) {
		this.entityId = id;
		this.skinURL = skinURL;
		this.cloakURL = cloakURL;
		release = false;
	}

	public PacketSkinURL(int id, String skinURL) {
		this.entityId = id;
		this.skinURL = skinURL;
		this.cloakURL = "none";
	}

	public PacketSkinURL(String cloakURL, int id) {
		this.entityId = id;
		this.skinURL = "none";
		this.cloakURL = cloakURL;
	}

	public void readData(SpoutInputStream input) throws IOException {
		entityId = input.readInt();
		skinURL = input.readString();
		cloakURL = input.readString();
		release = input.readBoolean();
	}

	public void writeData(SpoutOutputStream output) throws IOException {
		output.writeInt(entityId);
		output.writeString(skinURL);
		output.writeString(cloakURL);
		output.writeBoolean(release);
	}

	public void run(int PlayerId) {
		EntityPlayer e = SpoutClient.getInstance().getPlayerFromId(entityId);
		if (e != null) {
			//Check if these are the mc skin/cape, if so, use defaults instead
			String mcSkin = "http://s3.amazonaws.com/MinecraftSkins/" + e.username + ".png";
			String mcCape = "http://s3.amazonaws.com/MinecraftCloaks/" + e.username + ".png";
			if (this.skinURL.equalsIgnoreCase(mcSkin)) {
				this.skinURL = "http://static.spout.org/skin/" + e.username + ".png";
			}
			if (this.cloakURL.equalsIgnoreCase(mcCape)) {
				if (e.vip != null) {
					this.cloakURL = e.vip.getCape();
				} else {
					this.cloakURL = "http://static.spout.org/skin/" + e.username + ".png";
				}
			}

			if (!this.skinURL.equals("none")) {
				e.skinUrl = this.skinURL;
			}
			if (!this.cloakURL.equals("none")) {
				e.updateCloak(cloakURL);
			}
			if (release) {
				e.worldObj.releaseEntitySkin(e);
			}
			e.worldObj.obtainEntitySkin(e);
		}
	}

	public PacketType getPacketType() {
		return PacketType.PacketSkinURL;
	}

	public int getVersion() {
		return 1;
	}

	public void failure(int playerId) {
	}
}

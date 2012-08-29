package net.minecraft.src;

import java.net.ConnectException;
import java.net.UnknownHostException;

import net.minecraft.client.Minecraft;

class ThreadConnectToServer extends Thread {

	/** The IP address or domain used to connect. */
	final String ip;

	/** The port used to connect. */
	final int port;

	/** A reference to the GuiConnecting object. */
	final GuiConnecting connectingGui;

	ThreadConnectToServer(GuiConnecting par1GuiConnecting, String par2Str, int par3) {
		this.connectingGui = par1GuiConnecting;
		this.ip = par2Str;
		this.port = par3;
	}

	public void run() {
		try {
			GuiConnecting.setNetClientHandler(this.connectingGui, new NetClientHandler(GuiConnecting.func_74254_c(this.connectingGui), this.ip, this.port));

			if (GuiConnecting.isCancelled(this.connectingGui)) {
				return;
			}

			// Spout - start
			GuiConnecting.getNetClientHandler(this.connectingGui).addToSendQueue(new Packet250CustomPayload("REGISTER", "AutoProto:HShake".getBytes()));
			GuiConnecting.getNetClientHandler(this.connectingGui).addToSendQueue(new Packet250CustomPayload("REGISTER", "ChkCache:setHash".getBytes()));
			GuiConnecting.getNetClientHandler(this.connectingGui).addToSendQueue(new Packet250CustomPayload("AutoProto:HShake", "VanillaProtocol".getBytes()));
			// Spout - end
			GuiConnecting.getNetClientHandler(this.connectingGui).addToSendQueue(new Packet2ClientProtocol(39, GuiConnecting.func_74254_c(this.connectingGui).session.username, this.ip, this.port));
		} catch (UnknownHostException var2) {
			if (GuiConnecting.isCancelled(this.connectingGui)) {
				return;
			}

			displayConnectionIssue(ip, port, "Unknown host \'" + this.ip + "\'"); // Spout
		} catch (ConnectException var3) {
			if (GuiConnecting.isCancelled(this.connectingGui)) {
				return;
			}

			//Spout start
			if (var3.getMessage().toLowerCase().contains("connection refused")) {
				displayConnectionIssue(ip, port, "The server is not currently online!");
			} else {
				displayConnectionIssue(ip, port, var3.getMessage());
			}
			//Spout end
		} catch (Exception var4) {
			if (GuiConnecting.isCancelled(this.connectingGui)) {
				return;
			}

			var4.printStackTrace();
			//Spout Start
			displayConnectionIssue(ip, port, var4.toString());
			//Spout End
		}
	}
	
	//Spout Start
	private void displayConnectionIssue(String ip, int port, String message) {
		org.spoutcraft.client.gui.error.GuiConnectionLost.lastServerIp = ip;
		org.spoutcraft.client.gui.error.GuiConnectionLost.lastServerPort = port;
		Minecraft.getMinecraft().displayGuiScreen(new org.spoutcraft.client.gui.error.GuiConnectionLost(message));
	}
	//Spout End
}

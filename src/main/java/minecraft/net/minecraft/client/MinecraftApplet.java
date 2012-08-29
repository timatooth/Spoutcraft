package net.minecraft.client;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import net.minecraft.client.Minecraft;
import net.minecraft.src.CanvasMinecraftApplet;
import net.minecraft.src.MinecraftAppletImpl;
import net.minecraft.src.Session;

public class MinecraftApplet extends Applet {

	private Canvas mcCanvas;
	private Minecraft mc;
	private Thread mcThread = null;

	public void init() {
		this.mcCanvas = new CanvasMinecraftApplet(this);
		boolean var1 = "true".equalsIgnoreCase(this.getParameter("fullscreen"));

		this.mc = new MinecraftAppletImpl(this, this.mcCanvas, this, this.getWidth(), this.getHeight(), var1);
		this.mc.minecraftUri = this.getDocumentBase().getHost();
		if (this.getDocumentBase().getPort() > 0) {
			this.mc.minecraftUri = this.mc.minecraftUri + ":" + this.getDocumentBase().getPort();
		}

		if (this.getParameter("username") != null && this.getParameter("sessionid") != null) {
			this.mc.session = new Session(this.getParameter("username"), this.getParameter("sessionid"));
			System.out.println("Setting user: " + this.mc.session.username + ", " + this.mc.session.sessionId);
		} else {
			this.mc.session = new Session("Player", "");
		}
		//Spout Start
		if(this.getParameter("spoutcraftlauncher") != null) {
			Minecraft.spoutcraftLauncher = this.getParameter("spoutcraftlauncher").equalsIgnoreCase("true");
		}
		if(this.getParameter("portable") != null) {
			Minecraft.portable = this.getParameter("portable").equalsIgnoreCase("true");
		}
		//Spout End

		if (this.getParameter("server") != null && this.getParameter("port") != null) {
			this.mc.setServer(this.getParameter("server"), Integer.parseInt(this.getParameter("port")));
		}

		this.mc.setDemo("true".equals(this.getParameter("demo")));
		this.mc.hideQuitButton = !"true".equals(this.getParameter("stand-alone"));

		this.setLayout(new BorderLayout());
		this.add(this.mcCanvas, "Center");
		this.mcCanvas.setFocusable(true);
		this.validate();
	}

	public void startMainThread() {
		if (this.mcThread == null) {
			this.mcThread = new Thread(this.mc, "Minecraft main thread");
			this.mcThread.start();
		}
	}

	public void start() {
		if (this.mc != null) {
			this.mc.isGamePaused = false;
		}

	}

	public void stop() {
		if (this.mc != null) {
			this.mc.isGamePaused = true;
		}

	}

	public void destroy() {
		this.shutdown();
	}

	public void shutdown() {
		if (this.mcThread != null) {
			this.mc.shutdown();

			try {
				this.mcThread.join(10000L);
			} catch (InterruptedException var4) {
				try {
					this.mc.shutdownMinecraftApplet();
				} catch (Exception var3) {
					var3.printStackTrace();
				}
			}

			this.mcThread = null;
		}
	}

	public void clearApplet() {
		this.mcCanvas = null;
		this.mc = null;
		this.mcThread = null;

		try {
			this.removeAll();
			this.validate();
		} catch (Exception var2) {
			;
		}

	}
}

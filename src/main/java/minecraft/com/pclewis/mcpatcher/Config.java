package com.pclewis.mcpatcher;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

class Config {
	private File xmlFile = null;
	Document xml;
	Element selectedProfile;
	static final String TAG_ROOT = "mcpatcherProfile";
	static final String TAG_CONFIG1 = "config";
	static final String TAG_SELECTED_PROFILE = "selectedProfile";
	static final String TAG_LAST_MOD_DIRECTORY = "lastModDirectory";
	static final String TAG_DEBUG = "debug";
	static final String TAG_JAVA_HEAP_SIZE = "javaHeapSize";
	static final String TAG_LAST_VERSION = "lastVersion";
	static final String TAG_BETA_WARNING_SHOWN = "betaWarningShown";
	static final String TAG_MODS = "mods";
	static final String ATTR_PROFILE = "profile";
	static final String TAG_MOD = "mod";
	static final String TAG_NAME = "name";
	static final String TAG_TYPE = "type";
	static final String TAG_PATH = "path";
	static final String TAG_FILES = "files";
	static final String TAG_FILE = "file";
	static final String TAG_FROM = "from";
	static final String TAG_TO = "to";
	static final String TAG_CLASS = "class";
	static final String TAG_ENABLED = "enabled";
	static final String ATTR_VERSION = "version";
	static final String VAL_BUILTIN = "builtIn";
	static final String VAL_EXTERNAL_ZIP = "externalZip";
	static final String VAL_EXTERNAL_JAR = "externalJar";
	private static final int XML_INDENT_AMOUNT = 2;
	private static final String XSLT_REFORMAT = "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"><xsl:output method=\"xml\" omit-xml-declaration=\"no\"/><xsl:strip-space elements=\"*\"/><xsl:template match=\"@*|node()\"><xsl:copy><xsl:apply-templates select=\"@*|node()\"/></xsl:copy></xsl:template></xsl:stylesheet>";

	Config(File var1) throws ParserConfigurationException {
		this.xmlFile = new File(var1, "mcpatcher.xml");
		DocumentBuilderFactory var2 = DocumentBuilderFactory.newInstance();
		DocumentBuilder var3 = var2.newDocumentBuilder();
		boolean var4 = false;

		if (this.xmlFile.exists()) {
			try {
				this.xml = var3.parse(this.xmlFile);
			} catch (Exception var6) {
				var6.printStackTrace();
			}
		}

		if (this.xml == null) {
			this.xml = var3.newDocument();
			this.buildNewProperties();
			var4 = true;
		}

		if (var4) {
			this.saveProperties();
		}
	}

	Element getElement(Element var1, String var2) {
		if (var1 == null) {
			return null;
		} else {
			NodeList var3 = var1.getElementsByTagName(var2);
			Element var4;

			if (var3.getLength() == 0) {
				var4 = this.xml.createElement(var2);
				var1.appendChild(var4);
			} else {
				var4 = (Element)var3.item(0);
			}

			return var4;
		}
	}

	String getText(Node var1) {
		if (var1 == null) {
			return null;
		} else {
			switch (var1.getNodeType()) {
				case 1:
					NodeList var2 = var1.getChildNodes();

					for (int var3 = 0; var3 < var2.getLength(); ++var3) {
						Node var4 = var2.item(var3);

						if (var4.getNodeType() == 3) {
							return ((Text)var4).getData();
						}
					}

				case 2:
					return ((Attr)var1).getValue();

				case 3:
					return ((Text)var1).getData();

				default:
					return null;
			}
		}
	}

	void setText(Element var1, String var2, String var3) {
		if (var1 != null) {
			Element var4 = this.getElement(var1, var2);

			while (var4.hasChildNodes()) {
				var4.removeChild(var4.getFirstChild());
			}

			Text var5 = this.xml.createTextNode(var3);
			var4.appendChild(var5);
		}
	}

	void remove(Node var1) {
		if (var1 != null) {
			Node var2 = var1.getParentNode();
			var2.removeChild(var1);
		}
	}

	String getText(Element var1, String var2) {
		return this.getText(this.getElement(var1, var2));
	}

	Element getRoot() {
		if (this.xml == null) {
			return null;
		} else {
			Element var1 = this.xml.getDocumentElement();

			if (var1 == null) {
				var1 = this.xml.createElement("mcpatcherProfile");
				this.xml.appendChild(var1);
			}

			return var1;
		}
	}

	Element getConfig() {
		return this.getElement(this.getRoot(), "config");
	}

	Element getConfig(String var1) {
		return this.getElement(this.getConfig(), var1);
	}

	String getConfigValue(String var1) {
		return this.getText(this.getConfig(var1));
	}

	void setConfigValue(String var1, String var2) {
		Element var3 = this.getConfig(var1);

		if (var3 != null) {
			while (var3.hasChildNodes()) {
				var3.removeChild(var3.getFirstChild());
			}

			var3.appendChild(this.xml.createTextNode(var2));
		}
	}

	static String getDefaultProfileName(String var0) {
		return "Minecraft " + var0;
	}

	static boolean isDefaultProfile(String var0) {
		return var0.startsWith("Minecraft ");
	}

	void setDefaultProfileName(String var1) {
		Element var2 = this.getRoot();
		NodeList var3 = var2.getElementsByTagName("mods");
		String var4 = this.getConfigValue("selectedProfile");

		if (var4 == null || var4.equals("")) {
			this.setConfigValue("selectedProfile", var1);
		}

		boolean var6 = false;

		for (int var7 = 0; var7 < var3.getLength(); ++var7) {
			Node var8 = var3.item(var7);

			if (var8 instanceof Element) {
				Element var5 = (Element)var8;
				var4 = var5.getAttribute("profile");

				if (var4 == null || var4.equals("")) {
					if (var6) {
						var2.removeChild(var5);
					} else {
						var5.setAttribute("profile", var1);
						var6 = true;
					}
				}
			}
		}
	}

	Element findProfileByName(String var1, boolean var2) {
		Element var3 = null;
		Element var4 = this.getRoot();
		NodeList var5 = var4.getElementsByTagName("mods");
		int var6;
		Node var7;
		Element var8;
		String var9;

		for (var6 = 0; var6 < var5.getLength(); ++var6) {
			var7 = var5.item(var6);

			if (var7 instanceof Element) {
				var8 = (Element)var7;
				var9 = var8.getAttribute("profile");

				if (var1 == null || var1.equals(var9)) {
					return var8;
				}
			}
		}

		if (var2) {
			var3 = this.xml.createElement("mods");

			if (this.selectedProfile != null) {
				var5 = this.selectedProfile.getElementsByTagName("mod");

				for (var6 = 0; var6 < var5.getLength(); ++var6) {
					var7 = var5.item(var6);

					if (var7 instanceof Element) {
						var8 = (Element)var7;
						var9 = this.getText(var8, "type");

						if ("builtIn".equals(var9)) {
							var3.appendChild(var7.cloneNode(true));
						}
					}
				}
			}

			var3.setAttribute("profile", var1);
			var4.appendChild(var3);
		}

		return var3;
	}

	void selectProfile() {
		this.selectProfile(this.getConfigValue("selectedProfile"));
	}

	void selectProfile(String var1) {
		this.selectedProfile = this.findProfileByName(var1, true);
		this.setConfigValue("selectedProfile", var1);
	}

	void deleteProfile(String var1) {
		Element var2 = this.getRoot();
		Element var3 = this.findProfileByName(var1, false);

		if (var3 != null) {
			if (var3 == this.selectedProfile) {
				this.selectedProfile = null;
			}

			var2.removeChild(var3);
		}

		this.getMods();
	}

	void renameProfile(String var1, String var2) {
		if (!var1.equals(var2)) {
			Element var3 = this.findProfileByName(var1, false);

			if (var3 != null) {
				var3.setAttribute("profile", var2);
				String var4 = this.getConfigValue("selectedProfile");

				if (var1.equals(var4)) {
					this.setConfigValue("selectedProfile", var2);
				}
			}
		}
	}

	void rewriteModPaths(File var1, File var2) {
		NodeList var3 = this.getRoot().getElementsByTagName("mods");

		for (int var4 = 0; var4 < var3.getLength(); ++var4) {
			Element var5 = (Element)var3.item(var4);
			this.rewriteModPaths(var5, var1, var2);
		}
	}

	void rewriteModPaths(Element var1, File var2, File var3) {
		NodeList var4 = var1.getElementsByTagName("mod");

		for (int var5 = 0; var5 < var4.getLength(); ++var5) {
			Element var6 = (Element)var4.item(var5);
			String var7 = this.getText(var6, "type");

			if ("externalZip".equals(var7)) {
				String var8 = this.getText(var6, "path");

				if (var8 != null && !var8.equals("")) {
					File var9 = new File(var8);

					if (var2.equals(var9.getParentFile())) {
						this.setText(var6, "path", (new File(var3, var9.getName())).getPath());
					}
				}
			}
		}
	}

	ArrayList getProfiles() {
		ArrayList var1 = new ArrayList();
		Element var2 = this.getRoot();
		NodeList var3 = var2.getElementsByTagName("mods");

		for (int var4 = 0; var4 < var3.getLength(); ++var4) {
			Node var5 = var3.item(var4);

			if (var5 instanceof Element) {
				Element var6 = (Element)var5;
				String var7 = var6.getAttribute("profile");

				if (var7 != null && !var7.equals("")) {
					var1.add(var7);
				}
			}
		}

		Collections.sort(var1);
		return var1;
	}

	Element getMods() {
		if (this.selectedProfile == null) {
			this.selectProfile();
		}

		return this.selectedProfile;
	}

	boolean hasMod(String var1) {
		Element var2 = this.getMods();

		if (var2 != null) {
			NodeList var3 = var2.getElementsByTagName("mod");

			for (int var4 = 0; var4 < var3.getLength(); ++var4) {
				Element var5 = (Element)var3.item(var4);
				NodeList var6 = var5.getElementsByTagName("name");

				if (var6.getLength() > 0) {
					var5 = (Element)var6.item(0);

					if (var1.equals(this.getText(var5))) {
						return true;
					}
				}
			}
		}

		return false;
	}

	Element getMod(String var1) {
		Element var2 = this.getMods();

		if (var2 == null) {
			return null;
		} else {
			NodeList var3 = var2.getElementsByTagName("mod");

			for (int var4 = 0; var4 < var3.getLength(); ++var4) {
				Node var5 = var3.item(var4);

				if (var5 instanceof Element) {
					Element var6 = (Element)var5;

					if (var1.equals(this.getText(var6, "name"))) {
						return var6;
					}
				}
			}

			Element var7 = this.xml.createElement("mod");
			var2.appendChild(var7);
			Element var8 = this.xml.createElement("name");
			Text var9 = this.xml.createTextNode(var1);
			var8.appendChild(var9);
			var7.appendChild(var8);
			var8 = this.xml.createElement("enabled");
			var7.appendChild(var8);
			var8 = this.xml.createElement("type");
			var7.appendChild(var8);
			return var7;
		}
	}

	void setModEnabled(String var1, boolean var2) {
		this.setText(this.getMod(var1), "enabled", Boolean.toString(var2));
	}

	Element getModConfig(String var1) {
		return this.getElement(this.getMod(var1), "config");
	}

	Element getModConfig(String var1, String var2) {
		return this.getElement(this.getModConfig(var1), var2);
	}

	String getModConfigValue(String var1, String var2) {
		return this.getText(this.getModConfig(var1, var2));
	}

	void setModConfigValue(String var1, String var2, String var3) {
		Element var4 = this.getModConfig(var1, var2);

		if (var4 != null) {
			while (var4.hasChildNodes()) {
				var4.removeChild(var4.getFirstChild());
			}

			var4.appendChild(this.xml.createTextNode(var3));
		}
	}

	private void buildNewProperties() {
		if (this.xml != null) {
			this.getRoot();
			this.getConfig();

			if (this.selectedProfile != null) {
				this.getMods();
				this.setText(this.getMod("HD Textures"), "enabled", "true");
				this.setText(this.getMod("HD Font"), "enabled", "true");
				this.setText(this.getMod("Better Grass"), "enabled", "false");
			}
		}
	}

	boolean saveProperties() {
		boolean var1 = false;

		if (this.xml != null && this.xmlFile != null) {
			FileOutputStream var2 = null;

			try {
				TransformerFactory var3 = TransformerFactory.newInstance();
				Transformer var4;

				try {
					var3.setAttribute("indent-number", Integer.valueOf(2));
					var4 = var3.newTransformer(new StreamSource(new StringReader("<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"><xsl:output method=\"xml\" omit-xml-declaration=\"no\"/><xsl:strip-space elements=\"*\"/><xsl:template match=\"@*|node()\"><xsl:copy><xsl:apply-templates select=\"@*|node()\"/></xsl:copy></xsl:template></xsl:stylesheet>")));
					var4.setOutputProperty("indent", "yes");
					var4.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				} catch (Throwable var10) {
					var4 = var3.newTransformer();
				}

				DOMSource var5 = new DOMSource(this.xml);
				var2 = new FileOutputStream(this.xmlFile);
				var4.transform(var5, new StreamResult(new OutputStreamWriter(var2, "UTF-8")));
				var1 = true;
			} catch (Exception var11) {
				var11.printStackTrace();
			} finally {
				MCPatcherUtils.close((Closeable)var2);
			}
		}

		return var1;
	}
}

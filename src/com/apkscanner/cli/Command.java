package com.apkscanner.cli;

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.cli.CommandLine;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.apkscanner.core.scanner.ApkScanner;
import com.apkscanner.core.scanner.ApkScanner.Status;
import com.apkscanner.core.scanner.ApkScanner.StatusListener;
import com.apkscanner.core.scanner.PermissionGroupManager;
import com.apkscanner.data.apkinfo.ActivityAliasInfo;
import com.apkscanner.data.apkinfo.ActivityInfo;
import com.apkscanner.data.apkinfo.ApkInfo;
import com.apkscanner.data.apkinfo.ApkInfoHelper;
import com.apkscanner.data.apkinfo.CompatibleScreensInfo;
import com.apkscanner.data.apkinfo.PermissionGroup;
import com.apkscanner.data.apkinfo.PermissionInfo;
import com.apkscanner.data.apkinfo.ProviderInfo;
import com.apkscanner.data.apkinfo.ReceiverInfo;
import com.apkscanner.data.apkinfo.ResourceInfo;
import com.apkscanner.data.apkinfo.ServiceInfo;
import com.apkscanner.data.apkinfo.SupportsGlTextureInfo;
import com.apkscanner.data.apkinfo.SupportsScreensInfo;
import com.apkscanner.data.apkinfo.UsesConfigurationInfo;
import com.apkscanner.data.apkinfo.UsesFeatureInfo;
import com.apkscanner.data.apkinfo.UsesLibraryInfo;
import com.apkscanner.data.apkinfo.UsesPermissionInfo;
import com.apkscanner.gui.tabpanels.Resources.ResourceObject;
import com.apkscanner.gui.tabpanels.Resources.ResourceType;
import com.apkscanner.gui.util.ImageScaler;
import com.apkscanner.gui.util.JHtmlEditorPane;
import com.apkscanner.resource.Resource;
import com.apkscanner.util.FileUtil;
import com.apkscanner.util.FileUtil.FSStyle;
import com.apkscanner.util.Log;
import com.apkscanner.util.ZipFileUtil;
import com.google.common.collect.ImmutableMap;

public class Command implements StatusListener {

	private ApkScanner apkScanner;
	
	PermissionGroupManager permissionGroupManager = null; 
	private String mutiLabels;
	
	public Command(ApkScanner apkScanner, CommandLine cmd) {
		this.apkScanner = apkScanner;

		if(apkScanner != null) {
			apkScanner.setStatusListener(this);
		}

		final String apkFilePath = cmd.getArgs()[0];
		apkScanner.openApk(apkFilePath);
	}

	
	@SuppressWarnings("unchecked")
	private void printBasicInfo() {
		ApkInfo apkInfo = apkScanner.getApkInfo();


		boolean isSamsungSign = false;
		boolean isPlatformSign = false;
		String CertSummary = "";

		String allPermissionsList = "";
		String signaturePermissions = "";
		String notGrantPermmissions = "";
		String deprecatedPermissions = "";


		boolean hasSignatureLevel = false;
		boolean hasSystemLevel = false;
		boolean hasSignatureOrSystemLevel = false;

		String deviceRequirements = "";
		
		
		
		
		
		
		
		
		JSONObject apkInfoJSON = new JSONObject();

		JSONArray appLabels = new JSONArray();
		if(apkInfo.manifest.application.labels != null && apkInfo.manifest.application.labels.length > 0) {
			for(ResourceInfo r: apkInfo.manifest.application.labels) {
				JSONObject label = new JSONObject();
				label.put("name", r.name);
				if(r.configuration != null && !r.configuration.isEmpty() && !"default".equals(r.configuration)) {
					label.put("config", r.configuration);
				}
				appLabels.add(label);
			}
		}
		apkInfoJSON.put("labels", appLabels);

		if(apkInfo.manifest.packageName != null) apkInfoJSON.put("packageName", apkInfo.manifest.packageName);
		if(apkInfo.manifest.versionName != null) apkInfoJSON.put("versionName", apkInfo.manifest.versionName);
		if(apkInfo.manifest.versionCode != null) apkInfoJSON.put("versionCode", apkInfo.manifest.versionCode.toString());
		if(apkInfo.manifest.application.icons != null && apkInfo.manifest.application.icons.length > 0) {
			String iconPath = apkInfo.manifest.application.icons[apkInfo.manifest.application.icons.length - 1].name;
			apkInfoJSON.put("icon", makeBase64Image(iconPath));
		}
		if(apkInfo.manifest.usesSdk.minSdkVersion != null) apkInfoJSON.put("minSdkVersion", apkInfo.manifest.usesSdk.minSdkVersion.toString());
		if(apkInfo.manifest.usesSdk.targetSdkVersion != null) apkInfoJSON.put("targetSdkVersion", apkInfo.manifest.usesSdk.targetSdkVersion.toString());
		if(apkInfo.manifest.usesSdk.maxSdkVersion != null) apkInfoJSON.put("maxSdkVersion", apkInfo.manifest.usesSdk.maxSdkVersion.toString());
		
		apkInfoJSON.put("fileSize", FileUtil.getFileSize(apkInfo.fileSize, FSStyle.FULL));

		if(apkInfo.manifest.sharedUserId != null) apkInfoJSON.put("sharedUserId", apkInfo.manifest.sharedUserId);
		if(apkInfo.manifest.installLocation != null) apkInfoJSON.put("installLocation", apkInfo.manifest.installLocation);


		apkInfoJSON.put("hasLauncher", apkInfo.manifest.installLocation);
		/*
		isHidden = ApkInfoHelper.isHidden(apkInfo);
		isStartup = ApkInfoHelper.isHidden(apkInfo);
		isInstrumentation = ApkInfoHelper.isInstrumentation(apkInfo);
		debuggable = ApkInfoHelper.isDebuggable(apkInfo);
		 */
		isSamsungSign = (apkInfo.featureFlags & ApkInfo.APP_FEATURE_SAMSUNG_SIGN) != 0 ? true : false;
		isPlatformSign = (apkInfo.featureFlags & ApkInfo.APP_FEATURE_PLATFORM_SIGN) != 0 ? true : false;

		CertSummary = ""; // apkInfo.CertSummary;
		if(apkInfo.certificates != null) {
			for(String sign: apkInfo.certificates) {
				String[] line = sign.split("\n");
				if(line.length >= 3) {
					CertSummary += line[0] + "\n" + line[1] + "\n" + line[2] + "\n\n";
				} else {
					CertSummary += "error\n";
				}
			}
		}


		hasSignatureLevel = false; // apkInfo.hasSignatureLevel;
		hasSignatureOrSystemLevel = false; // apkInfo.hasSignatureOrSystemLevel;
		hasSystemLevel = false; // apkInfo.hasSystemLevel;
		notGrantPermmissions = "";

		ArrayList<UsesPermissionInfo> allPermissions = new ArrayList<UsesPermissionInfo>(); 
		StringBuilder permissionList = new StringBuilder();
		if(apkInfo.manifest.usesPermission != null && apkInfo.manifest.usesPermission.length > 0) {
			permissionList.append("<uses-permission> [" +  apkInfo.manifest.usesPermission.length + "]\n");
			for(UsesPermissionInfo info: apkInfo.manifest.usesPermission) {
				allPermissions.add(info);
				permissionList.append(info.name + " - " + info.protectionLevel);
				if(info.isSignatureLevel()) hasSignatureLevel = true;
				if(info.isSignatureOrSystemLevel()) hasSignatureOrSystemLevel = true;
				if(info.isSystemLevel()) hasSystemLevel = true;
				if(((info.isSignatureLevel() || info.isSignatureOrSystemLevel()) && !(isSamsungSign || isPlatformSign)) || info.isSystemLevel()) {
					notGrantPermmissions += info.name + " - " + info.protectionLevel + "\n";
				}
				if(info.maxSdkVersion != null) {
					permissionList.append(", maxSdkVersion : " + info.maxSdkVersion);
				}
				if(info.isDeprecated()) {
					deprecatedPermissions += info.getDeprecatedMessage() + "\n\n";
				}
				permissionList.append("\n");
			}
		}
		if(apkInfo.manifest.usesPermissionSdk23 != null && apkInfo.manifest.usesPermissionSdk23.length > 0) {
			if(permissionList.length() > 0) {
				permissionList.append("\n");
			}
			permissionList.append("<uses-permission-sdk-23> [" +  apkInfo.manifest.usesPermissionSdk23.length + "]\n");
			for(UsesPermissionInfo info: apkInfo.manifest.usesPermissionSdk23) {
				allPermissions.add(info);
				permissionList.append(info.name + " - " + info.protectionLevel);
				if(info.isSignatureLevel()) hasSignatureLevel = true;
				if(info.isSignatureOrSystemLevel()) hasSignatureOrSystemLevel = true;
				if(info.isSystemLevel()) hasSystemLevel = true;
				if(((info.isSignatureLevel() || info.isSignatureOrSystemLevel()) && !(isSamsungSign || isPlatformSign)) || info.isSystemLevel()) {
					notGrantPermmissions += info.name + " - " + info.protectionLevel + "\n";
				}
				if(info.maxSdkVersion != null) {
					permissionList.append(", maxSdkVersion : " + info.maxSdkVersion);
				}
				if(info.isDeprecated()) {
					deprecatedPermissions += info.getDeprecatedMessage() + "\n\n";
				}
				permissionList.append("\n");
			}
		}

		signaturePermissions = "";
		if(apkInfo.manifest.permission != null && apkInfo.manifest.permission.length > 0) {
			if(permissionList.length() > 0) {
				permissionList.append("\n");
			}
			permissionList.append("<permission> [" +  apkInfo.manifest.permission.length + "]\n");
			for(PermissionInfo info: apkInfo.manifest.permission) {
				permissionList.append(info.name + " - " + info.protectionLevel + "\n");
				if(!"normal".equals(info.protectionLevel)) {
					signaturePermissions += info.name + " - " + info.protectionLevel + "\n";
				}
			}
		}
		allPermissionsList = permissionList.toString();
		permissionGroupManager = new PermissionGroupManager(allPermissions.toArray(new UsesPermissionInfo[allPermissions.size()]));

		Log.e("aaaaaaaa");
		//Log.e(permissionGroupManager.toJSONString());

		StringBuilder deviceReqData = new StringBuilder();
		if(apkInfo.manifest.compatibleScreens != null) {
			for(CompatibleScreensInfo info: apkInfo.manifest.compatibleScreens) {
				deviceReqData.append(info.getReport());
			}
			deviceReqData.append("\n");
		}
		if(apkInfo.manifest.supportsScreens != null) {
			for(SupportsScreensInfo info: apkInfo.manifest.supportsScreens) {
				deviceReqData.append(info.getReport());
			}
			deviceReqData.append("\n");
		}
		if(apkInfo.manifest.usesFeature != null) {
			for(UsesFeatureInfo info: apkInfo.manifest.usesFeature) {
				deviceReqData.append(info.getReport());
			}
			deviceReqData.append("\n");
		}
		if(apkInfo.manifest.usesConfiguration != null) {
			for(UsesConfigurationInfo info: apkInfo.manifest.usesConfiguration) {
				deviceReqData.append(info.getReport());
			}
			deviceReqData.append("\n");
		}
		if(apkInfo.manifest.usesLibrary != null) {
			deviceReqData.append("uses library :\n");
			for(UsesLibraryInfo info: apkInfo.manifest.usesLibrary) {
				deviceReqData.append(info.getReport());
			}
			deviceReqData.append("\n");
		}
		if(apkInfo.manifest.supportsGlTexture != null) {
			for(SupportsGlTextureInfo info: apkInfo.manifest.supportsGlTexture) {
				deviceReqData.append(info.getReport());
			}
			deviceReqData.append("\n");
		}

		deviceRequirements = deviceReqData.toString();

		


		StringBuilder feature = new StringBuilder();
/*
		if("internalOnly".equals(installLocation)) {
			feature.append(makeHyperLink("@event", Resource.STR_FEATURE_ILOCATION_INTERNAL_LAB.getString(), Resource.STR_FEATURE_ILOCATION_INTERNAL_DESC.getString(), "feature-install-location-internal", null));
		} else if("auto".equals(installLocation)) {
			feature.append(makeHyperLink("@event", Resource.STR_FEATURE_ILOCATION_AUTO_LAB.getString(), Resource.STR_FEATURE_ILOCATION_AUTO_DESC.getString(), "feature-install-location-auto", null));
		} else if("preferExternal".equals(installLocation)) {
			feature.append(makeHyperLink("@event", Resource.STR_FEATURE_ILOCATION_EXTERNAL_LAB.getString(), Resource.STR_FEATURE_ILOCATION_EXTERNAL_DESC.getString(), "feature-install-location-external", null));
		}  
		feature.append("<br/>");

		if(isHidden) {
			feature.append(makeHyperLink("@event", Resource.STR_FEATURE_HIDDEN_LAB.getString(), Resource.STR_FEATURE_HIDDEN_DESC.getString(), "feature-hidden", null));
		} else {
			feature.append(makeHyperLink("@event", Resource.STR_FEATURE_LAUNCHER_LAB.getString(), Resource.STR_FEATURE_LAUNCHER_DESC.getString(), "feature-launcher", null));
		}
		if(isStartup) {
			feature.append(", " + makeHyperLink("@event", Resource.STR_FEATURE_STARTUP_LAB.getString(), Resource.STR_FEATURE_STARTUP_DESC.getString(), "feature-startup", null));
		}
		if(!signaturePermissions.isEmpty()) {
			feature.append(", " + makeHyperLink("@event", Resource.STR_FEATURE_SIGNATURE_LAB.getString(), Resource.STR_FEATURE_SIGNATURE_DESC.getString(), "feature-protection-level", null));
		}
		if(sharedUserId != null && !sharedUserId.startsWith("android.uid.system") ) {
			feature.append(", " + makeHyperLink("@event", Resource.STR_FEATURE_SHAREDUSERID_LAB.getString(), Resource.STR_FEATURE_SHAREDUSERID_DESC.getString(), "feature-shared-user-id", null));
		}
		if(deviceRequirements != null && !deviceRequirements.isEmpty()) {
			feature.append(", " + makeHyperLink("@event", Resource.STR_FEATURE_DEVICE_REQ_LAB.getString(), Resource.STR_FEATURE_DEVICE_REQ_DESC.getString(), "feature-device-requirements", null));
		}

		boolean systemSignature = false;
		StringBuilder importantFeatures = new StringBuilder();
		if(sharedUserId != null && sharedUserId.startsWith("android.uid.system")) {
			if(isSamsungSign || isPlatformSign) {
				importantFeatures.append(", <font style=\"color:#ED7E31; font-weight:bold\">");
			} else {
				importantFeatures.append(", <font style=\"color:#FF0000; font-weight:bold\">");
			}
			importantFeatures.append(makeHyperLink("@event", Resource.STR_FEATURE_SYSTEM_UID_LAB.getString(), Resource.STR_FEATURE_SYSTEM_UID_DESC.getString(), "feature-system-user-id", null));
			importantFeatures.append("</font>");
		}
		if(isPlatformSign) {
			importantFeatures.append(", <font style=\"color:#ED7E31; font-weight:bold\">");
			importantFeatures.append(makeHyperLink("@event", Resource.STR_FEATURE_PLATFORM_SIGN_LAB.getString(), Resource.STR_FEATURE_PLATFORM_SIGN_DESC.getString(), "feature-platform-sign", null));
			importantFeatures.append("</font>");
			systemSignature = true;
		}
		if(isSamsungSign) {
			importantFeatures.append(", <font style=\"color:#ED7E31; font-weight:bold\">");
			importantFeatures.append(makeHyperLink("@event", Resource.STR_FEATURE_SAMSUNG_SIGN_LAB.getString(), Resource.STR_FEATURE_SAMSUNG_SIGN_DESC.getString(), "feature-samsung-sign", null));
			importantFeatures.append("</font>");
			systemSignature = true;
		}
		if(((hasSignatureLevel || hasSignatureOrSystemLevel) && !systemSignature) || hasSystemLevel) {
			importantFeatures.append(", <font style=\"color:#ED7E31; font-weight:bold\">");
			importantFeatures.append(makeHyperLink("@event", Resource.STR_FEATURE_REVOKE_PERM_LAB.getString(), Resource.STR_FEATURE_REVOKE_PERM_DESC.getString(), "feature-revoke-permissions", null));
			importantFeatures.append("</font>");
		}
		if(deprecatedPermissions != null && !deprecatedPermissions.isEmpty()) {
			importantFeatures.append(", <font style=\"color:#ED7E31; font-weight:bold\">");
			importantFeatures.append(makeHyperLink("@event", Resource.STR_FEATURE_DEPRECATED_PREM_LAB.getString(), Resource.STR_FEATURE_DEPRECATED_PREM_DESC.getString(), "feature-deprecated-perm", null));
			importantFeatures.append("</font>");
		}
		if(debuggable) {
			importantFeatures.append(", <font style=\"color:#ED7E31; font-weight:bold\">");
			importantFeatures.append(makeHyperLink("@event", Resource.STR_FEATURE_DEBUGGABLE_LAB.getString(), Resource.STR_FEATURE_DEBUGGABLE_DESC.getString(), "feature-debuggable", null));
			importantFeatures.append("</font>");
		}
		if(isInstrumentation) {
			importantFeatures.append(", <font style=\"color:#ED7E31; font-weight:bold\">");
			importantFeatures.append(makeHyperLink("@event", Resource.STR_FEATURE_INSTRUMENTATION_LAB.getString(), Resource.STR_FEATURE_INSTRUMENTATION_DESC.getString(), "feature-instrumentation", null));
			importantFeatures.append("</font>");
		}
		if(importantFeatures.length() > 0) {
			feature.append("<br/>" + importantFeatures.substring(2));
		}

		String permGorupImg = makePermGroup();

		int infoHeight = 280;
		if(permissionGroupManager.getPermGroupMap().keySet().size() > 15) infoHeight = 220;
		else if(permissionGroupManager.getPermGroupMap().keySet().size() > 0) infoHeight = 260;

		mutiLabels = "";
		for(String s: labels) {
			mutiLabels += s + "\n";
		}

		StringBuilder strTabInfo = new StringBuilder("");
		strTabInfo.append("<table>");
		strTabInfo.append("  <tr>");
		strTabInfo.append("    <td width=170 height=" + infoHeight + ">");
		strTabInfo.append("      <image src=\"" + makeBase64Image(iconPath) + "\" width=150 height=150 />");
		strTabInfo.append("    </td>");
		strTabInfo.append("    <td height=" + infoHeight + ">");
		strTabInfo.append("      <div id=\"basic-info\">");
		strTabInfo.append("        <font style=\"font-size:20px; color:#548235; font-weight:bold\">");
		if(labels.length > 1) {
			strTabInfo.append("          " + makeHyperLink("@event", appName, mutiLabels, "other-lang", null));
			strTabInfo.append("        </font>");
		} else {
			strTabInfo.append("          " + appName);
			strTabInfo.append("</font><br/>");
		}
		if(labels.length > 1) {
			strTabInfo.append("        <font style=\"font-size:10px;\">");
			strTabInfo.append("          " + makeHyperLink("@event", "["+labels.length+"]", mutiLabels, "other-lang", null));
			strTabInfo.append("</font><br/>");
		}
		strTabInfo.append("        <font style=\"font-size:15px; color:#4472C4\">");
		strTabInfo.append("          [" + packageName +"]");
		strTabInfo.append("</font><br/>");
		strTabInfo.append("        <font style=\"font-size:15px; color:#ED7E31\">");
		strTabInfo.append("          " + makeHyperLink("@event", "Ver. " + versionName +" / " + (!versionCode.isEmpty() ? versionCode : "0"), "VersionName : " + versionName + "\n" + "VersionCode : " + (!versionCode.isEmpty() ? versionCode : "Unspecified"), "app-version", null));
		strTabInfo.append("        </font><br/>");
		strTabInfo.append("        <br/>");
		strTabInfo.append("        <font style=\"font-size:12px\">");
		strTabInfo.append("          @SDK Ver. " + sdkVersion + "<br/>");
		strTabInfo.append("          ");
		strTabInfo.append("        </font>");
		strTabInfo.append("        <br/><br/>");
		strTabInfo.append("        <font style=\"font-size:12px\">");
		strTabInfo.append("          [" + Resource.STR_FEATURE_LAB.getString() + "] ");
		strTabInfo.append("          " + feature);
		strTabInfo.append("        </font><br/>");
		strTabInfo.append("      </div>");
		strTabInfo.append("    </td>");
		strTabInfo.append("  </tr>");
		strTabInfo.append("</table>");
		strTabInfo.append("<div id=\"perm-group\" style=\"text-align:left; width:480px; padding-top:5px; border-top:1px; border-left:0px; border-right:0px; border-bottom:0px; border-style:solid;\">");
		strTabInfo.append("  <font style=\"font-size:12px;color:black;\">");
		if(allPermissionsList != null && !allPermissionsList.isEmpty()) {
			strTabInfo.append("    [" + Resource.STR_BASIC_PERMISSIONS.getString() + "] - ");
			strTabInfo.append("    " + makeHyperLink("@event","<u>" + Resource.STR_BASIC_PERMLAB_DISPLAY.getString() + "</u>",Resource.STR_BASIC_PERMDESC_DISPLAY.getString(),"display-list", null));
		} else {
			strTabInfo.append("    " + Resource.STR_LABEL_NO_PERMISSION.getString());
		}
		strTabInfo.append("  </font><br/>");
		strTabInfo.append("  <font style=\"font-size:5px\"><br/></font>");
		strTabInfo.append("  " + permGorupImg);
		strTabInfo.append("</div>");
		strTabInfo.append("<div height=10000 width=10000></div>");

		Log.i(strTabInfo.toString());
		*/
	}
	
	@SuppressWarnings("unchecked")
	private void printWidgetInfo() {
		ApkInfo apkInfo = apkScanner.getApkInfo();
		
		JSONObject info = new JSONObject();
		JSONArray data = new JSONArray();
		
		info.put("total", apkInfo.widgets.length);
		
		for(int i=0; i< apkInfo.widgets.length; i++) {
			String label = ApkInfoHelper.getResourceValue(apkInfo.widgets[i].lables, null);
			if(label == null) label = ApkInfoHelper.getResourceValue(apkInfo.manifest.application.labels, null);
			
			JSONObject rec = new JSONObject();
			rec.put("recid", i+1);
			rec.put("image", "<image src=\"" +  makeBase64Image(apkInfo.widgets[i].icons[apkInfo.widgets[i].icons.length-1].name) + "\" width=90 height=90 />");
			rec.put("name", label);
			rec.put("size", apkInfo.widgets[i].size);
			rec.put("class", apkInfo.widgets[i].name);
			rec.put("like", apkInfo.widgets[i].type);
			
			data.add(rec);
		}
		info.put("records", data);
		
		Log.e(info.toJSONString());
	}

	@SuppressWarnings("unchecked")
	private void printLibsInfo() {
		ApkInfo apkInfo = apkScanner.getApkInfo();
		if(apkInfo.libraries == null) return;

		String apkFilePath = apkInfo.filePath;
		
		JSONObject info = new JSONObject();
		JSONArray data = new JSONArray();

		info.put("total", apkInfo.libraries.length);

		for(int i=0; i< apkInfo.libraries.length; i++) {
			long size = ZipFileUtil.getFileSize(apkFilePath, apkInfo.libraries[i]);
			long compressed = ZipFileUtil.getCompressedSize(apkFilePath, apkInfo.libraries[i]);
			

			JSONObject rec = new JSONObject();
			rec.put("recid", i+1);
			rec.put("path", apkInfo.libraries[i]);
			rec.put("size", FileUtil.getFileSize(size, FSStyle.FULL));
			rec.put("ratio", String.format("%.2f", ((float)(size - compressed) / (float)size) * 100f) + " %");

			data.add(rec);
		}
		info.put("records", data);
		
		Log.e(info.toJSONString());
	}

	@SuppressWarnings("unchecked")
	private JSONObject makeTreeNode(int id, String text, String path, String icon, String config) {
		JSONObject node = new JSONObject();
		node.put("id", id);
		node.put("text", text);
		if(icon != null && !icon.isEmpty()) node.put("icon", icon);
		node.put("children", new JSONArray());

		if(config != null && !config.isEmpty()) node.put("config", config);
		node.put("path", path);
		return node;
	}

	private JSONObject makeTreeNode(int id, ResourceObject res) {
		return makeTreeNode(id, res.label, res.path, res.isFolder ? null : "jstree-file", res.config);
	}
	
	private String getOnlyFilename(String str) {
		String separator = (str.indexOf(File.separator) > -1) ? separator = File.separator : "/";
		return str.substring(str.lastIndexOf(separator) + 1, str.length());
	}

	@SuppressWarnings("unchecked")
	private void printRessInfo() {
		ApkInfo apkInfo = apkScanner.getApkInfo();
		if(apkInfo.libraries == null) return;

		int nodeId = 0;

		JSONArray tree = new JSONArray();
		JSONObject top = makeTreeNode(nodeId++, getOnlyFilename(apkInfo.filePath), "/", null, null);
		top.put("state", new JSONObject(ImmutableMap.of("opened", true)));
		tree.add(top);

		final ArrayList<JSONObject> topFiles = new ArrayList<JSONObject>();
		
		JSONObject[] eachTypeNodes = new JSONObject[ResourceType.COUNT.getInt()];

		String[] nameList = apkInfo.resources;
		for (int i = 0; i < nameList.length; i++) {
			if (nameList[i].endsWith("/") || nameList[i].startsWith("lib/")
					/*|| this.nameList[i].startsWith("META-INF/")*/)
				continue;

			ResourceObject resObj = new ResourceObject(nameList[i], false);
			if (nameList[i].indexOf("/") == -1) {
				topFiles.add(makeTreeNode(nodeId++, resObj));
				continue;
			}

			JSONObject typeNode = eachTypeNodes[resObj.type.getInt()];

			if (typeNode == null) {
				typeNode = makeTreeNode(nodeId++, resObj.type.toString(), resObj.type.toString() + "/", null, null);
				eachTypeNodes[resObj.type.getInt()] = typeNode;
				if (resObj.type != ResourceType.ETC) {
					//tree.add(typeNode);
					((JSONArray)top.get("children")).add(typeNode);
				}
			}

			JSONObject findnode = null;
			if (resObj.type != ResourceType.ETC) {
				String fileName = getOnlyFilename(nameList[i]);
				JSONArray childrens = (JSONArray) typeNode.get("children");
				for(Object child: childrens) {
					if(fileName.equals(((JSONObject)child).get("text"))) {
						findnode = (JSONObject)child;
						break;
					}
				}
			}

			if (findnode != null) {
				JSONArray childrens = (JSONArray) findnode.get("children");
				if (childrens.isEmpty()) {
					ResourceObject obj = new ResourceObject((String)findnode.get("path"), false);
					childrens.add(makeTreeNode(nodeId++, obj));
				}
				childrens.add(makeTreeNode(nodeId++, resObj));
			} else {
				((JSONArray)typeNode.get("children")).add(makeTreeNode(nodeId++, resObj));
			}
		}

		if (eachTypeNodes[ResourceType.ETC.getInt()] != null) {
			((JSONArray)top.get("children")).add(eachTypeNodes[ResourceType.ETC.getInt()]);
		}

		for (JSONObject node : topFiles) {
			((JSONArray)top.get("children")).add(node);
		}

		for(ResourceType type: ResourceType.values()) {
			switch(type) {
			case ASSET: case METAINF: case ETC: case COUNT: continue;
			default: if(eachTypeNodes[type.getInt()] == null) continue;
			}
			JSONObject node = eachTypeNodes[type.getInt()];
			JSONArray childrens = (JSONArray)node.get("children");
			Log.e(node.get("text") + " " + childrens.size());
			for(Object obj: childrens) {
				JSONObject child = (JSONObject)obj;
				int size = ((JSONArray)child.get("children")).size();
				if(size <= 0) {
					if(child.get("config") != null) {
						child.put("text", child.get("text") + " (" + child.get("config") +")");
					}
				} else {
					child.put("text", child.get("text") + " (" + size +")");

					for(Object tmp: ((JSONArray)child.get("children"))) {
						JSONObject grandchild = (JSONObject)tmp;
						if(grandchild.get("config") != null) {
							grandchild.put("text", grandchild.get("text") + " (" + grandchild.get("config") +")");
						}
					}
				}
			}
		}

		Log.e(tree.toJSONString());
	}

	
	
	@SuppressWarnings("unchecked")
	private void printCompsInfo() {
		ApkInfo apkInfo = apkScanner.getApkInfo();

		JSONObject info = new JSONObject();
		JSONArray data = new JSONArray();
		
		ArrayList<Object[]> ComponentList = new ArrayList<Object[]>();
		if(apkInfo.manifest.application.activity != null) {
			for(ActivityInfo comp: apkInfo.manifest.application.activity) {
				String type = null;
				if((comp.featureFlag & ApkInfo.APP_FEATURE_LAUNCHER) != 0 && (comp.featureFlag & ApkInfo.APP_FEATURE_MAIN) != 0) {
					type = "launcher";
				} else if((comp.featureFlag & ApkInfo.APP_FEATURE_MAIN) != 0) {
					type = "main";
				} else if((comp.featureFlag & ApkInfo.APP_FEATURE_LAUNCHER) != 0) {
					Log.w("set launcher flag, but not main");
					type = "activity";
				} else {
					type = "activity";
				}
				String startUp = (comp.featureFlag & ApkInfo.APP_FEATURE_STARTUP) != 0 ? "O" : "X";
				String enabled = (comp.enabled == null) || comp.enabled ? "O" : "X";
				String exported = (comp.exported == null) || comp.exported ? "O" : "X";
				String permission = comp.permission != null ? "O" : "X";
				if((comp.featureFlag & ApkInfo.APP_FEATURE_LAUNCHER) != 0) {
					ComponentList.add(0, new Object[] {comp.name, type, enabled, exported, permission, startUp, comp.getReport()});
				} else {
					ComponentList.add(new Object[] {comp.name, type, enabled, exported, permission, startUp, comp.getReport()});
				}
			}
		}
		if(apkInfo.manifest.application.activityAlias != null) {
			for(ActivityAliasInfo comp: apkInfo.manifest.application.activityAlias) {
				String type = null;
				if((comp.featureFlag & ApkInfo.APP_FEATURE_LAUNCHER) != 0 && (comp.featureFlag & ApkInfo.APP_FEATURE_MAIN) != 0) {
					type = "launcher-alias";
				} else if((comp.featureFlag & ApkInfo.APP_FEATURE_MAIN) != 0) {
					type = "main-alias";
				} else if((comp.featureFlag & ApkInfo.APP_FEATURE_LAUNCHER) != 0) {
					Log.w("set launcher flag, but not main");
					type = "activity-alias";
				} else {
					type = "activity-alias";
				}
				String startUp = (comp.featureFlag & ApkInfo.APP_FEATURE_STARTUP) != 0 ? "O" : "X";
				String enabled = (comp.enabled == null) || comp.enabled ? "O" : "X";
				String exported = (comp.exported == null) || comp.exported ? "O" : "X";
				String permission = comp.permission != null ? "O" : "X";
				if((comp.featureFlag & ApkInfo.APP_FEATURE_LAUNCHER) != 0) { 
					int i = 0;
					for(;i<ComponentList.size();i++) {
						String t = (String)((Object[])ComponentList.get(i))[1];
						if(t == null || !t.equals("launcher")) break;
					}
					ComponentList.add(i, new Object[] {comp.name, type, enabled, exported, permission, startUp, comp.getReport()});
				} else {
					ComponentList.add(new Object[] {comp.name, type, enabled, exported, permission, startUp, comp.getReport()});
				}
			}
		}
		if(apkInfo.manifest.application.service != null) {
			for(ServiceInfo comp: apkInfo.manifest.application.service) {
				String startUp = (comp.featureFlag & ApkInfo.APP_FEATURE_STARTUP) != 0 ? "O" : "X";
				String enabled = (comp.enabled == null) || comp.enabled ? "O" : "X";
				String exported = (comp.exported == null) || comp.exported ? "O" : "X";
				String permission = comp.permission != null ? "O" : "X"; 
				ComponentList.add(new Object[] {comp.name, "service", enabled, exported, permission, startUp, comp.getReport()});
			}
		}
		if(apkInfo.manifest.application.receiver != null) {
			for(ReceiverInfo comp: apkInfo.manifest.application.receiver) {
				String startUp = (comp.featureFlag & ApkInfo.APP_FEATURE_STARTUP) != 0 ? "O" : "X";
				String enabled = (comp.enabled == null) || comp.enabled ? "O" : "X";
				String exported = (comp.exported == null) || comp.exported ? "O" : "X";
				String permission = comp.permission != null ? "O" : "X"; 
				ComponentList.add(new Object[] {comp.name, "receiver", enabled, exported, permission, startUp, comp.getReport()});
			}
		}
		if(apkInfo.manifest.application.provider != null) {
			for(ProviderInfo comp: apkInfo.manifest.application.provider) {
				String startUp = "X";
				String enabled = (comp.enabled == null) || comp.enabled ? "O" : "X";
				String exported = (comp.exported == null) || comp.exported ? "O" : "X";
				String permission = "X";
				if(comp.permission != null || (comp.readPermission != null && comp.writePermission != null)) {
					permission = "R/W"; 
				} else if(comp.readPermission != null) {
					permission = "Read";
				} else if(comp.writePermission != null) {
					permission = "Write";
				}
				ComponentList.add(new Object[] {comp.name, "provider", enabled, exported, permission, startUp, comp.getReport()});
				//String startUp = (info.featureFlag & ActivityInfo.ACTIVITY_FEATURE_STARTUP) != 0 ? "O" : "X";
			}
		}
		
		info.put("total", ComponentList.size());
		int i = 1;
		for(Object[] item: ComponentList) {
			JSONObject rec = new JSONObject();
			rec.put("recid", i++);
			rec.put("class", item[0]);
			rec.put("like", item[1]);
			rec.put("state", item[2]);
			rec.put("export", item[3]);
			rec.put("perm", item[4]);
			rec.put("startup", item[5]);
			rec.put("content", item[6]);
			
			String cellColor = "";
			if("activity".equals(item[1]) || "main".equals(item[1])) {
				cellColor = "#B7F0B1";
			} else if("launcher".equals(item[1])) {
				cellColor = "#5D9657";
			} else if("activity-alias".equals(item[1])) {
				cellColor = "#96E2E2";
			} else if("service".equals(item[1])) {
				cellColor = "#B2CCFF";
			} else if("receiver".equals(item[1])) {
				cellColor = "#CEF279";
			} else if("provider".equals(item[1])) {
				cellColor = "#FFE08C";
			} else {
				cellColor = "#C8C8C8";
			}
			JSONObject style = new JSONObject();
			style.put("style", "background-color: " + cellColor);
			rec.put("w2ui", style);

			data.add(rec);
		}

		info.put("records", data);
		
		Log.e(info.toJSONString());
	}

	@SuppressWarnings("unchecked")
	private void printSignInfo() {
		ApkInfo apkInfo = apkScanner.getApkInfo();
		if(apkInfo.libraries == null) return;

		String apkFilePath = apkInfo.filePath;
		
		JSONObject info = new JSONObject();
		JSONArray data = new JSONArray();


		apkFilePath = apkInfo.filePath;
		String[] mCertList = apkInfo.certificates;
		String[] mCertFiles = apkInfo.certFiles;
		String mCertSummary = "";

		if(mCertList != null) {
			for(String sign: mCertList) {
				String[] line = sign.split("\n");
				if(line.length >= 3) {
					mCertSummary += line[0] + "\n" + line[1] + "\n" + line[2] + "\n\n";
				} else {
					mCertSummary += "error\n";
				}
			}
		}

		int listSize = mCertList.length;
		if(listSize > 1) {
			listSize++;
		}
		if(mCertFiles != null) {
			listSize += mCertFiles.length;
		}
		
		int i = 1;
		if(mCertList.length > 1) {
			JSONObject rec = new JSONObject();
			rec.put("recid", i);
			rec.put("item", Resource.STR_CERT_SUMMURY.getString());
			rec.put("content", mCertSummary);
			data.add(rec);

			for(; i <= mCertList.length; i++) {
				rec = new JSONObject();
				rec.put("recid", i+1);
				rec.put("item", Resource.STR_CERT_CERTIFICATE.getString() + "[" + i + "]");
				rec.put("content", mCertList[i-1]);
				data.add(rec);
			}
		} else if (mCertList.length == 1) {
			JSONObject rec = new JSONObject();
			rec.put("recid", i++);
			rec.put("item", Resource.STR_CERT_CERTIFICATE.getString() + "[1]");
			rec.put("content", mCertList[0]);
			data.add(rec);
		}

		if(mCertFiles != null) {
			for(String path: mCertFiles){
				ZipFile zipFile = null;
				InputStream is = null;
				String content = "";
				try {
					zipFile = new ZipFile(apkFilePath);
					ZipEntry entry = zipFile.getEntry(path);
					byte[] buffer = new byte[(int) entry.getSize()];
					is = zipFile.getInputStream(entry);
					is.read(buffer);
					content = new String(buffer);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if(is != null) {
						try {
							is.close();
						} catch (IOException e) { }
					}
					if(zipFile != null) {
						try {
							zipFile.close();
						} catch (IOException e) { }
					}
				}
				
				JSONObject rec = new JSONObject();
				rec.put("recid", i++);
				rec.put("item", path.substring(path.lastIndexOf("/")+1));
				rec.put("content", content);
				data.add(rec);
			}
		}
		
		info.put("total", listSize);
		info.put("records", data);
		
		Log.e(info.toJSONString());
	}

	private String makeHyperLink(String href, String text, String title, String id, String style)
	{
		if(href.startsWith("@event") && id != null && !id.isEmpty()) {
			String func = "event";
			
			if("other-lang".equals(id)) {
				func = "showMultiLabels";
			} else if("app-version".equals(id)) {
				func = "showApkVersionInfo";
			} else if("display-list".equals(id)) {
				func = "showAllPermList";
			} else if(id.endsWith("-sdk")){
				func = "showSdkVersionInfo";
			} else if(id.startsWith("feature-")) {
				func = "showFeatureInfo";
			} else {
				func = "showPermInfo";
			}
			
			href = String.format("javascript:%s('%s')", func, id);
			id = null;
		}
		return JHtmlEditorPane.makeHyperLink(href, text, title, id, style);
	}

	private String makeImage(String src)
	{
		String icon_class = src.replaceAll(".*(perm_.*)\\.png", "$1").replaceAll("_", "-");
		return "<div class=\"perm-group-icons " + icon_class + "\"></div>";
	}

	private String makePermGroup()
	{
		StringBuilder permGroup = new StringBuilder("");

		Set<String> keys = permissionGroupManager.getPermGroupMap().keySet();
		int cnt = 0;
		for(String key: keys) {
			PermissionGroup g = permissionGroupManager.getPermGroupMap().get(key);
			permGroup.append(makeHyperLink("@event", makeImage(g.getIconPath()), g.permSummary, g.name, g.hasDangerous?"color:red;":null));
			if(++cnt % 15 == 0) permGroup.append("<br/>");
		}


		return permGroup.toString();
	}
	
	private String makeBase64Image(String path) {
		String base64 = "";
		ImageIcon myimageicon = null;

		String extension = path.replaceAll(".*/", "").replaceAll(".*\\.", "").toLowerCase();
		
		try {
			myimageicon = new ImageIcon(new URL(path));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if(myimageicon != null) {
			myimageicon.setImage(ImageScaler.getMaxScaledImage(myimageicon,100,100));

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			try {
				ImageIO.write((RenderedImage) myimageicon.getImage(), extension, output);
			} catch (IOException e) {
				e.printStackTrace();
			}
			base64 = "data:image/"+extension+";base64," + DatatypeConverter.printBase64Binary(output.toByteArray());
		}
		
		return base64;
	}
	
	@Override
	public void onStateChanged(Status status) {
		Log.i("onStateChanged() " + status);
		switch(status) {
		case BASIC_INFO_COMPLETED:
			break;
		case WIDGET_COMPLETED:
			break;
		case LIB_COMPLETED:
			break;
		case RESOURCE_COMPLETED:
			break;
		case RES_DUMP_COMPLETED:
			break;
		case ACTIVITY_COMPLETED:
			break;
		case CERT_COMPLETED:
			break;
		case ALL_COMPLETED:
			printBasicInfo();
			printWidgetInfo();
			printLibsInfo();
			printRessInfo();
			printCompsInfo();
			printSignInfo();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onSuccess() {
		Log.v("ApkCore.onSuccess()");
	}
	
	@Override
	public void onStart(long estimatedTime) {
		Log.v("ApkCore.onStart()");
	}
	
	@Override
	public void onProgress(int step, String msg) {
		Log.v("ApkCore.onProgress() " + msg);
	}
	
	@Override
	public void onError(int error) {
		Log.v("ApkCore.onError() " + error);
	}
	
	@Override
	public void onCompleted() {
		Log.v("ApkCore.onCompleted()");
	}
	

}

package com.apkscanner.tool.adb;

import java.io.IOException;
import java.util.ArrayList;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.apkscanner.tool.adb.AdbDeviceHelper.SimpleOutputReceiver;
import com.apkscanner.util.Log;

public class AdbPackageManager {

	public static class PackageListObject {
		public String label;
		public String pacakge;
		public String codePath;
		public String apkPath;
		public String installer;

		@Override
		public String toString() {
			return this.label;
		}
	}

	public static class PackageInfo
	{
		public final String pkgName;
		public final String apkPath;
		public final String codePath;
		public final String versionName;
		public final int versionCode;
		public final boolean isSystemApp;
		public final String installer;

		public PackageInfo(String pkgName, String apkPath, String codePath, String versionName, int versionCode, boolean isSystemApp, String installer)
		{
			this.pkgName = pkgName;
			this.apkPath = apkPath;
			this.codePath = codePath;
			this.versionName = versionName;
			this.versionCode = versionCode;
			this.isSystemApp = isSystemApp;
			this.installer = installer;
		}

		@Override
		public String toString()
		{
			String s = "-Installed APK info\n";
			s += "Pakage : " + pkgName +"\n";
			s += "Version : " + versionName + " / " + versionCode +"\n";
			s += "APK Path : " + apkPath +"\n";
			s += "Installer : " + installer +"\n";
			return s;
		}
	}

	public static String[] getRecentlyActivityPackages(String device) {
		String[] result = AdbWrapper.shell(device, new String[] {"am", "stack", "list"}, null);
		ArrayList<String> pkgList = new ArrayList<String>();
		boolean isLegacy = false;
		for(String line: result) {
			if(line.startsWith("  taskId=")) {
				String pkg = line.replaceAll("  taskId=[0-9]*:\\s([^/]*)/.*", "$1").trim(); 
				if(pkg != null && !pkg.isEmpty() && !pkg.equals(line)) {
					if(pkg.indexOf(" ") == -1 && !pkgList.contains(pkg)) {
						if(line.indexOf("visible=true") >= 0)
							pkgList.add(0, pkg);
						else
							pkgList.add(pkg);
					} else {
						Log.w("Unknown pkg - " + pkg);
					}
				}
			}
			if(line.startsWith("Error: unknown command 'list'")) {
				isLegacy = true;
				break;
			}
		}

		if(isLegacy) {
			return getRecentlyActivityPackagesLegacy(device);
		}

		return pkgList.toArray(new String[0]);
	}

	private static String[] getRecentlyActivityPackagesLegacy(String device) {
		String[] result = AdbWrapper.shell(device, new String[] {"am", "stack", "boxes"}, null);
		ArrayList<String> pkgList = new ArrayList<String>();
		for(String line: result) {
			if(line.startsWith("    taskId=")) {
				String pkg = line.replaceAll("    taskId=[0-9]*:\\s([^/]*)/.*", "$1").trim(); 
				if(pkg != null && !pkg.isEmpty() && !pkg.equals(line)) {
					if(pkg.indexOf(" ") == -1 && !pkgList.contains(pkg)) {
						pkgList.add(0, pkg);
					} else {
						Log.w("Unknown pkg - " + pkg);
					}
				}
			}
		}
		return pkgList.toArray(new String[0]);
	}

	public static String[] getCurrentlyRunningPackages(String device) {
		String[] result = AdbWrapper.shell(device, new String[] {"ps"}, null);
		ArrayList<String> pkgList = new ArrayList<String>();
		for(String line: result) {
			if(!line.startsWith("root")) {
				String pkg = line.replaceAll(".* ([^\\s:]*)(:.*)?$", "$1");
				if(pkg != null && !pkg.isEmpty() && !pkg.equals(line)) {
					if(!pkg.startsWith("/") && !pkgList.contains(pkg)) {
						pkgList.add(pkg);
					}
				}
			}
		}
		if(pkgList.size() > 0 && pkgList.get(0).equals("NAME")) {
			pkgList.remove(0);
		}
		return pkgList.toArray(new String[0]);
	}

	public static ArrayList<PackageListObject> getPackageList(String device)
	{
		ArrayList<PackageListObject> list = new ArrayList<PackageListObject>();

		String[] result = AdbWrapper.shell(device, new String[] {"dumpsys", "package"}, null);
		String[] pmList = AdbWrapper.shell(device, new String[] {"pm", "list", "packages", "-f", "-i", "-u"}, null);

		boolean start = false;
		PackageListObject pack = null;
		String verName = null;
		String verCode = null;
		for(String line: result) {
			if(!start) {
				if(line.startsWith("Packages:")) {
					start = true;
				}
				continue;
			}
			if(line.matches("^\\s*Package\\s*\\[.*")) {
				if(pack != null) {
					if(pack.apkPath == null) {
						pack.apkPath = pack.codePath;
					}
					pack.label = pack.apkPath.replaceAll(".*/", "") + " - [" + pack.pacakge + "] - " + verName + "/" + verCode;
					list.add(pack);
				}
				pack = new PackageListObject();
				verName = null;
				verCode = null;
				pack.pacakge = line.replaceAll("^\\s*Package\\s*\\[(.*)\\].*:\\s*$", "$1");
				pack.codePath = null;
				pack.apkPath = null;
				for(String output: pmList) {
					if(output.matches("^package:.*=" + pack.pacakge + "\\s*installer=.*")) {
						pack.apkPath = output.replaceAll("^package:(.*)=" + pack.pacakge + "\\s*installer=(.*)", "$1");
						//pack.installer = output.replaceAll("^package:(.*)=" + pack.pacakge + "\\s*installer=(.*)", "$2");
					}
				}
			} else if(pack != null && pack.codePath == null && line.matches("^\\s*codePath=.*$")) {
				pack.codePath = line.replaceAll("^\\s*codePath=\\s*([^\\s]*).*$", "$1");
				if(pack.apkPath != null && !pack.apkPath.startsWith(pack.codePath)) {
					pack.apkPath = pack.codePath;
				}
			} else if(verName == null && line.matches("^\\s*versionName=.*$")) {
				verName = line.replaceAll("^\\s*versionName=\\s*([^\\s]*).*$", "$1");
			} else if(verCode == null && line.matches("^\\s*versionCode=.*$")) {
				verCode = line.replaceAll("^\\s*versionCode=\\s*([^\\s]*).*$", "$1");
			}
		}
		if(pack != null) {
			if(pack.apkPath == null) {
				pack.apkPath = pack.codePath;
			}
			pack.label = pack.apkPath.replaceAll(".*/", "") + " - [" + pack.pacakge + "] - " + verName + "/" + verCode;
			list.add(pack);
		}

		result = AdbWrapper.shell(device, new String[] {"ls", "/system/framework/*.apk"}, null);
		for(String line: result) {
			if(line.equals("/system/framework/framework-res.apk")
					|| !line.endsWith(".apk")) continue;
			pack = new PackageListObject();
			pack.apkPath = line;
			pack.codePath = "/system/framework";
			pack.pacakge = pack.apkPath.replaceAll(".*/(.*)\\.apk", "$1");
			pack.label = pack.apkPath.replaceAll(".*/", "");
			list.add(pack);
		}

		return list;
	}

	public static PackageInfo getPackageInfo(String device, String pkgName)
	{
		String[] result;
		String[] TargetInfo;
		String verName = null;
		int verCode = 0;
		String codePath = null;
		String apkPath = null;
		String installer = null;

		if(pkgName == null) return null;

		//Log.i("ckeckPackage() " + pkgName);

		if(!pkgName.matches("/system/framework/.*apk")) {
			result = AdbWrapper.shell(device, new String[] {"pm", "list", "packages", "-f", "-i", "-u", pkgName}, null);
			for(String output: result) {
				if(output.matches("^package:.*=" + pkgName + "\\s*installer=.*")) {
					apkPath = output.replaceAll("^package:(.*)=" + pkgName + "\\s*installer=(.*)", "$1");
					installer = output.replaceAll("^package:(.*)=" + pkgName + "\\s*installer=(.*)", "$2");
				}
			}

			TargetInfo = AdbWrapper.shell(device, new String[] {"dumpsys","package", pkgName}, null);

			verName = selectString(TargetInfo,"versionName=");
			String vercode = selectString(TargetInfo,"versionCode=");
			if(vercode != null && vercode.matches("\\d+")) {
				verCode = Integer.valueOf(selectString(TargetInfo,"versionCode="));
			}
			codePath = selectString(TargetInfo,"codePath=");

			if(installer == null)
				installer = selectString(TargetInfo,"installerPackageName=");

			if(installer != null && installer.equals("null"))
				installer = null;			
		} else {
			codePath = pkgName;
			apkPath = pkgName;
		}

		boolean isSystemApp = false;
		if((apkPath != null && apkPath.matches("^/system/.*"))
				|| (codePath != null && codePath.matches("^/system/.*"))) {
			isSystemApp = true;
		}

		if(apkPath == null && codePath != null && !codePath.isEmpty() 
				&& (isSystemApp || (!isSystemApp && AdbWrapper.root(device, null)))) {
			result = AdbWrapper.shell(device, new String[] {"ls", codePath}, null);
			for(String output: result) {
				if(output.matches("^.*apk")) {
					apkPath = codePath + "/" + output;
				}
			}
		}

		if(apkPath == null) return null;

		return new PackageInfo(pkgName, apkPath, codePath, verName, verCode, isSystemApp, installer);
	}

	public static PackageInfo getPackageInfo(IDevice device, String pkgName)
	{
		String[] result;
		String[] TargetInfo;
		String verName = null;
		int verCode = 0;
		String codePath = null;
		String apkPath = null;
		String installer = null;

		SimpleOutputReceiver outputReceiver = new SimpleOutputReceiver();

		if(pkgName == null) return null;

		//Log.i("ckeckPackage() " + pkgName);

		if(!pkgName.matches("/system/framework/.*apk")) {
			try {
				device.executeShellCommand("pm list packages -f -i -u " + pkgName, outputReceiver);
			} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException
					| IOException e) {
				e.printStackTrace();
			}
			result = outputReceiver.getOutput();
			for(String output: result) {
				if(output.matches("^package:.*=" + pkgName + "\\s*installer=.*")) {
					apkPath = output.replaceAll("^package:(.*)=" + pkgName + "\\s*installer=(.*)", "$1");
					installer = output.replaceAll("^package:(.*)=" + pkgName + "\\s*installer=(.*)", "$2");
				}
			}

			outputReceiver.clear();
			try {
				device.executeShellCommand("dumpsys package " + pkgName, outputReceiver);
			} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException
					| IOException e) {
				e.printStackTrace();
			}
			TargetInfo = outputReceiver.getOutput();

			verName = selectString(TargetInfo,"versionName=");
			String vercode = selectString(TargetInfo,"versionCode=");
			if(vercode != null && vercode.matches("\\d+")) {
				verCode = Integer.valueOf(selectString(TargetInfo,"versionCode="));
			}
			codePath = selectString(TargetInfo,"codePath=");

			if(installer == null)
				installer = selectString(TargetInfo,"installerPackageName=");

			if(installer != null && installer.equals("null"))
				installer = null;			
		} else {
			codePath = pkgName;
			apkPath = pkgName;
		}

		boolean isSystemApp = false;
		if((apkPath != null && apkPath.matches("^/system/.*"))
				|| (codePath != null && codePath.matches("^/system/.*"))) {
			isSystemApp = true;
		}

		if(apkPath == null && codePath != null && !codePath.isEmpty()) {
			boolean isRoot = false;
			if(!isSystemApp){
				outputReceiver.clear();
				try {
					device.executeShellCommand("id", outputReceiver);
				} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException
						| IOException e) {
					e.printStackTrace();
				}
				result = outputReceiver.getOutput();
				for(String output: result) {
					if(output.indexOf("uid=0") > -1) {
						isRoot = true;
					}
				}
			}
			if(isSystemApp || isRoot) {
				outputReceiver.clear();
				try {
					device.executeShellCommand("ls " + codePath, outputReceiver);
				} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException
						| IOException e) {
					e.printStackTrace();
				}
				result = outputReceiver.getOutput();
				for(String output: result) {
					if(output.matches("^.*apk")) {
						apkPath = codePath + "/" + output;
					}
				}
			}
		}

		if(apkPath == null) return null;

		return new PackageInfo(pkgName, apkPath, codePath, verName, verCode, isSystemApp, installer);
	}

	private static String selectString(String[] source, String key)
	{
		String temp = null;

		for(int i=0; i < source.length; i++) {
			if(source[i].matches("^\\s*"+key+".*$")) {
				temp = source[i].replaceAll("^\\s*"+key+"\\s*([^\\s]*).*$", "$1");
				break;
			}
		}
		return temp;
	}
}

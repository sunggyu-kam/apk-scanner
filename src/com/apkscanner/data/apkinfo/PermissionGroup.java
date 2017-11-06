package com.apkscanner.data.apkinfo;

import java.util.ArrayList;

public class PermissionGroup
{
	public String name;
	public String label;
	public String desc;
	public String icon;
	public String permSummary;
	public ArrayList<PermissionInfo> permList; 
	public boolean hasDangerous;

	public String getIconPath() {
		if(icon == null || !icon.startsWith("@drawable")) {
			icon = "@drawable/perm_group_unknown";
		}
		String path = icon.replace("@drawable/", "");

		if(getClass().getResource("/icons/" + path + ".png") != null) {
			path = getClass().getResource("/icons/" + path + ".png").toString();
		} else {
			//path = getClass().getResource("/icons/perm_group_default.png").toString();
		}
		return path;
	}

	public String getIconClassNameForWeb() {
		String className = "perm-group-unknown";
		if(icon != null && icon.startsWith("@drawable")) {
			className = icon.replace("@drawable/", "").replaceAll("_", "-");
		}
		return className;
	}
	
	
}

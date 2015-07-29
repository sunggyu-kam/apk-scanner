package com.ApkInfo.Resource;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import javax.swing.ImageIcon;

import com.ApkInfo.Core.CoreApkTool;
import com.ApkInfo.Core.MyXPath;

public enum Resource
{
	STR_APP_NAME				(Type.TEXT, "@app_name"),
	STR_APP_VERSION				(Type.TEXT, "1.5"),
	STR_APP_MAKER				(Type.TEXT, "jin_h.lee / sunggyu.kam"),
	STR_APP_MAKER_EMAIL			(Type.TEXT, "jin_h.lee@samsung.com;sunggyu.kam@samsung.com"),

	STR_BTN_OPEN				(Type.TEXT, "@btn_open"),
	STR_BTN_MANIFEST			(Type.TEXT, "@btn_manifest"),
	STR_BTN_EXPLORER			(Type.TEXT, "@btn_explorer"),
	STR_BTN_UNPACK				(Type.TEXT, "@btn_unpack"),
	STR_BTN_PACK				(Type.TEXT, "@btn_pack"),
	STR_BTN_INSTALL				(Type.TEXT, "@btn_install"),
	STR_BTN_SETTING				(Type.TEXT, "@btn_setting"),
	STR_BTN_ABOUT				(Type.TEXT, "@btn_about"),
	
	STR_MENU_NEW				(Type.TEXT, "@menu_new"),
	STR_MENU_NEW_WINDOW			(Type.TEXT, "@menu_new_window"),
	STR_MENU_NEW_APK_FILE		(Type.TEXT, "@menu_new_apk_file"),
	STR_MENU_NEW_PACKAGE		(Type.TEXT, "@menu_new_package"),
	STR_MENU_APK_FILE			(Type.TEXT, "@menu_apk_file"),
	STR_MENU_PACKAGE			(Type.TEXT, "@menu_package"),
	STR_MENU_INSTALL			(Type.TEXT, "@menu_install"),
	STR_MENU_CHECK_INSTALLED	(Type.TEXT, "@menu_check_installed"),

	STR_TAB_BASIC_INFO			(Type.TEXT, "@tab_basic_info"),
	STR_TAB_WIDGET				(Type.TEXT, "@tab_widget"),
	STR_TAB_LIB					(Type.TEXT, "@tab_lib"),
	STR_TAB_IMAGE				(Type.TEXT, "@tab_image"),
	STR_TAB_ACTIVITY			(Type.TEXT, "@tab_activity"),
	STR_TAB_CERT				(Type.TEXT, "@tab_cert"),
	
	STR_BASIC_PERMISSIONS		(Type.TEXT, "@basic_permissions"),
	STR_BASIC_PERMLAB_DISPLAY	(Type.TEXT, "@basic_permlab_display_list"),
	STR_BASIC_PERMDESC_DISPLAY	(Type.TEXT, "@basic_permdesc_display_list"),
	STR_BASIC_PERM_LIST_TITLE	(Type.TEXT, "@basic_perm_list_title"),
	STR_BASIC_PERM_DISPLAY_TITLE(Type.TEXT, "@basic_perm_display_title"),

	STR_FEATURE_LAB				(Type.TEXT, "@feature_lab"),
	STR_FEATURE_DESC			(Type.TEXT, "@feature_desc"),
	STR_FEATURE_LAUNCHER_LAB	(Type.TEXT, "@feature_launcher_lab"),
	STR_FEATURE_LAUNCHER_DESC	(Type.TEXT, "@feature_launcher_desc"),
	STR_FEATURE_HIDDEN_LAB		(Type.TEXT, "@feature_hidden_lab"),
	STR_FEATURE_HIDDEN_DESC		(Type.TEXT, "@feature_hidden_desc"),
	STR_FEATURE_STARTUP_LAB		(Type.TEXT, "@feature_startup_lab"),
	STR_FEATURE_STARTUP_DESC	(Type.TEXT, "@feature_startup_desc"),
	STR_FEATURE_SIGNATURE_LAB	(Type.TEXT, "@feature_signature_lab"),
	STR_FEATURE_SIGNATURE_DESC	(Type.TEXT, "@feature_signature_desc"),
	STR_FEATURE_SHAREDUSERID_LAB  (Type.TEXT, "@feature_shared_user_id_lab"),
	STR_FEATURE_SHAREDUSERID_DESC (Type.TEXT, "@feature_shared_user_id_desc"),

	STR_WIDGET_COLUMN_IMAGE		(Type.TEXT, "@widget_column_image"),
	STR_WIDGET_COLUMN_LABEL		(Type.TEXT, "@widget_column_label"),
	STR_WIDGET_COLUMN_SIZE		(Type.TEXT, "@widget_column_size"),
	STR_WIDGET_COLUMN_ACTIVITY	(Type.TEXT, "@widget_column_activity"),
	STR_WIDGET_COLUMN_TYPE		(Type.TEXT, "@widget_column_type"),
	STR_WIDGET_RESIZE_MODE		(Type.TEXT, "@widget_resize_mode"),
	STR_WIDGET_HORIZONTAL		(Type.TEXT, "@widget_horizontal"),
	STR_WIDGET_VERTICAL			(Type.TEXT, "@widget_vertical"),
	STR_WIDGET_TYPE_NORMAL		(Type.TEXT, "@widget_type_nomal"),
	STR_WIDGET_TYPE_SHORTCUT	(Type.TEXT, "@widget_type_shortcut"),
	
	STR_LIB_COLUMN_INDEX		(Type.TEXT, "@lib_column_index"),
	STR_LIB_COLUMN_PATH			(Type.TEXT, "@lib_column_path"),
	STR_LIB_COLUMN_SIZE			(Type.TEXT, "@lib_column_size"),
	
	STR_ACTIVITY_COLUME_CLASS	(Type.TEXT, "@activity_column_class"),
	STR_ACTIVITY_COLUME_TYPE	(Type.TEXT, "@activity_column_type"),
	STR_ACTIVITY_COLUME_STARTUP	(Type.TEXT, "@activity_column_startup"),
	STR_ACTIVITY_TYPE_ACTIVITY	(Type.TEXT, "@activity_type_activity"),
	STR_ACTIVITY_TYPE_SERVICE	(Type.TEXT, "@activity_type_service"),
	STR_ACTIVITY_TYPE_RECEIVER	(Type.TEXT, "@activity_type_receiver"),
	STR_ACTIVITY_LABEL_INTENT	(Type.TEXT, "@activity_label_intent_filter"),

	STR_CERT_SUMMURY			(Type.TEXT, "@cert_summury"),
	STR_CERT_CERTIFICATE		(Type.TEXT, "@cert_certificate"),
	
	STR_FILE_SIZE_BYTES			(Type.TEXT, "@file_size_Bytes"),
	STR_FILE_SIZE_KB			(Type.TEXT, "@file_size_KB"),
	STR_FILE_SIZE_MB			(Type.TEXT, "@file_size_MB"),
	STR_FILE_SIZE_GB			(Type.TEXT, "@file_size_GB"),
	STR_FILE_SIZE_TB			(Type.TEXT, "@file_size_TB"),
	
	
	STR_TREE_OPEN_PACKAGE			(Type.TEXT, "@tree_open_package"),
	STR_TREE_REFRESH					(Type.TEXT, "@Refresh"),
	STR_TREE_EXIT							(Type.TEXT, "@Exit"),

	
	IMG_TOOLBAR_OPEN			(Type.IMAGE, "toolbar_open.png"),
	IMG_TOOLBAR_OPEN_HOVER		(Type.IMAGE, "toolbar_open_hover.png"),
	IMG_TOOLBAR_MANIFEST		(Type.IMAGE, "toolbar_manifast.png"),
	IMG_TOOLBAR_MANIFEST_HOVER	(Type.IMAGE, "toolbar_manifast_hover.png"),
	IMG_TOOLBAR_EXPLORER		(Type.IMAGE, "toolbar_explorer.png"),
	IMG_TOOLBAR_EXPLORER_HOVER	(Type.IMAGE, "toolbar_explorer_hover.png"),
	IMG_TOOLBAR_PACK			(Type.IMAGE, "toolbar_pack.png"),
	IMG_TOOLBAR_PACK_HOVER		(Type.IMAGE, "toolbar_pack_hover.png"),
	IMG_TOOLBAR_UNPACK			(Type.IMAGE, "toolbar_unpack.png"),
	IMG_TOOLBAR_UNPACK_HOVER	(Type.IMAGE, "toolbar_unpack_hover.png"),
	IMG_TOOLBAR_INSTALL			(Type.IMAGE, "toolbar_install.png"),
	IMG_TOOLBAR_INSTALL_HOVER	(Type.IMAGE, "toolbar_install_hover.png"),
	IMG_TOOLBAR_ABOUT			(Type.IMAGE, "toolbar_about.png"),
	IMG_TOOLBAR_ABOUT_HOVER		(Type.IMAGE, "toolbar_about_hover.png"),
	IMG_TOOLBAR_SETTING		(Type.IMAGE, "toolbar_setting.png"),
	
	IMG_PERM_GROUP_PHONE_CALLS	(Type.IMAGE, "perm_group_phone_calls.png"),
	
	IMG_TOOLBAR_OPEN_ARROW		(Type.IMAGE, "down_on.png"),
	
	IMG_APP_ICON		(Type.IMAGE, "AppIcon.png"),
	IMG_QUESTION		(Type.IMAGE, "question.png"),
	IMG_WARNING			(Type.IMAGE, "warning.png"),
	IMG_SUCCESS			(Type.IMAGE, "Succes.png"),
	IMG_INSTALL_WAIT	(Type.IMAGE, "install_wait.gif"),
	IMG_LOADING			(Type.IMAGE, "loading.gif"),
	IMG_WAIT_BAR		(Type.IMAGE, "wait_bar.gif"),
	

	BIN_ADB_LNX			(Type.BIN, "adb"),
	BIN_ADB_WIN			(Type.BIN, "adb.exe"),
	BIN_APKTOOL_JAR		(Type.BIN, "apktool.jar");
	
	private enum Type {
		IMAGE,
		TEXT,
		BIN,
		ETC
	}

	private String value;
	private Type type;

	private static String lang = null;
	public static void setLanguage(String l) { lang = l; }
	public static String getLanguage() { return lang; }

	private Resource(Type type, String value)
	{
		this.type = type;
		this.value = value;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public String getPath()
	{
		if(type == Type.TEXT) return null;

		String subPath;
		switch(type){
		case IMAGE:
			return getClass().getResource("/icons/" + value).toString();
		case BIN:
			subPath = File.separator + "tool";
			break;
		case ETC: default:
			subPath = "";
			break;
		}
		return getUTF8Path() + subPath + File.separator + value;
	}
	
	public URL getURL()
	{
		if(type != Type.IMAGE) return null;
		return getClass().getResource("/icons/" + value);
	}
	
	public ImageIcon getImageIcon()
	{
		if(type != Type.IMAGE) return null;
		return new ImageIcon(getURL());
	}
	
	public ImageIcon getImageIcon(int width, int height)
	{
		if(type != Type.IMAGE) return null;
		ImageIcon tempImg = new ImageIcon(CoreApkTool.getScaledImage(new ImageIcon(getURL()),width,height));
		
		return tempImg;
	}
	
	public String getString()
	{
		if(type != Type.TEXT) return null;
		
		String id = getValue();
		String value = null;
		
		if(!id.matches("^@.*")) return id;
		id = id.replaceAll("^@(.*)", "$1");

		String value_path = getUTF8Path() + File.separator + "data" + File.separator;
		if(lang != null) {
			String ext_lang_value_path = value_path + "strings-" + lang + ".xml";
			
			if((new File(ext_lang_value_path)).exists()) {
				MyXPath xmlValue = new MyXPath(ext_lang_value_path);
				value = xmlValue.getNode("/resources/string[@name='" + id + "']").getTextContent();
			}
			
			if(value == null) {
				InputStream xml = getClass().getResourceAsStream("/values/strings-" + lang + ".xml");
				if(xml != null) {
					MyXPath xmlValue = new MyXPath(xml);
					value = xmlValue.getNode("/resources/string[@name='" + id + "']").getTextContent();
				}
			}
		}
		
		if(value == null) {
			String ext_lang_value_path = value_path + "strings.xml";
			if((new File(ext_lang_value_path)).exists()) {
				MyXPath xmlValue = new MyXPath(ext_lang_value_path);
				value = xmlValue.getNode("/resources/string[@name='" + id + "']").getTextContent();
			}
			
			if(value == null) {
				InputStream xml = getClass().getResourceAsStream("/values/strings.xml");
				if(xml != null) {
					MyXPath xmlValue = new MyXPath(xml);
					value = xmlValue.getNode("/resources/string[@name='" + id + "']").getTextContent();
				}
			}
		}

		return value;
	}

	private String getUTF8Path()
	{
		String resourcePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		resourcePath = (new File(resourcePath)).getParentFile().getPath();
		
		try {
			resourcePath = URLDecoder.decode(resourcePath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return resourcePath;
	}
}

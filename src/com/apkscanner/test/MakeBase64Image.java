package com.apkscanner.test;

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.xml.bind.DatatypeConverter;

import com.apkscanner.gui.util.ImageScaler;
import com.apkscanner.resource.Resource;
import com.apkscanner.util.Log;

public class MakeBase64Image {

    public static void main(String[] args) {
    	String iconsPath = "file://" + Resource.getUTF8Path() + File.separator + "res" + File.separator + "icons" + File.separator;
    	String[] sources = new String[] {
			"perm_group_accessibility_features.png",
			"perm_group_accounts.png",
			"perm_group_affects_battery.png",
			"perm_group_app_info.png",
			"perm_group_audio_settings.png",
			"perm_group_bluetooth.png",
			"perm_group_bookmarks.png",
			"perm_group_calendar.png",
			"perm_group_camera.png",
			"perm_group_contacts.png",
			"perm_group_device_alarms.png",
			"perm_group_display.png",
			"perm_group_location.png",
			"perm_group_messages.png",
			"perm_group_microphone.png",
			"perm_group_network.png",
			"perm_group_personal_info.png",
			"perm_group_phone_calls.png",
			"perm_group_screenlock.png",
			"perm_group_sensors.png",
			"perm_group_sensors1.png",
			"perm_group_shortrange_network.png",
			"perm_group_sms.png",
			"perm_group_social_info.png",
			"perm_group_status_bar.png",
			"perm_group_storage.png",
			"perm_group_sync_settings.png",
			"perm_group_system_clock.png",
			"perm_group_system_tools.png",
			"perm_group_unknown.png",
			"perm_group_user_dictionary.png",
			"perm_group_user_dictionary_write.png",
			"perm_group_voicemail.png",
			"perm_group_wallpaper.png",
			"perm_group_user_dictionary.png",
			"apk_file_icon.png",
			"resource_tab_tree_arsc.png",
			"resource_tab_tree_code.png",
			"resource_tab_tree_xml.gif"
    	};
    	
    	for(String s: sources) {
    		Log.e(s + ": " + makeBase64Image(s));
    	}
    	
    }
    
    private static String makeBase64Image(String path) {
		String base64 = "";
		ImageIcon myimageicon = null;

		String extension = path.replaceAll(".*/", "").replaceAll(".*\\.", "").toLowerCase();
		
		myimageicon = new ImageIcon(Resource.class.getResource("/icons/" + path));
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
}

package com.apkscanner;

import java.awt.EventQueue;

import javax.swing.JFrame;

import com.apkscanner.core.scanner.ApkScanner;
import com.apkscanner.gui.EasyMainUI;
import com.apkscanner.gui.MainUI;
import com.apkscanner.resource.Resource;
import com.apkscanner.util.Log;

public class UIController implements Runnable {
	public static final String APKSCANNER_GUI_APKSCANNER = "APKSCANNER";
	public static final String APKSCANNER_GUI_EASY_APKSCANNER = "EASY_APKSCANNER";

	private static UIController instance;

	private ApkScanner apkScanner;

//	MainUI mainUI = null;
//	EasyMainUI easymainUI = null;
	private JFrame mainframe = null;

	private UIController(ApkScanner apkScanner) {
		if(apkScanner == null) {
			apkScanner = ApkScanner.getInstance();
		}
		this.apkScanner = apkScanner;
	}

	public static UIController getInstance(ApkScanner apkScanner) {
		if(instance == null) {
			instance = new UIController(apkScanner);
		}
		return instance;
	}

	public static UIController getInstance() {
		return getInstance(null);
	}

	@Override
	public void run() {
		if(!EventQueue.isDispatchThread()) {
			EventQueue.invokeLater(this);
			return;
		}
		createAndShowGUI();
	}

	private void createAndShowGUI() {
		Log.i("start UIController");
		boolean isEasyGui = (boolean) Resource.PROP_USE_EASY_UI.getData();
		mainframe = new JFrame();
		Log.i("creat frame");
		if(	isEasyGui) {
			new EasyMainUI(apkScanner, mainframe);
		} else {
			new MainUI(apkScanner, mainframe);
		}

		mainframe.setVisible(true);

		if(!(boolean) Resource.PROP_SKIP_STARTUP_EASY_UI_DLG.getData()) {
			if(EasyMainUI.showDlgStartupEasyMode(mainframe)) {
				changeGui(isEasyGui ? APKSCANNER_GUI_APKSCANNER : APKSCANNER_GUI_EASY_APKSCANNER);
			}
		}
	}

	public void changeGui(final String state) {
		final boolean isEasyGui = APKSCANNER_GUI_EASY_APKSCANNER.equals(state);

		final String apkPath = apkScanner.getApkInfo() != null ? apkScanner.getApkInfo().filePath : null;
		if(!isEasyGui) {
			apkScanner = ApkScanner.getInstance(ApkScanner.APKSCANNER_TYPE_AAPT);
		} else {
			apkScanner = ApkScanner.getInstance(ApkScanner.APKSCANNER_TYPE_AAPTLIGHT);
		}

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				mainframe.setVisible(false);
				mainframe.getContentPane().removeAll();
				if(!isEasyGui) {
					new MainUI(apkScanner, mainframe);
				} else {
					new EasyMainUI(apkScanner, mainframe);
				}
				mainframe.setVisible(true);
				if(apkPath != null) {
					Thread thread = new Thread(new Runnable() {
						public void run() {
							apkScanner.openApk(apkPath);
						}
					});
					thread.setPriority(Thread.NORM_PRIORITY);
					thread.start();
				}
			}
		});
	}

	public static void changeToMainGui() {
		getInstance().changeGui(APKSCANNER_GUI_APKSCANNER);
	}

	public static void changeToEasyGui() {
		getInstance().changeGui(APKSCANNER_GUI_EASY_APKSCANNER);
	}
}

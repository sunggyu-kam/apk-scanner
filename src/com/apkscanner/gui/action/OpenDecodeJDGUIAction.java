package com.apkscanner.gui.action;

import java.awt.Component;
import java.awt.Window;

@SuppressWarnings("serial")
public class OpenDecodeJDGUIAction extends OpenDecompilerAction
{
	public static final String ACTION_COMMAND = "ACT_CMD_OPEN_DECOMPILER_JDGUI";

	public OpenDecodeJDGUIAction(ActionEventHandler h) { super(h); }

	@Override
	protected void evtOpenDecompiler(final Window owner, final Component comp) {
		if(!hasCode(owner)) return;
		launchJdGui(owner, comp);
	}
}
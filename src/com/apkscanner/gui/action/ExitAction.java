package com.apkscanner.gui.action;

import java.awt.Window;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class ExitAction extends AbstractUIAction
{
	public static final String ACTION_COMMAND = "ACT_CMD_EXIT";

	public ExitAction(ActionEventHandler h) { super(h); }

	@Override
	public void actionPerformed(ActionEvent e) {
		Window owner = getWindow(e);
		if(owner != null) owner.dispose();
	}
}

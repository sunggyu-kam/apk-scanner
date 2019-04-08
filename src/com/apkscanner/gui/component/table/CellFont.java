package com.apkscanner.gui.component.table;

import java.awt.Font;

public interface CellFont extends CellAttributeMap {
	public Font getFont(int row, int column);
	public void setFont(Font font, int row, int column);
	public void setFont(Font font, int[] rows, int[] columns);
}

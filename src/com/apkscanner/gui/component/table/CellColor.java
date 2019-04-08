package com.apkscanner.gui.component.table;

import java.awt.Color;

public interface CellColor extends CellAttributeMap {
	public Color getColor(int row, int column);
	public void setColor(Color color, int row, int column);
	public void setColor(Color color, int[] rows, int[] columns);
}

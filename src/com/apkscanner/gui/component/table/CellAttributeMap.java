package com.apkscanner.gui.component.table;

public interface CellAttributeMap {
	public Object[][] getTableMap();
	public void setSize(int rows, int columns);
	public void initValues();
	public void initRowValues(int row);
	public void initColumnValues(int column);
}

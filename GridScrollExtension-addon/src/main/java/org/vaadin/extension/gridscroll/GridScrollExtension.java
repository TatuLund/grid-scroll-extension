package org.vaadin.extension.gridscroll;

import java.util.List;

import org.vaadin.extension.gridscroll.shared.GridScrollExtensionClientRPC;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionServerRPC;

import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;

// This is the server-side UI component that provides public API 
// for MyComponent
@SuppressWarnings("serial")
public class GridScrollExtension extends AbstractExtension {
	private int lastXPosition;
	private int lastYPosition;
	private double[] columnWidths;
	private Grid<?> grid;
	
	/**
	 * Constructor method for the extension
	 * 
	 * @param grid Grid to be extended
	 */	
	public GridScrollExtension(Grid grid) {
		this.extend(grid);
		this.grid = grid;
		registerRpc(new GridScrollExtensionServerRPC() {
			@Override
			public void ping() {
			}

			@Override
			public void reportPosition(int Xposition, int Yposition) {
				if(Xposition == -1 || Yposition == -1) {
					restoreScrollPosition();
				} else {
					lastXPosition = Xposition;
					lastYPosition = Yposition;
				}
			}

			@Override
			public void reportColumns(double[] widths) {
				columnWidths = widths;				
			}
		});
	}
	
	/**
	 * Get actual width of the column by column reference
	 * Note: There is small delay after Grid has been attached before real widths are available
	 * 
	 * @param column The column reference
	 * @return Actual width of the column in pixels double value
	 */
	public double getColumnWidth(Column<?,?> column) {
		double width = 0;
		int i = 0;
		if (columnWidths == null) return -1.0;
		for (Column<?, ?> col : grid.getColumns()) {
			if (col == column) width = columnWidths[i];
			i++;
		}
		return width;		
	}
	
	/**
	 * Get actual width of the column by columnId 
	 * Note: There is small delay after Grid has been attached before real widths are available

	 * @param columnId Id string / property name of the column
	 * @return Actual width of the column in pixels double value
	 */
	public double getColumnWidth(String columnId) {
		double width = 0;
		int i = 0;
		if (columnWidths == null) return -1.0;
		for (Column<?, ?> column : grid.getColumns()) {
			if (column.getId().equals(columnId)) width = columnWidths[i];
			i++;
		}
		return width;
	}

	/**
	 * Get actual width of the column by index 
	 * Note: There is small delay after Grid has been attached before real widths are available

	 * @param i Index of the column
	 * @return Actual width of the column in pixels double value
	 */
	public double getColumnWidth(int i) {
		if (columnWidths == null) return -1.0;
		if (i > grid.getColumns().size()) return -1.0;
		return columnWidths[i];
	}

	/**
	 * Make Grid to scroll to the last known scroll position
	 * 
	 */
	public void restoreScrollPosition() {
		setScrollPosition(lastXPosition, lastYPosition);
	}

	/**
	 * Get X scroll position of the Grid
	 * 
	 * @return Scroll position X coordinate in pixels as int
	 */	
	public int getLastXPosition() {
		return lastXPosition;
	}

	/**
	 * Get Y scroll position of the Grid
	 * 
	 * @return Scroll position Y coordinate in pixels as int
	 */
	public int getLastYPosition() {
		return lastYPosition;
	}

	/**
	 * Set new scroll position in pixels and scroll grid to that position
	 * 
	 * @param Xposition The new y scroll position
	 * @param Yposition The new y scroll position
	 */
	public void setScrollPosition(int Xposition, int Yposition) {
		getClientRPC().setScrollPosition(Xposition, Yposition);
		lastXPosition = Xposition;
		lastYPosition = Yposition;
	}

	private GridScrollExtensionClientRPC getClientRPC() {
		return getRpcProxy(GridScrollExtensionClientRPC.class);
	}
}

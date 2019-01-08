package org.vaadin.extension.gridscroll.shared;

/***
 * Definitions for column resize compensation modes
 * 
 * @since 2.3.0
 * 
 * @author Tatu Lund
 */
public enum ColumnResizeCompensationMode {

    /**
     * When column resize compensation mode is set to NONE,
     * Grid's width or columns are not adjusted upon column resize. 
     */
	NONE,
	
    /**
     * When column resize compensation mode is set to RESIZE_GRID,
     * Grid's width is adjusted according to widths of the column. 
     */
	RESIZE_GRID,
	
    /**
     * When column resize compensation mode is set to RESIZE_COLUMN,
     * Grid's last column is adjusted occupy the remaining space
     * available (if there is any).
     *  
	 * Note: ColumnResizeCompensationMode.RESIZE_COLUM takes effect also when
	 * Grid is being resized.
	 * 
	 * Note: When ColumnResizeCompensationMode.RESIZE_COLUM is used, the {@link com.vaadin.ui.Grid.Column#setMaximumWidth(double)} 
	 * cannot be used with the last column
	 * 
     */
	RESIZE_COLUMN
	
}

package org.vaadin.extension.gridscroll;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;

/**
 * The GridColumnsResizedEvent when Grid's columns are being resized
 * 
 * @since 2.2.0
 * 
 * @see GridScrollExtension#addGridColumnsResizedListener(org.vaadin.extension.gridscroll.GridScrollExtension.GridColumnsResizedListener)
 *
 * @param <T> Bean type of the Grid
 * 
 * @author Tatu Lund
 */
@SuppressWarnings("serial")
public class GridColumnsResizedEvent<T> extends CustomComponent.Event {
	private int column;
	
	public GridColumnsResizedEvent(Grid<T> source, int column) {
		super(source);
	}

	/**
	 * Return the index of the column that was adjusted by the user
	 * 
	 * @since 2.3.5
	 * 
	 * @return Index of the column, -1 if there was no user originated resizing
	 */
	public int getColumn() {
		return column;
	}

}

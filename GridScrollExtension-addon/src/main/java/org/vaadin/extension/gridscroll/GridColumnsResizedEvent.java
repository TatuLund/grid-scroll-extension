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

	public GridColumnsResizedEvent(Grid<T> source) {
		super(source);
	}

}

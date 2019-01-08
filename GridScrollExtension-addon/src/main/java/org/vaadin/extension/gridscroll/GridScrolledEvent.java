package org.vaadin.extension.gridscroll;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;

/**
 * The GridScrolledEvent event when Grid scroll position changes
 * 
 * @since 2.2.0
 * 
 * @see GridScrollExtension#addGridScrolledListener(org.vaadin.extension.gridscroll.GridScrollExtension.GridScrolledListener)
 * 
 * @param <T> Bean type of the Grid
 * 
 * @author Tatu Lund
 */
@SuppressWarnings("serial")
public class GridScrolledEvent<T> extends CustomComponent.Event {

	public GridScrolledEvent(Grid<T> source) {
		super(source);
	}

}

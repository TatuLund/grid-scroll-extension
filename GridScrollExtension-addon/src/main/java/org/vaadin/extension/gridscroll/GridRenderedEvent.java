package org.vaadin.extension.gridscroll;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;

/**
 * The GridRenderedEvent event is fired once after Grid's initial column width calculation is complete
 *
 * @see GridScrollExtension#addGridRenderedListener(org.vaadin.extension.gridscroll.GridScrollExtension.GridRenderedListener)
 *
 * @param <T> Bean type of the Grid
 * 
 * @author Tatu Lund
 */
@SuppressWarnings("serial")
public class GridRenderedEvent<T> extends CustomComponent.Event {

	public GridRenderedEvent(Grid<T> source) {
		super(source);
	}

}

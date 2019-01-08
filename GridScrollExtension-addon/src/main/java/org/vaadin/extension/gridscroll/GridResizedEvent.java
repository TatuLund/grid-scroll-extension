package org.vaadin.extension.gridscroll;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;

/**
 * The GridResizedEvent event is fired everytime Grid size has been changed 
 * when {@link GridScrollExtension#setColumnResizeComponesationMode(org.vaadin.extension.gridscroll.shared.ColumnResizeCompensationMode)}
 * with {@link org.vaadin.extension.gridscroll.shared.ColumnResizeCompensationMode#RESIZE_GRID} has been applied.
 * 
 * @since 2.2.0
 * 
 * @see GridScrollExtension#addGridResizedListener(org.vaadin.extension.gridscroll.GridScrollExtension.GridResizedListener)
 *   
 * @param <T> Bean type of the Grid  
 * 
 * @author Tatu Lund
 */
@SuppressWarnings("serial")
public class GridResizedEvent<T> extends CustomComponent.Event {

	public GridResizedEvent(Grid<T> source) {
		super(source);
	}

}

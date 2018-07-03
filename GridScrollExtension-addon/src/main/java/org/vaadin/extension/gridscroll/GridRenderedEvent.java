package org.vaadin.extension.gridscroll;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;

/**
 * The GridRenderedEvent event is fired once after Grid's initial column width calculation is complete
 * 
 * @author Tatu Lund
 */
@SuppressWarnings("serial")
public class GridRenderedEvent extends CustomComponent.Event {

	public GridRenderedEvent(Component source) {
		super(source);
	}

}

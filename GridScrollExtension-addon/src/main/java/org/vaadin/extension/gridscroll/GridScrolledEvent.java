package org.vaadin.extension.gridscroll;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;

/**
 * The GridScrolledEvent event when Grid scroll position changes
 * 
 * @author Tatu Lund
 */
@SuppressWarnings("serial")
public class GridScrolledEvent extends CustomComponent.Event {

	public GridScrolledEvent(Component source) {
		super(source);
	}

}

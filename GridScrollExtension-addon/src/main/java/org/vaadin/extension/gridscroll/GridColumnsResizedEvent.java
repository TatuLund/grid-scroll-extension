package org.vaadin.extension.gridscroll;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;

/**
 * The GridColumnsResizedEvent when Grid's columns are being resized
 * 
 * @author Tatu Lund
 */
@SuppressWarnings("serial")
public class GridColumnsResizedEvent extends CustomComponent.Event {

	public GridColumnsResizedEvent(Component source) {
		super(source);
	}

}

package org.vaadin.extension.gridscroll;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;

/**
 * The GridResizedEvent event is fired everytime Grid size has been changed 
 * when GridScrollExtension.setAutoResizeWidth(true) has been applied
 * 
 * @author Tatu Lund
 */
@SuppressWarnings("serial")
public class GridResizedEvent extends CustomComponent.Event {

	public GridResizedEvent(Component source) {
		super(source);
	}

}

package org.vaadin.extension.gridscroll;

import org.vaadin.extension.gridscroll.shared.GridScrollExtensionClientRPC;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionServerRPC;

import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.Grid;

// This is the server-side UI component that provides public API 
// for MyComponent
@SuppressWarnings("serial")
public class GridScrollExtension extends AbstractExtension {
	private int lastXPosition;
	private int lastYPosition;

	/**
	 * Constructor method for the extension
	 * 
	 * @param grid Grid to be extended
	 */	
	public GridScrollExtension(Grid grid) {
		this.extend(grid);
		registerRpc(new GridScrollExtensionServerRPC() {
			@Override
			public void ping() {
			}

			@Override
			public void reportPosition(int Xposition, int Yposition) {
				if(Xposition == -1 || Yposition == -1) {
					restoreScrollPosition();
				} else {
					lastXPosition = Xposition;
					lastYPosition = Yposition;
				}
			}
		});
	}

	/**
	 * Make Grid to scroll to the last known scroll position
	 * 
	 */
	public void restoreScrollPosition() {
		setScrollPosition(lastXPosition, lastYPosition);
	}

	/**
	 * Get X scroll position of the Grid
	 * 
	 * @return Scroll position X coordinate in pixels as int
	 */	
	public int getLastXPosition() {
		return lastXPosition;
	}

	/**
	 * Get Y scroll position of the Grid
	 * 
	 * @return Scroll position Y coordinate in pixels as int
	 */
	public int getLastYPosition() {
		return lastYPosition;
	}

	/**
	 * Set new scroll position in pixels and scroll grid to that position
	 * 
	 * @param Xposition The new y scroll position
	 * @param Yposition The new y scroll position
	 */
	public void setScrollPosition(int Xposition, int Yposition) {
		getClientRPC().setScrollPosition(Xposition, Yposition);
		lastXPosition = Xposition;
		lastYPosition = Yposition;
	}

	private GridScrollExtensionClientRPC getClientRPC() {
		return getRpcProxy(GridScrollExtensionClientRPC.class);
	}
}

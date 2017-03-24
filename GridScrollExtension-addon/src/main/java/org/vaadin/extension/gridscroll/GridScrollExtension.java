package org.vaadin.extension.gridscroll;

import org.vaadin.extension.gridscroll.shared.GridScrollExtensionClientRPC;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionServerRPC;

import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;

// This is the server-side UI component that provides public API 
// for MyComponent
@SuppressWarnings("serial")
public class GridScrollExtension extends AbstractExtension {
	private int lastXPosition;
	private int lastYPosition;

	public GridScrollExtension(Grid grid) {
		this.extend(grid);
		registerRpc(new GridScrollExtensionServerRPC() {
			@Override
			public void ping() {
			}

			@Override
			public void reportPosition(int Xposition, int Yposition) {
				System.out.println("position report received");
				if(Xposition == -1 || Yposition == -1) {
					restoreScrollPosition();
				} else {
					System.out.println("New position: " + Xposition + ", "
							+ Yposition);
					lastXPosition = Xposition;
					lastYPosition = Yposition;
				}
			}
		});
	}

	public void restoreScrollPosition() {
		System.out.println("Restore scroll position " + lastXPosition + ", " + lastYPosition);
		setScrollPosition(lastXPosition, lastYPosition);
	}

	public int getLastXPosition() {
		return lastXPosition;
	}

	public int getLastYPosition() {
		return lastYPosition;
	}

	public void setScrollPosition(int Xposition, int Yposition) {
		getClientRPC().setScrollPosition(Xposition, Yposition);
	}

	private GridScrollExtensionClientRPC getClientRPC() {
		return getRpcProxy(GridScrollExtensionClientRPC.class);
	}
}

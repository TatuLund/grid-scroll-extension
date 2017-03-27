package org.vaadin.extension.gridscroll.client;

import org.vaadin.extension.gridscroll.GridScrollExtension;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionClientRPC;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionServerRPC;

import com.google.gwt.user.client.Timer;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.ui.Connect;

@SuppressWarnings("serial")
@Connect(GridScrollExtension.class)
public class GridScrollExtensionConnector extends AbstractExtensionConnector {

	private Grid<?> grid;
	
	private double xscroll = -1;
	private double yscroll = -1;
	Timer t = null;
	
	@Override
	protected void extend(ServerConnector target) {
		grid = (Grid<?>)((ComponentConnector)target).getWidget();
		
		registerRpc(GridScrollExtensionClientRPC.class,
				new GridScrollExtensionClientRPC() {

					@Override
					public void setScrollPosition(int x, int y) {
						grid.setScrollLeft(x);
						grid.setScrollTop(y);
						xscroll = x;
						yscroll = y;
					}
		});
		
		t = new Timer() {
			@Override
			public void run() {
				if(xscroll == -1 || yscroll == -1) {
					getServerRPC().reportPosition(-1, -1);
					return;
				}
				
				boolean send = false;
				double y = grid.getScrollTop();
				if(y != yscroll) {
					send = true;
					yscroll = y;
				}
				double x = grid.getScrollLeft();
				if(x != xscroll) {
					send = true;
					xscroll = x;
				}
				if(send) {
					getServerRPC().reportPosition((int)(xscroll + .5),(int)(yscroll + .5));
				}
			}
		};
		t.scheduleRepeating(250);
	}
	
	@Override
	public void onUnregister() {
		super.onUnregister();
		t.cancel();
	}
	
	private GridScrollExtensionServerRPC getServerRPC() {
		return getRpcProxy(GridScrollExtensionServerRPC.class);
	}
	
	
	
}

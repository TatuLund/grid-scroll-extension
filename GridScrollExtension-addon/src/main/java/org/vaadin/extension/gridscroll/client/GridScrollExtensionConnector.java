package org.vaadin.extension.gridscroll.client;

import org.vaadin.extension.gridscroll.GridScrollExtension;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionClientRPC;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionServerRPC;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.widget.grid.events.ColumnResizeEvent;
import com.vaadin.client.widget.grid.events.ColumnResizeHandler;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.ui.Connect;

@SuppressWarnings("serial")
@Connect(GridScrollExtension.class)
public class GridScrollExtensionConnector extends AbstractExtensionConnector {

	private Grid<?> grid;
	
	private double xscroll = -1;
	private double yscroll = -1;
	Timer t = null;
	
	private double[] getColumnWidths() {
		int columns = grid.getColumnCount();
		double[] widths = new double[columns]; 
		for (int i=0;i<columns;i++) widths[i] = grid.getColumns().get(i).getWidthActual();
		return widths;
	}
	
	@Override
	protected void extend(ServerConnector target) {
		grid = (Grid<?>)((ComponentConnector)target).getWidget();

		grid.addAttachHandler(new AttachEvent.Handler() {

			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
						@Override
						public boolean execute() {
							double[] widths = getColumnWidths();
							getServerRPC().reportColumns(widths);
							return false;	
						}
					}, 1000);
				}			
			}
		});
		
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
		
		grid.addColumnResizeHandler(new ColumnResizeHandler() {

			@Override
			public void onColumnResize(ColumnResizeEvent event) {
				double[] widths = getColumnWidths();
				getServerRPC().reportColumns(widths);
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

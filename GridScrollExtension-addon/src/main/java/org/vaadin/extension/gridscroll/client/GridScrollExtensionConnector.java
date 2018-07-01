package org.vaadin.extension.gridscroll.client;

import org.vaadin.extension.gridscroll.GridScrollExtension;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionClientRPC;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionServerRPC;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionState;

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
	private int width = -1;
	private int heigth = -1;
	Timer t = null;
	
	private double[] getColumnWidths() {
		int columns = grid.getVisibleColumns().size();
		double[] widths = new double[columns]; 
		for (int i=0;i<columns;i++) widths[i] = grid.getVisibleColumns().get(i).getWidthActual();
		return widths;
	}
	
	private boolean hasWidths(double[] widths) {
		boolean hasWidths = true;
		for (int i=0;i<grid.getVisibleColumns().size();i++) {
			if (!(widths[i] > 0)) hasWidths = false;
		}
		return hasWidths;
	}
	
	@Override
	protected void extend(ServerConnector target) {
		grid = (Grid<?>)((ComponentConnector)target).getWidget();

		grid.addAttachHandler(new AttachEvent.Handler() {

			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					getServerRPC().reportSize(grid.getOffsetWidth(), grid.getOffsetHeight());
					Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
						@Override
						public boolean execute() {
							double[] widths = getColumnWidths();
							if (hasWidths(widths)) {
								getServerRPC().reportColumns(widths);							
								getServerRPC().gridInitialColumnWidthsCalculated();
								return false;
							}
							else return true;
						}
					}, 100);
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
				if (getState().autoResizeWidth) {
					Double width = 0d;
					for (int i=0;i<widths.length;i++) width = width + widths[i];
					grid.setWidth(width.intValue()+16.5+"px");
					getServerRPC().reportSize(grid.getOffsetWidth(), grid.getOffsetHeight());
				}
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
				
				send = false;
				int w = grid.getOffsetWidth();
				if (w != width) {
					send = true;
					width = w;
				}
				int h = grid.getOffsetHeight();
				if (h != heigth) {
					send = true;
					heigth = h;
				}
				if(send) {
					getServerRPC().reportSize(width, heigth);
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
	
	@Override
    public GridScrollExtensionState getState() {
        return ((GridScrollExtensionState) super.getState());
    }
	
	
}

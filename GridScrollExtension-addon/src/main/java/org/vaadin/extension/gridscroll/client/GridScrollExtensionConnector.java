package org.vaadin.extension.gridscroll.client;

import org.vaadin.extension.gridscroll.GridScrollExtension;
import org.vaadin.extension.gridscroll.shared.ColumnResizeCompensationMode;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionClientRPC;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionServerRPC;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionState;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.BrowserInfo;
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
	
	// Check if widths has been calculated
	private boolean hasWidths(double[] widths) {
		boolean hasWidths = true;
		for (int i=0;i<grid.getVisibleColumns().size();i++) {
			if (!(widths[i] > 0)) hasWidths = false;
		}
		return hasWidths;
	}

	// Sets the width of Grid to be sum of widths
	private void adjustGridWidth(double[] widths) {
		Double width = 0d;
		for (int i=0;i<widths.length;i++) width = width + widths[i];
		// Add the scroll bar width if it exists 
		Double totalWidth = width + 0.5;
		Double scrollerWidth = getVerticalScrollBarWidth();
		if (scrollerWidth > 0.0) totalWidth = totalWidth + scrollerWidth; 
		grid.setWidth(totalWidth.intValue()+"px");
	}

	// Sets the width of the last Column of the Grid to fit remaining space of Grid
	// provided that there is space left
	private void adjustLastColumnWidth(double[] widths) {
		Double width = 0d;
		for (int i=0;i<widths.length-1;i++) width = width + widths[i];
		// Add the scroll bar width if it exists 
		Double totalWidth = width + 0.5;
		Double scrollerWidth = getVerticalScrollBarWidth();
		if (scrollerWidth > 0.0) totalWidth = totalWidth + scrollerWidth; 
		Double gridWidth = (double) grid.getOffsetWidth();
		if (totalWidth < gridWidth) {
			Double targetWidth = gridWidth - totalWidth; 
			grid.getVisibleColumns().get(grid.getVisibleColumns().size()-1).setWidth(targetWidth);
		}
	}
	
	// Return -1.0 if Grid has no vertical scroll bar otherwise its width
	private double getVerticalScrollBarWidth() {
		for (Element e : getGridParts("div")) {
			if (e.getClassName().contains("v-grid-scroller-vertical")) {
				if (BrowserInfo.get().isIE11() || BrowserInfo.get().isEdge()) { 
					return e.getClientWidth();
				} else {
					return e.getOffsetWidth();					
				}
			}
		}
		return -1.0;
	}
	
	// Get elements in Grid by tag name
	private Element[] getGridParts(String elem) {
		NodeList<Element> elems = grid.getElement().getElementsByTagName(elem);
		Element[] ary = new Element[elems.getLength()];
		for (int i = 0; i < ary.length; ++i) {
			ary[i] = elems.getItem(i);
		}
		return ary;
	}
	
	@Override
	protected void extend(ServerConnector target) {
		grid = (Grid<?>)((ComponentConnector)target).getWidget();

		grid.addAttachHandler(event -> {
			if (event.isAttached()) {
				getServerRPC().reportSize(grid.getOffsetWidth(), grid.getOffsetHeight());
				Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
					@Override
					public boolean execute() {
						double[] widths = getColumnWidths();
						if (hasWidths(widths)) {
							getServerRPC().reportColumns(widths);							
							getServerRPC().gridInitialColumnWidthsCalculated();
							if (getState().compensationMode == ColumnResizeCompensationMode.RESIZE_GRID) {
								adjustGridWidth(widths);
							}
							return false;
						}
						else return true;
					}
				}, 100);
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

					@Override
					public void recalculateGridWidth() {
						AnimationCallback adjustCallback = new AnimationCallback() {
				            @Override
				            public void execute(double timestamp) {
				            	double[] widths = getColumnWidths();
								adjustGridWidth(widths);
								getServerRPC().reportColumns(widths);
				            }
						};
						AnimationScheduler.get().requestAnimationFrame(adjustCallback);
					}

					@Override
					public void adjustLastColumn() {
						AnimationCallback adjustCallback = new AnimationCallback() {
				            @Override
				            public void execute(double timestamp) {
				            	double[] widths = getColumnWidths();
								adjustLastColumnWidth(widths);
				            }
						};
						AnimationScheduler.get().requestAnimationFrame(adjustCallback);
					}
		});
		
		// For some odd reason sorting resets Grid size, which may be bug in Grid, this is workaround
		grid.addSortHandler(event -> {
			AnimationCallback adjustCallback = new AnimationCallback() {
	            @Override
	            public void execute(double timestamp) {
	            	double[] widths = getColumnWidths();
					if (getState().compensationMode == ColumnResizeCompensationMode.RESIZE_GRID) adjustGridWidth(widths);
					else if (getState().compensationMode == ColumnResizeCompensationMode.RESIZE_COLUMN) adjustLastColumnWidth(widths);
	            }
			};
			if (getState().compensationMode != ColumnResizeCompensationMode.NONE) {
				AnimationScheduler.get().requestAnimationFrame(adjustCallback);
			}
		});
		
		grid.addColumnResizeHandler(event -> {
			double[] widths = getColumnWidths();
			getServerRPC().reportColumns(widths);
			if (getState().compensationMode == ColumnResizeCompensationMode.RESIZE_GRID) {
				getServerRPC().reportSize(grid.getOffsetWidth(), grid.getOffsetHeight());
				adjustGridWidth(widths);
			} else if (getState().compensationMode == ColumnResizeCompensationMode.RESIZE_COLUMN) {
				adjustLastColumnWidth(widths);				
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

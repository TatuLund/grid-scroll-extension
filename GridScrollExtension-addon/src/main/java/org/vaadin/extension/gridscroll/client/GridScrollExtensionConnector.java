package org.vaadin.extension.gridscroll.client;

import org.vaadin.extension.gridscroll.GridScrollExtension;
import org.vaadin.extension.gridscroll.shared.ColumnResizeCompensationMode;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionClientRPC;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionServerRPC;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionState;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.widgets.Grid;
import com.vaadin.client.widgets.Grid.Column;
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
	int visibleColIndex = -1;
	
	private double[] getColumnWidths() {
		int columns = grid.getVisibleColumns().size();
		double[] widths = new double[columns]; 
		for (int i=0;i<columns;i++) widths[i] = grid.getVisibleColumns().get(i).getWidthActual();
		return widths;
	}
	
	private double[] getColumnWidthsCapped() {
		int columns = grid.getVisibleColumns().size();
		double[] widths = new double[columns]; 
		for (int i=0;i<columns;i++) {
			widths[i] = grid.getVisibleColumns().get(i).getWidthActual();
			double maxWidth = grid.getVisibleColumns().get(i).getMaximumWidth();
			double minWidth = grid.getVisibleColumns().get(i).getMinimumWidth();
			if (!getState().widthGuardDisabled && widths[i] > maxWidth && maxWidth > 0) {
				widths[i] =  maxWidth;
				grid.getVisibleColumns().get(i).setWidth(maxWidth);
			} else if (!getState().widthGuardDisabled && widths[i] < minWidth && minWidth > 0) {
				widths[i] =  minWidth;
				grid.getVisibleColumns().get(i).setWidth(minWidth);
			}
		}
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
	private double adjustLastColumnWidth(double[] widths) {
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
			return targetWidth;
		} else {
			return -1.0;
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
							getServerRPC().reportColumns(widths,-1);							
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
								getServerRPC().reportColumns(widths,-1);
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
		
		grid.addColumnVisibilityChangeHandler(event -> {
			Column<?, ?> column = event.getColumn();
			updateGridDueColumnChange(column);			
		});
		
		grid.addColumnResizeHandler(event -> {
			Column<?, ?> column = event.getColumn();
			updateGridDueColumnChange(column);
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
					if (getState().compensationMode == ColumnResizeCompensationMode.RESIZE_COLUMN) {
						Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {					
							@Override
							public void execute() {
				    			double[] widths = getColumnWidthsCapped();
								adjustLastColumnWidth(widths);
								getServerRPC().reportColumns(widths,-1);						
							}
						});
					}
				}
			}
		};
		t.scheduleRepeating(250);
	}

	private void updateGridDueColumnChange(Column<?, ?> column) {
		int i = 0;
		visibleColIndex = -1;
		for (Column<?, ?> col : grid.getVisibleColumns()) {
			if (col == column) { 
				visibleColIndex = i;
				break;
			}
			i++;
		}
		if (getState().compensationMode == ColumnResizeCompensationMode.RESIZE_GRID) {
			Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {					
				@Override
				public void execute() {
					double[] widths = getColumnWidthsCapped();
					getServerRPC().reportSize(grid.getOffsetWidth(), grid.getOffsetHeight());
					adjustGridWidth(widths);
					getServerRPC().reportColumns(widths,visibleColIndex);
				}
			});
		} else if (getState().compensationMode == ColumnResizeCompensationMode.RESIZE_COLUMN) {
			Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {					
				@Override
				public void execute() {
					double[] widths = getColumnWidthsCapped();
					double adjustedWidth = adjustLastColumnWidth(widths);
					if (visibleColIndex == grid.getVisibleColumns().size()-1) widths[visibleColIndex] = adjustedWidth;
					getServerRPC().reportColumns(widths,visibleColIndex);						
				}
			});
		}
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

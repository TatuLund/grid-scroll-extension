package org.vaadin.extension.gridscroll;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.vaadin.extension.gridscroll.shared.GridScrollExtensionClientRPC;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionServerRPC;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionState;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.server.AbstractExtension;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.util.ReflectTools;

// This is the server-side UI component that provides public API 
// for MyComponent
@SuppressWarnings("serial")
public class GridScrollExtension extends AbstractExtension {
	private int lastXPosition;
	private int lastYPosition;
	private double[] columnWidths;
	private Grid<?> grid;
	private int lastWidth;
	private int lastHeight;
	
	public interface GridScrolledListener extends ConnectorEventListener {
		Method GRID_SCROLLED_METHOD = ReflectTools.findMethod(
				GridScrolledListener.class, "gridScrolled", GridScrolledEvent.class);
		public void gridScrolled(GridScrolledEvent event);
	}
	
	public interface GridRenderedListener extends ConnectorEventListener {
		Method GRID_RENDERED_METHOD = ReflectTools.findMethod(
				GridRenderedListener.class, "gridRendered", GridRenderedEvent.class);
		public void gridRendered(GridRenderedEvent event);
	}
	
	public interface GridResizedListener extends ConnectorEventListener {
		Method GRID_RESIZED_METHOD = ReflectTools.findMethod(
				GridResizedListener.class, "gridResized", GridResizedEvent.class);
		public void gridResized(GridResizedEvent event);
	}
	
	public interface GridColumnsResizedListener extends ConnectorEventListener {
		Method GRID_COLUMNS_RESIZED_METHOD = ReflectTools.findMethod(
				GridColumnsResizedListener.class, "gridColumnsResized", GridColumnsResizedEvent.class);
		public void gridColumnsResized(GridColumnsResizedEvent event);
	}
	
	/**
	 * Constructor method for the extension
	 * 
	 * @param grid Grid to be extended
	 */	
	public GridScrollExtension(Grid grid) {
		this.extend(grid);
		this.grid = grid;
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
					fireEvent(new GridScrolledEvent(grid));
				}
			}

			@Override
			public void reportColumns(double[] widths) {
				columnWidths = widths;				
				fireEvent(new GridColumnsResizedEvent(grid));
			}

			@Override
			public void reportSize(int width, int height) {
				lastWidth = width;
				lastHeight = height;
				fireEvent(new GridResizedEvent(grid));
			}

			@Override
			public void gridInitialColumnWidthsCalculated() {
				fireEvent(new GridRenderedEvent(grid)); 
			}
		});
	}
	
	
	/**
	 * Add a new GridRenderedListener
	 * The GridRenderedEvent event is fired once after Grid's initial column width calculation is complete
	 * 
	 * @param listener A GridRenderedListener to be added
	 */
	public Registration addGridRenderedListener(GridRenderedListener listener) {
		return addListener(GridRenderedEvent.class, listener, GridRenderedListener.GRID_RENDERED_METHOD);
	}
	
	/**
	 * Add a new GridResizedListener
	 * The GridResizedEvent event is fired every time Grid size has been changed 
	 * when GridScrollExtension.setAutoResizeWidth(true) has been applied
	 * 
	 * @param listener A GridResizedListener to be added
	 */
	public Registration addGridResizedListener(GridResizedListener listener) {
		return addListener(GridResizedEvent.class, listener, GridResizedListener.GRID_RESIZED_METHOD);
	}

	/**
	 * Add a new GridColumnsResizedListener
	 * The GridColumnsResizedEvent event is fired every time Grid column sizes has been changed
	 * 
	 * Note, there is similar event in Grid, but that is fired before you can fetch real column widths, this
	 * event is fired after widths are available, hence you can get correct widths
	 * 
	 * @param listener A GridColumnsResizedListener to be added
	 */
	public Registration addGridColumnsResizedListener(GridColumnsResizedListener listener) {
		return addListener(GridColumnsResizedEvent.class, listener, GridColumnsResizedListener.GRID_COLUMNS_RESIZED_METHOD);
	}
	
	/**
	 * Add a new GridScrolledListener
	 * The GridScrolledEvent event is fired when Grid scroll position changes
	 *  
	 * @param listener A GridScrolledListener to be added
	 */
	public Registration addGridScrolledListener(GridScrolledListener listener) {
		return addListener(GridScrolledEvent.class, listener, GridScrolledListener.GRID_SCROLLED_METHOD);
	}
	
	/**
	 * Get actual width of the column by column reference
	 * Note: There is small delay after Grid has been attached before real widths are available
	 * 
	 * @param column The column reference
	 * @return Actual width of the column in pixels double value
	 */
	public double getColumnWidth(Column<?,?> column, boolean wait) {
		double width = 0;
		int i = 0;
		if (!wait && columnWidths == null) return -1.0;
		else {
			do {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (columnWidths == null);
					
		}
		for (Column<?, ?> col : grid.getColumns()) {
			if (col == column) width = columnWidths[i];
			i++;
		}
		return width;		
	}
	
	/**
	 * Get actual width of the column by columnId 
	 * Note: There is small delay after Grid has been attached before real widths are available

	 * @param columnId Id string / property name of the column
	 * @return Actual width of the column in pixels double value
	 */
	public double getColumnWidth(String columnId) {
		double width = 0;
		int i = 0;
		if (columnWidths == null) return -1.0;
		for (Column<?, ?> column : grid.getColumns()) {
			if (column.getId().equals(columnId)) width = columnWidths[i];
			i++;
		}
		return width;
	}

	/**
	 * Get actual width of the column by index 
	 * Note: There is small delay after Grid has been attached before real widths are available

	 * @param i Index of the column
	 * @return Actual width of the column in pixels double value
	 */
	public double getColumnWidth(int i) {
		if (columnWidths == null) return -1.0;
		if (i > grid.getColumns().size()) return -1.0;
		return columnWidths[i];
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

	public int getWidth() {
		return lastWidth;
	}
	
	public int getHeight() {
		return lastHeight;
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

	/**
	 * Set Grid to resize itself according to column widths automatically
	 * 
	 * @param autoResizeWidth If true Grid resizes itself to column widths 
	 */
	public void setAutoResizeWidth(boolean autoResizeWidth) {
		getState().autoResizeWidth = autoResizeWidth;
	}
	
    @Override
    public GridScrollExtensionState getState() {
        return (GridScrollExtensionState) super.getState();
    }


}

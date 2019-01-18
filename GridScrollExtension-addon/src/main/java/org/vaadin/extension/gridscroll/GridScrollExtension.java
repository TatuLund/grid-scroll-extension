package org.vaadin.extension.gridscroll;

import java.lang.reflect.Method;

import org.vaadin.extension.gridscroll.shared.ColumnResizeCompensationMode;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionClientRPC;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionServerRPC;
import org.vaadin.extension.gridscroll.shared.GridScrollExtensionState;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.server.AbstractExtension;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.Grid.Column;
import com.vaadin.util.ReflectTools;

/**
 * GridScrollExtension is multi purpose Grid enhancement package. It features 
 * additional events (resize, scroll, etc.), methods for pixelwise scrolling and
 * methods for componsating empty space appearing to Grid right side when columns
 * or Grid is being resized. 
 * 
 * @param <T> Bean type of the Grid
 * 
 * @author Tatu Lund
 *
 */
@SuppressWarnings("serial")
public class GridScrollExtension<T> extends AbstractExtension {
	private int lastXPosition;
	private int lastYPosition;
	private double[] columnWidths;
	private Grid<T> grid;
	private int lastWidth;
	private int lastHeight;
	private boolean restorePosition = true;	
	
	/**
	 * A listener interface for {@link GridScrolledEvent}
	 * 
	 * @param <T> Bean type of the Grid
	 */
	public interface GridScrolledListener<T> extends ConnectorEventListener {
		Method GRID_SCROLLED_METHOD = ReflectTools.findMethod(
				GridScrolledListener.class, "gridScrolled", GridScrolledEvent.class);
		public void gridScrolled(GridScrolledEvent<T> event);
	}
	
	/**
	 * A Listener interface for {@link GridRenderedEvent}
	 *
	 * @param <T> Bean type of the Grid
	 */
	public interface GridRenderedListener<T> extends ConnectorEventListener {
		Method GRID_RENDERED_METHOD = ReflectTools.findMethod(
				GridRenderedListener.class, "gridRendered", GridRenderedEvent.class);
		public void gridRendered(GridRenderedEvent<T> event);
	}

	/**
	 * A Listener interface for {@link GridResizedEvent}
	 * 
	 * @param <T> Bean type of the Grid
	 */
	public interface GridResizedListener<T> extends ConnectorEventListener {
		Method GRID_RESIZED_METHOD = ReflectTools.findMethod(
				GridResizedListener.class, "gridResized", GridResizedEvent.class);
		public void gridResized(GridResizedEvent<T> event);
	}
	
	/**
	 * A Listener interface for {@link GridColumnsResizedEvent}
	 * 
	 * @param <T> Bean type of the Grid
	 */
	public interface GridColumnsResizedListener<T> extends ConnectorEventListener {
		Method GRID_COLUMNS_RESIZED_METHOD = ReflectTools.findMethod(
				GridColumnsResizedListener.class, "gridColumnsResized", GridColumnsResizedEvent.class);
		public void gridColumnsResized(GridColumnsResizedEvent<T> event);
	}
	
	/**
	 * Constructor method for the extension
	 * 
	 * @param grid Grid to be extended
	 */	
	public GridScrollExtension(Grid<T> grid) {
		this.extend(grid);
		this.grid = grid;
		registerRpc(new GridScrollExtensionServerRPC() {

			@Override
			public void ping() {
			}

			@Override
			public void reportPosition(int Xposition, int Yposition) {
				if (restorePosition && (Xposition == -1 || Yposition == -1))  {
					restoreScrollPosition();
				} else {
					lastXPosition = Xposition;
					lastYPosition = Yposition;
					fireEvent(new GridScrolledEvent<T>(grid));
				}
			}

			@Override
			public void reportColumns(double[] widths, int column) {
				columnWidths = widths;				
				fireEvent(new GridColumnsResizedEvent<T>(grid,column));
				// This is awful, but needed to overcome race condition with faulty 
				// internal mechanics of the Grid
				if (column != -1) {
					UI ui = UI.getCurrent();
					Thread t = new Thread(() -> {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
						}
						ui.access(() -> {
							grid.getColumns().get(column).setWidth(widths[column]);
						});
					});
					t.start();
				}
			}

			@Override
			public void reportSize(int width, int height) {
				lastWidth = width;
				lastHeight = height;
				fireEvent(new GridResizedEvent<T>(grid));
			}

			@Override
			public void gridInitialColumnWidthsCalculated() {
				fireEvent(new GridRenderedEvent<T>(grid)); 
			}
		});
	}
	
	
	/**
	 * Add a new GridRenderedListener
	 * The GridRenderedEvent event is fired once after Grid's initial column width calculation is complete
	 * 
	 * @since 2.2.0
	 * 
	 * @param listener A GridRenderedListener to be added
	 */
	public Registration addGridRenderedListener(GridRenderedListener<T> listener) {
		return addListener(GridRenderedEvent.class, listener, GridRenderedListener.GRID_RENDERED_METHOD);
	}
	
	/**
	 * Add a new {@link GridResizedListener}
	 * The {@link GridResizedEvent} event is fired every time Grid size has been changed 
     * when {@link GridScrollExtension#setColumnResizeCompensationMode(org.vaadin.extension.gridscroll.shared.ColumnResizeCompensationMode)}
     * with {@link org.vaadin.extension.gridscroll.shared.ColumnResizeCompensationMode#RESIZE_GRID} has been applied.
	 * 
	 * @since 2.2.0
	 * 
	 * @param listener A GridResizedListener to be added
	 */
	public Registration addGridResizedListener(GridResizedListener<T> listener) {
		return addListener(GridResizedEvent.class, listener, GridResizedListener.GRID_RESIZED_METHOD);
	}

	/**
	 * Add a new {@link GridColumnsResizedListener}
	 * The {@link GridColumnsResizedEvent} event is fired every time Grid column sizes has been changed
	 * 
	 * Note, there is similar event in Grid, but that is fired before you can fetch real column widths, this
	 * event is fired after widths are available, hence you can get correct widths
	 * 
	 * @since 2.2.0
	 * 
	 * @param listener A GridColumnsResizedListener to be added
	 */
	public Registration addGridColumnsResizedListener(GridColumnsResizedListener<T> listener) {
		return addListener(GridColumnsResizedEvent.class, listener, GridColumnsResizedListener.GRID_COLUMNS_RESIZED_METHOD);
	}
	
	/**
	 * Add a new {@link GridScrolledListener}
	 * The {@link GridScrolledEvent} event is fired when Grid scroll position changes
	 * 
	 * @since 2.2.0
	 *  
	 * @param listener A GridScrolledListener to be added
	 */
	public Registration addGridScrolledListener(GridScrolledListener<T> listener) {
		return addListener(GridScrolledEvent.class, listener, GridScrolledListener.GRID_SCROLLED_METHOD);
	}
	
	
	/**
	 * Recalculate the Grid's width and adjust it to according to actual column widths
	 * 
	 * Programmatic change of column widths do not trigger column resize event, hence you
	 * need to call this if you want to refit Grid 
	 * 
	 * @since 2.2.0
	 */
	public void adjustGridWidth() {
		getClientRPC().recalculateGridWidth();		
	}
	
	/**
	 * Adjust last the width of the last column to occupy remaining space (if such exist)
	 * 
	 * Programmatic change of column widths do not trigger column resize event, hence you
	 * need to call this if you want to refit Grid 
	 */
	public void adjustLastColumnWidth() {
		getClientRPC().adjustLastColumn();		
	}
	
	/**
	 * Get actual width of the column by column reference
	 * Note: There is small delay after Grid has been attached before real widths are available
	 * 
	 * @since 2.1.0
	 * 
	 * @param column The column reference
	 * @return Actual width of the column in pixels double value
	 */
	public double getColumnWidth(Column<?,?> column) {
		double width = 0;
		int i = 0;
		if (columnWidths == null) return -1.0;
		if (column.isHidden()) return -1.0;
		for (Column<?, ?> col : grid.getColumns()) {
			if (col == column) width = columnWidths[i];
			if (!col.isHidden()) i++;
		}
		return width;		
	}
	
	/**
	 * Get actual width of the column by columnId 
	 * Note: There is small delay after Grid has been attached before real widths are available
	 * 
	 * @since 2.1.0
     *
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
	 * 
	 * @since 2.1.0
     *
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

	/**
	 * Get actual width
	 * 
	 * @since 2.2.0
	 * 
	 * @return The last reported actual width of the Grid 
	 */
	public int getWidth() {
		return lastWidth;
	}
	
	/**
	 * Get actual height
	 * 
	 * @since 2.2.0
	 * 
	 * @return The last reported actual height of the Grid 
	 */
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
	 * @deprecated since 2.3.0, use {@link #setColumnResizeCompensationMode(ColumnResizeCompensationMode)} instead.
	 * 
	 * @param autoResizeWidth If true Grid resizes itself to column widths 
	 */
	@Deprecated
	public void setAutoResizeWidth(boolean autoResizeWidth) {
		if (autoResizeWidth) getState().compensationMode = ColumnResizeCompensationMode.RESIZE_GRID;
		else getState().compensationMode = ColumnResizeCompensationMode.NONE;
	}
	
	/**
	 * @deprecated since 2.3.4, please use {@link #setColumnResizeCompensationMode(ColumnResizeCompensationMode)} instead.
	 */
	@Deprecated
	public void setColumnResizeComponesationMode(ColumnResizeCompensationMode mode) {
		this.setColumnResizeCompensationMode(mode);
	}

	/**
	 * Set how Grid should be adjusted when columns are being resized by user
	 * ColumnResizeCompensationMode.RESIZE_GRID will adjust Grid width
	 * and ColumnResizeCompensationMode.RESIZE_COLUMN the last column.
	 * Default is ColumnResizeCompensationMode.NONE.
	 *
	 * Note: ColumnResizeCompensationMode.RESIZE_COLUM takes effect also when
	 * Grid is being resized.
	 *
	 * Note: When ColumnResizeCompensationMode.RESIZE_COLUM is used, the {@link com.vaadin.ui.Grid.Column#setMaximumWidth(double)}
	 * cannot be used with the last column
	 * 
	 * @since 2.3.0
	 *
	 * @param mode ColumnResizeCompensationMode
	 */
	public void setColumnResizeCompensationMode(ColumnResizeCompensationMode mode) {
		getState().compensationMode = mode;
	}

	/**
	 * @deprecated since 2.3.4, please use {@link #getColumnResizeCompensationMode()} instead.
	 */
	@Deprecated
	public ColumnResizeCompensationMode getColumnResizeComponesationMode() {
		return this.getColumnResizeCompensationMode();
	}

	/**
	 * Get the current compensation mode
	 *
	 * @since 2.3.0
	 *
	 * @return The current compensation mode
	 */
	public ColumnResizeCompensationMode getColumnResizeCompensationMode() {
		return getState().compensationMode;
	}
	
    @Override
    public GridScrollExtensionState getState() {
        return (GridScrollExtensionState) super.getState();
    }

    /**
     * By default extension restores the last known position in order to make Grid recover
     * position during {@link com.vaadin.ui.TabSheet.Tab} changes. There is a side effect
     * of this, that {@link Grid#scrollTo(int)} does not work when calling right after Grid
     * has been created. This feature can be turned off if needed by setting this to false.
     * 
     * @since 2.3.3
     * 
     * @param restorePosition Use false to disable automatic position restore feature
     */
    public void setRestorePosition(boolean restorePosition) {
    	this.restorePosition = restorePosition;
    }
}

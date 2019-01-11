package org.vaadin.extension.demo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.annotation.WebServlet;

import org.vaadin.extension.gridscroll.GridScrollExtension;
import org.vaadin.extension.gridscroll.shared.ColumnResizeCompensationMode;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Push
@Theme("demo")
@Title("GridScrollExtension Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

	public class GridTab1 extends VerticalLayout {
		private Grid<SimplePojo> grid1 = new Grid<>();
        private GridScrollExtension<SimplePojo> ext1 = new GridScrollExtension<>(grid1);

		GridTab1() {
			Random random = new Random(4837291937l);
			List<SimplePojo> data = new ArrayList<>();
			for (int i = 0; i < 1000; i++) {
				data.add(new SimplePojo(i, "Bean", true, new Date(),
						BigDecimal.valueOf(random.nextDouble() * 100), Double
								.valueOf(random.nextInt(5))));
			}
			grid1.addColumn(SimplePojo::getDescription).setCaption("Description").setMaximumWidth(250);			
			grid1.addColumn(SimplePojo::getStars).setCaption("Rating").setMaximumWidth(250);
			grid1.addColumn(SimplePojo::isTruth).setCaption("Boolean");
			grid1.addColumn(SimplePojo::getDate).setCaption("A date");
			grid1.addColumn(SimplePojo::getNumber).setCaption("Long number");
			grid1.setItems(data);
			grid1.setSizeFull();
			VerticalLayout vLayout= new VerticalLayout();
			vLayout.addComponent(grid1);
			vLayout.setSizeFull();
			vLayout.setComponentAlignment(grid1, Alignment.MIDDLE_CENTER);
			HorizontalLayout hLayout = new HorizontalLayout();
			TextField field = new TextField("Position");
			field.addBlurListener(event -> {
				System.out.println("Blur fired!");
			});
			field.addFocusListener(event -> {
				System.out.println("Focus fired!");
			});
			Button saveButton = new Button("Save", event -> {
				Integer yPos = ext1.getLastYPosition();
				field.setValue(yPos.toString());
			});
			Button gotoButton = new Button("Goto", event -> {
				Integer newPos = Integer.parseInt(field.getValue());				
				ext1.setScrollPosition(0, newPos);
			});
			Label widthsLabel = new Label();
			Label sizeLabel = new Label("Width: "+ext1.getWidth()+", Height: "+ext1.getHeight());
			Button columnButton = new Button("Columns", event -> {
				String widths = "Column widths:";
				for (Column<SimplePojo, ?> col : grid1.getColumns()) {
					widths = widths + " "+ ext1.getColumnWidth(col);
				}
				widthsLabel.setValue(widths);
				sizeLabel.setValue("Width: "+ext1.getWidth()+", Height: "+ext1.getHeight());
			});
			
			Button resetButton = new Button("Reset", event -> {
				for (Column col : grid1.getColumns()) col.setWidth(200.0d);
				ext1.adjustGridWidth();
			});
			
			ext1.setColumnResizeCompensationMode(ColumnResizeCompensationMode.RESIZE_GRID);
			
			ext1.addGridRenderedListener(event -> {
				String widths = "Column widths:";
				for (Column<SimplePojo, ?> col : grid1.getColumns()) {
					widths = widths + " "+ ext1.getColumnWidth(col);
				}
				widthsLabel.setValue(widths);				
				sizeLabel.setValue("Width: "+ext1.getWidth()+", Height: "+ext1.getHeight());
			});
			
			ext1.addGridResizedListener(event -> {
				sizeLabel.setValue("Width: "+ext1.getWidth()+", Height: "+ext1.getHeight());				
			});

			ext1.addGridScrolledListener(event -> {
				Integer yPos = ext1.getLastYPosition();
				field.setValue(yPos.toString());
			});

			ext1.addGridColumnsResizedListener(event -> {
				String widths = "Column widths:";
				for (Column<SimplePojo, ?> col : grid1.getColumns()) {
					widths = widths + " "+ ext1.getColumnWidth(col);
				}
				widthsLabel.setValue(widths);				
			});
			
			hLayout.addComponents(field,gotoButton,saveButton,columnButton,resetButton,widthsLabel,sizeLabel);
			hLayout.setComponentAlignment(gotoButton, Alignment.BOTTOM_LEFT);
			hLayout.setComponentAlignment(saveButton, Alignment.BOTTOM_LEFT);
			vLayout.addComponent(hLayout);
			addComponent(vLayout);
			setMargin(true);
					
		}
	}

	public class GridTab2 extends VerticalLayout {
		private Grid<SimplePojo> grid2 = new Grid<>();
        private GridScrollExtension<SimplePojo> ext2 = new GridScrollExtension<>(grid2);

		GridTab2() {
			Random random = new Random(4837291937l);
			List<SimplePojo> data = new ArrayList<>();
			for (int i = 0; i < 1000; i++) {
				data.add(new SimplePojo(i, "Bean", true, new Date(),
						BigDecimal.valueOf(random.nextDouble() * 100), Double
								.valueOf(random.nextInt(5))));
			}
			grid2.addColumn(SimplePojo::getDescription).setCaption("Description").setMaximumWidth(250);			
			grid2.addColumn(SimplePojo::getStars).setCaption("Rating").setMaximumWidth(250);
			grid2.addColumn(SimplePojo::isTruth).setCaption("Boolean");
			grid2.addColumn(SimplePojo::getDate).setCaption("A date");
			grid2.addColumn(SimplePojo::getNumber).setCaption("Long number");
			grid2.setItems(data);
			grid2.setSizeFull();
			VerticalLayout vLayout= new VerticalLayout();
			vLayout.addComponent(grid2);
			vLayout.setSizeFull();
			vLayout.setComponentAlignment(grid2, Alignment.MIDDLE_CENTER);
			HorizontalLayout hLayout = new HorizontalLayout();
			TextField field = new TextField("Position");
			Button saveButton = new Button("Save", event -> {
				Integer yPos = ext2.getLastYPosition();
				field.setValue(yPos.toString());
			});
			Button gotoButton = new Button("Goto", event -> {
				Integer newPos = Integer.parseInt(field.getValue());				
				ext2.setScrollPosition(0, newPos);
			});
			ext2.setColumnResizeCompensationMode(ColumnResizeCompensationMode.RESIZE_COLUMN);
			hLayout.addComponents(field,gotoButton,saveButton);
			hLayout.setComponentAlignment(gotoButton, Alignment.BOTTOM_LEFT);
			hLayout.setComponentAlignment(saveButton, Alignment.BOTTOM_LEFT);
			vLayout.addComponent(hLayout);
			addComponent(vLayout);
			setMargin(true);
		}
	}

    
    @Override
    protected void init(VaadinRequest request) {

        // Show it in the middle of the screen
        TabSheet tabSheet = new TabSheet();
        tabSheet.addTab(new GridTab1(), "Grid 1");
        tabSheet.addTab(new GridTab2(), "Grid 2");
        tabSheet.setSizeFull();
        setContent(tabSheet);        
    }
    
  @WebServlet(value = "/*", asyncSupported = true)
  @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
  public static class Servlet extends VaadinServlet {
  }
}

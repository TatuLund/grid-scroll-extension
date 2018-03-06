package org.vaadin.extension.demo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.annotation.WebServlet;

import org.vaadin.extension.gridscroll.GridScrollExtension;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;

@Push
@Theme("demo")
@Title("GridScrollExtension Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

	Grid<SimplePojo> grid1 = null;
	Grid<SimplePojo> grid2 = null;

	public class GridTab1 extends VerticalLayout {
		GridTab1() {
			Random random = new Random(4837291937l);
			List<SimplePojo> data = new ArrayList<>();
			for (int i = 0; i < 1000; i++) {
				data.add(new SimplePojo(i, "Bean", true, new Date(),
						BigDecimal.valueOf(random.nextDouble() * 100), Double
								.valueOf(random.nextInt(5))));
			}
			grid1 = new Grid<SimplePojo>();
			grid1.addColumn(SimplePojo::getDescription);			
			grid1.addColumn(SimplePojo::getStars);
			grid1.addColumn(SimplePojo::isTruth);
			grid1.addColumn(SimplePojo::getDate);
			grid1.addColumn(SimplePojo::getNumber);
			grid1.setItems(data);
			grid1.setSizeFull();
	        GridScrollExtension ext1 = new GridScrollExtension(grid1);
			VerticalLayout vLayout= new VerticalLayout();
			vLayout.addComponent(grid1);
			vLayout.setSizeFull();
			vLayout.setComponentAlignment(grid1, Alignment.MIDDLE_CENTER);
			HorizontalLayout hLayout = new HorizontalLayout();
			TextField field = new TextField("Position");
			Button saveButton = new Button("Save", event -> {
				Integer yPos = ext1.getLastYPosition();
				field.setValue(yPos.toString());
			});
			Button gotoButton = new Button("Goto", event -> {
				Integer newPos = Integer.parseInt(field.getValue());				
				ext1.setScrollPosition(0, newPos);
			});
			Label widthsLabel = new Label();
			Button columnButton = new Button("Columns", event -> {
				String widths = "Column widths:";
				for (Column<SimplePojo, ?> col : grid1.getColumns()) {
					widths = widths + " "+ ext1.getColumnWidth(col);
				}
				widthsLabel.setValue(widths);
			});

			ext1.setAutoResizeWidth(true);
			
			hLayout.addComponents(field,gotoButton,saveButton,columnButton,widthsLabel);
			hLayout.setComponentAlignment(gotoButton, Alignment.BOTTOM_LEFT);
			hLayout.setComponentAlignment(saveButton, Alignment.BOTTOM_LEFT);
			vLayout.addComponent(hLayout);
			addComponent(vLayout);
			setMargin(true);

		}
	}

	public class GridTab2 extends VerticalLayout {
		GridTab2() {
			Random random = new Random(4837291937l);
			List<SimplePojo> data = new ArrayList<>();
			for (int i = 0; i < 1000; i++) {
				data.add(new SimplePojo(i, "Bean", true, new Date(),
						BigDecimal.valueOf(random.nextDouble() * 100), Double
								.valueOf(random.nextInt(5))));
			}
			grid2 = new Grid<SimplePojo>();
			grid2.addColumn(SimplePojo::getDescription);			
			grid2.addColumn(SimplePojo::getStars);
			grid2.addColumn(SimplePojo::isTruth);
			grid2.addColumn(SimplePojo::getDate);
			grid2.addColumn(SimplePojo::getNumber);
			grid2.setItems(data);
			grid2.setSizeFull();
	        GridScrollExtension ext2 = new GridScrollExtension(grid2);
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

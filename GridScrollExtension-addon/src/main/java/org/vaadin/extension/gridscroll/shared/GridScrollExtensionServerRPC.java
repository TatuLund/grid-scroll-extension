package org.vaadin.extension.gridscroll.shared;

import java.util.List;

import com.vaadin.shared.communication.ServerRpc;

public interface GridScrollExtensionServerRPC extends ServerRpc {

	void ping();
	
	void reportPosition(int x, int y);
	
	void reportColumns(double[] widths, int column);
	
	void gridInitialColumnWidthsCalculated(); 
	
	void reportSize(int width, int height);
}

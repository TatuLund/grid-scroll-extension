package org.vaadin.extension.gridscroll.shared;

import com.vaadin.shared.communication.ClientRpc;

public interface GridScrollExtensionClientRPC extends ClientRpc {

	public void setScrollPosition(int x, int y);
	
	public void recalculateGridWidth();
	
	public void adjustLastColumn();
	
}

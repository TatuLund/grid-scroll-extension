package org.vaadin.extension.gridscroll.shared;

import com.vaadin.shared.communication.ServerRpc;

public interface GridScrollExtensionServerRPC extends ServerRpc {

	void ping();
	
	void reportPosition(int x, int y);
	
}

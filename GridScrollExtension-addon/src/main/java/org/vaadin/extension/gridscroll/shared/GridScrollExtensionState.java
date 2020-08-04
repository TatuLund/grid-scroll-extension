package org.vaadin.extension.gridscroll.shared;

import com.vaadin.shared.communication.SharedState;

public class GridScrollExtensionState extends SharedState {

	public ColumnResizeCompensationMode compensationMode = ColumnResizeCompensationMode.NONE;
	public boolean widthGuardDisabled = false;
}

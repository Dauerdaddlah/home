package de.ddd.aircontrol.gui.gbc;

import java.awt.GridBagConstraints;

public enum Fill
{
	NONE(GridBagConstraints.NONE),
	HORIZONTAL(GridBagConstraints.HORIZONTAL),
	VERTICAL(GridBagConstraints.VERTICAL),
	BOTH(GridBagConstraints.BOTH);
	
	private final int gbcConst;
	
	private Fill(int gbcConst)
	{
		this.gbcConst = gbcConst;
	}
	
	public int getGbcConst()
	{
		return gbcConst;
	}
}

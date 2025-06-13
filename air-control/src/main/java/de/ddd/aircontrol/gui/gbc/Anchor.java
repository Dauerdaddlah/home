package de.ddd.aircontrol.gui.gbc;

import java.awt.GridBagConstraints;

public enum Anchor
{
	CENTER(GridBagConstraints.CENTER),
	
	NORTH(GridBagConstraints.NORTH),
	SOUTH(GridBagConstraints.SOUTH),
	WEST(GridBagConstraints.WEST),
	EAST(GridBagConstraints.EAST),
	
	NORTH_EAST(GridBagConstraints.NORTHEAST),
	NORTH_WEST(GridBagConstraints.NORTHWEST),
	SOUTH_EAST(GridBagConstraints.SOUTHEAST),
	SOUTH_WEST(GridBagConstraints.SOUTHWEST);
	
	private final int gbcConst;
	
	private Anchor(int gbcConst)
	{
		this.gbcConst = gbcConst;
	}
	
	public int getGbcConst()
	{
		return gbcConst;
	}
}

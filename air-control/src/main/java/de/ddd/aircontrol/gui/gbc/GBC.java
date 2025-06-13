package de.ddd.aircontrol.gui.gbc;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GBC extends GridBagConstraints
{
	public GBC()
	{
		super(0, 0, 1, 1, 0, 0, Anchor.CENTER.getGbcConst(), Fill.NONE.getGbcConst(), new Insets(0, 0, 0, 0), 0, 0);
	}
	
	public GBC(int row, int col)
	{
		this();
		pos(row, col);
	}
	
	public GBC row(int row)
	{
		this.gridy = row;
		return this;
	}
	
	public GBC col(int col)
	{
		this.gridx = col;
		return this;
	}
	
	public GBC pos(int row, int col)
	{
		this.gridy = row;
		this.gridx = col;
		return this;
	}
	
	public GBC width(int width)
	{
		this.gridwidth = width;
		return this;
	}
	
	public GBC height(int height)
	{
		this.gridheight = height;
		return this;
	}
	
	public GBC size(int width, int height)
	{
		this.gridwidth = width;
		this.gridheight = height;
		return this;
	}
	
	public GBC weightx(int weightx)
	{
		this.weightx = weightx;
		return this;
	}
	
	public GBC weighty(int weighty)
	{
		this.weighty = weighty;
		return this;
	}
	
	public GBC weight(int weightx, int weighty)
	{
		this.weightx = weightx;
		this.weighty = weighty;
		return this;
	}
	
	public GBC padx(int padx)
	{
		this.ipadx = padx;
		return this;
	}
	
	public GBC pady(int pady)
	{
		this.ipady = pady;
		return this;
	}
	
	public GBC pad(int padx, int pady)
	{
		this.ipadx = padx;
		this.ipady = pady;
		return this;
	}
	
	public GBC anchor(Anchor anchor)
	{
		this.anchor = anchor.getGbcConst();
		return this;
	}
	
	public GBC fill(Fill fill)
	{
		this.fill = fill.getGbcConst();
		
		switch(fill)
		{
			case HORIZONTAL ->
			{
				if(weightx == 0)
				{
					weightx = 1;
				}
			}
			case VERTICAL ->
			{
				if(weighty == 0)
				{
					weighty = 1;
				}
			}
			case BOTH ->
			{
				if(weightx == 0)
				{
					weightx = 1;
				}
				
				if(weighty == 0)
				{
					weighty = 1;
				}
			}
			case NONE ->
			{
			}
		}
		return this;
	}
	
	public GBC insets(Insets insets)
	{
		this.insets = insets;
		return this;
	}
	
	public GBC insets(int inset)
	{
		this.insets.top = inset;
		this.insets.left = inset;
		this.insets.bottom = inset;
		this.insets.right = inset;
		return this;
	}
	
	public GBC insets(int top, int left, int bottom, int right)
	{
		this.insets.top = top;
		this.insets.left = left;
		this.insets.bottom = bottom;
		this.insets.right = right;
		return this;
	}
	
	public GBC insetTop(int top)
	{
		this.insets.top = top;
		return this;
	}
	
	public GBC insetLeft(int left)
	{
		this.insets.left = left;
		return this;
	}
	
	public GBC insetBottom(int bottom)
	{
		this.insets.bottom = bottom;
		return this;
	}
	
	public GBC insetRight(int right)
	{
		this.insets.right = right;
		return this;
	}
}

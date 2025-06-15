package de.ddd.aircontrol.gui.view;

import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import de.ddd.aircontrol.Environment;
import de.ddd.aircontrol.gui.Gui;
import de.ddd.aircontrol.gui.GuiResources;
import de.ddd.aircontrol.gui.gbc.GBC;

public class ViewSim extends JPanel implements View
{
	private final JToggleButton tglSim;
	
	public ViewSim(Gui gui)
	{
		tglSim = new JToggleButton();
		tglSim.setIcon(new ImageIcon(GuiResources.getResource(GuiResources.SWITCH_OFF_64)));
		tglSim.addItemListener(e ->
			{
				if(tglSim.isSelected())
				{
					tglSim.setIcon(new ImageIcon(GuiResources.getResource(GuiResources.SWITCH_ON_64)));
				}
				else
				{
					tglSim.setIcon(new ImageIcon(GuiResources.getResource(GuiResources.SWITCH_OFF_64)));
				}
				
				boolean selected = tglSim.isSelected();
				
				gui.changeData(env ->
						env.setSimulation(selected));
			});
		
		setLayout(new GridBagLayout());
		add(tglSim, new GBC(0, 0));
	}

	@Override
	public void updateState(Environment env)
	{
		tglSim.setSelected(env.isSimulation());
	}

}

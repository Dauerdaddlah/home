package de.ddd.aircontrol.gui.view;

import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import de.ddd.aircontrol.Environment;
import de.ddd.aircontrol.gui.GuiResources;
import de.ddd.aircontrol.gui.gbc.GBC;

public class ViewSim extends JPanel implements View
{
	private final JToggleButton tglSim;
	
	public ViewSim()
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
				
				Environment.getDefault().getChangeExecutor().execute(() ->
						Environment.getDefault().setSimulation(tglSim.isSelected()));
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

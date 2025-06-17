package de.ddd.aircontrol.gui.view;

import java.awt.GridBagLayout;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import de.ddd.aircontrol.Environment;
import de.ddd.aircontrol.gui.Gui;
import de.ddd.aircontrol.gui.GuiResources;
import de.ddd.aircontrol.gui.gbc.Fill;
import de.ddd.aircontrol.gui.gbc.GBC;
import de.ddd.aircontrol.pi.Model;
import de.ddd.aircontrol.pi.SimPi;
import de.ddd.aircontrol.sensor.SensorResult;
import de.ddd.aircontrol.sensor.SimSensor;

public class ViewSim extends JPanel implements View
{
	public static final String KEY_ORIG_PI = "origPi";
	public static final String KEY_ORIG_SENSOR = "origSensor";
	
	private final Gui gui;
	
	private final JToggleButton tglSim;
	private final JToggleButton tglSensor;
	private final JToggleButton tglPi;
	private final JCheckBox chkHumid;
	private final JLabel lblHumid;
	private final JCheckBox chkTemp;
	private final JLabel lblTemp;
	
	private final SimSensor simSensor;
	private final SimPi simPi;
	
	public ViewSim(Gui gui)
	{
		this.gui = gui;
		
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
			});
		
		tglPi = new JToggleButton("pi");
		
		tglSensor = new JToggleButton("sensor");
		
		chkHumid = new JCheckBox("Feuchtigkeit");
		lblHumid = new JLabel("35");
		JButton btnLeftHumid = new JButton("<");
		btnLeftHumid.addActionListener(e -> updateData(-1, 0));
		JButton btnRightHumid = new JButton(">");
		btnRightHumid.addActionListener(e -> updateData(1, 0));
		
		chkTemp = new JCheckBox("Temperatur");
		lblTemp = new JLabel("17");
		JButton btnLeftTemp = new JButton("<");
		btnLeftTemp.addActionListener(e -> updateData(0, -1));
		JButton btnRightTemp = new JButton(">");
		btnRightTemp.addActionListener(e -> updateData(0, 1));
		
		simSensor = new SimSensor();
		simPi = new SimPi(Model.PI_3_B);
		
		ItemListener il = e -> updateData();
		
		tglSim.addItemListener(il);
		tglPi.addItemListener(il);
		tglSensor.addItemListener(il);
		chkHumid.addItemListener(il);
		chkTemp.addItemListener(il);
		
		setLayout(new GridBagLayout());
		add(tglSim, new GBC(0, 0).insets(5));
		
		add(tglPi, new GBC(1, 0).insets(5));
		add(tglSensor, new GBC(2, 0).insets(5));
		add(chkHumid, new GBC(2, 1).insets(5));
		add(btnLeftHumid, new GBC(2, 2).insets(5));
		add(lblHumid, new GBC(2, 3).insets(5));
		add(btnRightHumid, new GBC(2, 4).insets(5));
		add(chkTemp, new GBC(3, 1).insets(5));
		add(btnLeftTemp, new GBC(3, 2).insets(5));
		add(lblTemp, new GBC(3, 3).insets(5));
		add(btnRightTemp, new GBC(3, 4).insets(5));
		
		add(new JLabel(), new GBC(99, 99).fill(Fill.BOTH));
	}
	
	private void updateData(int deltaHumid, int deltaTemp)
	{
		int h = Integer.parseInt(lblHumid.getText()) + deltaHumid;
		lblHumid.setText("" + Math.max(0, Math.min(100, h)));
		
		int t = Integer.parseInt(lblTemp.getText()) + deltaTemp;
		lblTemp.setText("" + t);
		
		updateData();
	}
	
	private void updateData()
	{
		boolean sim = tglSim.isSelected();
		boolean pi = tglPi.isSelected();
		boolean sensor = tglSensor.isSelected();
		
		boolean humid = chkHumid.isSelected();
		final double h = parseDouble(humid, lblHumid.getText());
		boolean temp = chkTemp.isSelected();
		final double t = parseDouble(temp, lblTemp.getText());
		
		gui.changeData(env ->
			{
				env.setSimulation(sim);
				
				simSensor.setResult(new SensorResult(h, t));
				
				if(sim && pi)
				{
					if(env.getPi() != simPi)
					{
						env.putValue(KEY_ORIG_PI, env.getPi());
						env.setPi(simPi);
					}
				}
				else
				{
					if(env.getPi() == simPi)
					{
						env.setPi(env.getValue(KEY_ORIG_PI));
					}
				}
				
				if(sim && sensor)
				{
					if(env.getSensor(Environment.SENSOR_BATH) != simSensor)
					{
						env.putValue(KEY_ORIG_SENSOR, env.getSensor(Environment.SENSOR_BATH));
						env.getSensors().put(Environment.SENSOR_BATH, simSensor);
					}
				}
				else
				{
					if(env.getSensor(Environment.SENSOR_BATH) == simSensor)
					{
						env.getSensors().put(Environment.SENSOR_BATH, env.getValue(KEY_ORIG_SENSOR));
					}
				}
			});
	}

	private double parseDouble(boolean active, String text)
	{
		if(!active)
		{
			return Double.NaN;
		}
		
		try
		{
			return Double.parseDouble(text);
		}
		catch(Exception e)
		{
			return Double.NaN;
		}
	}

	@Override
	public void updateState(Environment env)
	{
		tglSim.setSelected(env.isSimulation());
	}

}

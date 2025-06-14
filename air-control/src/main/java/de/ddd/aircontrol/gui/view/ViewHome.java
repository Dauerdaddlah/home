package de.ddd.aircontrol.gui.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

import de.ddd.aircontrol.Environment;
import de.ddd.aircontrol.gui.GuiResources;
import de.ddd.aircontrol.gui.gbc.Anchor;
import de.ddd.aircontrol.gui.gbc.Fill;
import de.ddd.aircontrol.gui.gbc.GBC;
import de.ddd.aircontrol.sensor.SensorResult;

public class ViewHome extends JPanel implements View
{
	private final JLabel lblHumidity;
	private final JLabel lblTemperature;
	private final JLabel lblBridge;
	private JSlider slider;
	
	public ViewHome()
	{
		JPanel content = this;
		
		content.setLayout(new GridBagLayout());
		
		lblHumidity = new JLabel("- %");
		
		lblTemperature = new JLabel("- °");
		
		lblBridge = new JLabel();
		lblBridge.setIcon(new ImageIcon(GuiResources.getResource("switch-unknown_64.png")));
		
		JLabel lblLevelimg = new JLabel();
		lblLevelimg.setIcon(new ImageIcon(GuiResources.getResource("fan_64.png")));
		lblLevelimg.setForeground(Color.RED);
		
		JLabel lblTemperatureImg = new JLabel();
		lblTemperatureImg.setIcon(new ImageIcon(GuiResources.getResource("thermometer_64.png")));
		
		JLabel lblHumidImg = new JLabel();
		lblHumidImg.setIcon(new ImageIcon(GuiResources.getResource("waterdrops_64.png")));
		
		JLabel lblBridgeImg = new JLabel();
		lblBridgeImg.setIcon(new ImageIcon(GuiResources.getResource("turn-on_64.png")));
		
		JToggleButton tglMode = new JToggleButton();
		tglMode.setIcon(new ImageIcon(GuiResources.getResource("finger_64.png")));
		tglMode.setMargin(new Insets(5, 5, 5, 5));
		
		slider = new JSlider(JSlider.HORIZONTAL, -1, 3, -1);
		slider.setSnapToTicks(true);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(1);
		slider.setPaintLabels(true);
		Dictionary<Integer, JComponent> levelLabelTable = new Hashtable<>();
		levelLabelTable.put(-1, new JLabel("Standard"));
		levelLabelTable.put(0, new JLabel("Aus"));
		levelLabelTable.put(1, new JLabel("1"));
		levelLabelTable.put(2, new JLabel("2"));
		levelLabelTable.put(3, new JLabel("3"));
		slider.setLabelTable(levelLabelTable);
		
		int row = 0;
		
		content.add(new JLabel(), new GBC(row, 0).fill(Fill.BOTH));
		content.add(new JLabel(), new GBC(row, 1).fill(Fill.BOTH));
		content.add(new JLabel(), new GBC(row, 2).fill(Fill.BOTH));
		content.add(new JLabel(), new GBC(row, 3).fill(Fill.BOTH));
		
		row++;
		content.add(lblHumidImg,
				new GBC(row, 0).anchor(Anchor.EAST).insets(5));
		content.add(lblHumidity,
				new GBC(row, 1).anchor(Anchor.WEST).insets(5));
		content.add(new JLabel("Feuchtigkeit"),
				new GBC(row + 1, 0).width(2));
		
		content.add(lblTemperatureImg,
				new GBC(row, 2).anchor(Anchor.EAST).insets(5));
		content.add(lblTemperature,
				new GBC(row, 3).anchor(Anchor.WEST).insets(5));
		content.add(new JLabel("Temperatur"),
				new GBC(row + 1, 2).width(2));
		
		row++;
		content.add(new JLabel(), new GBC(row, 0).fill(Fill.BOTH));
		content.add(new JLabel(), new GBC(row, 1).fill(Fill.BOTH));
		content.add(new JLabel(), new GBC(row, 2).fill(Fill.BOTH));
		content.add(new JLabel(), new GBC(row, 3).fill(Fill.BOTH));
		
		row++;
		content.add(lblBridgeImg,
				new GBC(row, 0).anchor(Anchor.EAST).insets(5));
		content.add(lblBridge,
				new GBC(row, 1).anchor(Anchor.WEST).insets(5));
		content.add(new JLabel("Schalter"),
				new GBC(row + 1, 0).width(2));
		
		content.add(tglMode,
				new GBC(row, 2).width(2).insets(5));
		content.add(new JLabel("Handbetrieb"),
				new GBC(row + 1, 2).width(2));
		
		row++;
		content.add(new JLabel(), new GBC(row, 0).fill(Fill.BOTH));
		content.add(new JLabel(), new GBC(row, 1).fill(Fill.BOTH));
		content.add(new JLabel(), new GBC(row, 2).fill(Fill.BOTH));
		content.add(new JLabel(), new GBC(row, 3).fill(Fill.BOTH));
		
		row++;
		content.add(lblLevelimg,
				new GBC(row, 0).insets(5));
		content.add(slider,
				new GBC(row, 1).width(3).fill(Fill.HORIZONTAL).insets(5));
		
		for(int i = 0; i < content.getComponentCount(); i++)
		{
			Component c = content.getComponent(i);
			c.setFont(c.getFont().deriveFont(20f));
		}
		
//		for(int r = 0; r < row + 1; r++)
//		{
//			for(int col = 0; col < 4; col++)
//			{
//				JLabel lbl = new JLabel();
//				lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
//				content.add(lbl,
//						new GBC(r, col).fill(Fill.BOTH).weight(0, 0));
//			}
//		}
	}
	
	@Override
	public void updateState(Environment env)
	{
		SensorResult result = env.getLastResult(Environment.SENSOR_BATH);
		
		if(result.hasHumidity())
		{
			lblHumidity.setText((int)result.humidity() + " %");
		}
		
		if(result.hasTemperature())
		{
			lblTemperature.setText((int)result.temperature() + "°");
		}
		
		switch(env.getVentilation().getVentilationMode())
		{
			case BRIDGE ->
			{
				lblBridge.setIcon(new ImageIcon(GuiResources.getResource("switch-on_64.png")));
			}
			case NORMAL ->
			{
				lblBridge.setIcon(new ImageIcon(GuiResources.getResource("switch-off_64.png")));
			}
			case UNKNOWN ->
			{
				lblBridge.setIcon(new ImageIcon(GuiResources.getResource("switch-unknown_64.png")));
			}
		}
		
		switch(env.getVentilation().getLevel())
		{
			case DEFAULT -> slider.setValue(-1);
			case OFF -> slider.setValue(0);
			case ONE -> slider.setValue(1);
			case TWO -> slider.setValue(2);
			case THREE -> slider.setValue(3);
		}
		slider.setValue(UNDEFINED_CONDITION);
	}
}

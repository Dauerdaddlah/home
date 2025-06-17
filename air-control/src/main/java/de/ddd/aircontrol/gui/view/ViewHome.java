package de.ddd.aircontrol.gui.view;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

import de.ddd.aircontrol.Environment;
import de.ddd.aircontrol.gui.Gui;
import de.ddd.aircontrol.gui.GuiResources;
import de.ddd.aircontrol.gui.gbc.Anchor;
import de.ddd.aircontrol.gui.gbc.Fill;
import de.ddd.aircontrol.gui.gbc.GBC;
import de.ddd.aircontrol.sensor.SensorResult;
import de.ddd.aircontrol.ventilation.Level;

public class ViewHome extends JPanel implements View
{
//	private final Gui gui;
	
	private final JLabel lblHumidity;
	private final JLabel lblTemperature;
	private final JLabel lblBridge;
	private final JSlider slider;
	private final JToggleButton tglMode;
	
	public ViewHome(Gui gui)
	{
//		this.gui = gui;
		
		JPanel content = this;
		
		content.setLayout(new GridBagLayout());
		
		lblHumidity = new JLabel("- %");
		
		lblTemperature = new JLabel("- °");
		
		lblBridge = new JLabel();
		lblBridge.setIcon(new ImageIcon(GuiResources.getResource(GuiResources.SWITCH_UNKNOWN_64)));
		
		JLabel lblLevelimg = new JLabel();
		lblLevelimg.setIcon(new ImageIcon(GuiResources.getResource(GuiResources.FAN_64)));
		lblLevelimg.setForeground(Color.RED);
		lblLevelimg.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent e)
				{
					gui.changeData(env -> env.getEventQueue().addAction(env.getUpdateAction()));
				}
			});
		
		JLabel lblTemperatureImg = new JLabel();
		lblTemperatureImg.setIcon(new ImageIcon(GuiResources.getResource(GuiResources.THERMOMETER_64)));
		
		JLabel lblHumidImg = new JLabel();
		lblHumidImg.setIcon(new ImageIcon(GuiResources.getResource(GuiResources.WATERDROPS_64)));
		
		JLabel lblBridgeImg = new JLabel();
		lblBridgeImg.setIcon(new ImageIcon(GuiResources.getResource(GuiResources.TURN_ON_64)));
		
		tglMode = new JToggleButton();
		tglMode.setIcon(new ImageIcon(GuiResources.getResource(GuiResources.FINGER_64)));
		tglMode.setMargin(new Insets(5, 5, 5, 5));
		tglMode.addItemListener(e ->
			{
				boolean handmode = tglMode.isSelected();
				
				gui.changeData(env ->
						env.setHandMode(handmode));
			});
		
		slider = new JSlider(JSlider.HORIZONTAL, -1, 3, -1);
		slider.setSnapToTicks(true);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(1);
		slider.setPaintLabels(true);
		Dictionary<Integer, JComponent> levelLabelTable = new Hashtable<>();
		levelLabelTable.put(levelToInt(Level.DEFAULT), new JLabel("Standard"));
		levelLabelTable.put(levelToInt(Level.OFF), new JLabel("Aus"));
		levelLabelTable.put(levelToInt(Level.ONE), new JLabel("1"));
		levelLabelTable.put(levelToInt(Level.TWO), new JLabel("2"));
		levelLabelTable.put(levelToInt(Level.THREE), new JLabel("3"));
		slider.setLabelTable(levelLabelTable);
		slider.addChangeListener(e ->
			{
				if(!slider.getValueIsAdjusting())
				{
					Level lvl = intToLevel(slider.getValue());
					
					gui.changeData(env ->
						{
							if(env.isHandMode())
							{
								env.getVentilation().setLevel(lvl, env.getPi());
							}
						});
				}
			});
		
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
		
		switch(env.getVentilation().getVentilationMode(env.getPi()))
		{
			case BRIDGE ->
			{
				lblBridge.setIcon(new ImageIcon(GuiResources.getResource(GuiResources.SWITCH_ON_64)));
			}
			case NORMAL ->
			{
				lblBridge.setIcon(new ImageIcon(GuiResources.getResource(GuiResources.SWITCH_OFF_64)));
			}
			case UNKNOWN ->
			{
				lblBridge.setIcon(new ImageIcon(GuiResources.getResource(GuiResources.SWITCH_UNKNOWN_64)));
			}
		}
		
		slider.setValue(levelToInt(env.getLastLevel()));
		slider.setEnabled(env.isHandMode());
		
		tglMode.setSelected(env.isHandMode());
	}
	
	private Level intToLevel(int i)
	{
		return switch(i)
			{
				case -1 -> Level.DEFAULT;
				default -> Level.OFF;
				case 1 -> Level.ONE;
				case 2 -> Level.TWO;
				case 3 -> Level.THREE;
			};
	}
	
	private int levelToInt(Level lvl)
	{
		return switch(lvl)
			{
				case DEFAULT -> -1;
				default -> 0;
				case ONE -> 1;
				case TWO -> 2;
				case THREE -> 3;
			};
	}
}

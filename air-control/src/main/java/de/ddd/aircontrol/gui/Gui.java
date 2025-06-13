package de.ddd.aircontrol.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

import de.ddd.aircontrol.gui.gbc.Anchor;
import de.ddd.aircontrol.gui.gbc.Fill;
import de.ddd.aircontrol.gui.gbc.GBC;

public class Gui
{
	public static final int WIDTH = 480;
	public static final int HEIGHT = 320;
	
	private final JFrame frame;
	
	public Gui()
	{
		JPanel zoom = new JPanel();
		JPanel content = new JPanel();
		
		initLayout(content);
		
		this.frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(zoom, BorderLayout.CENTER);
		
		zoom.setLayout(new BorderLayout());
		zoom.add(content, BorderLayout.CENTER);
		
		frame.setUndecorated(true);
//		frame.setAlwaysOnTop(true);
		frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		frame.pack();
		frame.setLocationRelativeTo(null);
		
		frame.setVisible(true);
	}

	private void initLayout(JPanel content)
	{
		content.setLayout(new GridBagLayout());
		
		// layout
		// --------------------------------------------------------------
		//                                                      Settings
		//
		//               Humid   XXX %            Temp   XXX°
		//               Bridge  XXX              Manual  XXX
		// Level                        Switch
		// --------------------------------------------------------------
		
		JLabel lblHumidity = new JLabel("- %");
		
		JLabel lblTemperature = new JLabel("- °");
		
		JLabel lblBridge = new JLabel();
		lblBridge.setIcon(new ImageIcon(getClass().getResource("switch-unknown_64.png")));
		
		JLabel lblLevelimg = new JLabel();
		lblLevelimg.setIcon(new ImageIcon(getClass().getResource("fan_64.png")));
		
		JLabel lblTemperatureImg = new JLabel();
		lblTemperatureImg.setIcon(new ImageIcon(getClass().getResource("thermometer_64.png")));
		
		JLabel lblHumidImg = new JLabel();
		lblHumidImg.setIcon(new ImageIcon(getClass().getResource("waterdrops_64.png")));
		
		JLabel lblBridgeImg = new JLabel();
		lblBridgeImg.setIcon(new ImageIcon(getClass().getResource("turn-on_64.png")));
		
		JToggleButton tglMode = new JToggleButton();
		tglMode.setIcon(new ImageIcon(getClass().getResource("finger_64.png")));
		
		JSlider slider = new JSlider(JSlider.HORIZONTAL, -1, 3, -1);
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
		
		JButton btnSettings = new JButton();
		btnSettings.setIcon(new ImageIcon(getClass().getResource("setting_32.png")));
		btnSettings.setMargin(new Insets(5, 5, 5, 5));
		
		int row = 0;
		content.add(btnSettings,
				new GBC(row, 3).anchor(Anchor.NORTH_EAST).insets(5));
		
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
}

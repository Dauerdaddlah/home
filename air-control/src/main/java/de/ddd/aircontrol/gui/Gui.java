package de.ddd.aircontrol.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

import de.ddd.aircontrol.Environment;
import de.ddd.aircontrol.event.EventAction;
import de.ddd.aircontrol.event.EventQueue;
import de.ddd.aircontrol.gui.gbc.Fill;
import de.ddd.aircontrol.gui.gbc.GBC;
import de.ddd.aircontrol.gui.view.View;
import de.ddd.aircontrol.gui.view.ViewHome;
import de.ddd.aircontrol.gui.view.ViewSettings;
import de.ddd.aircontrol.gui.view.ViewSim;

public class Gui
{
	public static final int WIDTH = 480;
	public static final int HEIGHT = 320;
	
	public static final String CLIENT_PROPERTY_VIEW = "view";
	public static final String VIEW_HOME = "home";
	public static final String VIEW_SETTINGS = "settings";
	public static final String VIEW_SIM = "sim";
	
	private final EventQueue executor;
	
	private final JFrame frame;
	
	private CardLayout cardView;
	private JPanel pnlView;
	
	private final ZoomPanel zoom;
	
	private final List<JToggleButton> viewButtons;
	private String currentView;
	
	private List<View> views;
	
	public Gui(EventQueue executor)
	{
		this.executor = executor;
		viewButtons = new ArrayList<>();
		views = new ArrayList<>();
		
		initUi();
		
		zoom = new ZoomPanel();
		zoom.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		JPanel content = initLayout();
		
		this.frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(zoom, BorderLayout.CENTER);
		
		zoom.setLayout(new GridBagLayout());
		zoom.add(content, new GBC().fill(Fill.BOTH));
		
		increaseFonts(content);
		
		// TODO set undecorated if no sim available
		frame.setUndecorated(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		
		frame.setVisible(true);
	}
	
	private void increaseFonts(Container container)
	{
		// increase fonts
		for(int i = 0; i < container.getComponentCount(); i++)
		{
			Component c = container.getComponent(i);
			c.setFont(c.getFont().deriveFont(20f));
			
			if(c instanceof Container cont)
			{
				increaseFonts(cont);
			}
		}
	}

	public void updateState(Environment env)
	{
		if(env.isSimulation() && zoom.getBorder() == null)
		{
			zoom.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
		}
		else if(!env.isSimulation() && zoom.getBorder() != null)
		{
			zoom.setBorder(null);
		}
		
		for(View view : views)
		{
			view.updateState(env);
		}
	}
	
	private void initUi()
	{
		UIManager.put("ToggleButton.select", new Color(171, 235, 198)); // light green
	}

	private JPanel initLayout()
	{
		cardView = new CardLayout();
		pnlView = new JPanel();
		pnlView.setLayout(cardView);
		
		JPanel pnlViewHome = new ViewHome(this);
		JPanel pnlViewSettings = new ViewSettings(this);
		JPanel pnlViewSim = new ViewSim(this);
		
		views.add((View)pnlViewHome);
		views.add((View)pnlViewSettings);
		views.add((View)pnlViewSim);
		
		pnlView.add(pnlViewHome, VIEW_HOME);
		pnlView.add(pnlViewSettings, VIEW_SETTINGS);
		pnlView.add(pnlViewSim, VIEW_SIM);
		
		JPanel top = initNavigationPanel();
		
		JPanel content = new JPanel();
		content.setLayout(new GridBagLayout());
		
		content.add(top, new GBC(0, 0).fill(Fill.HORIZONTAL));
		content.add(new JSeparator(JSeparator.HORIZONTAL), new GBC(1, 0).fill(Fill.HORIZONTAL));
		content.add(pnlView, new GBC(2, 0).fill(Fill.BOTH));
		
		return content;
	}
	
	private JPanel initNavigationPanel()
	{
		JToggleButton tglViewSettings = new JToggleButton();
		tglViewSettings.setIcon(new ImageIcon(GuiResources.getResource(GuiResources.SETTING_32)));
		tglViewSettings.setMargin(new Insets(5, 5, 5, 5));
		tglViewSettings.putClientProperty(CLIENT_PROPERTY_VIEW, VIEW_SETTINGS);
		tglViewSettings.addItemListener(this::viewStateChanged);
		viewButtons.add(tglViewSettings);
		
		JToggleButton tglViewHome = new JToggleButton();
		tglViewHome.setIcon(new ImageIcon(GuiResources.getResource(GuiResources.HOME_32)));
		tglViewHome.setMargin(new Insets(5, 5, 5, 5));
		tglViewHome.putClientProperty(CLIENT_PROPERTY_VIEW, VIEW_HOME);
		tglViewHome.setSelected(true);
		tglViewHome.addItemListener(this::viewStateChanged);
		viewButtons.add(tglViewHome);
		
		JToggleButton tglViewSim = new JToggleButton();
		tglViewSim.setIcon(new ImageIcon(GuiResources.getResource(GuiResources.CUBE_32)));
		tglViewSim.setMargin(new Insets(5, 5, 5, 5));
		tglViewSim.putClientProperty(CLIENT_PROPERTY_VIEW, VIEW_SIM);
		tglViewSim.addItemListener(this::viewStateChanged);
		viewButtons.add(tglViewSim);
		
		JPanel top = new JPanel();
		top.setLayout(new GridBagLayout());
		
		top.add(new JLabel(),
				new GBC(0, 0).fill(Fill.HORIZONTAL));
		top.add(tglViewSim,
				new GBC(0, 1).insets(5));
		top.add(tglViewHome,
				new GBC(0, 2).insets(5));
		top.add(tglViewSettings,
				new GBC(0, 3).insets(5));
		
		return top;
	}
	
	private void viewStateChanged(ItemEvent e)
	{
		JToggleButton tgl = (JToggleButton)e.getItemSelectable();
		
		if(e.getStateChange() == ItemEvent.SELECTED)
		{
			String newView = (String)tgl.getClientProperty(CLIENT_PROPERTY_VIEW);
			
			if(currentView != newView)
			{
				currentView = newView;
				cardView.show(pnlView, currentView);
			}
		}
		
		for(JToggleButton t : viewButtons)
		{
			t.setSelected(t.getClientProperty(CLIENT_PROPERTY_VIEW) == currentView);
		}
	}
	
	public void changeData(EventAction action)
	{
		executor.addAction(action);
	}
	
	@SuppressWarnings("unused")
	private void showGrid(Component comp)
	{
		if(comp instanceof JPanel pnl)
		{
			if(pnl.getLayout() instanceof GridBagLayout gbl)
			{
//				int rows = 
//				int cols = 
//				for(int rrow = 0; r < rows + 1; r++)
//				{
//					for(int col = 0; col < cols; col++)
//					{
//						JLabel lbl = new JLabel();
//						lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
//						content.add(lbl,
//								new GBC(r, col).fill(Fill.BOTH).weight(0, 0));
//					}
//				}
			}
		}
		
		if(comp instanceof Container cont)
		{
			for(int i = 0; i < cont.getComponentCount(); i++)
			{
				showGrid(cont.getComponent(i));
			}
		}
	}
}

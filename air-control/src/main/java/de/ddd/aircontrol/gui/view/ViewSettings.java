package de.ddd.aircontrol.gui.view;

import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.ddd.aircontrol.Environment;
import de.ddd.aircontrol.control.ControllerSimple;
import de.ddd.aircontrol.gui.Gui;
import de.ddd.aircontrol.gui.gbc.GBC;

public class ViewSettings extends JPanel implements View
{
	private final Gui gui;
	
	private final JLabel lblStart1;
	private final JLabel lblStart2;
	private final JLabel lblStart3;
	private final JLabel lblEnd1;
	private final JLabel lblEnd2;
	private final JLabel lblEnd3;
	
	public ViewSettings(Gui gui)
	{
		this.gui = gui;
		
		JButton btnLeftStart1 = new JButton("<-");
		btnLeftStart1.addActionListener(e -> adjust(-1, 0, 0, 0, 0, 0));
		JButton btnLeftStart2 = new JButton("<-");
		btnLeftStart2.addActionListener(e -> adjust(0, -1, 0, 0, 0, 0));
		JButton btnLeftStart3 = new JButton("<-");
		btnLeftStart3.addActionListener(e -> adjust(0, 0, -1, 0, 0, 0));
		JButton btnLeftEnd1 = new JButton("<-");
		btnLeftEnd1.addActionListener(e -> adjust(0, 0, 0, -1, 0, 0));
		JButton btnLeftEnd2 = new JButton("<-");
		btnLeftEnd2.addActionListener(e -> adjust(0, 0, 0, 0, -1, 0));
		JButton btnLeftEnd3 = new JButton("<-");
		btnLeftEnd3.addActionListener(e -> adjust(0, 0, 0, 0, 0, -1));
		
		JButton btnRightStart1 = new JButton("->");
		btnRightStart1.addActionListener(e -> adjust(1, 0, 0, 0, 0, 0));
		JButton btnRightStart2 = new JButton("->");
		btnRightStart2.addActionListener(e -> adjust(0, 1, 0, 0, 0, 0));
		JButton btnRightStart3 = new JButton("->");
		btnRightStart3.addActionListener(e -> adjust(0, 0, 1, 0, 0, 0));
		JButton btnRightEnd1 = new JButton("->");
		btnRightEnd1.addActionListener(e -> adjust(0, 0, 0, 1, 0, 0));
		JButton btnRightEnd2 = new JButton("->");
		btnRightEnd2.addActionListener(e -> adjust(0, 0, 0, 0, 1, 0));
		JButton btnRightEnd3 = new JButton("->");
		btnRightEnd3.addActionListener(e -> adjust(0, 0, 0, 0, 0, 1));
		
		lblStart1 = new JLabel();
		lblStart2 = new JLabel();
		lblStart3 = new JLabel();
		lblEnd1 = new JLabel();
		lblEnd2 = new JLabel();
		lblEnd3 = new JLabel();
		
		setLayout(new GridBagLayout());
		
		int row = 0;
		
		add(btnLeftStart1, new GBC(row, 0).insets(5));
		add(lblStart1, new GBC(row, 1).insets(5));
		add(btnRightStart1, new GBC(row, 2).insets(5));
		
		row++;
		add(btnLeftStart2, new GBC(row, 0).insets(5));
		add(lblStart2, new GBC(row, 1).insets(5));
		add(btnRightStart2, new GBC(row, 2).insets(5));
		
		row++;
		add(btnLeftStart3, new GBC(row, 0).insets(5));
		add(lblStart3, new GBC(row, 1).insets(5));
		add(btnRightStart3, new GBC(row, 2).insets(5));
		
		row++;
		add(btnLeftEnd1, new GBC(row, 0).insets(5));
		add(lblEnd1, new GBC(row, 1).insets(5));
		add(btnRightEnd1, new GBC(row, 2).insets(5));
		
		row++;
		add(btnLeftEnd2, new GBC(row, 0).insets(5));
		add(lblEnd2, new GBC(row, 1).insets(5));
		add(btnRightEnd2, new GBC(row, 2).insets(5));
		
		row++;
		add(btnLeftEnd3, new GBC(row, 0).insets(5));
		add(lblEnd3, new GBC(row, 1).insets(5));
		add(btnRightEnd3, new GBC(row, 2).insets(5));
	}

	private void adjust(int s1, int s2, int s3, int e1, int e2, int e3)
	{
		int start1 = Integer.valueOf(lblStart1.getText()) + s1;
		int start2 = Integer.valueOf(lblStart2.getText()) + s2;
		int start3 = Integer.valueOf(lblStart3.getText()) + s3;
		int end1 = Integer.valueOf(lblEnd1.getText()) + e1;
		int end2 = Integer.valueOf(lblEnd2.getText()) + e2;
		int end3= Integer.valueOf(lblEnd3.getText()) + e3;
		
		if(start1 <= start2 && start2 <= start3 && end1 <= start1 && end2 <= start2 && end3 <= start3
				&& start1 >= 0 && start2 >= 0 && start3 >= 0 && end1 >= 0 && end2 >= 0 && end3 >= 0
				&& start1 <= 100 && start2 <= 100 && start3 <= 100 && end1 <= 100 && end2 <= 100 && end3 <= 100)
		{
			gui.changeData(env ->
				{
					ControllerSimple cs = (ControllerSimple)env.getController();
					cs.setStart1(start1);
					cs.setStart2(start2);
					cs.setStart3(start3);
					cs.setEnd1(end1);
					cs.setEnd2(end2);
					cs.setEnd3(end3);
				});
		}
	}

	@Override
	public void updateState(Environment env)
	{
		// TODO
		ControllerSimple c = (ControllerSimple)env.getController();
		
		lblStart1.setText("" + c.getStart1());
		lblStart2.setText("" + c.getStart2());
		lblStart3.setText("" + c.getStart3());
		lblEnd1.setText("" + c.getEnd1());
		lblEnd2.setText("" + c.getEnd2());
		lblEnd3.setText("" + c.getEnd3());
	}

}

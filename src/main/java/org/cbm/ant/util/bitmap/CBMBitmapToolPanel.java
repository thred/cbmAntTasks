package org.cbm.ant.util.bitmap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

public class CBMBitmapToolPanel extends JPanel
{

	private static final long serialVersionUID = 220399334599462582L;

	private final Map<String, AbstractCBMBitmapTool> tools = new HashMap<String, AbstractCBMBitmapTool>();

	private final JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
	private final JPanel toolPanel = new JPanel(new BorderLayout(8, 4));
	private final JLabel toolLabel = new JLabel();

	private AbstractCBMBitmapTool activeTool = null;

	public CBMBitmapToolPanel()
	{
		super(new BorderLayout());

		toolBar.setFloatable(false);
		toolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

		toolPanel.setVisible(false);
		toolPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 0, 8));

		toolLabel.setFont(toolLabel.getFont().deriveFont(Font.BOLD | Font.ITALIC,
				toolLabel.getFont().getSize2D() * 1.2f));
		toolLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

		toolPanel.add(toolLabel, BorderLayout.NORTH);

		add(toolBar, BorderLayout.NORTH);
		add(toolPanel, BorderLayout.CENTER);
	}

	public void addTool(Action action)
	{
		toolBar.add(action);
	}

	public void addSeparator()
	{
		toolBar.addSeparator();
	}

	public void addTool(AbstractCBMBitmapTool tool)
	{
		final String toolName = tool.getToolName();
		final JToggleButton button = new JToggleButton(tool.getToolIcon());

		button.setSelected(false);
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (button.isSelected())
				{
					select(toolName);
				}
				else
				{
					unselect(toolName);
				}
			}
		});

		toolBar.add(button);

		tools.put(toolName, tool);
	}

	public void select(String toolName)
	{
		if (activeTool != null)
		{
			toolPanel.remove(activeTool);
			toolPanel.setVisible(false);

			toolLabel.setText("");
			toolLabel.setIcon(null);
		}

		activeTool = tools.get(toolName);

		if (activeTool != null)
		{
			toolLabel.setText(activeTool.getToolName());
			toolLabel.setIcon(activeTool.getToolIcon());

			toolPanel.add(activeTool, BorderLayout.CENTER);
			toolPanel.setVisible(true);
		}

		CBMBitmapUtility.getFrame().validate();
		CBMBitmapUtility.getFrame().repaint();
	}

	protected void unselect(String toolName)
	{
		AbstractCBMBitmapTool tool = tools.get(toolName);

		if (activeTool == tool)
		{
			select(null);
		}
	}

	public void onCBMBitmapProjectUpdate(CBMBitmapProjectController controller, CBMBitmapProjectModel model,
			PropertyChangeEvent event)
	{
		for (AbstractCBMBitmapTool tool : tools.values())
		{
			tool.onCBMBitmapProjectUpdate(controller, model, event);
		}
	}
}

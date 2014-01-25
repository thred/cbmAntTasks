package org.cbm.ant.util.bitmap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
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

    private final Map<String, AbstractCBMBitmapToolAction> toolActions =
        new HashMap<String, AbstractCBMBitmapToolAction>();

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

    public void addTool(AbstractCBMBitmapToolAction toolAction)
    {
        final String toolId = toolAction.getId();
        final JToggleButton button = new JToggleButton(toolAction);

        toolBar.add(button);

        toolActions.put(toolId, toolAction);
    }

    public void toggleTool(String toolId)
    {
        AbstractCBMBitmapToolAction toolAction = toolActions.get(toolId);

        for (AbstractCBMBitmapToolAction currentToolAction : toolActions.values())
        {
            if (currentToolAction != toolAction)
            {
                currentToolAction.setSelected(false);
            }
        }

        if (toolAction.isSelected())
        {
            select(toolAction.getTool());
        }
        else
        {
            select(null);
        }
    }

    protected void select(AbstractCBMBitmapTool tool)
    {
        if (activeTool == tool)
        {
            return;
        }

        if (activeTool != null)
        {
            toolPanel.remove(activeTool);
            toolPanel.setVisible(false);

            toolLabel.setText("");
            toolLabel.setIcon(null);
        }

        activeTool = tool;

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

    public void onCBMBitmapProjectUpdate(CBMBitmapProjectController controller, CBMBitmapProjectModel model,
        PropertyChangeEvent event)
    {
        for (AbstractCBMBitmapToolAction toolAction : toolActions.values())
        {
            toolAction.getTool().onCBMBitmapProjectUpdate(controller, model, event);
        }
    }
}

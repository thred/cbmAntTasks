package org.cbm.ant.util.bitmap;

import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public abstract class AbstractCBMBitmapTool extends JPanel
{

    private static final long serialVersionUID = 4410090095128441106L;

    private final String toolIconResourceName;
    private final String toolName;
    private final String toolDescription;

    public AbstractCBMBitmapTool(String toolIconResourceName, String toolName, String toolDescription)
    {
        super(new FlowLayout(FlowLayout.LEFT, 8, 4));

        this.toolIconResourceName = toolIconResourceName;
        this.toolName = toolName;
        this.toolDescription = toolDescription;
    }

    public Icon getToolIcon()
    {
        return new ImageIcon(getClass().getResource(getToolIconResourceName()));
    }

    protected String getToolIconResourceName()
    {
        return toolIconResourceName;
    }

    public String getToolName()
    {
        return toolName;
    }

    public String getToolDescription()
    {
        return toolDescription;
    }

    public void onCBMBitmapProjectUpdate(CBMBitmapProjectController controller, CBMBitmapProjectModel model,
        PropertyChangeEvent event)
    {
        // intentionally left blank
    }

    protected CBMBitmapProjectController getActiveController()
    {
        return CBMBitmapUtility.getFrame().getActiveController();
    }

    protected CBMBitmapProjectModel getActiveModel()
    {
        CBMBitmapProjectController controller = getActiveController();

        return (controller != null) ? controller.getModel() : null;
    }
}

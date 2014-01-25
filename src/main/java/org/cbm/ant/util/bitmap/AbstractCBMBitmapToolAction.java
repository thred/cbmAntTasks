package org.cbm.ant.util.bitmap;

import java.awt.event.ActionEvent;

public abstract class AbstractCBMBitmapToolAction extends AbstractCBMBitmapAction
{

    private static final long serialVersionUID = 7092623979311437783L;

    private final AbstractCBMBitmapTool tool;

    public AbstractCBMBitmapToolAction(String id, String name, AbstractCBMBitmapTool tool)
    {
        super(id, name);

        this.tool = tool;

        setSelected(false);
    }

    public boolean isSelected()
    {
        return (Boolean) getValue(SELECTED_KEY);
    }

    public void setSelected(boolean selected)
    {
        putValue(SELECTED_KEY, selected);
    }

    public AbstractCBMBitmapTool getTool()
    {
        return tool;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        CBMBitmapUtility.getFrame().getToolPanel().toggleTool(getId());
    }
}

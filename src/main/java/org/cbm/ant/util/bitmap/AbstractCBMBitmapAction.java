package org.cbm.ant.util.bitmap;

import javax.swing.AbstractAction;
import javax.swing.Icon;

public abstract class AbstractCBMBitmapAction extends AbstractAction
{

    private static final long serialVersionUID = -5354813484313503845L;

    public AbstractCBMBitmapAction(String name, Icon icon)
    {
        super(name, icon);
    }

    public AbstractCBMBitmapAction(String name)
    {
        super(name);
    }

    public CBMBitmapProjectController getActiveController()
    {
        CBMBitmapFrame frame = CBMBitmapUtility.getFrame();

        return frame.getActiveController();
    }

    public void updateState()
    {
        setEnabled(computeEnabled());
    }

    protected abstract boolean computeEnabled();

}

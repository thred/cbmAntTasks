package org.cbm.ant.util.bitmap.tool;

public class CBMBitmapTargetSizeToolAction extends AbstractCBMBitmapToolAction
{

    private static final long serialVersionUID = 8728191278011953675L;

    public CBMBitmapTargetSizeToolAction()
    {
        super("target-size", "Target Size", new CBMBitmapTargetSizeTool());
    }

    @Override
    protected boolean computeEnabled()
    {
        return true;
    }

}

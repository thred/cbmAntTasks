package org.cbm.ant.util.bitmap;


public class CBMBitmapResizeSourceImageToolAction extends AbstractCBMBitmapToolAction
{

    private static final long serialVersionUID = 8728191278011953675L;

    public CBMBitmapResizeSourceImageToolAction()
    {
        super("resize-source-image", "Resize Source Image", new CBMBitmapResizeSourceImageTool());
    }

    @Override
    protected boolean computeEnabled()
    {
        return true;
    }

}

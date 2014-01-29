package org.cbm.ant.util.bitmap.tool;

public class CBMBitmapDitherModeToolAction extends AbstractCBMBitmapToolAction
{

    private static final long serialVersionUID = 8728191278011953675L;

    public CBMBitmapDitherModeToolAction()
    {
        super("dither-mode", "Dithering Mode", new CBMBitmapDitherModeTool());
    }

    @Override
    protected boolean computeEnabled()
    {
        return true;
    }

}

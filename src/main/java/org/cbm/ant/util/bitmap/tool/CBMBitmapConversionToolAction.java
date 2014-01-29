package org.cbm.ant.util.bitmap.tool;

public class CBMBitmapConversionToolAction extends AbstractCBMBitmapToolAction
{

    private static final long serialVersionUID = 8728191278011953675L;

    public CBMBitmapConversionToolAction()
    {
        super("conversion", "Conversion", new CBMBitmapConversionTool());
    }

    @Override
    protected boolean computeEnabled()
    {
        return true;
    }

}

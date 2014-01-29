package org.cbm.ant.util.bitmap.tool;


public class CBMBitmapContrastAndBrightnessToolAction extends AbstractCBMBitmapToolAction
{

    private static final long serialVersionUID = 8728191278011953675L;

    public CBMBitmapContrastAndBrightnessToolAction()
    {
        super("contrast-and-brightness", "Contrast and Brightness", new CBMBitmapContrastAndBrightnessTool());
    }

    @Override
    protected boolean computeEnabled()
    {
        return true;
    }

}

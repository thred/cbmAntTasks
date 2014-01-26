package org.cbm.ant.util.bitmap;

public class CBMBitmapPaletteToolAction extends AbstractCBMBitmapToolAction
{

    private static final long serialVersionUID = 8728191278011953675L;

    public CBMBitmapPaletteToolAction()
    {
        super("palette", "Palette", new CBMBitmapPaletteTool());
    }

    @Override
    protected boolean computeEnabled()
    {
        return true;
    }

}

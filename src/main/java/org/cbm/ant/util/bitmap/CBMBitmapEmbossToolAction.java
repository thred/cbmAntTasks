package org.cbm.ant.util.bitmap;

public class CBMBitmapEmbossToolAction extends AbstractCBMBitmapToolAction
{

    private static final long serialVersionUID = 8728191278011953675L;

    public CBMBitmapEmbossToolAction()
    {
        super("emboss", "Emboss", new CBMBitmapEmbossTool());
    }

    @Override
    protected boolean computeEnabled()
    {
        return true;
    }

}

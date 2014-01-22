package org.cbm.ant.util.bitmap;

import java.awt.event.ActionEvent;

public class CBMBitmapExitAction extends AbstractCBMBitmapAction
{

    private static final long serialVersionUID = 6114587570625893678L;

    public CBMBitmapExitAction()
    {
        super("Exit");
    }

    @Override
    protected boolean computeEnabled()
    {
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        CBMBitmapUtility.exit();
    }

}

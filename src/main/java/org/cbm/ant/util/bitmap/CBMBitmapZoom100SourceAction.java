package org.cbm.ant.util.bitmap;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class CBMBitmapZoom100SourceAction extends AbstractCBMBitmapAction
{

    private static final long serialVersionUID = 6114587570625893678L;

    public CBMBitmapZoom100SourceAction()
    {
        super("zoom-100", "Source Image Zoom 100%");

        putValue(MNEMONIC_KEY, KeyEvent.VK_1);
    }

    @Override
    protected boolean computeEnabled()
    {
        return getActiveController() != null;
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        CBMBitmapProjectController controller = getActiveController();

        if (controller == null)
        {
            return;
        }

        controller.setSourceZoom(1);
    }
}

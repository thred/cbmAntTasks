package org.cbm.ant.util.bitmap;

import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

public abstract class AbstractCBMBitmapAction extends AbstractAction
{

    private static final long serialVersionUID = -5354813484313503845L;

    private final String id;

    public AbstractCBMBitmapAction(String id, String name)
    {
        super(name);

        this.id = id;

        URL resource = CBMBitmapUtility.class.getResource(id + ".png");

        if (resource != null)
        {
            putValue(SMALL_ICON, new ImageIcon(resource));
        }
    }

    public String getId()
    {
        return id;
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

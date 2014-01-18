package org.cbm.ant.util.bitmap;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JScrollPane;

public class CBMBitmapProjectController
{

    private final CBMBitmapProjectModel model;
    private final CBMBitmapProjectView view;

    public CBMBitmapProjectController(CBMBitmapProjectModel model)
    {
        super();

        this.model = model;

        view = new CBMBitmapProjectView(model);
    }

    public CBMBitmapProjectModel getModel()
    {
        return model;
    }

    public CBMBitmapProjectView getView()
    {
        return view;
    }

    public void setSourceImage(File file, BufferedImage image)
    {
        model.setSourceImage(image);

        JScrollPane scrollPane = view.getSourceScrollPane();
        Dimension viewSize = scrollPane.getViewport().getSize();
        CBMBitmapCanvas canvas = view.getSourceCanvas();

        canvas.setImage(image);
        canvas.setZoom(Math.min(
            Math.min(viewSize.getWidth() / image.getWidth(), viewSize.getHeight() / image.getHeight()), 2));

        view.invalidate();
        view.repaint();
    }

    public void setZoom(double zoom)
    {
        view.getSourceCanvas().setZoom(zoom);
    }

    public void zoom(double multiplier)
    {
        view.getSourceCanvas().zoom(multiplier, null);
    }

    public void zoomFit()
    {
        CBMBitmapCanvas canvas = view.getSourceCanvas();
        zoomFit(canvas);
    }

    private void zoomFit(CBMBitmapCanvas canvas)
    {
        JScrollPane scrollPane = canvas.getScrollPane();

        Dimension viewSize = scrollPane.getViewport().getSize();
        BufferedImage image = canvas.getImage();

        if (image == null)
        {
            return;
        }

        canvas.setZoom(Math.min(viewSize.getWidth() / image.getWidth(), viewSize.getHeight() / image.getHeight()));
    }

}

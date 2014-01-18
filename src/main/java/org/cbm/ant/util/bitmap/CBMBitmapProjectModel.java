package org.cbm.ant.util.bitmap;

import java.awt.image.BufferedImage;

public class CBMBitmapProjectModel
{

    private String name;
    private BufferedImage sourceImage;
    private BufferedImage targetImage;

    public CBMBitmapProjectModel()
    {
        super();
        
        name = "Project";
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public BufferedImage getSourceImage()
    {
        return sourceImage;
    }

    public void setSourceImage(BufferedImage sourceImage)
    {
        this.sourceImage = sourceImage;
    }

    public BufferedImage getTargetImage()
    {
        return targetImage;
    }

    public void setTargetImage(BufferedImage targetImage)
    {
        this.targetImage = targetImage;
    }

}

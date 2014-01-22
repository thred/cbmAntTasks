package org.cbm.ant.util.bitmap;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.EventListenerList;

public class CBMBitmapProjectModel
{

    protected final EventListenerList listenerList = new EventListenerList();

    private String name;
    private BufferedImage sourceImage;
    private BufferedImage targetImage;

    public CBMBitmapProjectModel()
    {
        super();

        name = "Project";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        listenerList.add(PropertyChangeListener.class, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        listenerList.remove(PropertyChangeListener.class, listener);
    }

    private void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);

        for (PropertyChangeListener listener : listenerList.getListeners(PropertyChangeListener.class))
        {
            listener.propertyChange(event);
        }
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        if (!CBMBitmapUtils.equals(this.name, name))
        {
            Object old = this.name;

            this.name = name;

            firePropertyChange("name", old, name);
        }
    }

    public BufferedImage getSourceImage()
    {
        return sourceImage;
    }

    public void setSourceImage(BufferedImage sourceImage)
    {
        if (!CBMBitmapUtils.equals(this.sourceImage, sourceImage))
        {
            Object old = this.sourceImage;

            this.sourceImage = sourceImage;

            firePropertyChange("sourceImage", old, sourceImage);
        }
    }

    public BufferedImage getTargetImage()
    {
        return targetImage;
    }

    public void setTargetImage(BufferedImage targetImage)
    {
        if (!CBMBitmapUtils.equals(this.targetImage, targetImage))
        {
            Object old = this.targetImage;

            this.targetImage = targetImage;

            firePropertyChange("targetImage", old, targetImage);
        }
    }

}

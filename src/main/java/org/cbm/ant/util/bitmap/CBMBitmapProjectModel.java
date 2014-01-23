package org.cbm.ant.util.bitmap;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.EventListenerList;

import org.cbm.ant.util.CBMBitmapDither;

public class CBMBitmapProjectModel
{

	protected final EventListenerList listenerList = new EventListenerList();

	private String name;
	private BufferedImage sourceImage;
	private BufferedImage targetImage;

	private Integer targetWidth;
	private Integer targetHeight;
	private CBMBitmapDither dither = CBMBitmapDither.NONE;

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

	public Integer getTargetWidth()
	{
		return targetWidth;
	}

	public void setTargetWidth(Integer targetWidth)
	{
		if (!CBMBitmapUtils.equals(this.targetWidth, targetWidth))
		{
			Object old = this.targetWidth;

			this.targetWidth = targetWidth;

			firePropertyChange("targetWidth", old, targetWidth);
		}
	}

	public Integer getTargetHeight()
	{
		return targetHeight;
	}

	public void setTargetHeight(Integer targetHeight)
	{
		if (!CBMBitmapUtils.equals(this.targetHeight, targetHeight))
		{
			Object old = this.targetHeight;

			this.targetHeight = targetHeight;

			firePropertyChange("targetHeight", old, targetHeight);
		}
	}

	public CBMBitmapDither getDither()
	{
		return dither;
	}

	public void setDither(CBMBitmapDither dither)
	{
		if (!CBMBitmapUtils.equals(this.dither, dither))
		{
			Object old = this.dither;

			this.dither = dither;

			firePropertyChange("dither", old, dither);
		}
	}

}

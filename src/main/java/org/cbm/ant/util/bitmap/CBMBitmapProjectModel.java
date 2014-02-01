package org.cbm.ant.util.bitmap;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;

import org.cbm.ant.util.CBMBitmapDither;
import org.cbm.ant.util.CBMBitmapEmboss;
import org.cbm.ant.util.CBMColor;
import org.cbm.ant.util.CBMPalette;
import org.cbm.ant.util.CBMColorSpace;
import org.cbm.ant.util.GraphicsMode;
import org.cbm.ant.util.bitmap.util.CBMBitmapUtils;

public class CBMBitmapProjectModel
{

	private class PaletteEntry
	{
		private Color color;
		private CBMBitmapPaletteUsage usage;

		public PaletteEntry(CBMColor cbmColor)
		{
			super();

			color = CBMPalette.DEFAULT.color(cbmColor);
			usage = CBMBitmapPaletteUsage.OPTIONAL;
		}

		public Color getColor()
		{
			return color;
		}

		public void setColor(Color color)
		{
			if (!CBMBitmapUtils.equals(this.color, color))
			{
				Object old = this.color;

				this.color = color;

				firePropertyChange("paletteEntry.color", old, color);
			}
		}

		public CBMBitmapPaletteUsage getUsage()
		{
			return usage;
		}

		public void setUsage(CBMBitmapPaletteUsage usage)
		{
			if (!CBMBitmapUtils.equals(this.usage, usage))
			{
				Object old = this.usage;

				this.usage = usage;

				firePropertyChange("paletteEntry.usage", old, usage);
			}
		}
	}

	protected final EventListenerList listenerList = new EventListenerList();

	private final Map<CBMColor, PaletteEntry> paletteEntries = new HashMap<CBMColor, CBMBitmapProjectModel.PaletteEntry>();

	private String name;
	private BufferedImage sourceImage;
	private BufferedImage targetImage;

	private Integer targetWidth;
	private Integer targetHeight;
	private CBMBitmapDither dither = CBMBitmapDither.NONE;
	private float ditherStrength = 1;
	private CBMBitmapEmboss emboss = CBMBitmapEmboss.NONE;
	private float embossStrength = 1;
	private float contrastRed = 1;
	private float contrastGreen = 1;
	private float contrastBlue = 1;
	private float brightnessRed = 0;
	private float brightnessGreen = 0;
	private float brightnessBlue = 0;
	private CBMColorSpace colorSpace = CBMColorSpace.RGB;
	private GraphicsMode graphicsMode = GraphicsMode.LORES;

	public CBMBitmapProjectModel()
	{
		super();

		name = "Project";

		for (CBMColor palette : CBMColor.values())
		{
			paletteEntries.put(palette, new PaletteEntry(palette));
		}
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

	public float getDitherStrength()
	{
		return ditherStrength;
	}

	public void setDitherStrength(float ditherStrength)
	{
		if (!CBMBitmapUtils.equals(this.ditherStrength, ditherStrength))
		{
			Object old = this.ditherStrength;

			this.ditherStrength = ditherStrength;

			firePropertyChange("ditherStrength", old, ditherStrength);
		}
	}

	public CBMBitmapEmboss getEmboss()
	{
		return emboss;
	}

	public void setEmboss(CBMBitmapEmboss emboss)
	{
		if (!CBMBitmapUtils.equals(this.emboss, emboss))
		{
			Object old = this.emboss;

			this.emboss = emboss;

			firePropertyChange("emboss", old, emboss);
		}
	}

	public float getEmbossStrength()
	{
		return embossStrength;
	}

	public void setEmbossStrength(float embossStrength)
	{
		if (!CBMBitmapUtils.equals(this.embossStrength, embossStrength))
		{
			Object old = this.embossStrength;

			this.embossStrength = embossStrength;

			firePropertyChange("embossStrength", old, embossStrength);
		}
	}

	public float getContrastRed()
	{
		return contrastRed;
	}

	public void setContrastRed(float contrastRed)
	{
		if (!CBMBitmapUtils.equals(this.contrastRed, contrastRed))
		{
			Object old = this.contrastRed;

			this.contrastRed = contrastRed;

			firePropertyChange("contrastRed", old, contrastRed);
		}
	}

	public float getContrastGreen()
	{
		return contrastGreen;
	}

	public void setContrastGreen(float contrastGreen)
	{
		if (!CBMBitmapUtils.equals(this.contrastGreen, contrastGreen))
		{
			Object old = this.contrastGreen;

			this.contrastGreen = contrastGreen;

			firePropertyChange("contrastGreen", old, contrastGreen);
		}
	}

	public float getContrastBlue()
	{
		return contrastBlue;
	}

	public void setContrastBlue(float contrastBlue)
	{
		if (!CBMBitmapUtils.equals(this.contrastBlue, contrastBlue))
		{
			Object old = this.contrastBlue;

			this.contrastBlue = contrastBlue;

			firePropertyChange("contrastBlue", old, contrastBlue);
		}
	}

	public float getBrightnessRed()
	{
		return brightnessRed;
	}

	public void setBrightnessRed(float brightnessRed)
	{
		if (!CBMBitmapUtils.equals(this.brightnessRed, brightnessRed))
		{
			Object old = this.brightnessRed;

			this.brightnessRed = brightnessRed;

			firePropertyChange("brightnessRed", old, brightnessRed);
		}
	}

	public float getBrightnessGreen()
	{
		return brightnessGreen;
	}

	public void setBrightnessGreen(float brightnessGreen)
	{
		if (!CBMBitmapUtils.equals(this.brightnessGreen, brightnessGreen))
		{
			Object old = this.brightnessGreen;

			this.brightnessGreen = brightnessGreen;

			firePropertyChange("brightnessGreen", old, brightnessGreen);
		}
	}

	public float getBrightnessBlue()
	{
		return brightnessBlue;
	}

	public void setBrightnessBlue(float brightnessBlue)
	{
		if (!CBMBitmapUtils.equals(this.brightnessBlue, brightnessBlue))
		{
			Object old = this.brightnessBlue;

			this.brightnessBlue = brightnessBlue;

			firePropertyChange("contrastRed", old, brightnessBlue);
		}
	}

	public Color getPaletteColor(CBMColor cbmColor)
	{
		return paletteEntries.get(cbmColor).getColor();
	}

	public void setPaletteColor(CBMColor cbmColor, Color color)
	{
		paletteEntries.get(cbmColor).setColor(color);
	}

	public CBMBitmapPaletteUsage getPaletteUsage(CBMColor cbmColor)
	{
		return paletteEntries.get(cbmColor).getUsage();
	}

	public void setPaletteUsage(CBMColor cbmColor, CBMBitmapPaletteUsage usage)
	{
		paletteEntries.get(cbmColor).setUsage(usage);
	}

	public CBMColor[] getAllowedColors()
	{
		List<CBMColor> result = new ArrayList<CBMColor>();

		for (CBMColor cbmColor : CBMColor.values())
		{
			PaletteEntry paletteEntry = paletteEntries.get(cbmColor);

			if (paletteEntry.getUsage() == CBMBitmapPaletteUsage.DO_NOT_USE)
			{
				continue;
			}

			result.add(cbmColor);
		}

		return result.toArray(new CBMColor[result.size()]);
	}

	public CBMColor[] getMandatoryColors()
	{
		List<CBMColor> result = new ArrayList<CBMColor>();

		for (CBMColor cbmColor : CBMColor.values())
		{
			PaletteEntry paletteEntry = paletteEntries.get(cbmColor);

			if (paletteEntry.getUsage() == CBMBitmapPaletteUsage.MANDATORY)
			{
				result.add(cbmColor);
			}
		}

		return result.toArray(new CBMColor[result.size()]);
	}

	public CBMPalette createEsitmationPalette()
	{
		CBMPalette palette = CBMPalette.DEFAULT.copy();

		for (CBMColor cbmColor : CBMColor.values())
		{
			palette.setFromColor(cbmColor, paletteEntries.get(cbmColor).getColor());
		}

		return palette;
	}

	public CBMColorSpace getColorSpace()
	{
		return colorSpace;
	}

	public void setColorSpace(CBMColorSpace colorSpace)
	{
		if (!CBMBitmapUtils.equals(this.colorSpace, colorSpace))
		{
			Object old = this.colorSpace;

			this.colorSpace = colorSpace;

			firePropertyChange("colorspace", old, colorSpace);
		}
	}

	public GraphicsMode getGraphicsMode()
	{
		return graphicsMode;
	}

	public void setGraphicsMode(GraphicsMode graphicsMode)
	{
		if (!CBMBitmapUtils.equals(this.graphicsMode, graphicsMode))
		{
			Object old = this.graphicsMode;

			this.graphicsMode = graphicsMode;

			firePropertyChange("graphicsmode", old, graphicsMode);
		}
	}

}

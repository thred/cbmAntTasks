package org.cbm.ant.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CBMBitmap
{

	private static class Raster
	{

		private final Palette[][] raster;
		private final int width;

		public Raster(int blockWidth, int blockHeight)
		{
			super();

			raster = new Palette[blockWidth * blockHeight][0];

			this.width = blockWidth;
		}

		public void set(int x, int y, Palette[] palette)
		{
			raster[x + (y * width)] = palette;
		}

		public Palette[] get(int blockX, int blockY)
		{
			return raster[blockX + (blockY * width)];
		}

		public void add(int blockX, int blockY, Palette palette)
		{
			Palette[] existing = raster[blockX + (blockY * width)];

			for (Palette current : existing)
			{
				if (current == palette)
				{
					return;
				}
			}

			existing = Arrays.copyOf(existing, existing.length + 1);
			existing[existing.length - 1] = palette;

			raster[blockX + (blockY * width)] = existing;
		}

	}

	private static class Entry implements Comparable<Entry>
	{
		private final Palette color;
		private int count;

		public Entry(Palette color)
		{
			this(color, 0);
		}

		public Entry(Palette color, int count)
		{
			super();

			this.color = color;
			this.count = count;
		}

		public Palette getColor()
		{
			return color;
		}

		public int getCount()
		{
			return count;
		}

		public void incCount()
		{
			count += 1;
		}

		@Override
		public int compareTo(Entry o)
		{
			return o.getCount() - count;
		}

		@Override
		public String toString()
		{
			return String.format("#%6s/%2d", Integer.toHexString(color.rgb()), count);
		}

	}

	private BufferedImage image;
	private int x = 0;
	private int y = 0;
	private int width = -1;
	private int height = -1;

	private int targetWidth = -1;
	private int targetHeight = -1;

	private int scaledTargetWidth;

	private int blockWidth = -1;
	private int blockHeight = -1;

	private int scaledBlockWidth;

	private GraphicsMode mode = GraphicsMode.LORES;
	private boolean dither = false;
	private boolean antiAlias = false;
	private boolean yuv = true;
	private int overscan = 0;
	private float contrast = 1;
	private Palette[] palette = Palette.values();
	private Palette[] mandatoryPalette = new Palette[0];

	private boolean updated = true;
	private Raster raster = null;
	private BufferedImage targetImage = null;

	public CBMBitmap()
	{
		super();
	}

	public CBMBitmap image(BufferedImage image)
	{
		this.image = image;

		updated = true;

		return this;
	}

	public Image getImage()
	{
		return image;
	}

	public void setImage(BufferedImage image)
	{
		updated = true;

		this.image = image;
	}

	public CBMBitmap area(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		updated = true;

		return this;
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		updated = true;

		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		updated = true;

		this.y = y;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		updated = true;

		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		updated = true;

		this.height = height;
	}

	public CBMBitmap targetSize(int targetWidth, int targetHeight)
	{
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;

		updated = true;

		return this;
	}

	public int getTargetWidth()
	{
		return targetWidth;
	}

	public void setTargetWidth(int targetWidth)
	{
		updated = true;

		this.targetWidth = targetWidth;
	}

	public int getTargetHeight()
	{
		return targetHeight;
	}

	public void setTargetHeight(int targetHeight)
	{
		updated = true;

		this.targetHeight = targetHeight;
	}

	public int getBlockWidth()
	{
		return blockWidth;
	}

	public void setBlockWidth(int blockWidth)
	{
		updated = true;

		this.blockWidth = blockWidth;
	}

	public int getBlockHeight()
	{
		return blockHeight;
	}

	public void setBlockHeight(int blockHeight)
	{
		updated = true;

		this.blockHeight = blockHeight;
	}

	public CBMBitmap blockSize(int blockWidth, int blockHeight)
	{
		this.blockWidth = blockWidth;
		this.blockHeight = blockHeight;

		updated = true;

		return this;
	}

	public GraphicsMode getMode()
	{
		return mode;
	}

	public void setMode(GraphicsMode mode)
	{
		updated = true;

		this.mode = mode;
	}

	public CBMBitmap mode(GraphicsMode mode)
	{
		this.mode = mode;

		updated = true;

		return this;
	}

	public CBMBitmap lores()
	{
		return mode(GraphicsMode.LORES);
	}

	public CBMBitmap hires()
	{
		return mode(GraphicsMode.HIRES);
	}

	public boolean isDither()
	{
		return dither;
	}

	public void setDither(boolean dither)
	{
		updated = true;

		this.dither = dither;
	}

	public CBMBitmap dither()
	{
		dither = true;

		updated = true;

		return this;
	}

	public boolean isAntiAlias()
	{
		return antiAlias;
	}

	public void setAntiAlias(boolean antiAlias)
	{
		updated = true;

		this.antiAlias = antiAlias;
	}

	public CBMBitmap antiAlias()
	{
		antiAlias = true;

		updated = true;

		return this;
	}

	public boolean isYuv()
	{
		return yuv;
	}

	public void setYuv(boolean yuv)
	{
		updated = true;

		this.yuv = yuv;
	}

	public CBMBitmap useYUV()
	{
		yuv = true;

		updated = true;

		return this;
	}

	public CBMBitmap useRGB()
	{
		yuv = false;

		updated = true;

		return this;
	}

	public int getOverscan()
	{
		return overscan;
	}

	public void setOverscan(int overscan)
	{
		updated = true;

		this.overscan = overscan;
	}

	public CBMBitmap overscan(int overscan)
	{
		this.overscan = overscan;

		updated = true;

		return this;
	}

	public float getContrast()
	{
		return contrast;
	}

	public void setContrast(float contrast)
	{
		this.contrast = contrast;

		updated = true;
	}

	public CBMBitmap contrast(float contrast)
	{
		setContrast(contrast);

		return this;
	}

	public Palette[] getPalette()
	{
		return palette;
	}

	public void setPalette(String palette)
	{
		String[] values = palette.split("\\s*,\\s*");
		Palette[] colors = new Palette[values.length];

		for (int i = 0; i < values.length; i += 1)
		{
			colors[i] = Palette.valueOf(values[i].toUpperCase());
		}

		setPalette(colors);
	}

	public void setPalette(Palette... palette)
	{
		this.palette = palette;

		updated = true;
	}

	public CBMBitmap palette(String palette)
	{
		setPalette(palette);

		return this;
	}

	public CBMBitmap palette(Palette... palette)
	{
		setPalette(palette);

		return this;
	}

	public Palette[] getMandatoryPalette()
	{
		return mandatoryPalette;
	}

	public void setMandatoryPalette(String mandatoryPalette)
	{
		String[] values = mandatoryPalette.split("\\s*,\\s*");
		Palette[] colors = new Palette[values.length];

		for (int i = 0; i < values.length; i += 1)
		{
			colors[i] = Palette.valueOf(values[i].toUpperCase());
		}

		setMandatoryPalette(colors);
	}

	public void setMandatoryPalette(Palette... mandatoryPalette)
	{
		this.mandatoryPalette = mandatoryPalette;

		updated = true;
	}

	public CBMBitmap mandatoryPalette(String mandatoryPalette)
	{
		setMandatoryPalette(mandatoryPalette);

		return this;
	}

	public CBMBitmap mandatoryPalette(Palette... mandatoryPalette)
	{
		setMandatoryPalette(mandatoryPalette);

		return this;
	}

	protected void update()
	{
		if (!updated)
		{
			return;
		}

		if (width < 1)
		{
			width = image.getWidth() - x;
		}

		if (height < 1)
		{
			height = image.getHeight() - y;
		}

		if (targetWidth < 1)
		{
			targetWidth = width;
		}

		if (targetHeight < 1)
		{
			targetHeight = height;
		}

		if (blockWidth < 1)
		{
			blockWidth = mode.getWidthPerChar();
		}

		if (blockHeight < 1)
		{
			blockHeight = mode.getHeightPerChar();
		}
		targetWidth -= targetWidth % blockWidth;
		targetHeight -= targetHeight % blockHeight;
		scaledTargetWidth = targetWidth / mode.getBitPerColor();
		scaledBlockWidth = blockWidth / mode.getBitPerColor();

		BufferedImage scaledImage = createScaledImage(image, x, y, width, height, scaledTargetWidth, targetHeight,
				antiAlias);

		if (contrast != 1)
		{
			scaledImage = normalize(scaledImage);
		}

		BufferedImage estimationImage = createImage(scaledImage, dither, null, scaledBlockWidth, blockHeight,
				mode, palette);

		raster = createRaster(estimationImage, Arrays.asList(mandatoryPalette), overscan);
		targetImage = createImage(scaledImage, dither, raster, scaledBlockWidth, blockHeight, mode, palette);
		
		updated = false;
	}

	public Raster getRaster()
	{
		update();

		return raster;
	}

	public BufferedImage getTargetImage()
	{
		update();

		return targetImage;
	}

	public BufferedImage getSampleImage()
	{
		BufferedImage targetImage = getTargetImage();
		BufferedImage sampleImage = new BufferedImage(targetWidth, targetHeight + 10, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = sampleImage.createGraphics();

		g.drawImage(targetImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_FAST), 0, 0, null);

		for (int i = 0; i < palette.length; i += 1)
		{
			int x = (targetWidth * i) / palette.length;

			g.setColor(new Color(palette[i].rgb()));
			g.fillRect(x, targetHeight, ((targetWidth * (i + 1)) / palette.length) - x, 10);
		}

		return sampleImage;
	}

	public byte[] getBitmapData()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try
		{
			try
			{
				writeBitmapData(out);
			}
			finally
			{
				out.close();
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException("Failed to write to byte array", e);
		}

		return out.toByteArray();
	}

	public int writeBitmapData(OutputStream out) throws IOException
	{
		int count = 0;
		Raster raster = getRaster();
		BufferedImage targetImage = getTargetImage();

		for (int y = 0; y < targetHeight; y += mode.getHeightPerChar())
		{
			for (int x = 0; x < scaledTargetWidth; x += mode.getWidthPerChar())
			{
				Palette[] colors = raster.get(x / mode.getWidthPerChar(), y / mode.getHeightPerChar());

				for (int innerY = y; innerY < (y + blockHeight); innerY += 1)
				{
					int value = 0;

					for (int innerX = x; innerX < (x + mode.getWidthPerChar()); innerX += 1)
					{
						int index = Palette.indexOf(colors, targetImage.getRGB(innerX, innerY), false, 1, 1, 1);

						if (mode == GraphicsMode.LORES)
						{
							if ((index < 0) || (index > 3))
							{
								throw new RuntimeException("This should not happen");
							}

							value <<= 2;
							value |= index;
						}
						else
						{
							if ((index < 0) || (index > 1))
							{
								throw new RuntimeException("This should not happen");
							}

							value <<= 1;
							value |= index;
						}
					}

					out.write(value);
					count += 1;
				}
			}
		}

		return count;
	}

	public int writeSpriteData(OutputStream out) throws IOException
	{
		int count = 0;
		Raster raster = getRaster();
		BufferedImage targetImage = getTargetImage();

		for (int y = 0; y < targetHeight; y += blockHeight)
		{
			for (int x = 0; x < scaledTargetWidth; x += scaledBlockWidth)
			{
				Palette[] colors = raster.get(x / scaledBlockWidth, y / blockHeight);

				for (int innerY = y; innerY < (y + blockHeight); innerY += 1)
				{
					int value = 0;

					for (int innerX = x; innerX < (x + scaledBlockWidth); innerX += 8 / mode.getBitPerColor())
					{
						for (int byteX = innerX; byteX < (innerX + 8 / mode.getBitPerColor()); byteX += 1)
						{
							int index = Palette.indexOf(colors, targetImage.getRGB(byteX, innerY), false, 1, 1, 1);
							
							if (mode == GraphicsMode.LORES)
							{
								if ((index < 0) || (index > 3))
								{
									throw new RuntimeException("This should not happen");
								}

								value <<= 2;
								value |= index;
							}
							else
							{
								if ((index < 0) || (index > 1))
								{
									throw new RuntimeException("This should not happen");
								}

								value <<= 1;
								value |= index;
							}
						}

						out.write(value);
						count += 1;
					}
				}
			}
		}

		return count;
	}

	public byte[] getCharacterData()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try
		{
			try
			{
				writeCharacterData(out);
			}
			finally
			{
				out.close();
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException("Failed to write to byte array", e);
		}

		return out.toByteArray();
	}

	public int writeCharacterData(OutputStream out) throws IOException
	{
		Raster raster = getRaster();

		for (int y = 0; y < (targetHeight / mode.getHeightPerChar()); y += 1)
		{
			for (int x = 0; x < (scaledTargetWidth / mode.getWidthPerChar()); x += 1)
			{
				Palette[] colors = raster.get(x, y);

				if (mode == GraphicsMode.LORES)
				{
					out.write(((colors.length > 2) ? colors[2].getIndex() : 0)
							| (((colors.length > 1) ? colors[1].getIndex() : 0) << 4));
				}
				else
				{
					out.write(((colors.length > 0) ? colors[0].getIndex() : 0)
							| (((colors.length > 1) ? colors[1].getIndex() : 0) << 4));
				}
			}
		}

		return (targetHeight / mode.getHeightPerChar()) * (scaledTargetWidth / mode.getWidthPerChar());
	}

	public byte[] getColorData()
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try
		{
			try
			{
				writeColorData(out);
			}
			finally
			{
				out.close();
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException("Failed to write to byte array", e);
		}

		return out.toByteArray();
	}

	public int writeColorData(OutputStream out) throws IOException
	{
		Raster raster = getRaster();

		if (mode == GraphicsMode.LORES)
		{
			for (int y = 0; y < (targetHeight / mode.getHeightPerChar()); y += 1)
			{
				for (int x = 0; x < (scaledTargetWidth / mode.getWidthPerChar()); x += 1)
				{
					Palette[] colors = raster.get(x, y);

					out.write((colors.length > 3) ? colors[3].getIndex() : 0);
				}
			}
		}

		return (targetHeight / mode.getHeightPerChar()) * (scaledTargetWidth / mode.getWidthPerChar());
	}

	private BufferedImage normalize(BufferedImage image)
	{
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;

		for (int y = 0; y < image.getHeight(); y += 1)
		{
			for (int x = 0; x < image.getWidth(); x += 1)
			{
				int yuv = Palette.rgb2yuv(image.getRGB(x, y));
				int yChannel = (yuv >> 16) & 0xff;

				min = Math.min(yChannel, min);
				max = Math.max(yChannel, max);
			}
		}

		double delta = 255d / (max - min);

		for (int y = 0; y < image.getHeight(); y += 1)
		{
			for (int x = 0; x < image.getWidth(); x += 1)
			{
				int yuv = Palette.rgb2yuv(image.getRGB(x, y));
				int yChannel = (yuv >> 16) & 0xff;
				int uChannel = (yuv >> 8) & 0xff;
				int vChannel = yuv & 0xff;

				yChannel = (int) ((yChannel - min) * delta);

				image.setRGB(x, y, Palette.yuv2rgb((yChannel << 16) | (uChannel << 8) | vChannel));
			}
		}

		return image;
	}

	private Raster createRaster(BufferedImage estimationImage, List<Palette> mandatoryPalette, int overscan)
	{
		Raster raster = new Raster(targetWidth / scaledBlockWidth, targetHeight / blockHeight);

		for (int y = 0; y < estimationImage.getHeight(); y += blockHeight)
		{
			for (int x = 0; x < estimationImage.getWidth(); x += scaledBlockWidth)
			{
				Palette[] palette = detectPalette(estimationImage, x, y, scaledBlockWidth, blockHeight,
						mode.getNumberOfColors(), mandatoryPalette, overscan, overscan);

				raster.set(x / scaledBlockWidth, y / blockHeight, palette);
			}
		}

		return raster;
	}

	private Palette[] detectPalette(BufferedImage image, int x, int y, int w, int h, int numberOfColors,
			List<Palette> mandatoryPalette, int xOverscan, int yOverscan)
	{
		Map<Palette, Entry> counts = new HashMap<Palette, Entry>();

		for (int j = -yOverscan; j < (h + yOverscan); j += 1)
		{
			for (int i = -xOverscan; i < (w + xOverscan); i += 1)
			{
				if (((y + j) < 0) || ((y + j) >= image.getHeight()) || ((x + i) < 0) || ((x + i) >= image.getWidth()))
				{
					continue;
				}

				Palette color = Palette.toPalette(image.getRGB(x + i, y + j) & 0x00ffffff, false, 1, 1, 1);
				Entry entry = counts.get(color);

				if (entry == null)
				{
					entry = new Entry(color);
					counts.put(color, entry);
				}

				entry.incCount();
			}
		}

		List<Palette> palette = new ArrayList<Palette>(mandatoryPalette);

		for (Palette current : mandatoryPalette)
		{
			counts.remove(current);
		}

		List<Entry> entries = new ArrayList<Entry>(counts.values());
		Collections.sort(entries);

		for (Entry entry : entries)
		{
			palette.add(entry.getColor());
		}

		return toPaletteArray(palette, numberOfColors);
	}

	private static BufferedImage createScaledImage(BufferedImage image, int x, int y, int width, int height,
			int scaledTargetWidth, int targetHeight, boolean antiAlias)
	{
		BufferedImage scaledImage = new BufferedImage(scaledTargetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = scaledImage.createGraphics();

		if (antiAlias)
		{
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		}
		else
		{
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		}

		g.drawImage(image, 0, 0, scaledTargetWidth, targetHeight, x, y, x + width, y + height, null);

		return scaledImage;
	}

	private static BufferedImage createImage(BufferedImage image, boolean dither, Raster raster, int blockWidth,
			int blockHeight, GraphicsMode mode, Palette... palette)
	{
		BufferedImage source = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

		source.getGraphics().drawImage(image, 0, 0, null);

		BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

		for (int y = 0; y < source.getHeight(); y += 1)
		{
			for (int x = 0; x < source.getWidth(); x += 1)
			{
				int sourceRGB = source.getRGB(x, y);
				Palette targetPalette = null;

				if (raster != null)
				{
					Palette[] possiblePalette = raster.get(x / blockWidth, y / blockHeight);

					if ((possiblePalette == null) || (possiblePalette.length < mode.getNumberOfColors()))
					{
						targetPalette = Palette.toPalette(palette, sourceRGB, true, 1, 1, 1);

						raster.add(x / blockWidth, y / blockHeight, targetPalette);
					}
					else
					{
						targetPalette = Palette.toPalette(possiblePalette, sourceRGB, true, 1, 1, 1);
					}
				}
				else
				{
					targetPalette = Palette.toPalette(palette, sourceRGB, true, 1, 1, 1);
				}

				int targetRGB = targetPalette.rgb();

				result.setRGB(x, y, targetRGB);

				if (dither)
				{
					int[] error = {
							((sourceRGB >> 16) & 0xff) - ((targetRGB >> 16) & 0xff),
							((sourceRGB >> 8) & 0xff) - ((targetRGB >> 8) & 0xff),
							(sourceRGB & 0xff) - (targetRGB & 0xff)
					};

					if (x < (source.getWidth() - 1))
					{
						source.setRGB(x + 1, y, apply(source.getRGB(x + 1, y), error, 7d / 16));
					}

					if (y < (source.getHeight() - 1))
					{
						if (x > 0)
						{
							source.setRGB(x - 1, y + 1, apply(source.getRGB(x - 1, y + 1), error, 3d / 16));
						}

						source.setRGB(x, y + 1, apply(source.getRGB(x, y + 1), error, 5d / 16));

						if (x > (source.getWidth() - 1))
						{
							source.setRGB(x + 1, y + 1, apply(source.getRGB(x + 1, y + 1), error, 1d / 16));
						}
					}
				}
			}
		}

		return result;
	}

	private static Palette[] toPaletteArray(List<Palette> palette, int max)
	{
		Palette[] result = new Palette[Math.min(palette.size(), max)];

		for (int i = 0; (i < palette.size()) && (i < max); i += 1)
		{
			result[i] = palette.get(i);
		}

		return result;
	}

	private static int apply(int rgb, int[] error, double coefficient)
	{
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;

		r = Palette.range(0, (int) (r + (coefficient * error[0])), 255);
		g = Palette.range(0, (int) (g + (coefficient * error[1])), 255);
		b = Palette.range(0, (int) (b + (coefficient * error[2])), 255);

		return (r << 16) + (g << 8) + b;
	}

}

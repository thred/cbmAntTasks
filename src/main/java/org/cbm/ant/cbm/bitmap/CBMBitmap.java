package org.cbm.ant.cbm.bitmap;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CBMBitmap
{

	private static class Raster
	{

		private final CBMColor[][] raster;
		private final int width;

		public Raster(int blockWidth, int blockHeight)
		{
			super();

			raster = new CBMColor[blockWidth * blockHeight][0];

			width = blockWidth;
		}

		public void set(int x, int y, CBMColor[] colors)
		{
			raster[x + (y * width)] = colors;
		}

		public CBMColor[] get(int blockX, int blockY)
		{
			return raster[blockX + (blockY * width)];
		}

		public void add(int blockX, int blockY, CBMColor color)
		{
			CBMColor[] existing = raster[blockX + (blockY * width)];

			for (CBMColor current : existing)
			{
				if (current == color)
				{
					return;
				}
			}

			existing = Arrays.copyOf(existing, existing.length + 1);
			existing[existing.length - 1] = color;

			raster[blockX + (blockY * width)] = existing;
		}

	}

	private static class Entry implements Comparable<Entry>
	{
		private final CBMColor color;
		private int count;

		public Entry(CBMColor color)
		{
			this(color, 0);
		}

		public Entry(CBMColor color, int count)
		{
			super();

			this.color = color;
			this.count = count;
		}

		public CBMColor getColor()
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
	private CBMBitmapDither dither = CBMBitmapDither.NONE;
	private float ditherStrength = 1;
	private CBMBitmapEmboss emboss = CBMBitmapEmboss.NONE;
	private float embossStrength = 1;
	private boolean drawSamplePalette = true;
	private int overscan = 0;
	private float[] contrast;
	private float[] brightness;
	private CBMColor[] allowedColors = CBMColor.values();
	private CBMColor[] mandatoryColors = new CBMColor[0];
	private CBMPalette estimationPalette = CBMPalette.DEFAULT;
	private CBMPalette samplePalette = CBMPalette.DEFAULT;
	private CBMColorSpace colorSpace = CBMColorSpace.RGB;

	private boolean updated = true;
	private Raster raster = null;
	private CBMImage targetImage = null;
	private BufferedImage scaledImage = null;

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

	public CBMBitmapDither getDither()
	{
		return dither;
	}

	public void setDither(CBMBitmapDither dither)
	{
		updated = true;

		this.dither = dither;
	}

	public CBMBitmap dither(CBMBitmapDither dither)
	{
		setDither(dither);

		return this;
	}

	public float getDitherStrength()
	{
		return ditherStrength;
	}

	public void setDitherStrength(float ditherStrength)
	{
		updated = true;

		this.ditherStrength = ditherStrength;
	}

	public CBMBitmapEmboss getEmboss()
	{
		return emboss;
	}

	public void setEmboss(CBMBitmapEmboss emboss)
	{
		updated = true;

		this.emboss = emboss;
	}

	public CBMBitmap emboss(CBMBitmapEmboss emboss)
	{
		setEmboss(emboss);

		return this;
	}

	public float getEmbossStrength()
	{
		return embossStrength;
	}

	public void setEmbossStrength(float embossStrength)
	{
		updated = true;

		this.embossStrength = embossStrength;
	}

	public boolean isSamplePalette()
	{
		return drawSamplePalette;
	}

	public void setSamplePalette(boolean drawSamplePalette)
	{
		this.drawSamplePalette = drawSamplePalette;
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

	public float[] getContrast()
	{
		return contrast;
	}

	public void setContrast(float[] contrast)
	{
		updated = true;

		this.contrast = contrast;
	}

	public CBMBitmap contrast(float[] contrast)
	{
		setContrast(contrast);

		return this;
	}

	public float[] getBrightness()
	{
		return brightness;
	}

	public void setBrightness(float[] brightness)
	{
		updated = true;

		this.brightness = brightness;
	}

	public CBMBitmap brightness(float[] brightness)
	{
		setBrightness(brightness);

		return this;
	}

	public CBMColor[] getAllowedColors()
	{
		return allowedColors;
	}

	public void setAllowedColors(String palette)
	{
		String[] values = palette.split("\\s*,\\s*");
		CBMColor[] colors = new CBMColor[values.length];

		for (int i = 0; i < values.length; i += 1)
		{
			colors[i] = CBMColor.valueOf(values[i].toUpperCase());
		}

		setAllowedColors(colors);
	}

	public void setAllowedColors(CBMColor... allowedColors)
	{
		this.allowedColors = allowedColors;

		updated = true;
	}

	public CBMBitmap allowedColors(String allowedColors)
	{
		setAllowedColors(allowedColors);

		return this;
	}

	public CBMBitmap allowedColors(CBMColor... allowedColors)
	{
		setAllowedColors(allowedColors);

		return this;
	}

	public CBMColor[] getMandatoryColors()
	{
		return mandatoryColors;
	}

	public void setMandatoryColors(String mandatoryColors)
	{
		String[] values = mandatoryColors.split("\\s*,\\s*");
		CBMColor[] colors = new CBMColor[values.length];

		for (int i = 0; i < values.length; i += 1)
		{
			colors[i] = CBMColor.valueOf(values[i].toUpperCase());
		}

		setMandatoryColors(colors);
	}

	public void setMandatoryColors(CBMColor... mandatoryColors)
	{
		this.mandatoryColors = mandatoryColors;

		updated = true;
	}

	public CBMBitmap mandatoryColors(String mandatoryColors)
	{
		setMandatoryColors(mandatoryColors);

		return this;
	}

	public CBMBitmap mandatoryColors(CBMColor... mandatoryColors)
	{
		setMandatoryColors(mandatoryColors);

		return this;
	}

	public CBMPalette getEstimationPalette()
	{
		return estimationPalette;
	}

	public void setEstimationPalette(CBMPalette estimationPalette)
	{
		this.estimationPalette = estimationPalette;
	}

	public CBMPalette getSamplePalette()
	{
		return samplePalette;
	}

	public void setSamplePalette(CBMPalette samplePalette)
	{
		this.samplePalette = samplePalette;
	}

	public CBMColorSpace getColorSpace()
	{
		return colorSpace;
	}

	public void setColorSpace(CBMColorSpace colorSpace)
	{
		updated = true;

		this.colorSpace = colorSpace;
	}

	public CBMBitmap colorSpace(CBMColorSpace colorSpace)
	{
		setColorSpace(colorSpace);

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

		scaledImage = createScaledImage(image, x, y, width, height, scaledTargetWidth, targetHeight);

		if ((contrast != null) || (brightness != null))
		{
			float[] c = {
					1f, 1f, 1f
			};
			float[] b = {
					0f, 0f, 0f
			};

			if (contrast != null)
			{
				if (contrast.length < 3)
				{
					c[0] = contrast[0];
					c[1] = contrast[0];
					c[2] = contrast[0];
				}
				else
				{
					c[0] = contrast[0];
					c[1] = contrast[1];
					c[2] = contrast[2];
				}
			}

			if (brightness != null)
			{
				if (brightness.length < 3)
				{
					b[0] = brightness[0] * 256;
					b[1] = brightness[0] * 256;
					b[2] = brightness[0] * 256;
				}
				else
				{
					b[0] = brightness[0] * 256;
					b[1] = brightness[1] * 256;
					b[2] = brightness[2] * 256;
				}
			}

			RescaleOp rescaleOp = new RescaleOp(c, b, null);
			rescaleOp.filter(scaledImage, scaledImage);
		}

		if (emboss != CBMBitmapEmboss.NONE)
		{
			BufferedImage[] yuvImages = CBMPalette.rgb2yuv(scaledImage);

			Kernel kernel = new Kernel(3, 3, emboss.getMask(embossStrength));
			BufferedImageOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
			yuvImages[0] = op.filter(yuvImages[0], null);

			scaledImage = CBMPalette.yuv2rgb(yuvImages);
		}

		CBMImage estimationImage = createImage(scaledImage, colorSpace, estimationPalette, allowedColors, mode, scaledBlockWidth,
				blockHeight, null, dither, ditherStrength);

		raster = createRaster(estimationImage, estimationPalette, Arrays.asList(mandatoryColors), overscan);
		targetImage = createImage(scaledImage, colorSpace, estimationPalette, allowedColors, mode, scaledBlockWidth,
				blockHeight, raster, dither, ditherStrength);

		updated = false;
	}

	public Raster getRaster()
	{
		update();

		return raster;
	}

	public CBMImage getTargetImage()
	{
		update();

		return targetImage;
	}

	public BufferedImage getSampleImage()
	{
		CBMImage targetImage = getTargetImage();
		int samplePaletteHeight = Math.min(targetHeight / 8, 32);

		if (drawSamplePalette)
		{
			height += samplePaletteHeight;
		}

		Set<CBMColor> usedColors = new HashSet<CBMColor>();
		BufferedImage resultImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = resultImage.createGraphics();

		g.drawImage(
				targetImage.toImage(estimationPalette, usedColors).getScaledInstance(targetWidth, targetHeight,
						Image.SCALE_FAST), 0, 0, null);

		if (drawSamplePalette)
		{
			List<CBMColor> colors = new ArrayList<CBMColor>(usedColors);

			Collections.sort(colors);

			for (int i = 0; i < colors.size(); i += 1)
			{
				int x = (targetWidth * i) / colors.size();

				g.setColor(estimationPalette.color(colors.get(i)));
				g.fillRect(x, targetHeight, ((targetWidth * (i + 1)) / colors.size()) - x, samplePaletteHeight);
			}
		}

		return resultImage;
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
		CBMImage targetImage = getTargetImage();

		for (int y = 0; y < targetHeight; y += mode.getHeightPerChar())
		{
			for (int x = 0; x < scaledTargetWidth; x += mode.getWidthPerChar())
			{
				CBMColor[] colors = raster.get(x / mode.getWidthPerChar(), y / mode.getHeightPerChar());

				for (int innerY = y; innerY < (y + blockHeight); innerY += 1)
				{
					int value = 0;

					for (int innerX = x; innerX < (x + mode.getWidthPerChar()); innerX += 1)
					{
						int index = estimationPalette
								.estimateIndex(colors, colorSpace, targetImage.get(innerX, innerY));

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
		CBMImage targetImage = getTargetImage();

		for (int y = 0; y < targetHeight; y += blockHeight)
		{
			for (int x = 0; x < scaledTargetWidth; x += scaledBlockWidth)
			{
				CBMColor[] colors = raster.get(x / scaledBlockWidth, y / blockHeight);

				for (int innerY = y; innerY < (y + blockHeight); innerY += 1)
				{
					int value = 0;

					for (int innerX = x; innerX < (x + scaledBlockWidth); innerX += 8 / mode.getBitPerColor())
					{
						for (int byteX = innerX; byteX < (innerX + (8 / mode.getBitPerColor())); byteX += 1)
						{
							int index = estimationPalette.estimateIndex(colors, colorSpace,
									targetImage.get(byteX, innerY));

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
				CBMColor[] colors = raster.get(x, y);

				if (mode == GraphicsMode.LORES)
				{
					out.write(((colors.length > 2) ? colors[2].index() : 0)
							| (((colors.length > 1) ? colors[1].index() : 0) << 4));
				}
				else
				{
					out.write(((colors.length > 0) ? colors[0].index() : 0)
							| (((colors.length > 1) ? colors[1].index() : 0) << 4));
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
					CBMColor[] colors = raster.get(x, y);

					out.write((colors.length > 3) ? colors[3].index() : 0);
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
				int yuv = CBMPalette.rgb2yuv(image.getRGB(x, y));
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
				int yuv = CBMPalette.rgb2yuv(image.getRGB(x, y));
				int yChannel = (yuv >> 16) & 0xff;
				int uChannel = (yuv >> 8) & 0xff;
				int vChannel = yuv & 0xff;

				yChannel = (int) ((yChannel - min) * delta);

				image.setRGB(x, y, CBMPalette.yuv2rgb((yChannel << 16) | (uChannel << 8) | vChannel));
			}
		}

		return image;
	}

	private Raster createRaster(CBMImage estimationImage, CBMPalette estimationPalette, List<CBMColor> mandatoryColors,
			int overscan)
	{
		Raster raster = new Raster(targetWidth / scaledBlockWidth, targetHeight / blockHeight);

		for (int y = 0; y < estimationImage.getHeight(); y += blockHeight)
		{
			for (int x = 0; x < estimationImage.getWidth(); x += scaledBlockWidth)
			{
				CBMColor[] colors = detectCBMColors(estimationImage, x, y, scaledBlockWidth, blockHeight,
						mode.getNumberOfColors(), estimationPalette, mandatoryColors, overscan, overscan);

				raster.set(x / scaledBlockWidth, y / blockHeight, colors);
			}
		}

		return raster;
	}

	private CBMColor[] detectCBMColors(CBMImage estimationImage, int x, int y, int w, int h, int numberOfColors,
			CBMPalette estimationPalette, List<CBMColor> mandatoryColors, int xOverscan, int yOverscan)
	{
		Map<CBMColor, Entry> counts = new HashMap<CBMColor, Entry>();

		for (int j = -yOverscan; j < (h + yOverscan); j += 1)
		{
			for (int i = -xOverscan; i < (w + xOverscan); i += 1)
			{
				if (((y + j) < 0) || ((y + j) >= estimationImage.getHeight()) || ((x + i) < 0)
						|| ((x + i) >= estimationImage.getWidth()))
				{
					continue;
				}

				CBMColor color = estimationPalette.estimateCBMColor(CBMColor.values(), colorSpace,
						estimationImage.get(x + i, y + j));
				Entry entry = counts.get(color);

				if (entry == null)
				{
					entry = new Entry(color);
					counts.put(color, entry);
				}

				entry.incCount();
			}
		}

		List<CBMColor> colors = new ArrayList<CBMColor>(mandatoryColors);

		for (CBMColor current : mandatoryColors)
		{
			counts.remove(current);
		}

		List<Entry> entries = new ArrayList<Entry>(counts.values());
		Collections.sort(entries);

		for (Entry entry : entries)
		{
			colors.add(entry.getColor());
		}

		return toCBMColorArray(colors, numberOfColors);
	}

	private static BufferedImage createScaledImage(BufferedImage image, int x, int y, int width, int height,
			int scaledTargetWidth, int targetHeight)
	{
		BufferedImage scaledImage = new BufferedImage(scaledTargetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = scaledImage.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(image, 0, 0, scaledTargetWidth, targetHeight, x, y, x + width, y + height, null);

		return scaledImage;
	}

	private static CBMImage createImage(BufferedImage image, CBMColorSpace colorSpace, CBMPalette estimationPalette,
			CBMColor[] allowedPalette, GraphicsMode mode, int blockWidth, int blockHeight, Raster raster,
			CBMBitmapDither dither, float ditherStrength)
	{
		CBMImage result = new CBMImage(image, colorSpace);

		for (int y = 0; y < result.getHeight(); y += 1)
		{
			for (int x = 0; x < result.getWidth(); x += 1)
			{
				if (raster != null)
				{
					CBMColor[] possiblePalette = raster.get(x / blockWidth, y / blockHeight);

					if ((possiblePalette == null) || (possiblePalette.length < mode.getNumberOfColors()))
					{
						CBMColor targetColor = dither.getStrategy().execute(result, estimationPalette, allowedPalette, x,
								y, ditherStrength);

						raster.add(x / blockWidth, y / blockHeight, targetColor);
					}
					else
					{
						dither.getStrategy().execute(result, estimationPalette, possiblePalette, x, y, ditherStrength);
					}
				}
				else
				{
					dither.getStrategy().execute(result, estimationPalette, allowedPalette, x, y, ditherStrength);
				}

			}
		}

		return result;
	}

	private static CBMColor[] toCBMColorArray(List<CBMColor> palette, int max)
	{
		CBMColor[] result = new CBMColor[Math.min(palette.size(), max)];

		for (int i = 0; (i < palette.size()) && (i < max); i += 1)
		{
			result[i] = palette.get(i);
		}

		return result;
	}

}

package org.cbm.ant.util.bitmap;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.cbm.ant.util.CBMBitmap;
import org.cbm.ant.util.CBMBitmapDither;
import org.cbm.ant.util.GraphicsMode;
import org.cbm.ant.util.bitmap.util.CBMBitmapCanvas;

public class CBMBitmapProjectController
{

	private final Object semaphore = new Object();

	private final CBMBitmapProjectModel model;
	private final CBMBitmapProjectView view;

	private Thread updateThread = null;
	private boolean updateNeeded = false;

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

	public void recalculate()
	{
		synchronized (semaphore)
		{
			updateNeeded = true;

			if (updateThread == null)
			{
				updateThread = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							while (updateNeeded)
							{
								synchronized (semaphore)
								{
									updateNeeded = false;
								}

								final CBMBitmap bitmap = new CBMBitmap().blockSize(8, 8);

								bitmap.setImage(model.getSourceImage());

								if (model.getTargetWidth() != null)
								{
									bitmap.setTargetWidth(model.getTargetWidth());
								}

								if (model.getTargetHeight() != null)
								{
									bitmap.setTargetHeight(model.getTargetHeight());
								}

								bitmap.setAntiAlias(true);
								bitmap.setYuv(false);
								bitmap.setDither(model.getDither());
								bitmap.setDitherStrength(model.getDitherStrength());
								bitmap.setMode(GraphicsMode.LORES);
								bitmap.setContrast(new float[] {
										model.getContrastRed(), model.getContrastGreen(), model.getContrastBlue()
								});
								bitmap.setBrightness(new float[] {
										model.getBrightnessRed(), model.getBrightnessGreen(), model.getBrightnessBlue()
								});
								bitmap.setAllowedColors(model.getAllowedColors());
								bitmap.setMandatoryColors(model.getMandatoryColors());
								bitmap.setEstimationPalette(model.createEsitmationPalette());

								final BufferedImage sampleImage = bitmap.getSampleImage();

								SwingUtilities.invokeLater(new Runnable()
								{
									@Override
									public void run()
									{
										model.setTargetImage(sampleImage);
										updateTargetCanvas();
									};
								});
							}
						}
						catch (Exception e)
						{
							e.printStackTrace(System.err);
						}
						finally
						{
							updateThread = null;
						}
					}
				}, "Image update");

				updateThread.setDaemon(true);
				updateThread.start();
			}
		}
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

		view.getSplitPane().setDividerLocation(0.5);
		view.invalidate();
		view.repaint();

		recalculate();
	}

	public void setSourceZoom(double zoom)
	{
		view.getSourceCanvas().setZoom(zoom);
	}

	public void sourceZoom(double multiplier)
	{
		view.getSourceCanvas().zoom(multiplier, null);
	}

	public void sourceZoomFit()
	{
		CBMBitmapCanvas canvas = view.getSourceCanvas();
		zoomFit(canvas);
	}

	public void setTargetZoom(double zoom)
	{
		view.getTargetCanvas().setZoom(zoom);
	}

	public void targetZoom(double multiplier)
	{
		view.getTargetCanvas().zoom(multiplier, null);
	}

	public void targetZoomFit()
	{
		CBMBitmapCanvas canvas = view.getTargetCanvas();
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

	public void resize(int width, int height)
	{
		Image image = model.getSourceImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		resizedImage.getGraphics().drawImage(image, 0, 0, null);
		model.setSourceImage(resizedImage);
		recalculate();
		updateSourceCanvas();
	}

	public void clearTargetSize()
	{
		model.setTargetWidth(null);
		model.setTargetHeight(null);
		recalculate();
	}

	public void targetSize(Integer targetWidth, Integer targetHeight)
	{
		model.setTargetWidth(targetWidth);
		model.setTargetHeight(targetHeight);
		recalculate();
	}

	private void updateSourceCanvas()
	{
		view.getSourceCanvas().setImage(model.getSourceImage());
		view.repaint();
	}

	private void updateTargetCanvas()
	{
		view.getTargetCanvas().setImage(model.getTargetImage());
		view.repaint();
	}

	public void setDither(CBMBitmapDither dither)
	{
		model.setDither(dither);
		recalculate();
	}

	public void toggleSplit()
	{
		JSplitPane splitPane = view.getSplitPane();
		int orientation = splitPane.getOrientation();

		if (orientation == JSplitPane.HORIZONTAL_SPLIT)
		{
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		}
		else
		{
			splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		}

		splitPane.setDividerLocation(0.5);
		splitPane.invalidate();
		splitPane.repaint();
	}

}

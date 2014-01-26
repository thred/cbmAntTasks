package org.cbm.ant.util.bitmap;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.cbm.ant.util.bitmap.util.CBMBitmapSlider;
import org.cbm.ant.util.bitmap.util.CBMBitmapUtils;
import org.cbm.ant.util.bitmap.util.GBC;

public class CBMBitmapContrastAndBrightnessTool extends AbstractCBMBitmapTool implements ActionListener, ChangeListener
{

	private static final long serialVersionUID = -3018765182933957393L;

	private final CBMBitmapSlider contrastRedSlider = CBMBitmapUtils.createSlider(100, 0, 800, 10, 50, this);
	private final CBMBitmapSlider contrastGreenSlider = CBMBitmapUtils.createSlider(100, 0, 800, 10, 50, this);
	private final CBMBitmapSlider contrastBlueSlider = CBMBitmapUtils.createSlider(100, 0, 800, 10, 50, this);
	private final JCheckBox contrastLink = CBMBitmapUtils.createCheckBox("Link color channels", true);
	private final JCheckBox contrastKeep = CBMBitmapUtils.createCheckBox("Keep overall contrast", true);

	private final CBMBitmapSlider brightnessRedSlider = CBMBitmapUtils.createSlider(0, -200, 200, 5, 25, this);
	private final CBMBitmapSlider brightnessGreenSlider = CBMBitmapUtils.createSlider(0, -200, 200, 5, 25, this);
	private final CBMBitmapSlider brightnessBlueSlider = CBMBitmapUtils.createSlider(0, -200, 200, 5, 25, this);
	private final JCheckBox brightnessLink = CBMBitmapUtils.createCheckBox("Link color channels", true);
	private final JCheckBox birghtnessKeep = CBMBitmapUtils.createCheckBox("Keep overall brightness", true);

	private final JButton resetButton = CBMBitmapUtils.createButton("Reset", this);

	public CBMBitmapContrastAndBrightnessTool()
	{
		super("contrast-and-brightness.png", "Contrast and Brightness",
				"Adjust contrast and brightness of the target image.");

		JPanel contrastPanel = new JPanel(new GridBagLayout());
		GBC gbc = new GBC(3, 5);

		contrastPanel.add(CBMBitmapUtils.createIcon("color-channel-red.png", contrastRedSlider), gbc);
		contrastPanel.add(contrastRedSlider, gbc.next().span(2));
		contrastPanel.add(CBMBitmapUtils.createIcon("color-channel-green.png", contrastGreenSlider), gbc.next());
		contrastPanel.add(contrastGreenSlider, gbc.next().span(2));
		contrastPanel.add(CBMBitmapUtils.createIcon("color-channel-blue.png", contrastBlueSlider), gbc.next());
		contrastPanel.add(contrastBlueSlider, gbc.next().span(2));
		contrastPanel.add(contrastLink, gbc.next().next().weight(1));
		contrastPanel.add(contrastKeep, gbc.next().weight(1));

		JPanel brightnessPanel = new JPanel(new GridBagLayout());
		gbc = new GBC(3, 5);

		brightnessPanel.add(CBMBitmapUtils.createIcon("color-channel-red.png", brightnessRedSlider), gbc);
		brightnessPanel.add(brightnessRedSlider, gbc.next().span(2));
		brightnessPanel.add(CBMBitmapUtils.createIcon("color-channel-green.png", brightnessGreenSlider), gbc.next());
		brightnessPanel.add(brightnessGreenSlider, gbc.next().span(2));
		brightnessPanel.add(CBMBitmapUtils.createIcon("color-channel-blue.png", brightnessBlueSlider), gbc.next());
		brightnessPanel.add(brightnessBlueSlider, gbc.next().span(2));
		brightnessPanel.add(brightnessLink, gbc.next().next().weight(1));
		brightnessPanel.add(birghtnessKeep, gbc.next().weight(1));

		add(CBMBitmapUtils.createLabel("contrast.png", "Contrast", contrastPanel));
		add(contrastPanel);
		add(CBMBitmapUtils.createLabel("brightness.png", "Brightness", brightnessPanel));
		add(brightnessPanel);
		add(resetButton);
	}

	@Override
	public void onCBMBitmapProjectUpdate(CBMBitmapProjectController controller, CBMBitmapProjectModel model,
			PropertyChangeEvent event)
	{
		if ((event != null) && (!event.getPropertyName().startsWith("contrast"))
				&& (!event.getPropertyName().startsWith("brightness")))
		{
			return;
		}

		if (model == null)
		{
			return;
		}
		
		int contrastRed = (int) (model.getContrastRed() * 100);
		int contrastGreen = (int) (model.getContrastGreen() * 100);
		int contrastBlue = (int) (model.getContrastBlue() * 100);

		contrastRedSlider.setValue(contrastRed);
		contrastGreenSlider.setValue(contrastGreen);
		contrastBlueSlider.setValue(contrastBlue);

		int brightnessRed = (int) (model.getBrightnessRed() * 100);
		int brightnessGreen = (int) (model.getBrightnessGreen() * 100);
		int brightnessBlue = (int) (model.getBrightnessBlue() * 100);

		brightnessRedSlider.setValue(brightnessRed);
		brightnessGreenSlider.setValue(brightnessGreen);
		brightnessBlueSlider.setValue(brightnessBlue);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		contrastRedSlider.setValue(100);
		contrastGreenSlider.setValue(100);
		contrastBlueSlider.setValue(100);

		brightnessRedSlider.setValue(0);
		brightnessGreenSlider.setValue(0);
		brightnessBlueSlider.setValue(0);
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		Object source = e.getSource();

		if (source == contrastRedSlider)
		{
			if (contrastLink.isSelected())
			{
				contrastGreenSlider.setValue(contrastRedSlider.getValue());
				contrastBlueSlider.setValue(contrastRedSlider.getValue());
			}

			updateContrast();
		}
		else if (source == contrastGreenSlider)
		{
			if (contrastLink.isSelected())
			{
				contrastRedSlider.setValue(contrastGreenSlider.getValue());
				contrastBlueSlider.setValue(contrastGreenSlider.getValue());
			}

			updateContrast();
		}
		else if (source == contrastBlueSlider)
		{
			if (contrastLink.isSelected())
			{
				contrastRedSlider.setValue(contrastBlueSlider.getValue());
				contrastGreenSlider.setValue(contrastBlueSlider.getValue());
			}

			updateContrast();
		}
		else if (source == brightnessRedSlider)
		{
			if (brightnessLink.isSelected())
			{
				brightnessGreenSlider.setValue(brightnessRedSlider.getValue());
				brightnessBlueSlider.setValue(brightnessRedSlider.getValue());
			}

			updateBrightness();
		}
		else if (source == brightnessGreenSlider)
		{
			if (brightnessLink.isSelected())
			{
				brightnessRedSlider.setValue(brightnessGreenSlider.getValue());
				brightnessBlueSlider.setValue(brightnessGreenSlider.getValue());
			}

			updateBrightness();
		}
		else if (source == brightnessBlueSlider)
		{
			if (brightnessLink.isSelected())
			{
				brightnessRedSlider.setValue(brightnessBlueSlider.getValue());
				brightnessGreenSlider.setValue(brightnessBlueSlider.getValue());
			}

			updateBrightness();
		}

	}

	private void updateContrast()
	{
		CBMBitmapProjectController controller = getActiveController();

		if (controller == null)
		{
			return;
		}

		CBMBitmapProjectModel model = controller.getModel();

		model.setContrastRed(contrastRedSlider.getValue() / 100f);
		model.setContrastGreen(contrastGreenSlider.getValue() / 100f);
		model.setContrastBlue(contrastBlueSlider.getValue() / 100f);

		controller.recalculate();
	}

	private void updateBrightness()
	{
		CBMBitmapProjectController controller = getActiveController();

		if (controller == null)
		{
			return;
		}

		CBMBitmapProjectModel model = controller.getModel();

		model.setBrightnessRed(brightnessRedSlider.getValue() / 100f);
		model.setBrightnessGreen(brightnessGreenSlider.getValue() / 100f);
		model.setBrightnessBlue(brightnessBlueSlider.getValue() / 100f);

		controller.recalculate();
	}

}

package org.cbm.ant.util.bitmap.tool;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.cbm.ant.cbm.bitmap.CBMColor;
import org.cbm.ant.cbm.bitmap.CBMPalette;
import org.cbm.ant.util.bitmap.CBMBitmapPaletteUsage;
import org.cbm.ant.util.bitmap.CBMBitmapProjectController;
import org.cbm.ant.util.bitmap.CBMBitmapProjectModel;
import org.cbm.ant.util.bitmap.util.CBMBitmapColorComponent;
import org.cbm.ant.util.bitmap.util.CBMBitmapUtils;

public class CBMBitmapPaletteTool extends AbstractCBMBitmapTool implements ActionListener
{

    private static final long serialVersionUID = -3018765182933957393L;

    private final Map<CBMColor, CBMBitmapColorComponent> colorComponents = new HashMap<>();
    private final JButton resetButton = CBMBitmapUtils.createButton("Reset", this);

    public CBMBitmapPaletteTool()
    {
        super("palette.png", "Palette", "Modify the (source) palette.");

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        panel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));

        for (CBMColor color : CBMColor.values())
        {
            CBMBitmapColorComponent component = new CBMBitmapColorComponent();

            component.addActionListener(this);
            component.setColor(CBMPalette.DEFAULT.color(color));
            component.setReferenceColor(CBMPalette.DEFAULT.color(color));

            colorComponents.put(color, component);

            panel.add(component);
        }

        add(panel);
        add(resetButton);
    }

    @Override
    public void onCBMBitmapProjectUpdate(CBMBitmapProjectController controller, CBMBitmapProjectModel model,
        PropertyChangeEvent event)
    {
        if (model == null)
        {
            return;
        }

        for (CBMColor color : CBMColor.values())
        {
            CBMBitmapColorComponent component = colorComponents.get(color);

            component.setColor(model.getPaletteColor(color));
            component.setUsage(model.getPaletteUsage(color));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (resetButton == e.getSource())
        {
            CBMBitmapProjectModel model = getActiveModel();

            for (CBMColor color : CBMColor.values())
            {
                CBMBitmapColorComponent component = colorComponents.get(color);

                component.setColor(CBMPalette.DEFAULT.color(color));
                component.setUsage(CBMBitmapPaletteUsage.OPTIONAL);

                if (model != null)
                {
                    model.setPaletteColor(color, CBMPalette.DEFAULT.color(color));
                    model.setPaletteUsage(color, CBMBitmapPaletteUsage.OPTIONAL);
                }
            }

            repaint();

            CBMBitmapProjectController controller = getActiveController();

            if (controller != null)
            {
                controller.recalculate();
            }

            return;
        }

        CBMColor palette = getPalette(e.getSource());

        if (palette != null)
        {
            CBMBitmapProjectModel model = getActiveModel();

            if (model == null)
            {
                return;
            }

            CBMBitmapColorComponent component = colorComponents.get(palette);

            model.setPaletteColor(palette, component.getColor());
            model.setPaletteUsage(palette, component.getUsage());

            getActiveController().recalculate();
        }
    }

    protected CBMColor getPalette(Object source)
    {
        for (Map.Entry<CBMColor, CBMBitmapColorComponent> entry : colorComponents.entrySet())
        {
            if (source == entry.getValue())
            {
                return entry.getKey();
            }
        }

        return null;
    }
}

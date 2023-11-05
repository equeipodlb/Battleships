package utils;

import javax.swing.*;
import java.awt.*;

public class IconUtils {
    public static ImageIcon resizedIcon(ImageIcon icon, int x, int y) {
        Image image = icon.getImage();
        image.setAccelerationPriority(1);
        Image newimg = image.getScaledInstance(x, y, Image.SCALE_FAST);
        return new ImageIcon(newimg);
    }

    // Devuelve un icono redimensionado para ajustarse al PreferredSize de un componente.
    public static ImageIcon resizedIcon(ImageIcon icon, JComponent component) {
        return resizedIcon(icon, (int) component.getPreferredSize().getWidth(), (int) component.getPreferredSize().getHeight());
    }
}

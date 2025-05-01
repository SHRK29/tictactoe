package com.uptc.edu.co.tictactoe.Utils;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.InputStream;

public class FontUtils {

    public static Font cargarFuenteBaloo(double size) {
        try {
            InputStream is = FontUtils.class.getResourceAsStream("/Fonts/Baloo2-ExtraBold.ttf");
            if (is != null) {
                Font font = Font.loadFont(is, size);
                if (font != null) {
                    return font;
                }
            }
            System.err.println("No se pudo cargar Baloo 2, usando Arial como fallback");
        } catch (Exception e) {
            System.err.println("Error al cargar fuente: " + e.getMessage());
        }

        return Font.font("Arial", FontWeight.EXTRA_BOLD, size);
    }
}

package com.space_invaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Linea {

    static ShapeRenderer linea = new ShapeRenderer();

    public static void dibujar(int x1, int y1, int x2, int y2){
        linea.begin(ShapeRenderer.ShapeType.Line);
        linea.setColor(1, 1, 1, 1);
        linea.line(x1, y1, x2, y2);
        linea.end();
    }
}

package com.space_invaders;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.space_invaders.screens.MenuScreen;

// Extendiendo Game es la forma correcta de manejar pantallas.
public class MyGame extends Game {

    private SpriteBatch batch;

    @Override
    public void create() {
        // Contiene recursos comunes para que las clases que lo necesitan lo compartan
        batch = new SpriteBatch();
        //Cargamos la pantalla inicial
        setScreen(new MenuScreen(this)); // this hace referencia a MyGame
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public void dispose() {
        // Asegúrate de liberar el batch al salir
        super.dispose();
        batch.dispose();
    }
}

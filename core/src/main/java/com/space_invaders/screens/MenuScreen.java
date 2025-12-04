package com.space_invaders.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.space_invaders.MyGame;
import com.space_invaders.red.HiloCliente;

public class MenuScreen implements Screen {

    public static boolean empieza = false;

    private final MyGame game;
    private final Texture imagenFondo;

    private Skin skin;
    private Stage stage;
    private Table table;

    private final SpriteBatch batch;
    private boolean multijugador;

    public MenuScreen(final MyGame game) {
        this.game = game;
        this.batch = game.getBatch(); // Hereda el SpriteBatch de MyGame
        imagenFondo = new Texture("FondoMenu.png");
    }

    @Override
    public void show() {
        // Carga la Skin y la apariencia de los botones
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        stage = new Stage();

        // Establecer el Stage como el procesador de entrada
        Gdx.input.setInputProcessor(stage);

        // Crear la Table y centrarla
        table = new Table(skin); // Integrar estilos de la skin
        table.setFillParent(true); // Hace que la Table ocupe todo el Stage y pantalla
        stage.addActor(table);

        // Crear y añadir los botones
        crearBotones();
    }

    private void crearBotones() {
        // Crear los TextButton
        TextButton botonJugar = new TextButton("JUGAR", skin);
        TextButton botonMultijugador = new TextButton("MULTIJUGADOR", skin);
        TextButton botonControles = new TextButton("CONTROLES", skin);
        TextButton botonSalir = new TextButton("SALIR", skin);

        // Añadir los botones a la Table, uno debajo del otro
        float anchoBoton = 200f; // Ancho fijo en píxeles
        float altoBoton = 60f; // Alto fijo en píxeles

        table.padTop(200); // Añade relleno a la parte de arriba de la tabla(para centrar los botones)
        table.add(botonJugar).width(anchoBoton).height(altoBoton).pad(15);
        table.row(); // Siguiente fila
        table.add(botonMultijugador).width(anchoBoton).height(altoBoton).pad(15);
        table.row();
        table.add(botonControles).width(anchoBoton).height(altoBoton).pad(15);
        table.row();
        table.add(botonSalir).width(anchoBoton).height(altoBoton).pad(15);

        // Añadir Listeners para las acciones
        botonJugar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                multijugador = false;
                game.setScreen(new GameScreen(game, multijugador));
            }
        });

        if(empieza){ // Deshabilitamos el boton multijugador hasta que ambos jugadores esten listos
            botonMultijugador.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    multijugador = true;
                    game.setScreen(new GameScreen(game, multijugador));
                }
            });
        }

        botonControles.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new Controles(game));
            }
        });
        botonSalir.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit(); // Cierra la aplicación
            }
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 0f);

        batch.begin();
        batch.draw(imagenFondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Dibujar los botones
        if (stage != null) {
            stage.act(delta);
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        // Actualiza el Stage para que el Table se recalcule y se mantenga centrado.
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        imagenFondo.dispose();

        // Liberar recursos que se inicializarán más adelante (verificando que no sean null)
        if (stage != null) {
            stage.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
    }
}

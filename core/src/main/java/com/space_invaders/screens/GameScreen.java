package com.space_invaders.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen; // Importamos la interfaz Screen
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.space_invaders.*;
import com.space_invaders.red.HiloCliente;

import java.awt.*;

// GameScreen implementa Screen
public class GameScreen implements Screen {
    final MyGame game;
    private SpriteBatch batch;

    private HiloCliente hc; // Hilo para el cliente

    private Texture fondo;
    private Sprite fondoPantalla;

    private boolean empiezaJuego = false;
    private boolean multijugador;
    private Sprite icono_1;
    private Sprite icono_2;

    private Skin skin;
    private Stage stage;
    private Table table;

    private Texture nave;
    private Texture nave_2;
    private Texture disparo;
    private Texture alien;
    private Jugador jugador;
    private Jugador2 jugador_2;
    private AlienManager alienManager;

    public GameScreen(final MyGame game, boolean multijugador) {
        this.game = game;
        batch = game.getBatch();

        hc = new HiloCliente();

        fondo = new Texture("FondoJuego.png");
        fondoPantalla = new Sprite(fondo);

        this.multijugador = multijugador;

        nave = new Texture("nave.png");
        nave_2 = new Texture("nave_2.png");
        disparo = new Texture("bala_2.png");
        alien = new Texture("alien_1.png");
        jugador = new Jugador(nave, disparo);
        jugador_2 = new Jugador2(nave_2, disparo);
        int anchoAliens = 7;
        int altoAliens = 4;
        int espacioAliens = 80;
        alienManager = new AlienManager(altoAliens, anchoAliens, espacioAliens, alien);

        icono_1 = new Sprite(nave);
        icono_2 = new Sprite(nave_2);

        hc.start();

        if(!multijugador) {
            jugador.posicion = new Vector2((Gdx.graphics.getWidth()/2f)-(jugador.sprite.getWidth()/2f), 10);
            jugador_2.posicion = new Vector2(0, Gdx.graphics.getHeight());
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 0f);

        if(!empiezaJuego){

        } else {
            float deltaTime = Gdx.graphics.getDeltaTime();
            alienManager.ActualizarMovimiento(deltaTime);

            batch.begin();
            icono_1.setSize(100, 100);
            icono_2.setSize(100, 100);
            icono_1.setPosition(150 / 2f - icono_1.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - icono_1.getHeight() / 2f);
            icono_2.setPosition(Gdx.graphics.getWidth() - (150 / 2f) - icono_1.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - icono_2.getHeight() / 2f);
            icono_1.draw(batch);
            icono_2.draw(batch);
            // batch.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            // Dibujar fondo de pantalla ajustado
            fondoPantalla.setSize(700, Gdx.graphics.getHeight());
            fondoPantalla.setPosition(150, 0);
            fondoPantalla.draw(batch);

            // Dibujamos las naves de los jugadores
            jugador.Dibujar(batch);
            jugador_2.Dibujar(batch);

            // Cuando ya no quedan aliens
            if (alienManager.victoria()) {
                game.setScreen(new MenuScreen(game));
            }

            // Si la bala conecta con un alien, si transporta fuera de la pantalla
            if (alienManager.colisionConBala(jugador.sprite_disparo)) {
                jugador.posicion_disparo.y = Gdx.graphics.getHeight();
            }
            if (alienManager.colisionConBala(jugador_2.sprite_disparo)) {
                jugador_2.posicion_disparo.y = Gdx.graphics.getHeight();
            }

            for (Alien alien : alienManager.getAliens()) {
                // Si los aliens tocan a cualquier jugador, ambos pierden
                if (alien.sprite.getBoundingRectangle().overlaps(jugador.sprite.getBoundingRectangle()) ||
                    alien.sprite.getBoundingRectangle().overlaps(jugador_2.sprite.getBoundingRectangle())) {
                    game.setScreen(new MenuScreen(game));
                }
            }
            alienManager.Dibujar(batch);
            game.getBatch().end();
            Linea.dibujar(150, 0, 150, Gdx.graphics.getHeight()); // Espacio de juego es de 700
            Linea.dibujar(850, 0, 850, Gdx.graphics.getHeight());

            // Mostrar el botón de back
            if (stage != null) {
                stage.act(delta);
                stage.draw();
            }
        }
    }

    // --- Métodos obligatorios de la interfaz Screen ---

    @Override
    public void resize(int width, int height) {
        // Aquí va la lógica del viewport
    }

    @Override
    public void dispose() {
        nave.dispose();
        nave_2.dispose();
        disparo.dispose();
        alien.dispose();

        if (stage != null) {
            stage.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
    }

    @Override public void show() {
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        table = new Table(skin); // Integrar estilos de la skin
        table.setFillParent(true);
        stage.addActor(table);
        TextButton back = new TextButton("Back", skin);
        float anchoBoton = 100f; // Ancho fijo en píxeles
        float altoBoton = 40f; // Alto fijo en píxeles
        table.top().left();
        table.add(back).width(anchoBoton).height(altoBoton).pad(15);

        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MenuScreen(game));
            }
        });
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}

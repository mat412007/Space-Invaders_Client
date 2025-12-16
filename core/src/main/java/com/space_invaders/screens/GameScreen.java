package com.space_invaders.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    private final SpriteBatch batch;

    private HiloCliente hc; // Hilo para el cliente
    private boolean empiezaJuego = false;

    private final Texture fondo;
    private final Sprite fondoPantalla;

    private final Sprite fondoEspera;
    private boolean multijugador;
    private final Sprite icono_1;
    private final Sprite icono_2;

    private Skin skin;
    private Stage stage;
    private Table table;

    private final Texture nave;
    private final Texture nave_2;
    private final Texture disparo;
    private final Texture alien;
    private Jugadores jugador;
    private Jugadores jugador_2;
    private AlienManager alienManager;

    public GameScreen(final MyGame game, boolean multijugador) {
        this.game = game;
        batch = game.getBatch();

        fondo = new Texture("FondoJuego.png");
        fondoPantalla = new Sprite(fondo);
        fondoEspera = new Sprite(new Texture("Espera.png"));

        nave = new Texture("nave.png");
        nave_2 = new Texture("nave_2.png");
        disparo = new Texture("bala_2.png");
        alien = new Texture("alien_1.png");
        int anchoAliens = 7;
        int altoAliens = 4;
        int espacioAliens = 80;
        alienManager = new AlienManager(altoAliens, anchoAliens, espacioAliens, alien);

        icono_1 = new Sprite(nave);
        icono_2 = new Sprite(nave_2);

        this.multijugador = multijugador;

        //jugador_2 = new Jugadores(nave_2, disparo, 2);

        if(multijugador) {
            hc = new HiloCliente(this);
            hc.start();
        }
        if(!multijugador) { // Ajusto las posiciones de las dos naves
            jugador = new Jugadores(nave, disparo, 1, null, true);
            jugador.posicion.set((Gdx.graphics.getWidth()/2f)-(jugador.sprite.getWidth()/2f), 10);
            jugador_2 = null;
        }

    }

    @Override
    public void render(float delta) {
        if(multijugador) {
            empiezaJuego = hc.empezar;
            if(jugador == null) {
                if(hc.idCliente == 1){
                    jugador = new Jugadores(nave, disparo, 1, hc, true);
                    jugador_2 = new Jugadores(nave_2, disparo, 2, hc, false);
                } else if(hc.idCliente == 2){
                    jugador = new Jugadores(nave_2, disparo, 2, hc, true);
                    jugador_2 = new Jugadores(nave, disparo, 1, hc, false);
                } else {
                    System.out.println("Error: ID de cliente no válido.");
                    return;
                }
            }
        }
        if(!empiezaJuego && multijugador) {
            batch.begin();
            fondoEspera.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            fondoEspera.setPosition(0, 0);
            fondoEspera.draw(batch);
            batch.end();
            return;
        }

        ScreenUtils.clear(0f, 0f, 0f, 0f);
        float deltaTime = Gdx.graphics.getDeltaTime();
        alienManager.ActualizarMovimiento(deltaTime);

        batch.begin();
        icono_1.setSize(100, 100);
        icono_2.setSize(100, 100);
        icono_1.setPosition(150 / 2f - icono_1.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - icono_1.getHeight() / 2f);
        icono_2.setPosition(Gdx.graphics.getWidth() - (150 / 2f) - icono_1.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - icono_2.getHeight() / 2f);
        icono_1.draw(batch);
        icono_2.draw(batch);

        // Dibujar fondo de pantalla ajustado
        fondoPantalla.setSize(700, Gdx.graphics.getHeight());
        fondoPantalla.setPosition(150, 0);
        fondoPantalla.draw(batch);

        // Dibujamos las naves de los jugadores y los aliens
        jugador.Dibujar(batch);
        jugador.Actualizar(deltaTime);
        if(multijugador) {
            jugador_2.Dibujar(batch);
            jugador_2.Actualizar(deltaTime);
        }
        alienManager.Dibujar(batch);

        // Líneas de límite del espacio de juego
        Linea.dibujar(150, 0, 150, Gdx.graphics.getHeight()); // Espacio de juego es de 700
        Linea.dibujar(850, 0, 850, Gdx.graphics.getHeight());

        // Mostrar el botón de back
        if (stage != null) {
            stage.act(delta);
            stage.draw();
        }

        // Cuando ya no quedan aliens
        if (alienManager.victoria()) {
            game.setScreen(new MenuScreen(game));
        }

        // Si la bala conecta con un alien, si transporta fuera de la pantalla
        if (alienManager.colisionConBala(jugador.sprite_disparo)) {
            jugador.posicion_disparo.y = Gdx.graphics.getHeight();
        }
//        if (alienManager.colisionConBala(jugador_2.sprite_disparo)) {
//            jugador_2.posicion_disparo.y = Gdx.graphics.getHeight();
//        }

        for (Alien alien : alienManager.getAliens()) {
            // Si los aliens tocan a cualquier jugador, ambos pierden
            if (alien.sprite.getBoundingRectangle().overlaps(jugador.sprite.getBoundingRectangle()) /*||
                alien.sprite.getBoundingRectangle().overlaps(jugador_2.sprite.getBoundingRectangle())*/) {
                game.setScreen(new MenuScreen(game));
            }
        }
        game.getBatch().end();
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
        fondo.dispose();

        if (stage != null) {
            stage.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }

        if (hc != null) {
            hc.cerrar();
        }
    }

    public void actualizarPosicionNave(int idJugador, float nuevaX) {
        if (idJugador == jugador.id) {
            jugador.posicion.x = nuevaX;
        } else if (idJugador == jugador_2.id) {
            jugador_2.posicion.x = nuevaX;
        }
    }

    public void actualizarPosicionDisparo(int idJugador, float nuevaX, float nuevaY) {
        if (idJugador == jugador.id) {
            jugador.posicion_disparo.x = nuevaX;
            jugador.posicion_disparo.y = nuevaY;
        } else if (idJugador == jugador_2.id) {
            jugador_2.posicion_disparo.x = nuevaX;
            jugador_2.posicion_disparo.y = nuevaY;
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

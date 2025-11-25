package com.space_invaders.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.ScreenUtils;
import com.space_invaders.MyGame;

public class Controles implements Screen {

    SpriteBatch batch;
    MyGame game;

    BitmapFont font;
    GlyphLayout layout;

    private Skin skin;
    private Stage stage;
    private Table table;

    // Imagenes
    Texture nave_1;
    Texture nave_2;
    Texture imagenEnemigo;

    // Visuales
    Sprite jugador_1;
    Sprite jugador_2;
    Sprite enemigo;

    // Posiciones
    Vector2 posicion_1;
    Vector2 posicion_2;
    Vector2 posicionEnemigo;

    public Controles(final MyGame game){
        this.game = game;
        batch = game.getBatch();

        nave_1 = new Texture("nave.png");
        nave_2 = new Texture("nave_2.png");
        imagenEnemigo = new Texture("alien_1.png");

        jugador_1 = new Sprite(nave_1);
        jugador_2 = new Sprite(nave_2);
        enemigo = new Sprite(imagenEnemigo);

        // Tamaño de visuales
        jugador_1.setSize(100, 100);
        jugador_2.setSize(100, 100);
        enemigo.setSize(100, 100);

        posicion_1 = new Vector2(Gdx.graphics.getWidth()/3f-jugador_1.getWidth(), (571f)*(5/8f)); // Uso la altura en la que terminan las instrucciones
        posicion_2 = new Vector2(Gdx.graphics.getWidth()/3f-jugador_2.getWidth(), (571f)*(3/8f));
        posicionEnemigo = new Vector2(Gdx.graphics.getWidth()/3f-enemigo.getWidth(), (571f)*(1/8f));

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        layout = new GlyphLayout();
    }

    // Para dibujar las imágenes
    public void dibujarJugador1(SpriteBatch batch){
        jugador_1.setPosition(posicion_1.x, posicion_1.y);
        jugador_1.draw(batch);
    }
    public void dibujarJugador2(SpriteBatch batch){
        jugador_2.setPosition(posicion_2.x, posicion_2.y);
        jugador_2.draw(batch);
    }
    public void dibujarEnemigo(SpriteBatch batch){
        enemigo.setPosition(posicionEnemigo.x, posicionEnemigo.y);
        enemigo.draw(batch);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 0f);
        batch.begin();

        font.getData().setScale(2.5f);
        dibujarJugador1(batch);
        String nombre = "JUGADOR 1";
        layout.setText(font, nombre);
        font.draw(batch, nombre, posicion_1.x+jugador_1.getWidth()+30f, posicion_1.y+(jugador_1.getHeight()/2f)+layout.height/2f);
        dibujarJugador2(batch);
        nombre = "JUGADOR 2";
        layout.setText(font, nombre);
        font.draw(batch, nombre, posicion_2.x+jugador_2.getWidth()+30f, posicion_2.y+(jugador_2.getHeight()/2f)+layout.height/2f);
        dibujarEnemigo(batch);
        nombre = "ENEMIGO";
        layout.setText(font, nombre);
        font.draw(batch, nombre, posicionEnemigo.x+enemigo.getWidth()+30f, posicionEnemigo.y+(enemigo.getHeight()/2f)+layout.height/2f);

        font.getData().setScale(4f);
        String titulo = "SPACE INVADERS";
        layout.setText(font, titulo);
        float posY = Gdx.graphics.getHeight() - 50f; // Posición inicial alta para el título
        font.draw(batch, titulo, (Gdx.graphics.getWidth()/2f) - (layout.width/2f), posY);
        posY -= layout.height + 30f; // Bajamos la posición por la altura del título más un pco de espacio adicional

        // DIBUJAR INSTRUCCIONES
        font.getData().setScale(1.5f);
        float espaciadoEntreLineas = 10f; // Espacio vertical adicional entre líneas de instrucción
        String[] instrucciones = {
            "Las reglas del juego son simples:",
            "El jugador controla una nave que dispara a los aliens.",
            "Los aliens se moverán constantemente hacia los costados y hacia abajo.",
            "El juego termina cuando todos los aliens están muertos,",
            "o cuando estos hacen contacto con la nave del jugador."
        };
        for (String linea : instrucciones) {
            layout.setText(font, linea);
            font.draw(batch, linea, (Gdx.graphics.getWidth()/2f) - (layout.width/2f), posY);

            posY -= layout.height + espaciadoEntreLineas; // Altura de la línea actual más el espacio adicional de separación
        }
        batch.end();

        if (stage != null) {
            stage.act(delta);
            stage.draw();
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

    @Override
    public void resize(int width, int height) {
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
        nave_1.dispose();
        nave_2.dispose();
        imagenEnemigo.dispose();
    }
}

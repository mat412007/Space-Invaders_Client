package com.space_invaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.space_invaders.red.HiloCliente;

public class Jugadores {
    private float x;
    private float y;
    public Vector2 posicion;
    public Vector2 posicion_disparo;
    public Sprite sprite;
    public Sprite sprite_disparo;
    public float velocidad = 350;
    public float velocidad_disparo = 1500;
    private HiloCliente hc;
    public int id;

    private boolean esLocal;

    public Jugadores(Texture img_nave, Texture img_disparo, int id, HiloCliente hc, boolean esLocal) {
        this.hc = hc;
        sprite = new Sprite(img_nave);
        sprite_disparo = new Sprite(img_disparo);
        this.id = id;

        this.esLocal = esLocal;

        // Redimensionar la imagen directamente al cargarla
        if(id == 1){
            float scaleFactor = 0.15f;  // Factor de escala para cambiar el tamaño
            sprite.setSize(sprite.getWidth() * scaleFactor, sprite.getHeight() * scaleFactor);
            sprite_disparo.setSize(sprite.getWidth() * scaleFactor - 2.5f, sprite.getHeight() * scaleFactor + 10);
        } else if(id == 2){
            float scaleFactor = 0.046875f;  // Factor de escala para cambiar el tamaño
            sprite.setSize(sprite.getWidth() * scaleFactor, sprite.getHeight() * scaleFactor);
            sprite_disparo.setSize(sprite.getWidth() * 0.15f - 2.5f, sprite.getHeight() * 0.15f + 10);
        }

        // Calcular la posición de las naves en modo multijugador
        if (id == 1){
            x = (float) Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2 - sprite.getWidth();
        } else if(id == 2){
            x = (float) Gdx.graphics.getWidth() / 2 - sprite.getWidth() / 2 + sprite.getWidth();
        }
        y = 10;
        posicion = new Vector2(x, y);
        posicion_disparo = new Vector2(0, 10000);
    }

    // Metodo para actualizar la posición de la nave
    public void Actualizar(float deltaTime){
        if(!esLocal) return;

        if((hc == null)){
            if(Gdx.input.isKeyPressed(Keys.A)){
                posicion.x -= Gdx.graphics.getDeltaTime() * velocidad;
            }
            if(Gdx.input.isKeyPressed(Keys.D)){
                posicion.x += Gdx.graphics.getDeltaTime() * velocidad;
            }
            if(Gdx.input.isKeyPressed(Keys.W) && posicion_disparo.y >= Gdx.graphics.getHeight() && id==1){
                posicion_disparo.x = posicion.x + sprite.getWidth()/2 - sprite_disparo.getWidth()/2; // Centrar el disparo en la nave
                posicion_disparo.y = posicion.y + sprite.getHeight();
            }
        }

        posicion_disparo.y += deltaTime*velocidad_disparo; // Necesita ajustes
        String msg = "";
        if(id == 1 && hc != null){
            if(Gdx.input.isKeyPressed(Keys.A)){ // Enviar mensajes al tocar las teclas de movimiento
                msg = "mover_izquierda:1";
            }
            if(Gdx.input.isKeyPressed(Keys.D)){
                msg = "mover_derecha:1";
            }
            if(Gdx.input.isKeyPressed(Keys.W)){
                msg = "disparar:1";
            }
        } else if(id == 2 && hc != null){
            if(Gdx.input.isKeyPressed(Keys.LEFT)) {
                msg = "mover_izquierda:2";
            }
            if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
                msg = "mover_derecha:2";
            }
            if(Gdx.input.isKeyPressed(Keys.UP)){
                msg = "disparar:2";
            }
        }
        if(hc != null && !msg.isEmpty()){
            hc.enviarMensaje(msg);
        }

        // Para que la nave no se salga de los limites
        if(posicion.x < 150){
            posicion.x = 150;
        } else if(posicion.x >= 850-sprite.getWidth()){
            posicion.x = 850-sprite.getWidth();
        }

//        // Accion de disparar
//         else if(Gdx.input.isKeyPressed(Keys.UP) && posicion_disparo.y >= Gdx.graphics.getHeight() && id==2){
//            posicion_disparo.x = posicion.x + sprite.getWidth()/2 - sprite_disparo.getWidth()/2;// +(sprite_disparo.getWidth()/4);
//            posicion_disparo.y = posicion.y + sprite.getHeight();
//        }
    }

    // Metodo para dibujar la nave en la posicion actualizada
    public void Dibujar(SpriteBatch batch){
        sprite.setPosition(posicion.x, posicion.y);
        sprite.draw(batch);

        sprite_disparo.setPosition(posicion_disparo.x, posicion_disparo.y);
        sprite_disparo.draw(batch);
    }
}

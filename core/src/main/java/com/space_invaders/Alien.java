package com.space_invaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Alien {
    public boolean alive;  // Ahora es un campo por cada alien
    public Vector2 posicion;
    public Sprite sprite;

    public Alien(Vector2 posicion, Texture image, Color color){
        this.sprite = new Sprite(image);
        sprite.setColor(color);
        sprite.setSize(image.getWidth()*0.15f, image.getHeight()*0.15f);
        this.posicion = posicion;
        this.alive = true;  // Se inicializa como vivo
    }

    // Dibuja al alien individualmente si está vivo
    public void Dibujar(SpriteBatch batch){
        if(alive){
            this.sprite.setPosition(posicion.x, posicion.y);
            this.sprite.draw(batch);
        }
    }
}

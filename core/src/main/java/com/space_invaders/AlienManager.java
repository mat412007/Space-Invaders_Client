package com.space_invaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class AlienManager {
    private Alien[] aliens;
    private int aliensRestantes;

    float currentDropDistance = 0f; // Distancia recorrida entre bajadas
    float distanciaBajada = 50f;

    float direccionHorizontal = 1f;
    float direccionVertical = 1f;

    public AlienManager(int alto, int ancho, int espacio, Texture alien_img) {
        aliens = new Alien[ancho * alto];
        llenar(alto, ancho, aliens, espacio, alien_img);
        aliensRestantes = getAliens().length;
    }

    public Alien[] getAliens() {
        return aliens;
    }

    public void ActualizarMovimiento(float deltaTime) {

        boolean hayAliensVivos;
        // Variables para el descenso
        float alienTotalDropDistance = 50f; // 80
        final float alienHorizontalSpeed = 500f;
        float alienVerticalSpeed = 300f;

        // Si ya hay una distancia de descenso pendiente, hay que aplicarla primero
        if (currentDropDistance > 0f && currentDropDistance < alienTotalDropDistance) {

            float minY = Float.MAX_VALUE;
            float maxY = Float.MIN_VALUE;
            float alienHeight = aliens[0].sprite.getHeight();
            // Encontrar límites verticales
            for (Alien alien : aliens) {
                if (alien.alive) {
                    if (alien.sprite.getY() < minY) minY = alien.sprite.getY();
                    if (alien.sprite.getY() > maxY) maxY = alien.sprite.getY() + alienHeight;
                }
            }

            // Control de dirección vertical (rebote contra bordes superior e inferior)
            if (direccionVertical == -1f && maxY >= Gdx.graphics.getHeight()) {
                direccionVertical = 1f;
            } else if (direccionVertical == 1f && minY <= 0) {
                direccionVertical = -1f;
            }

            float descensoEnEsteFrame = alienVerticalSpeed * deltaTime * direccionVertical;

            // Ajustar para no salirse del borde
            float screenTop = Gdx.graphics.getHeight();
            float screenBottom = 0f;

            // Si se mueve hacia arriba (direccionVertical = -1)
            if (direccionVertical == -1f && (maxY - descensoEnEsteFrame) > screenTop) {
                float ajuste = screenTop - maxY;
                descensoEnEsteFrame = -ajuste;
            }
            // Si se mueve hacia abajo (direccionVertical = 1)
            else if (direccionVertical == 1f && (minY - descensoEnEsteFrame) < screenBottom) {
                float ajuste = screenBottom - minY;
                descensoEnEsteFrame = -ajuste;
            }

            // Aplica el descenso corregido
            for (Alien alien : aliens) {
                if (alien.alive) {
                    alien.posicion.y -= descensoEnEsteFrame;
                    alien.sprite.setPosition(alien.posicion.x, alien.posicion.y);
                }
            }

            // Actualiza la distancia recorrida verticalmente
            currentDropDistance += descensoEnEsteFrame * direccionVertical;

            // Si el descenso terminó, lo reiniciamos
            if (currentDropDistance >= alienTotalDropDistance) {
                currentDropDistance = 0.0f;
            }

            return; // No mover horizontalmente mientras desciende
        }

        // --- Movimiento horizontal ---
        float minX = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        hayAliensVivos = false;
        boolean tocaBorde = false;

        float alienWidth = aliens[0].sprite.getWidth();
        for (Alien alien : aliens) {
            if (alien.alive) {
                hayAliensVivos = true;
                if (alien.sprite.getX() < minX) minX = alien.sprite.getX();
                if (alien.sprite.getX() > maxX) maxX = alien.sprite.getX() + alienWidth;
            }
        }

        if (!hayAliensVivos) return;

        // Límites horizontales
        float screenLeft = 150;
        float screenRight = 850;

        // Cambia dirección si toca los bordes laterales
        if (direccionHorizontal == 1.0f && maxX >= screenRight) {
            direccionHorizontal = -1.0f; // Cambia a izquierda
            tocaBorde = true;
        } else if (direccionHorizontal == -1.0f && minX <= screenLeft) {
            direccionHorizontal = 1.0f; // Cambia a derecha
            tocaBorde = true;
        }

        // Si toca borde, iniciar el descenso (pausando el movimiento horizontal)
        if (tocaBorde) {
            currentDropDistance = 0.001f;
        }

        // Movimiento horizontal normal
        if (!tocaBorde) {
            float movimientoHorizontal = alienHorizontalSpeed * direccionHorizontal * deltaTime;

            for (Alien alien : aliens) {
                if (alien.alive) {
                    alien.posicion.x += movimientoHorizontal;
                    alien.sprite.setPosition(alien.posicion.x, alien.posicion.y);
                }
            }
        }
    }

    // Llenamos el array de los aliens al empezar la partida
    public static void llenar(int alto, int ancho, Alien[] aliens, int espacio, Texture alien_img){
        int i = 0;
        for (int x = 0; x < alto; x++) {
            for (int y = 0; y < ancho; y++) {
                Vector2 posicionAlien = new Vector2(y * espacio, x * espacio);
                posicionAlien.x += Gdx.graphics.getWidth() / 2f;
                posicionAlien.y += Gdx.graphics.getHeight();
                posicionAlien.x -= (ancho / 2f) * espacio;
                posicionAlien.y -= alto * espacio;
                aliens[i] = new Alien(posicionAlien, alien_img, Color.GREEN);
                i++;
            }
        }
    }

    // Verificar el impacto de la bala con el alien
    public boolean colisionConBala(Sprite spriteBala) {
        for(Alien alien : aliens){
            if(alien.sprite.getBoundingRectangle().overlaps(spriteBala.getBoundingRectangle()) && alien.alive){
                alien.alive = false;
                aliensRestantes--;
                return true;
            }
        }
        return false;
    }

    public boolean victoria(){
        return aliensRestantes == 0;
    } // Condicion para la victoria

    public void Dibujar(SpriteBatch batch) {
        for (Alien alien : aliens) {
            alien.Dibujar(batch);
        }
    }
}


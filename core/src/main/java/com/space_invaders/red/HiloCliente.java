package com.space_invaders.red;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.space_invaders.MyGame;
import com.space_invaders.screens.GameScreen;
import com.space_invaders.screens.MenuScreen;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class HiloCliente extends Thread {

    private DatagramSocket conexion;
    private InetAddress IPServidor;
    private int puerto = 9998;
    private boolean fin = false;

    public boolean empezar = false;
    public int idCliente;
    private GameScreen gameScreen;

    //Constructor
    public HiloCliente(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        try {
            conexion = new DatagramSocket();
            IPServidor = InetAddress.getByName("255.255.255.255");
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Se envia mensaje de conexion al servidor...");
        enviarMensaje("Conexion");
    }

    public void cerrar() {
        fin = true;
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
    }

    public void enviarMensaje(String msg) {
        byte[] data = msg.getBytes();
        DatagramPacket dp = new DatagramPacket(data, data.length, IPServidor, puerto);
        try {
            conexion.send(dp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        do {
            byte[] data = new byte[1024];
            DatagramPacket dp = new DatagramPacket(data, data.length);
            try {
                conexion.receive(dp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            procesarMensaje(dp);
        } while(!fin);
    }

    private void procesarMensaje(DatagramPacket dp) {
        String msg = new String(dp.getData()).trim();

        String[] mensajePartido = msg.split(":");

        if(mensajePartido[0].equals("OK")) {
            idCliente = Integer.parseInt(msg.substring(3));
            System.out.println("ID :" + idCliente);
            System.out.println("Conexion establecida con el servidor en " + dp.getAddress() + ":" + dp.getPort());
            IPServidor = dp.getAddress();
        }
        if(mensajePartido[0].equals("Empieza")) {
            empezar = true;
        }

        if(mensajePartido[0].equals("actualizarPosicion")){
            if(mensajePartido[1].equals("x")){
                int naveId = Integer.parseInt(mensajePartido[2]);
                gameScreen.actualizarPosicionNave(naveId, Float.parseFloat(mensajePartido[3]));
            }
            if(mensajePartido[1].equals("d")){
                int naveId = Integer.parseInt(mensajePartido[2]);
                gameScreen.actualizarPosicionDisparo(naveId, Float.parseFloat(mensajePartido[3]), Float.parseFloat(mensajePartido[4]));
            }
        }
        if (mensajePartido[0].equals("actualizarDisparo")) {
            int id = Integer.parseInt(mensajePartido[1]);
            float x = Float.parseFloat(mensajePartido[2]);
            float y = Float.parseFloat(mensajePartido[3]);
            gameScreen.actualizarPosicionDisparo(id, x, y);
        }

        System.out.println("Mensaje recibido: " + msg);
    }

}

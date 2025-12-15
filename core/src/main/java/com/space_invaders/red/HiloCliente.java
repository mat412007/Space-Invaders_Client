package com.space_invaders.red;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.space_invaders.MyGame;
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

    //Constructor
    public HiloCliente() {
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

        if(msg.startsWith("OK")) {
            idCliente = Integer.parseInt(msg.substring(3));
            System.out.println("ID :" + idCliente);
            System.out.println("Conexion establecida con el servidor en " + dp.getAddress() + ":" + dp.getPort());
            IPServidor = dp.getAddress();
        }
        if(msg.equals("Empieza")) {
            empezar = true;
        }

        System.out.println("Mensaje recibido: " + msg);
    }

}

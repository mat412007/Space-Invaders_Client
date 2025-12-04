package com.space_invaders.red;

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

    //Constructor
    public HiloCliente() {
        try {
            conexion = new DatagramSocket();
            IPServidor = InetAddress.getByName("255.255.255.255");
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
        enviarMensaje("Conexion");
    }

    private void enviarMensaje(String msg) {
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
        String msg = Arrays.toString(dp.getData()).trim();
        if(msg.equals("OK")) {
            IPServidor = dp.getAddress();
        } else if(msg.equals("Empieza")) {
            MenuScreen.empieza = true;
        }
    }

}

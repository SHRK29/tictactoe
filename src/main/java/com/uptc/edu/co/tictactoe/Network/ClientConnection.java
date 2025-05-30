package com.uptc.edu.co.tictactoe.Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ClientConnection {
    private static final Logger LOGGER = Logger.getLogger(ClientConnection.class.getName());
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private final Gson gson;
    private final AtomicBoolean isConnected;

    public ClientConnection(Socket socket) throws IOException {
        LOGGER.info("Iniciando conexión con el servidor en " + socket.getInetAddress() + ":" + socket.getPort());
        this.socket = socket;
        try {
            this.input = new DataInputStream(socket.getInputStream());
            this.output = new DataOutputStream(socket.getOutputStream());
            this.gson = new Gson();
            this.isConnected = new AtomicBoolean(true);
            LOGGER.info("Conexión establecida exitosamente");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al inicializar streams: " + e.getMessage(), e);
            throw e;
        }
    }

    public synchronized void sendRequest(String type, Map<String, Object> params) throws IOException {
        if (!isConnected.get()) {
            LOGGER.warning("Intento de enviar datos a través de una conexión cerrada");
            throw new IOException("La conexión está cerrada");
        }

        try {
            Request request = new Request(type, params);
            String json = gson.toJson(request);
            LOGGER.fine("Enviando petición: " + type);
            output.writeUTF(json);
            output.flush();
            LOGGER.fine("Petición enviada exitosamente");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al enviar petición: " + e.getMessage(), e);
            isConnected.set(false);
            throw e;
        }
    }

    public Response receiveResponse() throws IOException {
        if (!isConnected.get()) {
            LOGGER.warning("Intento de recibir datos a través de una conexión cerrada");
            throw new IOException("La conexión está cerrada");
        }

        try {
            String json = input.readUTF();
            Response response = gson.fromJson(json, Response.class);
            LOGGER.fine("Respuesta recibida: " + response.getType());
            return response;
        } catch (SocketException e) {
            LOGGER.log(Level.SEVERE, "Conexión perdida con el servidor: " + e.getMessage(), e);
            isConnected.set(false);
            throw new IOException("La conexión se ha perdido", e);
        } catch (JsonSyntaxException e) {
            LOGGER.log(Level.SEVERE, "Error al procesar respuesta del servidor: " + e.getMessage(), e);
            throw new IOException("Error al procesar la respuesta del servidor", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error de E/S al recibir respuesta: " + e.getMessage(), e);
            throw e;
        }
    }

    public void close() {
        if (isConnected.get()) {
            try {
                LOGGER.info("Cerrando conexión con el servidor");
                isConnected.set(false);
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
                LOGGER.info("Conexión cerrada exitosamente");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error al cerrar la conexión: " + e.getMessage(), e);
            }
        }
    }

    public boolean isConnected() {
        return isConnected.get() && socket != null && !socket.isClosed();
    }

    public Socket getSocket() {
        return socket;
    }
}
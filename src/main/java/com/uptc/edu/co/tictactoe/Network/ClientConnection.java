package com.uptc.edu.co.tictactoe.Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ClientConnection {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private final Gson gson;
    private final AtomicBoolean isConnected;

    public ClientConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
        this.gson = new Gson();
        this.isConnected = new AtomicBoolean(true);
    }

    public synchronized void sendRequest(String type, Map<String, Object> params) throws IOException {
        if (!isConnected.get()) {
            throw new IOException("La conexión está cerrada");
        }

        try {
            Request request = new Request(type, params);
            String json = gson.toJson(request);
            output.writeUTF(json);
            output.flush();
        } catch (IOException e) {
            isConnected.set(false);
            throw e;
        }
    }

    public Response receiveResponse() throws IOException {
        if (!isConnected.get()) {
            throw new IOException("La conexión está cerrada");
        }

        try {
            String json = input.readUTF();
            return gson.fromJson(json, Response.class);
        } catch (SocketException e) {
            isConnected.set(false);
            throw new IOException("La conexión se ha perdido", e);
        } catch (JsonSyntaxException e) {
            throw new IOException("Error al procesar la respuesta del servidor", e);
        }
    }

    public synchronized void close() {
        if (isConnected.compareAndSet(true, false)) {
            try {
                if (input != null) input.close();
                if (output != null) output.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException e) {
                System.err.println("Error cerrando la conexión: " + e.getMessage());
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
package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.Scanner;
import java.util.Vector;

public class Server {
    private static Connection connection;
    private static Statement stmt;




    private Vector<ClientHandler> clients;
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public Server() {

        clients = new Vector<>();
        authService = new SimpleAuthService();
        ServerSocket server = null;
        Socket socket = null;

        try {
            connect();
            System.out.println("Соединение с БД произошло");

            server = new ServerSocket(8189);
            System.out.println("Сервер запустился");

            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(socket, this);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                disconnect();
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMsg(String nick, String msg) {
        for (ClientHandler c : clients) {
            c.sendMsg(nick + " : " + msg);
        }
    }

    public void privateMsg(ClientHandler sender, String receiver, String msg) {
        String message = String.format("[ %s ] private [ %s ] : %s", sender.getNick(), receiver, msg);

        for (ClientHandler c : clients) {
            if (c.getNick().equals(receiver)) {
                c.sendMsg(message);
                if (!sender.getNick().equals(receiver)) {
                    sender.sendMsg(message);
                }
                return;
            }
        }

        sender.sendMsg("not found user :" + receiver);
    }


    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientList();
    }


    public boolean isLoginAuthorized(String login) {
        for (ClientHandler c : clients) {
            if (c.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }

    public void broadcastClientList() {
        StringBuilder sb = new StringBuilder("/clientlist ");

        for (ClientHandler c : clients) {
            sb.append(c.getNick()).append(" ");
        }

        String msg = sb.toString();
        for (ClientHandler c : clients) {
            c.sendMsg(msg);
        }
    }



    public static void connect() throws ClassNotFoundException, SQLException { //метод соединения к Базы данных
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:mainDB.db");
        stmt = connection.createStatement();
    }

    public static void disconnect() { //метод дисконнекта от Базы данных
        try {
            stmt.close(); //закрываем объект создания запросов
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();  //закрываем соединение
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void readUsers() throws SQLException {//метод считываня из БД
        //делаем запрос из БД:
        ResultSet rs = stmt.executeQuery("SELECT nick, pass, login FROM users ");
        while (rs.next()) { //обход нашей таблицы
            System.out.println(rs.getString("nick") + " " + rs.getString("pass") + " " + rs.getString("login"));
        }
        rs.close(); //после того как обошли таблицу, закрываем
    }

    public static Statement getStmt() {
        return stmt;
    }



    public static Connection getConnection() {
        return connection;
    }

}

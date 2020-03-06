package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SimpleAuthService implements AuthService {
    private static PreparedStatement psInsert; //подготовленные запросы

    private class UserData {
        String login;
        String password;
        String nickname;

        public UserData(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }
    }

    private List<UserData> users;

    public SimpleAuthService() {
        users = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            users.add(new UserData("login" + i, "pass" + i, "nick" + i));
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for (UserData o : users) {
            if (o.login.equals(login) && o.password.equals(password)) {
                return o.nickname;
            }
        }
        return null;
    }



    public String getNicknameByLoginAndPasswordfromDB(String login, String password) throws SQLException {

        ResultSet rs = Server.getStmt().executeQuery("SELECT nick, pass, login FROM users ");
        while (rs.next()) { //обход нашей таблицы
            if(rs.getString("login").equals(login) && rs.getString("pass").equals(password)){
                return rs.getString("nick");
            }
            System.out.println(rs.getString("nick") + " " + rs.getString("pass"));
        }
        rs.close(); //после того как обошли таблицу, закрываем
        return null;
    }



    @Override
    public boolean registration(String login, String password, String nickname) {
        for (UserData o : users) {
            if (o.login.equals(login)) {
                return false;
            }
        }

        if (password.trim().equals("")) {
            return false;
        }

        users.add(new UserData(login, password, nickname));
        return true;
    }


}

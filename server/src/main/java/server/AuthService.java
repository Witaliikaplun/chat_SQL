package server;

import java.sql.SQLException;

public interface AuthService {

    boolean registration(String login, String password, String nickname);
    String getNicknameByLoginAndPassword(String login, String password);
    String getNicknameByLoginAndPasswordfromDB(String s, String s1) throws SQLException;


}

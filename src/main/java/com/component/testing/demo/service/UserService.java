package com.component.testing.demo.service;

import com.component.testing.demo.entity.User;
import com.component.testing.demo.helper.DBConnectionProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.testcontainers.shaded.com.google.common.annotations.VisibleForTesting;

public class UserService {

    private final DBConnectionProvider connectionProvider;

    public UserService(DBConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        createUsersTableIfNotExists();
    }

    public void createUser(User user) {
        try (Connection conn = this.connectionProvider.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("insert into users(id,name) values(?,?)");
            pstmt.setLong(1, user.id());
            pstmt.setString(2, user.name());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try (Connection conn = this.connectionProvider.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("select id,name from users");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                users.add(new User(id, name));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    private void createUsersTableIfNotExists() {
        try (Connection conn = this.connectionProvider.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(
                """
                create table if not exists users (
                    id bigint not null,
                    name varchar not null,
                    primary key (id)
                )
                """
            );
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @VisibleForTesting
    void clearAll() {
        try (Connection conn = this.connectionProvider.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(
                """
                delete from users where 1=1
                """
            );
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

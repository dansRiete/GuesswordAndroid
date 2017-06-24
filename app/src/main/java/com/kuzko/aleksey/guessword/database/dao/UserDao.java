package com.kuzko.aleksey.guessword.database.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.kuzko.aleksey.guessword.data.User;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Aleks on 20.06.2017.
 */

public class UserDao extends BaseDaoImpl<User, String> {
    public UserDao(ConnectionSource connectionSource, Class<User> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }
    public List<User> retrieveAll() throws SQLException{
        return this.queryForAll();
    }
}

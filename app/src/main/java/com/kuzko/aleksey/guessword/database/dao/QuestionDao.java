package com.kuzko.aleksey.guessword.database.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.kuzko.aleksey.guessword.data.Question;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Aleks on 10.06.2017.
 */

public class QuestionDao extends BaseDaoImpl<Question, Long> {

    public QuestionDao(ConnectionSource connectionSource, Class<Question> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Question> retrieveAll() throws SQLException{
        return this.queryForAll();
    }
}


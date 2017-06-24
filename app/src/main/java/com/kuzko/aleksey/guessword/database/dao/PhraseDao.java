package com.kuzko.aleksey.guessword.database.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.kuzko.aleksey.guessword.data.Phrase;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Aleks on 10.06.2017.
 */

public class PhraseDao extends BaseDaoImpl<Phrase, Long> {

    public PhraseDao(ConnectionSource connectionSource, Class<Phrase> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Phrase> retrieveAll() throws SQLException{
        return this.queryForAll();
    }
}

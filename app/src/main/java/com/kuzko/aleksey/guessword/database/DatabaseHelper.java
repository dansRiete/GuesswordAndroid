package com.kuzko.aleksey.guessword.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.kuzko.aleksey.guessword.database.dao.PhraseDao;
import com.kuzko.aleksey.guessword.database.dao.QuestionDao;
import com.kuzko.aleksey.guessword.database.dao.UserDao;
import com.kuzko.aleksey.guessword.data.Phrase;
import com.kuzko.aleksey.guessword.data.Question;
import com.kuzko.aleksey.guessword.data.User;

import java.sql.SQLException;

/**
 * Created by Aleks on 10.06.2017.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME ="guessword.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private PhraseDao phraseDao;
    private QuestionDao questionDao;
    private UserDao userDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try
        {
            TableUtils.createTable(connectionSource, Phrase.class);
            TableUtils.createTable(connectionSource, Question.class);
            TableUtils.createTable(connectionSource, User.class);
        }
        catch (SQLException e){
            Log.e(TAG, "error creating DB " + DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try{
            TableUtils.dropTable(connectionSource, Phrase.class, true);
            TableUtils.dropTable(connectionSource, Question.class, true);
            TableUtils.dropTable(connectionSource, User.class, true);
            onCreate(database, connectionSource);
        }
        catch (SQLException e){
            Log.e(TAG,"error upgrading db "+DATABASE_NAME+"from ver " + oldVersion);
            throw new RuntimeException(e);
        }
    }

    public PhraseDao getPhraseDao() throws SQLException{
        if(phraseDao == null){
            phraseDao = new PhraseDao(getConnectionSource(), Phrase.class);
        }
        return phraseDao;
    }

    public QuestionDao getQuestionDao() throws SQLException{
        if(questionDao == null){
            questionDao = new QuestionDao(getConnectionSource(), Question.class);
        }
        return questionDao;
    }

    public UserDao getUserDao() throws SQLException{
        if(userDao == null){
            userDao = new UserDao(getConnectionSource(), User.class);
        }
        return userDao;
    }

    @Override
    public void close(){
        super.close();
        phraseDao = null;
        questionDao = null;
    }
}

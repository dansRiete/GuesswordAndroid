package com.kuzko.aleksey.guessword;

import com.kuzko.aleksey.guessword.datamodel.Phrase;
import com.kuzko.aleksey.guessword.datamodel.User;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Aleks on 03.05.2017.
 */

public interface GuesswordService {

//    String BASE_GUESSWORD_URL = "http://192.168.0.105:8080/guessword-1/rest/";
    String BASE_GUESSWORD_URL = "http://192.168.1.2:8080/guessword-1/rest/";

    @GET("phrases")
    Observable<Response<List<Phrase>>> fetchAllPhrases(@Query("user_id") long userId);

    @GET("users")
    Observable<Response<List<User>>> fetchAllUsers();
}

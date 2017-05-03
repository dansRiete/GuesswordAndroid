package com.kuzko.aleksey.guessword;

import com.kuzko.aleksey.guessword.datamodel.Phrase;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Aleks on 03.05.2017.
 */

public interface GuesswordService {
    @GET("rest/phrases")
    Observable<List<Phrase>> getPhrases(@Query("userId") long userId);
}

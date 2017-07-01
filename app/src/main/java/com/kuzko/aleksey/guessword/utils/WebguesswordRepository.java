package com.kuzko.aleksey.guessword.utils;

import com.kuzko.aleksey.guessword.data.Phrase;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Aleks on 27.06.2017.
 */

public interface WebguesswordRepository {

    @GET("phrases")
    Observable<List<Phrase>> retrievePhrases();
}

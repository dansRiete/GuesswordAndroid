package com.kuzko.aleksey.guessword.exceptions;

/**
 * Created by Aleks on 10.06.2017.
 */

public class EmptyCollectionException extends Exception {
    public EmptyCollectionException(){
        super();
    }
    public EmptyCollectionException(String s){
        super(s);
    }
}

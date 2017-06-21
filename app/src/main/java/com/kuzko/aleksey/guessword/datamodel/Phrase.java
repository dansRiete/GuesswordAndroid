package com.kuzko.aleksey.guessword.datamodel;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by Aleks on 11.05.2016.
 */

@Entity
@Table(name = "words")
public class Phrase implements Serializable {

    @Transient
    public static final double TRAINED_PROBABILITY_FACTOR = 3;
    @Transient
    public static final double DEFAULT_PROBABILITY_FACTOR = 30;
    @Transient
    public static final double DEFAULT_MULTIPLIER = 1;

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField()
    private String foreignWord;

    @DatabaseField()
    private String nativeWord;

    @DatabaseField()
    private String transcription;

    @DatabaseField()
    private double probabilityFactor;

    @DatabaseField()
    private String label;

    @DatabaseField()
    private Date collectionAddingDateTime;

    @DatabaseField()
    private Date lastAccessDateTime;

    @DatabaseField()
    private double multiplier;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    public User user;

    @DatabaseField()
    private boolean isDeleted;

    @Transient
    private int indexStart;

    @Transient
    private int indexEnd;

    public Phrase() {
    }

    public Phrase(String foreignWord, String nativeWord, String transcription, String label, User user){
        this.user = user;
        this.foreignWord = foreignWord;
        this.nativeWord = nativeWord;
        this.transcription = transcription == null ? "" : transcription;
        this.probabilityFactor = DEFAULT_PROBABILITY_FACTOR;
        this.collectionAddingDateTime = new Date(System.currentTimeMillis());
        this.label = (label == null ? "" : label);
        this.multiplier = DEFAULT_MULTIPLIER;
    }

    public boolean isInList(HashSet<String> phrasesList){

        if(phrasesList != null){
            if(phrasesList.isEmpty()) {
                return true;
            }
            for(String str : phrasesList){
                if(this.label != null && this.label.equalsIgnoreCase(str)){
                    return true;
                }
            }
            return false;
        }else{
            return true;
        }
    }

    public boolean isTrained(){
        return probabilityFactor <= TRAINED_PROBABILITY_FACTOR;
    }

    @Override
    public String toString() {
        return "Phrase = {" + id + ". " + foreignWord + " - " + nativeWord  + (transcription == null || transcription.equals("") ? "" : " [" + transcription + "]") + ", prob=" + probabilityFactor + ", col add = " + collectionAddingDateTime + ", multiplier = " + multiplier + "}";
    }

    @Override
    public int hashCode() {
        return foreignWord.hashCode() * (nativeWord.hashCode() + 21);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Phrase phrase = (Phrase) o;

        return id == phrase.id;

    }

    //Setters and getters

    public long getId() {
        return id;
    }
    public String getForeignWord(){
        return foreignWord;
    }
    public void setForeignWord(String foreignWord) {
        this.foreignWord = foreignWord;
    }
    public String getNativeWord() {
        return nativeWord;
    }
    public void setNativeWord(String nativeWord) {
        this.nativeWord = nativeWord;
    }
    public String getTranscription() {
        return transcription;
    }
    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }
    public double getProbabilityFactor() {
        return probabilityFactor;
    }
    public void setProbabilityFactor(double probabilityFactor) {
        this.probabilityFactor = probabilityFactor;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public Date getCollectionAddingDateTime() {
        return collectionAddingDateTime;
    }

    public void setCollectionAddingDateTime(Timestamp collectionAddingDateTime) {
        this.collectionAddingDateTime = collectionAddingDateTime;
    }
    public Date getLastAccessDateTime() {
        return lastAccessDateTime;
    }
    public void setLastAccessDateTime(Timestamp lastAccessDateTime){
        this.lastAccessDateTime = lastAccessDateTime;
    }
    public int getIndexStart() {
        return indexStart;
    }
    public void setIndexStart(int indexStart) {
        this.indexStart = indexStart;
    }
    public int getIndexEnd() {
        return indexEnd;
    }
    public void setIndexEnd(int indexEnd) {
        this.indexEnd = indexEnd;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public double getMultiplier() {
        return multiplier;
    }
    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }


}

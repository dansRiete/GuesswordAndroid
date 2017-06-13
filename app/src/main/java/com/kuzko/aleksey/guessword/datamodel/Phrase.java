package com.kuzko.aleksey.guessword.datamodel;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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

    @javax.persistence.Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @Column(name = "for_word")
    private String foreignWord;

    @Column(name = "nat_word")
    private String nativeWord;

    @Column(name = "transcr")
    private String transcription;

    @Column(name = "prob_factor")
    private double probabilityFactor;

    @Column
    private String label;

    @Column(name = "create_date")
    private Date collectionAddingDateTime;

    @Column(name = "last_accs_date")
    private Date lastAccessDateTime;

    @Column(name = "rate")
    private double multiplier;

    /*@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    public User user;*/

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Transient
    private int indexStart;

    @Transient
    private int indexEnd;

    public Phrase() {
    }

    public Phrase(long id, String foreignWord, String nativeWord, String transcription, String label /*, User user*/){
        this.id = id;
//        this.user = user;
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
        return id + ". " + foreignWord + " - " + nativeWord  + (transcription == null || transcription.equals("") ? "" : " [" + transcription + "]");
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
    /*public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }*/
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

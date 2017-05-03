
package com.kuzko.aleksey.guessword.datamodel;


public class Phrase {

    private long id;
    private String foreignWord;
    private String nativeWord;
    private String transcription;
    private double probabilityFactor;
    private String label;
    private long collectionAddingDateTime;
    private long lastAccessDateTime;
    private double multiplier;
    private User user;
    private boolean isDeleted;
    private long indexStart;
    private long indexEnd;
    private boolean trained;

    public String toString(){
        return id + ". " + foreignWord + " - " + nativeWord + "\n";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getForeignWord() {
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

    public long getCollectionAddingDateTime() {
        return collectionAddingDateTime;
    }

    public void setCollectionAddingDateTime(long collectionAddingDateTime) {
        this.collectionAddingDateTime = collectionAddingDateTime;
    }

    public long getLastAccessDateTime() {
        return lastAccessDateTime;
    }

    public void setLastAccessDateTime(long lastAccessDateTime) {
        this.lastAccessDateTime = lastAccessDateTime;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public long getIndexStart() {
        return indexStart;
    }

    public void setIndexStart(long indexStart) {
        this.indexStart = indexStart;
    }

    public long getIndexEnd() {
        return indexEnd;
    }

    public void setIndexEnd(long indexEnd) {
        this.indexEnd = indexEnd;
    }

    public boolean isTrained() {
        return trained;
    }

    public void setTrained(boolean trained) {
        this.trained = trained;
    }

}

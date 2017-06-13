package com.kuzko.aleksey.guessword;

import com.kuzko.aleksey.guessword.database.HelperFactory;
import com.kuzko.aleksey.guessword.datamodel.Phrase;
import com.kuzko.aleksey.guessword.datamodel.Question;
import com.kuzko.aleksey.guessword.exceptions.EmptyCollectionException;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by Aleks on 04.05.2017.
 */

public class GuesswordRepository {

    private static GuesswordRepository instance;
    private List<Phrase> phrases;
    private Random random;
    private int maxPhraseIndex;
    private int activePhrasesNumber;
    private int activeUntrainedPhrasesNumber;
    private int activeTrainedPhrasesNumber;
    private static final double CHANCE_OF_APPEARING_TRAINED_PHRASES = 1D / 35D;
    private HashSet<String> selectedLabels;

    private GuesswordRepository(){
        try {
            phrases = HelperFactory.getHelper().getPhraseDao().retrieveAll();
            reloadIndices();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error during retrieving phrases collection from DB");
        }
    }

    public static GuesswordRepository getInstance(){
        if(instance == null){
            instance = new GuesswordRepository();
        }
        return instance;
    }

    public Question askQuestion() throws EmptyCollectionException{
        return Question.compose(retrieveRandomPhrase());
    }

    public Phrase retrieveRandomPhrase() throws EmptyCollectionException{
        if(phrases.isEmpty()){
            throw new EmptyCollectionException();
        }
        return retrievePhraseByIndex(random.nextInt(maxPhraseIndex));
    }

    private Phrase retrievePhraseByIndex(int index) {

        for (Phrase phrase : phrases) {
            if (phrase.isInList(selectedLabels) && index >= phrase.indexStart && index <= phrase.indexEnd) {
                return phrase;
            }
        }
        return null;
    }

    public void reloadIndices() {
        System.out.println("reloadIndices() from PhraseRepository");

        if(phrases.isEmpty()){
            return;
        }

        final long RANGE = 1_000_000_000;
        long startTime = System.currentTimeMillis();
        double temp = 0;
        double indexOfTrained;     //Index of appearing learnt words
        double rangeOfUnTrained;  //Ranhe indices non learnt words
        double scaleOfOneProb;
        int modificatePhrasesIndicesNumber = 0;
        int untrainedPhrasesProbabilityFactorsSumm = 0;
        this.activePhrasesNumber = 0;
        this.activeUntrainedPhrasesNumber = 0;

        for (Phrase phr : phrases) {
            phr.indexStart = phr.indexEnd = 0;
            if(phr.isInList(selectedLabels)){
                this.activePhrasesNumber++;
                if(phr.probabilityFactor > 3){
                    this.activeUntrainedPhrasesNumber++;
                    untrainedPhrasesProbabilityFactorsSumm += phr.probabilityFactor;
                }
            }
        }

        this.activeTrainedPhrasesNumber = activePhrasesNumber - activeUntrainedPhrasesNumber;
        indexOfTrained = CHANCE_OF_APPEARING_TRAINED_PHRASES / activeTrainedPhrasesNumber;
        rangeOfUnTrained = activeTrainedPhrasesNumber > 0 ? 1 - CHANCE_OF_APPEARING_TRAINED_PHRASES : 1;
        scaleOfOneProb = rangeOfUnTrained / untrainedPhrasesProbabilityFactorsSumm;

        for (Phrase currentPhrase : phrases) { //Sets indices for nonlearnt words
            if(currentPhrase.isInList(selectedLabels)){
                int indexStart;
                int indexEnd;
                double prob;
                prob = currentPhrase.probabilityFactor;

                //If activeUntrainedPhrasesNumber == 0 then all words have been learnt, setting equal for all indices
                if (activeUntrainedPhrasesNumber == 0) {

                    indexStart = (int) (temp * RANGE);
                    currentPhrase.indexStart = indexStart;
                    temp += CHANCE_OF_APPEARING_TRAINED_PHRASES / activeTrainedPhrasesNumber;
                    indexEnd = (int) ((temp * RANGE) - 1);
                    currentPhrase.indexEnd = indexEnd;

                } else { //Otherwise, set indices by algorithm

                    if (prob > 3) {

                        indexStart = (int) (temp * RANGE);
                        currentPhrase.indexStart = indexStart;
                        temp += scaleOfOneProb * prob;
                        indexEnd = (int) ((temp * RANGE) - 1);
                        currentPhrase.indexEnd = indexEnd;
//                        System.out.println("Index Start = " + indexStart + ", Index End = " + indexEnd);

                    } else {

                        indexStart = (int) (temp * RANGE);
                        currentPhrase.indexStart = indexStart;
                        temp += indexOfTrained;
                        indexEnd = (int) ((temp * RANGE) - 1);
                        currentPhrase.indexEnd = indexEnd;
//                        System.out.println("Index Start = " + indexStart + ", Index End = " + indexEnd);
                    }
                }

                modificatePhrasesIndicesNumber++;
                if(modificatePhrasesIndicesNumber == activePhrasesNumber){
                    this.maxPhraseIndex = currentPhrase.indexEnd;
                }
            }
        }
        System.out.println("CALL: reloadIndices() from PhrasesRepository," + " Indexes changed=" + modificatePhrasesIndicesNumber + ", Time taken " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public void updateProb(Phrase askedPhrase) {

    }

    public void updateQuestion(Question question) {

    }

    public void persistQuestion(Question question) {

    }
}

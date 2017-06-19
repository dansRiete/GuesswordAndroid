package com.kuzko.aleksey.guessword;

import android.util.Log;

import com.kuzko.aleksey.guessword.database.HelperFactory;
import com.kuzko.aleksey.guessword.database.dao.PhraseDao;
import com.kuzko.aleksey.guessword.database.dao.QuestionDao;
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
    private List<Phrase> allPhrases;

    private List<Question> todaysQuestions;
    private Random random = new Random();
    private PhraseDao phraseDao;
    private QuestionDao questionDao;
    private int maxPhraseIndex;
    private int activePhrasesNumber;
    private int activeUntrainedPhrasesNumber;
    private int activeTrainedPhrasesNumber;
    private static final double CHANCE_OF_APPEARING_TRAINED_PHRASES = 1D / 35D;
    private HashSet<String> selectedLabels;

    private GuesswordRepository(){

        try {
            phraseDao = HelperFactory.getHelper().getPhraseDao();
            questionDao = HelperFactory.getHelper().getQuestionDao();
            allPhrases = phraseDao.retrieveAll();
            todaysQuestions = questionDao.retrieveAll();
            /*System.out.println("todaysQuestions");
            for(Question q : todaysQuestions){
                System.out.println(q.getAskedPhrase());
            }*/
            reloadIndices();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error during retrieving allPhrases collection from DB");
        }
    }

    public static GuesswordRepository getInstance(){
        if(instance == null){
            instance = new GuesswordRepository();
        }
        return instance;
    }

    public Question askQuestion() throws EmptyCollectionException, SQLException{
        Question askedQuestion = Question.compose(retrieveRandomPhrase());
        todaysQuestions.add(0, askedQuestion);
//        HelperFactory.getHelper().getQuestionDao().create(askedQuestion);
        return askedQuestion;
    }

    public Question getCurrentQuestion(){
        Question question = null;
        if(!todaysQuestions.isEmpty())
            question = todaysQuestions.get(0);
        return question;
    }

    public Phrase retrieveRandomPhrase() throws EmptyCollectionException{
        if(allPhrases.isEmpty()){
            throw new EmptyCollectionException();
        }
        return retrievePhraseByIndex(random.nextInt(maxPhraseIndex));
    }

    public void createPhrase(Phrase addedPhrase){
        if(addedPhrase == null){
            throw new IllegalArgumentException("Phrase can not be null");
        }
        allPhrases.add(addedPhrase);
        try {
            HelperFactory.getHelper().getPhraseDao().create(addedPhrase);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Something went wrong during creating Phrase in DB");
        }
        reloadIndices();
    }

    private Phrase retrievePhraseByIndex(int index) {

        for (Phrase phrase : allPhrases) {
            if (phrase.isInList(selectedLabels) && index >= phrase.getIndexStart() && index <= phrase.getIndexEnd()) {
                return phrase;
            }
        }
        return null;
    }

    private void reloadIndices() {
        System.out.println("reloadIndices() from PhraseRepository");

        if(allPhrases.isEmpty()){
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

        for (Phrase phr : allPhrases) {
            phr.setIndexStart(0);
            phr.setIndexEnd(0);
            if(phr.isInList(selectedLabels)){
                this.activePhrasesNumber++;
                if(phr.getProbabilityFactor() > 3){
                    this.activeUntrainedPhrasesNumber++;
                    untrainedPhrasesProbabilityFactorsSumm += phr.getProbabilityFactor();
                }
            }
        }

        this.activeTrainedPhrasesNumber = activePhrasesNumber - activeUntrainedPhrasesNumber;
        indexOfTrained = CHANCE_OF_APPEARING_TRAINED_PHRASES / activeTrainedPhrasesNumber;
        rangeOfUnTrained = activeTrainedPhrasesNumber > 0 ? 1 - CHANCE_OF_APPEARING_TRAINED_PHRASES : 1;
        scaleOfOneProb = rangeOfUnTrained / untrainedPhrasesProbabilityFactorsSumm;

        for (Phrase currentPhrase : allPhrases) { //Sets indices for nonlearnt words
            if(currentPhrase.isInList(selectedLabels)){
                int indexStart;
                int indexEnd;
                double prob;
                prob = currentPhrase.getProbabilityFactor();

                //If activeUntrainedPhrasesNumber == 0 then all words have been learnt, setting equal for all indices
                if (activeUntrainedPhrasesNumber == 0) {

                    indexStart = (int) (temp * RANGE);
                    currentPhrase.setIndexStart(indexStart);
                    temp += CHANCE_OF_APPEARING_TRAINED_PHRASES / activeTrainedPhrasesNumber;
                    indexEnd = (int) ((temp * RANGE) - 1);
                    currentPhrase.setIndexEnd(indexEnd);

                } else { //Otherwise, set indices by algorithm

                    if (prob > 3) {

                        indexStart = (int) (temp * RANGE);
                        currentPhrase.setIndexStart(indexStart);
                        temp += scaleOfOneProb * prob;
                        indexEnd = (int) ((temp * RANGE) - 1);
                        currentPhrase.setIndexEnd(indexEnd);
//                        System.out.println("Index Start = " + indexStart + ", Index End = " + indexEnd);

                    } else {

                        indexStart = (int) (temp * RANGE);
                        currentPhrase.setIndexStart(indexStart);
                        temp += indexOfTrained;
                        indexEnd = (int) ((temp * RANGE) - 1);
                        currentPhrase.setIndexEnd(indexEnd);
//                        System.out.println("Index Start = " + indexStart + ", Index End = " + indexEnd);
                    }
                }

                modificatePhrasesIndicesNumber++;
                if(modificatePhrasesIndicesNumber == activePhrasesNumber){
                    this.maxPhraseIndex = currentPhrase.getIndexEnd();
                }
            }
        }
        System.out.println("CALL: reloadIndices() from PhrasesRepository," + " Indexes changed=" + modificatePhrasesIndicesNumber + ", Time taken " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public void updateProb(Phrase askedPhrase) {

    }

    public void updateQuestion(Question question) throws SQLException {
        questionDao.update(question);
    }

    public void persistQuestion(Question question) throws SQLException {
        questionDao.create(question);
    }

    public List<Question> getTodaysQuestions() {
        Log.i(getClass().getName(), todaysQuestions.toString());
        for(Question question : todaysQuestions){
            question.setAnswered(true);
        }
        return todaysQuestions;
    }
}

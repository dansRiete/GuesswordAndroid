package com.kuzko.aleksey.guessword.data;

import android.util.Log;

import com.kuzko.aleksey.guessword.MyApplication;
import com.kuzko.aleksey.guessword.database.HelperFactory;
import com.kuzko.aleksey.guessword.database.dao.PhraseDao;
import com.kuzko.aleksey.guessword.database.dao.QuestionDao;
import com.kuzko.aleksey.guessword.exceptions.EmptyCollectionException;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Aleks on 04.05.2017.
 */

public class GuesswordRepository {

    private static GuesswordRepository instance;

    public List<Phrase> getAllPhrases() {
        return allPhrases;
    }

    private List<Phrase> allPhrases;
    private static MyApplication application;
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
            allPhrases = phraseDao.queryForEq("user_id" , application.retrieveLoggedUser().getLogin());
            todaysQuestions = new LinkedList<>(questionDao.queryBuilder().orderBy("askDate", false).query());
            for(Question question : todaysQuestions){
                question.setAnswered(true);
            }
            Log.i(getClass().getSimpleName(), "GuesswordRepository init completed, allPhrases.size = " +
                                              allPhrases.size() + ", todaysQuestions.size = " + todaysQuestions.size());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error during retrieving allPhrases collection from DB");
        }
    }

    public static GuesswordRepository getInstance(){
        if(application == null){
            throw new IllegalStateException("You must invoke GuesswordRepository.init() first!");
        }
        if(instance == null){
            instance = new GuesswordRepository();
        }
        return instance;
    }



    public static void init(MyApplication _application){
        application = _application;
    }

    public Question askQuestion() throws EmptyCollectionException{

        Phrase randomPhrase = retrieveRandomPhrase();
        Question askedQuestion = Question.compose(randomPhrase);
        todaysQuestions.add(0, askedQuestion);
        return askedQuestion;
    }

    public Question getCurrentQuestion(){
        Question question = null;
        if(!todaysQuestions.isEmpty())
            question = todaysQuestions.get(0);
        return question;
    }


    public Question getPreviousQuestion(){
        Question question = null;
        if(todaysQuestions.size() >= 2)
            question = todaysQuestions.get(1);
        return question;
    }

    public Phrase retrieveRandomPhrase() throws EmptyCollectionException{
        reloadIndices();
        if(allPhrases.isEmpty()){
            throw new EmptyCollectionException();
        }
        int randomIndex = random.nextInt(maxPhraseIndex);
        Phrase phrase = retrievePhraseByIndex(randomIndex);
        if(phrase == null){
            throw new RuntimeException("Retrieved phase was null. RandomIndex = " + randomIndex +
                    ", maxPhraseIndex = " + maxPhraseIndex);
        }
        phrase.setLastAccessDateTime(new Date(System.currentTimeMillis()));
        return phrase;
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
    }

    private Phrase retrievePhraseByIndex(int index) {

        for (Phrase phrase : allPhrases) {

            if (index >= phrase.getIndexStart() && index <= phrase.getIndexEnd()) {
                return phrase;
            }
        }
        return null;
    }

    private void reloadIndices() {
        Log.i(getClass().getSimpleName(), "reloadIndices() from PhraseRepository");

        double NANOSECONDS_IN_ONE_MILLISECOND = 1000000;

        if(allPhrases.isEmpty()){
            return;
        }

        final long RANGE = 1_000_000_000;
        long startTime = System.nanoTime();
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
                    Log.i(getClass().getSimpleName(), "All phrases are learnt");

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
        double timeTaken = ((double) (System.nanoTime() - startTime)) / NANOSECONDS_IN_ONE_MILLISECOND;
        Log.i(getClass().getSimpleName(), modificatePhrasesIndicesNumber + " indexes were changed, " +
                                          timeTaken + "ms taken");
    }

    public void updateQuestion(final Question question) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HelperFactory.getHelper().getPhraseDao().update(question.getAskedPhrase());
                    questionDao.update(question);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void persistQuestion(final Question question) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HelperFactory.getHelper().getPhraseDao().update(question.getAskedPhrase());
                    questionDao.create(question);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public List<Question> getTodaysQuestions() {
        return todaysQuestions;
    }

    public static void close(){
        instance = null;
        application = null;
    }
}

package com.kuzko.aleksey.guessword.data;

import com.j256.ormlite.field.DatabaseField;
import com.kuzko.aleksey.guessword.utils.AnswerChecker;
import com.kuzko.aleksey.guessword.utils.Hints;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by Aleks on 11.11.2016.
 */

@Entity
@Table(name = "questions")
public class Question implements Serializable {

    @Transient
    private static final double RIGHT_ANSWER_MULTIPLIER = 1.44;

    @Transient
    private static final double RIGHT_ANSWER_SUBTRAHEND = 3;

    @Transient
    private static final double WRONG_ANSWER_ADDEND = 6;

    @Transient
    private static final double TRAINED_PROBABILITY_FACTOR = 3;

    @Transient
    private static final int PROBABILITY_FACTOR_ACCURACY = 1;

    @Transient
    private static final int MULTIPLIER_ACCURACY = 2;

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField()
    private String answerLiteral;

    @DatabaseField()
    private Date askDate;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Phrase askedPhrase;

    @DatabaseField()
    private boolean answerCorrect;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private User user;

    @DatabaseField()
    private double initialProbabilityFactor;

    @DatabaseField()
    private double initialProbabilityMultiplier;

    @DatabaseField()
    private double afterAnswerProbabilityFactor;

    @DatabaseField()
    private double afterAnswerProbabilityMultiplier;

    @Transient
    private long initStartIndex;

    @Transient
    private long initEndIndex;

    @Transient
    private long afterAnswerStartIndex;

    @Transient
    private long afterAnswerEndIndex;

    @Transient
    private Date initLastAccessDate;

    @Transient
    private boolean answered;

    public Question() {
    }

    private Question(Phrase askedPhrase) {
        System.out.println("CALL: Question(Phrase askedPhrase, PhrasesRepository phrasesRepository) from Question");

        this.askedPhrase = askedPhrase;
        this.initialProbabilityFactor = askedPhrase.getProbabilityFactor();
        this.initialProbabilityMultiplier = askedPhrase.getMultiplier();
        this.initStartIndex = askedPhrase.getIndexStart();
        this.initEndIndex = askedPhrase.getIndexEnd();
        this.initLastAccessDate = askedPhrase.getLastAccessDateTime();
        //TODO WTF?!
        this.askDate /*= askedPhrase.lastAccessDateTime*/ = new Timestamp(System.currentTimeMillis());
        this.user = askedPhrase.getUser();
    }

    public static Question compose(Phrase askedPhrase) {
        System.out.println("CALL: compose(Phrase askedPhrase, PhrasesRepository dbHelper) from Question");
        if (askedPhrase == null) {
            throw new IllegalArgumentException("Could not compose. Asked phrase was null");
        }
        return new Question(askedPhrase);
    }

    public void answer(String answer) {
        System.out.println("CALL: answer(String answerLiteral) from Question");
        if (!answered) {
            this.answerLiteral = answer;

            if (AnswerChecker.checkLiterals(answer, askedPhrase.getForeignWord())) {
                rightAnswer();
            } else {
                wrongAnswer();
            }
        }
    }

    public void rightAnswer() {
        System.out.println("CALL: rightAnswer() from Question - " + this + ", isAnswered()=" + isAnswered() + ", answerCorrect=" + answerCorrect);

        if (getAskedPhrase().getLastAccessDateTime() != null && !lastInLog()) {
            System.out.println("CALL: rightAnswer() from Question - " + this + ", isAnswered()=" + isAnswered() + ", answerCorrect=" + answerCorrect);
            return;
        }

        this.answerCorrect = true;

        if (isTrained()) {
            this.afterAnswerProbabilityFactor = initialProbabilityFactor;
            this.afterAnswerProbabilityMultiplier = initialProbabilityMultiplier;
            askedPhrase.setProbabilityFactor(afterAnswerProbabilityFactor);
            askedPhrase.setMultiplier(afterAnswerProbabilityMultiplier);
        } else {
            this.afterAnswerProbabilityFactor = initialProbabilityFactor - RIGHT_ANSWER_SUBTRAHEND * initialProbabilityMultiplier;
            this.afterAnswerProbabilityMultiplier = initialProbabilityMultiplier * RIGHT_ANSWER_MULTIPLIER;
            askedPhrase.setProbabilityFactor(afterAnswerProbabilityFactor);
            askedPhrase.setMultiplier(afterAnswerProbabilityMultiplier);
        }

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HelperFactory.getHelper().getPhraseDao().update(askedPhrase);
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException();
                }
            }
        }).start();*/


        this.afterAnswerStartIndex = askedPhrase.getIndexStart();
        this.afterAnswerEndIndex = askedPhrase.getIndexEnd();
        this.answerCorrect = true;
        /*if (!answered) {
            if (this.answerLiteral == null || this.answerLiteral.equals("")) {
                this.answerLiteral = askedPhrase.getForeignWord();
            }
//            this.answerLiteral = askedPhrase.getForeignWord();
        }*//* else {

        }*/
        updateDataInDb();
        this.answered = true;
        System.out.println("CALL: rightAnswer() from Question - " + this + ", isAnswered()=" + isAnswered() + ", answerCorrect=" + answerCorrect);

    }

    public void wrongAnswer() {
        System.out.println("CALL: wrongAnswer() from Question");

        if (getAskedPhrase().getLastAccessDateTime() != null && !lastInLog()) {
            return;
        }

        this.answerCorrect = false;

        if (!isTrained()) {
            this.afterAnswerProbabilityFactor = initialProbabilityFactor + WRONG_ANSWER_ADDEND * initialProbabilityMultiplier;
            this.afterAnswerProbabilityMultiplier = 1;
            askedPhrase.setProbabilityFactor(afterAnswerProbabilityFactor);
            askedPhrase.setMultiplier(afterAnswerProbabilityMultiplier);
        } else {
            this.afterAnswerProbabilityFactor = initialProbabilityFactor + WRONG_ANSWER_ADDEND;
            this.afterAnswerProbabilityMultiplier = 1;
            askedPhrase.setProbabilityFactor(afterAnswerProbabilityFactor);
            askedPhrase.setMultiplier(afterAnswerProbabilityMultiplier);
        }

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HelperFactory.getHelper().getPhraseDao().update(askedPhrase);
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException();
                }
            }
        }).start();*/

        this.afterAnswerStartIndex = askedPhrase.getIndexStart();
        this.afterAnswerEndIndex = askedPhrase.getIndexEnd();
        this.answerCorrect = false;

        if (answered) {
            this.answerLiteral = null;
        }
        updateDataInDb();
        this.answered = true;

    }

    public void updateDataInDb() {
        if (answered) {
                GuesswordRepository.getInstance().updateQuestion(this);
            } else {
                GuesswordRepository.getInstance().persistQuestion(this);
            }

    }

    private boolean lastInLog() {
        return askedPhrase.getLastAccessDateTime() == askDate;
    }

    private boolean isTrained() {
        return initialProbabilityFactor <= TRAINED_PROBABILITY_FACTOR;
    }

    public String string(){
        return askedPhrase.getNativeWord() + " " + Hints.shortHint(askedPhrase.getForeignWord());
    }

    public String probabilityFactorHistory() {
        if (!answered) {
            return new BigDecimal(initialProbabilityFactor).setScale(PROBABILITY_FACTOR_ACCURACY, BigDecimal.ROUND_HALF_UP).toString();
        } else {
            BigDecimal beforeProbabilityFactor = new BigDecimal(initialProbabilityFactor).setScale(MULTIPLIER_ACCURACY, BigDecimal.ROUND_HALF_UP);
            BigDecimal afterProbabilityFactor = new BigDecimal(afterAnswerProbabilityFactor).setScale(MULTIPLIER_ACCURACY, BigDecimal.ROUND_HALF_UP);
            return beforeProbabilityFactor.toString() + " ➩ " + afterProbabilityFactor.toString() + " (" +
                    (afterProbabilityFactor.doubleValue() > beforeProbabilityFactor.doubleValue() ? "+" : "") +
                    afterProbabilityFactor.subtract(beforeProbabilityFactor) + ")";
        }
    }

    public String multiplierHistory() {
        if (!answered) {
            return new BigDecimal(initialProbabilityMultiplier).setScale(MULTIPLIER_ACCURACY, BigDecimal.ROUND_HALF_UP).toString();
        } else {
            BigDecimal beforeMultiplier = new BigDecimal(initialProbabilityMultiplier).setScale(MULTIPLIER_ACCURACY, BigDecimal.ROUND_HALF_UP);
            BigDecimal afterMultiplier = new BigDecimal(afterAnswerProbabilityMultiplier).setScale(MULTIPLIER_ACCURACY, BigDecimal.ROUND_HALF_UP);
            return beforeMultiplier.toString() + " ➩ " + afterMultiplier.toString() + " (" +
                    (afterMultiplier.doubleValue() > beforeMultiplier.doubleValue() ? "+" : "") + afterMultiplier.subtract(beforeMultiplier) + ")";
        }
    }

    public String lastAccessDate() {
        if (this.initLastAccessDate != null) {
            return this.initLastAccessDate.toString();/*.format(DateTimeFormatter.ofPattern("d MMM yyyy HH:mm"))*/
        } else {
            return "NEVER ACCESSED";
        }
    }

    public String creationDate() {
        return this.askedPhrase.toString()/*.getCollectionAddingDateTime().format(DateTimeFormatter.ofPattern("d MMM yyyy HH:mm"))*/;
    }

    public String label() {
        if (this.askedPhrase.getLabel() != null) {
            return this.askedPhrase.getLabel();
        } else {
            return "";
        }
    }

    /*public String appearingPercentage() {
        String appearingPercentage = "";
        if (phrasesRepository != null) {
            appearingPercentage = new BigDecimal((double) (initEndIndex - initStartIndex) / (double) phrasesRepository.getGreatestPhrasesIndex() * 100).setScale(5, BigDecimal.ROUND_HALF_UP).toString();
            if (answered) {
                appearingPercentage += " ➩ " +
                        new BigDecimal((double) (afterAnswerEndIndex - afterAnswerStartIndex) / (double) phrasesRepository.getGreatestPhrasesIndex() * 100).setScale(5, BigDecimal.ROUND_HALF_UP);
            }
        }
        return appearingPercentage;
    }*/

    public boolean answerIsCorrect() {
        return answered && answerCorrect;
    }

    public int trainedAfterAnswer() {
        if (answered) {
            if (initialProbabilityFactor > Phrase.TRAINED_PROBABILITY_FACTOR &&
                    afterAnswerProbabilityFactor <= Phrase.TRAINED_PROBABILITY_FACTOR) {
                return 1;
            } else if (initialProbabilityFactor <= Phrase.TRAINED_PROBABILITY_FACTOR &&
                    afterAnswerProbabilityFactor > Phrase.TRAINED_PROBABILITY_FACTOR) {
                return -1;
            }
        }
        return 0;
    }

    //Getters and setters

    public Date getAskDate() {
        return askDate;
    }

    public Phrase getAskedPhrase() {
        return askedPhrase;
    }

    public Long getId() {
        return id;
    }

    public String getAnswerLiteral() {
        return answerLiteral;
    }

    public boolean isAnswerCorrect() {
        return answerCorrect;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    @Override
    public String toString() {
        return getAskedPhrase() + ", " + "answered=" + answered + ", isAnswerCorrect=" + isAnswerCorrect() + ", " + creationDate();
    }
}

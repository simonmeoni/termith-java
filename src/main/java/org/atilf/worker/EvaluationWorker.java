package org.atilf.worker;

import org.atilf.models.TermithIndex;
import org.atilf.module.disambiguisation.Evaluation;
import org.atilf.module.disambiguisation.EvaluationProfile;

import java.util.Map;

/**
 * @author Simon Meoni
 *         Created on 24/10/16.
 */
public class EvaluationWorker implements Runnable {

    private Map<String, EvaluationProfile> evaluationProfile;
    private TermithIndex termithIndex;

    public EvaluationWorker(Map<String, EvaluationProfile> evaluationProfile, TermithIndex termithIndex) {

        this.evaluationProfile = evaluationProfile;
        this.termithIndex = termithIndex;
    }

    @Override
    public void run() {
        Evaluation evaluation = new Evaluation(
                evaluationProfile,
                termithIndex.get_termSubLexic()
        );
        evaluation.execute();
    }
}
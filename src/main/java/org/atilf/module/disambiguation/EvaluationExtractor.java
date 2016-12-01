package org.atilf.module.disambiguation;

import org.atilf.models.disambiguation.EvaluationProfile;
import org.atilf.models.termith.TermithIndex;
import org.atilf.module.tools.FilesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author Simon Meoni
 *         Created on 24/10/16.
 */
public class EvaluationExtractor extends ContextExtractor {

    private final Map<String, EvaluationProfile> _evaluationLexicon;
    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluationExtractor.class.getName());
    private String _p;
    private CountDownLatch _extactorCounter;

    public EvaluationExtractor(String p, TermithIndex termithIndex) {
        super(p,termithIndex.getContextLexicon());
        _p = p;
        termithIndex.getEvaluationLexicon().put(FilesUtils.nameNormalizer(p),new HashMap<>());
        _evaluationLexicon = termithIndex.getEvaluationLexicon().get(FilesUtils.nameNormalizer(p));
    }

    public EvaluationExtractor(String p, TermithIndex termithIndex, CountDownLatch extactorCounter) {
        super(p,termithIndex.getContextLexicon());
        _p = p;
        _extactorCounter = extactorCounter;
        termithIndex.getEvaluationLexicon().put(FilesUtils.nameNormalizer(p),new HashMap<>());
        _evaluationLexicon = termithIndex.getEvaluationLexicon().get(FilesUtils.nameNormalizer(p));
    }

    private boolean containInSpecLexicon(String corresp){
        return (_contextLexicon.containsKey(corresp.substring(1) + "_lexOff") ||
                _contextLexicon.containsKey(corresp.substring(1) + "_lexOn"));
    }

    @Override
    public void run() {
        LOGGER.info("add " + _p + " to evaluation lexicon");
        this.execute();
        _extactorCounter.countDown();
        LOGGER.info(_p + " added");
    }

    @Override
    protected String normalizeKey(String c, String l) {
            return (c + "_" + l).replace("#", "");
    }
}

package module.timer;

import models.TermithIndex;
import org.slf4j.Logger;

/**
 * @author Simon Meoni
 *         Created on 19/09/16.
 */
public class InitCorpusTimer extends ProgressBarTimer{
    public InitCorpusTimer(TermithIndex termithIndex, Logger logger) {
        super(termithIndex, "Init corpus progression", logger);
    }
    @Override
    public void run() {
        update(termithIndex.getXmlCorpus().size());
    }
}

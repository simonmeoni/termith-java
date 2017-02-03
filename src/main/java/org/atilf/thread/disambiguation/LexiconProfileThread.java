package org.atilf.thread.disambiguation;

import org.atilf.models.TermithIndex;
import org.atilf.models.disambiguation.RConnectionPool;
import org.atilf.models.disambiguation.RLexicon;
import org.atilf.module.disambiguation.lexiconProfile.SpecCoefficientInjector;
import org.atilf.thread.Thread;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.currentThread;

/**
 * The LexiconProfileThread process the specificity coefficient for each pair of lemma/_pos of a termEntry contained by
 * _contextLexicon map of termithIndex
 * @author Simon Meoni
 *         Created on 12/10/16.
 */
public class LexiconProfileThread extends Thread{

    /**
     * this constructor initialize the _termithIndex fields and initialize the _poolSize field with the default value
     * with the number of available processors.
     *
     * @param termithIndex
     *         the termithIndex is an object that contains the results of the process
     */
    public LexiconProfileThread(TermithIndex termithIndex) {
        super(termithIndex);
    }

    /**
     * this constructor initialize all the needed fields. it initialize the termithIndex who contains the result of
     * process and initialize the size of the pool of _executorService field.
     *
     * @param termithIndex
     *         the termithIndex is an object that contains the results of the process
     * @param poolSize
     *         the number of thread used during the process
     *
     * @see TermithIndex
     * @see ExecutorService
     */
    public LexiconProfileThread(TermithIndex termithIndex, int poolSize) {
        super(termithIndex, poolSize);
    }

    /**
     * this is the method who converts global corpus into a R variable and compute the specificity coefficient for each
     * words for each context of terms candidates entries (also known as lexical profile)
     * @throws InterruptedException thrown if awaitTermination function is interrupted while waiting
     */
    public void execute() throws InterruptedException, IOException {
        /*
        convert global corpus into R variable
         */

        RLexicon rLexicon = new RLexicon(_termithIndex.getCorpusLexicon());
        RConnectionPool RConnectionPool = new RConnectionPool(8,rLexicon);
        _termithIndex.getContextLexicon().forEach(
                (key, value) -> _executorService.submit(new SpecCoefficientInjector(
                        key,
                        _termithIndex,
                        rLexicon,
                        RConnectionPool))
        );

        _logger.info("Waiting SpecCoefficientInjector executors to finish");
        _executorService.shutdown();
        _executorService.awaitTermination(1L, TimeUnit.DAYS);
        RConnectionPool.removeThread(currentThread());
        Files.delete(rLexicon.getCsvPath());
    }
}

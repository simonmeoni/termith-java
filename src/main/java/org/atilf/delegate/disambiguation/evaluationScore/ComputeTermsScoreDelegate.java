package org.atilf.delegate.disambiguation.evaluationScore;

import org.atilf.delegate.Delegate;
import org.atilf.module.disambiguation.evaluationScore.ComputeTermsScore;
import org.flowable.engine.delegate.DelegateExecution;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Simon Meoni Created on 15/12/16.
 */
public class ComputeTermsScoreDelegate extends Delegate {

    /**
     * this method is used to executeTasks the different steps of processing of a delegate
     *
     * @throws IOException
     *         thrown a IO exception if a file is not found or have a permission problem during the xsl transformation
     *         phase
     * @throws InterruptedException
     *         thrown if awaitTermination function is interrupted while waiting
     * @throws ExecutionException
     *         thrown a exception if a system process is interrupted
     * @param execution
     */
    @Override
    public void executeTasks(DelegateExecution execution) throws IOException, InterruptedException, ExecutionException {
        _logger.info("ComputeTermScore phase is started");
        _termithIndex.getScoreTerms().keySet().forEach(
                p -> _executorService.submit(new ComputeTermsScore(p,_termithIndex))
        );
        _logger.info("ComputeTermScore phase is finished");
        _executorService.shutdown();
        _executorService.awaitTermination(1L, TimeUnit.DAYS);
    }
}

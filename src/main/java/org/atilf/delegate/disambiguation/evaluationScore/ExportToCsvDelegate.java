package org.atilf.delegate.disambiguation.evaluationScore;

import org.atilf.delegate.Delegate;
import org.atilf.module.disambiguation.evaluationScore.ExportScoreToJson;
import org.atilf.runner.Runner;
import org.flowable.engine.delegate.DelegateExecution;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Simon Meoni Created on 15/12/16.
 */
public class ExportToCsvDelegate extends Delegate {

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
        _executorService.submit(new ExportScoreToJson(_termithIndex, Runner.getScorePath(), true)).get();
        _executorService.shutdown();
        _executorService.awaitTermination(1L, TimeUnit.DAYS);
    }
}

package org.atilf.thread;

import org.atilf.models.TermithIndex;
import org.atilf.worker.DisambExporterWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.atilf.models.TermithIndex._base;

/**
 * @author Simon Meoni
 *         Created on 25/10/16.
 */
public class DisambExporterThread {
    private final TermithIndex termithIndex;
    private final int poolSize;
    private static final Logger LOGGER = LoggerFactory.getLogger(DisambEvaluationThread.class.getName());

    public DisambExporterThread(TermithIndex termithIndex, int poolSize) {

        this.termithIndex = termithIndex;
        this.poolSize = poolSize;
    }

    public void execute() throws IOException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        Files.list(_base).forEach(
                p -> executor.submit(new DisambExporterWorker(p,termithIndex))
        );
        LOGGER.info("Waiting SubLexicExtractorWorker executors to finish");
        executor.shutdown();
        executor.awaitTermination(1L, TimeUnit.DAYS);
    }
}

package org.atilf.runner;

import org.atilf.models.TermithIndex;
import org.atilf.thread.disambiguation.contextLexicon.ContextExtractorThread;
import org.atilf.thread.disambiguation.contextLexicon.DisambiguationXslTransformerThread;
import org.atilf.thread.disambiguation.disambiguationExporter.DisambiguationExporterThread;
import org.atilf.thread.disambiguation.evaluation.CommonWordsPosLemmaCleanerThread;
import org.atilf.thread.disambiguation.evaluation.EvaluationExtractorThread;
import org.atilf.thread.disambiguation.evaluation.EvaluationThread;
import org.atilf.thread.disambiguation.evaluation.ThresholdLexiconCleanerThread;
import org.atilf.thread.disambiguation.evaluationScore.*;
import org.atilf.thread.disambiguation.lexiconProfile.LexiconProfileThread;
import org.atilf.thread.enrichment.cleaner.WorkingFileCleanerThread;
import org.atilf.tools.BenchmarkFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * this is the module.disambiguation process of termITH
 * @author Simon Meoni
 *         Created on 11/10/16.
 */
public class Disambiguation extends Runner {
    
    /**
     * this constructor initializes the _termithIndex field.
     * @param termithIndex
     *         the termithIndex is an object that contains the results of the process
     */
    public Disambiguation(TermithIndex termithIndex){
        super(termithIndex, Runner.DEFAULT_POOL_SIZE);
    }

    /**
     * this constructor initializes the _termithIndex field and the number of thread used during the process
     * @param termithIndex
     *         the termithIndex is an object that contains the results of the process
     * @param poolSize
     *         the number of thread used during the process
     */
    public Disambiguation(TermithIndex termithIndex, int poolSize){
        super(termithIndex, poolSize);
    }

    /**
     * The execute method of Disambiguation has four phases :
     *      1) ContextLexiconThread : the extraction of context for each terminology entries in the learning corpus
     *      2) LexiconProfileThread :the creation of lexical profile for each terminology entries
     *      3) EvaluationThread : the evaluation consists to extract context for terminologies entries in the evaluation
     *      corpus and compare context evaluation corpus and learning evaluation
     *      in order to determine the terminology or not a terms candidates in the evaluation corpus
     *      4) DisambiguationExporterThread : report the result on the associate tei file
     *      @see LexiconProfileThread
     *      @see EvaluationThread
     *      @see DisambiguationExporterThread
     */
    @Override
    public void execute() {
        try {
            _logger.info("Pool size set to: " + _poolSize);
        /*
        Context extraction phase
         */
            executeThread(DisambiguationXslTransformerThread.class,_termithIndex,_poolSize);
            executeThread(ContextExtractorThread.class,_termithIndex,_poolSize);
        /*
        Lexicon profile processing
         */
            executeThread(LexiconProfileThread.class,_termithIndex, _poolSize);
        /*
        Evaluation phase
         */
            executeThread(ThresholdLexiconCleanerThread.class,_termithIndex,_poolSize);
            executeThread(CommonWordsPosLemmaCleanerThread.class,_termithIndex,_poolSize);
            executeThread(EvaluationExtractorThread.class,_termithIndex,_poolSize);
            executeThread(EvaluationThread.class,_termithIndex,_poolSize);
        /*
        Export results
         */
            executeThread(DisambiguationExporterThread.class,_termithIndex,_poolSize);
        /*
        Score phase
         */
            if (TermithIndex.getScorePath() != null){
                executeThread(TransformXslScore.class,_termithIndex,_poolSize);
                executeThread(AggregateTeiTermsThread.class,_termithIndex,_poolSize);
                executeThread(ComputeTermsScoreThread.class,_termithIndex,_poolSize);
                executeThread(ComputeTotalTermsScoreThread.class,_termithIndex,_poolSize);
                executeThread(ExportToCsvThread.class,_termithIndex,_poolSize);
                executeThread(ExportToJsonThread.class,_termithIndex,_poolSize);
                executeThread(ContextJsonExporterThread.class,_termithIndex,_poolSize);
            }

            executeThread(WorkingFileCleanerThread.class,_termithIndex,_poolSize);

            if (BenchmarkFactory._exportBenchmark) {
                BenchmarkFactory.export(_termithIndex.getMemoryPerformanceEvents());
                BenchmarkFactory.export(_termithIndex.getTimePerformanceEvents());
            }


        } catch (InterruptedException | ExecutionException | IOException e) {
            _logger.error("error during execution of thread : ", e);
        }

    }

}

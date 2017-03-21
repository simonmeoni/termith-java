package org.atilf.delegate.enrichment.analyzer;

import org.atilf.delegate.Delegate;
import org.atilf.models.enrichment.CorpusAnalyzer;
import org.atilf.models.enrichment.TagNormalizer;
import org.atilf.module.enrichment.analyzer.TerminologyParser;
import org.atilf.module.enrichment.analyzer.TerminologyStandOff;
import org.atilf.module.enrichment.analyzer.TermsuitePipelineBuilder;
import org.atilf.module.enrichment.analyzer.TreeTaggerWorker;
import org.atilf.module.enrichment.initializer.TextExtractor;
import org.atilf.runner.Runner;
import org.atilf.tools.FilesUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * The TreeTaggerWorkerDelegate calls several modules classes which analyzer the morphology of each file in the corpus and the
 * terminology of the corpus. The morphology is analyzed with a treetagger wrapper. The result is serialized to
 * the json termsuite format. The terminology uses the json files write during the analyzer of the morphology.
 * The terminology is export as two json and tbx files. Finally the result of the phase is prepared in order to
 * write them into the tei files of the corpus
 * @author Simon Meoni
 *         Created on 01/09/16.
 */
public class TreeTaggerWorkerDelegate extends Delegate {

    /**
     * this method return the result of the InitializerThread.
     * @return it returns a hashmap who contains the extracted text of the previous step of each files
     * @see TextExtractor
     */
    private Map<String,StringBuilder> createTextHashMap(){
        Map<String,StringBuilder> textMap = new HashMap<>();

        /*
        read extracted text of the previous phase and put the result to the hashmap. the filename is
        the key of each entries
         */
        _termithIndex.getExtractedText().forEach(
                (key,value) -> textMap.put(key,FilesUtils.readObject(value, StringBuilder.class))
        );
        return textMap;
    }

    /**
     *  Firstly, the method create two timer inherited objects. These objects show the progress of the tokenization jobs
     *  and the Json serialization jobs. Secondly, a corpusAnalyzer object is initialized : it contains the several
     *  metadata of the corpus. this metadata is used to write termsuite morphology json format. After that all
     *  TreeTaggerWorker jobs are finished, the written json files is given as input to a termsuitePipeline. The output
     *  of Termsuite is deserialize to a java object contained in a termithIndex.
     *  The result of the morphology is added to the termithIndex
     * @throws IOException throws exception if a file is not find
     * @throws InterruptedException throws java concurrent executorService exception
     * @throws ExecutionException throws an exception if a TreeTagger process is interrupted
     * @see TreeTaggerWorker
     * @see TermsuitePipelineBuilder
     * @see TerminologyParser
     * @see TerminologyStandOff
     */
    public void execute() throws InterruptedException, IOException, ExecutionException {
        /*
        Build Corpus analyzer
         */
        CorpusAnalyzer corpusAnalyzer = new CorpusAnalyzer(createTextHashMap());
        TagNormalizer.initTag(Runner.getLang());
        /*
        Write morphology json file
         */
        _termithIndex.getExtractedText().forEach((key, txt) ->
                _executorService.submit(new TreeTaggerWorker(
                        _termithIndex,
                        corpusAnalyzer,
                        key,
                        Runner.getOut().toString()
                ))
        );
        _logger.info("waiting that all json files are serialized");
        _executorService.shutdown();
        _executorService.awaitTermination(1L,TimeUnit.DAYS);
        _logger.info("terminology extraction finished");
    }
}
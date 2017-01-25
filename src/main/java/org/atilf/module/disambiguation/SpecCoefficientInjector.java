package org.atilf.module.disambiguation;

import com.github.rcaller.rstuff.RCaller;
import com.github.rcaller.rstuff.RCode;
import org.atilf.models.disambiguation.CorpusLexicon;
import org.atilf.models.disambiguation.LexiconProfile;
import org.atilf.models.disambiguation.RLexicon;
import org.atilf.models.disambiguation.RResources;
import org.atilf.models.termith.TermithIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.atilf.models.disambiguation.AnnotationResources.LEX_OFF;
import static org.atilf.models.disambiguation.AnnotationResources.LEX_ON;

/**
 * compute specificity coefficient for each word of a term candidate entry. the result is retained on the
 * _lexicalProfile field. This class call a R script with the library Rcaller.
 * @author Simon Meoni
 *         Created on 21/10/16.
 */
public class SpecCoefficientInjector implements Runnable{
    private LexiconProfile _lexiconProfile;
    private RLexicon _rLexicon;
    private CorpusLexicon _corpusLexicon;
    private boolean _computeSpecificities = true;
    private RLexicon _rContextLexicon;
    private String _id;
    private String _rResultPath = TermithIndex.getOutputPath() + "/" + UUID.randomUUID().toString();
    private static final Logger LOGGER = LoggerFactory.getLogger(SpecCoefficientInjector.class.getName());

    /**
     *  constructor of SpecCoefficientInjector
     * @param lexiconProfile this is the lexiconProfile of the termithIndex
     * @param rLexicon the rLexicon variable is an object that contain the corpusLexicon converted into R variable and
     *                 the size of the corpus
     * @param corpusLexicon this the corpus of the termithIndex
     * @see LexiconProfile
     * @see RLexicon
     * @see CorpusLexicon
     */
    public SpecCoefficientInjector(LexiconProfile lexiconProfile, RLexicon rLexicon, CorpusLexicon corpusLexicon) {

        _corpusLexicon = corpusLexicon;
        _lexiconProfile = lexiconProfile;
        _rLexicon = rLexicon;
        /*
        instantiate _rContextLexicon
         */
        _rContextLexicon = new RLexicon(_lexiconProfile, _corpusLexicon);
    }

    /**
     * constructor for SpecCoefficientInjector
     * @param id term entry id of key in lexicalProfile
     * @param termithIndex the termithIndex of a process
     * @param rLexicon the corpusLexicon converted into a RLexicon object that contains the R variable
     *                 of the corpusLexicon and his size
     */
    public SpecCoefficientInjector(String id,TermithIndex termithIndex, RLexicon rLexicon){
        if (isLexiconPresent(termithIndex,id)) {
            _id = id;
            _corpusLexicon = termithIndex.getCorpusLexicon();
            _lexiconProfile = termithIndex.getContextLexicon().get(id);
            _rLexicon = rLexicon;
            _rContextLexicon = new RLexicon(_lexiconProfile, _corpusLexicon);
            _computeSpecificities = true;
        }
        else {
            _computeSpecificities = false;
        }
    }

    private boolean isLexiconPresent(TermithIndex termithIndex, String id) {
        String on = id.split("_")[0] + LEX_ON.getValue();
        String off = id.split("_")[0] + LEX_OFF.getValue();
        return termithIndex.getContextLexicon().containsKey(on) && termithIndex.getContextLexicon().containsKey(off);
    }

    /**
     * call reduceToLexicalProfile method
     */
    public void execute() {
        try {
            if (_computeSpecificities) {
                reduceToLexicalProfile(computeSpecCoefficient());
            }
            else {
                LOGGER.info("only terminology or non-terminology lexicon profile is present, " +
                        "no need to compute coefficients for terminology entry : ", _id);
            }
        }
        catch (Exception e){
            LOGGER.error("problem during the execution of SpecCoefficientInjector :", e);
        }
    }


    /**
     * add specificityCoefficient calculated at each word in the _lexicalProfile
     * @param specificityCoefficient this float array contains specificities coefficients for all words
     *                               of a lexical profile
     */
    private void reduceToLexicalProfile(List<Float> specificityCoefficient) {
        int cnt = 0;
        for (String id : _rContextLexicon.getIdContextLexicon()) {
            _lexiconProfile.addCoefficientSpec(
                    _corpusLexicon.getLexicalEntry(Integer.parseInt(id)),
                    specificityCoefficient.get(cnt));
            cnt++;
        }
    }

    /**
     * this method calls the R script and compute specificity coefficient for each word of a context
     * @return coefficients specificities
     */
    List<Float> computeSpecCoefficient() {
        LOGGER.debug("compute specificity coefficient");
        /*
        instantiate rcaller
         */
        RCaller rcaller = RCaller.create();
        /*
        instantiate rcode and add the script, add some variable and execute specificities.lexicon function
         */
        RCode code = RCode.create();
        /*
        import R script
         */
        code.addRCode(RResources.SCRIPT.toString());
        /*
        add variable
         */
        /*
        add size of the corpus
         */
        code.addRCode("sumCol <-" + _rLexicon.getSize());
        /*
        add size of the corpus and the size of the context
         */
        code.addRCode("tabCol <-" + "c(" + _rContextLexicon.getSize() + "," + _rLexicon.getSize() + ")");
        code.addRCode("names(tabCol) <- c(\"sublexicon\",\"complementary\")");
        /*
        add occurrences numbers for all words of the corpus
         */
        code.addRCode("lexic <- import_csv(\"" + _rLexicon.getCsvPath() + "\")");

        /*
        add occurrences numbers for all words of the context
         */
        code.addRCode("sublexic <- import_csv(\"" + _rContextLexicon.getCsvPath() + "\")");

        /*
        compute specificities coefficient for all words of the corpus
         */
        code.addRCode("res <- specificities.lexicon(lexic,sublexic,sumCol,tabCol)");
        /*
        retained only specificities for words in the context
         */
        code.addRCode("res <- res[,1]");
        code.addRCode("names(res) <- names(lexic)");
        code.addRCode("res <- res[match(names(sublexic),names(res))]");
        code.addRCode("names(res) <- NULL");
        code.addRCode("export_csv(list(res),\"" + _rResultPath + "\")");
        /*
        execute script
         */
        rcaller.setRCode(code);
        rcaller.runOnly();
        rcaller.deleteTempFiles();
        LOGGER.debug("specificity coefficient has been computed");
        return resToFloat();
    }

    private List<Float> resToFloat() {
        List<Float> floatArray = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(_rResultPath))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                    if (Objects.equals(sCurrentLine, "Inf")){
                        floatArray.add(Float.POSITIVE_INFINITY);
                        LOGGER.error("positive infinity was return by R");
                    }
                    else if (Objects.equals(sCurrentLine, "-Inf")){
                        floatArray.add(Float.NEGATIVE_INFINITY);
                        LOGGER.error("negative infinity was return by R");
                    }
                    else {
                        floatArray.add(Float.parseFloat(sCurrentLine));
                    }
            }
            br.close();
        }
        catch (IOException e) {
            LOGGER.error("cannot read result of R",e);
        }
        return floatArray;
    }

    /*
    run execute method
     */
    @Override
    public void run() {
        LOGGER.info("compute specificities coefficient for : " + _id);
        execute();
        LOGGER.info("specificities coefficient is computed for : " + _id);
    }
}

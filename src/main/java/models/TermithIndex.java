package models;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Simon Meoni
 *         Created on 13/09/16.
 */
public class TermithIndex {

    private String outputPath;
    private String lang;
    private String treeTaggerHome;
    private Path base;
    private boolean trace;

    public Path corpus;
    public List<Path> terminologies;
    public Map<String, StringBuffer> tokenizeTeiBody;
    public Map<String, StringBuffer> morphoSyntaxStandOff;
    public Map<String, StringBuffer> extractedText;
    public Map<String, StringBuffer> xmlCorpus;
    public Map<String, Path> JsonTreeTagger;

    private TermithIndex(Builder builder) {
        base = builder.base;
        trace = builder.trace;
        outputPath = builder.outputPath;
        treeTaggerHome = builder.treeTaggerHome;
        lang = builder.lang;
        terminologies = new CopyOnWriteArrayList<>();
        morphoSyntaxStandOff = new ConcurrentHashMap<>();
        tokenizeTeiBody = new ConcurrentHashMap<>();
        corpus = null;
        extractedText = new ConcurrentHashMap<>();
        xmlCorpus = new ConcurrentHashMap<>();
        JsonTreeTagger = new ConcurrentHashMap<>();
    }

    public String getOutputPath() {
        return outputPath;
    }

    public String getLang() {
        return lang;
    }

    public String getTreeTaggerHome() {
        return treeTaggerHome;
    }

    public Path getBase() {
        return base;
    }

    public boolean isTrace() {
        return trace;
    }

    public List<Path> getTerminologies() {
        return terminologies;
    }

    public Map<String, StringBuffer> getMorphoSyntaxStandOff() {
        return morphoSyntaxStandOff;
    }

    public Map<String, StringBuffer> getTokenizeTeiBody() {
        return tokenizeTeiBody;
    }

    public Path getCorpus() {
        return corpus;
    }

    public Map<String, StringBuffer> getExtractedText() {
        return extractedText;
    }

    public Map<String, StringBuffer> getXmlCorpus() {
        return xmlCorpus;
    }

    public Map<String, Path> getJsonTreeTagger() {
        return JsonTreeTagger;
    }

    public void setCorpus(Path corpus) {
        this.corpus = corpus;
    }

    /**
     * This class is used to instance a termITH object
     */
    public static class Builder
    {
        Path base;
        boolean trace = false;
        String outputPath = null;
        String lang;
        String treeTaggerHome;
        private List<Path> terminologies;
        private Map<String, StringBuffer> morphoSyntaxStandOff;
        private Map<String, StringBuffer> tokenizeTeiBody;
        private Path corpus;
        private Map<String, StringBuffer> extractedText;
        private Map<String, StringBuffer> xmlCorpus;

        /**
         * This method set the input folder path
         * @param path path of the base corpus
         * @return return the path of the base corpus
         * @throws IOException
         */
        public Builder baseFolder(String path) throws IOException {
            this.base = Paths.get(path);
            return this;
        }

        /**
         * this method set a boolean that used to activate or not the trace : each step of the process will be export to
         * the result folder
         * @param activate boolean use to activate the "trace" mode
         * @return value of the activate boolean
         */
        public Builder trace(boolean activate){
            this.trace = activate;
            return this;
        }

        /**
         * set the output path of the result
         * @param outputPath the output path
         * @return return output path
         */
        public Builder export(String outputPath){
            this.outputPath = outputPath;
            return this;
        }

        /**
         * set the lang
         * @param lang set language
         * @return return string language
         */
        public Builder lang(String lang){
            this.lang = lang;
            return this;
        }

        /**
         * set the TreeTagger path
         * @param treeTaggerHome TreeTagger path
         * @return return TreeTagger path
         */
        public Builder treeTaggerHome(String treeTaggerHome){
            this.treeTaggerHome = treeTaggerHome;
            return this;
        }

        /**
         * This method is used to finalize the building of the TermithText Object
         * @see TermithIndex
         * @return return termith object
         */
        public TermithIndex build() {
            return  new TermithIndex(this);
        }
    }
}
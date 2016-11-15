package org.atilf.module.treetagger;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;
import java.util.stream.Collector;

/**
 * send a text to a TreeTagger process and retrieve the result in the _ttOut field
 * @author Simon Meoni
 *         Created on 01/09/16.
 */
class TreeTaggerWrapper {

    private final Logger LOGGER = LoggerFactory.getLogger(TreeTaggerWrapper.class.getName());

    private final StringBuilder _txt;
    private TreeTaggerParameter _treeTaggerParameter;
    private String _outputPath;
    private StringBuilder _ttOut = new StringBuilder();

    /**
     * constructor for TreeTaggerWrapper
     * @param txt the extracted text
     * @param treeTaggerParameter the tree tagger parameter is contains on this class
     * @param outputPath the working directory of the termith process
     */
    TreeTaggerWrapper(StringBuilder txt, TreeTaggerParameter treeTaggerParameter,
                      String outputPath) {
        _txt = txt;
        _treeTaggerParameter = treeTaggerParameter;
        _outputPath = outputPath;
    }

    /**
     * get the output of the treetagger process
     * @return return a StringBuilder with the output of the treetagger process
     */
    StringBuilder getTtOut() {
        return _ttOut;
    }

    /**
     * execute a treetagger process with certain parameters (with the help of _treeTaggerParameter field) and
     * convert extracted text into a readable format for treetagger (one word per line )
     * @throws IOException thrown a exception if the input given at treetagger is not readable
     * @throws InterruptedException thrown a exception if the execution of TreeTagger is interrupted
     */
    public void execute() throws IOException, InterruptedException {
        /*
        convert extracted text and write it to a file
         */
        String ttPath = writeFile(parsingText());
        /*
        execute a treetagger process
         */
        Process p = Runtime.getRuntime().exec(new String[]{"bash","-c", _treeTaggerParameter.parse() + " "
                + ttPath});

        /*
        get the result of treetagger
         */
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        _ttOut =
                bufferedReader.lines().map(String::toString).collect(Collector.of(
                        StringBuilder::new,
                        (stringBuilder, str) -> stringBuilder.append(str).append("\n"),
                        StringBuilder::append)
                );

        /*
        check if TreeTagger is not interrupted during the execution
         */
        int exitCode = p.waitFor();
        if (exitCode != 0) {
            throw new InterruptedException(IOUtils.toString(p.getErrorStream(),"UTF-8"));

        }
        /*
        destroy the process
         */
        if (p.isAlive()){
        p.destroy();
        }
        /*
        delete the input has been given at treetagger
         */
        Files.delete(Paths.get(ttPath));
    }

    /**
     * write file given at TreeTagger
     * @param parsingText the parsing text compute with parsing text method
     * @return the path of the text input
     * @throws IOException thrown an exception if the file has been not written
     */
    private String writeFile(String parsingText) throws IOException {
        File temp = new File( _outputPath + "/" + UUID.randomUUID().toString() + ".tt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(parsingText);
        bw.flush();
        bw.close();
        LOGGER.debug("write treetagger input :"  + temp.getAbsolutePath());
        return temp.getAbsolutePath();
    }

    /**
     * make each word separate by a space and punctuations in a new line.
     * @return return the parsing text
     */
    private String parsingText(){
        Deque<String> oldPunctuations = new ArrayDeque<>();
        Deque<String> newPunctuations = new ArrayDeque<>();
        oldPunctuations.add(".");
        oldPunctuations.add("?");
        oldPunctuations.add("!");
        oldPunctuations.add(";");
        oldPunctuations.add(",");
        oldPunctuations.add(",");
        oldPunctuations.add(":");
        oldPunctuations.add("(");
        oldPunctuations.add(")");
        oldPunctuations.add("[");
        oldPunctuations.add("]");
        oldPunctuations.add("{");
        oldPunctuations.add("}");
        oldPunctuations.add("\"");
        oldPunctuations.add("\'");

        newPunctuations.add("\n.\n");
        newPunctuations.add("\n?\n");
        newPunctuations.add("\n!\n");
        newPunctuations.add("\n;\n");
        newPunctuations.add("\n,\n");
        newPunctuations.add("\n,\n");
        newPunctuations.add("\n:\n");
        newPunctuations.add("\n(\n");
        newPunctuations.add("\n)\n");
        newPunctuations.add("\n[\n");
        newPunctuations.add("\n]\n");
        newPunctuations.add("\n{\n");
        newPunctuations.add("\n}\n");
        newPunctuations.add("\n\"\n");
        newPunctuations.add("\n\'\n");

        /*
        remove the page layout
         */
        String parseTxt = _txt.toString().trim();
        /*
        replace space by newline
         */
        parseTxt = parseTxt.replaceAll("\\s+", "\n");
        /*
        split punctuations
         */
        while (!oldPunctuations.isEmpty()) {
                    parseTxt = parseTxt.replace(oldPunctuations.poll(), newPunctuations.poll());
        }

        return parseTxt;
    }
}

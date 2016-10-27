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
 * @author Simon Meoni
 *         Created on 01/09/16.
 */
public class TreeTaggerWrapper {

    private final Logger LOGGER = LoggerFactory.getLogger(TreeTaggerWrapper.class.getName());

    private final StringBuilder _txt;
    private TreeTaggerParameter _treeTaggerParameter;
    private String _treeTaggerHome;
    private String _outputPath;
    private StringBuilder _ttOut = new StringBuilder();

    public TreeTaggerWrapper(StringBuilder txt, String treeTaggerHome, TreeTaggerParameter treeTaggerParameter,
    String outputPath) {
        _txt = txt;
        _treeTaggerHome = treeTaggerHome;
        _treeTaggerParameter = treeTaggerParameter;
        _outputPath = outputPath;
    }

    public StringBuilder get_ttOut() {
        return _ttOut;
    }

    public void execute() throws IOException, InterruptedException {
        String ttPath = writeFile(parsingText());
        Process p = Runtime.getRuntime().exec(new String[]{"bash","-c", _treeTaggerParameter.parse() + " "
                + ttPath});

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        _ttOut =
                bufferedReader.lines().map(String::toString).collect(Collector.of(
                        StringBuilder::new,
                        (stringBuilder, str) -> stringBuilder.append(str).append("\n"),
                        StringBuilder::append)
                );

        int exitCode = p.waitFor();
        if (exitCode != 0) {
            throw new InterruptedException(IOUtils.toString(p.getErrorStream(),"UTF-8"));

        }
        if (p.isAlive()){
        p.destroy();
        }
        Files.delete(Paths.get(ttPath));
    }
    
    private String writeFile(String parsingText) throws IOException {
        File temp = new File( _outputPath + "/" + UUID.randomUUID().toString() + ".tt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
        bw.write(parsingText);
        bw.flush();
        bw.close();
        LOGGER.debug("write treetagger output :"  + temp.getAbsolutePath());
        return temp.getAbsolutePath();
    }

    public String parsingText(){
        Deque<String> oldPuncts = new ArrayDeque<>();
        Deque<String> newPuncts = new ArrayDeque<>();
        oldPuncts.add(".");
        oldPuncts.add("?");
        oldPuncts.add("!");
        oldPuncts.add(";");
        oldPuncts.add(",");
        oldPuncts.add(",");
        oldPuncts.add(":");
        oldPuncts.add("(");
        oldPuncts.add(")");
        oldPuncts.add("[");
        oldPuncts.add("]");
        oldPuncts.add("{");
        oldPuncts.add("}");
        oldPuncts.add("\"");
        oldPuncts.add("\'");

        newPuncts.add("\n.\n");
        newPuncts.add("\n?\n");
        newPuncts.add("\n!\n");
        newPuncts.add("\n;\n");
        newPuncts.add("\n,\n");
        newPuncts.add("\n,\n");
        newPuncts.add("\n:\n");
        newPuncts.add("\n(\n");
        newPuncts.add("\n)\n");
        newPuncts.add("\n[\n");
        newPuncts.add("\n]\n");
        newPuncts.add("\n{\n");
        newPuncts.add("\n}\n");
        newPuncts.add("\n\"\n");
        newPuncts.add("\n\'\n");

        String parseTxt = _txt.toString().trim();
        parseTxt = parseTxt.replaceAll("\\s+", "\n");
        while (!oldPuncts.isEmpty()) {
                    parseTxt = parseTxt.replace(oldPuncts.poll(), newPuncts.poll());
        }

        return parseTxt;
    }
}

package org.atilf.cli.disambiguation;

import ch.qos.logback.classic.Level;
import org.apache.commons.cli.*;
import org.atilf.module.tools.CLIUtils;
import org.atilf.runner.Runner;
import org.atilf.runner.RunnerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.TransformerException;
import java.io.IOException;

//import org.atilf.runner.Disambiguation;

/**
 * @author Simon Meoni
 *         Created on 11/10/16.
 */
public class DisambiguationCLI {
    private static final Options options = new Options();
    private static final Logger LOGGER = LoggerFactory.getLogger(DisambiguationCLI.class.getName());

    private DisambiguationCLI() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * @param args shell args
     * @throws IOException
     * @throws InterruptedException
     * @throws TransformerException
     */
    public static void main(String[] args) throws IOException, InterruptedException, TransformerException {
        CommandLineParser parser = new PosixParser();

        Option learning = new Option("le","learning",true,"path of the learning corpus in tei format");
        learning.setRequired(true);
        Option evaluation = new Option("e","evaluation",true,"path of the evaluation corpus in tei format");
        evaluation.setRequired(true);
        Option out = new Option("o","output",true,"output folder");
        out.setRequired(true);
        Option debug = new Option("d","debug",true,"show debug log");
        debug.setRequired(false);
        debug.setArgs(0);
        Option thresholdMin = new Option("tmin","thresholdMin",true,"specificity profile threshold filter minimum");
        thresholdMin.setRequired(false);
        Option thresholdMax = new Option("tmax","thresholdMax",true,"specificity profile threshold filter maximum");
        thresholdMax.setRequired(false);

        options.addOption(learning);
        options.addOption(evaluation);
        options.addOption(out);
        options.addOption(thresholdMin);
        options.addOption(thresholdMax);
        options.addOption(debug);

        try {
            CommandLine line = parser.parse( options, args );

            if (line.hasOption("debug")){
                CLIUtils.setGlobalLogLevel(Level.DEBUG);
            }
            RunnerBuilder runnerBuilder = new RunnerBuilder()
                    .setLearningPath(line.getOptionValue("le"))
                    .setBpmnDiagram("runner/disambiguation.bpmn20.xml")
                    .setEvaluationPath(line.getOptionValue("e"))
                    .setOut(line.getOptionValue("o"));

            if (line.hasOption("thresholdMin") && line.hasOption("thresholdMax")){
                runnerBuilder.setThresholdMin(Integer.parseInt(line.getOptionValue("tmin")));
                runnerBuilder.setThresholdMax(Integer.parseInt(line.getOptionValue("tmax")));
            }
            Runner runner = runnerBuilder.createRunner();
            runner.execute();
        } catch (ParseException e) {
            LOGGER.error("There are some problems during running : ",e);
        } catch (Exception e) {
            LOGGER.error("There are some problems during the creation of runner : ",e);
        }
    }
}

package thread;

import eu.project.ttc.tools.TermSuitePipeline;
import module.FilesUtilities;
import module.TermSuitePipelineBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created by Simon Meoni on 18/08/16.
 */
public class TermithXmlInjector {

    private static Logger LOGGER = Logger.getLogger(TermithXmlInjector.class.getName());
    private static int DEFAULT_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    private int poolSize;
    private Path corpus;
    private Map<String, StringBuffer> extractedText;
    private ExecutorService executorService;
    private String treeTaggerHome;
    private String lang;

    public TermithXmlInjector (Map<String, StringBuffer> extractedText, String treeTaggerHome, String lang)
            throws IOException {
        this(DEFAULT_POOL_SIZE, extractedText, treeTaggerHome, lang);
    }

    public TermithXmlInjector(int poolSize, Map<String, StringBuffer> extractedText,
                              String treeTaggerHome, String lang) throws IOException {
        this.poolSize = poolSize;
        this.treeTaggerHome = treeTaggerHome;
        this.executorService = Executors.newFixedThreadPool(poolSize);
        this.extractedText = extractedText;
        this.corpus = Paths.get(FilesUtilities.createTemporaryFolder("corpus"));
        this.lang = lang;

        LOGGER.log(Level.INFO, "temporary folder created: " + this.corpus);
        Files.createDirectories(Paths.get(this.corpus + "/json"));
        Files.createDirectories(Paths.get(this.corpus + "/txt"));
        LOGGER.log(Level.INFO, "create temporary text files in " + this.corpus + "/txt folder");
        FilesUtilities.createFiles(this.corpus + "/txt", extractedText, "txt");
    }

    public void execute() throws InterruptedException, IOException, ExecutionException {
        Future<TermSuitePipeline> termsuiteTask = executorService.submit(
                new TextTermSuiteWorker(treeTaggerHome, this.corpus + "/txt", lang)
        );
        JsonRetrieverWorker jsonRetrieverWorker = new JsonRetrieverWorker(Paths.get(this.corpus + "/json"),
                executorService, termsuiteTask);
        executorService.submit(jsonRetrieverWorker);
        termsuiteTask.get();
        jsonRetrieverWorker.stop();
        executorService.shutdown();
        executorService.awaitTermination(1L,TimeUnit.DAYS);
    }

    private class JsonRetrieverWorker implements Runnable {

        private Logger LOGGER = Logger.getLogger(JsonRetrieverWorker.class.getName());
        WatchService watcher = FileSystems.getDefault().newWatchService();
        Path dir;
        WatchKey key;
        ExecutorService executorService;
        Future<?> task;

        JsonRetrieverWorker(Path dir, ExecutorService executorService, Future<?> task) throws IOException {
            LOGGER.log(Level.INFO, "Initialized File Watching Service");
            this.dir = dir;
            this.key = dir.register(watcher, ENTRY_CREATE);
            this.executorService = executorService;
            this.task = task;
        }

        @Override
        public void run() {
            LOGGER.log(Level.INFO,"File Watcher Service Started");
            for (;;) {
                try {
                    this.key = watcher.take();

                } catch (InterruptedException x) {
                    return;
                }
                for (WatchEvent<?> event: key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    WatchEvent<Path> ev = (WatchEvent<Path>)event;
                    Path filename = dir.resolve(ev.context());
                    LOGGER.log(Level.INFO, "New file retrieve: " + filename);
                    executorService.execute(new MorphoSyntaxInjectorWorker(filename.toFile()));
                }
                boolean valid = key.reset();
                if (!valid) {
                    LOGGER.log(Level.INFO,"Interrupt File Watcher Service");
                    break;
                }
            }
        }

        public void stop() throws IOException {
            LOGGER.log(Level.INFO,"File Watcher Service Terminated");
            watcher.close();
        }

    }

    private class TextTermSuiteWorker implements Callable<TermSuitePipeline> {
        String textPath;
        String treeTaggerHome;
        String lang;

        TextTermSuiteWorker(String treeTaggerHome, String textPath, String lang) {
            this.textPath = textPath;
            this.treeTaggerHome = treeTaggerHome;
            this.lang = lang;
        }

        @Override
        public TermSuitePipeline call() throws Exception {
            LOGGER.log(Level.INFO,"Build Termsuite Pipeline");
            TermSuitePipelineBuilder termSuitePipelineBuilder = new TermSuitePipelineBuilder(
                    lang,
                    this.textPath,
                    this.treeTaggerHome
            );
            LOGGER.log(Level.INFO,"Run Termsuite Pipeline");
            termSuitePipelineBuilder.getTermsuitePipeline().run();
            LOGGER.log(Level.INFO, "Finished execution of Termsuite Pipeline, result in :" +
                    textPath.replace("/txt",""));
            return termSuitePipelineBuilder.getTermsuitePipeline();
        }
    }

    private class MorphoSyntaxInjectorWorker implements Runnable{

        private Logger LOGGER = Logger.getLogger(JsonRetrieverWorker.class.getName());
        File json;
        ConcurrentHashMap<String, Initializer> intialializedFiles;

        MorphoSyntaxInjectorWorker(File json) {
            this.json = json;
        }

        @Override
        public void run() {
            LOGGER.log(Level.INFO,"MorphoSyntaxInjectorWorker Started, processing: " + json.getAbsolutePath());
            //TODO Implement 9th phase of TermITH process
            LOGGER.log(Level.INFO,"MorphoSyntaxInjectorWorker Terminated");
        }
    }
}

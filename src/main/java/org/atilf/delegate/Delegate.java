package org.atilf.delegate;

import com.google.common.eventbus.EventBus;
import org.atilf.models.TermithIndex;
import org.atilf.monitor.observer.MemoryPerformanceEvent;
import org.atilf.monitor.observer.TimePerformanceEvent;
import org.atilf.runner.Runner;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * the abstract class Delegate is a main part of the workflow of the termith process. The runner classes call the
 * inherit Delegate classes linearly.
 * The execute method is contains the multithreaded jobs (like the classes module inherited from the Runnable class)
 * who process the file corpus
 * @author Simon Meoni Created on 10/11/16.
 */
public abstract class Delegate implements JavaDelegate{
    protected final ExecutorService _executorService = Executors.newFixedThreadPool(Runner._poolSize);
    protected final Logger _logger = LoggerFactory.getLogger(this.getClass().getName());
    protected TermithIndex _termithIndex = Runner._termithIndex;
    private EventBus _eventBus = new EventBus();
    private DelegateExecution _execution;
    private TimePerformanceEvent _timePerformanceEvent;
    private MemoryPerformanceEvent _memoryPerformanceEvent;

    /**
     * this method is used to execute the different steps of processing of a delegate
     * @throws IOException thrown a IO exception if a file is not found or have a permission problem during the
     * xsl transformation phase
     * @throws InterruptedException thrown if awaitTermination function is interrupted while waiting
     * @throws ExecutionException thrown a exception if a system process is interrupted
     */
    protected void execute() throws IOException, InterruptedException, ExecutionException {}

    @Override
    public void execute(DelegateExecution execution) {
        try {
            initialize(execution);
            execute();
            _eventBus.post(_timePerformanceEvent);
            _eventBus.post(_memoryPerformanceEvent);
        } catch (IOException | InterruptedException | ExecutionException e) {
            _logger.error("there are some errors during execution of " + this.getClass().getName() + " :",e);
        }
    }

    public void initialize(DelegateExecution execution){
        _execution = execution;
        _timePerformanceEvent = new TimePerformanceEvent(
                this.getClass().getSimpleName(),
                Runner.getCorpusSize(),
                Runner.getTimePerformanceEvents()
        );
        _memoryPerformanceEvent = new MemoryPerformanceEvent(
                this.getClass().getSimpleName(),
                Runner.getCorpusSize(),
                Runner.getMemoryPerformanceEvents()
        );
        _eventBus.register(_timePerformanceEvent);
        _eventBus.register(_memoryPerformanceEvent);
    }

    protected  <T extends Object> T getFlowableVariable(String flowableName, T defaultValue){

        T getVar = (T) _execution.getVariable(flowableName);

        if (_execution.getVariable(flowableName) != null) {
            return getVar;
        } else {
            return defaultValue;
        }
    }
}
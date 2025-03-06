package org.dacss.projectinitai.loaders;

import ai.djl.Model;
import ai.djl.ModelException;
import uk.ac.manchester.tornado.api.*;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;
import uk.ac.manchester.tornado.api.exceptions.TornadoExecutionPlanException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <h1>{@link LoadUnLoadLLM}</h1>
 * Class for loading and unloading a pre-trained model dynamically on to/from the GPU.
 * this will most likely be moved to the 'models-mod' module. if we dont create any more loaders.
 */
public class LoadUnLoadLLM {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoadUnLoadLLM.class);
    private Model model;
    private byte[] modelData;

    /**
     * {@link #LoadUnLoadLLM()}
     * 0-parameter constructor.
     */
    public LoadUnLoadLLM() {
    }

    /**
     * {@link #loadUnloadLLM(String)}
     * Loads a model with DJL and TornadoVM.
     *
     * @return byte[] - returns the model data.
     */
    public byte[] loadUnloadLLM(String modelPath) {
        try {
            Path path = Paths.get(modelPath);
            model = Model.newInstance("model");
            model.load(path);

            modelData = Files.readAllBytes(path);
            final byte[] finalModelData = modelData; // Make modelData effectively final

            // Create a 2D Worker
            WorkerGrid workerGrid = new WorkerGrid2D(16, 16);
            // Attach the worker to the Grid
            GridScheduler gridScheduler = new GridScheduler("loadUnloadLLM", workerGrid);
            // Create a context
            KernelContext context = new KernelContext();
            // Set the local-group size
            workerGrid.setLocalWork(16, 16, 1);

            TaskGraph taskGraph = new TaskGraph("s0")
                    .transferToDevice(DataTransferMode.FIRST_EXECUTION, finalModelData)
                    .task("loadModel", () -> {
                        int idx = context.globalIdx;
                        if (idx < finalModelData.length) {
                            finalModelData[idx] = (byte) (finalModelData[idx] + 1);
                        }
                    })
                    .task("killSwitch", () -> {
                        int idx = context.globalIdx;
                        if (idx < finalModelData.length) {
                            //fixme ASAP!: we need to ensure this will not 0 out all data on GPU, but only the model data
                            finalModelData[idx] = 0; // Simulate unloading by setting data to zero
                        }
                    })
                    .transferToHost(DataTransferMode.EVERY_EXECUTION, finalModelData);

            ImmutableTaskGraph immutableTaskGraph = taskGraph.snapshot();

            try (TornadoExecutionPlan executionPlan = new TornadoExecutionPlan(immutableTaskGraph)) {
                executionPlan.getDevice(0).getAvailableProcessors();
                executionPlan.withGridScheduler(gridScheduler).execute();
            } catch (TornadoExecutionPlanException loadModelKernelExc) {
                log.error("Error executing Tornado plan: {}", loadModelKernelExc.getMessage());
            } finally {
                //fixme ASAP: we dont want this to be happening on every execution of the class, only when the model is unloaded(ie. by user switching llm or shutting down the system)
                killSwitch(finalModelData);
            }
        } catch (IOException | ModelException loadUnLoadExc) {
            log.error("Error loading model: {}", loadUnLoadExc.getMessage());
        }

        return modelData;
    }

    /**
     * KillSwitch method to transfer the LLM back to the host and set the data to 0.
     *
     * @param modelData the model data.
     */
    private void killSwitch(byte[] modelData) {
        // This method is now integrated into the task graph
    }

    /**
     * {@link #unloadModel(byte[])}
     * Unloads a model with DJL and TornadoVM.
     *
     * @return boolean - returns true if the model was successfully unloaded.
     */
    public boolean unloadModel(byte[] modelData) {
        boolean success = false;
        try {
            if (model != null) {
                model.close();
            }
            success = true;
        } catch (Exception unloadExc) {
            log.error("Error unloading model: {}", unloadExc.getMessage());
        }

        return success;
    }

    /**
     * {@link #loadModel(String)}
     * Loads a model from the file system.
     *
     * @param modelPath the path to the model.
     * @return byte[] - returns the model data.
     */
    private byte[] loadModel(String modelPath) throws IOException {
        Path path = Paths.get(modelPath);
        return Files.readAllBytes(path);
    }

    /**
     * {@link #getModel()}
     *
     * @return byte[] - returns the model.
     */
    public byte[] getModel() {
        return loadUnloadLLM("modelPath");
    }

    /**
     * {@link #modelIsLoaded()}
     * Checks if the kernel is loaded.
     *
     * @return boolean - returns true if the kernel is loaded.
     */
    public boolean modelIsLoaded() {
        return model != null;
    }

}
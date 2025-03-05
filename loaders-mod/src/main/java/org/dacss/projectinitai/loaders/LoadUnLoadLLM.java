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
 * Class for loading and unloading a model dynamically on to/from the GPU.
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
     * {@link #loadModelKernel(String)}
     * Loads a model with DJL and TornadoVM.
     *
     * @return byte[] - returns the model data.
     */
    public byte[] loadModelKernel(String modelPath) {
        try {
            Path path = Paths.get(modelPath);
            model = Model.newInstance("model");
            model.load(path);

            modelData = Files.readAllBytes(path);
            final byte[] finalModelData = modelData; // Make modelData effectively final

            TaskGraph taskGraph = new TaskGraph("s0")
                    .transferToDevice(DataTransferMode.FIRST_EXECUTION, finalModelData)
                    .task("loadModel", () -> {
                        KernelContext context = new KernelContext();
                        int idx = context.globalIdx;
                        if (idx < finalModelData.length) {
                            finalModelData[idx] = (byte) (finalModelData[idx] + 1);
                        }
                    })
                    .task("killSwitch", () -> {
                        KernelContext context = new KernelContext();
                        int idx = context.globalIdx;
                        if (idx < finalModelData.length) {
                            finalModelData[idx] = 0; // Simulate unloading by setting data to zero
                        }
                    })
                    .transferToHost(DataTransferMode.EVERY_EXECUTION, finalModelData);

            ImmutableTaskGraph immutableTaskGraph = taskGraph.snapshot();

            try (TornadoExecutionPlan executionPlan = new TornadoExecutionPlan(immutableTaskGraph)) {
                executionPlan.getDevice(0).getAvailableProcessors();
                executionPlan.withDynamicReconfiguration(Policy.PERFORMANCE, DRMode.PARALLEL).execute();
            } catch (TornadoExecutionPlanException loadModelKernelExc) {
                log.error("Error executing Tornado plan: {}", loadModelKernelExc.getMessage());
            } finally {
                killSwitch(finalModelData);
            }
        } catch (IOException | ModelException e) {
            log.error("Error loading model: {}", e.getMessage());
        }

        return modelData;
    }

    /**
     * Killswitch method to transfer the LLM back to the host and set the data to 0.
     *
     * @param modelData the model data.
     */
    private void killSwitch(byte[] modelData) {
        // This method is now integrated into the task graph
    }

    /**
     * {@link #unloadModelKernel(byte[])}
     * Unloads a model with DJL and TornadoVM.
     *
     * @return boolean - returns true if the model was successfully unloaded.
     */
    public boolean unloadModelKernel(byte[] modelData) {
        boolean success = false;
        try {
            if (model != null) {
                model.close();
            }
            success = true;
        } catch (Exception e) {
            log.error("Error unloading model: {}", e.getMessage());
        }

        return success;
    }

    /**
     * {@link #loadModel(String)}
     * <p>
     * Loads a model from the file system.
     * </p>
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
        return loadModelKernel("modelPath");
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

    /**
     * {@link #kernelIsUnloaded()}
     * Checks if the kernel is unloaded.
     *
     * @return boolean - returns true if the kernel is unloaded.
     */
    public boolean kernelIsUnloaded() {
        return model == null;
    }
}
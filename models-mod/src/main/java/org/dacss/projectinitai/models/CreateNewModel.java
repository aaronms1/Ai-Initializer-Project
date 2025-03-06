package org.dacss.projectinitai.models;

import ai.djl.Model;
import ai.djl.ndarray.NDManager;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;
import ai.djl.nn.norm.BatchNorm;
import ai.djl.nn.norm.Dropout;
import ai.djl.training.initializer.Initializer;
import ai.djl.training.initializer.XavierInitializer;
import reactor.core.publisher.Flux;
import uk.ac.manchester.tornado.api.*;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;
import uk.ac.manchester.tornado.api.exceptions.TornadoExecutionPlanException;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * <h1>{@link CreateNewModel}</h1>
 * Class to create a new language model (LLM) with specified parameters.
 */
public class CreateNewModel implements ModelsIface<String> {

    private static final String MODELS_DIR = System.getProperty("user.home") + "/.project-ai-initializer/models/";
    private final String modelName;
    private final ModelSettings modelSettings;

    /**
     * Constructor to initialize CreateNewModel with specific model settings.
     *
     * @param modelName The name of the new model.
     * @param modelSettings The settings for the new model.
     */
    public CreateNewModel(String modelName, ModelSettings modelSettings) {
        this.modelName = modelName;
        this.modelSettings = modelSettings;
    }

    /**
     * Creates a new language model (LLM) with specified parameters.
     *
     * @param action The action to be performed (e.g., CREATE).
     * @return A Flux emitting the path to the created model.
     */
    @Override
    public Flux<String> processModel(ModelsActions action) {
        if (action == ModelsActions.CREATE) {
            try (NDManager manager = NDManager.newBaseManager()) {
                Initializer initializer = new XavierInitializer();

                // Define the model architecture
                SequentialBlock block = new SequentialBlock();

                // Define tasks for TornadoVM
                TaskGraph taskGraph = new TaskGraph("createModel")
                        .task("addLinear1", () -> block.add(Linear.builder().setUnits(modelSettings.getHiddenSize()).build()))
                        .task("addBatchNorm", () -> block.add(BatchNorm.builder().build()))
                        .task("addDropout", () -> block.add(Dropout.builder().optRate(modelSettings.getDropoutRate()).build()))
                        .task("addLinear2", () -> block.add(Linear.builder().setUnits(modelSettings.getVocabSize()).build()))
                        .transferToDevice(DataTransferMode.FIRST_EXECUTION, block)
                        .task("initializeModel", () -> {
                            // Initialize model parameters here
                        })
                        .task("saveModel", () -> {
                            try (Model model = Model.newInstance(modelName)) {
                                model.setBlock(block);
                                String outputPath = MODELS_DIR + modelName;
                                model.save(Paths.get(outputPath), modelName);
                            } catch (IOException createModelSaveTaskExc) {
                                throw new RuntimeException(createModelSaveTaskExc);
                            }
                        })
                        .transferToHost(DataTransferMode.EVERY_EXECUTION, block);

                // Create a 2D Worker
                WorkerGrid workerGrid = new WorkerGrid2D(16, 16);
                // Attach the worker to the Grid
                GridScheduler gridScheduler = new GridScheduler("createModel", workerGrid);
                // Create a context
                KernelContext context = new KernelContext();
                // Set the local-group size
                workerGrid.setLocalWork(16, 16, 1);

                // Create an immutable task-graph
                try (TornadoExecutionPlan executionPlan = new TornadoExecutionPlan(taskGraph.snapshot())) {
                    // Execute the task graph
                    TornadoExecutionResult result = executionPlan.withGridScheduler(gridScheduler).execute();
                } catch (TornadoExecutionPlanException createNewModelExc) {
                    throw new RuntimeException(createNewModelExc);
                }

                return Flux.just(MODELS_DIR + modelName);
            }
        }
        return Flux.empty();
    }
}
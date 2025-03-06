package org.dacss.projectinitai.models;

import ai.djl.Model;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.nn.Parameter;
import reactor.core.publisher.Flux;
import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.TornadoExecutionPlan;
import uk.ac.manchester.tornado.api.TornadoExecutionResult;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * <h1>{@link MergeModels}</h1>
 * Class to merge two models by averaging their parameters.
 */
public class MergeModels implements ModelsIface<String> {

    private static final String MODELS_DIR = System.getProperty("user.home") + "/.project-ai-initializer/models/";
    private final String modelPath1;
    private final String modelPath2;

    /**
     * Constructor to initialize MergeModels with specific model paths.
     *
     * @param modelPath1 The path to the first model.
     * @param modelPath2 The path to the second model.
     */
    public MergeModels(String modelPath1, String modelPath2) {
        this.modelPath1 = modelPath1;
        this.modelPath2 = modelPath2;
    }

    /**
     * Merges two models by averaging their parameters.
     *
     * @param action The action to be performed (e.g., MERGE).
     * @return A Flux emitting the merged model settings.
     */
    @Override
    public Flux<String> processModel(ModelsActions action) {
        if (action == ModelsActions.MERGE) {
            ModelSettingsFactory settingsFactory1 = new ModelSettingsFactory(modelPath1);
            ModelSettingsFactory settingsFactory2 = new ModelSettingsFactory(modelPath2);

            return Flux.zip(
                    settingsFactory1.processModel(ModelsActions.SETTINGS).next(),
                    settingsFactory2.processModel(ModelsActions.SETTINGS).next()
                )
                .flatMap(tuple -> {
                    ModelSettings settings1 = tuple.getT1();
                    ModelSettings settings2 = tuple.getT2();

                    try (Model model1 = Model.newInstance(settings1.getModelName(), MODELS_DIR + modelPath1);
                         Model model2 = Model.newInstance(settings2.getModelName(), MODELS_DIR + modelPath2);
                         NDManager manager = NDManager.newBaseManager()) {

                        Map<String, NDArray> params1 = new HashMap<>();
                        Map<String, NDArray> params2 = new HashMap<>();
                        Map<String, NDArray> mergedParams = new HashMap<>();

                        // Define tasks for TornadoVM
                        TaskGraph taskGraph = new TaskGraph("mergeModels")
                                .transferToDevice(DataTransferMode.FIRST_EXECUTION, params1, params2)
                                .task("loadParams1", () -> loadParameters(model1, params1))
                                .task("loadParams2", () -> loadParameters(model2, params2))
                                .task("mergeParams", () -> mergeParameters(params1, params2, mergedParams))
                                .transferToHost(DataTransferMode.EVERY_EXECUTION, mergedParams);

                        // Create an immutable task-graph
                        TornadoExecutionPlan executionPlan = new TornadoExecutionPlan(taskGraph.snapshot());

                        // Execute the task graph
                        TornadoExecutionResult result = executionPlan.execute();

                        Model mergedModel = Model.newInstance("mergedModel");
                        mergedModel.setBlock(model1.getBlock());
                        for (@Parallel Map.Entry<String, NDArray> entry : mergedParams.entrySet()) {
                            Parameter param = mergedModel.getBlock().getParameters().get(entry.getKey());
                            if (param != null) {
                                param.setArray(entry.getValue());
                            }
                        }

                        String outputPath = MODELS_DIR + "mergedModel";
                        mergedModel.save(Paths.get(outputPath), "mergedModel");

                        return Flux.just(outputPath);
                    } catch (IOException e) {
                        return Flux.error(e);
                    }
                })
                .onErrorResume(mergeModelsExc -> {
                    System.err.println("Error processing merged model: " + mergeModelsExc.getMessage());
                    return Flux.empty();
                });
        }
        return Flux.empty();
    }

    /**
     * Loads the parameters of a model into a map.
     *
     * @param model The model from which to load parameters.
     * @param params The map to store the parameters.
     */
    private void loadParameters(Model model, Map<String, NDArray> params) {
        for (@Parallel Parameter param : model.getBlock().getParameters().values()) {
            params.put(param.getName(), param.getArray());
        }
    }

    /**
     * Merges the parameters of two models by averaging them.
     *
     * @param params1 The parameters of the first model.
     * @param params2 The parameters of the second model.
     * @param mergedParams The map to store the merged parameters.
     */
    private void mergeParameters(Map<String, NDArray> params1, Map<String, NDArray> params2, Map<String, NDArray> mergedParams) {
        for (@Parallel String key : params1.keySet()) {
            if (params2.containsKey(key)) {
                NDArray array1 = params1.get(key);
                NDArray array2 = params2.get(key);
                NDArray mergedArray = array1.add(array2).div(2);
                mergedParams.put(key, mergedArray);
            }
        }
    }
}
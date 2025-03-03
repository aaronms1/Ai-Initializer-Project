// package org.dacss.projectinitai.services;

// import org.dacss.projectinitai.annotations.Bridge;
// import org.dacss.projectinitai.models.ModelActions;
// import org.dacss.projectinitai.models.ModelIface;
// import org.dacss.projectinitai.models.utilities.*;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.stereotype.Service;
// import reactor.core.publisher.Flux;

// import java.io.IOException;
// import java.text.MessageFormat;

// /**
//  * <h1>{@link ModelsService}</h1>
//  * Backend hilla endpoint service for model operations.
//  */
// @Service
// @Bridge("models-service")
// public class ModelsService implements ModelIface {

//     private static final Logger log = LoggerFactory.getLogger(ModelsService.class);

//     public ModelsService() {}

//     @Override
//     public Flux<Object> processModel(ModelActions action, String modelPath1, String modelPath2) throws IOException {
//         Flux<Object> flux;
//         try {
//             flux = switch (action) {
//                 case CREATE -> CreateNewModelUtil.createNewModel();
//                 case CLONE -> CloneModelUtil.cloneModel(modelPath1);
//                 case DESTROY -> DestroyModelUtil.destroyModel(modelPath1);
//                 case LIST -> ListModelsUtil.listModels();
//                 case MERGE -> MergeModelUtil.mergeModels(modelPath1, modelPath2);
//                 case SETTINGS -> null;
//                 case TRAIN -> null;
//             };
//         } catch (IOException modelsServiceExc) {
//             log.error("{}: Error processing model: {}", action,  modelsServiceExc.getMessage());
//             throw modelsServiceExc;
//         } finally {
//             log.info("{}: {} {}", action, modelPath1, modelPath2);
//         }
//         assert flux != null;
//         return flux;
//     }
// }

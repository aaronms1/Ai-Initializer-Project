 package org.dacss.projectinitai.services;

 import org.dacss.projectinitai.annotations.Bridge;

 import org.dacss.projectinitai.models.*;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.stereotype.Service;
 import reactor.core.publisher.Flux;

 import java.io.IOException;

 /**
  * <h1>{@link ModelsService}</h1>
  */
 @Service
 @Bridge("models-service")
 public class ModelsService implements ModelsIface {

     Logger logger = LoggerFactory.getLogger(ModelsService.class);

     public ModelsService() {}

     @Override
     public Flux<Object> processModel(ModelsActions action) throws IOException {
         Flux<Object> flux;
         String modelPath1 = "";
            String modelPath2 = "";
         try {
             flux = switch (action) {
                 case CREATE -> CreateNewModel.createNewModel();
                 case CLONE -> CloneModel.cloneModel();
                 case DESTROY -> DestroyModel.destroyModel();
                 case LIST -> ListLocalModels.listModels();
                 case MERGE -> MergeModels.mergeModels(modelPath1, modelPath2);
                 case SETTINGS -> null;
                 case TRAIN -> null;
             };
         } finally {
             logger.info("Processed model action: {}", action);
         }
         assert flux != null;
         return flux;
     }
 }

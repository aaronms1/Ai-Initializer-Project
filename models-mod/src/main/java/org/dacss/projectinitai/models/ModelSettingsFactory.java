package org.dacss.projectinitai.models;

public class ModelSettingsFactory {
    public static ModelSettings createModelSettings(String modelName, String modelVersion, String architectures, double attentionDropout, int bosTokenId, int eosTokenId, String hiddenAct, int hiddenSize, double initializerRange, int intermediateSize, int maxPositionEmbeddings, int maxWindowLayers, String modelType, int numAttentionHeads, int numHiddenLayers, int numKeyValueHeads, double rmsNormEps, double ropeTheta, int slidingWindow, boolean tieWordEmbeddings, String torchDtype, String transformersVersion, boolean useCache, boolean useMrope, boolean useSlidingWindow, int vocabSize) {
        ModelSettings settings = new ModelSettings();
        settings.setModelName(modelName);
        settings.setModelVersion(modelVersion);
        settings.setArchitectures(architectures);
        settings.setAttentionDropout(attentionDropout);
        settings.setBosTokenId(bosTokenId);
        settings.setEosTokenId(eosTokenId);
        settings.setHiddenAct(hiddenAct);
        settings.setHiddenSize(hiddenSize);
        settings.setInitializerRange(initializerRange);
        settings.setIntermediateSize(intermediateSize);
        settings.setMaxPositionEmbeddings(maxPositionEmbeddings);
        settings.setMaxWindowLayers(maxWindowLayers);
        settings.setModelType(modelType);
        settings.setNumAttentionHeads(numAttentionHeads);
        settings.setNumHiddenLayers(numHiddenLayers);
        settings.setNumKeyValueHeads(numKeyValueHeads);
        settings.setRmsNormEps(rmsNormEps);
        settings.setRopeTheta(ropeTheta);
        settings.setSlidingWindow(slidingWindow);
        settings.setTieWordEmbeddings(tieWordEmbeddings);
        settings.setTorchDtype(torchDtype);
        settings.setTransformersVersion(transformersVersion);
        settings.setUseCache(useCache);
        settings.setUseMrope(useMrope);
        settings.setUseSlidingWindow(useSlidingWindow);
        settings.setVocabSize(vocabSize);
        return settings;
    }
}
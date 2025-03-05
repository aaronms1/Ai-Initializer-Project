package org.dacss.projectinitai.models;

public class ModelSettings {
    private String modelName;
    private String modelVersion;
    private String architectures;
    private double attentionDropout;
    private int bosTokenId;
    private int eosTokenId;
    private String hiddenAct;
    private int hiddenSize;
    private double initializerRange;
    private int intermediateSize;
    private int maxPositionEmbeddings;
    private int maxWindowLayers;
    private String modelType;
    private int numAttentionHeads;
    private int numHiddenLayers;
    private int numKeyValueHeads;
    private double rmsNormEps;
    private double ropeTheta;
    private int slidingWindow;
    private boolean tieWordEmbeddings;
    private String torchDtype;
    private String transformersVersion;
    private boolean useCache;
    private boolean useMrope;
    private boolean useSlidingWindow;
    private int vocabSize;

    public ModelSettings() {
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getArchitectures() {
        return architectures;
    }

    public void setArchitectures(String architectures) {
        this.architectures = architectures;
    }

    public double getAttentionDropout() {
        return attentionDropout;
    }

    public void setAttentionDropout(double attentionDropout) {
        this.attentionDropout = attentionDropout;
    }

    public int getBosTokenId() {
        return bosTokenId;
    }

    public void setBosTokenId(int bosTokenId) {
        this.bosTokenId = bosTokenId;
    }

    public int getEosTokenId() {
        return eosTokenId;
    }

    public void setEosTokenId(int eosTokenId) {
        this.eosTokenId = eosTokenId;
    }

    public String getHiddenAct() {
        return hiddenAct;
    }

    public void setHiddenAct(String hiddenAct) {
        this.hiddenAct = hiddenAct;
    }

    public int getHiddenSize() {
        return hiddenSize;
    }

    public void setHiddenSize(int hiddenSize) {
        this.hiddenSize = hiddenSize;
    }

    public double getInitializerRange() {
        return initializerRange;
    }

    public void setInitializerRange(double initializerRange) {
        this.initializerRange = initializerRange;
    }

    public int getIntermediateSize() {
        return intermediateSize;
    }

    public void setIntermediateSize(int intermediateSize) {
        this.intermediateSize = intermediateSize;
    }

    public int getMaxPositionEmbeddings() {
        return maxPositionEmbeddings;
    }

    public void setMaxPositionEmbeddings(int maxPositionEmbeddings) {
        this.maxPositionEmbeddings = maxPositionEmbeddings;
    }

    public int getMaxWindowLayers() {
        return maxWindowLayers;
    }

    public void setMaxWindowLayers(int maxWindowLayers) {
        this.maxWindowLayers = maxWindowLayers;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public int getNumAttentionHeads() {
        return numAttentionHeads;
    }

    public void setNumAttentionHeads(int numAttentionHeads) {
        this.numAttentionHeads = numAttentionHeads;
    }

    public int getNumHiddenLayers() {
        return numHiddenLayers;
    }

    public void setNumHiddenLayers(int numHiddenLayers) {
        this.numHiddenLayers = numHiddenLayers;
    }

    public int getNumKeyValueHeads() {
        return numKeyValueHeads;
    }

    public void setNumKeyValueHeads(int numKeyValueHeads) {
        this.numKeyValueHeads = numKeyValueHeads;
    }

    public double getRmsNormEps() {
        return rmsNormEps;
    }

    public void setRmsNormEps(double rmsNormEps) {
        this.rmsNormEps = rmsNormEps;
    }

    public double getRopeTheta() {
        return ropeTheta;
    }

    public void setRopeTheta(double ropeTheta) {
        this.ropeTheta = ropeTheta;
    }

    public int getSlidingWindow() {
        return slidingWindow;
    }

    public void setSlidingWindow(int slidingWindow) {
        this.slidingWindow = slidingWindow;
    }

    public boolean isTieWordEmbeddings() {
        return tieWordEmbeddings;
    }

    public void setTieWordEmbeddings(boolean tieWordEmbeddings) {
        this.tieWordEmbeddings = tieWordEmbeddings;
    }

    public String getTorchDtype() {
        return torchDtype;
    }

    public void setTorchDtype(String torchDtype) {
        this.torchDtype = torchDtype;
    }

    public String getTransformersVersion() {
        return transformersVersion;
    }

    public void setTransformersVersion(String transformersVersion) {
        this.transformersVersion = transformersVersion;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public boolean isUseMrope() {
        return useMrope;
    }

    public void setUseMrope(boolean useMrope) {
        this.useMrope = useMrope;
    }

    public boolean isUseSlidingWindow() {
        return useSlidingWindow;
    }

    public void setUseSlidingWindow(boolean useSlidingWindow) {
        this.useSlidingWindow = useSlidingWindow;
    }

    public int getVocabSize() {
        return vocabSize;
    }

    public void setVocabSize(int vocabSize) {
        this.vocabSize = vocabSize;
    }
}
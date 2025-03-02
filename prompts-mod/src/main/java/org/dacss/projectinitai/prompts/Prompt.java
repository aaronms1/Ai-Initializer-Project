package org.dacss.projectinitai.prompts;

/**
 * <h1>{@link Prompt}</h1>
 * This class represents a prompt with a specified maximum number of tokens.
 */
public class Prompt {
    private String prompt;
    private int maxTokens;

    /**
     * <h3>{@link #Prompt(String, int)}</h3>
     *
     * @param prompt The text of the prompt.
     * @param maxTokens The maximum number of tokens for the prompt.
     */
    public Prompt(String prompt, int maxTokens) {
        this.prompt = prompt;
        this.maxTokens = maxTokens;
    }

    /**
     * Gets the text of the prompt.
     *
     * @return The text of the prompt.
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * Sets the text of the prompt.
     *
     * @param prompt The new text of the prompt.
     */
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    /**
     * Gets the maximum number of tokens for the prompt.
     *
     * @return The maximum number of tokens.
     */
    public int getMaxTokens() {
        return maxTokens;
    }

    /**
     * Sets the maximum number of tokens for the prompt.
     *
     * @param maxTokens The new maximum number of tokens.
     */
    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    /**
     * Returns a string representation of the prompt in JSON format.
     *
     * @return A JSON string representation of the prompt.
     */
    @Override
    public String toString() {
        return "{\"prompt\": \"" + prompt + "\", \"max_tokens\": " + maxTokens + "}";
    }
}
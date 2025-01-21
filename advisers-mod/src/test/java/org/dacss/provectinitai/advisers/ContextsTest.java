package org.dacss.provectinitai.advisers;

import org.dacss.projectinitai.contexts.ContextsHandler;
import org.dacss.projectinitai.advisers.interfaces.AIOutputContextualAdviserIface;
import org.dacss.projectinitai.advisers.implementors.ContextFacadeIfaceImpl;
import org.dacss.projectinitai.advisers.interfaces.ContextualAdviserIface;
import org.dacss.projectinitai.advisers.interfaces.DataHandlerContextualAdviserIface;
import org.dacss.projectinitai.advisers.interfaces.UserInputContextualAdviserIface;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class ContextsTest {

    @Test
    public void testContextFacadeIface() {
        ContextFacadeIfaceImpl<String> contextFacade = new ContextFacadeIfaceImpl<>(new ContextsHandler<>());
        assertNotNull(contextFacade.getSystemInfo(), "System info should not be null");
    }

    @Test
    public void testUserInputContextualAdviserIface() {
        UserInputContextualAdviserIface<String> userInputAdviser = new ContextFacadeIfaceImpl<>(new ContextsHandler<>());
        assertNotNull(userInputAdviser.processUserInput("test input"), "Processed user input should not be null");
    }

    @Test
    public void testAIOutputContextualAdviserIface() {
        AIOutputContextualAdviserIface<String> aiOutputAdviser = new ContextFacadeIfaceImpl<>(new ContextsHandler<>());
        assertNotNull(aiOutputAdviser.processAIOutput("test output"), "Processed AI output should not be null");
    }

    @Test
    public void testDataHandlerContextualAdviserIface() {
        DataHandlerContextualAdviserIface<String> dataHandlerAdviser = new ContextFacadeIfaceImpl<>(new ContextsHandler<>());
        assertNotNull(dataHandlerAdviser.handleData("test data"), "Handled data should not be null");
    }

    @Test
    public void testContextualAdviserIface() {
        ContextualAdviserIface<String> contextualAdviser = new ContextFacadeIfaceImpl<>(new ContextsHandler<>());
        assertNotNull(contextualAdviser.updateContext("user request", "AI response"), "Updated context should not be null");
    }
}

package org.dacss.projectinitai.messages;

/**
 * <h1>{@link Event}</h1>
 * This class represents an event that can be sent from the frontend.
 */
public class Event {
    private final MessageAction action;
    private String user;//todo: implement a user module
    private final Object payload;

    /**
     * <h3>{@link #Event(MessageAction, String, Object)}</h3>
     * Private constructor for Event.
     *
     * @param action The action associated with the event.
     * @param user The user associated with the event.
     * @param payload The payload of the event.
     */
    private Event(MessageAction action, String user, Object payload) {
        this.action = action;
        this.user = user;
        this.payload = payload;
    }

    /**
     * <h3>{@link #action(MessageAction)}</h3>
     * Creates a new EventBuilder with the specified action.
     *
     * @param action The action associated with the event.
     * @return A new EventBuilder instance.
     */
    public static EventBuilder action(MessageAction action) {
        return new EventBuilder(action);
    }

    /**
     * <h3>{@link #getUser()}</h3>
     *
     * @return The user associated with the event.
     */
    public String getUser() {
        return user;
    }

    /**
     * <h3>{@link EventBuilder}</h3>
     * Builder class for creating Event instances.
     */
    public static class EventBuilder {
        private final MessageAction action;
        private String user;
        private Object payload;

        /**
         * <h3>{@link #EventBuilder(MessageAction)}</h3>
         *
         * @param action The action associated with the event.
         */
        public EventBuilder(MessageAction action) {
            this.action = action;
        }

        /**
         * <h3>{@link #user(String)}</h3>
         * Sets the user for the event.
         *
         * @param user The user associated with the event.
         * @return The current EventBuilder instance.
         */
        public EventBuilder user(String user) {
            this.user = user;
            return this;
        }

        /**
         * <h3>{@link #withPayload(Object)}</h3>
         * Sets the payload for the event.
         *
         * @param payload The payload of the event.
         * @return The current EventBuilder instance.
         */
        public EventBuilder withPayload(Object payload) {
            this.payload = payload;
            return this;
        }

        /**
         * <h3>{@link #build()}</h3>
         * Builds and returns a new Event instance.
         *
         * @return A new Event instance.
         */
        public Event build() {
            return new Event(action, user, payload);
        }
    }
}
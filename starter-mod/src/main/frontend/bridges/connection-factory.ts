import axios, { AxiosInstance, Method } from "axios";
import { Observable, from, Subject } from "rxjs";
import { switchMap } from "rxjs/operators";
import { RSocketClient, JsonSerializer, IdentitySerializer } from "rsocket-core";
import RSocketWebSocketClient from "rsocket-websocket-client";

/**
 * Create an RSocket client to interact with the backend.
 */
const rSocketClient = new RSocketClient({
  serializers: {
    data: JsonSerializer, // Serialize data as JSON
    metadata: IdentitySerializer, // Serialize metadata as plain text
  },
  setup: {
    keepAlive: 60000, // Keep-alive interval in milliseconds
    lifetime: 180000, // Lifetime of the connection
    dataMimeType: "application/json", // Data mime type for JSON
    metadataMimeType: "message/x.rsocket.routing.v0", // Metadata for routing RSocket requests
  },
  transport: new RSocketWebSocketClient({
    url: "ws://localhost:7000", // RSocket server WebSocket URL
  }),
});

/**
 * Create a connection client for a dynamic service-method-action structure.
 *
 * @param config Configuration object containing the API prefix.
 * @returns An object with `call` and `rsocketCall` methods to interact with the backend.
 */
export function createConnectionClient(config: { prefix: string }) {
  const instance: AxiosInstance = axios.create({
    baseURL: config.prefix,
    withCredentials: true, // Support for cookies if needed
    headers: {
      "Content-Type": "application/json",
    },
  });

  /**
   * Fetch the service name from the BridgeRegistry.
   *
   * @param bridgeName The name of the bridge.
   * @returns The service name associated with the bridge.
   */
  async function fetchServiceName(bridgeName: string): Promise<string> {
    const response = await instance.get(`/bridgeRegistry/${bridgeName}`);
    return response.data.serviceName;
  }

  /**
   * Call the backend service endpoint dynamically via HTTP.
   *
   * @param bridgeName Name of the bridge (e.g., MessageBridge).
   * @param method The method on the service to invoke (e.g., processMessages).
   * @param params Object containing request data (either query params or body).
   * @param httpMethod HTTP method to use (e.g., GET, POST).
   * @returns An RxJS Observable that emits the API call's response.
   */
  function call<T = any>(
    bridgeName: string,
    method: string,
    params?: any,
    httpMethod: Method = "POST"
  ): Observable<T> {
    return from(fetchServiceName(bridgeName)).pipe(
      switchMap((serviceName: string) => {
        const url = `/${serviceName}/${method}`;

        const request = instance.request<T>({
          url,
          method: httpMethod,
          data: httpMethod === "POST" ? params : undefined,
          params: httpMethod !== "POST" ? params : undefined,
        });

        // Convert the Promise-based Axios request to an Observable
        return from(request.then((response) => response.data));
      })
    );
  }

  /**
   * Call the backend service using RSocket. Supports request-response and request-stream interactions.
   *
   * @param route The RSocket route (e.g., "user.request").
   * @param data The payload to send to the RSocket route.
   * @param isStream Whether the call expects a stream of responses (true) or a single response (false).
   * @returns An RxJS Observable that emits the response(s) from the RSocket server.
   */
  function rsocketCall<T = any>(
    route: string,
    data: any,
    isStream: boolean = false
  ): Observable<T> {
    const subject = new Subject<T>();

    // Establish connection and send request
    rSocketClient.connect().subscribe({
      onComplete: (rsocket) => {
        const metadata = String.fromCharCode(route.length) + route; // Encode RSocket route in metadata

        if (isStream) {
          // Handle a request-stream interaction
          rsocket.requestStream({ data, metadata }).subscribe({
            onNext: (payload) => subject.next(payload.data),
            onComplete: () => subject.complete(),
            onError: (error) => subject.error(error),
          });
        } else {
          // Handle a request-response interaction
          rsocket.requestResponse({ data, metadata }).subscribe({
            onComplete: (payload) => {
              subject.next(payload.data);
              subject.complete();
            },
            onError: (error) => subject.error(error),
          });
        }
      },
      onError: (error) => subject.error(error),
    });

    return subject.asObservable();
  }

  // Return a client object that exposes both `call` and `rsocketCall` methods.
  return { call, rsocketCall };
}

/**
 * Create and export the default connection client instance.
 */
const client = createConnectionClient({ prefix: "connect" });
export default client;

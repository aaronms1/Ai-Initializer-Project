import { from, Observable } from "rxjs";
import { map } from "rxjs/operators";
import client from "./connection-factory";
import { MetricsTypes } from '../enums/metrics-types';

/**
 * <h1>{@link MetricsBridge}</h1>
 */
export const MetricsBridge = (action: MetricsTypes): Observable<any> => {
  return from(
    client.call(
      "MetricsBridge",
      "processMetrics",
      { action })
  ).pipe(map(response => response));
};

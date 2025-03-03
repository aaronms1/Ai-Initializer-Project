import { from, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import client from './connection-factory';
import { ModelActions } from '../enums/model-actions';

/**
 * <h1>{@link ModelsBridge}</h1>
 * @param action
 * @constructor
 */
export const ModelsBridge = (action: ModelActions): Observable<any> => {
  return from(
    client.call("ModelsBridge", 'processModels', { action })
  ).pipe(map(response => response));
}

import { from, Observable } from "rxjs";
import { map } from "rxjs/operators";
import client from "./connection-factory";
import { SnapshotsActions } from '../enums/snapshots-actions';

/**
 * <h1>{@link SnapshotsBridge}</h1>
 */
export const SnapshotsBridge = (action: SnapshotsActions): Observable<any> => {
  return from(
    client.call("SnapshotsBridge", "processSnapshots", { action })
  ).pipe(map(response => response));
}

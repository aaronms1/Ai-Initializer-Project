import { from, Observable } from "rxjs";
import { map } from "rxjs/operators";
import client from "./connection-factory";
import { ChecksumActions } from '../enums/checksum-actions';

/**
 * <h1>{@link ChecksumsBridge}</h1>
 */
export const ChecksumsBridge = (action: ChecksumActions, filePath: string): Observable<any> => {
  return from(
    client.call(
      "ChecksumsBridge",
      "calculateChecksum",
      { action, filePath }
    )
  ).pipe(map(response => response));
};

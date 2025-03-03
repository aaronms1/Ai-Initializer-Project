import { from, Observable } from "rxjs";
import { map } from "rxjs/operators";
import client from "./connection-factory";
import { DownloadActions } from '../enums/download-actions';

/**
 * <h1>{@link SearchModelsBridge}</h1>
 */
export const SearchModelsBridge
  = (action: DownloadActions): Observable<any> => {
  return from(
    client.call(
      "SearchModelsBridge",
      "download",
      { action })
  ).pipe(map(response => response));
};

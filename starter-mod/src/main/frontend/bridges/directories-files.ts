import { from, Observable } from "rxjs";
import { map } from "rxjs/operators";
import client from "./connection-factory";
import { DirectoryActions } from "../enums/directory-actions";

/**
 * <h1>{@link DirectoriesFilesBridge}</h1>
 */
export const DirectoriesFilesBridge = (action: DirectoryActions, path: string, fileName: string): Observable<any> => {
  return from(
    client.call("DirectoriesFilesBridge", "processDirFile", { action, path, fileName })
  ).pipe(map(response => response));
};

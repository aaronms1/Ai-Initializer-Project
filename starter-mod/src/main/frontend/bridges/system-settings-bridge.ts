import { from, Observable } from 'rxjs';
import client from "./connection-factory";
import { SystemSettingsOptions } from '../enums/system-settings-options';
import { map } from 'rxjs/operators';

/**
 * <h1>{@link SystemSettingsBridge}</h1>
 * @param option
 * @constructor
 */
export const SystemSettingsBridge = (option: SystemSettingsOptions): Observable<any> => {
  return from(
    client.call("SystemSettingsBridge", "processSettings", { option })
  ).pipe(map(response => response));
};

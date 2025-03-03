import { from, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import client  from './connection-factory';

export class ContentGalleryBridge {
  //todo: implement enums for actions, here as well as the backend
  static getGallery(): Observable<any> {
    return from(
      client.call('ContentGalleryBridge', 'getGallery', {})
    ).pipe(map(response => response));
  }
}

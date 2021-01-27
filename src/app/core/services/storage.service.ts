import { Injectable } from '@angular/core';
import { LocalUser } from 'src/app/shared/models/local-user.model';
import { STORAGE_KEYS } from '../config/storage-keys.config';

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  constructor() { }

  public getUser(): LocalUser {
    let user: string = localStorage.getItem(STORAGE_KEYS.localUser) as any;

    if (!user)
      return null as any;
    
    return JSON.parse(user);
  }

  public setLocalUser(localUser: LocalUser) {
    if (!localUser)
      localStorage.removeItem(STORAGE_KEYS.localUser);
    else
      localStorage.setItem(STORAGE_KEYS.localUser, JSON.stringify(localUser));
  }
}

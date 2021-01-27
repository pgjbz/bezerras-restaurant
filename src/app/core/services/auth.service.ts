import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { StorageService } from './storage.service';
import { environment } from '../../../environments/environment';
import { Credential } from 'src/app/shared/models/credential.model';

import { Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient, private storageService: StorageService) { }

  public authenticate(credential: Credential): Observable<any> {
    return this.http.post(`${environment.baseUrl}/login`,
      credential,
      {
        observe: 'response',
        'responseType': 'text'
      });
  }
}

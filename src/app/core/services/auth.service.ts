import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { StorageService } from './storage.service';
import { environment } from '../../../environments/environment';
import { Credential } from 'src/app/shared/models/credential.model';

import jwt_decode from "jwt-decode";

import { Observable } from "rxjs";
import { LocalUser } from 'src/app/shared/models/local-user.model';

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

  public successfullLogin(authorizationValue: string): void {
    let token: string = authorizationValue.substring(7);
    let decodedToken: any = this.decodeToken(token);
    let username: string = decodedToken.sub;
    let id: number = decodedToken.id;
    let user: LocalUser = new LocalUser(username, token, id);
    this.storageService.setLocalUser(user);
  }

  private decodeToken(token: string): any {
    try {
      return jwt_decode(token);
    } catch(Error){
      return null;
    }
  }

}

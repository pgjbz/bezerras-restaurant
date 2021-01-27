export class LocalUser {
  
  constructor(private _user: string, private _token: string) {
  }

  public get user(): string {
    return this._user;
  }

  public set user(value: string) {
    this._user = value;
  }

  public get token(): string {
    return this._token;
  }

  public set token(value: string) {
    this._token = value;
  }

}
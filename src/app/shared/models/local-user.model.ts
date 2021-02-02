export class LocalUser {
  
  constructor(private _user: string, private _token: string, private _id: number) {
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

  public get id() {
    return this._id;
  }

  public set id(value: number) {
    this._id = value;
  }

}
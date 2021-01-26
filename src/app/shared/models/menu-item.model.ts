export class MenuItem {

  constructor(private _name: string, private _path: string) { }
  
  public get name(): string { return this._name; }

  public set name(value: string) { this._name = value; }

  public get path(): string { return this._path; }

  public set path(value: string) { this._path = value;}

}
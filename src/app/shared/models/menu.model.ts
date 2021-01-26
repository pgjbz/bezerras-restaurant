import { MenuItem } from "./menu-item.model";

export class Menu {

  constructor(private _menuItems: MenuItem[], private _path: string = '', private _name: string = '') {
  }

  public get menuItems(): MenuItem[] {
    return this._menuItems;
  }
  
  public addMenuItem(item: MenuItem): void{
    this._menuItems.push(item);
  }

  public get path(): string {
    return this._path;
  }

  public set path(value: string) {
    this._path = value;
  }

  public get name(): string {
    return this._name;
  }
  
  public set name(value: string) {
    this._name = value;
  }

}
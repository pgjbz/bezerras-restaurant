import { Component, OnInit } from '@angular/core';
import { MenuItem } from 'src/app/shared/models/menu-item.model';
import { Menu } from 'src/app/shared/models/menu.model';


@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  menus: Menu[] = [];

  constructor() { }

  ngOnInit(): void {
    for (let i: number = 0; i < 10; i++) { 
      console.log('Add menu')
      let menu: Menu = this.menus[i] = new Menu([], 'https://www.google.com', `Menu ${i}`);
      console.log(menu)
      if (i % 2 === 0) {
        for (let j: number = 0; j < 2; j++) {
          console.log('Add menu item');
          menu.addMenuItem(new MenuItem(`Menu item ${j}`, 'https://www.google.com.br'));
        }
      }
      console.log(menu)
    }
  }

}

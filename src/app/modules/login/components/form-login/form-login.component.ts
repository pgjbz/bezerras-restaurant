import { Component, OnInit } from '@angular/core';
import { Credential } from 'src/app/shared/models/credential.model';

@Component({
  selector: 'app-form-login',
  templateUrl: './form-login.component.html',
  styleUrls: ['./form-login.component.css']
})
export class FormLoginComponent implements OnInit {

  credential: Credential = new Credential('', '');

  constructor() { }

  onSubmit(e: Event) {
    console.log(e);
    console.log(this.credential);
  }

  ngOnInit(): void {
  }

}

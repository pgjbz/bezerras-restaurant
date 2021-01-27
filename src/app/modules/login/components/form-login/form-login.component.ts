import { Component, OnInit } from '@angular/core';
import { Credential } from 'src/app/shared/models/credential.model';
import { AuthService } from 'src/app/core/services/auth.service';

@Component({
  selector: 'app-form-login',
  templateUrl: './form-login.component.html',
  styleUrls: ['./form-login.component.css']
})
export class FormLoginComponent implements OnInit {

  credential: Credential = {username: '', password: ''};

  constructor(private auth: AuthService) { }

  onSubmit(e: Event) {
    this.auth.authenticate(this.credential).subscribe(response => console.log(response), error => console.log(error));
  }

  ngOnInit(): void {
  }

}

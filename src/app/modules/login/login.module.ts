import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormLoginComponent } from './components/form-login/form-login.component';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [FormLoginComponent],
  imports: [
    CommonModule,
    FormsModule
  ]
})
export class LoginModule { }

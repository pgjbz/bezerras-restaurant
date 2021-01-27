import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormLoginComponent } from './components/form-login/form-login.component';
import { FormsModule } from '@angular/forms';
import { PageLoginComponent } from './pages/page-login/page-login.component';

@NgModule({
  declarations: [FormLoginComponent, PageLoginComponent],
  imports: [
    CommonModule,
    FormsModule
  ]
})
export class LoginModule { }

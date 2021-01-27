import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { FormLoginComponent } from './modules/login/components/form-login/form-login.component';
import { PageLoginComponent } from './modules/login/pages/page-login/page-login.component';

@NgModule({
  declarations: [
    AppComponent,
    PageLoginComponent,
    FormLoginComponent
  ],
  imports: [
    BrowserModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

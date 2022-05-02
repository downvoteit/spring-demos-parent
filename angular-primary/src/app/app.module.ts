import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import {CreateItemComponent} from './create-item/create-item.component';
import {GetItemComponent} from './get-item/get-item.component';
import {GetItemsComponent} from './get-items/get-items.component';

@NgModule({
  declarations: [
    AppComponent,
    CreateItemComponent,
    GetItemComponent,
    GetItemsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}

import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {GetItemsCategoryComponent} from "./get-items-category/get-items-category.component";
import {AppComponent} from "./app.component";

const routes: Routes = [
  {path: '', component: GetItemsCategoryComponent},
  {path: 'browse', component: GetItemsCategoryComponent},
  {
    path: 'ledger',
    component: AppComponent,
    resolve: {url: 'externalUrlRedirectResolver'},
    data: {externalUrl: 'http://localhost:7002'}
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

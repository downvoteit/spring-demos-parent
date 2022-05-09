import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {GetItemsCategoryComponent} from "./get-items-category/get-items-category.component";

const routes: Routes = [
  {path: '', component: GetItemsCategoryComponent},
  {path: 'browse', component: GetItemsCategoryComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

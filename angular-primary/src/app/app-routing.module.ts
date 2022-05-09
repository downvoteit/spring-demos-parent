import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {CreateItemComponent} from "./create-item/create-item.component";
import {GetItemComponent} from "./get-item/get-item.component";
import {GetItemsComponent} from "./get-items/get-items.component";

const routes: Routes = [
  {path: '', component: GetItemsComponent},
  {path: 'add', component: CreateItemComponent},
  {path: 'find', component: GetItemComponent},
  {path: 'browse', component: GetItemsComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

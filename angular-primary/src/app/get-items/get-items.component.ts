import {Component, OnDestroy, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Subscription} from "rxjs";
import {headers, ItemRequest} from "../app.types";
import {environment} from "../../environments/environment";

@Component({
  selector: 'app-get-items',
  template: `
    <div class="plain-tbl">
      <div>
        <h2>Browse all items</h2>
      </div>
      <div>
        <table aria-describedby="items">
          <thead>
          <tr>
            <th id="name">Name</th>
            <th id="amount">Amount</th>
            <th id="price">Price</th>
            <th id="action">&nbsp;</th>
          </tr>
          </thead>
          <tbody>
          <ng-container *ngIf="loaded; else noData">
            <tr *ngFor="let i of data">
              <td>{{ i.name }}</td>
              <td class="plain-tbl-right">{{ i.amount }}</td>
              <td class="plain-tbl-right">{{ i.price }}</td>
              <td class="plain-tbl-center">
                <button (click)="deleteItem()">Delete</button>
              </td>
            </tr>
          </ng-container>
          <ng-template #noData>
            <tr>
              <td>&nbsp;</td>
              <td>No data found</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
            </tr>
          </ng-template>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: []
})
export class GetItemsComponent implements OnInit, OnDestroy {
  data: ItemRequest[] = [];
  loaded = false;
  subscriptions: Subscription[] = [];

  constructor(private http: HttpClient) {
  }

  ngOnInit(): void {
    this.getItems();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  getItems() {
    try {

      const subscription = this.http
        .get<ItemRequest[]>(`${environment.baseUrl}/items/all`, {headers: headers})
        .subscribe(response => {
          this.data = response;
          this.loaded = true;
        });

      this.subscriptions.push(subscription);
    } catch (e) {
      console.error(e);
    }
  }

  deleteItem() {
    console.log('works');
  }
}

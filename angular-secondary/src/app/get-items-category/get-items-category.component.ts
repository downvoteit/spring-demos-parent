import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {AppHttpHeaders, ItemsCategoryReq, PagedReq, PageLimits} from "../app.types";

@Component({
  selector: 'app-get-items-category',
  template: `
    <div class="plain-tbl">
      <div id="items">
        <h2>Browse all items by category</h2>
      </div>
      <div>
        <table aria-describedby="items">
          <thead>
          <tr>
            <th id="name">#</th>
            <th id="name">Name</th>
            <th id="amount">Amount</th>
            <th id="price">Price</th>
          </tr>
          </thead>
          <tbody>
          <ng-container *ngIf="responseGet.length > 0; else noData">
            <tr *ngFor="let item of responseGet; let i = index">
              <td class="plain-tbl-center">{{ (i + 1) + request.page }}</td>
              <td>{{ item.name }}</td>
              <td class="plain-tbl-right">{{ item.amount  }}</td>
              <td class="plain-tbl-right">{{ item.price | number: '1.2-2' }}</td>
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
      <div>
        <div class="plain-tbl-pagination">
          <label for="limit">Limit</label>
          <select id="limit" (change)="setLimit(limit.value)" #limit>
            <option *ngFor="let item of limits; let i = index" [value]="item" [selected]="item == 10">{{ item }}</option>
          </select>
          <button (click)="previousPage()">Previous page</button>
          <button (click)="nextPage()">Next page</button>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class GetItemsCategoryComponent implements OnInit, OnDestroy {
  subscriptions: Subscription[] = [];
  limits = PageLimits;
  request: PagedReq = {page: 0, limit: 10};
  responseGet: ItemsCategoryReq[] = [];

  constructor(private http: HttpClient) {
  }

  ngOnInit(): void {
    this.getItems().then();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  async getItems() {
    try {
      const url = `${environment.baseUrl}/paged?page=${this.request.page}&limit=${this.request.limit}`;
      this.responseGet = <ItemsCategoryReq[]>await this.http
        .get<ItemsCategoryReq[]>(url, {headers: AppHttpHeaders})
        .toPromise();
    } catch (e) {
      this.responseGet = [];

      console.error(e);
    }
  }

  setLimit(limit: string) {
    this.request.page = 0;
    this.request.limit = Number(limit);
    this.getItems().then();
  }

  previousPage() {
    const page = this.request.page - this.request.limit;
    if (page < 0) {
      return;
    }
    this.request.page = page;
    this.getItems().then();
  }

  nextPage() {
    this.request.page = this.request.page + this.request.limit;
    this.getItems().then();
  }
}

import {Component, OnDestroy, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Subscription} from "rxjs";
import {AppHttpHeaders, CategoryArray, ItemReq, PagedReq, PageLimits, ResDto} from "../app.types";
import {environment} from "../../environments/environment";

@Component({
  selector: 'app-get-items',
  template: `
    <div class="plain-tbl">
      <div id="items">
        <h2>Browse all items</h2>
      </div>
      <div>
        <table aria-describedby="items">
          <thead>
          <tr>
            <th id="name">#</th>
            <th id="name">Category</th>
            <th id="name">Name</th>
            <th id="amount">Amount</th>
            <th id="price">Price</th>
            <th id="action">&nbsp;</th>
          </tr>
          </thead>
          <tbody>
          <ng-container *ngIf="responseGet.length > 0; else noData">
            <tr *ngFor="let item of responseGet; let i = index">
              <td class="plain-tbl-center">{{ (i + 1) + request.page }}</td>
              <td>{{ mapCategoryIdToEnum(item) }}</td>
              <td>{{ item.name }}</td>
              <td class="plain-tbl-right">{{ item.amount }}</td>
              <td class="plain-tbl-right">{{ item.price }}</td>
              <td class="plain-tbl-center">
                <button (click)="deleteItem(item.id)">Delete row</button>
              </td>
            </tr>
          </ng-container>
          <ng-template #noData>
            <tr>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>No data found</td>
              <td>&nbsp;</td>
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
        <div *ngIf="responseDelete" id="response">{{ responseDelete.message }}</div>
      </div>
    </div>
  `,
  styles: []
})
export class GetItemsComponent implements OnInit, OnDestroy {
  subscriptions: Subscription[] = [];
  limits = PageLimits;
  request: PagedReq = {page: 0, limit: 10};
  responseGet: ItemReq[] = [];
  responseDelete: ResDto | undefined;

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
      this.responseGet = <ItemReq[]>await this.http
        .get<ItemReq[]>(url, {headers: AppHttpHeaders})
        .toPromise();
    } catch (e) {
      this.responseGet = [];

      console.error(e);
    }
  }

  async deleteItem(id: number) {
    try {
      const url = `${environment.baseUrl}/${id}`;
      const response = <ResDto>await this.http
        .delete<ResDto>(url, {headers: AppHttpHeaders})
        .toPromise();

      await this.getItems().then();

      this.responseDelete = response;
    } catch (e) {
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

  mapCategoryIdToEnum(item: ItemReq) {
    return CategoryArray[item.categoryId - 1];
  }
}

import {Component, OnDestroy, OnInit} from '@angular/core';
import {AppHttpHeaders, CategoriesEnum, CategoryArray, ItemRequest, ItemRequestDefault} from "../app.types";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {Subscription} from "rxjs";
import {environment} from "../../environments/environment";
import {ValidationService} from "../validation/validation.service";

@Component({
  selector: 'app-get-item',
  template: `
    <form (ngSubmit)="onSubmit()" [formGroup]="form">
      <div>
        <h2>Find an item</h2>
      </div>
      <div>
        <label for="categoryId">Category</label>
        <select formControlName="categoryId" id="categoryId">
          <option *ngFor="let item of categories; let i = index" [value]="(i + 1)">{{ item }}</option>
        </select>
      </div>
      <div>
        <label for="name">Name</label>
        <input minlength="3" maxlength="20" required
               formControlName="name"
               id="name"
               placeholder="Name"
               type="text">
      </div>
      <div>
        <label for="amount">Amount</label>
        <input minlength="1" maxlength="12"
               class="plain-inp-val-right"
               formControlName="amount"
               id="amount"
               placeholder="Amount"
               type="text">
      </div>
      <div>
        <label for="price">Price</label>
        <input minlength="1" maxlength="12"
               class="plain-inp-val-right"
               formControlName="price"
               id="price"
               placeholder="Price"
               type="text">
      </div>
      <div class="plain-btn">
        <button [disabled]="form.invalid" type="submit">Find</button>
      </div>
      <div *ngIf="response" id="response">{{ response }}</div>
    </form>
  `,
  styles: []
})
export class GetItemComponent implements OnInit, OnDestroy {
  subscriptions: Subscription[] = [];
  categories: CategoriesEnum[] = CategoryArray;
  response: string = '';
  form: FormGroup;

  constructor(private builder: FormBuilder, private http: HttpClient) {
    this.form = this.builder.group({
      id: [{value: '', disabled: true}],
      categoryId: [{value: '', disabled: true}],
      name: [{value: '', disabled: false}, [Validators.compose([Validators.required, ValidationService.validateNameAlphaNumeric])]],
      amount: [{value: '', disabled: true}],
      price: [{value: '', disabled: true}],
    });

    this.form.patchValue(ItemRequestDefault);
  }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((s) => s.unsubscribe());
  }

  onSubmit() {
    this.getItems();
  }

  getItems() {
    const name = this.form.get('name')?.value;

    this.response = 'Searching...';

    const subscription = this.http
      .get<ItemRequest>(`${environment.baseUrl}/items/${name}`, {headers: AppHttpHeaders})
      .subscribe(response => {
        this.form.patchValue(response);
        this.response = 'Found';
      });

    this.subscriptions.push(subscription);
  }
}

import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, Validators} from "@angular/forms";
import {ValidationService} from "../validation/validation.service";
import {HttpClient} from "@angular/common/http";
import {interval, Subscription} from "rxjs";
import {CategoriesEnum, ItemRequest, ItemResponse} from "../app.types";
import {environment} from "../../environments/environment";

@Component({
  selector: 'app-create-item',
  template: `
    <form (ngSubmit)="onSubmit()" [formGroup]="form">
      <div>
        <h2>Add new item</h2>
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
        <input minlength="1" maxlength="12" required
               class="plain-inp-val-right"
               formControlName="amount"
               id="amount"
               placeholder="Amount"
               type="text">
      </div>
      <div>
        <label for="price">Price</label>
        <input minlength="1" maxlength="12" required
               class="plain-inp-val-right"
               formControlName="price"
               id="price"
               placeholder="Price"
               type="text">
      </div>
      <div class="plain-btn">
        <button [disabled]="form.invalid" type="submit">Add</button>
      </div>
      <div *ngIf="response" id="response">{{ response.message }}</div>
    </form>
  `,
  styles: []
})
export class CreateItemComponent implements OnInit, OnDestroy {
  subscriptions: Subscription[] = [];
  categories: CategoriesEnum[] = Object.values(CategoriesEnum);
  default: ItemRequest = {id: 0, categoryId: 1, name: '', amount: 0, price: 0.0};
  response: ItemResponse | undefined;
  form = this.builder.group({
    id: [{value: this.default.id, disabled: false}],
    categoryId: [{value: this.default.categoryId, disabled: false}, [Validators.required]],
    name: [{value: this.default.name, disabled: false}, [Validators.compose([Validators.required, ValidationService.validateNameAlphaNumeric])]],
    amount: [{value: this.default.amount, disabled: false}, [Validators.compose([Validators.required, ValidationService.validateNumericAndGteZero])]],
    price: [{value: this.default.price, disabled: false}, [Validators.compose([Validators.required, ValidationService.validateNumericAndGteZero])]],
  });

  constructor(private builder: FormBuilder, private http: HttpClient) {
  }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((s) => s.unsubscribe());
  }

  async onSubmit() {
    try {
      this.form.disable();

      const request: ItemRequest = this.form.value;

      this.response = await this.http
        .post<ItemResponse>(`${environment.baseUrl}/items/queue`, request)
        .toPromise();

      let subscription = interval(3000).subscribe(() => this.response = undefined);
      this.subscriptions.push(subscription);

      this.form.reset();
      this.form.patchValue(this.default);
    } catch (e) {
      console.error(e);
    } finally {
      this.form.enable();
    }
  }
}

import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ValidationService} from "../validation/validation.service";
import {HttpClient} from "@angular/common/http";
import {interval, Subscription} from "rxjs";
import {CategoriesEnum, CategoryArray, ItemRequest, ItemRequestDefault, ItemResponse, ItemResponseDefault} from "../app.types";
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
  categories: CategoriesEnum[] = CategoryArray;
  response: ItemResponse = ItemResponseDefault;
  form: FormGroup;

  constructor(private builder: FormBuilder, private http: HttpClient) {
    this.form = this.builder.group({
      id: [{value: '', disabled: false}],
      categoryId: [{value: '', disabled: false}, [Validators.required]],
      name: [{value: '', disabled: false}, [Validators.compose([Validators.required, ValidationService.validateNameAlphaNumeric])]],
      amount: [{value: '', disabled: false}, [Validators.compose([Validators.required, ValidationService.validateNumericAndGteZero])]],
      price: [{value: '', disabled: false}, [Validators.compose([Validators.required, ValidationService.validateNumericAndGteZero])]],
    });

    this.form.patchValue(ItemRequestDefault);
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

      this.response = <ItemResponse>await this.http
        .post<ItemResponse>(`${environment.baseUrl}/items/queue`, request)
        .toPromise();

      let subscription = interval(3000).subscribe(() => this.response = ItemResponseDefault);
      this.subscriptions.push(subscription);

      this.form.reset();
      this.form.patchValue(ItemRequestDefault);
    } catch (e) {
      console.error(e);
    } finally {
      this.form.enable();
    }
  }
}

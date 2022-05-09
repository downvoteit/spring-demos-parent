import {AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {AppHttpHeaders, CategoryArray, CategoryEnum, ItemReq, ItemRequestDefault} from "../app.types";
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
        <select formControlName="categoryId" id="categoryId" #categoryId>
          <option *ngFor="let item of categories; let i = index" [value]="(i + 1)">{{ item }}</option>
        </select>
      </div>
      <div>
        <label for="name">Name</label>
        <input minlength="3" maxlength="30" required #name
               formControlName="name"
               id="name"
               placeholder="Please enter a Name"
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
        <button [disabled]="form.invalid" type="submit" #submit>Find</button>
      </div>
      <div *ngIf="response" id="response">{{ response }}</div>
    </form>
  `,
  styles: []
})
export class GetItemComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('categoryId') categoryId!: ElementRef;
  @ViewChild('name') name!: ElementRef;
  @ViewChild('submit') submit!: ElementRef;
  subscriptions: Subscription[] = [];
  categories: CategoryEnum[] = CategoryArray;
  response: string = '';
  form: FormGroup;

  constructor(private builder: FormBuilder, private http: HttpClient) {
    this.form = this.builder.group({
      id: [{value: '', disabled: true}],
      categoryId: [{value: '', disabled: false}],
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

  ngAfterViewInit(): void {
    this.name.nativeElement.focus();
  }

  onSubmit() {
    this.getItem().then();
  }

  async getItem() {
    const itemCategoryId = Number(this.form.get('categoryId')?.value);
    const itemName = this.form.get('name')?.value;

    try {
      this.response = `Searching for an item with category ${CategoryArray[itemCategoryId]} name ${itemName}...`;
      this.categoryId.nativeElement.disabled = true;
      this.name.nativeElement.disabled = true;
      this.submit.nativeElement.disabled = true;

      const url = `${environment.baseUrl}/row/${itemCategoryId}/${itemName}`;
      const response = <ItemReq>await this.http
        .get<ItemReq>(url, {headers: AppHttpHeaders})
        .toPromise();

      this.response = 'Search successful, item is found';

      this.form.reset();
      this.form.patchValue(response);
    } catch (e) {
      this.response = 'Search unsuccessful, item cannot be found';
      this.form.patchValue({id: 0, categoryId: 1, name: itemName, amount: 0, price: 0.0});

      console.error(e);
    } finally {
      this.categoryId.nativeElement.disabled = false;
      this.name.nativeElement.disabled = false;
      this.submit.nativeElement.disabled = false;
      this.name.nativeElement.focus();
    }
  }
}

import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, Validators} from "@angular/forms";
import {HttpClient} from "@angular/common/http";
import {ValidationService} from "../validation/validation.service";
import {interval, Subscription} from "rxjs";

interface ItemRequest {
  id: number;
  categoryId: number;
  name: string;
  amount: number;
  price: number;
}

interface ItemResponse {
  id: number;
  message: string;
}

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit, OnDestroy {
  url = 'http://localhost:7003/items/topic';
  subscriptions: Subscription[] = [];
  default: ItemRequest = {id: 0, categoryId: 1, name: '', amount: 0, price: 0.0};
  validation = {
    name: {required: true, min: 3, max: 10},
    amount: {required: true, min: 1, max: 6},
    price: {required: true, min: 1, max: 12}
  };
  response: ItemResponse | undefined;

  constructor(private builder: FormBuilder, private http: HttpClient) {
  }

  form = this.builder.group({
    id: [this.default.id],
    categoryId: [this.default.categoryId, Validators.required],
    name: [this.default.name, Validators.compose([
      this.validation.name.required ? Validators.required : null,
      Validators.minLength(this.validation.name.min),
      Validators.maxLength(this.validation.name.max),
      ValidationService.validateNameAlphaNumeric
    ])],
    amount: [this.default.amount, Validators.compose([
      this.validation.amount.required ? Validators.required : null,
      Validators.minLength(this.validation.amount.min),
      Validators.maxLength(this.validation.amount.max),
      ValidationService.validateNumericAndGteZero,
    ])],
    price: [this.default.price, Validators.compose([
      this.validation.price.required ? Validators.required : null,
      Validators.minLength(this.validation.price.min),
      Validators.maxLength(this.validation.price.max),
      ValidationService.validateNumericAndGteZero,
    ])],
  })

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
        .post<ItemResponse>(this.url, request)
        .toPromise();

      let subscription = interval(3000).subscribe(() => this.response = undefined);
      this.subscriptions.push(subscription);

      this.form.patchValue(this.default);
    } catch (e) {
      console.error(e);
    } finally {
      this.form.enable();
    }
  }

}

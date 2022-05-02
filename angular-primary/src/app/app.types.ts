import {HttpHeaders} from "@angular/common/http";

export const headers = new HttpHeaders()
  .set('Content-Type', 'application/json')
  .set('Accept', 'application/json');

export interface ItemRequest {
  id: number;
  categoryId: number;
  name: string;
  amount: number;
  price: number;
}

export interface ItemResponse {
  id: number;
  message: string;
}

export enum CategoriesEnum {
  primary = 'Primary',
  secondary = 'Secondary',
  tertiary = 'Tertiary',
}

import {HttpHeaders} from "@angular/common/http";

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

export const AppHttpHeaders = new HttpHeaders()
  .set('Content-Type', 'application/json')
  .set('Accept', 'application/json');

export const CategoryArray: CategoriesEnum[] = Object.values(CategoriesEnum);

export const ItemRequestDefault = {id: 0, categoryId: 1, name: '', amount: 0, price: 0.0};
export const ItemResponseDefault = {id: 0, message: ''};

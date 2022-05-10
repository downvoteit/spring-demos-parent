import {HttpHeaders} from "@angular/common/http";

export const AppHttpHeaders = new HttpHeaders()
  .set('Content-Type', 'application/json')
  .set('Accept', 'application/json');

export enum CategoryEnum {
  primary = 'Primary',
  secondary = 'Secondary',
  tertiary = 'Tertiary',
}

export const CategoryArray: CategoryEnum[] = Object.values(CategoryEnum);
export const ItemRequestDefault = {id: 0, categoryId: 1, name: '', amount: 0, price: 0.0};
export const ItemResponseDefault = {id: 0, message: ''};
export const PageLimits = [5, 10, 50, 100];

export interface PagedReq {
  page: number;
  limit: number;
}

export interface ItemReq {
  id: number;
  categoryId: number;
  name: string;
  amount: number;
  price: number;
}

export interface ResDto {
  id: number;
  message: string;
}

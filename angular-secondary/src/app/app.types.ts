import {HttpHeaders} from "@angular/common/http";

export const AppHttpHeaders = new HttpHeaders()
  .set('Content-Type', 'application/json')
  .set('Accept', 'application/json');

export const PageLimits = [5, 10, 50, 100];

export interface PagedReq {
  page: number;
  limit: number;
}

export interface ItemsCategoryReq {
  id: number;
  name: string;
  amount: number;
  price: number;
}

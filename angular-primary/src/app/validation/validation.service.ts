import {Injectable} from '@angular/core';
import {AbstractControl} from "@angular/forms";

@Injectable({
  providedIn: 'root'
})
export class ValidationService {
  constructor() {
  }

  static validateLength(control: AbstractControl): { [key: string]: any } {
    const pattern = /[^a-zA-Z-_\d]+/i;
    if (pattern.test(control.value)) {
      return {notAlphaNumeric: true};
    } else {
      return {};
    }
  }

  static validateNameAlphaNumeric(control: AbstractControl): { [key: string]: any } {
    const pattern = /[^a-zA-Z-_\d]+/i;
    if (pattern.test(control.value)) {
      return {notAlphaNumeric: true};
    } else {
      return {};
    }
  }

  static validateNumericAndGteZero(control: AbstractControl): { [key: string]: any } {
    const pattern = /[\D]+/i;
    if (pattern.test(control.value)) {
      return {notNumeric: true};
    } else if (Number(control.value) <= 0) {
      return {notGte: true};
    } else {
      return {};
    }
  }

  static validateDecimalAndGteZero(control: AbstractControl): { [key: string]: any } {
    const pattern = /[^(0-9.)]+/i;
    if (pattern.test(control.value)) {
      return {notNumeric: true};
    } else if (Number(control.value) <= 0) {
      return {notGte: true};
    } else {
      return {};
    }
  }
}

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetItemsCategoryComponent } from './get-items-category.component';

describe('GetItemsCategoriesComponent', () => {
  let component: GetItemsCategoryComponent;
  let fixture: ComponentFixture<GetItemsCategoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GetItemsCategoryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GetItemsCategoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

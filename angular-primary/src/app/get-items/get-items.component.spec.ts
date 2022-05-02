import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GetItemsComponent} from './get-items.component';

describe('GetItemsComponent', () => {
  let component: GetItemsComponent;
  let fixture: ComponentFixture<GetItemsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GetItemsComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GetItemsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

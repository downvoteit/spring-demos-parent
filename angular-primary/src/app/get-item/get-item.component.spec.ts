import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GetItemComponent} from './get-item.component';

describe('GetItemComponent', () => {
  let component: GetItemComponent;
  let fixture: ComponentFixture<GetItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GetItemComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GetItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

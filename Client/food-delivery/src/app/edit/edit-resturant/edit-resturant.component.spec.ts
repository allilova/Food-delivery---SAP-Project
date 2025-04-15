import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditResturantComponent } from './edit-resturant.component';

describe('EditResturantComponent', () => {
  let component: EditResturantComponent;
  let fixture: ComponentFixture<EditResturantComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditResturantComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditResturantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

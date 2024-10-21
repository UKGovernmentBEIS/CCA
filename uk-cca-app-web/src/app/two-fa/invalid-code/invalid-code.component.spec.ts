import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { InvalidCodeComponent } from './invalid-code.component';

describe('InvalidCodeComponent', () => {
  let component: InvalidCodeComponent;
  let fixture: ComponentFixture<InvalidCodeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InvalidCodeComponent],
      declarations: [],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InvalidCodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

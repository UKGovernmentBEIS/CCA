import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { LoadingSpinnerComponent } from './loading-spinner.component';

describe('LoadingSpinnerComponent', () => {
  let component: LoadingSpinnerComponent;
  let fixture: ComponentFixture<LoadingSpinnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoadingSpinnerComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(LoadingSpinnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the spinner element', () => {
    const spinner = fixture.debugElement.query(By.css('.loading-spinner__spinner'));
    expect(spinner).not.toBeNull();
    expect(spinner.attributes['aria-hidden']).toBe('true');
  });

  it('should render with role status and aria-live polite', () => {
    const container = fixture.debugElement.query(By.css('.loading-spinner'));
    expect(container).not.toBeNull();
    expect(container.attributes['role']).toBe('status');
    expect(container.attributes['aria-live']).toBe('polite');
  });
});

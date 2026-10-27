import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { TwoFaLinkComponent } from './two-fa-link.component';

describe('TwoFaLinkComponent', () => {
  let component: TwoFaLinkComponent;
  let fixture: ComponentFixture<TwoFaLinkComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TwoFaLinkComponent],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TwoFaLinkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

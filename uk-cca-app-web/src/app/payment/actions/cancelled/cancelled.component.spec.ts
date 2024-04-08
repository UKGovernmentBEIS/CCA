import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { PaymentModule } from '../../payment.module';
import { CancelledComponent } from './cancelled.component';

describe('CancelledComponent', () => {
  let component: CancelledComponent;
  let fixture: ComponentFixture<CancelledComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, PaymentModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CancelledComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

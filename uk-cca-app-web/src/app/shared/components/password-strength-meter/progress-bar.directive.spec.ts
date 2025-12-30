import { ComponentRef } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { PasswordStrengthMeterComponent } from './password-strength-meter.component';
import { PasswordStrengthMeterService } from './password-strength-meter.service';
import { FeedbackResult } from './types';

class Service extends PasswordStrengthMeterService {
  score(_: string): number {
    return 1;
  }

  scoreWithFeedback(_: string): FeedbackResult {
    return {
      score: 1,
      feedback: {
        warning: 'warning text',
        suggestions: ['try entering a better password.'],
      },
    };
  }
}

describe('Directive: PasswordStrengthMeter - ProgressBar', () => {
  let component: PasswordStrengthMeterComponent;
  let fixture: ComponentFixture<PasswordStrengthMeterComponent>;
  let componentRef: ComponentRef<PasswordStrengthMeterComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [PasswordStrengthMeterComponent],
      providers: [
        {
          provide: Service,
          useClass: PasswordStrengthMeterService,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PasswordStrengthMeterComponent);
    component = fixture.componentInstance;
    componentRef = fixture.componentRef;
    fixture.detectChanges();
  }));

  it('should create progress bar items with default value', () => {
    fixture.detectChanges();
    expect(component.numberOfProgressBarItems()).toBe(5);
    const items = fixture.debugElement.queryAll(By.css('.psm__progress-bar-item'));

    expect(items.length).toEqual(5);
    expect(items[0].styles['width']).toEqual(`${100 / 5}%`);
  });

  it('should create progress bar items with provided value', () => {
    componentRef.setInput('numberOfProgressBarItems', 3);
    fixture.detectChanges();

    expect(component.numberOfProgressBarItems()).toBe(3);

    const items = fixture.debugElement.queryAll(By.css('.psm__progress-bar-item'));

    expect(items.length).toEqual(3);
    expect(items[0].styles['width']).toEqual(`33.333333333333336%`);
  });

  it('should update the aria attributes', () => {
    componentRef.setInput('password', '123456abc12345');
    fixture.detectChanges();

    expect(component.numberOfProgressBarItems()).toBe(5);

    const progressBarElement = fixture.debugElement.query(By.css('.psm__progress-bar')).nativeElement as HTMLDivElement;

    expect(progressBarElement.getAttribute('aria-valuenow')).toEqual('80');
    expect(progressBarElement.getAttribute('data-strength')).toEqual('3');

    const progressBarOverlay = fixture.debugElement.query(By.css('.psm__progress-bar-overlay'))
      .nativeElement as HTMLDivElement;

    expect(progressBarOverlay.style.width).toEqual('80%');
    expect(progressBarOverlay.style.backgroundColor).toEqual('rgb(0, 112, 60)');
  });
});

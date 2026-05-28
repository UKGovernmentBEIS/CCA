import { signal } from '@angular/core';
import { type ComponentFixture, TestBed } from '@angular/core/testing';

import { type Mocked } from 'vitest';

import { TimeoutBannerComponent } from './timeout-banner.component';
import { TimeoutBannerService } from './timeout-banner.service';

describe('TimeoutBannerComponent', () => {
  let component: TimeoutBannerComponent;
  let fixture: ComponentFixture<TimeoutBannerComponent>;
  let timeoutBannerServiceMock: Mocked<Pick<TimeoutBannerService, 'extendSession' | 'signOut'>> &
    Pick<TimeoutBannerService, 'isVisible' | 'timeExtensionAllowed' | 'timeOffsetSeconds'>;
  let dialogElement: HTMLDialogElement;

  const setupDialogMethods = () => {
    dialogElement = fixture.nativeElement.querySelector('dialog');
    (dialogElement as any).showModal = vi.fn(() => {
      dialogElement.setAttribute('open', '');
    });
    (dialogElement as any).close = vi.fn(() => {
      dialogElement.removeAttribute('open');
    });
  };

  beforeEach(async () => {
    timeoutBannerServiceMock = {
      isVisible: signal(false),
      timeExtensionAllowed: signal(true),
      timeOffsetSeconds: 120,
      extendSession: vi.fn(),
      signOut: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [TimeoutBannerComponent],
      providers: [{ provide: TimeoutBannerService, useValue: timeoutBannerServiceMock }],
    }).compileComponents();

    fixture = TestBed.createComponent(TimeoutBannerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    setupDialogMethods();
  });

  afterEach(() => {
    document.body.classList.remove('govuk-timeout-warning-overlay');
    vi.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should open dialog when isVisible signal is set to true', () => {
    timeoutBannerServiceMock.isVisible.set(true);
    fixture.detectChanges();
    expect(component.isDialogOpen()).toBeTruthy();
  });

  it('should close dialog when isVisible signal is set to false', () => {
    timeoutBannerServiceMock.isVisible.set(true);
    fixture.detectChanges();
    expect(component.isDialogOpen()).toBeTruthy();

    timeoutBannerServiceMock.isVisible.set(false);
    fixture.detectChanges();
    expect(component.isDialogOpen()).toBeFalsy();
  });

  it('should start with dialog closed', () => {
    expect(component.isDialogOpen()).toBeFalsy();
  });

  it('should call extendSession when continue button clicked', () => {
    timeoutBannerServiceMock.isVisible.set(true);
    fixture.detectChanges();

    const continueBtn = fixture.nativeElement.querySelector('.govuk-button:not(.govuk-button--secondary)');
    continueBtn.click();

    expect(timeoutBannerServiceMock.extendSession).toHaveBeenCalled();
  });

  it('should call signOut when sign out button clicked', () => {
    timeoutBannerServiceMock.isVisible.set(true);
    fixture.detectChanges();

    const signOutBtn = fixture.nativeElement.querySelector('.govuk-button--secondary');
    signOutBtn.click();

    expect(timeoutBannerServiceMock.signOut).toHaveBeenCalled();
  });

  it('should show extend session message when timeExtensionAllowed is true', () => {
    const textDiv = fixture.nativeElement.querySelector('[aria-relevant="additions"][aria-hidden="true"]');
    expect(textDiv.innerHTML).toContain('if you do not respond');
  });

  it('should show no-extension message when timeExtensionAllowed is false', () => {
    timeoutBannerServiceMock.timeExtensionAllowed.set(false);
    fixture.detectChanges();
    const textDiv = fixture.nativeElement.querySelector('[aria-relevant="additions"][aria-hidden="true"]');
    expect(textDiv.innerHTML).toContain('automatically signed out');
  });

  it('should add overlay class when opening dialog', () => {
    timeoutBannerServiceMock.isVisible.set(true);
    fixture.detectChanges();
    expect(document.body.classList.contains('govuk-timeout-warning-overlay')).toBeTruthy();
  });

  it('should remove overlay class when closing dialog', () => {
    timeoutBannerServiceMock.isVisible.set(true);
    fixture.detectChanges();
    timeoutBannerServiceMock.isVisible.set(false);
    fixture.detectChanges();
    expect(document.body.classList.contains('govuk-timeout-warning-overlay')).toBeFalsy();
  });

  it('should manage focus on dialog open/close', () => {
    const button = fixture.nativeElement.querySelector('.govuk-button');
    button.focus();

    timeoutBannerServiceMock.isVisible.set(true);
    fixture.detectChanges();
    expect(document.activeElement?.getAttribute('role')).toBe('dialog');

    timeoutBannerServiceMock.isVisible.set(false);
    fixture.detectChanges();
    expect(document.activeElement).toBe(button);
  });
});

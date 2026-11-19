import { Component, inject } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter, Router } from '@angular/router';

import { timer } from 'rxjs';

import { PendingRequestService } from '@netz/common/services';
import { BasePage } from '@netz/common/testing';
import { ButtonDirective } from '@netz/govuk-components';

import { PendingButtonDirective } from './pending-button.directive';

describe('PendingButtonDirective', () => {
  let directive: PendingButtonDirective;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: ` <button netzPendingButton govukButton type="submit" (click)="startRequest()">Submit</button> `,
    providers: [PendingRequestService],
    imports: [PendingButtonDirective, ButtonDirective],
  })
  class TestComponent {
    private readonly pendingRequest = inject(PendingRequestService);

    private readonly router = inject(Router);

    startRequest() {
      timer(500).pipe(this.pendingRequest.trackRequest()).subscribe();
    }

    saveAndNavigate() {
      timer(500)
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['next']));
    }
  }

  @Component({ template: '' })
  class NextPageComponent {}

  let releaseNextPage: () => void;

  class Page extends BasePage<TestComponent> {
    get button() {
      return this.query('button');
    }
  }

  beforeEach(async () => {
    const nextPageLoaded = new Promise<void>((resolve) => (releaseNextPage = resolve));

    await TestBed.configureTestingModule({
      imports: [PendingButtonDirective],
      providers: [provideRouter([{ path: 'next', loadComponent: () => nextPageLoaded.then(() => NextPageComponent) }])],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    page = new Page(fixture);

    fixture.detectChanges();
    directive = fixture.debugElement.query(By.directive(PendingButtonDirective)).componentInstance;
  });

  afterEach(() => {
    vi.clearAllTimers();
    vi.useRealTimers();
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should disable the button if a request is pending', () => {
    vi.useFakeTimers();

    expect(page.button.disabled).toBeFalsy();

    page.button.click();
    fixture.detectChanges();

    expect(page.button.disabled).toBeTruthy();

    vi.advanceTimersByTime(500);
    fixture.detectChanges();

    expect(page.button.disabled).toBeFalsy();
  });

  it('should keep the button disabled until the navigation that follows a request settles', async () => {
    vi.useFakeTimers();

    fixture.componentInstance.saveAndNavigate();
    fixture.detectChanges();

    expect(page.button.disabled).toBeTruthy();

    await vi.advanceTimersByTimeAsync(500);
    fixture.detectChanges();

    expect(page.button.disabled).toBeTruthy();

    releaseNextPage();
    await vi.runAllTimersAsync();
    fixture.detectChanges();

    expect(TestBed.inject(Router).url).toEqual('/next');
    expect(page.button.disabled).toBeFalsy();
  });
});

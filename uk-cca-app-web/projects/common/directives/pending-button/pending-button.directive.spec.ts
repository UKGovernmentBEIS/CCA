import { Component, inject } from '@angular/core';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

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

    startRequest() {
      timer(500).pipe(this.pendingRequest.trackRequest()).subscribe();
    }
  }

  class Page extends BasePage<TestComponent> {
    get button() {
      return this.query('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PendingButtonDirective],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    page = new Page(fixture);

    fixture.detectChanges();
    directive = fixture.debugElement.query(By.directive(PendingButtonDirective)).componentInstance;
  });

  it('should create an instance', () => {
    expect(directive).toBeTruthy();
  });

  it('should disable the button if a request is pending', fakeAsync(() => {
    expect(page.button.disabled).toBeFalsy();

    page.button.click();
    fixture.detectChanges();
    expect(page.button.disabled).toBeTruthy();

    tick(500);
    fixture.detectChanges();
    expect(page.button.disabled).toBeFalsy();
  }));
});

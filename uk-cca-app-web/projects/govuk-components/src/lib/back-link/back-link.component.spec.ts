import { Component, inject, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BackLinkComponent } from './back-link.component';

describe('BackLinkComponent', () => {
  @Component({
    imports: [BackLinkComponent],
    template: '<govuk-back-link [link]="link()" [route]="route"  [inverse]="inverse()"></govuk-back-link>',
  })
  class MockParentComponent {
    link = signal('../back');
    route = inject(ActivatedRoute).snapshot;
    inverse = signal(false);
  }

  let fixture: ComponentFixture<MockParentComponent>;
  let parentComponent: MockParentComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, BackLinkComponent, MockParentComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(MockParentComponent);
    parentComponent = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(parentComponent).toBeTruthy();
  });

  it('should have inverse color class', async () => {
    const hostElement: HTMLElement = fixture.nativeElement;
    const backlinkDiv = hostElement.querySelector<HTMLElement>('.govuk-back-link');
    expect(backlinkDiv.classList).not.toContain('govuk-back-link--inverse');

    fixture.componentInstance.inverse.set(true);
    fixture.detectChanges();

    expect(backlinkDiv.classList).toContain('govuk-back-link--inverse');
  });
});

import { Component, inject, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Params, provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { BackLinkComponent } from './back-link.component';

describe('BackLinkComponent', () => {
  @Component({
    template: '<govuk-back-link [link]="link()" [route]="route"  [inverse]="inverse()"></govuk-back-link>',
    imports: [BackLinkComponent],
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
      imports: [BackLinkComponent, MockParentComponent],
      providers: [provideRouter([])],
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

describe('BackLinkComponent links', () => {
  @Component({
    template:
      '<govuk-back-link [link]="link()" [fragment]="fragment()" [queryParams]="queryParams()" [route]="route" />',
    imports: [BackLinkComponent],
  })
  class MockPageComponent {
    link = signal('../');
    fragment = signal<string | undefined>(undefined);
    queryParams = signal<Params | undefined>(undefined);
    route = inject(ActivatedRoute).snapshot;
  }

  let harness: RouterTestingHarness;
  let page: MockPageComponent;

  const href = () =>
    harness.routeNativeElement.querySelector<HTMLAnchorElement>('.govuk-back-link').getAttribute('href');

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [provideRouter([{ path: 'parent/child', component: MockPageComponent }])],
    });

    harness = await RouterTestingHarness.create();
    page = await harness.navigateByUrl('/parent/child?change=true', MockPageComponent);
  });

  it('keeps the current query params without a fragment', () => {
    expect(href()).toEqual('/parent?change=true');
  });

  it('appends the fragment and drops the current query params', () => {
    page.fragment.set('reports');
    harness.detectChanges();

    expect(href()).toEqual('/parent#reports');
  });

  it('uses the given query params instead of the current ones', () => {
    page.queryParams.set({});
    harness.detectChanges();

    expect(href()).toEqual('/parent');

    page.queryParams.set({ page: 2 });
    harness.detectChanges();

    expect(href()).toEqual('/parent?page=2');
  });
});

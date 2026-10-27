import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Title } from '@angular/platform-browser';
import { provideRouter, Router, TitleStrategy } from '@angular/router';

import { CcaTitleStrategy } from './title-strategy';

@Component({ template: '' })
class TestComponent {}

describe('CcaTitleStrategy', () => {
  let router: Router;
  let titleService: Title;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideRouter([
          {
            path: '',
            component: TestComponent,
            children: [
              { path: 'plain', component: TestComponent },
              {
                title: 'Subtitled page',
                path: 'titled',
                component: TestComponent,
              },
              {
                title: 'Manage your Climate Change Agreements (CCA)',
                path: 'full-name',
                component: TestComponent,
                data: { titleFullServiceName: true },
              },
              {
                title: () => 'Resolved page',
                path: 'resolved',
                component: TestComponent,
              },
              {
                title: 'Review target unit details',
                path: 'workflow',
                component: TestComponent,
                children: [
                  {
                    title: 'Summary',
                    path: 'step',
                    component: TestComponent,
                  },
                  { path: 'inherited', component: TestComponent },
                  {
                    title: 'Review target unit details',
                    path: 'same',
                    component: TestComponent,
                  },
                ],
              },
            ],
          },
          { path: '**', component: TestComponent },
        ]),
        { provide: TitleStrategy, useClass: CcaTitleStrategy },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    titleService = TestBed.inject(Title);
  });

  it('uses the deepest route title', async () => {
    await router.navigateByUrl('/titled');

    expect(titleService.getTitle()).toBe('Subtitled page - CCA - GOV.UK');
  });

  it('composes the leaf title with the nearest titled ancestor', async () => {
    await router.navigateByUrl('/workflow/step');

    expect(titleService.getTitle()).toBe('Summary - Review target unit details - CCA - GOV.UK');
  });

  it('inherits the closest ancestor title without composing', async () => {
    await router.navigateByUrl('/workflow/inherited');

    expect(titleService.getTitle()).toBe('Review target unit details - CCA - GOV.UK');
  });

  it('uses a function title resolved into the snapshot', async () => {
    await router.navigateByUrl('/resolved');

    expect(titleService.getTitle()).toBe('Resolved page - CCA - GOV.UK');
  });

  it('does not compose when the leaf title equals the ancestor title', async () => {
    await router.navigateByUrl('/workflow/same');

    expect(titleService.getTitle()).toBe('Review target unit details - CCA - GOV.UK');
  });

  it('appends only the GOV.UK suffix when the title already contains the full service name', async () => {
    await router.navigateByUrl('/full-name');

    expect(titleService.getTitle()).toBe('Manage your Climate Change Agreements (CCA) - GOV.UK');
  });

  it('falls back to the short service name when nothing is set', async () => {
    await router.navigateByUrl('/does-not-exist');

    expect(titleService.getTitle()).toBe('CCA - GOV.UK');
  });
});

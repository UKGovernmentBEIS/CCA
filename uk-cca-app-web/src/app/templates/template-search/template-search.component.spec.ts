import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, convertToParamMap, ParamMap, provideRouter } from '@angular/router';

import { BehaviorSubject, Observable, of, Subject } from 'rxjs';

import { NotificationTemplateSearchResults } from 'cca-api';

import { mockNotificationTemplateSearchResults } from '../testing/mock-data';
import { TemplateSearchComponent, TemplateSearchFetchFn } from './template-search.component';

describe('TemplateSearchComponent', () => {
  let fixture: ComponentFixture<TemplateSearchComponent>;
  let queryParamMap$: BehaviorSubject<ParamMap>;
  let searchResults$: Observable<NotificationTemplateSearchResults>;
  const fetchFn: TemplateSearchFetchFn = () => searchResults$;

  const statusText = () =>
    (fixture.debugElement.query(By.css('p[role="status"]')).nativeElement as HTMLElement).textContent.trim();

  beforeEach(async () => {
    queryParamMap$ = new BehaviorSubject<ParamMap>(convertToParamMap({ page: '1', pageSize: '30' }));
    searchResults$ = of(mockNotificationTemplateSearchResults);

    await TestBed.configureTestingModule({
      imports: [TemplateSearchComponent],
      providers: [provideRouter([]), { provide: ActivatedRoute, useValue: { queryParamMap: queryParamMap$ } }],
    }).compileComponents();

    fixture = TestBed.createComponent(TemplateSearchComponent);
    fixture.componentRef.setInput('fetchFn', fetchFn);
    fixture.componentRef.setInput('templateType', 'email');
    fixture.componentRef.setInput('fragment', 'emails');
    fixture.detectChanges();
  });

  it('should show the result count once the response arrives', () => {
    expect(statusText()).toBe(`${mockNotificationTemplateSearchResults.total} results`);
  });

  it('should use singular wording when a single template is returned', () => {
    searchResults$ = of({ templates: [{ id: 10, name: 'GenericEmailTemplate' }], total: 1 });

    queryParamMap$.next(convertToParamMap({ term: 'generic', page: '1', pageSize: '30' }));
    fixture.detectChanges();

    expect(statusText()).toBe('1 result');
  });

  it('should clear the previous results until the new query responds', () => {
    const inFlight$ = new Subject<NotificationTemplateSearchResults>();
    searchResults$ = inFlight$;

    queryParamMap$.next(convertToParamMap({ term: 'letter', page: '1', pageSize: '30' }));
    fixture.detectChanges();

    expect(statusText()).toBe('');
    expect(fixture.debugElement.query(By.css('[data-testid="template-list-component"]'))).toBeNull();

    inFlight$.next(mockNotificationTemplateSearchResults);
    fixture.detectChanges();

    expect(statusText()).toBe(`${mockNotificationTemplateSearchResults.total} results`);
    expect(fixture.debugElement.query(By.css('[data-testid="template-list-component"]'))).toBeTruthy();
  });

  it('should announce no results when a new query returns none', () => {
    const inFlight$ = new Subject<NotificationTemplateSearchResults>();
    searchResults$ = inFlight$;

    queryParamMap$.next(convertToParamMap({ term: 'nothing matches', page: '1', pageSize: '30' }));
    fixture.detectChanges();

    // Blank first, so the empty result set is announced as a change when it arrives.
    expect(statusText()).toBe('');

    inFlight$.next({ templates: [], total: 0 });
    fixture.detectChanges();

    expect(statusText()).toBe('There are no results to show');
  });
});

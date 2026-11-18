import { provideHttpClient } from '@angular/common/http';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { CountryService } from '@shared/services';

import { mockAuditDetailsAndCorrectiveActionsState } from '../../../testing/mock-data';
import { CorrectiveActionsSummaryComponent } from './corrective-actions-summary.component';

describe('CorrectiveActionsSummaryComponent', () => {
  let component: CorrectiveActionsSummaryComponent;
  let fixture: ComponentFixture<CorrectiveActionsSummaryComponent>;
  let store: RequestTaskStore;

  const mockCountryService = {
    countries: signal([
      { code: 'GB', name: 'United Kingdom', officialName: 'United Kingdom' },
      { code: 'GR', name: 'Greece', officialName: 'Greece' },
    ]),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CorrectiveActionsSummaryComponent],
      providers: [
        provideHttpClient(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Dashboard' },
        { provide: CountryService, useValue: mockCountryService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockAuditDetailsAndCorrectiveActionsState);

    fixture = TestBed.createComponent(CorrectiveActionsSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct content', () => {
    expect(fixture.nativeElement.innerHTML).toMatchSnapshot();
  });
});

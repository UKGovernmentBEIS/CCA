import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockRequestTaskState } from '../../../testing/mock-data';
import AuthorisationAdditionalEvidenceSummaryComponent from './authorisation-additional-evidence-summary.component';

describe('AuthorisationAdditionalEvidenceSummaryComponent', () => {
  let component: AuthorisationAdditionalEvidenceSummaryComponent;
  let fixture: ComponentFixture<AuthorisationAdditionalEvidenceSummaryComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuthorisationAdditionalEvidenceSummaryComponent],
      providers: [
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Review underlying agreement variation' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockRequestTaskState);

    fixture = TestBed.createComponent(AuthorisationAdditionalEvidenceSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(fixture).toMatchSnapshot();
  });
});

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestActionStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockRequestActionState } from '../../testing/mock-data';
import { OverallDecisionComponent } from './overall-decision.component';

describe('OverallDecisionComponent', () => {
  let component: OverallDecisionComponent;
  let fixture: ComponentFixture<OverallDecisionComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OverallDecisionComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Underlying agreement application rejected' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState(mockRequestActionState);

    fixture = TestBed.createComponent(OverallDecisionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should match snapshot', () => {
    expect(fixture).toMatchSnapshot();
  });
});

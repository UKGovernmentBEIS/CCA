import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestActionStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockRequestActionState } from '../../testing/mock-data';
import { ReviewTargetUnitDetailsComponent } from './review-target-unit-details.component';

describe('ReviewTargetUnitDetailsComponent', () => {
  let component: ReviewTargetUnitDetailsComponent;
  let fixture: ComponentFixture<ReviewTargetUnitDetailsComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewTargetUnitDetailsComponent],
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

    fixture = TestBed.createComponent(ReviewTargetUnitDetailsComponent);
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

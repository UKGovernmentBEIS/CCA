import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { mockUNAReviewRequestTaskState } from '@requests/common';

import { FacilityDecisionComponent } from './facility-decision.component';

describe('FacilityDecisionComponent', () => {
  let fixture: ComponentFixture<FacilityDecisionComponent>;
  let store: RequestTaskStore;

  const unaTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  const route: any = {
    snapshot: {
      params: {
        facilityId: 'ADS_53-F00007',
      },
      pathFromRoot: [],
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FacilityDecisionComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: TaskService, useValue: unaTaskService },
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Review application for underlying agreement' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockUNAReviewRequestTaskState);

    fixture = TestBed.createComponent(FacilityDecisionComponent);
    fixture.detectChanges();
  });

  it('should match snapshot', () => {
    expect(fixture.nativeElement).toMatchSnapshot();
  });
});

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TASK_STATUS_TAG_MAP } from '@netz/common/pipes';
import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { taskStatusTagMap } from '@requests/common';

import { mockRequestActionState } from '../testing/mock-data';
import { UnderlyingAgreementReviewedTaskListComponent } from './underlying-agreement-reviewed-task-list.component';

describe('UnderlyingAgreementReviewTaskListComponent', () => {
  let component: UnderlyingAgreementReviewedTaskListComponent;
  let fixture: ComponentFixture<UnderlyingAgreementReviewedTaskListComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnderlyingAgreementReviewedTaskListComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        {
          provide: TASK_STATUS_TAG_MAP,
          useValue: taskStatusTagMap,
        },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState(mockRequestActionState);

    fixture = TestBed.createComponent(UnderlyingAgreementReviewedTaskListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

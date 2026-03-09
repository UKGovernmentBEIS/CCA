import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TASK_STATUS_TAG_MAP } from '@netz/common/pipes';
import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { BasePage } from '@netz/common/testing';
import { taskStatusTagMap } from '@requests/common';

import { mockAcceptedRequestActionState } from '../testing/mock-data';
import { UnderlyingAgreementVariationReviewedTaskListComponent } from './underlying-agreement-variation-reviewed-task-list.component';

describe('UnderlyingAgreementVariationReviewedTaskListComponent', () => {
  let component: UnderlyingAgreementVariationReviewedTaskListComponent;
  let fixture: ComponentFixture<UnderlyingAgreementVariationReviewedTaskListComponent>;
  let store: RequestActionStore;
  let page: Page;

  class Page extends BasePage<UnderlyingAgreementVariationReviewedTaskListComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.govuk-task-list__item > .govuk-task-list__name-and-hint'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnderlyingAgreementVariationReviewedTaskListComponent],
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
    store.setState(mockAcceptedRequestActionState);

    fixture = TestBed.createComponent(UnderlyingAgreementVariationReviewedTaskListComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task list', () => {
    expect(page.sections.map((el) => el.textContent.trim())).toEqual([
      'Describe the changes',
      'Target unit details',
      'Manage facilities',
      'TP5 (2021-2022)',
      'TP6 (2024)',
      'Authorisation and additional evidence',
      'Overall decision',
    ]);
  });
});

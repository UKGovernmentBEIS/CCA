import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { mockRequestTaskItemDTO } from '../../../testing/mock-data';
import ProvideEvidenceCheckAnswersComponent from './provide-evidence-check-answers.component';

describe('ProvideEvidenceCheckAnswersComponent', () => {
  let component: ProvideEvidenceCheckAnswersComponent;
  let fixture: ComponentFixture<ProvideEvidenceCheckAnswersComponent>;
  let store: RequestTaskStore;
  let page: Page;

  const unaActivationTaskService: Partial<jest.Mocked<TaskService>> = {
    submitSubtask: jest.fn().mockReturnValue(of({})),
  };

  class Page extends BasePage<ProvideEvidenceCheckAnswersComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }

    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [
          ...(Array.from(row.querySelectorAll('dt')) ?? []),
          ...(Array.from(row.querySelectorAll('dd')) ?? []),
        ])
        .map((pair) => pair.map((element) => element?.textContent?.trim()));
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ProvideEvidenceCheckAnswersComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TaskService, useValue: unaActivationTaskService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem(mockRequestTaskItemDTO);

    fixture = TestBed.createComponent(ProvideEvidenceCheckAnswersComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(page.header.textContent.trim()).toEqual('Check your answers');

    expect(page.summaryListValues).toEqual([
      ['Uploaded files', 'evidenceFile.xlsx'],
      ['Comments', 'My comments'],
    ]);
  });

  it('should submit', () => {
    const taskServiceSpy = jest.spyOn(unaActivationTaskService, 'submitSubtask');

    page.submitButton.click();
    fixture.detectChanges();

    expect(taskServiceSpy).toHaveBeenCalledWith('underlyingAgreementActivationDetails');
  });
});

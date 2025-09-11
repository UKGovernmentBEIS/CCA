import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub, BasePage } from '@netz/common/testing';
import { PROVIDE_EVIDENCE_SUBTASK } from '@requests/common';

import { mockRequestTaskItemDTO } from '../../../testing/mock-data';
import ProvideEvidenceDetailsComponent from './provide-evidence-details.component';

describe('ProvideEvidenceDetailsComponent', () => {
  let component: ProvideEvidenceDetailsComponent;
  let fixture: ComponentFixture<ProvideEvidenceDetailsComponent>;
  let store: RequestTaskStore;
  let page: Page;

  const route = new ActivatedRouteStub();
  const unaActivationTaskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  class Page extends BasePage<ProvideEvidenceDetailsComponent> {
    get comments() {
      return this.getInputValue('#comments');
    }
    set comments(value: string) {
      this.setInputValue('#comments', value);
    }
    get filesText() {
      return this.queryAll<HTMLDivElement>('.cca-multi-file-upload__message');
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ProvideEvidenceDetailsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: route },
        { provide: TaskService, useValue: unaActivationTaskService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem(mockRequestTaskItemDTO);

    fixture = TestBed.createComponent(ProvideEvidenceDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show form values', () => {
    expect(page.comments).toEqual('My comments');
    expect(page.filesText.map((row) => row.textContent.trim())).toEqual(['evidenceFile.xlsx']);
  });

  it('should edit and save', () => {
    const taskServiceSpy = jest.spyOn(unaActivationTaskService, 'saveSubtask');

    page.comments = 'My comments change';
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(taskServiceSpy).toHaveBeenCalledWith(PROVIDE_EVIDENCE_SUBTASK, 'details', route, {
      evidenceFiles: ['evidenceFile'],
      comments: 'My comments change',
    });
  });
});

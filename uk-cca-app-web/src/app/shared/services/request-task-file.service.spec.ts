import { HttpResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule, ValidationErrors } from '@angular/forms';

import { lastValueFrom, Observable } from 'rxjs';

import { SignalStore } from '@netz/common/store';
import { asyncData, mockClass } from '@netz/common/testing';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'cca-api';

import { RequestTaskFileService } from './request-task-file.service';

class MockedStore extends SignalStore<MockedState> {
  constructor() {
    super(initialMockedState);
  }
}

interface MockedState {
  requestTaskId: number;
}

const initialMockedState: MockedState = {
  requestTaskId: 1,
};

describe('RequestTaskFileService', () => {
  let service: RequestTaskFileService;
  let mockedStore: MockedStore;
  let attachmentsService: jest.Mocked<RequestTaskAttachmentsHandlingService>;

  beforeEach(() => {
    attachmentsService = mockClass(RequestTaskAttachmentsHandlingService);
    attachmentsService.uploadRequestTaskAttachment.mockReturnValue(
      asyncData<any>(new HttpResponse({ body: { data: { uuid: 'xyz' } } })),
    );

    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [
        MockedStore,
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentsService },
        { provide: TasksService, useValue: mockClass(TasksService) },
      ],
    });

    service = TestBed.inject(RequestTaskFileService);
    mockedStore = TestBed.inject(MockedStore);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should upload a single file', async () => {
    const control = new FormControl({ file: new File(['content'], 'file.txt') });
    expect(
      lastValueFrom(
        service.upload(mockedStore.state.requestTaskId, 'RDE_SUBMIT')(control) as Observable<ValidationErrors>,
      ),
    ).resolves.toBeNull();
  });

  it('should upload multiple files', () => {
    const control = new FormControl([{ file: new File(['content'], 'file.txt') }]);
    expect(
      lastValueFrom(
        service.uploadMany(mockedStore.state.requestTaskId, 'RDE_SUBMIT')(control) as Observable<ValidationErrors>,
      ),
    ).resolves.toBeNull();
  });
});

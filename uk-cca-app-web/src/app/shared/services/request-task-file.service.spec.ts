import { HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule, ValidationErrors } from '@angular/forms';

import { lastValueFrom, Observable } from 'rxjs';

import { SignalStore } from '@netz/common/store';
import { asyncData, mockClass } from '@netz/common/testing';
import { Mocked } from 'vitest';

import { FileUuidDTO, RequestTaskAttachmentsHandlingService, TasksService } from 'cca-api';

import { RequestTaskFileService } from './request-task-file.service';

@Injectable()
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
  let attachmentsService: Mocked<RequestTaskAttachmentsHandlingService>;

  beforeEach(() => {
    attachmentsService = mockClass(RequestTaskAttachmentsHandlingService);
    attachmentsService.uploadRequestTaskAttachment.mockReturnValue(
      asyncData(new HttpResponse<FileUuidDTO>({ body: { uuid: 'xyz' } })) as Observable<FileUuidDTO>,
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

    await expect(
      lastValueFrom(
        service.upload(mockedStore.state.requestTaskId, 'RDE_SUBMIT')(control) as Observable<ValidationErrors>,
      ),
    ).resolves.toBeNull();
  });

  it('should upload multiple files', async () => {
    const control = new FormControl([{ file: new File(['content'], 'file.txt') }]);

    await expect(
      lastValueFrom(
        service.uploadMany(mockedStore.state.requestTaskId, 'RDE_SUBMIT')(control) as Observable<ValidationErrors>,
      ),
    ).resolves.toBeNull();
  });
});

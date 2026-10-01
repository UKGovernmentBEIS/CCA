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

  // `UploadedFileRef.name` is typed as a string and is what the file list renders, so stored files
  // always get a name instead of undefined. The name is empty when the attachments map of the
  // payload does not cover the uuid; payloads skip such entries rather than inventing a name.
  describe('buildFormControl', () => {
    it('should name a stored file from the attachments map', () => {
      const control = service.buildFormControl(1, 'uuid-1', { 'uuid-1': 'permit.pdf' }, 'RDE_SUBMIT');

      expect(control.value).toEqual({ uuid: 'uuid-1', file: { name: 'permit.pdf' } });
    });

    it('should leave the name empty when the attachments map does not cover the uuid', () => {
      expect(service.buildFormControl(1, 'uuid-1', { 'uuid-2': 'other.pdf' }, 'RDE_SUBMIT').value).toEqual({
        uuid: 'uuid-1',
        file: { name: '' },
      });
      expect(service.buildFormControl(1, 'uuid-1', undefined, 'RDE_SUBMIT').value).toEqual({
        uuid: 'uuid-1',
        file: { name: '' },
      });
    });

    it('should name a list of stored files', () => {
      const control = service.buildFormControl(1, ['uuid-1', 'uuid-2'], { 'uuid-1': 'permit.pdf' }, 'RDE_SUBMIT');

      expect(control.value).toEqual([
        { uuid: 'uuid-1', file: { name: 'permit.pdf' } },
        { uuid: 'uuid-2', file: { name: '' } },
      ]);
    });

    it('should not build a file for an empty uuid field', () => {
      expect(service.buildFormControl(1, null, {}, 'RDE_SUBMIT').value).toBeNull();
      expect(service.buildFormControl(1, '', {}, 'RDE_SUBMIT').value).toBeNull();
    });
  });
});

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { defer, firstValueFrom, of, take } from 'rxjs';

import { ActivatedRouteStub, mockClass, testSchedulerFactory } from '@netz/common/testing';

import {
  FileAttachmentsService,
  RequestTaskAttachmentsHandlingService,
  TasksService,
  UnderlyingAgreementsService,
} from 'cca-api';

import { FileDownloadComponent } from './file-download.component';

describe('FileDownloadComponent', () => {
  let component: FileDownloadComponent;
  let fixture: ComponentFixture<FileDownloadComponent>;
  let requestTaskAttachmentsHandlingService: jest.Mocked<RequestTaskAttachmentsHandlingService>;

  beforeEach(async () => {
    Object.defineProperty(window, 'onfocus', { set: jest.fn() });
    requestTaskAttachmentsHandlingService = mockClass(RequestTaskAttachmentsHandlingService);
    requestTaskAttachmentsHandlingService.generateRequestTaskGetFileAttachmentToken.mockReturnValue(
      of({ token: 'abce', tokenExpirationMinutes: 1 }),
    );

    const activatedRoute = new ActivatedRouteStub({ uuid: 'xyz', taskId: 11 });

    await TestBed.configureTestingModule({
      imports: [FileDownloadComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: RequestTaskAttachmentsHandlingService, useValue: requestTaskAttachmentsHandlingService },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: UnderlyingAgreementsService, useValue: mockClass(UnderlyingAgreementsService) },
        { provide: FileAttachmentsService, useValue: { configuration: { basePath: '' } } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FileDownloadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the download link', async () => {
    await expect(firstValueFrom(component.url$)).resolves.toEqual('/v1.0/file-attachments/abce');
  });

  it('should refresh the download link', async () => {
    requestTaskAttachmentsHandlingService.generateRequestTaskGetFileAttachmentToken
      .mockClear()
      .mockImplementation(() => {
        let subscribes = 0;

        return defer(() => {
          subscribes += 1;

          return subscribes === 1
            ? of({ token: 'abcf', tokenExpirationMinutes: 1 })
            : subscribes === 2
              ? of({ token: 'abcd', tokenExpirationMinutes: 2 })
              : of({ token: 'abce', tokenExpirationMinutes: 1 });
        });
      });

    testSchedulerFactory().run(({ expectObservable }) =>
      expectObservable(component.url$.pipe(take(3))).toBe('a 59s 999ms b 119s 999ms (c|)', {
        a: '/v1.0/file-attachments/abcf',
        b: '/v1.0/file-attachments/abcd',
        c: '/v1.0/file-attachments/abce',
      }),
    );
  });
});

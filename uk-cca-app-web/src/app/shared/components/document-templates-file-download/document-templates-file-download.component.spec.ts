import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { defer, firstValueFrom, of, take } from 'rxjs';

import { ActivatedRouteStub, mockClass, testSchedulerFactory } from '@netz/common/testing';

import { DocumentTemplateFilesService } from 'cca-api';

import { DocumentTemplatesFileDownloadComponent } from './document-templates-file-download.component';

describe('DocumentTemplatesFileDownloadComponent', () => {
  let component: DocumentTemplatesFileDownloadComponent;
  let fixture: ComponentFixture<DocumentTemplatesFileDownloadComponent>;
  let documentTemplateFilesService: jest.Mocked<DocumentTemplateFilesService>;

  beforeEach(async () => {
    Object.defineProperty(window, 'onfocus', { set: jest.fn() });

    documentTemplateFilesService = mockClass(DocumentTemplateFilesService);
    documentTemplateFilesService.generateGetDocumentTemplateFileToken.mockReturnValue(
      of({ token: 'abce', tokenExpirationMinutes: 1 }),
    );

    const activatedRoute = new ActivatedRouteStub({ templateId: 1, uuid: 'xyz' });

    await TestBed.configureTestingModule({
      imports: [DocumentTemplatesFileDownloadComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: activatedRoute },
        {
          provide: DocumentTemplateFilesService,
          useValue: documentTemplateFilesService,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DocumentTemplatesFileDownloadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the download link', async () => {
    await expect(firstValueFrom(component.url$)).resolves.toEqual('/api/v1.0/file-document-templates/abce');
  });

  it('should refresh the download link', async () => {
    documentTemplateFilesService.generateGetDocumentTemplateFileToken.mockClear().mockImplementation(() => {
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
        a: '/api/v1.0/file-document-templates/abcf',
        b: '/api/v1.0/file-document-templates/abcd',
        c: '/api/v1.0/file-document-templates/abce',
      }),
    );
  });
});

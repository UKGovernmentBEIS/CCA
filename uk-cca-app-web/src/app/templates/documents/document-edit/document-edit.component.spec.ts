import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { FileType } from '@shared/components';

import { DocumentTemplatesService } from 'cca-api';

import { mockDocumentTemplateViewDTO } from '../../testing/mock-data';
import { DocumentEditComponent } from './document-edit.component';

describe('DocumentEditComponent', () => {
  let component: DocumentEditComponent;
  let fixture: ComponentFixture<DocumentEditComponent>;
  let documentTemplatesService: jest.Mocked<Partial<DocumentTemplatesService>>;
  let router: Router;

  beforeEach(async () => {
    documentTemplatesService = {
      getDocumentTemplateById: jest.fn().mockReturnValue(of(mockDocumentTemplateViewDTO)),
      updateDocumentTemplate: jest.fn().mockReturnValue(of(null)),
    };

    await TestBed.configureTestingModule({
      imports: [DocumentEditComponent],
      providers: [
        { provide: ActivatedRoute, useValue: { snapshot: { params: { templateId: 10 } } } },
        { provide: DocumentTemplatesService, useValue: documentTemplatesService },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);

    fixture = TestBed.createComponent(DocumentEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call document service with correct template ID', () => {
    expect(documentTemplatesService.getDocumentTemplateById).toHaveBeenCalledWith(10);
  });

  it('should display edit template name in page heading', () => {
    const pageHeading = fixture.nativeElement.querySelector('netz-page-heading');
    expect(pageHeading.textContent.trim()).toBe(`Edit ${mockDocumentTemplateViewDTO.name}`);
  });

  it('should set file type to DOCX', () => {
    expect(component.fileType).toBe(FileType.DOCX);
  });

  it('should initialize form with template file data', () => {
    const form = component.form();
    const documentTemplateControl = form.get('documentTemplate');

    expect(documentTemplateControl?.value).toEqual({
      uuid: mockDocumentTemplateViewDTO.fileUuid,
      file: { name: mockDocumentTemplateViewDTO.filename },
    });
  });

  it('should render file input with correct properties', () => {
    const fileInput = fixture.nativeElement.querySelector('cca-file-input');
    expect(fileInput).toBeTruthy();
  });

  it('should generate correct download URL', () => {
    const uuid = 'test-uuid';
    const downloadUrl = component.getDownloadUrl(uuid);
    expect(downloadUrl).toEqual(['../file-download', 'attachment', uuid]);
  });

  it('should validate required file', () => {
    const form = component.form();
    const documentTemplateControl = form.get('documentTemplate');

    documentTemplateControl?.setValue(null);

    expect(documentTemplateControl?.hasError('required')).toBe(true);
  });

  it('should not submit form when invalid', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);
    const form = component.form();
    jest.spyOn(form, 'invalid', 'get').mockReturnValue(true);

    component.onSubmit();

    expect(documentTemplatesService.updateDocumentTemplate).not.toHaveBeenCalled();
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should call updateDocumentTemplate with correct parameters on valid form submission', () => {
    const mockFile = new File(['test content'], 'test.docx', {
      type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    });

    const form = component.form();

    form.get('documentTemplate')?.setValue({
      uuid: 'test-uuid',
      file: mockFile,
    });

    component.onSubmit();

    expect(documentTemplatesService.updateDocumentTemplate).toHaveBeenCalledWith(
      mockDocumentTemplateViewDTO.id,
      mockFile,
    );
  });

  it('should navigate back with notification state after successful update', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    const mockFile = new File(['test content'], 'test.docx', {
      type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    });

    const form = component.form();

    form.get('documentTemplate')?.setValue({
      uuid: 'test-uuid',
      file: mockFile,
    });

    component.onSubmit();

    expect(navigateSpy).toHaveBeenCalledWith(['..'], {
      relativeTo: expect.any(Object),
      replaceUrl: true,
      state: { notification: true },
    });
  });

  it('should render summary component with computed data', () => {
    const summaryComponent = fixture.nativeElement.querySelector('cca-summary');
    expect(summaryComponent).toBeTruthy();
    expect(component.data()).toBeDefined();
  });

  it('should initialize form with null when no file data exists', () => {
    const templateWithoutFile = { ...mockDocumentTemplateViewDTO, fileUuid: null, filename: null };
    documentTemplatesService.getDocumentTemplateById.mockReturnValue(of(templateWithoutFile));

    const newFixture = TestBed.createComponent(DocumentEditComponent);
    const newComponent = newFixture.componentInstance;
    newFixture.detectChanges();

    const form = newComponent.form();
    expect(form.get('documentTemplate')?.value).toBeNull();
  });
});

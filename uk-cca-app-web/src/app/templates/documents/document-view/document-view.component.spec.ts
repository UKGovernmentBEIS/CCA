import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { Mocked } from 'vitest';

import { DocumentTemplatesService } from 'cca-api';

import { mockDocumentTemplateViewDTO } from '../../testing/mock-data';
import { DocumentViewComponent } from './document-view.component';

describe('DocumentViewComponent', () => {
  let component: DocumentViewComponent;
  let fixture: ComponentFixture<DocumentViewComponent>;
  let documentTemplatesService: Mocked<Partial<DocumentTemplatesService>>;

  beforeEach(async () => {
    documentTemplatesService = {
      getDocumentTemplateById: vi.fn().mockReturnValue(of(mockDocumentTemplateViewDTO)),
    };

    await TestBed.configureTestingModule({
      imports: [DocumentViewComponent],
      providers: [
        { provide: ActivatedRoute, useValue: { snapshot: { params: { templateId: 10 } } } },
        { provide: DocumentTemplatesService, useValue: documentTemplatesService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DocumentViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call document service with correct template ID', () => {
    expect(documentTemplatesService.getDocumentTemplateById).toHaveBeenCalledWith(10);
  });

  it('should display template name in page heading', () => {
    const pageHeading = fixture.nativeElement.querySelector('netz-page-heading');
    expect(pageHeading.textContent.trim()).toBe(mockDocumentTemplateViewDTO.name);
  });

  it('should render summary component with computed data', () => {
    const summaryComponent = fixture.nativeElement.querySelector('cca-summary');
    expect(summaryComponent).toBeTruthy();
    expect(component.data()).toBeDefined();
  });

  it('should not render content when template is not available', () => {
    documentTemplatesService.getDocumentTemplateById.mockReturnValue(of(null));

    const newFixture = TestBed.createComponent(DocumentViewComponent);
    newFixture.detectChanges();

    const pageHeading = newFixture.nativeElement.querySelector('netz-page-heading');
    expect(pageHeading).toBeFalsy();
  });
});

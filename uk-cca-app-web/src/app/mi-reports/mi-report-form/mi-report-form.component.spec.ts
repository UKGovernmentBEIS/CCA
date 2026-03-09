import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub, mockClass } from '@netz/common/testing';

import { MiReportsUserDefinedService, MiReportUserDefinedDTO } from 'cca-api';

import { MiReportsExportService } from '../core/mi-reports-export.service';
import { MiReportFormComponent } from './mi-report-form.component';

describe('MiReportFormComponent', () => {
  let component: MiReportFormComponent;
  let fixture: ComponentFixture<MiReportFormComponent>;
  let miReportsUserDefinedService: jest.Mocked<MiReportsUserDefinedService>;
  let router: Router;

  const mockQuery: MiReportUserDefinedDTO = {
    reportName: 'Existing Report',
    description: 'Existing Description',
    queryDefinition: 'SELECT * FROM existing',
  };

  const createComponent = async (isEditMode: boolean) => {
    const mockMiReportsUserDefinedService = mockClass(MiReportsUserDefinedService);
    mockMiReportsUserDefinedService.createMiReportUserDefined = jest.fn().mockReturnValue(of({}));
    mockMiReportsUserDefinedService.updateMiReportUserDefined = jest.fn().mockReturnValue(of({}));
    mockMiReportsUserDefinedService.generateCustomReport = jest.fn().mockReturnValue(of({ results: [] }));

    const mockMiReportsExportService = {
      exportToExcel: jest.fn(),
    };

    const mockActivatedRoute = isEditMode
      ? new ActivatedRouteStub({ queryId: '123' }, null, { query: mockQuery })
      : new ActivatedRouteStub();

    await TestBed.configureTestingModule({
      imports: [MiReportFormComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: MiReportsUserDefinedService, useValue: mockMiReportsUserDefinedService },
        { provide: MiReportsExportService, useValue: mockMiReportsExportService },
      ],
    }).compileComponents();

    miReportsUserDefinedService = TestBed.inject(
      MiReportsUserDefinedService,
    ) as jest.Mocked<MiReportsUserDefinedService>;
    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate');

    fixture = TestBed.createComponent(MiReportFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  afterEach(() => {
    TestBed.resetTestingModule();
    jest.clearAllMocks();
  });

  describe('Create mode', () => {
    beforeEach(async () => {
      await createComponent(false);
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize form with empty values', () => {
      expect(component['form'].controls.reportName.value).toBeNull();
      expect(component['form'].controls.description.value).toBeNull();
      expect(component['form'].controls.queryDefinition.value).toBeNull();
    });

    it('should display create mode heading', () => {
      const compiled = fixture.nativeElement;

      expect(compiled.textContent).toContain('New MI Report');
      expect(compiled.textContent).not.toContain('Change');
    });

    it('should not submit if form is invalid', () => {
      component['form'].patchValue({ reportName: '', queryDefinition: '' });
      component.onSubmit();

      expect(miReportsUserDefinedService.createMiReportUserDefined).not.toHaveBeenCalled();
      expect(component['isErrorSummaryDisplayed']()).toBe(true);
    });

    it('should call createMiReportUserDefined and navigate back on submit', () => {
      component['form'].patchValue({
        reportName: 'Test Report',
        description: 'Test Description',
        queryDefinition: 'SELECT * FROM test',
      });

      component.onSubmit();

      expect(miReportsUserDefinedService.createMiReportUserDefined).toHaveBeenCalledWith({
        reportName: 'Test Report',
        description: 'Test Description',
        queryDefinition: 'SELECT * FROM test',
      });

      expect(router.navigate).toHaveBeenCalledWith(['..'], {
        relativeTo: expect.anything(),
      });
    });

    it('should have Save and confirm button', () => {
      const compiled = fixture.nativeElement;
      const submitButton = compiled.querySelector('button[type="submit"]');

      expect(submitButton).toBeTruthy();
      expect(submitButton.textContent).toContain('Save and confirm');
    });

    it('should have Export to Excel button', () => {
      const compiled = fixture.nativeElement;
      const exportButton = compiled.querySelector('button[type="button"]');

      expect(exportButton).toBeTruthy();
      expect(exportButton.textContent).toContain('Export to Excel');
    });

    it('should show error summary when form is invalid on export', () => {
      component['form'].patchValue({ queryDefinition: '' });
      component.exportToExcel();

      expect(component['isErrorSummaryDisplayed']()).toBe(true);
      expect(miReportsUserDefinedService.generateCustomReport).not.toHaveBeenCalled();
    });

    it('should call generateCustomReport when exporting with valid query', () => {
      component['form'].patchValue({ queryDefinition: 'SELECT * FROM test' });
      component.exportToExcel();

      expect(miReportsUserDefinedService.generateCustomReport).toHaveBeenCalledWith({
        sqlQuery: 'SELECT * FROM test',
      });
    });
  });

  describe('Edit mode', () => {
    beforeEach(async () => {
      await createComponent(true);
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize form with prepopulated values from route data', () => {
      expect(component['form'].controls.reportName.value).toBe('Existing Report');
      expect(component['form'].controls.description.value).toBe('Existing Description');
      expect(component['form'].controls.queryDefinition.value).toBe('SELECT * FROM existing');
    });

    it('should display edit mode heading', () => {
      const compiled = fixture.nativeElement;

      expect(compiled.textContent).toContain('MI Report details');
      expect(compiled.textContent).toContain('Change');
    });

    it('should not submit if form is invalid', () => {
      component['form'].patchValue({ reportName: '', queryDefinition: '' });
      component.onSubmit();

      expect(miReportsUserDefinedService.updateMiReportUserDefined).not.toHaveBeenCalled();
      expect(component['isErrorSummaryDisplayed']()).toBe(true);
    });

    it('should call updateMiReportUserDefined and navigate back on submit', () => {
      component['form'].patchValue({
        reportName: 'Updated Report',
        description: 'Updated Description',
        queryDefinition: 'SELECT * FROM updated',
      });

      component.onSubmit();

      expect(miReportsUserDefinedService.updateMiReportUserDefined).toHaveBeenCalledWith(123, {
        reportName: 'Updated Report',
        description: 'Updated Description',
        queryDefinition: 'SELECT * FROM updated',
      });

      expect(router.navigate).toHaveBeenCalledWith(['../..'], {
        relativeTo: expect.anything(),
      });
    });

    it('should have Return to MI Reports link', () => {
      const compiled = fixture.nativeElement;
      const returnLink = compiled.querySelector('a.govuk-link');

      expect(returnLink).toBeTruthy();
      expect(returnLink.textContent).toContain('Return to: MI Reports');
    });
  });
});

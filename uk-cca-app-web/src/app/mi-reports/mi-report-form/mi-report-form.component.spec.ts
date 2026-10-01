import { HttpErrorResponse, provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of, throwError } from 'rxjs';

import { ActivatedRouteStub, mockClass } from '@netz/common/testing';
import { Mocked } from 'vitest';

import { MiReportsUserDefinedService, MiReportUserDefinedDTO } from 'cca-api';

import { MiReportsExportService } from '../core/mi-reports-export.service';
import { MiReportFormComponent } from './mi-report-form.component';

describe('MiReportFormComponent', () => {
  let component: MiReportFormComponent;
  let fixture: ComponentFixture<MiReportFormComponent>;
  let miReportsUserDefinedService: Mocked<MiReportsUserDefinedService>;
  let router: Router;

  const mockQuery: MiReportUserDefinedDTO = {
    reportName: 'Existing Report',
    description: 'Existing Description',
    queryDefinition: 'SELECT * FROM existing',
  };

  const createComponent = async (isEditMode: boolean) => {
    const mockMiReportsUserDefinedService = mockClass(MiReportsUserDefinedService);
    mockMiReportsUserDefinedService.createMiReportUserDefined = vi.fn().mockReturnValue(of({}));
    mockMiReportsUserDefinedService.updateMiReportUserDefined = vi.fn().mockReturnValue(of({}));
    mockMiReportsUserDefinedService.generateCustomReport = vi.fn().mockReturnValue(of({ results: [] }));

    const mockMiReportsExportService = {
      exportToExcel: vi.fn(),
    };

    const mockActivatedRoute = isEditMode
      ? new ActivatedRouteStub({ queryId: '123' }, null, { query: mockQuery })
      : new ActivatedRouteStub();

    await TestBed.configureTestingModule({
      imports: [MiReportFormComponent],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: MiReportsUserDefinedService, useValue: mockMiReportsUserDefinedService },
        { provide: MiReportsExportService, useValue: mockMiReportsExportService },
      ],
    }).compileComponents();

    miReportsUserDefinedService = TestBed.inject(MiReportsUserDefinedService) as Mocked<MiReportsUserDefinedService>;
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate');

    fixture = TestBed.createComponent(MiReportFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  afterEach(() => {
    TestBed.resetTestingModule();
    vi.clearAllMocks();
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

    it('should validate the query maximum length', () => {
      const queryDefinition = component['form'].controls.queryDefinition;

      queryDefinition.setValue('a'.repeat(50000));
      expect(queryDefinition.valid).toBe(true);

      queryDefinition.setValue('a'.repeat(50001));
      expect(queryDefinition.invalid).toBe(true);
    });

    it.each([
      {
        field: 'reportName' as const,
        maximumLength: 255,
        errorMessage: 'The report name should not be more than 255 characters',
      },
      {
        field: 'description' as const,
        maximumLength: 10000,
        errorMessage: 'The description should not be more than 10000 characters',
      },
    ])('should validate the $field maximum length', ({ field, maximumLength, errorMessage }) => {
      const control = component['form'].controls[field];

      control.setValue('a'.repeat(maximumLength));
      expect(control.valid).toBe(true);

      control.setValue('a'.repeat(maximumLength + 1));
      expect(control.errors).toEqual({ maxlength: errorMessage });
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

    it('should display a query validation error when the request returns FORM1001', () => {
      miReportsUserDefinedService.createMiReportUserDefined.mockReturnValue(
        throwError(
          () =>
            new HttpErrorResponse({
              status: 400,
              error: { code: 'FORM1001' },
            }),
        ),
      );
      component['form'].patchValue({
        reportName: 'Test Report',
        queryDefinition: 'SELECT * FROM test',
      });

      component.onSubmit();

      expect(component['form'].controls.queryDefinition.errors).toEqual({
        apiError: 'Form validation failed. Please review the form fields and ensure the query definition is valid.',
      });
      expect(component['isErrorSummaryDisplayed']()).toBe(true);
      expect(router.navigate).not.toHaveBeenCalled();
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

import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub, mockClass } from '@netz/common/testing';

import { MiReportsUserDefinedService, MiReportUserDefinedInfoDTO, MiReportUserDefinedResults } from 'cca-api';

import { MiReportsExportService } from '../core/mi-reports-export.service';
import { mockCustomMiReportResult } from '../testing/mock-data';
import { MiReportsListComponent } from './mi-reports-list.component';

describe('MiReportsListComponent', () => {
  let component: MiReportsListComponent;
  let fixture: ComponentFixture<MiReportsListComponent>;
  let miReportsUserDefinedService: jest.Mocked<MiReportsUserDefinedService>;
  let miReportsExportService: jest.Mocked<MiReportsExportService>;
  let router: Router;

  const mockReports: MiReportUserDefinedInfoDTO[] = [
    { id: 1, reportName: 'Report One', description: 'First report' },
    { id: 2, reportName: 'Report Two', description: 'Second report' },
  ];

  const mockResults: MiReportUserDefinedResults = {
    queries: mockReports,
    total: 2,
  };

  beforeEach(async () => {
    const mockMiReportsUserDefinedService = mockClass(MiReportsUserDefinedService);
    mockMiReportsUserDefinedService.getAllMiReportsUserDefined = jest.fn().mockReturnValue(of(mockResults));
    mockMiReportsUserDefinedService.getMiReportUserDefinedById = jest
      .fn()
      .mockReturnValue(of({ reportName: 'Report One', queryDefinition: 'SELECT * FROM test' }));
    mockMiReportsUserDefinedService.generateCustomReport = jest.fn().mockReturnValue(of(mockCustomMiReportResult));

    const mockMiReportsExportService = mockClass(MiReportsExportService);
    mockMiReportsExportService.exportToExcel = jest.fn();

    const mockActivatedRoute = new ActivatedRouteStub(null, {});

    await TestBed.configureTestingModule({
      imports: [MiReportsListComponent],
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
    miReportsExportService = TestBed.inject(MiReportsExportService) as jest.Mocked<MiReportsExportService>;
    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate');

    fixture = TestBed.createComponent(MiReportsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch reports on init', () => {
    expect(miReportsUserDefinedService.getAllMiReportsUserDefined).toHaveBeenCalledWith(0, 10);
  });

  it('should render table with report data', () => {
    const compiled = fixture.nativeElement;
    const rows = compiled.querySelectorAll('tbody tr');

    expect(rows.length).toBe(2);
  });

  it('should render report name as edit link', () => {
    const compiled = fixture.nativeElement;
    const nameLinks = compiled.querySelectorAll('tbody tr td a.govuk-link');
    const editLink = Array.from<HTMLAnchorElement>(nameLinks).find((a) => a.textContent.includes('Report One'));

    expect(editLink).toBeTruthy();
  });

  it('should render Delete link for each row', () => {
    const compiled = fixture.nativeElement;
    const deleteLinks = Array.from<HTMLAnchorElement>(compiled.querySelectorAll('tbody tr td a.govuk-link')).filter(
      (a) => a.textContent.includes('Delete'),
    );

    expect(deleteLinks.length).toBe(2);
  });

  it('should render Export to Excel link for each row', () => {
    const compiled = fixture.nativeElement;
    const exportLinks = Array.from<HTMLAnchorElement>(compiled.querySelectorAll('tbody tr td a.govuk-link')).filter(
      (a) => a.textContent.includes('Export to Excel'),
    );

    expect(exportLinks.length).toBe(2);
  });

  it('should call export services when exportToExcel is triggered', () => {
    component.exportToExcel(mockReports[0]);

    expect(miReportsUserDefinedService.getMiReportUserDefinedById).toHaveBeenCalledWith(1);
    expect(miReportsUserDefinedService.generateCustomReport).toHaveBeenCalledWith({ sqlQuery: 'SELECT * FROM test' });
    expect(miReportsExportService.exportToExcel).toHaveBeenCalledWith(mockCustomMiReportResult, 'Report One');
  });

  it('should render table headers', () => {
    const compiled = fixture.nativeElement;
    const headers = compiled.querySelectorAll('thead th');

    expect(headers.length).toBe(3);
    expect(headers[0].textContent.trim()).toBe('Name');
    expect(headers[1].textContent.trim()).toBe('Description');
    expect(headers[2].textContent.trim()).toBe('Actions');
  });

  it('should navigate with page query param on page change', () => {
    component.onPageChange(2);

    expect(router.navigate).toHaveBeenCalledWith([], {
      queryParams: { page: 2 },
      queryParamsHandling: 'merge',
      relativeTo: expect.anything(),
    });
  });

  it('should navigate with page and pageSize on page size change', () => {
    component.onPageSizeChange(20);

    expect(router.navigate).toHaveBeenCalledWith([], {
      queryParams: { page: 1, pageSize: 20 },
      queryParamsHandling: 'merge',
      relativeTo: expect.anything(),
    });
  });

  it('should not navigate when page is the same', () => {
    component.onPageChange(1);

    expect(router.navigate).not.toHaveBeenCalled();
  });
});

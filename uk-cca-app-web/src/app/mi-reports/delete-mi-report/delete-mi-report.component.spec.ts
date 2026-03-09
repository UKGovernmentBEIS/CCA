import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub, mockClass } from '@netz/common/testing';

import { MiReportsUserDefinedService, MiReportUserDefinedDTO } from 'cca-api';

import { DeleteMiReportComponent } from './delete-mi-report.component';

describe('DeleteMiReportComponent', () => {
  let component: DeleteMiReportComponent;
  let fixture: ComponentFixture<DeleteMiReportComponent>;
  let miReportsUserDefinedService: jest.Mocked<MiReportsUserDefinedService>;
  let router: Router;

  const mockQuery: MiReportUserDefinedDTO = {
    reportName: 'Test Report',
    description: 'Test Description',
    queryDefinition: 'SELECT * FROM test',
  };

  beforeEach(async () => {
    const mockMiReportsUserDefinedService = mockClass(MiReportsUserDefinedService);
    mockMiReportsUserDefinedService.deleteMiReportUserDefined = jest.fn().mockReturnValue(of({}));

    const mockActivatedRoute = new ActivatedRouteStub({ queryId: '42' }, null, { query: mockQuery });

    await TestBed.configureTestingModule({
      imports: [DeleteMiReportComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: MiReportsUserDefinedService, useValue: mockMiReportsUserDefinedService },
      ],
    }).compileComponents();

    miReportsUserDefinedService = TestBed.inject(
      MiReportsUserDefinedService,
    ) as jest.Mocked<MiReportsUserDefinedService>;
    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate');

    fixture = TestBed.createComponent(DeleteMiReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have correct queryId from route', () => {
    expect(component['queryId']).toBe(42);
  });

  it('should display report name in heading', () => {
    const compiled = fixture.nativeElement;

    expect(compiled.textContent).toContain('Are you sure you want to delete the Test Report MI Report?');
  });

  it('should display warning message', () => {
    const compiled = fixture.nativeElement;

    expect(compiled.textContent).toContain('You will not be able to undo this action');
    expect(compiled.textContent).toContain('Your MI Report and all its data will be deleted permanently');
  });

  it('should display Delete MI Report caption', () => {
    const compiled = fixture.nativeElement;

    expect(compiled.textContent).toContain('Delete MI Report');
  });

  it('should delete report and navigate back on confirmation', () => {
    component.onDelete();

    expect(miReportsUserDefinedService.deleteMiReportUserDefined).toHaveBeenCalledWith(42);
    expect(router.navigate).toHaveBeenCalledWith(['../..'], {
      relativeTo: expect.anything(),
    });
  });

  it('should have delete button with warning style', () => {
    const compiled = fixture.nativeElement;
    const deleteButton = compiled.querySelector('button');

    expect(deleteButton).toBeTruthy();
    expect(deleteButton.textContent).toContain('Yes, delete the MI Report');
    expect(deleteButton.classList.contains('govuk-button--warning')).toBe(true);
  });

  it('should call onDelete when delete button is clicked', () => {
    jest.spyOn(component, 'onDelete');
    const compiled = fixture.nativeElement;
    const deleteButton = compiled.querySelector('button');

    deleteButton.click();

    expect(component.onDelete).toHaveBeenCalled();
  });

  it('should have Return to MI Reports link', () => {
    const compiled = fixture.nativeElement;
    const returnLink = compiled.querySelector('a.govuk-link');

    expect(returnLink).toBeTruthy();
    expect(returnLink.textContent).toContain('Return to: MI Reports');
  });
});

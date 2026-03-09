import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of, throwError } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { BasePage, mockClass } from '@netz/common/testing';

import { MiReportsUserDefinedService } from 'cca-api';

import { MiReportsExportService } from '../core/mi-reports-export.service';
import { mockCustomMiReportResult } from '../testing/mock-data';
import { CustomReportComponent } from './custom.component';

describe('CustomComponent', () => {
  let component: CustomReportComponent;
  let fixture: ComponentFixture<CustomReportComponent>;
  let page: Page;

  const miReportsUserDefinedService = mockClass(MiReportsUserDefinedService);
  const miReportsExportService = mockClass(MiReportsExportService);

  class Page extends BasePage<CustomReportComponent> {
    set queryValue(value: string) {
      this.setInputValue('#query', value);
    }

    get errorSummary() {
      return this.query<HTMLElement>('govuk-error-summary');
    }

    get errorSummaryErrors() {
      return this.queryAll<HTMLAnchorElement>('govuk-error-summary a');
    }

    get formErrorMessage() {
      return this.query<HTMLElement>('div[formcontrolname="query"] span.govuk-error-message');
    }

    get executeButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomReportComponent, PageHeadingComponent],
      providers: [
        provideRouter([]),
        { provide: MiReportsUserDefinedService, useValue: miReportsUserDefinedService },
        { provide: MiReportsExportService, useValue: miReportsExportService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CustomReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit a valid sql', () => {
    page.executeButton.click();
    fixture.detectChanges();

    miReportsUserDefinedService.generateCustomReport.mockReturnValueOnce(of(mockCustomMiReportResult));

    expect(page.formErrorMessage).toBeTruthy();

    page.queryValue = 'select * from account';
    page.executeButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(page.formErrorMessage).toBeFalsy();
    expect(miReportsUserDefinedService.generateCustomReport).toHaveBeenCalledTimes(1);
    expect(miReportsUserDefinedService.generateCustomReport).toHaveBeenCalledWith({
      sqlQuery: 'select * from account',
    });
  });

  it('should display error message when submitting an invalid sql', () => {
    jest.spyOn(miReportsUserDefinedService, 'generateCustomReport').mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            error: {
              code: 'REPORT1001',
              data: ['StatementCallback; bad SQL grammar [select * from (select * from accounts) t where 1=0];'],
              message: 'Custom query could not be executed',
            },
            status: 400,
          }),
      ),
    );

    page.queryValue = 'select * from accounts';
    page.executeButton.click();
    fixture.detectChanges();

    expect(miReportsUserDefinedService.generateCustomReport).toHaveBeenCalledTimes(1);
    expect(miReportsUserDefinedService.generateCustomReport).toHaveBeenCalledWith({
      sqlQuery: 'select * from accounts',
    });

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryErrors[0].textContent.trim()).toEqual('Unable to execute query');
  });
});

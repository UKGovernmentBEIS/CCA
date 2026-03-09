import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { BasePage, mockClass } from '@netz/common/testing';

import { MiReportsUserDefinedService } from 'cca-api';

import { MiReportsExportService } from './core/mi-reports-export.service';
import { MiReportsComponent } from './mi-reports.component';

describe('MiReportsComponent', () => {
  let component: MiReportsComponent;
  let fixture: ComponentFixture<MiReportsComponent>;
  let page: Page;

  class Page extends BasePage<MiReportsComponent> {
    get buttons(): HTMLAnchorElement[] {
      return Array.from(this.queryAll<HTMLAnchorElement>('a[govukButton]'));
    }

    get heading(): HTMLElement {
      return this.query<HTMLElement>('netz-page-heading');
    }
  }

  beforeEach(async () => {
    const mockMiReportsUserDefinedService = mockClass(MiReportsUserDefinedService);
    mockMiReportsUserDefinedService.getAllMiReportsUserDefined = jest
      .fn()
      .mockReturnValue(of({ queries: [], total: 0 }));

    const mockMiReportsExportService = mockClass(MiReportsExportService);

    await TestBed.configureTestingModule({
      imports: [MiReportsComponent, PageHeadingComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: MiReportsUserDefinedService, useValue: mockMiReportsUserDefinedService },
        { provide: MiReportsExportService, useValue: mockMiReportsExportService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MiReportsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display page heading', () => {
    expect(page.heading.textContent).toContain('MI Reports');
  });

  it('should have Custom report button', () => {
    const customButton = page.buttons.find((b) => b.textContent.includes('Custom report'));
    expect(customButton).toBeTruthy();
    expect(customButton.getAttribute('routerlink')).toBe('custom');
  });

  it('should have Create new report button', () => {
    const createButton = page.buttons.find((b) => b.textContent.includes('Create new report'));
    expect(createButton).toBeTruthy();
    expect(createButton.getAttribute('routerlink')).toBe('create-mi-report');
  });
});

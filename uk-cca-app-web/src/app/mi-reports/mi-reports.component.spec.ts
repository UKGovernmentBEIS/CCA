import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { miReportTypeDescriptionMap } from './core/mi-report';
import { MiReportsComponent } from './mi-reports.component';
import { MiReportsStore } from './store/mi-reports.store';

describe('MiReportsComponent', () => {
  let component: MiReportsComponent;
  let miReportsStore: MiReportsStore;
  let fixture: ComponentFixture<MiReportsComponent>;
  let page: Page;

  const miReports = [{ id: 1, miReportType: 'CUSTOM' }];

  class Page extends BasePage<MiReportsComponent> {
    get cells(): HTMLTableCellElement[] {
      return Array.from(this.queryAll<HTMLTableCellElement>('td'));
    }
  }

  const routeStub = new ActivatedRouteStub(null, null, {
    miReports: miReports,
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MiReportsComponent, PageHeadingComponent],
      providers: [{ provide: ActivatedRoute, useValue: routeStub }],
    }).compileComponents();
  });

  beforeEach(() => {
    miReportsStore = TestBed.inject(MiReportsStore);
    miReportsStore.setState(miReports);
    fixture = TestBed.createComponent(MiReportsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create table with expected content', () => {
    const cells = page.cells;
    expect(cells.length).toEqual(1);
    const reportDescriptions = cells.map((c) => c.textContent);
    const expectedDescriptions = miReports
      .map((r) => miReportTypeDescriptionMap[r.miReportType])
      .sort((a, b) => a.localeCompare(b));

    reportDescriptions.forEach((value, index) => {
      expect(value).toEqual(expectedDescriptions[index]);
    });
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import { BasePage } from '@netz/common/testing';
import { NestedMessageValidationError } from '@shared/components';

import { CsvErrorSummaryComponent } from './csv-error-summary.component';

describe('CsvErrorSummaryComponent', () => {
  let component: CsvErrorSummaryComponent;
  let fixture: ComponentFixture<CsvErrorSummaryComponent>;
  let page: Page;

  class Page extends BasePage<CsvErrorSummaryComponent> {
    get summaries() {
      return this.queryAll<HTMLParagraphElement>('li p').map((el) => el.textContent.trim());
    }
  }

  beforeEach(() => {
    fixture = TestBed.createComponent(CsvErrorSummaryComponent);
    component = fixture.componentInstance;
    component.errorList$ = of([
      {
        message: 'Test message',
        columns: ['Test columns'],
        rows: [
          {
            rowIndex: 1,
          },
          {
            rowIndex: 2,
          },
        ],
      },
    ] as NestedMessageValidationError[]);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.summaries).toEqual(['Test message', "Check the data in column 'Test columns' on rows 1, 2"]);
  });
});

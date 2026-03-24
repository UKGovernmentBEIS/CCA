import { type ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { getByRole, getByText } from '@testing';

import { summaryData } from './mock';
import { SummaryComponent } from './summary.component';

describe('Summary Component', () => {
  let fixture: ComponentFixture<SummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SummaryComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(SummaryComponent);
    fixture.componentRef.setInput('data', summaryData);
    await fixture.whenStable();
    fixture.detectChanges();
  });

  it('should render each section accordingly', () => {
    summaryData.forEach((section) => {
      expect(getByText(section.header)).toBeTruthy();
    });

    summaryData.forEach((section) => {
      section.data.forEach((item) => {
        expect(getByText(item.key)).toBeTruthy();
        expect(getByText(item.value[0])).toBeTruthy();
      });
    });
  });

  it('should render optional changeLink', () => {
    summaryData.forEach((section) => {
      section.data.forEach((item) => {
        if (item.change) {
          const itemRow = getByText(item.value[0]).closest('.govuk-summary-list__row');
          const link = getByRole('link', {}, itemRow as HTMLElement);
          expect((link as HTMLElement | null)?.textContent ?? '').toContain('Change');
          expect((link as Element | null)?.getAttribute('href')).toBe(`${section.changeLink}?change=true`);
        }
      });
    });
  });

  it('should handle change and prewrap options', () => {
    summaryData.forEach((section) => {
      section.data.forEach((item) => {
        if (item.preline) {
          expect(
            getByText(item.value[0]).closest('[govuksummarylistrowvalue]').classList.contains('pre-line'),
          ).toBeTruthy();
        }
      });
    });
  });

  it('should display diff styles', () => {
    summaryData.forEach((section) => {
      section.data.forEach((item) => {
        if (item.fieldDiff) {
          expect(getByText(item.value[0]).classList.contains('field-diff')).toBeTruthy();
        }
      });
    });
  });
});

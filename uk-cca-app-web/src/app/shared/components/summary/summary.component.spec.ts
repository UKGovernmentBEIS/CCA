import { render } from '@testing-library/angular';
import { getByRole, screen } from '@testing-library/dom';

import { summaryData } from './mock';
import { SummaryComponent } from './summary.component';

describe('Summary Component', () => {
  beforeEach(async () => {
    await render(SummaryComponent, { componentInputs: { data: summaryData } });
  });

  it('should render each section accordingly', async () => {
    summaryData.forEach((section) => {
      expect(screen.getByText(section.header)).toBeInTheDocument();
    });

    summaryData.forEach((section) => {
      section.data.forEach((item) => {
        expect(screen.getByText(item.key)).toBeInTheDocument();
        expect(screen.getByText(item.value[0])).toBeInTheDocument();
      });
    });
  });

  it('should render optional changeLink', async () => {
    summaryData.forEach((section) => {
      section.data.forEach((item) => {
        if (item.change) {
          const itemRow = screen.getByText(item.value[0]).closest('.govuk-summary-list__row');
          const link = getByRole(itemRow as HTMLElement, 'link');
          expect(link).toHaveTextContent('Change');
          expect(link).toHaveAttribute('href', `${section.changeLink}?change=true`);
        }
      });
    });
  });

  it('should handle change and prewrap options', async () => {
    summaryData.forEach((section) => {
      section.data.forEach((item) => {
        if (item.prewrap) {
          expect(screen.getByText(item.value[0]).classList.contains('pre-wrap')).toBeTruthy();
        }
      });
    });
  });
});

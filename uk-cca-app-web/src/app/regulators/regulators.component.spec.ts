import { HttpClientTestingModule } from '@angular/common/http/testing';

import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { RegulatorsComponent } from './regulators.component';

describe('RegulatorsComponent', () => {
  beforeEach(async () =>
    render(RegulatorsComponent, {
      imports: [HttpClientTestingModule],
    }),
  );

  it('should render', () => {
    expect(screen.getByText(/Regulator users and contacts/)).toBeInTheDocument();
  });

  it('should render all regulator tabs', () => {
    expect(screen.getByRole('tablist')).toBeInTheDocument();

    const tabs = screen.getAllByRole('tab');
    expect(tabs).toHaveLength(3);

    const tabHeaders = ['Regulator users', 'Site contacts', 'External contacts'];
    tabs.forEach((t, idx) => {
      expect(t).toContainElement(screen.getByText(tabHeaders[idx]));
    });
  });

  it('should render all tabs eagerly', () => {
    expect(document.getElementById('regulator-users')).toBeVisible();
    expect(document.querySelector('#site-contacts')).not.toBeInTheDocument();
    expect(document.querySelector('external-contacts')).not.toBeInTheDocument();
  });
});

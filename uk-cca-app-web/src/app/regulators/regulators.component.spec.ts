import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { getAllByRole, getByRole, getByText } from '@testing';

import { RegulatorsComponent } from './regulators.component';

describe('RegulatorsComponent', () => {
  let fixture: ComponentFixture<RegulatorsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegulatorsComponent],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(RegulatorsComponent);
    fixture.detectChanges();
  });

  it('should render', () => {
    expect(getByText(/Regulator users and contacts/)).toBeTruthy();
  });

  it('should render all regulator tabs', () => {
    expect(getByRole('tablist')).toBeTruthy();

    const tabs = getAllByRole('tab');
    expect(tabs).toHaveLength(3);

    const tabHeaders = ['Regulator users', 'Site contacts', 'External contacts'];
    tabs.forEach((t, idx) => {
      expect(t.textContent).toContain(tabHeaders[idx]);
    });
  });

  it('should render all tabs eagerly', () => {
    expect(document.getElementById('regulator-users')).toBeTruthy();
    expect(document.querySelector('#site-contacts')).toBeFalsy();
    expect(document.querySelector('external-contacts')).toBeFalsy();
  });
});

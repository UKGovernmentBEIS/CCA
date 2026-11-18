import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getAllByRole, getByText } from '@testing';

import { mockSectorDetails } from '../../specs/fixtures/mock';
import { ActiveSectorStore } from '../active-sector.store';
import { SectorDetailsTabComponent } from './sector-details-tab.component';

describe('SectorDetailsTabComponent', () => {
  let fixture: ComponentFixture<SectorDetailsTabComponent>;
  let store: ActiveSectorStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SectorDetailsTabComponent],
      providers: [ActiveSectorStore, { provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(ActiveSectorStore);
    store.setState(mockSectorDetails);

    fixture = TestBed.createComponent(SectorDetailsTabComponent);
    fixture.detectChanges();
  });

  it('should render "Sector details" and "Sector contact" titles', () => {
    expect(getByText('Sector details')).toBeTruthy();
    expect(getByText('Sector contact')).toBeTruthy();
  });

  it('should render "details" section', () => {
    const detailsList = document.querySelectorAll("[data-testid='details-list'] div");

    const elements = [];

    detailsList.forEach((div) => {
      elements.push([div.querySelector('dt').textContent, div.querySelector('dd').textContent]);
    });

    expect(elements).toEqual([
      ['Sector name', 'common'],
      ['Sector acronym', 'acronym'],
      ['Sector / Trade association name', 'legal'],
      ['Sector facilitator', 'facilitator name facilitator last name'],
      ['Address for service of notices', ' address 1 city 1  12345 '],
      ['Energy intensive or EPR', 'EPR'],
    ]);
  });

  it('should render "contacts" section', () => {
    const contactList = document.querySelectorAll("[data-testid='contact-list'] div");

    const elements = [];

    contactList.forEach((div) => {
      elements.push([div.querySelector('dt').textContent, div.querySelector('dd').textContent]);
    });

    expect(elements).toEqual([
      ['Title', 'Mr.'],
      ['First name', 'John'],
      ['Last name', 'Doe'],
      ['Job title', 'job title'],
      ['Organisation name', 'org name'],
      ['Address', ' address 1 city 1  12345 '],
      ['Phone number', '123456789'],
      ['Email address', 'johny@doe.com'],
    ]);
  });

  it('should render 11 change links', () => {
    expect(getAllByRole('link', { name: /Change/i }, fixture.nativeElement).length).toBe(11);
  });
});

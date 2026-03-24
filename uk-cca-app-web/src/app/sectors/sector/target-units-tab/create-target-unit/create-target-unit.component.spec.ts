import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { getByText } from '@testing';

import { mockSectorDetails, mockSectorScheme, mockSectorUserDetails } from '../../../specs/fixtures/mock';
import { ActiveSectorStore } from '../../active-sector.store';
import { CreateTargetUnitComponent } from './create-target-unit.component';
import { CreateTargetUnitStore } from './create-target-unit.store';
import { mockCreateTargetUnitState } from './specs/fixture/mocks';

describe('CreateTargetUnitComponent', () => {
  let component: CreateTargetUnitComponent;
  let fixture: ComponentFixture<CreateTargetUnitComponent>;
  let createTargetUnitStore: CreateTargetUnitStore;
  let sectorStore: ActiveSectorStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateTargetUnitComponent],
      providers: [
        CreateTargetUnitStore,
        ActiveSectorStore,
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({ sectorUserDetails: mockSectorUserDetails, canEditSectorUser: true }),
            snapshot: {
              paramMap: { get: jest.fn().mockReturnValue({ id: 1 }) },
              data: { subSectorScheme: mockSectorScheme },
            },
          },
        },
      ],
    }).compileComponents();

    createTargetUnitStore = TestBed.inject(CreateTargetUnitStore);
    createTargetUnitStore.setState(mockCreateTargetUnitState);
    sectorStore = TestBed.inject(ActiveSectorStore);
    sectorStore.setState(mockSectorDetails);
    fixture = TestBed.createComponent(CreateTargetUnitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(getByText('New target unit')).toBeTruthy();
    expect(getByText('Target unit details')).toBeTruthy();
  });

  it('should display the correct form fields', () => {
    expect(getByText('Operator type')).toBeTruthy();
    expect(getByText('Operator name')).toBeTruthy();
    expect(getByText('Standard Industrial Classification (SIC) codes (optional)')).toBeTruthy();
    expect(getByText('Subsector')).toBeTruthy();
  });

  it('should contain submit button and "return to" link', () => {
    expect(getByText('Continue')).toBeTruthy();
    expect(getByText('Return to: Sector target units')).toBeTruthy();
  });
});

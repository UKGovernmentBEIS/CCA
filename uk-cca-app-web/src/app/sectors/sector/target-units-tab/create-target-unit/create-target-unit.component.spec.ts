import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { screen } from '@testing-library/dom';

import { mockSectorDetails, mockSectorScheme, mockSectorUserDetails } from 'src/app/sectors/specs/fixtures/mock';

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
    expect(screen.getByText('New target unit')).toBeInTheDocument();
    expect(screen.getByText('Target unit details')).toBeInTheDocument();
  });

  it('should display the correct form fields', () => {
    expect(screen.getByText('Operator type')).toBeInTheDocument();
    expect(screen.getByText('Operator name')).toBeInTheDocument();
    expect(screen.getByText('Does your company have a registration number?')).toBeInTheDocument();
    expect(screen.getByText('Standard Industrial Classification (SIC) code (optional)')).toBeInTheDocument();
    expect(screen.getByText('Subsector')).toBeInTheDocument();
  });

  it('should contain submit button and "return to" link', () => {
    expect(screen.getByText('Continue')).toBeInTheDocument();
    expect(screen.getByText('Return to: Sector target units')).toBeInTheDocument();
  });
});

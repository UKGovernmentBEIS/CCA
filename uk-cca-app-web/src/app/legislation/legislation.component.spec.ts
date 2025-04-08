import { ComponentFixture, TestBed } from '@angular/core/testing';

import { screen } from '@testing-library/angular';

import { LegislationComponent } from './legislation.component';

describe('LegislationComponent', () => {
  let component: LegislationComponent;
  let fixture: ComponentFixture<LegislationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LegislationComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(LegislationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display appropriate contents', () => {
    expect(screen.getByText('Legislation')).toBeInTheDocument();

    expect(
      screen.getByText(
        'References are made throughout the service to the following pieces of legislation which set out specific terms of the scheme:',
      ),
    ).toBeInTheDocument();

    expect(screen.getByText('The Climate Change Agreements (Administration) Regulations 2012')).toBeInTheDocument();

    expect(
      screen.getByText('The Climate Change Agreements (Eligible Facilities) Regulations 2012'),
    ).toBeInTheDocument();

    expect(
      screen.getByText(
        'These regulations have been amended several times since their introduction. You should read both the original regulation and their subsequent amendments.',
      ),
    ).toBeInTheDocument();

    // This part should be split in order for `screen` of testing library to work.
    const textToMatch1 =
      /These regulations were made under the powers set out in paragraphs 52D-F of Schedule 6 of the/;

    const textToMatch2 = /Finance Act 2000/;

    expect(screen.getByText(textToMatch1)).toBeInTheDocument();
    expect(screen.getByText(textToMatch2)).toBeInTheDocument();
  });
});

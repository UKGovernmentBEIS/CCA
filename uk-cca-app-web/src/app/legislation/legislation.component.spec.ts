import { ComponentFixture, TestBed } from '@angular/core/testing';

import { getByText } from '@testing';

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
    expect(getByText('Legislation')).toBeTruthy();

    expect(
      getByText(
        'References are made throughout the service to the following pieces of legislation which set out specific terms of the scheme:',
      ),
    ).toBeTruthy();

    expect(getByText('The Climate Change Agreements (Administration) Regulations 2012')).toBeTruthy();

    expect(getByText('The Climate Change Agreements (Eligible Facilities) Regulations 2012')).toBeTruthy();

    expect(
      getByText(
        'These regulations have been amended several times since their introduction. You should read both the original regulation and their subsequent amendments.',
      ),
    ).toBeTruthy();

    // This part should be split for matching with getByText
    const textToMatch1 =
      /These regulations were made under the powers set out in paragraphs 52D-F of Schedule 6 of the/;

    const textToMatch2 = /Finance Act 2000/;

    expect(getByText(textToMatch1)).toBeTruthy();
    expect(getByText(textToMatch2)).toBeTruthy();
  });
});

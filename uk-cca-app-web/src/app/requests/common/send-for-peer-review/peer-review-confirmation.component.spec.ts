import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';

import { PeerReviewConfirmationComponent } from './peer-review-confirmation.component';

describe('PeerReviewConfirmationComponent', () => {
  let fixture: ComponentFixture<PeerReviewConfirmationComponent>;

  const candidateAssignees = [{ id: 'user1', firstName: 'Jane', lastName: 'Smith' }];

  const configureComponent = async (data = {}) => {
    await TestBed.configureTestingModule({
      imports: [PeerReviewConfirmationComponent],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              data,
              paramMap: convertToParamMap({ assigneeId: 'user1' }),
            },
            parent: {
              snapshot: {
                data: { candidateAssignees },
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PeerReviewConfirmationComponent);
    fixture.detectChanges();
  };

  afterEach(() => {
    TestBed.resetTestingModule();
  });

  it('should display the default peer review confirmation message', async () => {
    await configureComponent();

    expect(fixture.nativeElement.querySelector('.govuk-panel__body').textContent.trim()).toBe(
      'Sent to Jane Smith for peer review',
    );
  });

  it('should display a custom peer review confirmation message', async () => {
    await configureComponent({
      confirmationPrefix: 'Notice of Intent sent to',
      confirmationSuffix: ' for peer review.',
    });

    expect(fixture.nativeElement.querySelector('.govuk-panel__body').textContent.trim()).toBe(
      'Notice of Intent sent to Jane Smith for peer review.',
    );
  });
});

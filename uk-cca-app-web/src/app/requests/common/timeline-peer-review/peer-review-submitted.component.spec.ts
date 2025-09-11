import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { PeerReviewSubmittedComponent } from '@requests/common';

import { mockPeerReviewActionState } from './testing/mock-data';

describe('AdminTerminationPeerReviewSubmittedComponent', () => {
  let component: PeerReviewSubmittedComponent;
  let fixture: ComponentFixture<PeerReviewSubmittedComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PeerReviewSubmittedComponent],
      providers: [
        RequestActionStore,
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(),
        },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState(mockPeerReviewActionState);

    fixture = TestBed.createComponent(PeerReviewSubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(fixture).toMatchSnapshot();
  });
});

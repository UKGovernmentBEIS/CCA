import { nonFacilitySections, TaskItemStatus, UNAVariationRequestTaskPayload } from '@requests/common';
import { produce } from 'immer';

export function initializeUnderlyingAgreementVariationSubmitPayload(
  currentPayload: UNAVariationRequestTaskPayload,
): UNAVariationRequestTaskPayload {
  return produce(currentPayload, (payload) => {
    // We need to pre-populate the review sections as UNCHANGED, so that only the changes
    // applied from the sector user's variation can change them to UNDECIDED.
    [...nonFacilitySections].forEach((section) => {
      payload.sectionsCompleted[section] = !payload.sectionsCompleted[section]
        ? TaskItemStatus.UNCHANGED
        : payload.sectionsCompleted[section];

      if (!payload.reviewSectionsCompleted[section]) {
        payload.reviewSectionsCompleted[section] = TaskItemStatus.UNCHANGED;
      }
    });

    payload.originalUnderlyingAgreementContainer.underlyingAgreement.facilities.forEach((f) => {
      // pre-populate reviewSectionsCompleted for facilities
      if (!payload.reviewSectionsCompleted[f.facilityId])
        payload.reviewSectionsCompleted[f.facilityId] = TaskItemStatus.UNCHANGED;

      payload.sectionsCompleted[f.facilityId] = !payload.sectionsCompleted[f.facilityId]
        ? TaskItemStatus.UNCHANGED
        : payload.sectionsCompleted[f.facilityId];
    });
  });
}

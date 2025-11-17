import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'adminTerminationReason',
})
export class PeerReviewDecisionPipe implements PipeTransform {
  transform(decisionType: 'AGREE' | 'DISAGREE') {
    if (!decisionType) throw new Error('The decision should only be agree or disagree');
    return decisionType === 'AGREE' ? 'I agree with the determination' : 'I disagree with the determination';
  }
}

import { Pipe, PipeTransform } from '@angular/core';

import { SectorAssociationDetailsResponseDTO } from 'cca-api';

@Pipe({ name: 'sectorEnergyEprFactor' })
export class SectorEnergyEprFactorPipe implements PipeTransform {
  transform(value: SectorAssociationDetailsResponseDTO['energyIntensiveOrEPR']): string {
    switch (value) {
      case 'ENVIRONMENTAL_PERMITTING_REGULATIONS':
        return 'EPR';

      case 'ENERGY_INTENSIVE':
        return 'Energy intensive';
    }

    throw new Error('invalid sector energy epr factor type');
  }
}

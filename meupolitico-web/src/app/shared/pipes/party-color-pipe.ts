import { Pipe, PipeTransform } from '@angular/core';
import { getPartyColor } from '../../core/utils/party-colors';

@Pipe({
  name: 'partyColor',
  standalone: true
})
export class PartyColorPipe implements PipeTransform {
  transform(sigla: string | null | undefined): string {
    return getPartyColor(sigla);
  }
}

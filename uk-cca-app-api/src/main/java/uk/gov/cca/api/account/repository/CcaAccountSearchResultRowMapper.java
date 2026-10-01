package uk.gov.cca.api.account.repository;

import uk.gov.cca.api.account.domain.dto.CcaAccountSearchResultInfoDTO;
import uk.gov.netz.api.account.domain.dto.AccountSearchResults;
import uk.gov.netz.api.account.search.query.AccountSearchQueryResultsMapper;
import uk.gov.netz.api.account.search.query.AccountSearchResultRow;

import java.util.List;

public class CcaAccountSearchResultRowMapper implements AccountSearchQueryResultsMapper<AccountSearchResultRow, CcaAccountSearchResultInfoDTO> {

    @Override
    public AccountSearchResults<CcaAccountSearchResultInfoDTO> toResults(List<AccountSearchResultRow> rows, long total) {
        return AccountSearchResults.<CcaAccountSearchResultInfoDTO>builder()
                .accounts(rows.stream().map(this::toDto).toList())
                .total(total)
                .build();
    }

    @Override
    public AccountSearchResults<CcaAccountSearchResultInfoDTO> emptyResults() {
        return null;
    }

    CcaAccountSearchResultInfoDTO toDto(AccountSearchResultRow row) {
        return new CcaAccountSearchResultInfoDTO(
                row.getId(),
                row.getName(),
                row.getBusinessId(),
                row.getStatus());
    }
}

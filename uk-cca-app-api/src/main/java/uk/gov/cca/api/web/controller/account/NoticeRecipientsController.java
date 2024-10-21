package uk.gov.cca.api.web.controller.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.cca.api.account.domain.dto.AdditionalNoticeRecipientDTO;
import uk.gov.cca.api.web.orchestrator.account.service.NoticeRecipientsServiceOrchestrator;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.security.AuthorizedRole;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.util.List;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@RestController
@RequestMapping(path = "/v1.0/account-notice")
@RequiredArgsConstructor
@Tag(name = "Notice recipients")
public class NoticeRecipientsController {

    private final NoticeRecipientsServiceOrchestrator noticeRecipientsServiceOrchestrator;

    @GetMapping(path = "/{accountId}/additional-recipients")
    @Operation(summary = "Retrieves the additional Operators and the Sector users")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = AdditionalNoticeRecipientDTO.class))))
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @AuthorizedRole(roleType = REGULATOR)
    public ResponseEntity<List<AdditionalNoticeRecipientDTO>> getAdditionalNoticeRecipients(
            @Parameter(hidden = true) AppUser appUser,
            @PathVariable("accountId") @Parameter(description = "The target unit account id") Long accountId) {
        return new ResponseEntity<>(noticeRecipientsServiceOrchestrator.getAdditionalNoticeRecipients(appUser, accountId), HttpStatus.OK);
    }
}

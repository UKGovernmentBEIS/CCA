package uk.gov.cca.api.web.controller.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.UpdateTargetUnitAccountFinancialIndependenceStatusCodeDTO;
import uk.gov.cca.api.account.domain.dto.UpdateTargetUnitAccountResponsiblePersonDTO;
import uk.gov.cca.api.account.domain.dto.UpdateTargetUnitAccountSicCodeDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountUpdateService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.security.Authorized;

@Validated
@RestController
@RequestMapping(path = "/v1.0/target-unit-accounts/{accountId}/update/")
@RequiredArgsConstructor
@Tag(name = "Update Target Unit Account")
public class TargetUnitAccountUpdateController {

    private final TargetUnitAccountUpdateService targetUnitAccountUpdateService;

    @PatchMapping(path = "/sic")
    @Operation(summary = "Updates TargetUnit Account Sic codes")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.UPDATE_TARGET_UNIT_ACCOUNT_ERROR_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> updateTargetUnitAccountSicCode(
            @PathVariable("accountId") @Parameter(description = "The account id")
            Long accountId,
            @RequestBody @Valid @Parameter(description = "The updated data for sic codes", required = true)
            UpdateTargetUnitAccountSicCodeDTO updateTargetUnitAccountSicCodeDTO) {

        targetUnitAccountUpdateService.updateTargetUnitAccountSicCodes(accountId, updateTargetUnitAccountSicCodeDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(path = "/financial")
    @Operation(summary = "Updates TargetUnit Account financial independenceStatus code")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.UPDATE_TARGET_UNIT_ACCOUNT_ERROR_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> updateTargetUnitAccountFinancialIndependenceStatusCode(
            @PathVariable("accountId") @Parameter(description = "The account id")
            Long accountId,
            @RequestBody @Valid @Parameter(description = "The updated data for financial independenceStatus code", required = true)
            UpdateTargetUnitAccountFinancialIndependenceStatusCodeDTO updateTargetUnitAccountFinancialIndependenceStatusCodeDTO) {

        targetUnitAccountUpdateService.updateTargetUnitAccountFinancialIndependenceStatusCode(accountId, updateTargetUnitAccountFinancialIndependenceStatusCodeDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(path = "/responsible")
    @Operation(summary = "Updates TargetUnitAccount Responsible contact details")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.UPDATE_TARGET_UNIT_ACCOUNT_ERROR_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> updateTargetUnitAccountResponsiblePerson(
            @PathVariable("accountId") @Parameter(description = "The account id")
            Long accountId,
            @RequestBody @Valid @Parameter(description = "The updated data for responsible person", required = true)
            UpdateTargetUnitAccountResponsiblePersonDTO updateTargetUnitAccountResponsiblePersonDTO) {

        targetUnitAccountUpdateService.updateTargetUnitAccountResponsiblePerson(accountId, updateTargetUnitAccountResponsiblePersonDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(path = "/administrative")
    @Operation(summary = "Updates TargetUnitAccount Administrative contact details")
    @ApiResponse(responseCode = "204", description = SwaggerApiInfo.NO_CONTENT)
    @ApiResponse(responseCode = "400", description = SwaggerApiInfo.UPDATE_TARGET_UNIT_ACCOUNT_ERROR_BAD_REQUEST,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR,
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> updateTargetUnitAccountAdministrativePerson(
            @PathVariable("accountId") @Parameter(description = "The account id")
            Long accountId,
            @RequestBody @Valid @Parameter(description = "The updated data for administrative person", required = true)
            TargetUnitAccountContactDTO updateTargetUnitAccountContactDTO) {

        targetUnitAccountUpdateService.updateTargetUnitAccountAdministrativePerson(accountId, updateTargetUnitAccountContactDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

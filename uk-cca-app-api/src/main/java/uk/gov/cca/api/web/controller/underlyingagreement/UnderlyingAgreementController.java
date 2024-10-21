package uk.gov.cca.api.web.controller.underlyingagreement;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementDocumentService;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.netz.api.security.Authorized;
import uk.gov.netz.api.token.FileToken;

@RestController
@RequestMapping(path = "/v1.0/underlying-agreements/")
@RequiredArgsConstructor
@Tag(name = "Underlying Agreements")
public class UnderlyingAgreementController {
    
    private final UnderlyingAgreementDocumentService underlyingAgreementDocumentService;
    
    @GetMapping(path = "/{id}/document")
    @Operation(summary = "Generate the token to get the document that belongs to the provided underlying agreement id")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FileToken.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @Authorized(resourceId = "#underlyingAgreementId")
    public ResponseEntity<FileToken> generateGetUnderlyingAgreementDocumentToken(
            @PathVariable("id") @Parameter(name = "id", description = "The underlying agreement id") @NotNull Long underlyingAgreementId,
            @RequestParam("fileDocumentUuid") @Parameter(name = "fileDocumentUuid", description = "The document uuid") @NotNull UUID fileDocumentUuid) {

        FileToken getFileAttachmentToken = underlyingAgreementDocumentService.generateGetFileDocumentToken(underlyingAgreementId, fileDocumentUuid);
        return new ResponseEntity<>(getFileAttachmentToken, HttpStatus.OK);
    }

}

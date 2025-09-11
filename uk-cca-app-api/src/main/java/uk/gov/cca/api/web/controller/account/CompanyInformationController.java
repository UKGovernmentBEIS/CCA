package uk.gov.cca.api.web.controller.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.domain.dto.CompanyProfileInfo;
import uk.gov.cca.api.account.domain.dto.CompanyProfileDTO;
import uk.gov.cca.api.account.transform.CompanyInformationMapper;
import uk.gov.cca.api.web.constants.SwaggerApiInfo;
import uk.gov.cca.api.web.controller.exception.ErrorResponse;
import uk.gov.netz.api.companieshouse.CompanyInformationService;

import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/v1.0/company-information/")
@RequiredArgsConstructor
@Tag(name = "Companies information")
public class CompanyInformationController {

    private final CompanyInformationService companyInformationService;
    private final CompanyInformationMapper mapper = Mappers.getMapper(CompanyInformationMapper.class);

    @GetMapping("/{registrationNumber}")
    @Operation(summary = "Retrieves information about the company that corresponds to the provided registration number")
    @ApiResponse(responseCode = "200", description = SwaggerApiInfo.OK, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CompanyProfileDTO.class))})
    @ApiResponse(responseCode = "403", description = SwaggerApiInfo.FORBIDDEN, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "404", description = SwaggerApiInfo.NOT_FOUND, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "500", description = SwaggerApiInfo.INTERNAL_SERVER_ERROR, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    @ApiResponse(responseCode = "503", description = SwaggerApiInfo.GET_COMPANY_PROFILE_SERVICE_UNAVAILABLE, content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))})
    public ResponseEntity<CompanyProfileDTO> getCompanyProfileByRegistrationNumber(
        @Parameter(description = "The registration number") @PathVariable("registrationNumber") @NotBlank @Size(max = 255) String registrationNumber) {
    	CompanyProfileInfo companyProfile = companyInformationService.getCompanyProfile(registrationNumber, CompanyProfileInfo.class);
        return new ResponseEntity<>(mapper.toCompanyProfileDTO(companyProfile), HttpStatus.OK);
    }
}

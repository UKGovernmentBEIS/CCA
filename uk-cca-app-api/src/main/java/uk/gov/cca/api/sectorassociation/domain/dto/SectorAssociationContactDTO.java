package uk.gov.cca.api.sectorassociation.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The sector association contact details DTO.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SectorAssociationContactDTO {

    /** The title of the contact. */
    @Size(max = 20, message = "{contact.title.size}")
    private String title;

    /** The first name of the contact. */
    @NotBlank(message = "{contact.firstName.notEmpty}")
    @Size(max = 255, message = "{contact.firstName.size}")
    private String firstName;

    /** The last name of the contact. */
    @NotBlank(message = "{contact.lastName.notEmpty}")
    @Size(max = 255, message = "{contact.lastName.size}")
    private String lastName;

    /** The job title of the contact. */
    @Size(max = 255, message = "{contact.jobTitle.size}")
    private String jobTitle;

    /** The name of the organisation associated with the contact. */
    @Size(max = 255, message = "{contact.organisationName.size}")
    private String organisationName;

    /** The address details of the contact. */
    @NotNull(message = "{contact.address.notNull}")
    @Valid
    private AddressDTO address;

    /** The phone number of the contact. */
    @Size(max = 255, message = "{contact.phoneNumber.size}")
    private String phoneNumber;

    /** The email address of the contact. */
    @NotBlank(message = "{contact.email.notEmpty}")
    @Size(max = 255, message = "{contact.email.size}")
    private String email;
}

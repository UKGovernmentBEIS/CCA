package uk.gov.cca.api.account.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "account_target_unit_contact")
public class TargetUnitAccountContact {

    @Id
    @SequenceGenerator(name = "account_target_unit_contact_id_generator", sequenceName = "account_target_unit_contact_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_target_unit_contact_id_generator")
    private Long id;

    @Email
    @NotBlank
    @Column(name = "email")
    private String email;

    @NotBlank
    @Column(name = "firstname")
    private String firstName;

    @NotBlank
    @Column(name = "lastname")
    private String lastName;

    @Column(name = "job_title")
    private String jobTitle;

    @EqualsAndHashCode.Include()
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "contact_type")
    private TargetUnitAccountContactType contactType;

    @EqualsAndHashCode.Include
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_target_unit_id")
    private TargetUnitAccount targetUnitAccount;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private AccountAddress address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "phone_code")
    private String phoneCode;
}

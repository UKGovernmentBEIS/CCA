package uk.gov.cca.api.sectorassociation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.files.common.domain.FileEntity;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@SequenceGenerator(name = "default_file_id_generator", sequenceName = "sector_association_scheme_document_id_seq", allocationSize = 1)
@Table(name = "sector_association_scheme_document")
public class SectorAssociationSchemeDocument extends FileEntity {
}
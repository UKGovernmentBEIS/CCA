package uk.gov.cca.api.sectorassociation.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.LockMode;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;

import java.util.Optional;

@Repository
public class SectorAssociationRepositoryImpl implements SectorAssociationCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    public Optional<SectorAssociation> findByIdForUpdate(Long id) {
        return ((Query<SectorAssociation>)entityManager.createQuery("select sa from SectorAssociation sa where sa.id = :id"))
                .setLockMode("ac", LockMode.PESSIMISTIC_WRITE)
                .setTimeout(5000)
                .setParameter("id", id)
                .uniqueResultOptional();
    }
}

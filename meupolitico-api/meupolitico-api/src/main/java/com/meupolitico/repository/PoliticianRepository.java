package com.meupolitico.repository;

import com.meupolitico.entity.Politician;
import com.meupolitico.enums.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PoliticianRepository extends JpaRepository<Politician, Long> {

    Optional<Politician> findByExternalId(String externalId);

    List<Politician> findByName(String name);
    List<Politician> findByNameContainingIgnoreCase(String name);

    List<Politician> findByBallotName(String ballotName);
    List<Politician> findByBallotNameContainingIgnoreCase(String ballotName);

    List<Politician> findByPhotoUrl(String photoUrl);

    List<Politician> findByParty(String party);
    List<Politician> findByPartyIgnoreCase(String party);

    List<Politician> findByState(String state);
    List<Politician> findByStateIgnoreCase(String state);

    List<Politician> findByPosition(String position);
    List<Politician> findByPositionContainingIgnoreCase(String position);

    List<Politician> findByStatus(String status);
    List<Politician> findByStatusIgnoreCase(String status);

    List<Politician> findByBirthDate(LocalDate birthDate);

    List<Politician> findByGender(Gender gender);

    List<Politician> findByPartyAndState(String party, String state);
    List<Politician> findByStateAndPosition(String state, String position);
    List<Politician> findByPartyAndPosition(String party, String position);
}
package com.meupolitico.repository;

import com.meupolitico.entity.Vote;
import com.meupolitico.enums.VoteChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long>, JpaSpecificationExecutor<Vote> {

    List<Vote> findByPoliticianId(Long politicianId);

    List<Vote> findByVote(VoteChoice vote);

    List<Vote> findByDate(LocalDate date);

    List<Vote> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
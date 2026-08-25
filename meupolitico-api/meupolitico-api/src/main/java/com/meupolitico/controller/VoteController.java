package com.meupolitico.controller;

import com.meupolitico.dto.request.VoteRequest;
import com.meupolitico.dto.response.VoteResponse;
import com.meupolitico.enums.VoteChoice;
import com.meupolitico.service.VoteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @GetMapping
    public ResponseEntity<List<VoteResponse>> findAll() {
        return ResponseEntity.ok(voteService.findAll());
    }

    @GetMapping("/politician/{politicianId}")
    public ResponseEntity<List<VoteResponse>> findByPoliticianId(@PathVariable Long politicianId) {
        return ResponseEntity.ok(voteService.findByPoliticianId(politicianId));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<VoteResponse>> search(
            @RequestParam(required = false) Long politicianId,
            @RequestParam(required = false) VoteChoice vote,
            @RequestParam(required = false) String proposition,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                voteService.search(politicianId, vote, proposition, startDate, endDate, pageable)
        );
    }

    @GetMapping("/search/vote")
    public ResponseEntity<List<VoteResponse>> findByVote(@RequestParam VoteChoice vote) {
        return ResponseEntity.ok(voteService.findByVote(vote));
    }

    @GetMapping("/search/date")
    public ResponseEntity<List<VoteResponse>> findByDate(@RequestParam LocalDate date) {
        return ResponseEntity.ok(voteService.findByDate(date));
    }

    @GetMapping("/search/date-range")
    public ResponseEntity<List<VoteResponse>> findByDateRange(@RequestParam LocalDate start,
                                                              @RequestParam LocalDate end) {
        return ResponseEntity.ok(voteService.findByDateRange(start, end));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoteResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(voteService.findById(id));
    }

    @PostMapping
    public ResponseEntity<VoteResponse> create(@Valid @RequestBody VoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(voteService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VoteResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody VoteRequest request) {
        return ResponseEntity.ok(voteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        voteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
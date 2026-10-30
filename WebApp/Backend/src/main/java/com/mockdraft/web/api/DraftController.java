package com.mockdraft.web.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mockdraft.core.draft.DraftState;
import com.mockdraft.core.service.DraftService;
import com.mockdraft.core.service.ValidationException;
import com.mockdraft.web.api.Dtos.CreateDraftRequest;
import com.mockdraft.web.api.Dtos.DraftStateDto;
import com.mockdraft.web.api.Dtos.DraftSummaryDto;
import com.mockdraft.web.api.Dtos.PickRequest;
import com.mockdraft.web.api.Dtos.PlayerDto;
import com.mockdraft.web.api.Dtos.RosterDto;
import com.mockdraft.web.security.RateLimits;

/**
 * Draft endpoints. The acting user always comes from the authenticated session; ownership of each draft is
 * enforced in {@link DraftService} (other users' drafts look like 404s).
 */
@RestController
@RequestMapping("/api/drafts")
public class DraftController {

    private final DraftService drafts;
    private final RateLimits rateLimits;

    public DraftController(DraftService drafts, RateLimits rateLimits) {
        this.drafts = drafts;
        this.rateLimits = rateLimits;
    }

    @GetMapping
    public List<DraftSummaryDto> list(@AuthenticationPrincipal String username) {
        return drafts.list(username).stream().map(DraftSummaryDto::of).toList();
    }

    @PostMapping
    public ResponseEntity<DraftStateDto> create(@AuthenticationPrincipal String username, @RequestBody CreateDraftRequest body) {
        rateLimits.draftWrite(username);
        if (body.numTeams() == null) {
            throw new ValidationException("numTeams", "League size is required.");
        }
        DraftState state = drafts.create(username, body.teamName(), body.numTeams(), body.draftPosition());
        return ResponseEntity.status(HttpStatus.CREATED).body(DraftStateDto.of(state));
    }

    @GetMapping("/{id}")
    public DraftStateDto get(@AuthenticationPrincipal String username, @PathVariable long id) {
        return DraftStateDto.of(drafts.get(username, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal String username, @PathVariable long id) {
        rateLimits.draftWrite(username);
        drafts.delete(username, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/available")
    public List<PlayerDto> available(@AuthenticationPrincipal String username, @PathVariable long id) {
        DraftState state = drafts.get(username, id);
        int n = state.draft().numTeams();
        return drafts.available(state).stream().map(p -> PlayerDto.of(p, n)).toList();
    }

    @PostMapping("/{id}/picks")
    public DraftStateDto pick(@AuthenticationPrincipal String username, @PathVariable long id, @RequestBody PickRequest body) {
        rateLimits.draftWrite(username);
        if (body.playerRank() == null) {
            throw new ValidationException("playerRank", "Choose a player.");
        }
        return DraftStateDto.of(drafts.makeUserPick(username, id, body.playerRank()));
    }

    @PostMapping("/{id}/auto-pick")
    public DraftStateDto autoPick(@AuthenticationPrincipal String username, @PathVariable long id) {
        rateLimits.draftWrite(username);
        return DraftStateDto.of(drafts.autoPick(username, id));
    }

    @PostMapping("/{id}/simulate")
    public DraftStateDto simulate(@AuthenticationPrincipal String username, @PathVariable long id) {
        rateLimits.draftWrite(username);
        return DraftStateDto.of(drafts.simulateToUserTurn(username, id));
    }

    @GetMapping("/{id}/teams/{slot}/roster")
    public RosterDto roster(@AuthenticationPrincipal String username, @PathVariable long id, @PathVariable int slot) {
        DraftState state = drafts.get(username, id);
        return RosterDto.of(drafts.roster(username, id, slot), state.draft().numTeams());
    }
}

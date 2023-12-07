package com.samedtek.conferenceboard.controller;

import com.samedtek.conferenceboard.model.payload.ConferencePayload;
import com.samedtek.conferenceboard.model.response.ResponseMessage;
import com.samedtek.conferenceboard.service.ConferenceBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/")
public class ConferenceBoardController {

    private final ConferenceBoardService conferenceBoardService;

    @Autowired
    public ConferenceBoardController(ConferenceBoardService conferenceBoardService) {
        this.conferenceBoardService = conferenceBoardService;
    }

    @Operation(summary = "Schedule Conference Presentations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schedule conference presentations is successful.",
                    content = {@Content(mediaType = "application/json")})})
    @PostMapping("schedule-conference-board")
    public ResponseEntity<?> scheduleConferenceBoard(@Valid @RequestBody ConferencePayload conferencePayload) {
        conferenceBoardService.scheduleConferenceBoard(conferencePayload);
        return ResponseEntity.ok().body(new ResponseMessage("Conference Board successfully created"));
    }

    @Operation(summary = "Get Scheduled Conferences")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Scheduled conferences successfully get.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "404", description = "Could not be found any conference",
                    content = @Content)})
    @GetMapping("get-conference-board")
    public ResponseEntity<?> getConferenceBoard() {
        String response = conferenceBoardService.getConfereneBoard();
        if (StringUtils.isBlank(response)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Could not be found any conference"));
        } else {
            return ResponseEntity.ok().body(conferenceBoardService.getConfereneBoard());
        }
    }

    @Operation(summary = "Delete All Scheduled Conferences")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Scheduled conferences are successfully deleted.",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "Could not be found any conference",
                    content = @Content)})
    @DeleteMapping("reset-conference-board")
    public ResponseEntity<?> resetConferenceBoard() {
        conferenceBoardService.resetConferenceBoard();
        return ResponseEntity.ok().body(new ResponseMessage("Conference Board successfully reset"));
    }

}

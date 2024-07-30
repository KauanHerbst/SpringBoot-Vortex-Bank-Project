package com.herbst.vortexbank.v1.controllers;

import com.herbst.vortexbank.util.MediaType;
import com.herbst.vortexbank.v1.dtos.*;
import com.herbst.vortexbank.v1.services.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@Tag(name = "Wallet Controller V1", description = "Endpoints V1 for Wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping(value = "/wallet/password",  consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Create Wallet Password", description = "Endpoint for create wallet password", responses = {
            @ApiResponse(description = "Accepted", responseCode = "202", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
    })
    public ResponseEntity<StandardResponseDTO> createWalletPassword(@RequestBody CreateWalletPasswordDTO data){
        StandardResponseDTO dto = walletService.createWalletPassword(data);
        return ResponseEntity.status(dto.getStatus()).body(dto);
    }

    @PostMapping(value = "/wallet/transaction",  consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
            MediaType.APPLICATION_YML}, produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Transaction Endpoint", description = "Endpoint for transactions in wallet", responses = {
            @ApiResponse(description = "Success", responseCode = "201", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionDTO.class)),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = TransactionDTO.class)),
                    @Content(mediaType = "application/x-yaml", schema = @Schema(implementation = TransactionDTO.class))
            }),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
    })
   public ResponseEntity<TransactionDTO> transaction(@Valid @RequestBody TransactionReceivedDTO data){
        return ResponseEntity.ok(walletService.transaction(data));
   }

    @GetMapping(value = "/wallet/transaction/{transactionId}", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Get Transaction", description = "Endpoint for get transactions in wallet", responses = {
            @ApiResponse(description = "Success", responseCode = "201", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionDTO.class)),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = TransactionDTO.class)),
                    @Content(mediaType = "application/x-yaml", schema = @Schema(implementation = TransactionDTO.class))
            }),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
    })
   public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable String transactionId){
        return ResponseEntity.ok(walletService.getTransaction(transactionId));
   }

}

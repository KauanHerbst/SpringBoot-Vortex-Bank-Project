package com.herbst.vortexbank.v1.controllers;

import com.herbst.vortexbank.util.MediaType;
import com.herbst.vortexbank.v1.dtos.*;
import com.herbst.vortexbank.v1.services.AccountService;
import com.herbst.vortexbank.v1.services.AuthService;
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
@Tag(name = "Account Controller V1", description = "Endpoints V1 for Accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/account", consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Create Account", description = "Endpoint for create Account", responses = {
            @ApiResponse(description = "Success", responseCode = "201", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDTO.class)),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = AccountDTO.class)),
                    @Content(mediaType = "application/x-yaml", schema = @Schema(implementation = AccountDTO.class))
            }),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
    })
    public ResponseEntity<AccountDTO> create(@RequestBody CreateAccountDTO accountDataDTO) throws Exception {
        return ResponseEntity.ok(authService.create(accountDataDTO));
    }
    @PostMapping(value = "/account/password", consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Create Account Password", description = "Endpoint for create account password", responses = {
            @ApiResponse(description = "Accepted", responseCode = "202", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
    })
    public ResponseEntity<StandardResponseDTO> createAccountPassword(@RequestBody CreateAccountPasswordDTO accountPasswordDTO){
        StandardResponseDTO dto = accountService.createAccountPassword(accountPasswordDTO);
        return ResponseEntity.status(dto.getStatus()).body(dto);
    }

    @PostMapping(value = "/account/signin", consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Sign in", description = "Endpoint for Sign in", responses = {
            @ApiResponse(description = "Success", responseCode = "201", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TokenDTO.class)),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = TokenDTO.class)),
                    @Content(mediaType = "application/x-yaml", schema = @Schema(implementation = TokenDTO.class))
            }),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
    })
    public ResponseEntity<TokenDTO> signin(@Valid @RequestBody AccountSignInDTO accountSignInDTO){
        TokenDTO tokenDTO = authService.signin(accountSignInDTO);
        return ResponseEntity.ok().body(tokenDTO);
    }

    @PostMapping(value = "/account/newpassword", consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Change Account Password", description = "Endpoint for change account password", responses = {
            @ApiResponse(description = "Success", responseCode = "201", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
    })
    public ResponseEntity<StandardResponseDTO> changeAccountPassword(@RequestBody ChangeAccountPasswordDTO changeAccountPasswordDTO){
        StandardResponseDTO dto = accountService.changeAccountPassword(changeAccountPasswordDTO);
        return ResponseEntity.status(dto.getStatus()).body(dto);
    }

    @PostMapping(value = "/account/search", consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Search Account", description = "Endpoint for Search Account", responses = {
            @ApiResponse(description = "Success", responseCode = "201", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AccountTransactionDTO.class)),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = AccountTransactionDTO.class)),
                    @Content(mediaType = "application/x-yaml", schema = @Schema(implementation = AccountTransactionDTO.class))
            }),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
    })
    public ResponseEntity searchAccountForTransaction(@RequestBody AccountAddresseeDTO data){
        return ResponseEntity.ok(accountService.searchAccountForTransaction(data));
    }

    @GetMapping(value = "/account/{accountId}",
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Get Account", description = "Endpoint for Get Account", responses = {
            @ApiResponse(description = "Success", responseCode = "201", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDTO.class)),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = AccountDTO.class)),
                    @Content(mediaType = "application/x-yaml", schema = @Schema(implementation = AccountDTO.class))
            }),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
    })
    public ResponseEntity<AccountWithoutDetailsDTO> getAccount(@PathVariable Long accountId){
        return ResponseEntity.ok(accountService.getAccount(accountId));
    }
}

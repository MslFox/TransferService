package com.example.transfer.controller;

import com.example.transfer.model.ConfirmRequest;
import com.example.transfer.model.TransferRequest;
import com.example.transfer.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin(origins = {"http://localhost:3000",  "https://mslfox.github.io/"})
@RestController
public class TransactionController {
    private final TransferService transferService;

    public TransactionController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<Object> transfers(@RequestBody @Valid TransferRequest transferRequest) {
        return transferService.getTransferResponse(transferRequest);
    }

    @PostMapping("/confirmOperation")
    public ResponseEntity<Object> confirmOperation(@Valid @RequestBody ConfirmRequest confirmRequest) {
        return transferService.getConfirmedResponse(confirmRequest);
    }
}

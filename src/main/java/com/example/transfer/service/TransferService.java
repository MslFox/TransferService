package com.example.transfer.service;

import com.example.transfer.exception.TransactionException;
import com.example.transfer.model.AllowedTransferBody;
import com.example.transfer.model.ConfirmRequest;
import com.example.transfer.model.ErrorMessage;
import com.example.transfer.model.TransferRequest;
import com.example.transfer.repository.UnconfirmedTransferRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Log
@Service
public class TransferService {

    @Value("${transfer.commission:0.01}")
    private double commission;
    private final UnconfirmedTransferRepository repository;

    public TransferService(UnconfirmedTransferRepository repository) {
        this.repository = repository;
    }

    public ResponseEntity<Object> getTransferResponse(TransferRequest transferRequest) {
        return getTransferResponseId(makeId(), transferRequest);
    }

    public ResponseEntity<Object> getTransferResponseId(String id, TransferRequest transferRequest) {
        repository.addTransferAndGetId(id, transferRequest)
                .orElseThrow(() -> new TransactionException(ErrorMessage.DUPLICATE_TRANSACTION));
        return ResponseEntity.ok().body(new AllowedTransferBody(id));
    }

    public ResponseEntity<Object> getConfirmedResponse(ConfirmRequest confirmRequest) {
        return getConfirmedResponseWithLog(repository.getTransfer(confirmRequest.getOperationId()), confirmRequest);
    }

    public ResponseEntity<Object> getConfirmedResponseWithLog(Optional<TransferRequest> optionalTransferRequest, ConfirmRequest confirmRequest) {
        optionalTransferRequest.orElseThrow(() -> new TransactionException(ErrorMessage.MISSING_TRANSACTION));
        log.info(String.format("TRANSFER confirmed! %s TRANSFER_COMMISSION = %s",
                optionalTransferRequest.get().toLogString(),
                optionalTransferRequest.get().getAmount().getValue() * commission));
        repository.removeTransfer(confirmRequest.getOperationId());
        return ResponseEntity.ok().body(new AllowedTransferBody(confirmRequest.getOperationId()));
    }

    public String makeId() {
        return UUID.randomUUID().toString();
    }
}

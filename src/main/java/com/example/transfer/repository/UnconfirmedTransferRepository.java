package com.example.transfer.repository;

import com.example.transfer.model.TransferRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UnconfirmedTransferRepository {
    private final ConcurrentHashMap<String, TransferRequest> unconfirmedTransfers = new ConcurrentHashMap<>();

    public Optional<String> addTransferAndGetId(String id, TransferRequest transferRequest) {
        return unconfirmedTransfers.put(id, transferRequest) == null ?
                Optional.of(id) : Optional.empty();
    }

    public Optional<TransferRequest> getTransfer(String id) {
        return Optional.ofNullable(unconfirmedTransfers.get(id));
    }
    public void removeTransfer(String id) {
        unconfirmedTransfers.remove(id);
    }
}

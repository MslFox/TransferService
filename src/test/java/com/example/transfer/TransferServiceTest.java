package com.example.transfer;

import com.example.transfer.exception.TransactionException;
import com.example.transfer.model.AllowedTransferBody;
import com.example.transfer.model.ConfirmRequest;
import com.example.transfer.model.TransferRequest;
import com.example.transfer.repository.UnconfirmedTransferRepository;
import com.example.transfer.service.TransferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {
    public final static String ID_PATTERN = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";
    @Mock
    UnconfirmedTransferRepository mockRepository;
    @Mock
    private TransferRequest mockTransferRequest;
    @InjectMocks
    private TransferService transferService;
    private final String id = UUID.randomUUID().toString();

    @Test
    void getConfirmResponse() {
        var transferRequest = TransferRequest.builder().cardFromCVV("1111111111111111").cardToNumber("2222222222222222").cardFromCVV("333").cardFromValidTill("10/25").amount(new TransferRequest.Amount("RUR", 1000)).build();
        var confirmRequest = ConfirmRequest.builder().operationId(id).code("0000").build();

        when(mockRepository.getTransfer(any())).thenReturn(Optional.of(transferRequest));
        assertEquals(ResponseEntity.ok().body(new AllowedTransferBody(id)), transferService.getConfirmedResponse(confirmRequest));

        when(mockRepository.getTransfer(any())).thenReturn(Optional.empty());
        assertThrows(TransactionException.class, () -> transferService.getConfirmedResponse(confirmRequest));
    }

    @Test
    void getTransferResponse() {
        TransferService mockTransferService = mock(TransferService.class);

        when(mockTransferService.getTransferResponse(any())).thenCallRealMethod();
        mockTransferService.getTransferResponse(any());
        verify(mockTransferService, times(1)).getTransferResponseId(any(), any());
        verify(mockTransferService, times(1)).makeId();
    }

    @Test
    void getTransferResponseId() {
        when(mockRepository.addTransferAndGetId(any(), any())).thenReturn(Optional.of(id));
        assertEquals(ResponseEntity.ok().body(new AllowedTransferBody(id)), transferService.getTransferResponseId(id, mockTransferRequest));
        when(mockRepository.addTransferAndGetId(any(), any())).thenReturn(Optional.empty());
        assertThrows(TransactionException.class, () -> transferService.getTransferResponseId(id, mockTransferRequest));
    }

    @Test
    void makeId() {
        assertTrue(transferService.makeId().matches(ID_PATTERN));
    }
}
package com.example.transfer;

import com.example.transfer.model.ConfirmRequest;
import com.example.transfer.model.TransferRequest;
import com.example.transfer.repository.UnconfirmedTransferRepository;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;

import java.util.Objects;

import static com.example.transfer.model.ErrorMessage.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTransferAppTests {
    @Value("${integration.test:false}")
    private boolean isIntegrationTest;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    UnconfirmedTransferRepository unconfirmedTransferRepository;
    private static GenericContainer<?> transferApp;
    private static final TransferRequest OK_TRANSFER_REQUEST = TransferRequest.builder().
            cardFromNumber("1111111111111111").
            cardToNumber("2222222222222222").
            cardFromCVV("333").
            cardFromValidTill("10/25").
            amount(new TransferRequest.Amount("RUR", 1000)).
            build();

    @BeforeAll
    public static void setUp(@Value("${integration.test:false}") boolean isIntegrationTest) {
        if (!isIntegrationTest) return;
        transferApp = new GenericContainer<>("transfer:latest").
                withExposedPorts(5500);
        transferApp.start();
     }

    @Test
    public void postConfirm() {
        if (!isIntegrationTest) return;
        var transferResponse = getResponse(OK_TRANSFER_REQUEST);
        var actualOperationId = Objects.requireNonNull(transferResponse.getBody()).get("operationId").toString();
        var confirmResponse = getResponse(new ConfirmRequest(actualOperationId, "0000"));
        assertEquals(confirmResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(confirmResponse.getBody()).get("operationId").toString(),
                actualOperationId);

        confirmResponse = getResponse(new ConfirmRequest(actualOperationId + "test", "0000"));
        assertEquals(confirmResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(Objects.requireNonNull(confirmResponse.getBody()).get("message").toString(),
                WRONG_OPERATION_ID_FORMAT);

        confirmResponse = getResponse(new ConfirmRequest(null, "0000"));
        assertEquals(confirmResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(Objects.requireNonNull(confirmResponse.getBody()).get("message").toString(),
                NULL_OPERATION_ID);

        confirmResponse = getResponse(new ConfirmRequest(actualOperationId.toLowerCase(), "0000"));
        assertEquals(confirmResponse.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        assertEquals(Objects.requireNonNull(confirmResponse.getBody()).get("message").toString(),
                MISSING_TRANSACTION);

    }

    @Test
    public void postTransfers() {
        if (!isIntegrationTest) return;
        var transferResponse = getResponse(OK_TRANSFER_REQUEST);
        assertEquals(transferResponse.getStatusCode(), HttpStatus.OK);
        assertTrue(Objects.requireNonNull(transferResponse.getBody()).get("operationId").toString().
                matches(TransferServiceTest.ID_PATTERN));

        var badTransferRequest = OK_TRANSFER_REQUEST.clone();
        badTransferRequest.setCardFromNumber(null);
        transferResponse = getResponse(badTransferRequest);
        assertEquals(transferResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(Objects.requireNonNull(transferResponse.getBody()).get("message").toString(),
                NULL_CARD_NUMBER_SENDER);
        badTransferRequest = OK_TRANSFER_REQUEST.clone();
        badTransferRequest.setCardToNumber(null);
        transferResponse = getResponse(badTransferRequest);
        assertEquals(transferResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(Objects.requireNonNull(transferResponse.getBody()).get("message").toString(),
                NULL_CARD_NUMBER_RECIPIENT);

        badTransferRequest = OK_TRANSFER_REQUEST.clone();
        badTransferRequest.setCardFromCVV(null);
        transferResponse = getResponse(badTransferRequest);
        assertEquals(transferResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(Objects.requireNonNull(transferResponse.getBody()).get("message").toString(),
                NULL_CVV_SENDER);

        badTransferRequest = OK_TRANSFER_REQUEST.clone();
        badTransferRequest.setCardFromCVV("asd");
        transferResponse = getResponse(badTransferRequest);
        assertEquals(transferResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(Objects.requireNonNull(transferResponse.getBody()).get("message").toString(),
                WRONG_CVV_FORMAT_SENDER);

        badTransferRequest = OK_TRANSFER_REQUEST.clone();
        badTransferRequest.setCardFromValidTill(null);
        transferResponse = getResponse(badTransferRequest);
        assertEquals(transferResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(Objects.requireNonNull(transferResponse.getBody()).get("message").toString(),
                NULL_TILL_DATE_SENDER);

        badTransferRequest = OK_TRANSFER_REQUEST.clone();
        badTransferRequest.setCardFromValidTill("abs");
        transferResponse = getResponse(badTransferRequest);
        assertEquals(transferResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(Objects.requireNonNull(transferResponse.getBody()).get("message").toString(),
                WRONG_TILL_DATE_FORMAT_SENDER);

        badTransferRequest = OK_TRANSFER_REQUEST.clone();
        badTransferRequest.getAmount().setCurrency(null);
        transferResponse = getResponse(badTransferRequest);
        assertEquals(transferResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(Objects.requireNonNull(transferResponse.getBody()).get("message").toString(),
                NULL_CURRENCY);

        badTransferRequest = OK_TRANSFER_REQUEST.clone();
        badTransferRequest.getAmount().setCurrency("USD");
        transferResponse = getResponse(badTransferRequest);
        assertEquals(transferResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(Objects.requireNonNull(transferResponse.getBody()).get("message").toString(),
                NOT_SUPPORTED_CURRENCY);

        badTransferRequest = OK_TRANSFER_REQUEST.clone();
        badTransferRequest.getAmount().setValue(0);
        transferResponse = getResponse(badTransferRequest);
        assertEquals(transferResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(Objects.requireNonNull(transferResponse.getBody()).get("message").toString(),
                NOT_MORE_THEN_ZERO);
    }

    private ResponseEntity<JSONObject> getResponse(TransferRequest expectedRequest) {
        return restTemplate.postForEntity(
                "http://localhost:" + transferApp.getMappedPort(5500) + "/transfer",
                expectedRequest,
                JSONObject.class);
    }

    private ResponseEntity<JSONObject> getResponse(ConfirmRequest expectedRequest) {
        return restTemplate.postForEntity(
                "http://localhost:" + transferApp.getMappedPort(5500) + "/confirmOperation",
                expectedRequest,
                JSONObject.class);
    }
}

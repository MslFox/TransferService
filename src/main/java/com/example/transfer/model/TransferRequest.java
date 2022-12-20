package com.example.transfer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

import static com.example.transfer.model.ErrorMessage.*;

@Data
@Builder
public class TransferRequest {
    @NotNull(message = NULL_CARD_NUMBER_SENDER)
    private String cardFromNumber;
    @NotNull(message = NULL_CARD_NUMBER_RECIPIENT)
    private String cardToNumber;
    @NotNull(message = NULL_CVV_SENDER)
    @Pattern(regexp = "[0-9]{3}", message = WRONG_CVV_FORMAT_SENDER)
    private String cardFromCVV;
    @NotNull(message = NULL_TILL_DATE_SENDER)
    @Pattern(regexp = "(0[0-9]|1[0-2])/((2[2-9])|(3[0-9]))", message = WRONG_TILL_DATE_FORMAT_SENDER)
    private String cardFromValidTill;

    @Valid
    private Amount amount;


    public TransferRequest clone() {
        return new TransferRequest(
                this.cardFromNumber,
                this.cardToNumber,
                this.cardFromCVV,
                this.cardFromValidTill,
                this.amount.clone());
    }

    public String toLogString() {
        return String.format("CARD_FROM = %s CARD_TO = %s AMOUNT_VALUE = %s ",
                cardFromNumber,
                cardToNumber,
                amount.value
        );
    }

    @Data
    @AllArgsConstructor
    public static class Amount {
        @NotNull(message = NULL_CURRENCY)
        @Pattern(regexp = "RUR", message = NOT_SUPPORTED_CURRENCY)
        private String currency;
        @Positive(message = NOT_MORE_THEN_ZERO)
        private long value;

        protected Amount clone() {
            return new Amount(
                    this.currency,
                    this.value);
        }
    }
}

package com.example.transfer.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RejectedTransferBody {
    private static int count = 0;
    private String message;
    private int id;

    public RejectedTransferBody(String message, int id) {
        this.message = message;
        this.id = count;
        count++;
        if (count == 2_147_483_647) count = 0;
    }
}


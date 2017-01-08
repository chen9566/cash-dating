package me.jiangcai.dating.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author CJ
 */
@Data
public class TicketInfo {
    private final LocalDate expiredDate;
    private final long number;
}

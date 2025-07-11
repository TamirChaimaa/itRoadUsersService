package com.itRoad.users_service.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDTO {
    private Long totalUsers;
    private Long activeUsers;
    private Long adherantUsers;
    private Long adminUsers;
}

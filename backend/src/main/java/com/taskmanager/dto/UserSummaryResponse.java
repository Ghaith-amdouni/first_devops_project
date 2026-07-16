package com.taskmanager.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryResponse {

    private Long id;
    private String username;
    private String email;
    private String role;
}

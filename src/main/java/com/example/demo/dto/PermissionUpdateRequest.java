package com.example.demo.dto;

import lombok.Data;

@Data
public class PermissionUpdateRequest {
    private String moduleName;
    private boolean canView;
    private boolean canCreate;
    private boolean canEdit;
    private boolean canDelete;
}

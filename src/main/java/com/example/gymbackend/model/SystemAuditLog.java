package com.example.gymbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_audit_logs")
@Getter
@Setter
public class SystemAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_name", length = 50)
    private String tableName;

    @Column(name = "record_id")
    private Long recordId;

    @Column(length = 20)
    private String action; // INSERT, UPDATE, DELETE

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_data", columnDefinition = "jsonb")
    private String oldData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_data", columnDefinition = "jsonb")
    private String newData;

    @Column(name = "performed_by")
    private Long performedBy;

    @Column(name = "performed_at")
    private LocalDateTime performedAt = LocalDateTime.now();
}

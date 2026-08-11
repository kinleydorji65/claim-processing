package com.claim.claim_processing.application.entity.others;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

import com.claim.claim_processing.common.entities.common.SubmissionChannelMaster;

@Entity
@Table(name = "VISA_DOWNLOADED", 
       schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisaDownloaded {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "LOCATION", length = 255)
    private String location;

    @Column(name = "SUBMISSION_CHANNEL_ID")
    private Long submissionChannelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBMISSION_CHANNEL_ID", 
                referencedColumnName = "ID", 
                insertable = false, 
                updatable = false)
    private SubmissionChannelMaster submissionChannel;

    @Column(name = "DOWNLOADED_DATE")
    private LocalDateTime downloadedDate;

    @Column(name = "DEVICE_NAME", length = 255)
    private String deviceName;

    @Column(name = "IP_ADDRESS", length = 45)
    private String ipAddress;

    @Column(name = "NPPF_NUMBER", length = 100)
    private String nppfNumber;

    @Column(name = "CID", length = 100)
    private String cid;

    @Column(name = "CREATED_AT", updatable = false)
    private Timestamp createdAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    // ONE-TO-ONE with VisaResponseData (inverse side)
    @OneToOne(mappedBy = "visaDownloaded", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private VisaResponseData visaResponseData;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Timestamp.from(Instant.now());
        }
        if (downloadedDate == null) {
            downloadedDate = LocalDateTime.now();
        }
    }
}
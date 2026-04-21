package com.claim.claim_processing.common.entities.others.agency;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "AGENCY_ADDRESS_DETAIL", schema = "PPFMS_REGISTRATION_SERVICE_SCHEMA")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class AddressDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BUILDING_NO")
    private String buildingNo;

    @Column(name = "FLOOR_NO")
    private String floorNo;

    @Column(name = "STREET_NAME")
    private String streetName;

    @Column(name = "DISTRICT")
    private String district;

    @Column(name = "STATE")
    private String state;

    @Column(name = "CITY")
    private String city;

    @Column(name = "DZONGKHAG_ID")
    private Long dzongkhagId;

    @Column(name = "GEWOG_ID")
    private Long gewogId;

    @Column(name = "COUNTRY_ID")
    private Long countryId;

    // @Column(name = "AGENCY_CODE")
    // private String agencyCode;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "TOLL_FREE_NUMBER")
    private String tollFreeNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "WEB_ADDRESS")
    private String webAddress;

    @Column(name = "VILLAGE_ID")
    private Long villageId;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @ManyToOne
    @JsonBackReference  
    @JoinColumn(name = "AGENCY_CODE", referencedColumnName = "AGENCY_CODE", nullable = true)
    private AgencyDetail agency;

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

}

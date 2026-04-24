package com.claim.claim_processing.common.entities.others.member;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MEMBER_ADDRESS", schema = "PPFMS_REGISTRATION_SERVICE_SCHEMA")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MemberAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CURRENT_COUNTRY_ID")
    private Long currentCountryId;

    @Column(name = "PERMANENT_COUNTRY_ID")
    private Long permanentCountryId;

    @Column(name = "MEMBER_DETAIL_ID")
    private Long memberDetailId;

    @Column(name = "NATIONALITY_ID")
    private Long nationalityId;

    @Column(name = "PERMANENT_DZONGKHAG_ID")
    private Long permanentDzongkhagId;

    @Column(name = "PERMANENT_GEWOG_ID")
    private Long permanentGewogId;

    @Column(name = "PERMANENT_VILLAGE_id")
    private Long permanentVillageId;

    @Column(name = "CURENT_DZONGKHAG_ID")
    private Long currentDzongkhagId;

    @Column(name = "CURRENT_GEWOG_ID")
    private Long currentGewogId;

    @Column(name = "CURRENT_VILLAGE_id")
    private Long currentVillageId;

    @Column(name="THRAM_NUMBER")
    private Long thramNumber;

    @Column(name="HOUSE_NUMBER")
    private String houseNumber;

    @Column(name = "STREET_NAME")
    private String streetName;

    @Column(name = "CURRENT_DISTRICT")
    private String currentDistrict;

    @Column(name = "PERMANENT_DISTRICT")
    private String permanentDistrict;

    @Column(name = "CURRENT_STATE")
    private String currentState;

    @Column(name = "PERMANENT_STATE")
    private String permanentState;

    @Column(name = "CURRENT_CITY")
    private String currentCity;

    @Column(name = "PERMANENT_CITY")
    private String permanentCity;

    @Column(name = "BUILDING_NO")
    private String buildingNo;

    @Column(name = "FLOOR_NO")
    private String floorNo;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "MEMBER_CODE", referencedColumnName = "MEMBER_CODE", nullable = false)
    private MemberDetail member;
}

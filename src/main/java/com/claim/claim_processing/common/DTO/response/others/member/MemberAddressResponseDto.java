package com.claim.claim_processing.common.DTO.response.others.member;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberAddressResponseDto {

    private Long id;

    // -------------------------------
    // MEMBER
    // -------------------------------
    private Long memberDetailId;
    private String memberCode;

    private Long nationalityId;
    private String nationalityName; // optional (master lookup)

    // -------------------------------
    // CURRENT ADDRESS
    // -------------------------------
    private Long currentCountryId;
    private String currentCountryName;

    private Long currentDzongkhagId;
    private String currentDzongkhagName;

    private Long currentGewogId;
    private String currentGewogName;

    private Long currentVillageId;
    private String currentVillageName;

    private String currentDistrict;
    private String currentState;
    private String currentCity;

    // -------------------------------
    // PERMANENT ADDRESS
    // -------------------------------
    private Long permanentCountryId;
    private String permanentCountryName;

    private Long permanentDzongkhagId;
    private String permanentDzongkhagName;

    private Long permanentGewogId;
    private String permanentGewogName;

    private Long permanentVillageId;
    private String permanentVillageName;

    private String permanentDistrict;
    private String permanentState;
    private String permanentCity;

    // -------------------------------
    // PROPERTY DETAILS
    // -------------------------------
    private Long thramNumber;
    private String houseNumber;
    private String buildingNo;
    private String floorNo;

    private String streetName;
}

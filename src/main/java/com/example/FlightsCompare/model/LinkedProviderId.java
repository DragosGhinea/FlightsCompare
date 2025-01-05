package com.example.FlightsCompare.model;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class LinkedProviderId implements Serializable {
    private String providerUserId;
    private ProviderType provider;
}

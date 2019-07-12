package com.genius.cometogether_mob;

public class User {
    private String accountType;
    private String firstName;
    private String lastName;
    private String organizationName;
    private String organizationDetails;

    public User()
    {

    }

    public User(String accountType, String firstName, String lastName, String organizationName,
                 String organizationDetails)
    {
        this.accountType = accountType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.organizationName = organizationName;
        this.organizationDetails = organizationDetails;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getOrganizationDetails() {
        return organizationDetails;
    }
}

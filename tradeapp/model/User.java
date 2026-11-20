package com.tradeapp.model;

public class User {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String businessName;
    private String phone;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public String getBusinessName() { return businessName; }
    public String getPhone() { return phone; }
}

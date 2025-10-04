package org.java.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
public class UserModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false)
  private String email;

  private Integer age;

  private String phone;

  private String address;

  public UserModel() {
  }

  public UserModel(String firstName,
                   String lastName,
                   String email,
                   Integer age,
                   String phone,
                   String address) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.age = age;
    this.phone = phone;
    this.address = address;
  }

  public UUID id() {
    return id;
  }

  public String firstName() {
    return firstName;
  }


  public String lastName() {
    return lastName;
  }


  public String email() {
    return email;
  }


  public Integer age() {
    return age;
  }


  public String phone() {
    return phone;
  }


  public String address() {
    return address;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String firstName;
    private String lastName;
    private String email;
    private Integer age;
    private String phone;
    private String address;

    public Builder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public Builder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public Builder email(String email) {
      this.email = email;
      return this;
    }

    public Builder age(Integer age) {
      this.age = age;
      return this;
    }

    public Builder phone(String phone) {
      this.phone = phone;
      return this;
    }

    public Builder address(String address) {
      this.address = address;
      return this;
    }

    public UserModel build() {
      return new UserModel(firstName, lastName, email, age, phone, address);
    }
  }
}

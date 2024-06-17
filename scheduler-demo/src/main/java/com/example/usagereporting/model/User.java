package com.example.usagereporting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 *  A model class representing user information.
 */
@Document(collection = "user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

  @Id
  private String id;
  private String firstname;
  private String lastname;
  private String username;

  @Indexed(unique = true)
  private String subUuid;
  private String currentLicenseId;
  private String accountId;
  private String accountStatus;
  @Indexed(unique = true)
  private String email;

  private String password;
  private String signintype;
  private String fbId;
  private String githubId;
  private String phonenumber;
  private String avatar;
  private String Idtoken;
  private Date loggedinDate;
  private Date loggedoutDate;

  private List<String> role;

  private String org;
  private String org_country;
  private String org_sub1;
  private String org_sub2;
  private String org_sub3;
  private boolean status;

  private List<Product> products;

}
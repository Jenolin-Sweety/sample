package com.example.usagereporting.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *  A model class representing user's purchase of a product.
 */
@Document(collection = "product")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

  @Id
  private String pID;
  private String arovaRef;
  private String product;
  private String productExternalName;
  private String subUUID;
  private String status;
  private String startTime;
  private String lastReportTime;


}
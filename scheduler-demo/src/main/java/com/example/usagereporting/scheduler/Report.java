package com.example.usagereporting.scheduler;

import com.example.usagereporting.model.Product;
import com.example.usagereporting.model.User;
import com.example.usagereporting.repository.ProductRepository;
import com.example.usagereporting.repository.UserRepository;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.servicecontrol.v1.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.services.servicecontrol.v1.ServiceControl;
import com.google.cloud.ServiceOptions;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class Report {

  private static final String CLOUD_SCOPE = "https://www.googleapis.com/auth/cloud-platform";

  @Autowired
  private ProductRepository productRepository;


  @Autowired
  private UserRepository userRepository;


  /**
   * Scheduled method to perform periodic reporting for user products.
   * This method fetches users from a repository, iterates through their products,
   * and reports usage metrics to a Google Cloud service using Google Service Control API.
   *
   * Scheduled to run daily at 8:00 AM UTC.
   *
   * @throws IOException If an I/O error occurs during API communication.
   */
  @Scheduled(cron = "0 0 8 * * ?")
  public void sample() throws IOException {
    System.out.println("scheduler started every 2 minutes");

    String serviceName = "isaas-codelab.mp-marketplace-partner-demos.appspot.com";
    try {
      ServiceControl serviceControl = new ServiceControl.Builder(GoogleNetHttpTransport.newTrustedTransport(),
          new JacksonFactory(),
          new HttpCredentialsAdapter(
              GoogleCredentials.getApplicationDefault().createScoped(Collections.singletonList(CLOUD_SCOPE))))
          .setApplicationName(ServiceOptions.getDefaultProjectId()).build();

      List<User> userList = userRepository.findAll();

      if (userList != null) {
        for (User user : userList) {
          for (Product product : user.getProducts()) {
            if (product.getSubUUID() == null || product.getSubUUID().isEmpty()) {
              continue;
            }

            String startTime = product.getLastReportTime() != null && !product.getLastReportTime().isEmpty() ? product.getLastReportTime()
                : product.getStartTime();
            String formattedStartTime = startTime.toString();
            String endTime = Instant.now().toString();
            String metricPlanName = product.getProduct().replace('-', '_');

            Operation operation = new Operation();
            operation.setOperationId(UUID.randomUUID().toString());
            operation.setConsumerId(product.getSubUUID());
            operation.setOperationName("Codelab Usage Report");
            operation.setStartTime(formattedStartTime);
            operation.setEndTime(endTime);
            System.out.println(operation);


            MetricValueSet metricValueSet = new MetricValueSet();
            metricValueSet.setMetricName(String.format("%s/%s_requests", serviceName, metricPlanName));

            MetricValue metricValue = new MetricValue();
            metricValue.setInt64Value(getUsageForProduct());

            metricValueSet.setMetricValues(Collections.singletonList(metricValue));
            operation.setMetricValueSets(Collections.singletonList(metricValueSet));


            // Adjust timestamp format for startTime

            CheckRequest checkRequest = new CheckRequest();
            checkRequest.setOperation(operation);
            CheckResponse response = serviceControl.services().check(serviceName, checkRequest).execute();

            if (response.getCheckErrors() != null && response.getCheckErrors().size() > 0) {
              System.out.printf("Errors for user %s with product %s:\n", user.getAccountId(), product.getPID());
              System.out.println(response.getCheckErrors());

              // TODO: Temporarily turn off service for the user.
              continue;
            }

            // userLabels are only allowed in report()
            // Attribute the current cost of this report to the `products_db` resource

            //Report.addCostAttribution(operation, "saas-storage-solutions", "products_db");

            ReportRequest reportRequest = new ReportRequest();
            reportRequest.setOperations(Collections.singletonList(operation));
            serviceControl.services().report(serviceName, reportRequest).execute();

            product.setLastReportTime(endTime);
            productRepository.save(product);

          }
        }
      }
    } catch (Exception e) {
      // Catch any other exceptions that might occur during reflection or invocation
      e.printStackTrace();
    }

  }

  /**
   * Retrieves the usage for the product since the last report time.
   *
   * @return The usage count since the last report time.
   */
  private static long getUsageForProduct() {
    // TODO: Get the usage since the last report time.
    return 10;
  }

  /**
   * Adds cost attribution labels to the given operation.
   * Attribute the cost associated with this `operation` to the given `resourceName` within the given `containerName`,
   *
   * @param operation The operation.
   * @param containerName The container name.
   * @param resourceName The resource name.
   */
  private static void addCostAttribution(Operation operation, String containerName, String resourceName) {
    Map<String, String> userLabels = ImmutableMap.<String, String>builder()
        .put("cloudmarketplace.googleapis.com/container_name", containerName)
        .put("cloudmarketplace.googleapis.com/resource_name", resourceName).build();
    operation.setUserLabels(userLabels);
  }
}



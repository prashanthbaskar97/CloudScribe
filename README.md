# CloudScribe

A scalable, cloud-native assignment management backend built with Spring Boot, deployed across AWS and GCP, and powered by CI/CD pipelines.

---

## 🚀 Overview

**CloudScribe** is a multi-cloud assignment management platform designed with modern backend architecture. It provides REST and GraphQL APIs for managing assignments and submissions, integrates performance monitoring, and supports dynamic infrastructure provisioning and deployment using infrastructure-as-code (IaC) and DevOps best practices.

---

## 🛠 Tech Stack

| Layer             | Technologies                                                                 |
|------------------|-------------------------------------------------------------------------------|
| Backend          | Spring Boot, Java, REST APIs, GraphQL                                         |
| CI/CD            | GitHub Actions, Packer (for AMI builds)                                       |
| Cloud (AWS)      | EC2, RDS, DynamoDB, CloudWatch, ELB, Auto Scaling, VPC                        |
| Cloud (GCP)      | Google Cloud Storage, Lambda via custom triggers                              |
| Infra-as-Code    | Pulumi (TypeScript)                                                           |
| Monitoring       | Log4j, StatsD, CloudWatch                                                     |
| Load Testing     | Apache JMeter                                                                 |
| Testing          | JUnit, Mockito                                                                |

---

## 📦 Features

- ✅ Spring Boot REST & GraphQL API for CRUD operations on assignments and submissions  
- ✅ CI/CD with GitHub Actions (unit testing + post-merge deployment)  
- ✅ AMI building using Packer for EC2-based deployment  
- ✅ Infrastructure provisioning with Pulumi across AWS (EC2, RDS, DynamoDB, etc.)  
- ✅ CloudWatch + StatsD integration for live performance metrics  
- ✅ JMeter load testing setup included  
- ✅ GCP integration for assignment file storage via Lambda + SNS  
- ✅ Email status updates using DNS config + MX records  

---

## 🧪 Running Tests

```bash
./mvnw test

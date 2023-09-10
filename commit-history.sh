#!/bin/bash

declare -a commits=(
  "2023-09-10|Initial Spring Boot project structure with basic REST APIs"
  "2023-09-20|Add GraphQL support and DTOs for assignment and submission"
  "2023-10-01|Implement service layer and repository integration"
  "2023-10-12|Add authentication filters and security config"
  "2023-10-25|Set up GitHub Actions CI pipeline with unit tests"
  "2023-11-02|Create JMeter test plan for load testing"
  "2023-11-15|Add AWS infra setup scripts (VPC, EC2, RDS, etc.)"
  "2023-11-25|Integrate CloudWatch metrics and Log4j logging"
  "2023-12-05|Deploy with Packer + AMI; add GCP Lambda and SNS integration"
  "2023-12-15|Finalize MX, DNS, HTTPS cert configs; project complete"
)

for entry in "${commits[@]}"; do
  IFS='|' read -r date message <<< "$entry"
  export GIT_COMMITTER_DATE="$date 12:00:00"
  export GIT_AUTHOR_DATE="$date 12:00:00"
  git add .
  git commit -m "$message" --date "$date 12:00:00"
done


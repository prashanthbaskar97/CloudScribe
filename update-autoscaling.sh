#!/bin/bash
set -e 
set -o pipefail

aws ec2 create-launch-template-version \
    --launch-template-name "cloudapp-ec2-launchtemplate" \
    --version-description launch-template-versioning \
    --source-version '$Latest' \
    --launch-template-data "ImageId=$AMI_ID"

aws autoscaling update-auto-scaling-group --auto-scaling-group-name csye6225_asg \
  --launch-template LaunchTemplateName=cloudapp-ec2-launchtemplate,Version='$Latest'

aws autoscaling start-instance-refresh --auto-scaling-group-name csye6225_asg --preferences '{"InstanceWarmup": 60, "MinHealthyPercentage": 50}' > InstanceRefreshStatus.json

InstanceRefreshId=$(jq -r .InstanceRefreshId InstanceRefreshStatus.json)
echo "Instance refresh id: $InstanceRefreshId\n"
echo "Beginning auto scaling group instance refresh"

while true; do
    statuses=$(aws autoscaling describe-instance-refreshes --auto-scaling-group-name csye6225_asg)
    for statusBlob in $(echo "${statuses}" | jq -r '.InstanceRefreshes[] | @base64'); do
        _jq() {
            echo "${statusBlob}" | base64 --decode | jq -r "${1}"
        }
        if [ "$(_jq '.InstanceRefreshId')" = "$InstanceRefreshId" ]; then
            status=$(_jq '.Status')
                if [ "$status" = "Failed" ] || [ "$status" = "RollbackFailed" ]; then
                    echo "Auto scaling action failed with status: $status"
                    exit 1
                elif [ "$status" = "Successful" ] || [ "$status" = "Cancelled" ] || [ "$status" = "RollbackSuccessful" ]; then
                    echo "Auto scaling action completed with status: $status"
                    exit 0
                fi
            progressPercentage=$(_jq '.PercentageComplete')
            echo "Current status: $status, Progress complete percentage: $progressPercentage"
        fi
    done
sleep 5
done

packer {
  required_plugins {
    amazon = {
      version = ">= 1.2.6"
      source  = "github.com/hashicorp/amazon"
    }
  }
}

variable "aws_region" {
  type    = string
  default = env("AWS_DEFAULT_REGION")
}

variable "subnet_id" {
  type    = string
  default = env("AWS_DEFAULT_SUBNET")
}

variable "vpc_id" {
  type    = string
  default = "vpc-0a648a4bd634e0398"
}

variable "source_ami" {
  type    = string
  default = "ami-06db4d78cb1d3bbf9" # Debian 12
}

variable "ssh_username" {
  type    = string
  default = "admin"
}

variable "ami_users" {
  type    = list(string)
  default = ["088914901283", "402435988681"]
}

source "amazon-ebs" "cloudapp-ami" {
  region          = "${var.aws_region}"
  ami_name        = "cloudapp_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
  ami_description = "AMI for CloudApp"
  ami_regions = [
    "${var.aws_region}",
  ]
  associate_public_ip_address = true

  aws_polling {
    delay_seconds = 30
    max_attempts  = 80
  }
  instance_type = "t2.micro"
  source_ami    = "${var.source_ami}"
  ssh_username  = "${var.ssh_username}"
  subnet_id     = "${var.subnet_id}"
  ami_users     = "${var.ami_users}"


  launch_block_device_mappings {
    delete_on_termination = true
    device_name           = "/dev/xvda"
    volume_size           = 25
    volume_type           = "gp2"
  }
}

build {
  sources = ["source.amazon-ebs.cloudapp-ami"]

  provisioner "file" {
    source      = "CloudAppRelease.zip"
    destination = "~/CloudAppRelease.zip"
  }

  provisioner "file" {
    source      = "setup-cloudapp-instance.sh"
    destination = "~/setup-cloudapp-instance.sh"
  }

  provisioner "shell" {
    environment_vars = [
      "DEBIAN_FRONTEND=noninteractive",
      "CHECKPOINT_DISABLE=1"
    ]
    inline = ["sudo bash ~/setup-cloudapp-instance.sh"]
  }

  post-processor "manifest" {
    output = "manifest.json"
  }
}
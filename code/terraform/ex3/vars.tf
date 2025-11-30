variable "region" {
  description = "The AWS region to deploy resources in"
  type        = string
  default     = "eu-north-1"

}

variable "zone" {
  description = "The AWS availability zone to deploy the instance in"
  type        = string
  default     = "eu-north-1a"

}

variable "webuser" {
  default = "ubuntu"
}

variable "amiID" {
  type = map(any)
  default = {
    "eu-north-1" = "ami-0c322300a1dd5dc79"
    "us-east-1"  = "ami-0ff8a91507f77f867"
    "us-west-2"  = "ami-0b2f6494ff0b07a0e"
  }
}
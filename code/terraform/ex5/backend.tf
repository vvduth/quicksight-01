terraform {
  backend "s3" {
    bucket = "bucketname"
    key = "terraform/backend"
    region = "eu-north-1"
  }
}
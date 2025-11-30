resource "aws_instance" "web" {
  ami           = data.aws_ami.amiID.id
  instance_type = "t3.micro"
  # can either use hardcoded key name or reference the key pair resource
  # for hard coded, make sure the key is in the folder where terraform is run
  key_name               = "dove-key"
  vpc_security_group_ids = [aws_security_group.dove-sg.id]
  availability_zone      = "eu-north-1a"
  tags = {
    Name    = "dove-web-instance"
    Project = "dove-terraform"
  }
}

resource "aws_ec2_instance_state" "web-state" {
  instance_id = aws_instance.web.id
  state = "running"
}
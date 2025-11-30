resource "aws_instance" "web" {
  ami           = var.amiID[var.region]
  instance_type = "t3.micro"
  # can either use hardcoded key name or reference the key pair resource
  # for hard coded, make sure the key is in the folder where terraform is run
  key_name               = "dove-key"
  vpc_security_group_ids = [aws_security_group.dove-sg.id]
  availability_zone      = var.zone
  tags = {
    Name    = "dove-web-instance"
    Project = "dove-terraform"
  }

  provisioner "file" {
    source = "web.sh"
    destination = "/tmp/web.sh"
  }

  connection {
    type = "ssh"
    user = var.webuser
    private_key = file("dove-key")
    ## get the public IP after instance is created
    host = self.public_ip
  }

  provisioner "remote-exec" {
    inline = [
      "chmod +x /tmp/web.sh", # give permission to execute the script
      "sudo /tmp/web.sh"
    ]
  }
}

resource "aws_ec2_instance_state" "web-state" {
  instance_id = aws_instance.web.id
  state       = "running"
}
variable "project_name" {
  description = "Prefixo para nomear recursos"
  type        = string
  default     = "adopet"
}

variable "vpc_id" {
  description = "VPC onde ECS/ALB vão rodar"
  type        = string
}

variable "public_subnet_ids" {
  description = "Subnets públicas para ALB e Fargate (string com IDs separados por vírgula)"
  type        = string
}

locals {
  public_subnet_ids_list = split(",", var.public_subnet_ids)
}

variable "docker_image" {
  description = "Imagem Docker da aplicação"
  type        = string
}
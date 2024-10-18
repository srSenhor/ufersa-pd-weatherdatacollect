# Prática Offline 2.2 - Coleta e distribuição de Dados climáticos

Este projeto se trata de uma prática offline da disciplina de **Programação Concorrente e Distribuída**.

## Definições

Bastante similar à [prática 2.1](https://github.com/srSenhor/ufersa-pd-multicast-socket), mas com um exemplo mais simples. Também teremos um drone sobrevoando determinada região coletando dados climáticos e enviando ao servidor (1), que irá repassar os dados para outros dois servidores (2 e 3), e todos armazenaram em sua própria base de dados (usaremos a memória principal). Além disso, os clientes podem se conectar ao *datacenter*.

## Detalhes do funcionamento

- Simulação se inicia com um comando do console para o "drone" realizar a coleta de dados (geração aleatória numa faixa aceitável)
- Clientes leem dados do sistema e podem escolher que tipos de dados querem receber
  - Cliente 1 recebe apenas pressão atmosférica
  - Cliente 2 recebe temperatura e umidade relativa do ar
  - Cliente 3 recebe todos os dados
- Programa roda durante um tempo estipulado

## Tecnologias usadas

- [RabbitMQ](https://www.rabbitmq.com/docs/download)
# FinTrack
Aplicação fullstack de controle financeiro pessoal. O usuário cria uma conta, registra receitas e despesas por categoria e acompanha tudo em um dashboard com gráficos e cotações de câmbio em tempo real.

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-brightgreen?style=flat-square)
![React](https://img.shields.io/badge/React-18-61DAFB?style=flat-square)
![TypeScript](https://img.shields.io/badge/TypeScript-5-3178C6?style=flat-square)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-336791?style=flat-square)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square)

## Funcionalidades

- Cadastro e autenticação com JWT
- Registro de receitas e despesas com categoria e data
- Dashboard com resumo mensal (receitas, despesas, saldo)
- Gráfico de pizza por categoria de despesa
- Gráfico de barras com evolução dos últimos 6 meses
- Cotações em tempo real de USD, EUR e BTC via [AwesomeAPI](https://docs.awesomeapi.com.br)
- Navegação por mês

## Arquitetura dos containers

```
Navegador → fintrack-web:80 (nginx + React)
                │
                └── /api/* → fintrack-api:8080 (Spring Boot)
                                   │
                                   └── postgres:5432 (PostgreSQL)
```

## Pré-requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop) instalado e rodando

## Rodando com Docker

### 1. Clone o repositório

```bash
git clone https://github.com/GabriG07/Fintrack-docker.git
cd fintrack-docker
```

### 2. Suba os containers

```bash
docker-compose up --build
```

A aplicação estará disponível em `http://localhost`.


Para parar:

```bash
docker-compose down
```


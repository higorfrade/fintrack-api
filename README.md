# Fintrack API

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen)
![License](https://img.shields.io/badge/license-MIT-green)

A **Fintrack API** é um sistema de controle financeiro criado com **Spring Boot**. Esta API tem como objetivo fornecer funcionalidades para o gerenciamento de finanças pessoais e corporativas, incluindo autenticação via **JWT**, envio de e-mails usando o **Java Mail Sender** com integração ao servidor da **Brevo**, e segurança com **PasswordEncoder**.

Esta API está hospedada na plataforma **Render** e pode ser acessada em: [https://fintrack-api-vymx.onrender.com/api/status](https://fintrack-api-vymx.onrender.com/api/status)

---

## Funcionalidades

- **Gerenciamento Financeiro:** Controle de transações, contas e categorias financeiras.
- **Autenticação JWT:** A API utiliza JSON Web Token (JWT) para autenticação de usuários e controle de sessões.
- **Envio de E-mails:** Integração com o servidor da **Brevo** para envio de e-mails transacionais.
- **Segurança:** Utiliza **PasswordEncoder** para proteger as senhas dos usuários de uma forma segura.

---

## Tecnologias Utilizadas

- **Spring Boot:** Framework utilizado para o desenvolvimento da API.
- **JWT (JSON Web Token):** Para autenticação e autorização segura de usuários.
- **Java Mail Sender (Brevo):** Para envio de e-mails.
- **Spring Security:** Para proteção da API e controle de acesso.
- **Spring Data JPA:** Para persistência de dados no banco de dados.
- **PostgreSQL:** Banco de dados utilizado para armazenar as informações.

---

## Endpoints

A API possui os seguintes endpoints principais:

1. **Status Check**
    - `GET /status ou /health`: Retorna se a API está funcionando corretamente.

2. **Contas**
    - `POST /profiles/login`: Realiza o login e retorna um JWT token para autenticação.
    - `POST /profiles/register`: Registra um novo usuário.
    - `DELETE /profiles/delete-account`: Deleta uma conta.

3. **Transações**
    - `GET /expenses`: Lista todas as despesas do usuário.
    - `POST /expenses/add`: Cria uma nova despesa financeira.
    - `DELETE /expenses/delete/{expenseId}`: Deleta uma despesa.
  
    - `GET /incomes`: Lista todas as receitas do usuário.
    - `POST /incomes/add`: Cria uma nova receita financeira.
    - `DELETE /incomes/delete/{expenseId}`: Deleta uma receita.

4. **Filter**
    - `POST /filter`: Cria um filtro para busca financeira.
    
5. **Categorias**
    - `GET /categories`: Lista todas as categorias financeiras.
    - `GET /categories/{type}`: Lista as categorias financeiras por tipo.
    - `POST /categories/create`: Cria uma nova categoria.
    - `PUT /categories/update/{categoryId}`: Atualiza uma categoria.
      
6. **Dashboard**
    - `GET /dashboard`: Lista todas as transações financeiras.

---

## Como Rodar Localmente

### Pré-requisitos

- **Java 17** ou superior
- **Maven** (para construção e dependências)
- **MySQL** (ou qualquer outro banco de dados de sua preferência)

### Passos

1. **Clone este repositório:**

   ```bash
   git clone https://github.com/higorfrade/fintrack-api.git
   cd fintrack-api
Configure as variáveis de ambiente para conexão com o banco de dados (MySQL):

2. **No arquivo *src/main/resources/application.properties*, ajuste as configurações do banco de dados:**

   ```bash
   spring.datasource.url=jdbc:mysql://localhost:3306/fintrack_db
   spring.datasource.username=root
   spring.datasource.password=senha_do_banco

3. **Compile o projeto com Maven:**

   ```bash
   mvn clean install

4. **Execute a aplicação:**

   ```bash
   mvn spring-boot:run

A API estará disponível em **http://localhost:8080/api**.

---

## Testes
Para garantir que a API funcione corretamente, testes automatizados foram implementados. Para rodá-los, utilize o comando:

  ```bash
  mvn test
  ```

---

## Implantação
A API está atualmente hospedada no Render. A URL para acesso é: https://fintrack-api-vymx.onrender.com/api/.

---

## Contribuindo
Se você quiser contribuir para o projeto, siga estas etapas:

- Faça um fork do repositório.

- Crie uma branch para a sua feature (git checkout -b feature/nova-feature).

- Faça as alterações necessárias e commite (git commit -am 'Adiciona nova feature').

- Envie para o repositório remoto (git push origin feature/nova-feature).

- Abra um pull request.

---

## Licença
Este projeto está licenciado sob a licença MIT.

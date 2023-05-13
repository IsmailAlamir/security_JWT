# security_JWT

## Summary Of The Components And Their Functionality:
1. **User** and **Role** : These classes represent the user entity and user roles in the application.

2. **UserRepository** : This interface extends the JpaRepository interface and provides methods for interacting with the user database.

3. **AuthenticationController** : This controller class handles authentication-related endpoints such as user registration and authentication.

4. **RegisterRequest** and **AuthenticateRequest** : These classes define the request payload structures for user registration and authentication.

5. **AuthenticationResponse** : This class defines the response payload structure for authentication operations, including the JWT token.

6. **AuthenticationService** : This service class handles user registration and authentication logic.

7. **ApplicationConfig** : This configuration class provides beans for user details service, authentication provider, password encoder, and authentication manager.

8. **JwtService** : This service class handles JWT-related operations, such as token generation, validation, and extraction of claims.

9. **DemoController** : This controller class contains a sample endpoint that requires authentication.


## Overview Of The Typical Cycle :

#### 1. User Registration:
   - Client sends a registration request containing user's details (first name, last name, email, password) to the `/api/v1/auth/register` endpoint.
   - `AuthenticationController` receives the request and delegates registration process to `AuthenticationService`.
   - `AuthenticationService` creates a new `User` object with provided details, encrypts the password using a password encoder, assigns a default role (e.g., `Role.USER`), and saves the user to the database using `UserRepository`.
   - `AuthenticationService` generates a JWT token for the registered user using `JwtService`, including any additional claims if required.
   - `AuthenticationService` returns an `AuthenticationResponse` containing the generated JWT token.

#### 2. User Authentication:
   - Client sends an authentication request containing user's credentials (email, password) to `/api/v1/auth/authenticate` endpoint.
   - `AuthenticationController` receives the request and delegates authentication process to `AuthenticationService`.
   - `AuthenticationService` uses Spring Security `AuthenticationManager` to authenticate the user by matching provided credentials with stored user credentials.
   - If authentication is successful, `AuthenticationService` retrieves the user from database using `UserRepository`.
   - `AuthenticationService` generates a new JWT token for the authenticated user using `JwtService`, including any necessary claims.
   - `AuthenticationService` returns an `AuthenticationResponse` containing the generated JWT token.

#### 3. Secured Endpoint Access:
   - Client includes JWT token in request headers, typically as "Authorization" header with value "Bearer [token]".
   - `JwtAuthenticationFilter` intercepts the request and extracts the JWT token from headers.
   - `JwtAuthenticationFilter` uses `JwtService` to validate the token, including checking its expiration, integrity, and matching it with user details.
   - If token is valid, `JwtAuthenticationFilter` sets the authenticated user in security context using `SecurityContextHolder`.
   - Request continues to secured endpoint, such as `DemoController`, where authorized user can access protected resources.
   - If token is invalid or expired, `JwtAuthenticationFilter` denies access and returns appropriate error response.


## Routes :

#### 1. Register Endpoint
- Method: POST
- URL: http://localhost:8083/api/v1/auth/register
- Body:
```
{
  "firstName": "Ismail",
  "lastName": "AlAmir",
  "email": "isalamir@hotmail.com",
  "password": "password"
}
```
#### 2. Authenticate Endpoint

- Method: POST
- URL: http://localhost:8083/api/v1/auth/authenticate
- Body:
```
{
  "email": "isalamir@hotmail.com",
  "password": "password"
}
```

#### 3. Secured Endpoint

- Method: GET
- URL: http://localhost:8083/api/v1/demo-controller
- Headers:
   - Key: Authorization
   - Value: Bearer {access_token}

#### 4. Refresh Token Endpoint

- Method: POST
- URL: http://localhost:8083/api/v1/auth/refresh
- Headers:
   - Key: Authorization
   - Value: Bearer {refresh_token}
